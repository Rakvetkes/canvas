package org.aki.helvetti.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.aki.helvetti.feature.CBiomeInversionManager;
import org.aki.helvetti.worldgen.structure.placement.CLandmarkManager;
import org.aki.helvetti.worldgen.structure.placement.landmarks.CLandmarkTypes;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Pair;

import java.util.stream.Stream;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;

import org.aki.helvetti.CCanvasMain;

/**
 * Custom BiomeSource for the Lelyetia dimension
 * Uses sequential rule-based matching to determine biomes
 * Each biome has parameter ranges, and the first matching biome is selected
 */
public class CLelyetiaBiomeSource extends BiomeSource {
    private static final List<WeakReference<CLelyetiaBiomeSource>> INSTANCES = Collections.synchronizedList(new ArrayList<>());

    public static final MapCodec<CLelyetiaBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.list(
                RecordCodecBuilder.<CBiomeEntry>create(entryInstance ->
                    entryInstance.group(
                        CNoiseRange.CODEC.fieldOf("parameters").forGetter(CBiomeEntry::parameters),
                        Biome.CODEC.fieldOf("biome").forGetter(CBiomeEntry::biome)
                    ).apply(entryInstance, CBiomeEntry::new)
                )
            ).fieldOf("biomes").forGetter(source -> source.biomeEntries)
        ).apply(instance, CLelyetiaBiomeSource::new)
    );

    private final List<CBiomeEntry> biomeEntries;
    private final Holder<Biome> defaultBiome;
    private final BiMap<Holder<Biome>, Holder<Biome>> inversionCache = HashBiMap.create();
    private boolean cacheInitialized = false;

    private CLelyetiaBiomeSource(List<CBiomeEntry> biomeEntries) {
        this.biomeEntries = new ArrayList<>(biomeEntries);
        // Use the last biome as default fallback
        this.defaultBiome = biomeEntries.isEmpty() ? null : biomeEntries.get(biomeEntries.size() - 1).biome();
        INSTANCES.add(new WeakReference<>(this));
    }

    /**
     * Represents a biome entry with its parameter range conditions
     */
    public record CBiomeEntry(CNoiseRange parameters, Holder<Biome> biome) {}

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.biomeEntries.stream().map(CBiomeEntry::biome);
    }

    @Override
    @Nonnull
    public Holder<Biome> getNoiseBiome(int x, int y, int z, @Nonnull Climate.Sampler sampler) {
        Climate.TargetPoint targetPoint = sampler.sample(x, y, z);
        Holder<Biome> biomeHolder = null;
        
        // Sequential matching: return the first biome whose parameters match
        for (CBiomeEntry entry : this.biomeEntries) {
            if (entry.parameters().matches(targetPoint)) {
                biomeHolder = entry.biome();
                break;
            }
        }

        // Fallback to default biome if no match found
        if (biomeHolder == null) {
            biomeHolder = this.defaultBiome != null ? this.defaultBiome : this.biomeEntries.get(0).biome();
        } else {
            Pair<ResourceLocation, Double> landmarkInfo = CLandmarkManager
                .getNearestInfluentialLandmark((x << 2) + 2, (z << 2) + 2, sampler);
            int inversionLock = landmarkInfo == null ? 0 : CLandmarkTypes.get(landmarkInfo.getFirst()).getInversionLock(landmarkInfo.getSecond());
            if (inversionLock == -1) {
                biomeHolder = toInvertedBiome(biomeHolder);
            } else if (inversionLock == 1) {
                biomeHolder = toNormalBiome(biomeHolder);
            }
        }

        return biomeHolder;
    }

    private Holder<Biome> toInvertedBiome(Holder<Biome> original) {
        if (!cacheInitialized) throw new IllegalStateException("Inversion cache not initialized.");
        return inversionCache.containsKey(original) ? inversionCache.get(original) : original;
    }

    private Holder<Biome> toNormalBiome(Holder<Biome> inverted) {
        if (!cacheInitialized) throw new IllegalStateException("Inversion cache not initialized.");
        return inversionCache.containsValue(inverted) ? inversionCache.inverse().get(inverted) : inverted;
    }

    public void initializeCache() {
        inversionCache.clear();
        Map<ResourceKey<Biome>, Holder<Biome>> availableBiomes = new HashMap<>();
        for (CBiomeEntry entry : this.biomeEntries) {
            entry.biome().unwrapKey().ifPresent(key -> availableBiomes.putIfAbsent(key, entry.biome()));
        }

        for (CBiomeEntry entry : this.biomeEntries) {
            Holder<Biome> normalHolder = entry.biome();
            normalHolder.unwrapKey().ifPresent(key -> {
                CBiomeInversionManager.getInvertedBiome(key).ifPresent(invertedKey -> {
                    Holder<Biome> invertedHolder = availableBiomes.get(invertedKey);
                    if (invertedHolder != null) {
                        CCanvasMain.LOGGER.info("Caching inversion pair: " + normalHolder + " <-> " + invertedHolder);
                        inversionCache.put(normalHolder, invertedHolder);
                    }
                });
            });
        }
        cacheInitialized = true;
    }

    public static void initializeAllCaches() {
        synchronized (INSTANCES) {
            Iterator<WeakReference<CLelyetiaBiomeSource>> iterator = INSTANCES.iterator();
            while (iterator.hasNext()) {
                CLelyetiaBiomeSource source = iterator.next().get();
                if (source == null) {
                    iterator.remove();
                } else {
                    source.initializeCache();
                }
            }
        }
    }

}
