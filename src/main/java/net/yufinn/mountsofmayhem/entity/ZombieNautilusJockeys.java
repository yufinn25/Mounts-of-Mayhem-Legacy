package net.yufinn.mountsofmayhem.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

@Mod.EventBusSubscriber(modid = MountsOfMayhem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ZombieNautilusJockeys {
    private ZombieNautilusJockeys() {}

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.loadedFromDisk() || !(event.getLevel() instanceof ServerLevel level) || !(event.getEntity() instanceof Drowned drowned)) return;
        if (drowned.isBaby() || drowned.isPassenger() || !drowned.getMainHandItem().is(Items.TRIDENT)) return;
        var biome = level.getBiome(drowned.blockPosition());
        if (biome.is(Biomes.RIVER) || biome.is(Biomes.FROZEN_RIVER) || level.random.nextFloat() >= 0.5F) return;
        ZombieNautilusEntity mount = ModEntities.ZOMBIE_NAUTILUS.get().create(level);
        if (mount == null) return;
        mount.moveTo(drowned.getX(), drowned.getY(), drowned.getZ(), drowned.getYRot(), 0.0F);
        if (!level.noCollision(mount)) return;
        mount.finalizeSpawn(level, level.getCurrentDifficultyAt(drowned.blockPosition()), MobSpawnType.JOCKEY, null, null);
        if (drowned.isPersistenceRequired()) mount.setPersistenceRequired();
        level.addFreshEntity(mount);
        drowned.startRiding(mount);
    }
}
