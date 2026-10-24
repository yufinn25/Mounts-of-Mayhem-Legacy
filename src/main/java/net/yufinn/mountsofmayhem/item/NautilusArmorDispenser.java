package net.yufinn.mountsofmayhem.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.yufinn.mountsofmayhem.entity.NautilusEntity;
import net.yufinn.mountsofmayhem.entity.ZombieNautilusEntity;
import java.util.List;

public final class NautilusArmorDispenser extends OptionalDispenseItemBehavior {
    private NautilusArmorDispenser() {}

    public static void register(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModItems.NAUTILUS_ARMORS.forEach(armor -> DispenserBlock.registerBehavior(armor.get(), new NautilusArmorDispenser()));
            OptionalDispenseItemBehavior horseArmor = new OptionalDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource source, ItemStack stack) {
                    BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                    for (net.minecraft.world.entity.animal.horse.AbstractHorse horse : source.getLevel().getEntitiesOfClass(net.minecraft.world.entity.animal.horse.AbstractHorse.class, new AABB(pos), h -> h.isAlive() && h.canWearArmor())) {
                        if (horse.isArmor(stack) && !horse.isWearingArmor() && horse.isTamed()) {
                            horse.getSlot(401).set(stack.split(1));
                            setSuccess(true);
                            return stack;
                        }
                    }
                    return super.execute(source, stack);
                }
            };
            DispenserBlock.registerBehavior(ModItems.COPPER_HORSE_ARMOR.get(), horseArmor);
            DispenserBlock.registerBehavior(ModItems.NETHERITE_HORSE_ARMOR.get(), horseArmor);
        });
    }

    @Override
    protected ItemStack execute(BlockSource source, ItemStack stack) {
        BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
        List<NautilusEntity> targets = source.getLevel().getEntitiesOfClass(NautilusEntity.class, new AABB(pos), nautilus -> nautilus.isAlive() && !nautilus.isBaby() && (nautilus.isTame() || nautilus instanceof ZombieNautilusEntity) && nautilus.getItemBySlot(EquipmentSlot.CHEST).isEmpty());
        if (targets.isEmpty()) return super.execute(source, stack);
        NautilusEntity nautilus = targets.get(0);
        ItemStack worn = stack.copy();
        worn.setCount(1);
        nautilus.setItemSlot(EquipmentSlot.CHEST, worn);
        nautilus.setDropChance(EquipmentSlot.CHEST, 2.0F);
        nautilus.setPersistenceRequired();
        stack.shrink(1);
        setSuccess(true);
        return stack;
    }
}
