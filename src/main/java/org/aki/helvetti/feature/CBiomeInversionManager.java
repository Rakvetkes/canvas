package org.aki.helvetti.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.biome.Biome;
import org.aki.helvetti.CCanvasMain;

import javax.annotation.Nonnull;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * JSON format ({@code worldgen/biome/<biome_id>.json}):
 * {@code "inverted": "<normal_biome_id>"} for inverted biomes that have a normal counterpart;
 * {@code "inverted": null} for inverted biomes that are independent
 */
public final class CBiomeInversionManager {

    private static final BiMap<ResourceKey<Biome>, ResourceKey<Biome>> INVERTED_TO_NORMAL = HashBiMap.create();
    private static final Set<ResourceKey<Biome>> INDEPENDENT_INVERTED_BIOMES = new HashSet<>();
    private static final Gson GSON = new Gson();

    /** check if the biome is an inverted biome */
    public static boolean isBiomeInverted(Holder<Biome> biomeHolder) {
        return biomeHolder.unwrapKey().map(key -> {
            return INVERTED_TO_NORMAL.containsKey(key) || INDEPENDENT_INVERTED_BIOMES.contains(key);
        }).orElse(false);
    }

    /** get normal biome for an inverted biome. null if not present or not inverted */
    public static Optional<ResourceKey<Biome>> getNormalBiome(ResourceKey<Biome> invertedBiome) {
        return Optional.ofNullable(INVERTED_TO_NORMAL.get(invertedBiome));
    }

    /** get inverted biome for a normal biome. null if not present or already inverted */
    public static Optional<ResourceKey<Biome>> getInvertedBiome(ResourceKey<Biome> normalBiome) {
        return Optional.ofNullable(INVERTED_TO_NORMAL.inverse().get(normalBiome));
    }

    public static SimpleJsonResourceReloadListener createLoader() {
        return new InversionLoader();
    }

    private static class InversionLoader extends SimpleJsonResourceReloadListener {
        public InversionLoader() {
            super(GSON, "worldgen/biome");
        }

        @Override
        protected void apply(@Nonnull Map<ResourceLocation, JsonElement> object, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
            INVERTED_TO_NORMAL.clear();
            object.forEach((location, json) -> {
                if (json.isJsonObject()) {
                    JsonObject jsonObject = json.getAsJsonObject();
                    if (jsonObject.has("inverted")) {
                        try {
                            JsonElement item = jsonObject.get("inverted");
                            if (!item.isJsonNull()) {
                                String normalBiomeId = item.getAsString();
                                ResourceLocation normalLocation = ResourceLocation.parse(normalBiomeId);
                                ResourceKey<Biome> invertedKey = ResourceKey.create(Registries.BIOME, location);
                                ResourceKey<Biome> normalKey = ResourceKey.create(Registries.BIOME, normalLocation);
                                INVERTED_TO_NORMAL.put(invertedKey, normalKey);
                                // CCanvasMain.LOGGER.info("Registered inversion: {} -> {}", normalKey, invertedKey);
                            } else {
                                ResourceKey<Biome> invertedKey = ResourceKey.create(Registries.BIOME, location);
                                INDEPENDENT_INVERTED_BIOMES.add(invertedKey);
                                // CCanvasMain.LOGGER.info("Registered inverted biome: {}", invertedKey);
                            }
                        } catch (Exception e) {
                            CCanvasMain.LOGGER.error("Failed to parse inverted biome config for {}", location, e);
                        }
                    }
                }
            });
        }
    }
}
