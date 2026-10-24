package net.yufinn.mountsofmayhem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.yufinn.mountsofmayhem.enchantment.ModEnchantments;
import net.yufinn.mountsofmayhem.entity.ModEffects;
import net.yufinn.mountsofmayhem.entity.ModEntities;
import net.yufinn.mountsofmayhem.entity.ModSounds;
import net.yufinn.mountsofmayhem.item.ModItems;
import net.yufinn.mountsofmayhem.item.SpearItem;

@Mod(MountsOfMayhem.MOD_ID)
public class MountsOfMayhem {
    public static final String MOD_ID = "mountsofmayhem";

    public MountsOfMayhem() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        net.yufinn.mountsofmayhem.network.ModNetwork.register();
        ModItems.ITEMS.register(bus);
        ModEnchantments.ENCHANTMENTS.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModSounds.SOUNDS.register(bus);
        ModEffects.EFFECTS.register(bus);
        net.yufinn.mountsofmayhem.menu.ModMenus.MENUS.register(bus);
        net.yufinn.mountsofmayhem.loot.NautilusArmorLootModifier.SERIALIZERS.register(bus);
        net.yufinn.mountsofmayhem.world.SetSpawnsModifier.SERIALIZERS.register(bus);
        bus.addListener(ModEntities::attributes);
        bus.addListener(ModEntities::spawnPlacements);
        bus.addListener(net.yufinn.mountsofmayhem.entity.ZombieHorseEvents::spawnPlacements);
        bus.addListener(MountsOfMayhem::onCreativeTab);
        bus.addListener(net.yufinn.mountsofmayhem.item.NautilusArmorDispenser::register);
        bus.addListener(net.yufinn.mountsofmayhem.combat.SpearMobsTrigger::register);
        MinecraftForge.EVENT_BUS.addListener(MountsOfMayhem::onFuel);
        MinecraftForge.EVENT_BUS.addListener(MountsOfMayhem::onAttack);
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    private static void onCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) { ModItems.SPEARS.forEach(spear -> event.accept(spear.get())); event.accept(ModItems.COPPER_HORSE_ARMOR.get()); event.accept(ModItems.NETHERITE_HORSE_ARMOR.get()); ModItems.NAUTILUS_ARMORS.forEach(armor -> event.accept(armor.get())); }
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) { event.accept(ModItems.NAUTILUS_SPAWN_EGG.get()); event.accept(ModItems.ZOMBIE_NAUTILUS_SPAWN_EGG.get()); event.accept(ModItems.CAMEL_HUSK_SPAWN_EGG.get()); event.accept(ModItems.PARCHED_SPAWN_EGG.get()); }
    }

    private static void onAttack(AttackEntityEvent event) {
        if (event.getEntity().getMainHandItem().getItem() instanceof SpearItem) event.setCanceled(true);
    }

    private static void onFuel(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().is(ModItems.WOODEN_SPEAR.get())) event.setBurnTime(200);
    }
}
