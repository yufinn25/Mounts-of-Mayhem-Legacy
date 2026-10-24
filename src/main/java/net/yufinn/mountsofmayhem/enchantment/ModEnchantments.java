package net.yufinn.mountsofmayhem.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MountsOfMayhem.MOD_ID);
    public static final RegistryObject<Enchantment> LUNGE = ENCHANTMENTS.register("lunge", LungeEnchantment::new);
}
