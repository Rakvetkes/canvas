package org.aki.helvetti.worldgen.structure.formations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;

import java.util.ArrayList;
import java.util.List;

public record CCircularFormation(int radius, int count) implements CFormation {
    public static final MapCodec<CCircularFormation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("radius").forGetter(CCircularFormation::radius),
            Codec.INT.fieldOf("count").forGetter(CCircularFormation::count)
    ).apply(instance, CCircularFormation::new));

    @Override
    public List<Vec3i> getChunkOffsets(long seed) {
        List<Vec3i> offsets = new ArrayList<>();
        // double startAngle = (seed ^ x ^ z) % 360 / 360.0 * 2 * Math.PI;
        double startAngle = 0.0;

        for (int i = 0; i < count; i++) {
            double angle = startAngle + (2 * Math.PI * i / count);
            int offX = (int) (Math.round(Math.cos(angle) * radius));
            int offZ = (int) (Math.round(Math.sin(angle) * radius));
            offsets.add(new Vec3i(offX, 0, offZ));
        }
        return offsets;
    }

    @Override
    public CFormation.Type<?> type() {
        return CFormationTypes.CIRCULAR.get();
    }

    @Override
    public int getMaxChunkDist() {
        return radius;
    }
}
