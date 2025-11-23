package org.aki.helvetti.worldgen;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.StructureManager;
import net.minecraft.resources.ResourceLocation;

import org.aki.helvetti.block.CBlocks;
import org.aki.helvetti.feature.CBiomeInversionManager;
import org.aki.helvetti.worldgen.structure.CLandmarkManager;
import org.aki.helvetti.worldgen.structure.landmarks.CLandmarkTypes;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

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

    public static final int MIRROR_LEVEL = 200;

    private static final double TREND_WEIGHT = 0.7;
    private static final double NATURAL_WEIGHT = 0.5;

    /**
     * Flipping list - maps original block states to their flipped versions.
     * Add custom block state mappings here for terrain inversion.
     */
    private static final Map<BlockState, BlockState> FLIPPING_LIST = new HashMap<>();
    
    static {
        FLIPPING_LIST.put(Blocks.GRASS_BLOCK.defaultBlockState(), CBlocks.FLIPPED_GRASS_BLOCK.get().defaultBlockState());
    }

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

    /**
     * Override buildSurface to implement terrain inversion feature.
     * 
     * This method:
     * 1. Records original ground level (c)
     * 2. Checks noise conditions (erosion >= 0.0, continentalness <= -0.1)
     * 3. If conditions met, flips blocks from y >= -c to position MIRROR_LEVEL - y
     */
    @Override
    public void buildSurface(@Nonnull WorldGenRegion region, @Nonnull StructureManager structures, 
                            @Nonnull RandomState randomState, @Nonnull ChunkAccess chunk) {
        // First, let the parent class generate the normal terrain
        super.buildSurface(region, structures, randomState, chunk);
        
        // Get noise router for accessing noise functions
        var noiseRouter = randomState.router();
        
        // Get chunk position
        int chunkX = chunk.getPos().getMinBlockX();
        int chunkZ = chunk.getPos().getMinBlockZ();
        
        // Process each column in the chunk
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int worldX = chunkX + localX;
                int worldZ = chunkZ + localZ;

                if (CBiomeInversionManager.isBiomeInverted(region.getBiome(new BlockPos(worldX, 0, worldZ)))) {
                    double continentalness = noiseRouter.continents().compute(new DensityFunction
                        .SinglePointContext(worldX, 0, worldZ));
                    double erosion = noiseRouter.erosion().compute(new DensityFunction
                        .SinglePointContext(worldX << 2, 0, worldZ << 2));
                    double noiseCarving = getNoiseCarving(continentalness, erosion);

                    Pair<ResourceLocation, Double> nearestLandmark = CLandmarkManager
                        .getNearestInfluentialLandmark(worldX, worldZ, randomState.sampler());
                    double landmarkCarving = nearestLandmark == null ? 0.0 : CLandmarkTypes
                        .get(nearestLandmark.getFirst()).getLandmarkCarving(nearestLandmark.getSecond());
                    
                    double finalCarving = noiseCarving * NATURAL_WEIGHT + landmarkCarving * (1.0 - NATURAL_WEIGHT);
                    int groundLevel = findGroundLevel(chunk, localX, localZ);

                    if (groundLevel != Integer.MIN_VALUE) {
                        int adjustedBase = (int) (-groundLevel - finalCarving * 150.0);
                        // Clamp to valid range: minimum is minBuildHeight + 2, maximum is -2
                        int minBound = chunk.getMinBuildHeight() + 2;
                        int maxBound = -2;
                        adjustedBase = Math.max(minBound, Math.min(maxBound, adjustedBase));
                        flipTerrainColumn(chunk, localX, localZ, adjustedBase);
                    }
                }
            }
        }
    }
    
    /**
     * Calculates the carving noise value based on continentalness and erosion.
     * 
     * @param continentalness The continentalness value to check
     * @param erosion The erosion value to check
     * @return the carving noise value
     */
    private double getNoiseCarving(double continentalness, double erosion) {
        double lowerContinents = Math.floor(continentalness * 10.0) / 10.0;
        double upperContinents = Math.ceil(continentalness * 10.0) / 10.0;
        double trend = Math.min(continentalness - lowerContinents, upperContinents - continentalness) * 20.0;
        return trend * TREND_WEIGHT + Math.abs(erosion) * (1.0 - TREND_WEIGHT);
    }
    
    /**
     * Finds the original ground level in a column.
     * Returns the highest non-air block Y coordinate.
     * 
     * @param chunk The chunk to search
     * @param x Local x coordinate in chunk
     * @param z Local z coordinate in chunk
     * @return The Y coordinate of the highest non-air block, or Integer.MIN_VALUE if none found
     */
    private int findGroundLevel(ChunkAccess chunk, int x, int z) {
        MutableBlockPos mutablePos = new MutableBlockPos();
        for (int y = chunk.getMaxBuildHeight() - 1; y >= chunk.getMinBuildHeight(); y--) {
            mutablePos.set(x, y, z);
            BlockState state = chunk.getBlockState(mutablePos);
            if (!state.isAir()) {
                return y;
            }
        }
        return Integer.MIN_VALUE;
    }
    
    /**
     * Flips terrain in a column from y >= baseLevel to position MIRROR_LEVEL - y.
     * 
     * @param chunk The chunk to modify
     * @param x Local x coordinate in chunk
     * @param z Local z coordinate in chunk
     * @param baseLevel The level from which to start flipping
     */
    private void flipTerrainColumn(ChunkAccess chunk, int x, int z, int baseLevel) {
        int minBuildHeight = chunk.getMinBuildHeight();
        int maxBuildHeight = chunk.getMaxBuildHeight() - 1;
        int minY = Math.max(baseLevel, minBuildHeight);
        int maxY = maxBuildHeight;
        
        // Estimate capacity: assume average 20% of the height range contains blocks
        int estimatedBlocks = (maxY - minY + 1) / 5;
        Map<Integer, BlockState> blocksToFlip = new HashMap<>(estimatedBlocks);
        MutableBlockPos mutablePos = new MutableBlockPos();

        // Collect all blocks from y >= baseLevel
        for (int y = minY; y <= maxY; y++) {
            mutablePos.set(x, y, z);
            BlockState state = chunk.getBlockState(mutablePos);
            if (!state.isAir()) {
                int flippedY = MIRROR_LEVEL - y;
                // Only flip if target position is within world bounds
                if (flippedY >= minBuildHeight && flippedY <= maxBuildHeight) {
                    // Check if this block has a flipped version
                    BlockState flippedState = getFlippedBlockState(state);
                    blocksToFlip.put(flippedY, flippedState);
                }
            }
        }
        
        // Clear original blocks
        BlockState airState = Blocks.AIR.defaultBlockState();
        for (int y = minY; y <= maxY; y++) {
            mutablePos.set(x, y, z);
            chunk.setBlockState(mutablePos, airState, false);
        }
        
        // Place flipped blocks
        for (Map.Entry<Integer, BlockState> entry : blocksToFlip.entrySet()) {
            mutablePos.set(x, entry.getKey(), z);
            chunk.setBlockState(mutablePos, entry.getValue(), false);
        }
    }
    
    /**
     * Gets the flipped version of a block state based on the flipping list.
     * If the block state is not in the flipping list, returns the original state.
     * 
     * @param original The original block state
     * @return The flipped block state, or original if no mapping exists
     */
    private BlockState getFlippedBlockState(BlockState original) {
        BlockState flippedState = FLIPPING_LIST.get(original);
        
        if (flippedState != null) {
            return flippedState;
        }
        
        // No flipping mapping found, return original
        return original;
    }
}
