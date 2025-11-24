package org.aki.helvetti.worldgen.structure.placement.formations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import org.aki.helvetti.CCanvasMain;

import java.util.HashMap;
import java.util.Map;

public final class CFormationTypes {
    private static final Map<ResourceLocation, CFormationType<?>> REGISTRY = new HashMap<>();

    public static final CFormationType<CFixedFormation> FIXED = register("fixed", CFixedFormation.CODEC);
    public static final CFormationType<CCircularFormation> CIRCULAR = register("circular", CCircularFormation.CODEC);

    public static <T extends CFormation> CFormationType<T> register(String name, MapCodec<T> codec) {
        CFormationType<T> type = () -> codec;
        REGISTRY.put(ResourceLocation.fromNamespaceAndPath(CCanvasMain.MODID, name), type);
        return type;
    }

    public static final Codec<CFormation> CODEC = ResourceLocation.CODEC.dispatch(
        CFormationTypes::getKey,
        CFormationTypes::getCodec
    );

    private static ResourceLocation getKey(CFormation formation) {
        CFormationType<?> type = formation.type();
        return REGISTRY.entrySet().stream()
                .filter(e -> e.getValue() == type)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown formation type: " + type));
    }

    private static MapCodec<? extends CFormation> getCodec(ResourceLocation location) {
        CFormationType<?> type = REGISTRY.get(location);
        if (type == null) {
            throw new IllegalArgumentException("Unknown formation type: " + location);
        }
        return type.codec();
    }
}
