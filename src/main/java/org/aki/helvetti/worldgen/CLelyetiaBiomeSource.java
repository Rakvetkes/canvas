package org.aki.helvetti.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Custom BiomeSource for the Lelyetia dimension
 * Uses sequential rule-based matching to determine biomes
 * Each biome has parameter ranges, and the first matching biome is selected
 */
public class CLelyetiaBiomeSource extends BiomeSource {
    public static final MapCodec<CLelyetiaBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.list(
                RecordCodecBuilder.<CBiomeEntry>create(entryInstance ->
                    entryInstance.group(
                        CBiomeParameterRange.CODEC.fieldOf("parameters").forGetter(CBiomeEntry::parameters),
                        Biome.CODEC.fieldOf("biome").forGetter(CBiomeEntry::biome)
                    ).apply(entryInstance, CBiomeEntry::new)
                )
            ).fieldOf("biomes").forGetter(source -> source.biomeEntries)
        ).apply(instance, CLelyetiaBiomeSource::new)
    );

    private final List<CBiomeEntry> biomeEntries;
    private final Holder<Biome> defaultBiome;

    private CLelyetiaBiomeSource(List<CBiomeEntry> biomeEntries) {
        this.biomeEntries = new ArrayList<>(biomeEntries);
        // Use the last biome as default fallback
        this.defaultBiome = biomeEntries.isEmpty() ? null : biomeEntries.get(biomeEntries.size() - 1).biome();
    }

    /**
     * Represents a biome entry with its parameter range conditions
     */
    public record CBiomeEntry(CBiomeParameterRange parameters, Holder<Biome> biome) {}

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.biomeEntries.stream().map(CBiomeEntry::biome);
    }

    @Override
    @Nonnull
    public Holder<Biome> getNoiseBiome(int x, int y, int z, @Nonnull Climate.Sampler sampler) {
        Climate.TargetPoint targetPoint = sampler.sample(x, y, z);
        
        // Sequential matching: return the first biome whose parameters match
        for (CBiomeEntry entry : this.biomeEntries) {
            if (entry.parameters().matches(targetPoint)) {
                return entry.biome();
            }
        }
        
        // Fallback to default biome if no match found
        return this.defaultBiome != null ? this.defaultBiome : this.biomeEntries.get(0).biome();
    }
}
