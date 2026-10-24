package net.yufinn.mountsofmayhem.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class NautilusChargeGoal extends Goal {
    private static final int TIME_BETWEEN_ATTACKS = 80;
    private static final float KNOCKBACK_FORCE = 2.0F;
    private static final double MAX_CHARGE_DISTANCE = 12.0D;
    private static final double MAX_TARGET_DETECTION_DISTANCE = 11.0D;

    private static final int BEHAVIOR_DURATION = 60;

    private final NautilusEntity nautilus;
    private final float speed;
    private final TargetingConditions chargeTargeting;
    private Vec3 chargeVelocity = Vec3.ZERO;
    private Vec3 startPosition = Vec3.ZERO;
    private int runningTicks;

    public NautilusChargeGoal(NautilusEntity nautilus) {
        this.nautilus = nautilus;

        this.speed = nautilus.isZombie() ? 0.5F : 0.6F;

        this.chargeTargeting = TargetingConditions.forCombat().selector(target ->
                (nautilus.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || target.getType() != EntityType.ARMOR_STAND)
                        && nautilus.level().getWorldBorder().isWithinBounds(target.getBoundingBox()));
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return nautilus.getTarget() != null && nautilus.getChargeCooldown() <= 0 && !nautilus.isTame();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = nautilus.getTarget();
        if (target == null || !target.isAlive()) return false;
        if (nautilus.isTame()) return false;
        if (this.runningTicks > BEHAVIOR_DURATION) return false;
        if (nautilus.position().subtract(startPosition).lengthSqr() >= MAX_CHARGE_DISTANCE * MAX_CHARGE_DISTANCE) return false;
        if (target.position().subtract(nautilus.position()).lengthSqr() >= MAX_TARGET_DETECTION_DISTANCE * MAX_TARGET_DETECTION_DISTANCE) return false;
        if (!nautilus.hasLineOfSight(target)) return false;
        return nautilus.getChargeCooldown() <= 0;
    }

    @Override
    public void start() {
        runningTicks = 0;
        startPosition = nautilus.position();
        LivingEntity target = nautilus.getTarget();
        chargeVelocity = target.position().subtract(nautilus.position()).normalize().scale(this.speed);
        if (canContinueToUse()) nautilus.playChargeSound();
    }

    @Override
    public void tick() {
        runningTicks++;
        LivingEntity target = nautilus.getTarget();
        if (target == null) return;
        nautilus.lookAt(target, 360.0F, 360.0F);
        nautilus.setDeltaMovement(chargeVelocity);
        List<LivingEntity> hits = nautilus.level().getEntitiesOfClass(LivingEntity.class, nautilus.getBoundingBox(), entity -> chargeTargeting.test(nautilus, entity));
        if (hits.isEmpty()) return;
        LivingEntity hit = hits.get(0);
        if (nautilus.hasPassenger(hit)) return;
        dealDamage(hit);
        dealKnockback(hit);
        nautilus.setChargeCooldown(TIME_BETWEEN_ATTACKS);
        nautilus.setTarget(null);
    }

    private void dealDamage(LivingEntity target) {
        DamageSource source = nautilus.damageSources().mobAttack(nautilus);
        float damage = (float) nautilus.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (target.hurt(source, damage)) {

            EnchantmentHelper.doPostHurtEffects(target, nautilus);
            EnchantmentHelper.doPostDamageEffects(nautilus, target);
        }
    }

    private void dealKnockback(LivingEntity target) {
        int speedLevel = nautilus.hasEffect(MobEffects.MOVEMENT_SPEED) ? nautilus.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1 : 0;
        int slowLevel = nautilus.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? nautilus.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
        float speedBoostPower = 0.25F * (float) (speedLevel - slowLevel);
        float speedFactor = Mth.clamp(this.speed * (float) nautilus.getAttributeValue(Attributes.MOVEMENT_SPEED), 0.2F, 2.0F) + speedBoostPower;

        float strength = speedFactor * KNOCKBACK_FORCE;
        if (strength > 0.0F) {
            float yaw = nautilus.getYRot() * Mth.DEG_TO_RAD;
            target.knockback(strength, Mth.sin(yaw), -Mth.cos(yaw));
            nautilus.setDeltaMovement(nautilus.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
        }
    }

    @Override
    public void stop() {
        nautilus.setChargeCooldown(TIME_BETWEEN_ATTACKS);
        nautilus.setTarget(null);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
