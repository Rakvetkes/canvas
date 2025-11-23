package org.aki.helvetti.client;

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
import org.aki.helvetti.block.CBlocks;
import org.aki.helvetti.item.CItems;

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
        }, CBlocks.FLIPPED_GRASS_BLOCK.get());

        // Register biome-based color for leaf blocks
        event.register((state, level, pos, tintIndex) -> {
            if (level != null && pos != null) {
                return BiomeColors.getAverageFoliageColor(level, pos);
            }
            return FoliageColor.getDefaultColor();
        }, CBlocks.LELYETIAN_BIRCH_LEAVES.get(),
            CBlocks.GLOWING_LELYETIAN_BIRCH_LEAVES.get(),
            CBlocks.LELYETIAN_MAPLE_LEAVES.get());
    }
    
    @SubscribeEvent
    static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // Register biome-based color for flipped grass block item
        // Uses the default grass color for items
        event.register((stack, tintIndex) -> GrassColor.getDefaultColor()
            , CItems.FLIPPED_GRASS_BLOCK_ITEM.get());

        // Register foliage color for leaf items
        event.register((stack, tintIndex) -> FoliageColor.getDefaultColor()
            , CItems.LELYETIAN_BIRCH_LEAVES_ITEM.get(),
                CItems.GLOWING_LELYETIAN_BIRCH_LEAVES_ITEM.get(),
                CItems.LELYETIAN_MAPLE_LEAVES_ITEM.get());
    }
}
