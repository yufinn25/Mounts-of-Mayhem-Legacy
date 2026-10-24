package net.yufinn.mountsofmayhem.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MountsOfMayhem.MOD_ID);

    public static final RegistryObject<EntityType<NautilusEntity>> NAUTILUS = ENTITIES.register("nautilus",
            () -> EntityType.Builder.of(NautilusEntity::new, MobCategory.WATER_CREATURE).sized(0.875F, 0.95F).clientTrackingRange(10).build("nautilus"));

    public static final RegistryObject<EntityType<ZombieNautilusEntity>> ZOMBIE_NAUTILUS = ENTITIES.register("zombie_nautilus",
            () -> EntityType.Builder.of(ZombieNautilusEntity::new, MobCategory.MONSTER).sized(0.875F, 0.95F).clientTrackingRange(10).build("zombie_nautilus"));

    public static final RegistryObject<EntityType<CamelHuskEntity>> CAMEL_HUSK = ENTITIES.register("camel_husk",
            () -> EntityType.Builder.of(CamelHuskEntity::new, MobCategory.MONSTER).sized(1.7F, 2.375F).clientTrackingRange(10).build("camel_husk"));

    public static final RegistryObject<EntityType<ParchedEntity>> PARCHED = ENTITIES.register("parched",
            () -> EntityType.Builder.of(ParchedEntity::new, MobCategory.MONSTER).sized(0.6F, 1.99F).clientTrackingRange(8).build("parched"));

    public static void attributes(EntityAttributeCreationEvent event) {
        event.put(NAUTILUS.get(), NautilusEntity.createAttributes().build());
        event.put(ZOMBIE_NAUTILUS.get(), ZombieNautilusEntity.createAttributes().build());
        event.put(PARCHED.get(), ParchedEntity.createAttributes().build());
        event.put(CAMEL_HUSK.get(), net.minecraft.world.entity.animal.camel.Camel.createAttributes().build());
    }

    public static void spawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(NAUTILUS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NautilusEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(PARCHED.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ParchedEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
