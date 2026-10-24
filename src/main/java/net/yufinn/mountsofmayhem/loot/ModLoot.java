package net.yufinn.mountsofmayhem.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.yufinn.mountsofmayhem.item.ModItems;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = MountsOfMayhem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModLoot {
    private static final Map<String, List<Entry>> TABLES = Map.of(
            "chests/bastion_treasure", List.of(new Entry(ModItems.DIAMOND_SPEAR, 0.152F), new Entry(ModItems.DIAMOND_SPEAR, 0.152F, true, true)),
                        "chests/buried_treasure", List.of(new Entry(ModItems.IRON_SPEAR, 0.167F)),
            "chests/end_city_treasure", List.of(new Entry(ModItems.DIAMOND_SPEAR, 0.127F, true, false)),
            "chests/underwater_ruin_small", List.of(new Entry(ModItems.STONE_SPEAR, 0.285F)),
            "chests/underwater_ruin_big", List.of(new Entry(ModItems.STONE_SPEAR, 0.263F)),
            "chests/village/village_weaponsmith", List.of(new Entry(ModItems.COPPER_SPEAR, 0.306F), new Entry(ModItems.IRON_SPEAR, 0.229F)));

    private ModLoot() {}

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        if (!name.getNamespace().equals("minecraft")) return;
        List<Entry> entries = TABLES.get(name.getPath());
        if (entries == null) return;
        for (Entry entry : entries) event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(entry.chance())).add(withFunctions(LootItem.lootTableItem(entry.item().get()), entry)).build());
    }

    private static LootPoolEntryContainer.Builder<?> withFunctions(LootPoolSingletonContainer.Builder<?> builder, Entry entry) {
        if (entry.enchanted()) builder.apply(EnchantWithLevelsFunction.enchantWithLevels(UniformGenerator.between(20.0F, 39.0F)).allowTreasure());
        if (entry.damaged()) builder.apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0F, 0.2F)));
        return builder;
    }

    private record Entry(Supplier<? extends Item> item, float chance, boolean enchanted, boolean damaged) {
        Entry(Supplier<? extends Item> item, float chance) {
            this(item, chance, false, false);
        }
    }
}
