package org.aki.helvetti.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

/**
 * Custom chunk generator for the Lelyetia dimension that wraps NoiseBasedChunkGenerator.
 * This class behaves exactly like minecraft:noise generator and serves as a foundation 
 * for future customizations.
 */
public class CLelyetiaChunkGenerator extends NoiseBasedChunkGenerator {
    
    /**
     * Codec for serialization/deserialization of the chunk generator.
     * This codec is compatible with minecraft:noise format.
     */
    public static final MapCodec<CLelyetiaChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings)
        ).apply(instance, CLelyetiaChunkGenerator::new)
    );

    /**
     * Constructor for CLelyetiaChunkGenerator.
     * 
     * @param biomeSource The biome source to use for biome placement
     * @param settings The noise generator settings for terrain generation
     */
    public CLelyetiaChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    /**
     * Returns the codec for this chunk generator.
     * 
     * @return The MapCodec for serialization
     */
    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    // This generator currently behaves exactly like NoiseBasedChunkGenerator.
    // You can override methods here to customize terrain generation in the future.
    // Common methods to override include:
    // - buildSurface() for custom surface building
    // - applyCarvers() for custom cave/ravine generation
    // - fillFromNoise() for custom noise-based terrain
}
