package org.aki.helvetti.worldgen.tree;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

import javax.annotation.Nonnull;

/**
 * Abstract base class for foliage placers that can be inverted (grow downward).
 * 
 * This class handles the common logic for inverted foliage:
 * - Adjusting Y coordinates based on inversion
 * - Providing an isInverted() method for subclasses to override
 */
public abstract class CInvertableFoliagePlacer extends FoliagePlacer {
    
    protected CInvertableFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    /**
     * Determines if this foliage placer generates inverted (downward-growing) foliage.
     * 
     * @return true if foliage grows downward, false if it grows upward
     */
    protected abstract boolean isInverted();

    /**
     * Overrides the standard placeLeavesRow to adjust Y coordinates for inverted trees.
     * For inverted trees, the localY parameter is negated before placement.
     * 
     * @param level The level to place in
     * @param foliageSetter The foliage setter
     * @param random Random source
     * @param config The tree configuration
     * @param pos The center position
     * @param radius The radius of this foliage layer
     * @param localY The Y offset from the center position
     * @param doubleTrunk Whether this is a double trunk tree
     */
    @Override
    protected void placeLeavesRow(
            @Nonnull LevelSimulatedReader level,
            @Nonnull FoliageSetter foliageSetter,
            @Nonnull RandomSource random,
            @Nonnull TreeConfiguration config,
            @Nonnull BlockPos pos,
            int radius,
            int localY,
            boolean doubleTrunk) {
        
        // Adjust Y coordinate based on inversion
        int adjustedY = isInverted() ? -localY : localY;
        
        // Call the parent implementation with adjusted Y
        super.placeLeavesRow(level, foliageSetter, random, config, pos, radius, adjustedY, doubleTrunk);
    }
}
