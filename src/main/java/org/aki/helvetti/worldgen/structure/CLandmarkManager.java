package org.aki.helvetti.worldgen.structure;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.ChunkPos;
import com.mojang.datafixers.util.Pair;
import org.apache.commons.lang3.tuple.MutablePair;

import org.aki.helvetti.worldgen.structure.landmarks.CLandmarkTypes;
import org.aki.helvetti.CCanvasMain;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages and caches structure placements that are marked as landmarks.
 * This allows for efficient querying of landmark structures in the world,
 * which can be used for various gameplay or generation features.
 */
public final class CLandmarkManager {
    private static final List<CLocatablePlacement> landmarkPlacements = new ArrayList<>();
    private static long levelSeed;
    private static boolean cacheInitialized = false;
    
    /**
     * Initializes the cache of landmark placements from the server's registry.
     * This should be called when the server starts or when resources are reloaded.
     *
     * @param server The Minecraft server instance.
     */
    public static void initializeCache(@Nonnull MinecraftServer server) {
        landmarkPlacements.clear();
        levelSeed = server.getWorldData().worldGenOptions().seed();
        
        Registry<StructureSet> registry = server.registryAccess().registryOrThrow(Registries.STRUCTURE_SET);
        for (var entry : registry.entrySet()) {
            StructureSet structureSet = entry.getValue();
            if (structureSet.placement() instanceof CLocatablePlacement landmarkPlacement) {
                if (landmarkPlacement.landmarkType() != null) {
                    CCanvasMain.LOGGER.info("Registered landmark structure: " + entry.getKey());
                    landmarkPlacements.add(landmarkPlacement);
                }
            }
        }
        cacheInitialized = true;
    }
    
    /**
     * Checks if a specific chunk contains a landmark structure.
     *
     * @param chunkPos The chunk position to check.
     * @param sampler The climate sampler.
     * @return The ResourceLocation of the landmark type if found, null otherwise.
     */
    @Nullable
    public static ResourceLocation getLandmark(ChunkPos chunkPos, Climate.Sampler sampler) {
        if (!cacheInitialized) throw new IllegalStateException("Landmark cache not initialized.");
        for (CLocatablePlacement placement : landmarkPlacements) {
            if (placement.isPlacementChunk(levelSeed, chunkPos.x, chunkPos.z, sampler)) {
                return placement.landmarkType();
            }
        }
        return null;
    }

    @Nullable
    public static Pair<ResourceLocation, Double> getNearestInfluentialLandmark(int x, int z, Climate.Sampler sampler) {
        if (!cacheInitialized) throw new IllegalStateException("Landmark cache not initialized.");
        MutablePair<ResourceLocation, Double> result = new MutablePair<>(null, Double.MAX_VALUE);

        for (CLocatablePlacement placement : landmarkPlacements) {
            int influentialRange = CLandmarkTypes.get(placement.landmarkType()).getInfluentialRange();
            if (influentialRange <= 0) continue;

            placement.forEachInRadius(levelSeed, new ChunkPos(x >> 4, z >> 4),
                (influentialRange + 15) >> 4, sampler, (target) -> {
                long dx = target.getMiddleBlockX() - x;
                long dz = target.getMiddleBlockZ() - z;
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= influentialRange && dist < result.getRight()) {
                    result.setRight(dist);
                    result.setLeft(placement.landmarkType());
                }
            });
        }
        
        if (result.getLeft() != null) {
            return Pair.of(result.getLeft(), result.getRight());
        }
        return null;
    }
}

