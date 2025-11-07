package org.aki.helvetti.worldgen.tree.lelyetianbirch;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.aki.helvetti.worldgen.tree.CInvertableTrunkPlacer;
import org.aki.helvetti.worldgen.tree.CTreePlacers;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A trunk placer for tall Lelyetian Birch trees.
 * These trees are 3-4 times taller than normal birch trees (15-28 blocks).
 */
public class CLelyetianBirchTrunkPlacer extends CInvertableTrunkPlacer {
    public static final MapCodec<CLelyetianBirchTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
        trunkPlacerParts(instance).apply(instance, CLelyetianBirchTrunkPlacer::new)
    );

    public CLelyetianBirchTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    @Nonnull
    protected TrunkPlacerType<?> type() {
        return CTreePlacers.LELYETIAN_BIRCH_TRUNK_PLACER.get();
    }

    @Override
    protected boolean isInverted() {
        return false;
    }

    @Override
    @Nonnull
    protected List<FoliagePlacer.FoliageAttachment> placeTrunkInternal(
            @Nonnull LevelSimulatedReader level,
            @Nonnull BiConsumer<BlockPos, BlockState> blockSetter,
            @Nonnull RandomSource random,
            int freeTreeHeight,
            @Nonnull BlockPos pos,
            @Nonnull TreeConfiguration config) {

        // Set dirt below the tree
        setDirtAt(level, blockSetter, random, pos.below(), config);

        int actualHeight = 0;

        // Place trunk blocks
        while (actualHeight < freeTreeHeight && placeLog(level, blockSetter, random, pos.above(actualHeight), config)) {
            actualHeight++;
        }

        // Return foliage attachment points at multiple heights
        ImmutableList.Builder<FoliagePlacer.FoliageAttachment> builder = ImmutableList.builder();
        
        // Calculate how many foliage layers to place
        int layerSpacing = 4; // Average spacing between foliage layers
        int numLayers = Math.max(1, actualHeight / layerSpacing);
        int discardedLayers = numLayers / 3;
        
        // Start from the top and work downwards
        for (int i = 0; i <= numLayers - 1 - discardedLayers; i++) {
            int layerHeight = (int)(actualHeight * (1.0 - (double)i / numLayers));
            
            // Calculate probability: (i/numLayers)^6 - (i/numLayers) + 1
            double ratio = (double)i / numLayers;
            double probability = Math.pow(ratio, 6) - ratio + 1.0;
            int radiusModifier = (random.nextDouble() < probability) ? -1 : 0;
            
            builder.add(new FoliagePlacer.FoliageAttachment(
                pos.above(layerHeight),
                radiusModifier,
                false // doubleTrunk
            ));
        }
        
        return builder.build();
    }
}
