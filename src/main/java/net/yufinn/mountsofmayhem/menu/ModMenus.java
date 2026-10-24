package net.yufinn.mountsofmayhem.menu;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.yufinn.mountsofmayhem.entity.NautilusEntity;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MountsOfMayhem.MOD_ID);
    public static final RegistryObject<MenuType<NautilusInventoryMenu>> NAUTILUS = MENUS.register("nautilus", () -> IForgeMenuType.create((id, inventory, data) -> {
        Entity entity = inventory.player.level().getEntity(data.readInt());
        return entity instanceof NautilusEntity nautilus ? new NautilusInventoryMenu(id, inventory, nautilus) : null;
    }));
}
