package org.aki.helvetti.worldgen.structure.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import javax.annotation.Nonnull;

import org.aki.helvetti.worldgen.structure.placement.formations.CFormation;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A structure placement that positions structures relative to another "anchor" structure placement.
 * The relative position is determined by a {@link CFormation}.
 * This allows for complex groupings of structures, such as villages surrounding a castle.
 * The anchor structure placement must implement {@link CLocatablePlacement} to allow for proper placement checks.
 */
public class CRelativePlacement extends StructurePlacement implements CLocatablePlacement {
    @SuppressWarnings("deprecation")
    public static final MapCodec<CRelativePlacement> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(CRelativePlacement::locateOffset),
            FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", FrequencyReductionMethod.DEFAULT).forGetter(CRelativePlacement::frequencyReductionMethod),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(CRelativePlacement::frequency),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("salt").forGetter(CRelativePlacement::salt),
            ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(CRelativePlacement::exclusionZone),
            CFormation.CODEC.fieldOf("formation").forGetter(CRelativePlacement::formation),
            StructurePlacement.CODEC.fieldOf("anchor").forGetter(CRelativePlacement::anchor),
            ResourceLocation.CODEC.optionalFieldOf("landmark_type").forGetter(CRelativePlacement::optionalLandmarkType)
        ).apply(instance, CRelativePlacement::new)
    );

    private final CFormation formation;
    private final StructurePlacement anchor;
    private final Optional<ResourceLocation> landmarkType;

    public CRelativePlacement(
            Vec3i locateOffset,
            FrequencyReductionMethod frequencyReductionMethod,
            float frequency,
            int salt,
            @SuppressWarnings("deprecation") Optional<ExclusionZone> exclusionZone,
            CFormation formation,
            StructurePlacement anchor,
            Optional<ResourceLocation> landmarkType) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
        this.formation = formation;
        this.anchor = anchor;
        this.landmarkType = landmarkType;
        if (!(anchor instanceof CLocatablePlacement)) {
            throw new IllegalArgumentException("Anchor structure placement must implement CLocatablePlacement");
        }
    }

    public CFormation formation() {
        return this.formation;
    }

    public StructurePlacement anchor() {
        return this.anchor;
    }

    public Optional<ResourceLocation> optionalLandmarkType() {
        return this.landmarkType;
    }

    @Override
    public ResourceLocation landmarkType() {
        return this.landmarkType.orElse(null);
    }

    @Override
    protected boolean isPlacementChunk(@Nonnull ChunkGeneratorStructureState state, int x, int z) {
        return this.isPlacementChunk(state.getLevelSeed(), x, z, state.randomState().sampler());
    }

    /**
     * Checks if a structure should be placed at the given chunk coordinates.
     * This is done by checking if there is a valid anchor structure at a position that would result
     * in this structure being placed at (x, z) according to the formation.
     *
     * @param seed The world seed.
     * @param x The chunk X coordinate.
     * @param z The chunk Z coordinate.
     * @param sampler The climate sampler.
     * @return true if this chunk is a valid placement location.
     */
    @Override
    public boolean isPlacementChunk(long seed, int x, int z, Climate.Sampler sampler) {
        if (anchor instanceof CLocatablePlacement landmarkAnchor) {
            List<Vec3i> offsets = this.formation.getChunkOffsets(seed);
            for (Vec3i offset : offsets) {
                int targetX = x - offset.getX();
                int targetZ = z - offset.getZ();
                if (landmarkAnchor.isPlacementChunk(seed, targetX, targetZ, sampler)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void forEachInRadius(long seed, ChunkPos centre, int radiusChunk, Climate.Sampler sampler, Consumer<ChunkPos> action) {
        if (anchor instanceof CLocatablePlacement landmarkAnchor) {
            int maxFormationDist = formation.getMaxChunkDist();
            List<Vec3i> offsets = formation.getChunkOffsets(seed);
            // Expand search radius to find anchors that might place a relative structure within the requested radius
            int searchRadius = radiusChunk + maxFormationDist;
            landmarkAnchor.forEachInRadius(seed, centre, searchRadius, sampler, (anchorPos) -> {
                for (Vec3i offset : offsets) {
                    int rx = anchorPos.x + offset.getX();
                    int rz = anchorPos.z + offset.getZ();
                    long dx = rx - centre.x;
                    long dz = rz - centre.z;
                    // Check if the found relative structure is within the requested radius from the center
                    if (dx * dx + dz * dz <= (long)radiusChunk * radiusChunk) {
                        action.accept(new ChunkPos(rx, rz));
                    }
                }
            });
        }
    }

    @Override
    @Nonnull
    public StructurePlacementType<?> type() {
        return CStructurePlacementTypes.RELATIVE.get();
    }
}
