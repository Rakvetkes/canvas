package org.aki.helvetti.registered;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;

import org.aki.helvetti.CCanvasMain;

@EventBusSubscriber(modid = CCanvasMain.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CClientEventHandler {
    // should i put this in a GAME bus handler¿
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        CCanvasMain.LOGGER.info("<Alacy> client here, {}", Minecraft.getInstance().getUser().getName());
    }
    
    @SubscribeEvent
    static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        // Register biome-based color for flipped grass block
        // The block uses tintindex 0 for the grass-colored parts
        event.register((state, level, pos, tintIndex) -> {
            // Return grass color based on biome
            if (level != null && pos != null) {
                return BiomeColors.getAverageGrassColor(level, pos);
            }
            return GrassColor.getDefaultColor();
        }, CCanvasBlocks.FLIPPED_GRASS_BLOCK.get());

        // Register biome-based color for leaf blocks
        event.register((state, level, pos, tintIndex) -> {
            if (level != null && pos != null) {
                return BiomeColors.getAverageFoliageColor(level, pos);
            }
            return FoliageColor.getDefaultColor();
        }, CCanvasBlocks.LELYETIAN_BIRCH_LEAVES.get(),
            CCanvasBlocks.GLOWING_LELYETIAN_BIRCH_LEAVES.get(),
            CCanvasBlocks.LELYETIAN_MAPLE_LEAVES.get());
    }
    
    @SubscribeEvent
    static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // Register biome-based color for flipped grass block item
        // Uses the default grass color for items
        event.register((stack, tintIndex) -> GrassColor.getDefaultColor()
            , CCanvasItems.FLIPPED_GRASS_BLOCK_ITEM.get());

        // Register foliage color for leaf items
        event.register((stack, tintIndex) -> FoliageColor.getDefaultColor()
            , CCanvasItems.LELYETIAN_BIRCH_LEAVES_ITEM.get(),
                CCanvasItems.GLOWING_LELYETIAN_BIRCH_LEAVES_ITEM.get(),
                CCanvasItems.LELYETIAN_MAPLE_LEAVES_ITEM.get());
    }
    
}
