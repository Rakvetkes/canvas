package org.aki.helvetti.feature;

import java.util.HashMap;
import java.util.Map;

import org.aki.helvetti.block.CBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public final class CBlockInversionManager {

    public static final int MIRROR_LEVEL = 200;

    /** Check if the block position should be inverted */
    public static boolean shouldBeInverted(Level level, BlockPos blockPos) {
        // Currently merely based on biome inversion status
        return CBiomeInversionManager.isBiomeInverted(level.getBiome(blockPos));
    }

    /** Check if the block position should be inverted during world generation */
    public static boolean shouldBeInvertedWG(LevelReader levelReader, BlockPos blockPos) {
        return CBiomeInversionManager.isBiomeInverted(levelReader.getBiome(blockPos));
    }

    /**
     * Flipping list - maps original block states to their flipped versions.
     * Add custom block state mappings here for terrain inversion.
     */
    public static final Map<BlockState, BlockState> FLIPPING_LIST = new HashMap<>();
    
    public static void registerFlippingList(FMLCommonSetupEvent event) {
        FLIPPING_LIST.put(Blocks.GRASS_BLOCK.defaultBlockState(), CBlocks.FLIPPED_GRASS_BLOCK.get().defaultBlockState());
    }

    /**
     * Gets the flipped version of a block state based on the flipping list.
     * If the block state is not in the flipping list, returns the original state.
     * 
     * @param original The original block state
     * @return The flipped block state, or original if no mapping exists
     */
    public static BlockState getFlippedBlockState(BlockState original) {
        BlockState flippedState = FLIPPING_LIST.get(original);
        return (flippedState != null) ? flippedState : original;
    }

}
