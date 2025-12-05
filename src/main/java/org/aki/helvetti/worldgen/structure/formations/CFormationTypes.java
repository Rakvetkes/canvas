package org.aki.helvetti.worldgen.structure.formations;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.aki.helvetti.CCanvasMain;

public final class CFormationTypes {
    
    public static final ResourceKey<Registry<CFormation.Type<?>>> REGISTRY_KEY = ResourceKey
        .createRegistryKey(ResourceLocation.fromNamespaceAndPath(CCanvasMain.MODID, "formation_type"));
    
    public static final DeferredRegister<CFormation.Type<?>> FORMATION_TYPES = DeferredRegister.create(REGISTRY_KEY, CCanvasMain.MODID);
    public static final Registry<CFormation.Type<?>> REGISTRY = FORMATION_TYPES.makeRegistry(builder -> {});
    
    private static <T extends CFormation> DeferredHolder<CFormation.Type<?>, CFormation.Type<T>> register(String name, MapCodec<T> codec) {
        return FORMATION_TYPES.register(name, () -> new CFormation.Type<>(codec));
    }

    public static void register(IEventBus eventBus) {
        FORMATION_TYPES.register(eventBus);
    }
    
    public static final DeferredHolder<CFormation.Type<?>, CFormation.Type<CFixedFormation>> FIXED = register("fixed", CFixedFormation.CODEC);
    public static final DeferredHolder<CFormation.Type<?>, CFormation.Type<CCircularFormation>> CIRCULAR = register("circular", CCircularFormation.CODEC);

}
