package org.aki.helvetti.worldgen.structure.landmarks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CSimpleLandmark(int influentialRange, int innerThreshold, int outerThreshold) implements CLandmark {
    public static final MapCodec<CSimpleLandmark> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("influential_range").forGetter(CSimpleLandmark::influentialRange),
            Codec.INT.fieldOf("inner_threshold").forGetter(CSimpleLandmark::innerThreshold),
            Codec.INT.fieldOf("outer_threshold").forGetter(CSimpleLandmark::outerThreshold)
    ).apply(instance, CSimpleLandmark::new));

    @Override
    public int getInfluentialRange() {
        return influentialRange;
    }


    @Override
    public int getInversionLock(double value) {
        return value < innerThreshold ? -1 : (value < outerThreshold ? 1 : 0);
    }

    @Override
    public double getLandmarkCarving(double value) {
        double ratio = ((double) innerThreshold - value) / (double) innerThreshold;
        return value < innerThreshold ? ratio * ratio : 0.0;
    }

    @Override
    public Type<?> type() {
        return CLandmarkTypes.SIMPLE.get();
    }
}
