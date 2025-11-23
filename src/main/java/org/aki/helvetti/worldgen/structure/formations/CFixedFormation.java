package org.aki.helvetti.worldgen.structure.formations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;

import java.util.List;

public class CFixedFormation implements CFormation {
    private static final Codec<Vec3i> XZ_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("x").forGetter(Vec3i::getX),
            Codec.INT.fieldOf("z").forGetter(Vec3i::getZ)
    ).apply(instance, (x, z) -> new Vec3i(x, 0, z)));

    public static final MapCodec<CFixedFormation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            XZ_CODEC.listOf().fieldOf("offsets").forGetter(CFixedFormation::offsets)
    ).apply(instance, CFixedFormation::new));

    private final List<Vec3i> offsets;
    private final int maxDistance;

    public CFixedFormation(List<Vec3i> offsets) {
        this.offsets = offsets;
        this.maxDistance = offsets.stream()
                .mapToInt(vec -> Math.max(Math.abs(vec.getX()), Math.abs(vec.getZ())))
                .max()
                .orElse(0);
    }

    public List<Vec3i> offsets() {
        return offsets;
    }

    @Override
    public List<Vec3i> getChunkOffsets(long seed) {
        return offsets;
    }

    @Override
    public CFormationType<?> type() {
        return CFormationTypes.FIXED;
    }

    @Override
    public int getMaxChunkDist() {
        return maxDistance;
    }
}
