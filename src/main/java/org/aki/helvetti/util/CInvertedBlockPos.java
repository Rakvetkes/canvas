package org.aki.helvetti.util;

import com.google.errorprone.annotations.Immutable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

import javax.annotation.Nonnull;

/**
 * A BlockPos subclass that inverts the behavior of above() and below() methods.
 * This is useful for generating inverted structures in inverted biomes.
 * 
 * When using this class:
 * - above() actually moves down
 * - below() actually moves up
 * - above(int) moves down by the specified amount
 * - below(int) moves up by the specified amount
 * - offset(int, int, int) & offset(Vec3i) adjust the Y coordinate inversely
 * - immutable() returns a regular BlockPos
 *
 * Anything but immutable() in the list preserves the inverted behavior!
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
    public CInvertedBlockPos above() {
        return this.above(1);
    }
    
    /**
     * Returns a position that is actually above this position (inverted behavior).
     */
    @Override
    @Nonnull
    public CInvertedBlockPos below() {
        return this.below(1);
    }
    
    /**
     * Returns a position that is actually below by the specified distance (inverted behavior).
     */
    @Override
    @Nonnull
    public CInvertedBlockPos above(int distance) {
        return offset(0, distance, 0);
    }
    
    /**
     * Returns a position that is actually above by the specified distance (inverted behavior).
     */
    @Override
    @Nonnull
    public CInvertedBlockPos below(int distance) {
        return offset(0, -distance, 0);
    }

    /**
     * Offsets the position by the specified amounts in each direction (inverted Y direction).
     */
    @Override
    @Nonnull
    public CInvertedBlockPos offset(int x, int y, int z) {
        return new CInvertedBlockPos(this.getX() + x, this.getY() - y, this.getZ() + z);
    }

    /**
     * Offsets the position by the components of the given vector (inverted Y direction).
     */
    @Override
    @Nonnull
    public CInvertedBlockPos offset(Vec3i vec3i) {
        return offset(vec3i.getX(), vec3i.getY(), vec3i.getZ());
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

    /**
     * Returns a BlockPos representing the same coordinates (immutable).
     * @return An immutable BlockPos representing the same coordinates.
     */
    @Override
    @Nonnull
    public BlockPos immutable() {
        return toRegular();
    }
}
