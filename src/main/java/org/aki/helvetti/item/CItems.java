package org.aki.helvetti.item;

import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.aki.helvetti.CCanvasMain;
import org.aki.helvetti.block.CBlocks;

public final class CItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CCanvasMain.MODID);

    public static final DeferredItem<BlockItem> FLIPPED_GRASS_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("flipped_grass_block", CBlocks.FLIPPED_GRASS_BLOCK);
    public static final DeferredItem<BlockItem> LELYETIAN_BIRCH_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("lelyetian_birch_leaves", CBlocks.LELYETIAN_BIRCH_LEAVES);
    public static final DeferredItem<BlockItem> GLOWING_LELYETIAN_BIRCH_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("glowing_lelyetian_birch_leaves", CBlocks.GLOWING_LELYETIAN_BIRCH_LEAVES);
    public static final DeferredItem<BlockItem> LELYETIAN_MAPLE_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("lelyetian_maple_leaves", CBlocks.LELYETIAN_MAPLE_LEAVES);

}
