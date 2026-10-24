package net.yufinn.mountsofmayhem.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

public record SetSpawnsModifier(HolderSet<Biome> biomes, MobCategory category, List<MobSpawnSettings.SpawnerData> spawners) implements BiomeModifier {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MountsOfMayhem.MOD_ID);

    public static final RegistryObject<Codec<SetSpawnsModifier>> CODEC = SERIALIZERS.register("set_spawns", () -> RecordCodecBuilder.create(inst -> inst.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(SetSpawnsModifier::biomes),
            MobCategory.CODEC.fieldOf("category").forGetter(SetSpawnsModifier::category),
            MobSpawnSettings.SpawnerData.CODEC.listOf().fieldOf("spawners").forGetter(SetSpawnsModifier::spawners)
    ).apply(inst, SetSpawnsModifier::new)));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD || !this.biomes.contains(biome)) return;
        for (MobSpawnSettings.SpawnerData spawner : this.spawners) {
            builder.getMobSpawnSettings().getSpawner(this.category).removeIf(existing -> existing.type == spawner.type);
            builder.getMobSpawnSettings().addSpawn(this.category, spawner);
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC.get();
    }
}
