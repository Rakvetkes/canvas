package org.aki.helvetti.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class CBlockInversionManager {

    /** Check if the block position should be inverted */
    public static boolean shouldBeInverted(Level level, BlockPos blockPos) {
        // Currently merely based on biome inversion status
        return CBiomeInversionManager.isBiomeInverted(level.getBiome(blockPos));
    }

}
