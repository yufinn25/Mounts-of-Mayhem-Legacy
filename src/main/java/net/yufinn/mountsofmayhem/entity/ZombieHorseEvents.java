package net.yufinn.mountsofmayhem.entity;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.yufinn.mountsofmayhem.item.ModItems;

@Mod.EventBusSubscriber(modid = MountsOfMayhem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ZombieHorseEvents {
    private ZombieHorseEvents() {}

    public static void spawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(EntityType.ZOMBIE_HORSE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,

                (type, level, spawnType, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL
                        && (spawnType == MobSpawnType.SPAWNER || Monster.isDarkEnoughToSpawn(level, pos, random))
                        && Mob.checkMobSpawnRules(type, level, spawnType, pos, random),
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    public static void onFinalize(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof ZombieHorse horse)) return;
        var random = horse.getRandom();
        horse.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(0.5D + random.nextDouble() * 0.0666666D + random.nextDouble() * 0.0666666D + random.nextDouble() * 0.0666666D);
        horse.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((9.0D + random.nextDouble() + random.nextDouble() + random.nextDouble()) / 42.16D);
        if (event.getSpawnType() != MobSpawnType.NATURAL || event.getDifficulty().getDifficulty() == Difficulty.PEACEFUL) return;
        Zombie zombie = EntityType.ZOMBIE.create(horse.level());
        if (zombie == null) return;
        zombie.moveTo(horse.getX(), horse.getY(), horse.getZ(), horse.getYRot(), 0.0F);

        zombie.finalizeSpawn(event.getLevel(), event.getDifficulty(), MobSpawnType.NATURAL, null, null);
        zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.IRON_SPEAR.get()));
        zombie.startRiding(horse, true);
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof ZombieHorse horse)) return;
        var health = horse.getAttribute(Attributes.MAX_HEALTH);
        if (health != null && health.getBaseValue() != 25.0D) {
            health.setBaseValue(25.0D);
            if (!event.loadedFromDisk()) horse.setHealth(25.0F);
        }
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof ZombieHorse horse) || horse.level().isClientSide) return;
        if (horse.getFirstPassenger() instanceof Zombie rider) {
            horse.goalSelector.disableControlFlag(Goal.Flag.MOVE);
            horse.goalSelector.disableControlFlag(Goal.Flag.JUMP);
            horse.goalSelector.disableControlFlag(Goal.Flag.LOOK);
            var target = rider.getTarget();
            boolean spear = rider.goalSelector.getRunningGoals().anyMatch(g -> g.getGoal() instanceof SpearChargeGoal);
            if (target != null && !spear && horse.tickCount % 10 == 0) horse.getNavigation().moveTo(target, 1.0D);
            if (target != null) horse.getLookControl().setLookAt(target, 30.0F, 30.0F);
        } else {
            horse.goalSelector.enableControlFlag(Goal.Flag.MOVE);
            horse.goalSelector.enableControlFlag(Goal.Flag.JUMP);
            horse.goalSelector.enableControlFlag(Goal.Flag.LOOK);
        }
        burnInSunlight(horse);
    }

    private static void burnInSunlight(ZombieHorse horse) {
        if (!horse.isAlive() || horse.isOnFire() || !horse.level().isDay() || !horse.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) return;
        float brightness = horse.getLightLevelDependentMagicValue();
        BlockPos pos = BlockPos.containing(horse.getX(), horse.getEyeY(), horse.getZ());
        boolean wet = horse.isInWaterRainOrBubble() || horse.isInPowderSnow || horse.wasInPowderSnow;
        if (brightness > 0.5F && horse.getRandom().nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && !wet && horse.level().canSeeSky(pos)) horse.setSecondsOnFire(8);
    }

    @SubscribeEvent
    public static void onDespawn(MobSpawnEvent.AllowDespawn event) {
        if (!(event.getEntity() instanceof ZombieHorse horse) || horse.isTamed() || horse.isPersistenceRequired() || horse.hasCustomName()) return;
        Player player = horse.level().getNearestPlayer(horse, -1.0D);
        if (player == null) return;
        double distance = player.distanceToSqr(horse);
        if (distance > 128.0D * 128.0D || (horse.getNoActionTime() > 600 && horse.getRandom().nextInt(800) == 0 && distance > 32.0D * 32.0D)) event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof ZombieHorse horse) || event.getHand() != InteractionHand.MAIN_HAND || horse.isVehicle()) return;
        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());
        if (!horse.level().isClientSide) horse.setPersistenceRequired();
        if (held.is(Items.RED_MUSHROOM) && !horse.isBaby()) {
            if (!horse.level().isClientSide) {
                boolean used = false;
                if (horse.getHealth() < horse.getMaxHealth()) { horse.heal(3.0F); used = true; }
                if (!horse.isTamed() && horse.getTemper() < horse.getMaxTemper()) { horse.modifyTemper(3); used = true; }
                if (used) {
                    horse.playSound(SoundEvents.HORSE_EAT, 1.0F, 1.0F + (horse.getRandom().nextFloat() - horse.getRandom().nextFloat()) * 0.2F);
                    if (!player.getAbilities().instabuild) held.shrink(1);
                }
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(horse.level().isClientSide));
            event.setCanceled(true);
        } else if (!horse.isTamed() && !horse.isBaby() && held.isEmpty() && !player.isSecondaryUseActive()) {
            if (!horse.level().isClientSide) {
                player.setYRot(horse.getYRot());
                player.setXRot(horse.getXRot());
                player.startRiding(horse);
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(horse.level().isClientSide));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ZombieHorse horse)) return;
        event.getDrops().removeIf(drop -> drop.getItem().is(Items.ROTTEN_FLESH));
        if (horse.isBaby()) return;
        int count = 2 + horse.getRandom().nextInt(2 + event.getLootingLevel());
        event.getDrops().add(new ItemEntity(horse.level(), horse.getX(), horse.getY(), horse.getZ(), new ItemStack(Items.ROTTEN_FLESH, count)));
    }
}
