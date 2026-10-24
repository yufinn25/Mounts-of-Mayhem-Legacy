package net.yufinn.mountsofmayhem.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.yufinn.mountsofmayhem.item.ModItems;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = MountsOfMayhem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CamelHuskJockeys {
    private static final Map<SpawnGroupData, Boolean> TRIED = Collections.synchronizedMap(new WeakHashMap<>());

    private CamelHuskJockeys() {}

    @SubscribeEvent
    public static void onFinalize(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof Husk husk) || event.getSpawnType() != MobSpawnType.NATURAL) return;
        SpawnGroupData data = event.getSpawnData();
        if (data != null && TRIED.put(data, Boolean.TRUE) != null) return;
        ServerLevel level = event.getLevel().getLevel();
        CamelHuskEntity camel = ModEntities.CAMEL_HUSK.get().create(level);
        if (camel == null || !level.noCollision(ModEntities.CAMEL_HUSK.get().getAABB(husk.getX(), husk.getY(), husk.getZ())) || husk.getRandom().nextFloat() >= 0.1F) return;
        husk.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.IRON_SPEAR.get()));
        camel.moveTo(husk.getX(), husk.getY(), husk.getZ(), husk.getYRot(), 0.0F);
        camel.finalizeSpawn(level, event.getDifficulty(), MobSpawnType.JOCKEY, null, null);
        husk.startRiding(camel);
        level.addFreshEntity(camel);
        ParchedEntity parched = ModEntities.PARCHED.get().create(level);
        if (parched == null) return;
        parched.moveTo(husk.getX(), husk.getY(), husk.getZ(), husk.getYRot(), 0.0F);
        parched.finalizeSpawn(level, event.getDifficulty(), MobSpawnType.JOCKEY, null, null);
        parched.startRiding(camel);
        level.addFreshEntity(parched);
    }

    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Husk husk) || !(husk.getVehicle() instanceof CamelHuskEntity) || !(event.getSource().getEntity() instanceof Player)) return;
        int count = husk.getRandom().nextInt(2 + event.getLootingLevel());
        if (count > 0) event.getDrops().add(new ItemEntity(husk.level(), husk.getX(), husk.getY(), husk.getZ(), new ItemStack(Items.RABBIT_FOOT, count)));
    }
}
