package org.aki.helvetti.worldgen.tree;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.aki.helvetti.util.CInvertedBlockPos;
import org.aki.helvetti.util.Invertible;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Abstract base class for trunk placers that can be inverted (grow downward).
 * 
 * This class handles the common logic for inverted trees:
 * - Downward space checking
 * - Position conversion to CInvertedBlockPos
 * - Delegating actual trunk placement to subclasses
 */
public abstract class CInvertableTrunkPlacer extends TrunkPlacer {
    
    protected CInvertableTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    /**
     * Determines if this trunk placer generates inverted (downward-growing) trees.
     * 
     * @return true if trees grow downward, false if they grow upward
     */
    protected abstract boolean isInverted();

    /**
     * Places the actual trunk blocks and returns foliage attachment points.
     * This method is called after space checking has been performed.
     * 
     * Subclasses should implement the actual trunk placement logic here.
     * For inverted trees, the pos parameter will already be converted to CInvertedBlockPos.
     * 
     * @param level The level to place in
     * @param blockSetter The block setter
     * @param random Random source
     * @param freeTreeHeight The calculated free tree height
     * @param pos The position to place at (already converted to CInvertedBlockPos if inverted)
     * @param config The tree configuration
     * @return List of foliage attachment points
     */
    protected abstract List<FoliagePlacer.FoliageAttachment> placeTrunkInternal(
            @Nonnull LevelSimulatedReader level,
            @Nonnull BiConsumer<BlockPos, BlockState> blockSetter,
            @Nonnull RandomSource random,
            int freeTreeHeight,
            @Invertible @Nonnull BlockPos pos,
            @Nonnull TreeConfiguration config);

    /**
     * Main entry point for trunk placement.
     * Handles space checking for inverted trees and delegates to placeTrunkInternal.
     */
    @Override
    @Nonnull
    public final List<FoliagePlacer.FoliageAttachment> placeTrunk(
            @Nonnull LevelSimulatedReader level,
            @Nonnull BiConsumer<BlockPos, BlockState> blockSetter,
            @Nonnull RandomSource random,
            int freeTreeHeight,
            @Nonnull BlockPos pos,
            @Nonnull TreeConfiguration config) {

        // For inverted trees, perform downward space check first
        if (isInverted() && !CTreeUtil.checkDownwardSpace(level, pos, freeTreeHeight, config)) {
            // System.out.println("Inverted tree space check failed at " + pos);
            return ImmutableList.of(); // Not enough space, don't place tree
        }

        // Convert position to CInvertedBlockPos if this is an inverted tree
        BlockPos adjustedPos = isInverted() ? CInvertedBlockPos.from(pos) : pos;

        // Delegate to subclass implementation
        return placeTrunkInternal(level, blockSetter, random, freeTreeHeight, adjustedPos, config);
    }
}
