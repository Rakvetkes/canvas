package org.aki.helvetti.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.ChunkPos;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;

import org.apache.commons.lang3.tuple.MutablePair;
import org.aki.helvetti.worldgen.structure.landmarks.CLandmark;
import org.aki.helvetti.worldgen.structure.placement.CLocatablePlacement;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages and caches structure placements that are marked as landmarks.
 * This allows for efficient querying of landmark structures in the world,
 * which can be used for various gameplay or generation features.
 */
public class CLandmarkIndex {
    
    public static final MapCodec<CLandmarkIndex> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryFileCodec.create(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC).listOf().fieldOf("structure_sets").forGetter(index -> index.structureSets)
    ).apply(instance, CLandmarkIndex::new));

    private final List<Holder<StructureSet>> structureSets;
    private final List<CLocatablePlacement> landmarkPlacements = new ArrayList<>();
    private boolean isInitialized = false;

    public CLandmarkIndex(List<Holder<StructureSet>> structureSets) {
        this.structureSets = structureSets;
    }
    
    public void ensureInitialized() {
        if (!isInitialized) {
            initialize();
            isInitialized = true;
        }
    }

    /**
     * Initializes the cache of landmark placements from the provided structure sets.
     * Populates the internal list of locatable placements for faster lookup.
     */
    public void initialize() {
        for (Holder<StructureSet> holder : structureSets) {
            if (holder.isBound()
                    && holder.value().placement() instanceof CLocatablePlacement landmarkPlacement
                    && landmarkPlacement.landmark() != null) {
                landmarkPlacements.add(landmarkPlacement);
            }
        }
    }
    
    @Nullable
    public CLandmark getLandmark(long levelSeed, ChunkPos chunkPos) {
        ensureInitialized();
        for (CLocatablePlacement placement : landmarkPlacements) {
            if (placement.isPlacementChunk(levelSeed, chunkPos.x, chunkPos.z)) {
                return placement.landmark();
            }
        }
        return null;
    }

    @Nullable
    public Pair<CLandmark, Double> getNearestInfluential(long levelSeed, int x, int z) {
        MutablePair<CLandmark, Double> result = new MutablePair<>(null, Double.MAX_VALUE);

        ensureInitialized();
        for (CLocatablePlacement placement : landmarkPlacements) {
            int influentialRange = placement.landmark().getInfluentialRange();
            if (influentialRange <= 0) continue;

            placement.forEachInRadius(levelSeed, new ChunkPos(x >> 4, z >> 4),
                (influentialRange + 15) >> 4, (target) -> {
                long dx = target.getMiddleBlockX() - x;
                long dz = target.getMiddleBlockZ() - z;
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= influentialRange && dist < result.getRight()) {
                    result.setRight(dist);
                    result.setLeft(placement.landmark());
                }
            });
        }
        
        if (result.getLeft() != null) {
            return Pair.of(result.getLeft(), result.getRight());
        }
        return null;
    }
}

