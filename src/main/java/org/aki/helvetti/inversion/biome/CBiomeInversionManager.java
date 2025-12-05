package org.aki.helvetti.inversion.biome;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.aki.helvetti.CCanvasMain;

/**
 * Manages biome inversions using NeoForge DataMaps.
 * <p>
 * Data is loaded from {@code data/<namespace>/data_maps/helvetti/biome_inversion.json}.
 */
public final class CBiomeInversionManager {

    private static final Map<ResourceKey<Biome>, Holder<Biome>> NORMAL_TO_INVERTED = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Biome>, Holder<Biome>> INVERTED_TO_NORMAL = new ConcurrentHashMap<>();

    /** check if the biome is an inverted biome */
    public static boolean isBiomeInverted(Holder<Biome> biomeHolder) {
        return biomeHolder.getData(CBiomeDataMaps.INVERSION_DATA) != null;
    }

    /** get normal biome for an inverted biome. null if not present or not inverted */
    public static Optional<Holder<Biome>> getNormalBiome(Holder<Biome> invertedBiome) {
        return invertedBiome.unwrapKey().map(INVERTED_TO_NORMAL::get);
    }

    /** get inverted biome for a normal biome. null if not present or already inverted */
    public static Optional<Holder<Biome>> getInvertedBiome(Holder<Biome> normalBiome) {
        return normalBiome.unwrapKey().map(NORMAL_TO_INVERTED::get);
    }

    public static void onTagsUpdated(RegistryAccess registries) {
        NORMAL_TO_INVERTED.clear();
        INVERTED_TO_NORMAL.clear();
        
        registries.registry(Registries.BIOME).ifPresent(registry -> {
            registry.holders().forEach(holder -> {
                CBiomeInversionData data = holder.getData(CBiomeDataMaps.INVERSION_DATA);
                if (data != null && data.normalBiome().isPresent()) {
                    ResourceKey<Biome> normalKey = data.normalBiome().get();
                    NORMAL_TO_INVERTED.put(normalKey, holder);
                    holder.unwrapKey().ifPresent(invertedKey -> {
                        registry.getHolder(normalKey).ifPresent(normalHolder -> {
                            INVERTED_TO_NORMAL.put(invertedKey, normalHolder);
                        });
                    });
                }
            });
        });
        
        CCanvasMain.LOGGER.info("Loaded {} biome inversions", NORMAL_TO_INVERTED.size());
    }
}
