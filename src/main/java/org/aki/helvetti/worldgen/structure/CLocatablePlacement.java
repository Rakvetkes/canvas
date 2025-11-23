package org.aki.helvetti.worldgen.structure;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Climate;

import java.util.function.Consumer;

/**
 * Interface for structure placements that can be located and queried for their positions.
 * This allows for finding structure instances without generating the full chunk.
 */
public interface CLocatablePlacement {
    /**
     * Gets the unique identifier for the type of landmark this placement represents.
     * @return The landmark type ResourceLocation, or null if not a landmark.
     */
    ResourceLocation landmarkType();

    /**
     * Determines if a structure should be placed in the specified chunk.
     * This method should be deterministic based on the seed and coordinates.
     *
     * @param seed The world seed.
     * @param x The chunk X coordinate.
     * @param z The chunk Z coordinate.
     * @param sampler The climate sampler, used for noise-based conditions.
     * @return true if the structure should be placed in this chunk, false otherwise.
     */
    boolean isPlacementChunk(long seed, int x, int z, Climate.Sampler sampler);

    /**
     * Performs an action for each structure instance within a specified radius around a center chunk.
     *
     * @param seed The world seed.
     * @param centre The center chunk position.
     * @param radiusChunk The search radius in chunks.
     * @param sampler The climate sampler, used for noise-based conditions.
     * @param action The action to perform for each found ChunkPos.
     */
    void forEachInRadius(long seed, ChunkPos centre, int radiusChunk,
        Climate.Sampler sampler, Consumer<ChunkPos> action);
}
