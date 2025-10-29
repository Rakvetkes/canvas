package org.aki.helvetti.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.aki.helvetti.CCanvasMain;

/**
 * Registry for custom chunk generators
 */
public class CChunkGenerators {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = 
        DeferredRegister.create(BuiltInRegistries.CHUNK_GENERATOR, CCanvasMain.MODID);

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<CLelyetiaChunkGenerator>> LELYETIA_CHUNK_GENERATOR = 
        CHUNK_GENERATORS.register("lelyetia_chunk_generator", () -> CLelyetiaChunkGenerator.CODEC);

    public static void register(IEventBus modEventBus) {
        CHUNK_GENERATORS.register(modEventBus);
    }
}
