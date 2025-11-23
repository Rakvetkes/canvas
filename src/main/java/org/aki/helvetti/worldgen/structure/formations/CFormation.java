package org.aki.helvetti.worldgen.structure.formations;

import com.mojang.serialization.Codec;
import net.minecraft.core.Vec3i;
import java.util.List;

public interface CFormation {
    Codec<CFormation> CODEC = CFormationTypes.CODEC;

    List<Vec3i> getChunkOffsets(long seed);

    CFormationType<?> type();

    int getMaxChunkDist();
}
