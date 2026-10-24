package net.yufinn.mountsofmayhem.combat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.yufinn.mountsofmayhem.enchantment.ModEnchantments;
import net.yufinn.mountsofmayhem.entity.ModSounds;
import net.yufinn.mountsofmayhem.item.SpearItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SpearAttacks {
    public static final double JAB_MIN_REACH = 2.0D;
    public static final double JAB_MAX_REACH = 4.5D;
    public static final double JAB_CREATIVE_MAX_REACH = 6.5D;
    public static final double CHARGE_MIN_REACH = 2.0D;
    public static final double HITBOX_MARGIN = 0.125D;
    public static final double CHARGE_MAX_REACH = 4.5D;
    public static final double DAMAGE_MIN_SPEED = 4.6D;
    public static final double KNOCKBACK_MIN_SPEED = 5.1D;
    private static final Map<UUID, Map<Integer, Integer>> LAST_HIT = new HashMap<>();
    private record Sample(Vec3 pos, Vec3 vel, long tick) {}
    private static final Map<Integer, Sample> SAMPLES = new HashMap<>();

    public static void jab(ServerPlayer player, ItemStack stack) {
        if (player.getAttackStrengthScale(0.5F) < 1.0F) return;
        Level level = player.level();
        int hits = 0;
        double maxReach = player.getAbilities().instabuild ? JAB_CREATIVE_MAX_REACH : JAB_MAX_REACH;
        for (LivingEntity target : raycast(player, JAB_MIN_REACH, maxReach)) if (jabHit(player, stack, target)) hits++;
        if (hits > 0) level.playSound(null, player.getX(), player.getY(), player.getZ(), hitSound(stack), player.getSoundSource(), 1.0F, 1.0F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), wood(stack) ? ModSounds.ITEM_SPEAR_WOOD_ATTACK.get() : ModSounds.ITEM_SPEAR_ATTACK.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        int lunge = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LUNGE.get(), stack);
        if (lunge > 0) lunge(player, stack, lunge);
        player.resetAttackStrengthTicker();
    }

    public static void charge(LivingEntity user, ItemStack stack, SpearItem spear, int elapsed) {
        SpearItem.ChargeStats stats = spear.getChargeStats();
        Vec3 look = user.getViewVector(1.0F);
        double attackerSpeed = look.dot(motion(user));
        int active = elapsed - stats.delay();
        if (active < 0 || active > stats.engaged() + stats.tired() + stats.disengaged()) return;
        boolean mob = !(user instanceof Player);
        double scale = mob ? 0.2D : 1.0D;
        double reach = mob ? 0.5D : 1.0D;
        Map<Integer, Integer> last = LAST_HIT.computeIfAbsent(user.getUUID(), id -> new HashMap<>());
        boolean affected = false;
        for (LivingEntity target : raycast(user, CHARGE_MIN_REACH * reach, CHARGE_MAX_REACH * reach)) {
            Integer previous = last.get(target.getId());
            if (previous != null && user.tickCount - previous < 10) continue;
            last.put(target.getId(), user.tickCount);
            double relative = Math.max(0.0D, attackerSpeed - look.dot(motion(target)));
            boolean dismount = active <= stats.engaged() && attackerSpeed >= stats.dismountSpeed() * scale;
            boolean knockback = active <= stats.engaged() + stats.tired() && attackerSpeed >= KNOCKBACK_MIN_SPEED * scale;
            boolean damage = relative >= DAMAGE_MIN_SPEED * scale;
            if (!dismount && !knockback && !damage) continue;
            boolean hit = false;
            boolean dealt = damage && target.hurt(ModDamageTypes.spear(user), (float) user.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) + (float) Mth.floor(relative * stats.multiplier()) + EnchantmentHelper.getDamageBonus(stack, target.getMobType()));
            hit = dealt;
            if (knockback) {
                extraKnockback(user, target, 0.4F);
                extraKnockback(user, target, EnchantmentHelper.getKnockbackBonus(user) * 0.5F);
                hit = true;
            }
            if (dismount && target.isPassenger()) {
                target.stopRiding();
                hit = true;
            }

            stack.hurtAndBreak(1, user, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            if (dealt) {
                EnchantmentHelper.doPostHurtEffects(target, user);
                EnchantmentHelper.doPostDamageEffects(user, target);
            }
            if (hit) {
                user.setLastHurtMob(target);
                affected = true;
            }
            if (stack.isEmpty()) break;
        }
        if (affected) {

            net.yufinn.mountsofmayhem.network.ModNetwork.sendKineticHit(user);
            if (user instanceof ServerPlayer serverPlayer) SpearMobsTrigger.INSTANCE.trigger(serverPlayer, last.size());
        }
    }

    private static Vec3 motion(Entity entity) {
        Entity source = !(entity instanceof Player) && entity.isPassenger() ? entity.getRootVehicle() : entity;
        return velocity(source).scale(20.0D);
    }

    public static void stopCharging(LivingEntity user) {
        LAST_HIT.remove(user.getUUID());
        SAMPLES.remove(user.getId());
    }

    public static boolean hasHit(LivingEntity user) {
        Map<Integer, Integer> hits = LAST_HIT.get(user.getUUID());
        return hits != null && !hits.isEmpty();
    }

    private static Vec3 velocity(Entity entity) {
        long now = entity.level().getGameTime();
        Sample old = SAMPLES.get(entity.getId());
        if (old != null && old.tick() == now) return old.vel();
        Vec3 pos = entity.position();
        Vec3 vel = old != null && now - old.tick() == 1L ? pos.subtract(old.pos()) : entity instanceof Player ? Vec3.ZERO : new Vec3(entity.getX() - entity.xo, entity.getY() - entity.yo, entity.getZ() - entity.zo);
        if (SAMPLES.size() > 512) SAMPLES.values().removeIf(sample -> now - sample.tick() > 40L);
        SAMPLES.put(entity.getId(), new Sample(pos, vel, now));
        return vel;
    }

    private static List<LivingEntity> raycast(LivingEntity user, double minReach, double maxReach) {
        Level level = user.level();
        Vec3 eye = user.getEyePosition();
        Vec3 look = user.getViewVector(1.0F);
        double reach = maxReach;
        HitResult block = level.clip(new ClipContext(eye, eye.add(look.scale(reach)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user));
        if (block.getType() != HitResult.Type.MISS) reach = Math.min(reach, block.getLocation().distanceTo(eye));
        Vec3 end = eye.add(look.scale(reach));
        AABB area = user.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0D);
        List<LivingEntity> result = new ArrayList<>();
        for (Entity entity : level.getEntities(user, area, e -> e instanceof LivingEntity && e.isAlive() && !e.isSpectator() && e.isPickable() && e != user.getVehicle() && !e.isPassengerOfSameVehicle(user) && !(e instanceof Player targetPlayer && user instanceof Player attacker && !attacker.canHarmPlayer(targetPlayer)))) {
            AABB box = entity.getBoundingBox().inflate(HITBOX_MARGIN);
            Optional<Vec3> hit = box.clip(eye, end);
            double distance = hit.map(eye::distanceTo).orElse(box.contains(eye) ? 0.0D : -1.0D);
            if (distance >= minReach && distance <= reach) result.add((LivingEntity) entity);
        }
        return result;
    }

    private static boolean jabHit(ServerPlayer player, ItemStack stack, LivingEntity target) {
        float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        damage += EnchantmentHelper.getDamageBonus(stack, target.getMobType());
        boolean dealtDamage = target.hurt(ModDamageTypes.spear(player), damage);
        extraKnockback(player, target, 0.4F);
        extraKnockback(player, target, EnchantmentHelper.getKnockbackBonus(player) * 0.5F);
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        if (dealtDamage) {
            int fire = EnchantmentHelper.getFireAspect(player);
            if (fire > 0) target.setSecondsOnFire(fire * 4);
            EnchantmentHelper.doPostHurtEffects(target, player);
            EnchantmentHelper.doPostDamageEffects(player, target);
        }
        player.setLastHurtMob(target);
        return true;
    }

    private static void extraKnockback(LivingEntity attacker, LivingEntity target, float strength) {
        if (strength <= 0.0F) return;
        float yaw = attacker.getYRot() * Mth.DEG_TO_RAD;
        target.knockback(strength, Mth.sin(yaw), -Mth.cos(yaw));
        attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
    }

    public static boolean wood(ItemStack stack) {
        return stack.is(net.yufinn.mountsofmayhem.item.ModItems.WOODEN_SPEAR.get());
    }

    private static net.minecraft.sounds.SoundEvent hitSound(ItemStack stack) {
        return wood(stack) ? ModSounds.ITEM_SPEAR_WOOD_HIT.get() : ModSounds.ITEM_SPEAR_HIT.get();
    }

    public static void lunge(ServerPlayer player, ItemStack stack, int level) {
        boolean creative = player.getAbilities().instabuild;

        if (player.isPassenger() || player.isFallFlying() || player.isInWater() || (!creative && player.getFoodData().getFoodLevel() < 7)) return;
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        if (!creative) player.causeFoodExhaustion(4.0F * level);
        Vec3 look = player.getLookAngle();
        double magnitude = 0.458D * level;
        player.setDeltaMovement(player.getDeltaMovement().add(look.x * magnitude, 0.0D, look.z * magnitude));
        player.hurtMarked = true;
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ITEM_SPEAR_LUNGE.get(), player.getSoundSource(), 1.0F, 1.0F);
    }
}
