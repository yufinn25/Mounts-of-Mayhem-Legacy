package net.yufinn.mountsofmayhem.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.yufinn.mountsofmayhem.item.ModItems;
import org.jetbrains.annotations.NotNull;
import java.util.function.Supplier;

public class NautilusArmorLootModifier extends LootModifier {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MountsOfMayhem.MOD_ID);
    public static final Supplier<Codec<NautilusArmorLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, NautilusArmorLootModifier::new)));
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> NAUTILUS_ARMOR = SERIALIZERS.register("nautilus_armor", CODEC);

    public NautilusArmorLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
        int roll = context.getRandom().nextInt(185);
        if (roll >= 37) return loot;
        loot.add(new ItemStack(roll < 20 ? ModItems.COPPER_NAUTILUS_ARMOR.get() : roll < 30 ? ModItems.IRON_NAUTILUS_ARMOR.get() : roll < 35 ? ModItems.GOLDEN_NAUTILUS_ARMOR.get() : ModItems.DIAMOND_NAUTILUS_ARMOR.get()));
        return loot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
