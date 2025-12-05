package org.aki.helvetti.worldgen.biomesources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import org.aki.helvetti.worldgen.CLandmarkIndex;
import org.aki.helvetti.worldgen.CNoiseRange;
import org.aki.helvetti.worldgen.structure.landmarks.CLandmark;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;
import com.mojang.datafixers.util.Pair;

import java.util.stream.Stream;

import org.aki.helvetti.inversion.biome.CBiomeInversionManager;

/**
 * Custom BiomeSource for the Lelyetia dimension
 * Uses sequential rule-based matching to determine biomes
 * Each biome has parameter ranges, and the first matching biome is selected
 * 
 * This BiomeSource requires the world seed and landmark index to be set
 * via the setSeed and setLandmarkIndex methods before use.
 * If these are not set, it delegates to segmented biome selector without landmark related logic.
 */
public class CLelyetianBiomeSource extends BiomeSource {

    public static final MapCodec<CLelyetianBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.list(
                RecordCodecBuilder.<CBiomeEntry>create(entryInstance ->
                    entryInstance.group(
                        CNoiseRange.CODEC.fieldOf("parameters").forGetter(CBiomeEntry::parameters),
                        Biome.CODEC.fieldOf("biome").forGetter(CBiomeEntry::biome)
                    ).apply(entryInstance, CBiomeEntry::new)
                )
            ).fieldOf("biomes").forGetter(source -> source.biomeEntries)
        ).apply(instance, CLelyetianBiomeSource::new)
    );

    private final List<CBiomeEntry> biomeEntries;
    private final Holder<Biome> defaultBiome;
    
    private final CDeferredFinalHolder<Long> seed = new CDeferredFinalHolder<>();
    private final CDeferredFinalHolder<CLandmarkIndex> landmarkIndex = new CDeferredFinalHolder<>();

    private CLelyetianBiomeSource(List<CBiomeEntry> biomeEntries) {
        this.biomeEntries = new ArrayList<>(biomeEntries);
        this.defaultBiome = biomeEntries.isEmpty() ? null : biomeEntries.get(biomeEntries.size() - 1).biome();
    }

    public void setSeed(long seed) {
        this.seed.set(seed);
    }

    public void setLandmarkIndex(CLandmarkIndex landmarkIndex) {
        this.landmarkIndex.set(landmarkIndex);
    }

    /**
     * Represents a biome entry with its parameter range conditions
     */
    public record CBiomeEntry(CNoiseRange parameters, Holder<Biome> biome) {}

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
        Holder<Biome> biomeHolder = null;
        
        // Sequential matching: return the first biome whose parameters match
        for (CBiomeEntry entry : this.biomeEntries) {
            if (entry.parameters().matches(targetPoint)) {
                biomeHolder = entry.biome();
                break;
            }
        }

        if (biomeHolder == null) {
            biomeHolder = this.defaultBiome != null ? this.defaultBiome : this.biomeEntries.get(0).biome();
        } else {
            if (this.seed.hasValue() && this.landmarkIndex.hasValue()) {
                Pair<CLandmark, Double> landmarkInfo = this.landmarkIndex.get().getNearestInfluential(this.seed.get(), (x << 2) + 2, (z << 2) + 2);
                if (landmarkInfo != null) {
                    int inversionLock = landmarkInfo.getFirst().getInversionLock(landmarkInfo.getSecond());
                    if (inversionLock == -1) {
                        biomeHolder = CBiomeInversionManager.getInvertedBiome(biomeHolder).orElse(biomeHolder);
                    } else if (inversionLock == 1) {
                        biomeHolder = CBiomeInversionManager.getNormalBiome(biomeHolder).orElse(biomeHolder);
                    }
                }
            }
        }

        return biomeHolder;
    }

}
