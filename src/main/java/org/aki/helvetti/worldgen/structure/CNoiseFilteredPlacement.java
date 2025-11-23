package org.aki.helvetti.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.biome.Climate;

import org.aki.helvetti.worldgen.CNoiseRange;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A structure placement that extends {@link RandomSpreadStructurePlacement} to include noise-based filtering.
 * This allows structures to be placed only in specific noise regions, effectively acting as "landmarks".
 */
public class CNoiseFilteredPlacement extends RandomSpreadStructurePlacement implements CLocatablePlacement {
    @SuppressWarnings("deprecation")
    public static final MapCodec<CNoiseFilteredPlacement> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(CNoiseFilteredPlacement::locateOffset),
            FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", FrequencyReductionMethod.DEFAULT).forGetter(CNoiseFilteredPlacement::frequencyReductionMethod),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(CNoiseFilteredPlacement::frequency),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("salt").forGetter(CNoiseFilteredPlacement::salt),
            ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(CNoiseFilteredPlacement::exclusionZone),
            Codec.intRange(0, 4096).fieldOf("spacing").forGetter(CNoiseFilteredPlacement::spacing),
            Codec.intRange(0, 4096).fieldOf("separation").forGetter(CNoiseFilteredPlacement::separation),
            RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(CNoiseFilteredPlacement::spreadType),
            CNoiseRange.CODEC.optionalFieldOf("noise_selector").forGetter(CNoiseFilteredPlacement::noiseSelector),
            ResourceLocation.CODEC.optionalFieldOf("landmark_type").forGetter(CNoiseFilteredPlacement::optionalLandmarkType)
        ).apply(instance, CNoiseFilteredPlacement::new)
    );

    private final Optional<CNoiseRange> noiseSelector;
    private final Optional<ResourceLocation> landmarkType;

    public CNoiseFilteredPlacement(
            Vec3i locateOffset,
            FrequencyReductionMethod frequencyReductionMethod,
            float frequency,
            int salt,
            @SuppressWarnings("deprecation") Optional<ExclusionZone> exclusionZone,
            int spacing,
            int separation,
            RandomSpreadType spreadType,
            Optional<CNoiseRange> noiseSelector,
            Optional<ResourceLocation> landmarkType) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone, spacing, separation, spreadType);
        this.noiseSelector = noiseSelector;
        this.landmarkType = landmarkType;
    }

    /**
     * Gets the noise selector for this placement.
     */
    public Optional<CNoiseRange> noiseSelector() {
        return this.noiseSelector;
    }

    public Optional<ResourceLocation> optionalLandmarkType() {
        return this.landmarkType;
    }

    /**
     * Gets the landmark info for this placement.
     */
    @Override
    public ResourceLocation landmarkType() {
        return this.landmarkType.orElse(null);
    }

    /**
     * Checks if a structure should be placed at the given chunk coordinates.
     * This implementation combines the standard random spread check with an additional noise-based check.
     *
     * @param seed The world seed.
     * @param x The chunk X coordinate.
     * @param z The chunk Z coordinate.
     * @param sampler The climate sampler for noise values.
     * @return true if the chunk is a valid placement location.
     */
    @Override
    public boolean isPlacementChunk(long seed, int x, int z, Climate.Sampler sampler) {
        ChunkPos potential = this.getPotentialStructureChunk(seed, x, z);
        return potential.x == x && potential.z == z && noiseCheck(seed, x, z, sampler);
    }

    protected boolean noiseCheck(long seed, int x, int z, Climate.Sampler sampler) {
        return noiseSelector.map((noiseSelector) -> {
            ChunkPos chunkPos = new ChunkPos(x, z);
            // Sample at the center of the chunk at y=0
            int blockX = chunkPos.getMinBlockX() + 8;
            int blockZ = chunkPos.getMinBlockZ() + 8;
            return noiseSelector.matches(sampler.sample(blockX >> 2, 0, blockZ >> 2));
        }).orElse(true);
    }

    @Override
    public void forEachInRadius(long seed, ChunkPos centre, int radiusChunk, Climate.Sampler sampler, Consumer<ChunkPos> action) {
        int spacing = this.spacing();
        
        // Calculate the grid range that covers the search radius
        int minGridX = Math.floorDiv(centre.x - radiusChunk, spacing);
        int maxGridX = Math.floorDiv(centre.x + radiusChunk, spacing);
        int minGridZ = Math.floorDiv(centre.z - radiusChunk, spacing);
        int maxGridZ = Math.floorDiv(centre.z + radiusChunk, spacing);

        for (int gridX = minGridX; gridX <= maxGridX; gridX++) {
            for (int gridZ = minGridZ; gridZ <= maxGridZ; gridZ++) {
                ChunkPos candidate = this.getPotentialStructureChunk(seed, gridX * spacing, gridZ * spacing);
                if (this.noiseCheck(seed, candidate.x, candidate.z, sampler)) {
                    long dx = centre.x - candidate.x;
                    long dz = centre.z - candidate.z;
                    // Check if the candidate is within the circular radius
                    if (dx * dx + dz * dz <= (long)radiusChunk * radiusChunk) {
                        action.accept(candidate);
                    }
                }
            }
        }
    }

    /**
     * Override to add noise-based filtering for structure placement candidates.
     * Only generates candidates if their noise values match the noise_selector criteria.
     */
    @Override
    protected boolean isPlacementChunk(@Nonnull ChunkGeneratorStructureState state, int x, int z) {
        return this.isPlacementChunk(state.getLevelSeed(), x, z, state.randomState().sampler());
    }

    @Override
    @Nonnull
    public StructurePlacementType<?> type() {
        return CStructurePlacementTypes.NOISE_FILTERED.get();
    }
}
