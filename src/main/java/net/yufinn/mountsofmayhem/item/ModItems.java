package net.yufinn.mountsofmayhem.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.yufinn.mountsofmayhem.entity.ModEntities;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MountsOfMayhem.MOD_ID);
    public static final List<RegistryObject<Item>> SPEARS = new ArrayList<>();
    public static final RegistryObject<Item> WOODEN_SPEAR = spear("wooden_spear", () -> Tiers.WOOD, 1.0F, 1.54F, new SpearItem.ChargeStats(15, 100, 100, 100, 14.0D, 0.7F));
    public static final RegistryObject<Item> STONE_SPEAR = spear("stone_spear", () -> Tiers.STONE, 2.0F, 1.33F, new SpearItem.ChargeStats(14, 90, 90, 95, 13.0D, 0.82F));
    public static final RegistryObject<Item> COPPER_SPEAR = spear("copper_spear", () -> ModTiers.COPPER, 2.0F, 1.18F, new SpearItem.ChargeStats(13, 80, 85, 85, 12.0D, 0.82F));
    public static final RegistryObject<Item> IRON_SPEAR = spear("iron_spear", () -> Tiers.IRON, 3.0F, 1.05F, new SpearItem.ChargeStats(12, 50, 85, 90, 11.0D, 0.95F));
    public static final RegistryObject<Item> GOLDEN_SPEAR = spear("golden_spear", () -> Tiers.GOLD, 1.0F, 1.05F, new SpearItem.ChargeStats(14, 70, 100, 105, 13.0D, 0.7F));
    public static final RegistryObject<Item> DIAMOND_SPEAR = spear("diamond_spear", () -> Tiers.DIAMOND, 4.0F, 0.95F, new SpearItem.ChargeStats(10, 60, 70, 70, 10.0D, 1.075F));
    public static final RegistryObject<Item> NETHERITE_SPEAR = spear("netherite_spear", () -> Tiers.NETHERITE, 5.0F, 0.87F, new SpearItem.ChargeStats(8, 50, 60, 65, 9.0D, 1.2F));

    public static final RegistryObject<Item> NAUTILUS_SPAWN_EGG = ITEMS.register("nautilus_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.NAUTILUS, 0xD9B78A, 0xA24B3A, new Item.Properties()));
    public static final RegistryObject<Item> ZOMBIE_NAUTILUS_SPAWN_EGG = ITEMS.register("zombie_nautilus_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.ZOMBIE_NAUTILUS, 0x5B7F6D, 0x8A5A4A, new Item.Properties()));

    public static final RegistryObject<Item> CAMEL_HUSK_SPAWN_EGG = ITEMS.register("camel_husk_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.CAMEL_HUSK, 0x7A6A4A, 0xC8B27A, new Item.Properties()));

    public static final RegistryObject<Item> PARCHED_SPAWN_EGG = ITEMS.register("parched_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.PARCHED, 0xB5A585, 0x6E6450, new Item.Properties()));

    public static final RegistryObject<Item> COPPER_HORSE_ARMOR = ITEMS.register("copper_horse_armor", () -> new net.minecraft.world.item.HorseArmorItem(4, "copper", new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> NETHERITE_HORSE_ARMOR = ITEMS.register("netherite_horse_armor", () -> new NetheriteHorseArmorItem(new Item.Properties().stacksTo(1).fireResistant()));

    public static final RegistryObject<Item> COPPER_NAUTILUS_ARMOR = ITEMS.register("copper_nautilus_armor", () -> new NautilusArmorItem("copper", 4, 0.0F, 0.0F, new Item.Properties()));
    public static final RegistryObject<Item> IRON_NAUTILUS_ARMOR = ITEMS.register("iron_nautilus_armor", () -> new NautilusArmorItem("iron", 5, 0.0F, 0.0F, new Item.Properties()));
    public static final RegistryObject<Item> GOLDEN_NAUTILUS_ARMOR = ITEMS.register("golden_nautilus_armor", () -> new NautilusArmorItem("gold", 7, 0.0F, 0.0F, new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_NAUTILUS_ARMOR = ITEMS.register("diamond_nautilus_armor", () -> new NautilusArmorItem("diamond", 11, 2.0F, 0.0F, new Item.Properties()));
    public static final RegistryObject<Item> NETHERITE_NAUTILUS_ARMOR = ITEMS.register("netherite_nautilus_armor", () -> new NautilusArmorItem("netherite", 19, 3.0F, 0.1F, new Item.Properties().fireResistant()));
    public static final List<RegistryObject<Item>> NAUTILUS_ARMORS = List.of(COPPER_NAUTILUS_ARMOR, IRON_NAUTILUS_ARMOR, GOLDEN_NAUTILUS_ARMOR, DIAMOND_NAUTILUS_ARMOR, NETHERITE_NAUTILUS_ARMOR);

    private static RegistryObject<Item> spear(String name, Supplier<Tier> tier, float damage, float speed, SpearItem.ChargeStats stats) {
        RegistryObject<Item> object = ITEMS.register(name, () -> new SpearItem(tier.get(), damage, speed, stats, name.startsWith("netherite") ? new Item.Properties().fireResistant() : new Item.Properties()));
        SPEARS.add(object);
        return object;
    }
}
