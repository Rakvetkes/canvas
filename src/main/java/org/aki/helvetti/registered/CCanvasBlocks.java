package org.aki.helvetti.registered;

import net.minecraft.world.level.block.LeavesBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.aki.helvetti.CCanvasMain;
import org.aki.helvetti.registered.block.CBlockProperties;
import org.aki.helvetti.registered.block.CFlippedGrassBlock;

public final class CCanvasBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CCanvasMain.MODID);

    public static void register(IEventBus event) {
        BLOCKS.register(event);
    }

    public static final DeferredBlock<CFlippedGrassBlock> FLIPPED_GRASS_BLOCK = BLOCKS.register("flipped_grass_block", () -> new CFlippedGrassBlock(CBlockProperties.grassBlockProperties()));
    public static final DeferredBlock<LeavesBlock> LELYETIAN_BIRCH_LEAVES = BLOCKS.register("lelyetian_birch_leaves", () -> new LeavesBlock(CBlockProperties.leavesProperties()));
    public static final DeferredBlock<LeavesBlock> GLOWING_LELYETIAN_BIRCH_LEAVES = BLOCKS.register("glowing_lelyetian_birch_leaves", () -> new LeavesBlock(CBlockProperties.glowingLeavesProperties()));
    public static final DeferredBlock<LeavesBlock> LELYETIAN_MAPLE_LEAVES = BLOCKS.register("lelyetian_maple_leaves", () -> new LeavesBlock(CBlockProperties.leavesProperties()));

}
