package net.yufinn.mountsofmayhem.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.yufinn.mountsofmayhem.entity.NautilusEntity;
import net.yufinn.mountsofmayhem.item.NautilusArmorItem;

public class NautilusInventoryMenu extends AbstractContainerMenu {
    private final NautilusEntity nautilus;

    public NautilusInventoryMenu(int id, Inventory inventory, NautilusEntity nautilus) {
        super(ModMenus.NAUTILUS.get(), id);
        this.nautilus = nautilus;
        SimpleContainer container = new SimpleContainer(2);
        container.setItem(0, nautilus.isSaddled() ? new ItemStack(Items.SADDLE) : ItemStack.EMPTY);
        container.setItem(1, nautilus.getItemBySlot(EquipmentSlot.CHEST).copy());
        container.addListener(changed -> apply(changed));
        addSlot(new Slot(container, 0, 8, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.SADDLE) && !hasItem();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addSlot(new Slot(container, 1, 8, 36) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof NautilusArmorItem && !hasItem();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 142));
    }

    private void apply(Container container) {
        if (nautilus.level().isClientSide) return;
        nautilus.setSaddled(!container.getItem(0).isEmpty());
        ItemStack armor = container.getItem(1).copy();
        nautilus.setItemSlot(EquipmentSlot.CHEST, armor);
        if (!armor.isEmpty()) nautilus.setDropChance(EquipmentSlot.CHEST, 2.0F);
        nautilus.setPersistenceRequired();
    }

    public NautilusEntity getNautilus() {
        return nautilus;
    }

    @Override
    public boolean stillValid(Player player) {
        return nautilus.isAlive() && nautilus.distanceTo(player) < 8.0F;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < 2) {
            if (!moveItemStackTo(stack, 2, slots.size(), true)) return ItemStack.EMPTY;
        } else if (getSlot(1).mayPlace(stack)) {
            if (!moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
        } else if (getSlot(0).mayPlace(stack)) {
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }
}
