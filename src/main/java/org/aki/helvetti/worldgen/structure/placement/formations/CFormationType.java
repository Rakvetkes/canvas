package org.aki.helvetti.worldgen.structure.placement.formations;

import com.mojang.serialization.MapCodec;

public interface CFormationType<T extends CFormation> {
    MapCodec<T> codec();
}
