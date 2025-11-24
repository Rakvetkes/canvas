package org.aki.helvetti.worldgen.feature.tree;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import javax.annotation.Nonnull;

/**
 * Utility class for tree generation in Lelyetia dimension.
 */
public final class CTreeUtil {

    /**
     * Check if there's enough space below for an inverted tree to grow.
     * This method checks downward from the given position.
     * 
     * @param level The level to check in
     * @param pos The starting position (ceiling attachment point)
     * @param height The total tree height
     * @param config The tree configuration
     * @return true if there's enough space, false otherwise
     */
    public static boolean checkDownwardSpace(@Nonnull LevelSimulatedReader level, 
                                             @Nonnull BlockPos pos, 
                                             int height, 
                                             @Nonnull TreeConfiguration config) {
        // Get the feature size to determine space requirements
        if (!(config.minimumSize instanceof CTwoLayersFeatureSizePlaceholder)) {
            // If not using inverted feature size, just allow it
            return true;
        }
        
        CTwoLayersFeatureSizePlaceholder featureSize = (CTwoLayersFeatureSizePlaceholder) config.minimumSize;
        int limit = featureSize.getLimit();
        int lowerSize = featureSize.getLowerSize();
        int upperSize = featureSize.getUpperSize();
        
        // Check space downward
        for (int y = 0; y < height; y++) {
            BlockPos checkPos = pos.below(y);
            
            // Determine required radius at this height
            int radius = (y >= limit) ? upperSize : lowerSize;
            
            // Check horizontal space around this position
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos testPos = checkPos.offset(x, 0, z);
                    // Check if this position can be replaced
                    if (!TreeFeature.validTreePos(level, testPos)) {
                        // System.out.println("CTreeUtil: Space check failed at " + testPos 
                        //        + " (y offset=" + y + ", radius=" + radius + ")");
                        return false;
                    }
                }
            }
        }
        
        // System.out.println("CTreeUtil: Inverted tree space check passed at " + pos + " with height " + height);
        return true;
    }
}
