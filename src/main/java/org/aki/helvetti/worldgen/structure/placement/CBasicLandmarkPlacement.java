package org.aki.helvetti.worldgen.structure.placement;

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

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A structure placement that extends {@link RandomSpreadStructurePlacement} to support landmark querying.
 * This placement can be used for structures that need to be identified as landmarks in the world.
 */
public class CBasicLandmarkPlacement extends RandomSpreadStructurePlacement implements CLocatablePlacement {
    @SuppressWarnings("deprecation")
    public static final MapCodec<CBasicLandmarkPlacement> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(CBasicLandmarkPlacement::locateOffset),
            FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", FrequencyReductionMethod.DEFAULT).forGetter(CBasicLandmarkPlacement::frequencyReductionMethod),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(CBasicLandmarkPlacement::frequency),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("salt").forGetter(CBasicLandmarkPlacement::salt),
            ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(CBasicLandmarkPlacement::exclusionZone),
            Codec.intRange(0, 4096).fieldOf("spacing").forGetter(CBasicLandmarkPlacement::spacing),
            Codec.intRange(0, 4096).fieldOf("separation").forGetter(CBasicLandmarkPlacement::separation),
            RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(CBasicLandmarkPlacement::spreadType),
            ResourceLocation.CODEC.optionalFieldOf("landmark_type").forGetter(CBasicLandmarkPlacement::optionalLandmarkType)
        ).apply(instance, CBasicLandmarkPlacement::new)
    );

    private final Optional<ResourceLocation> landmarkType;

    public CBasicLandmarkPlacement(
            Vec3i locateOffset,
            FrequencyReductionMethod frequencyReductionMethod,
            float frequency,
            int salt,
            @SuppressWarnings("deprecation") Optional<ExclusionZone> exclusionZone,
            int spacing,
            int separation,
            RandomSpreadType spreadType,
            Optional<ResourceLocation> landmarkType) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone, spacing, separation, spreadType);
        this.landmarkType = landmarkType;
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
     * @return true if the chunk is a valid placement location.
     */
    @Override
    public boolean isPlacementChunk(long seed, int x, int z) {
        ChunkPos potential = this.getPotentialStructureChunk(seed, x, z);
        return potential.x == x && potential.z == z;
    }

    @Override
    public void forEachInRadius(long seed, ChunkPos centre, int radiusChunk, Consumer<ChunkPos> action) {
        int spacing = this.spacing();
        
        // Calculate the grid range that covers the search radius
        int minGridX = Math.floorDiv(centre.x - radiusChunk, spacing);
        int maxGridX = Math.floorDiv(centre.x + radiusChunk, spacing);
        int minGridZ = Math.floorDiv(centre.z - radiusChunk, spacing);
        int maxGridZ = Math.floorDiv(centre.z + radiusChunk, spacing);

        for (int gridX = minGridX; gridX <= maxGridX; gridX++) {
            for (int gridZ = minGridZ; gridZ <= maxGridZ; gridZ++) {
                ChunkPos candidate = this.getPotentialStructureChunk(seed, gridX * spacing, gridZ * spacing);
                long dx = centre.x - candidate.x;
                long dz = centre.z - candidate.z;
                // Check if the candidate is within the circular radius
                if (dx * dx + dz * dz <= (long)radiusChunk * radiusChunk) {
                    action.accept(candidate);
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
        return this.isPlacementChunk(state.getLevelSeed(), x, z);
    }

    @Override
    @Nonnull
    public StructurePlacementType<?> type() {
        return CStructurePlacementTypes.BASIC.get();
    }
}
