package org.aki.helvetti.util;

import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;

import com.google.errorprone.annotations.Immutable;

/**
 * A BlockPos subclass that inverts the behavior of above() and below() methods.
 * This is useful for generating inverted structures in inverted biomes.
 * 
 * When using this class:
 * - above() actually moves down
 * - below() actually moves up
 * - above(int) moves down by the specified amount
 * - below(int) moves up by the specified amount
 * 
 * 
 * Don't use it too often... just some grammar sugar for specific cases
 * 
 */
@Immutable
public class CInvertedBlockPos extends BlockPos {
    
    /**
     * Creates an InvertedBlockPos from coordinates.
     */
    public CInvertedBlockPos(int x, int y, int z) {
        super(x, y, z);
    }
    
    /**
     * Creates an InvertedBlockPos from an existing BlockPos.
     */
    public CInvertedBlockPos(BlockPos pos) {
        super(pos.getX(), pos.getY(), pos.getZ());
    }
    
    /**
     * Returns a position that is actually below this position (inverted behavior).
     */
    @Override
    @Nonnull
    public BlockPos above() {
        return this.above(1);
    }
    
    /**
     * Returns a position that is actually above this position (inverted behavior).
     */
    @Override
    @Nonnull
    public BlockPos below() {
        return this.below(1);
    }
    
    /**
     * Returns a position that is actually below by the specified distance (inverted behavior).
     */
    @Override
    @Nonnull
    public BlockPos above(int distance) {
        return super.below(distance);
    }
    
    /**
     * Returns a position that is actually above by the specified distance (inverted behavior).
     */
    @Override
    @Nonnull
    public BlockPos below(int distance) {
        return super.above(distance);
    }
    
    /**
     * Converts a regular BlockPos to an InvertedBlockPos.
     */
    public static CInvertedBlockPos from(BlockPos pos) {
        if (pos instanceof CInvertedBlockPos) {
            return (CInvertedBlockPos) pos;
        }
        return new CInvertedBlockPos(pos);
    }
    
    /**
     * Converts this InvertedBlockPos back to a regular BlockPos.
     */
    public BlockPos toRegular() {
        return new BlockPos(this.getX(), this.getY(), this.getZ());
    }
}
