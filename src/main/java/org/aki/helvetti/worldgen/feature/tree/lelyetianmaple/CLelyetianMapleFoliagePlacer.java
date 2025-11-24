package org.aki.helvetti.worldgen.feature.tree.lelyetianmaple;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

import javax.annotation.Nonnull;

import org.aki.helvetti.worldgen.feature.tree.CInvertableFoliagePlacer;
import org.aki.helvetti.worldgen.feature.tree.CTreePlacers;

public class CLelyetianMapleFoliagePlacer extends CInvertableFoliagePlacer {
    public static final MapCodec<CLelyetianMapleFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
        foliagePlacerParts(instance).and(
            Codec.floatRange(0.0f, 10.0f).fieldOf("sphere_radius")
                .forGetter(placer -> placer.radius)
        ).apply(instance, CLelyetianMapleFoliagePlacer::new)
    );

    protected final float radius;

    public CLelyetianMapleFoliagePlacer(IntProvider radius, IntProvider offset, float sphereRadius) {
        super(radius, offset);
        this.radius = sphereRadius;
    }

    @Override
    protected boolean isInverted() {
        return false;
    }

    @Override
    @Nonnull
    protected FoliagePlacerType<?> type() {
        return CTreePlacers.LELYETIAN_MAPLE_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(@Nonnull LevelSimulatedReader level, @Nonnull FoliageSetter blockSetter, @Nonnull RandomSource random, @Nonnull TreeConfiguration config, int maxFreeTreeHeight, @Nonnull FoliageAttachment attachment, int foliageHeight, int foliageRadius, int offset) {
        BlockPos pos = attachment.pos();
        for (int y = offset + foliageHeight / 2; y >= offset - foliageHeight / 2; --y) {
            placeLeavesRow(level, blockSetter, random, config, pos, Mth.floor(this.radius), y, false);
        }
    }

    @Override
    public int foliageHeight(@Nonnull RandomSource random, int height, @Nonnull TreeConfiguration config) {
        return Mth.floor(this.radius) * 2 + 1;
    }

    @Override
    protected boolean shouldSkipLocation(@Nonnull RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        int localRad = localX * localX + localY * localY + localZ * localZ;
        return localRad > this.radius * this.radius;
    }

    public float sphereRadius() {
        return this.radius;
    }
}
