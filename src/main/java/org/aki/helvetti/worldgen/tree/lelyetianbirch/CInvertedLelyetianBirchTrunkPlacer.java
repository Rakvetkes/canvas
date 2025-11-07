package org.aki.helvetti.worldgen.tree.lelyetianbirch;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.aki.helvetti.worldgen.tree.CTreePlacers;

import javax.annotation.Nonnull;

public class CInvertedLelyetianBirchTrunkPlacer extends CLelyetianBirchTrunkPlacer {
    public static final MapCodec<CInvertedLelyetianBirchTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
        trunkPlacerParts(instance).apply(instance, CInvertedLelyetianBirchTrunkPlacer::new)
    );

    public CInvertedLelyetianBirchTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    @Nonnull
    protected TrunkPlacerType<?> type() {
        return CTreePlacers.INVERTED_LELYETIAN_BIRCH_TRUNK_PLACER.get();
    }

    @Override
    protected boolean isInverted() {
        return true;
    }
}
