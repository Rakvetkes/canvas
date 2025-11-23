package org.aki.helvetti.worldgen.structure.landmarks;

public interface CLandmarkType {
    int getInfluentialRange();
    int getInversionLock(double value);
    double getLandmarkCarving(double value);
}
