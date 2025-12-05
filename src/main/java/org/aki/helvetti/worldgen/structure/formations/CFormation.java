package org.aki.helvetti.worldgen.structure.formations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Vec3i;
import java.util.List;

public interface CFormation {

    Codec<CFormation> CODEC = Codec.lazyInitialized(() -> CFormationTypes.REGISTRY
        .byNameCodec().dispatch(CFormation::type, Type::codec));

    CFormation.Type<?> type();

    public static record Type<T extends CFormation>(MapCodec<T> codec) {}

    List<Vec3i> getChunkOffsets(long seed);
    int getMaxChunkDist();

}
