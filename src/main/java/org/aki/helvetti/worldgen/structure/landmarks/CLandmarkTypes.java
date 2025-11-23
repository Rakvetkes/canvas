package org.aki.helvetti.worldgen.structure.landmarks;

import net.minecraft.resources.ResourceLocation;
import org.aki.helvetti.CCanvasMain;

import java.util.HashMap;
import java.util.Map;

public class CLandmarkTypes {
    private static final Map<ResourceLocation, CLandmarkType> REGISTRY = new HashMap<>();

    public static final CLandmarkType NONE = register("none", new CSimpleLandmarkType(0, 0, 0));
    public static final CLandmarkType BASILICA = register("basilica", new CSimpleLandmarkType(128, 32, 128));

    public static CLandmarkType register(String name, CLandmarkType type) {
        REGISTRY.put(ResourceLocation.fromNamespaceAndPath(CCanvasMain.MODID, name), type);
        return type;
    }

    public static CLandmarkType get(ResourceLocation id) {
        return REGISTRY.get(id);
    }
}
