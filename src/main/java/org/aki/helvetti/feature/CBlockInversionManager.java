package org.aki.helvetti.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

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

}
