package org.aki.helvetti.worldgen.feature.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;

import javax.annotation.Nonnull;
import java.util.OptionalInt;

/**
 * A feature size that checks space requirements downward instead of upward.
 * This is used for inverted trees that grow downward from the ceiling.
 * 
 * Unlike TwoLayersFeatureSize which checks upward space requirements,
 * this class checks downward space to ensure the inverted tree has enough room to grow.
 */
public class CTwoLayersFeatureSizePlaceholder extends FeatureSize {
    public static final MapCodec<CTwoLayersFeatureSizePlaceholder> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.intRange(0, 81).fieldOf("limit").forGetter(size -> size.limit),
            Codec.intRange(0, 16).fieldOf("lower_size").forGetter(size -> size.lowerSize),
            Codec.intRange(0, 16).fieldOf("upper_size").forGetter(size -> size.upperSize)
        ).apply(instance, CTwoLayersFeatureSizePlaceholder::new)
    );

    private final int limit;
    private final int lowerSize;
    private final int upperSize;

    public CTwoLayersFeatureSizePlaceholder(int limit, int lowerSize, int upperSize) {
        super(OptionalInt.empty());
        this.limit = limit;
        this.lowerSize = lowerSize;
        this.upperSize = upperSize;
    }

    @Override
    @Nonnull
    protected FeatureSizeType<?> type() {
        return CTreePlacers.TWO_LAYERS_FEATURE_SIZE_PLACEHOLDER.get();
    }

    /**
     * Returns the minimum size required at a given height.
     * 
     * IMPORTANT: For inverted trees, we disable Minecraft's built-in upward space check
     * by always returning 0. Instead, we perform our own downward space check in the
     * TrunkPlacer.placeTrunk() method.
     * 
     * Minecraft's TreeFeature always checks space from y to y+height-1 (upward),
     * which is incorrect for inverted trees. By returning 0, we skip this check
     * and implement our own in CInvertedLelyetianBirchTrunkPlacer.
     * 
     * @param height The total tree height
     * @param y The current height offset being checked (0 to height-1)
     * @return Always 0 to disable built-in check
     */
    @Override
    public int getSizeAtHeight(int height, int y) {
        // Disable Minecraft's upward space check
        // We'll do our own downward check in the TrunkPlacer
        return -1;
    }
    
    /**
     * Get the lower size (space needed near the ceiling/base).
     */
    public int getLowerSize() {
        return this.lowerSize;
    }
    
    /**
     * Get the upper size (space needed away from ceiling/toward ground).
     */
    public int getUpperSize() {
        return this.upperSize;
    }
    
    /**
     * Get the limit (boundary between lower and upper zones).
     */
    public int getLimit() {
        return this.limit;
    }
}
