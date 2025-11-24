package org.aki.helvetti.worldgen.feature.tree.lelyetianmaple;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import javax.annotation.Nonnull;

import org.aki.helvetti.worldgen.feature.tree.CTreePlacers;

public class CInvertedLelyetianMapleTrunkPlacer extends CLelyetianMapleTrunkPlacer {
    public static final MapCodec<CInvertedLelyetianMapleTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
        trunkPlacerParts(instance).apply(instance, CInvertedLelyetianMapleTrunkPlacer::new)
    );

    public CInvertedLelyetianMapleTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override @Nonnull
    protected TrunkPlacerType<?> type() {
        return CTreePlacers.INVERTED_LELYETIAN_MAPLE_TRUNK_PLACER.get();
    }

    @Override
    protected boolean isInverted() {
        return true;
    }
}
