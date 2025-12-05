package org.aki.helvetti.worldgen.chunkgenerators;

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
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.StructureManager;

import org.aki.helvetti.CCanvasMain;
import org.aki.helvetti.inversion.CBlockInversionManager;
import org.aki.helvetti.inversion.biome.CBiomeInversionManager;
import org.aki.helvetti.worldgen.CLandmarkIndex;
import org.aki.helvetti.worldgen.biomesources.CLelyetianBiomeSource;
import org.aki.helvetti.worldgen.structure.landmarks.CLandmark;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Custom chunk generator for the Lelyetia dimension that wraps NoiseBasedChunkGenerator.
 * This class behaves exactly like minecraft:noise generator and serves as a foundation 
 * for future customizations.
 * 
 * Requires a CLelyetianBiomeSource and CLandmarkIndex to function properly.
 * If the biome source is not of type CLelyetianBiomeSource, the landmark index is ignored
 * and the generator behaves like a standard NoiseBasedChunkGenerator with passive inversion logic.
 */
public class CLelyetianChunkGenerator extends NoiseBasedChunkGenerator {
    
    public static final MapCodec<CLelyetianChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings),
            CLandmarkIndex.CODEC.fieldOf("landmarks").forGetter(source -> source.landmarkIndex)
        ).apply(instance, CLelyetianChunkGenerator::new)
    );

    private static final double TREND_WEIGHT = 0.7;
    private static final double NATURAL_WEIGHT = 0.5;

    private final CLandmarkIndex landmarkIndex;

    public CLelyetianChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings, CLandmarkIndex landmarkIndex) {
        super(biomeSource, settings);
        if (biomeSource instanceof CLelyetianBiomeSource lelyetianSource) {
            this.landmarkIndex = landmarkIndex;
            lelyetianSource.setLandmarkIndex(landmarkIndex);
        } else {
            this.landmarkIndex = null;
            CCanvasMain.LOGGER.warn("CLelyetianChunkGenerator initialized with non-CLelyetianBiomeSource. " +
                "Landmark index will be ignored and generator will be delegated.");
        }
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(@Nonnull WorldGenRegion region, @Nonnull StructureManager structures, 
                            @Nonnull RandomState randomState, @Nonnull ChunkAccess chunk) {
        super.buildSurface(region, structures, randomState, chunk);
        
        var noiseRouter = randomState.router();
        long seed = region.getSeed();
        
        int chunkX = chunk.getPos().getMinBlockX();
        int chunkZ = chunk.getPos().getMinBlockZ();
        
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int worldX = chunkX + localX;
                int worldZ = chunkZ + localZ;

                if (CBiomeInversionManager.isBiomeInverted(region.getBiome(new BlockPos(worldX, 0, worldZ)))) {
                    double continentalness = noiseRouter.continents().compute(new DensityFunction
                        .SinglePointContext(worldX, 0, worldZ));
                    double erosion = noiseRouter.erosion().compute(new DensityFunction
                        .SinglePointContext(worldX << 2, 0, worldZ << 2));
                    double finalCarving = getNoiseCarving(continentalness, erosion);

                    if (this.landmarkIndex != null) {
                        Pair<CLandmark, Double> nearestLandmark = this.landmarkIndex.getNearestInfluential(seed, worldX, worldZ);
                        double landmarkCarving = nearestLandmark == null ? 0.0 : nearestLandmark.getFirst().getLandmarkCarving(nearestLandmark.getSecond());
                        finalCarving = finalCarving * NATURAL_WEIGHT + landmarkCarving * (1.0 - NATURAL_WEIGHT);
                    }

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
                int flippedY = CBlockInversionManager.MIRROR_LEVEL - y;
                // Only flip if target position is within world bounds
                if (flippedY >= minBuildHeight && flippedY <= maxBuildHeight) {
                    // Check if this block has a flipped version
                    BlockState flippedState = CBlockInversionManager.getFlippedBlockState(state);
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


    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState,
            StructureManager structureManager, ChunkAccess chunk) {
        return super.fillFromNoise(blender, randomState, structureManager, chunk);
    }

}
