package org.aki.helvetti.worldgen.feature.tree.lelyetianbirch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

import javax.annotation.Nonnull;

import org.aki.helvetti.worldgen.feature.tree.CInvertableFoliagePlacer;
import org.aki.helvetti.worldgen.feature.tree.CTreePlacers;

/**
 * A foliage placer for Lelyetian Birch trees that places multiple layers of leaves
 * with decreasing size as the tree gets taller. Each foliage attachment point
 * gets a birch-like blob of leaves, with higher layers having smaller blobs.
 */
public class CLelyetianBirchFoliagePlacer extends CInvertableFoliagePlacer {
    public static final MapCodec<CLelyetianBirchFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
        foliagePlacerParts(instance).and(
            Codec.intRange(0, 16).fieldOf("height").forGetter(placer -> placer.height)
        ).apply(instance, CLelyetianBirchFoliagePlacer::new)
    );

    protected final int height;

    public CLelyetianBirchFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset);
        this.height = height;
    }

    @Override
    @Nonnull
    protected FoliagePlacerType<?> type() {
        return CTreePlacers.LELYETIAN_BIRCH_FOLIAGE_PLACER.get();
    }

    protected boolean isInverted() {
        return false;
    }

    @Override
    protected void createFoliage(
            @Nonnull LevelSimulatedReader level,
            @Nonnull FoliageSetter foliageSetter,
            @Nonnull RandomSource random,
            @Nonnull TreeConfiguration config,
            int maxFreeTreeHeight,
            @Nonnull FoliageAttachment attachment,
            int foliageHeight,
            int foliageRadius,
            int offset) {
        
        BlockPos pos = attachment.pos();
        
        // Place foliage layers - Y adjustment is handled by CInvertableFoliagePlacer.placeLeavesRow
        for (int y = offset; y >= offset - foliageHeight; y--) {
            int layer = y - (offset - foliageHeight);
            int layerRadius = Math.max(1, foliageRadius + attachment.radiusOffset() - layer / 2);
            placeLeavesRow(level, foliageSetter, random, config, pos, layerRadius,
                y, attachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(@Nonnull RandomSource random, int height, @Nonnull TreeConfiguration config) {
        return this.height;
    }

    @Override
    protected boolean shouldSkipLocation(@Nonnull RandomSource random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        if (Math.abs(dx) == radius && Math.abs(dz) == radius) {
            return random.nextInt(3) != 0;
        } else if (Math.abs(dx) == radius || Math.abs(dz) == radius) {
            return random.nextInt(3) == 0;
        }
        return false;
    }
}
