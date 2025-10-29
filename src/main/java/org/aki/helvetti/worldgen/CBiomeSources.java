package org.aki.helvetti.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.biome.BiomeSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.aki.helvetti.CCanvasMain;

/**
 * Registry for custom biome sources
 */
public class CBiomeSources {
    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES = 
        DeferredRegister.create(BuiltInRegistries.BIOME_SOURCE, CCanvasMain.MODID);

    public static final DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<CLelyetiaBiomeSource>> LELYETIA_BIOME_SOURCE = 
        BIOME_SOURCES.register("lelyetia_biome_source", () -> CLelyetiaBiomeSource.CODEC);

    public static void register(IEventBus modEventBus) {
        BIOME_SOURCES.register(modEventBus);
    }
}
