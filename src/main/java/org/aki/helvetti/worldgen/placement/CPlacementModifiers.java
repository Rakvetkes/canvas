package org.aki.helvetti.worldgen.placement;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.aki.helvetti.CCanvasMain;

/**
 * Registry for custom placement modifiers
 */
public class CPlacementModifiers {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = 
        DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, CCanvasMain.MODID);

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<CInvertedHeightmapPlacement>> INVERTED_HEIGHTMAP = 
        PLACEMENT_MODIFIERS.register("inverted_heightmap", 
            () -> () -> CInvertedHeightmapPlacement.CODEC);

    public static void register(IEventBus modEventBus) {
        PLACEMENT_MODIFIERS.register(modEventBus);
    }
}
