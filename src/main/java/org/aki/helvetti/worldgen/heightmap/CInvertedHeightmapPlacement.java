package org.aki.helvetti.worldgen.heightmap;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * Custom placement modifier for inverted biomes.
 * Instead of finding the top solid block (like normal heightmap),
 * this finds the first solid block from y=0 upwards.
 * This is used to place trees on the underside of floating islands.
 * Only places on natural terrain blocks (not structures) to avoid placing inside buildings.
 */
public class CInvertedHeightmapPlacement extends PlacementModifier {
    public static final MapCodec<CInvertedHeightmapPlacement> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Heightmap.Types.CODEC.fieldOf("heightmap").forGetter(p -> p.heightmap)
        ).apply(instance, CInvertedHeightmapPlacement::new)
    );

    private static final TagKey<Block> INVERTED_TERRAIN = 
        TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("helvetti", "inverted_terrain"));

    private final Heightmap.Types heightmap;

    public CInvertedHeightmapPlacement(Heightmap.Types heightmap) {
        this.heightmap = heightmap;
    }

    @Override
    public Stream<BlockPos> getPositions(@Nonnull PlacementContext context, @Nonnull RandomSource random, @Nonnull BlockPos pos) {
        WorldGenLevel level = context.getLevel();
        int x = pos.getX();
        int z = pos.getZ();
        
        // Start from y=0 and search upwards for the first solid block
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        
        // Search from y=0 upwards (or from minBuildHeight if 0 is below it)
        int startY = Math.max(0, minY);
        
        for (int y = startY; y < maxY; y++) {
            BlockPos checkPos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(checkPos);
            
            // Check if this is a natural terrain block (not a structure block)
            // This prevents trees from spawning inside/under buildings
            if (this.heightmap.isOpaque().test(state) && isNaturalTerrain(state)) {
                // Found the first natural terrain block, place feature below it
                return Stream.of(checkPos.below());
            }
        }

        // If no solid block found, return empty stream (no placement)
        return Stream.empty();
    }

    /**
     * Check if a block is natural terrain suitable for tree placement.
     * This excludes man-made blocks like planks, bricks, etc.
     * 
     * @param state The block state to check
     * @return true if this is natural terrain, false otherwise
     */
    private boolean isNaturalTerrain(BlockState state) {
        return state.is(INVERTED_TERRAIN);
    }

    @Override
    public PlacementModifierType<?> type() {
        return CPlacementModifiers.INVERTED_HEIGHTMAP.get();
    }
}
