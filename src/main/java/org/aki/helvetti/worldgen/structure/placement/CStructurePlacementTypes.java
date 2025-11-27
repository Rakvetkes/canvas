package org.aki.helvetti.worldgen.structure.placement;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.aki.helvetti.CCanvasMain;

/**
 * Registry for custom structure placement types.
 */
public final class CStructurePlacementTypes {
    public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPES = 
        DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, CCanvasMain.MODID);

    /**
     * Landmark structure placement type - supports noise-based filtering
     */
    public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<CBasicLandmarkPlacement>> BASIC = 
        STRUCTURE_PLACEMENT_TYPES.register("basic", () -> () -> CBasicLandmarkPlacement.CODEC);

    /**
     * Relative structure placement type - places structure relative to another structure's placement
     */
    public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<CRelativePlacement>> RELATIVE = 
        STRUCTURE_PLACEMENT_TYPES.register("relative", () -> () -> CRelativePlacement.CODEC);

    public static void register(IEventBus eventBus) {
        STRUCTURE_PLACEMENT_TYPES.register(eventBus);
    }
}
