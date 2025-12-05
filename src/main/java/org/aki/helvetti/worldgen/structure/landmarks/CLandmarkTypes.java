package org.aki.helvetti.worldgen.structure.landmarks;

import org.aki.helvetti.CCanvasMain;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CLandmarkTypes {
    
    public static final ResourceKey<Registry<CLandmark.Type<?>>> REGISTRY_KEY = ResourceKey
        .createRegistryKey(ResourceLocation.fromNamespaceAndPath(CCanvasMain.MODID, "landmark_type"));
    
    public static final DeferredRegister<CLandmark.Type<?>> LANDMARK_TYPES = DeferredRegister.create(REGISTRY_KEY, CCanvasMain.MODID);
    public static final Registry<CLandmark.Type<?>> REGISTRY = LANDMARK_TYPES.makeRegistry(builder -> {});
    
    private static <T extends CLandmark> DeferredHolder<CLandmark.Type<?>, CLandmark.Type<T>> register(String name, MapCodec<T> codec) {
        return LANDMARK_TYPES.register(name, () -> new CLandmark.Type<>(codec));
    }


    public static void register(IEventBus eventBus) {
        LANDMARK_TYPES.register(eventBus);
    }

    public static final DeferredHolder<CLandmark.Type<?>, CLandmark.Type<CSimpleLandmark>> SIMPLE = register("simple", CSimpleLandmark.CODEC);
    
}
