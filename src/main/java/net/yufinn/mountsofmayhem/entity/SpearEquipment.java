package net.yufinn.mountsofmayhem.entity;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.yufinn.mountsofmayhem.item.ModItems;

@Mod.EventBusSubscriber(modid = MountsOfMayhem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpearEquipment {
    private SpearEquipment() {}

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof Zombie zombie && !(zombie instanceof Drowned) && zombie.goalSelector.getAvailableGoals().stream().noneMatch(wrapped -> wrapped.getGoal() instanceof SpearChargeGoal)) zombie.goalSelector.addGoal(1, new SpearChargeGoal(zombie, 1.0D, 1.0D, 10.0F, 2.0F));
        if (event.loadedFromDisk()) return;
        if (event.getEntity() instanceof ZombifiedPiglin piglin) {
            if (piglin.getMainHandItem().is(Items.GOLDEN_SWORD) && piglin.getRandom().nextFloat() < 0.05F) piglin.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.GOLDEN_SPEAR.get()));
        } else if (event.getEntity() instanceof Piglin piglin) {
            if (!piglin.isBaby() && piglin.getMainHandItem().is(Items.GOLDEN_SWORD) && piglin.getRandom().nextFloat() < 0.1F) piglin.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.GOLDEN_SPEAR.get()));
        } else if (event.getEntity() instanceof Zombie zombie && !(zombie instanceof Drowned) && (zombie.getClass() == Zombie.class || zombie instanceof Husk || zombie instanceof ZombieVillager)) {

            if (zombie.getMainHandItem().is(Items.IRON_SWORD) && zombie.getRandom().nextBoolean()) {
                zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.IRON_SPEAR.get()));
            }
        }
    }
}
