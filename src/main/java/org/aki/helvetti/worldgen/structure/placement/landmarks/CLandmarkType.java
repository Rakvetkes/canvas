package org.aki.helvetti.worldgen.structure.placement.landmarks;

public interface CLandmarkType {
    int getInfluentialRange();
    int getInversionLock(double value);
    double getLandmarkCarving(double value);
}
