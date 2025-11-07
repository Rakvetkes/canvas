package org.aki.helvetti.worldgen.tree.lelyetianbirch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import org.aki.helvetti.worldgen.tree.CTreePlacers;

import javax.annotation.Nonnull;

public class CInvertedLelyetianBirchFoliagePlacer extends CLelyetianBirchFoliagePlacer {
    public static final MapCodec<CInvertedLelyetianBirchFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
        foliagePlacerParts(instance).and(
            Codec.intRange(0, 16).fieldOf("height").forGetter(placer -> placer.height)
        ).apply(instance, CInvertedLelyetianBirchFoliagePlacer::new)
    );

    public CInvertedLelyetianBirchFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset, height);
    }

    @Override
    @Nonnull
    protected FoliagePlacerType<?> type() {
        return CTreePlacers.INVERTED_LELYETIAN_BIRCH_FOLIAGE_PLACER.get();
    }

    @Override
    protected boolean isInverted() {
        return true;
    }
}
