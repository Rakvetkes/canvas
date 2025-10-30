package org.aki.helvetti.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.server.level.WorldGenRegion;
import org.aki.helvetti.CCanvasMain;
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

    /**
     * Flipping list - maps original block states to their flipped versions.
     * Add custom block state mappings here for terrain inversion.
     */
    private static final Map<BlockState, BlockState> FLIPPING_LIST = new HashMap<>();
    
    static {
        FLIPPING_LIST.put(Blocks.GRASS_BLOCK.defaultBlockState(), CCanvasMain.FLIPPED_GRASS_BLOCK.get().defaultBlockState());
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
    public void buildSurface(@Nonnull WorldGenRegion region, @Nonnull net.minecraft.world.level.StructureManager structures, 
                            @Nonnull RandomState randomState, @Nonnull ChunkAccess chunk) {
        // First, let the parent class generate the normal terrain
        super.buildSurface(region, structures, randomState, chunk);
        
        // Get noise router for accessing noise functions
        var noiseRouter = randomState.router();
        DensityFunction erosion = noiseRouter.erosion();
        DensityFunction continentalness = noiseRouter.continents();
        
        // Get chunk position
        int chunkX = chunk.getPos().getMinBlockX();
        int chunkZ = chunk.getPos().getMinBlockZ();
        
        // Process each column in the chunk
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int worldX = chunkX + localX;
                int worldZ = chunkZ + localZ;
                
                // Sample noise values at this position
                DensityFunction.SinglePointContext context = 
                    new DensityFunction.SinglePointContext(worldX, 0, worldZ);
                double erosionValue = erosion.compute(context);
                double continentalnessValue = continentalness.compute(context);
                
                // Check if conditions are met
                if (erosionValue >= 0.0 && continentalnessValue <= -0.1) {
                    // Find original ground level (c)
                    int groundLevel = findGroundLevel(chunk, localX, localZ);
                    
                    if (groundLevel != Integer.MIN_VALUE) {
                        // Flip blocks from y >= -groundLevel to MIRROR_LEVEL - y
                        flipTerrainColumn(chunk, localX, localZ, groundLevel);
                    }
                }
            }
        }
    }
    
    /**
     * Finds the original ground level in a column.
     * Returns the highest non-air block Y coordinate.
     */
    private int findGroundLevel(ChunkAccess chunk, int x, int z) {
        for (int y = chunk.getMaxBuildHeight() - 1; y >= chunk.getMinBuildHeight(); y--) {
            BlockState state = chunk.getBlockState(new net.minecraft.core.BlockPos(x, y, z));
            if (!state.isAir()) {
                return y;
            }
        }
        return Integer.MIN_VALUE;
    }
    
    /**
     * Flips terrain in a column from y >= -c to position MIRROR_LEVEL - y.
     * 
     * @param chunk The chunk to modify
     * @param x Local x coordinate in chunk
     * @param z Local z coordinate in chunk
     * @param groundLevel The original ground level (c)
     */
    private void flipTerrainColumn(ChunkAccess chunk, int x, int z, int groundLevel) {
        int minY = -groundLevel;
        int maxY = chunk.getMaxBuildHeight() - 1;
        
        // Store blocks that need to be flipped
        Map<Integer, BlockState> blocksToFlip = new HashMap<>();
        
        // Collect all blocks from y >= -groundLevel
        for (int y = minY; y <= maxY; y++) {
            if (y >= chunk.getMinBuildHeight() && y <= chunk.getMaxBuildHeight() - 1) {
                BlockState state = chunk.getBlockState(new net.minecraft.core.BlockPos(x, y, z));
                if (!state.isAir()) {
                    int flippedY = MIRROR_LEVEL - y;
                    // Only flip if target position is within world bounds
                    if (flippedY >= chunk.getMinBuildHeight() && flippedY <= chunk.getMaxBuildHeight() - 1) {
                        // Check if this block has a flipped version
                        BlockState flippedState = getFlippedBlockState(state);
                        blocksToFlip.put(flippedY, flippedState);
                    }
                }
            }
        }
        
        // Clear original blocks
        for (int y = minY; y <= maxY; y++) {
            if (y >= chunk.getMinBuildHeight() && y <= chunk.getMaxBuildHeight() - 1) {
                chunk.setBlockState(new net.minecraft.core.BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), false);
            }
        }
        
        // Place flipped blocks
        for (Map.Entry<Integer, BlockState> entry : blocksToFlip.entrySet()) {
            chunk.setBlockState(new net.minecraft.core.BlockPos(x, entry.getKey(), z), entry.getValue(), false);
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
