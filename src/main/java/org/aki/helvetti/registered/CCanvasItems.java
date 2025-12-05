package org.aki.helvetti.registered;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.aki.helvetti.CCanvasMain;

public final class CCanvasItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CCanvasMain.MODID);

    public static void register(IEventBus event) {
        ITEMS.register(event);
    }

    public static final DeferredItem<BlockItem> FLIPPED_GRASS_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("flipped_grass_block", CCanvasBlocks.FLIPPED_GRASS_BLOCK);
    public static final DeferredItem<BlockItem> LELYETIAN_BIRCH_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("lelyetian_birch_leaves", CCanvasBlocks.LELYETIAN_BIRCH_LEAVES);
    public static final DeferredItem<BlockItem> GLOWING_LELYETIAN_BIRCH_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("glowing_lelyetian_birch_leaves", CCanvasBlocks.GLOWING_LELYETIAN_BIRCH_LEAVES);
    public static final DeferredItem<BlockItem> LELYETIAN_MAPLE_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("lelyetian_maple_leaves", CCanvasBlocks.LELYETIAN_MAPLE_LEAVES);

}
