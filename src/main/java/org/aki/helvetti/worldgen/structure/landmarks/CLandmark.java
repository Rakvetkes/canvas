package org.aki.helvetti.worldgen.structure.landmarks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public interface CLandmark {

    Codec<CLandmark> CODEC = Codec.lazyInitialized(() -> CLandmarkTypes.REGISTRY
        .byNameCodec().dispatch(CLandmark::type, Type::codec));
    
    CLandmark.Type<?> type();

    public static record Type<T extends CLandmark>(MapCodec<T> codec) {}

    int getInfluentialRange();
    int getInversionLock(double value);
    double getLandmarkCarving(double value);
    
}
