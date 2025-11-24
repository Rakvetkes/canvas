package org.aki.helvetti.worldgen.structure.placement.landmarks;

import java.util.HashSet;
import java.util.Set;

public record CSimpleLandmarkType(int influentialRange, int innerThreshold, int outerThreshold) implements CLandmarkType {
    @Override
    public int getInfluentialRange() {
        return influentialRange;
    }

    static final Set<Integer> i = new HashSet<>();

    @Override
    public int getInversionLock(double value) {
        return value < innerThreshold ? -1 : (value < outerThreshold ? 1 : 0);
    }

    @Override
    public double getLandmarkCarving(double value) {
        double ratio = ((double) innerThreshold - value) / (double) innerThreshold;
        return value < innerThreshold ? ratio * ratio : 0.0;
    }
}
