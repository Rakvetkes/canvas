package org.aki.helvetti.worldgen.tree.lelyetianmaple;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.phys.Vec3;
import org.aki.helvetti.util.InvertiblePos;
import org.aki.helvetti.worldgen.tree.CInvertableTrunkPlacer;
import org.aki.helvetti.worldgen.tree.CTreePlacers;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CLelyetianMapleTrunkPlacer extends CInvertableTrunkPlacer {
    public static final MapCodec<CLelyetianMapleTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
        trunkPlacerParts(instance).apply(instance, CLelyetianMapleTrunkPlacer::new)
    );

    public CLelyetianMapleTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected boolean isInverted() {
        return false;
    }

    @Override @Nonnull
    protected TrunkPlacerType<?> type() {
        return CTreePlacers.LELYETIAN_MAPLE_TRUNK_PLACER.get();
    }

    @Override
    protected List<FoliagePlacer.FoliageAttachment> placeTrunkInternal(
            @NotNull LevelSimulatedReader level,
            @NotNull BiConsumer<BlockPos, BlockState> blockSetter,
            @NotNull RandomSource random,
            int freeTreeHeight,
            @InvertiblePos @NotNull BlockPos pos,
            @NotNull TreeConfiguration config) {

        setDirtAt(level, blockSetter, random, pos.below().immutable(), config);
        int actualHeight = 0;
        while (actualHeight < freeTreeHeight && placeLog(level, blockSetter, random,
            pos.above(actualHeight).immutable(), config)) {
            actualHeight++;
        }

        ImmutableList.Builder<FoliagePlacer.FoliageAttachment> builder = ImmutableList.builder();
        Function<BlockPos, Boolean> logPlacer = pos1 -> placeLog(level, blockSetter, random, pos1, config);
        Function<BlockPos, FoliagePlacer.FoliageAttachment> foliagePlacer = pos1 ->
            new FoliagePlacer.FoliageAttachment(pos1, 0, false);

        int distToFoliage = config.foliagePlacer instanceof CLelyetianMapleFoliagePlacer ?
            Mth.floor(((CLelyetianMapleFoliagePlacer) config.foliagePlacer).sphereRadius()) : 1;

        int layerSpacing = 3;
        int layerCount = (actualHeight + layerSpacing - 2) / layerSpacing;
        int skippedLayerCount = layerCount / 2;
        int layerBranchDiff = 3;
        int foliageSpacing = 3;
        float rotation = 105.0f, rIncOffset = 30.0f;

        Vec3 branchDir = new Vec3(random.nextDouble() + 0.1, 0.0, random.nextDouble() + 0.1).normalize();
        branchDir = new Vec3(branchDir.x, 0.5, branchDir.z).normalize();

        for (int i = skippedLayerCount; i < layerCount; ++i) {
            for (int j = 0; j < 3; ++j) {
                int dY = randInt(random, i * layerSpacing, 2);
                BlockPos startingPos = pos.above(dY);
                int baseLen = getBaseBranchLength(actualHeight - i * layerSpacing, actualHeight);
                int currentBranchLen = randInt(random, baseLen, baseLen / 3);

                // System.out.println("Placing stem branch " + j + " on layer " + i);
                // Place a branch
                BranchPlacer branchPlacer = new StemBranchPlacer(branchDir, currentBranchLen,
                    foliageSpacing, distToFoliage);
                builder.addAll(branchPlacer.placeBranch(logPlacer, foliagePlacer, random, startingPos));

                // Rotate branch direction for next branch
                double angleRad = Math.toRadians(rotation + random.nextFloat() * rIncOffset);
                double cos = Math.cos(angleRad);
                double sin = Math.sin(angleRad);
                branchDir = new Vec3(
                    branchDir.x * cos - branchDir.z * sin, branchDir.y,
                    branchDir.x * sin + branchDir.z * cos
                );
            }
        }
        // Place central top branch
        BranchPlacer terminalPlacer = new TerminalBranchPlacer(0, 1, 0, distToFoliage);
        builder.addAll(terminalPlacer.placeBranch(logPlacer, foliagePlacer, random,
            pos.above(actualHeight - 1).offset(horizontalOffset(random))));

        return builder.build();
    }

    protected int getBaseBranchLength(int dY, int height) {
        float ratio = (float) dY / (float) height;
        return (int)((1.2f - (ratio - 1.0f) * (ratio - 1.0f)) * 0.5f * height);
    }

    public interface BranchPlacer {
        List<FoliagePlacer.FoliageAttachment> placeBranch(
            @NotNull Function<BlockPos, Boolean> logPlacer,
            @NotNull Function<BlockPos, FoliagePlacer.FoliageAttachment> foliagePlacer,
            @NotNull RandomSource random,
            @InvertiblePos @NotNull BlockPos startPos
        );
    }

    public static class TerminalBranchPlacer implements BranchPlacer {
        protected final Vec3i direction;
        protected final int distToFoliage;
        public TerminalBranchPlacer(Vec3i direction, int distToFoliage) {
            this.direction = direction;
            this.distToFoliage = distToFoliage;
        }
        public TerminalBranchPlacer(int x, int y, int z, int distToFoliage) {
            this(new Vec3i(x, y, z), distToFoliage);
        }

        @Override
        public List<FoliagePlacer.FoliageAttachment> placeBranch(
            @NotNull Function<BlockPos, Boolean> logPlacer,
            @NotNull Function<BlockPos, FoliagePlacer.FoliageAttachment> foliagePlacer,
            @NotNull RandomSource random,
            @InvertiblePos @NotNull BlockPos trunkPos) {
            BlockPos currentPos = trunkPos.offset(direction);
            if (!logPlacer.apply(currentPos)) return ImmutableList.of();
            currentPos = currentPos.offset(direction.multiply(distToFoliage));
            currentPos = currentPos.offset(extendToSide(random, direction));
            return ImmutableList.of(foliagePlacer.apply(currentPos.immutable()));
        }
    }

    public static class StemBranchPlacer implements BranchPlacer {
        protected final Vec3 direction;
        protected final Vec3 finalDirection;
        protected final int length;
        protected final int foliageSpacing;
        protected final int distToFoliage;

        public StemBranchPlacer(Vec3 direction, Vec3 finalDirection,
                                int length, int foliageSpacing, int distToFoliage) {
            this.direction = direction;
            this.finalDirection = finalDirection;
            this.length = length;
            this.foliageSpacing = foliageSpacing;
            this.distToFoliage = distToFoliage;
        }

        public StemBranchPlacer(Vec3 direction, int length, int foliageSpacing, int distToFoliage) {
            this(direction, new Vec3(direction.x, 0.0, direction.z).normalize(),
                length, foliageSpacing, distToFoliage);
        }

        @Override
        public List<FoliagePlacer.FoliageAttachment> placeBranch(
            @NotNull Function<BlockPos, Boolean> logPlacer,
            @NotNull Function<BlockPos, FoliagePlacer.FoliageAttachment> foliagePlacer,
            @NotNull RandomSource random,
            @InvertiblePos @NotNull BlockPos trunkPos) {
            ImmutableList.Builder<FoliagePlacer.FoliageAttachment> builder = ImmutableList.builder();

            Vec3 currentDirection = this.direction;
            int xSkip = 0, ySkip = 0, zSkip = 0;
            double skipChance = 0.5;
            BlockPos currentPos = trunkPos;

            for (int i = 1; i < length; ++i) {
                Vec3 effectiveDirection = new Vec3(
                    xSkip == 0 ? currentDirection.x : 0.0,
                    ySkip == 0 ? currentDirection.y : 0.0,
                    zSkip == 0 ? currentDirection.z : 0.0
                );
                Vec3i stepDirection = directionStep(random, effectiveDirection);
                currentPos = currentPos.offset(stepDirection);
                if (xSkip + ySkip + zSkip < 3 && i != length - 1
                    && random.nextDouble() < skipChance) {
                    if (stepDirection.getX() != 0) xSkip = 1;
                    if (stepDirection.getY() != 0) ySkip = 1;
                    if (stepDirection.getZ() != 0) zSkip = 1;
                    // System.out.println("branch part " + i + " skipped, direction " + stepDirection);
                } else {
                    if (!logPlacer.apply(currentPos.immutable())) break;
                    xSkip = ySkip = zSkip = 0;
                    // System.out.println("branch part " + i + " placed, direction " + stepDirection);
                }

                if ((length - i) % foliageSpacing == 0) {
                    Vec3i subBranchDirection = getSubBranchDirection(random, currentDirection);
                    BranchPlacer subBranchPlacer = new TerminalBranchPlacer(subBranchDirection, distToFoliage);
                    builder.addAll(subBranchPlacer.placeBranch(logPlacer, foliagePlacer, random, currentPos.immutable()));
                }
                currentDirection = curve(currentDirection);
            }

            BranchPlacer terminalPlacer = new TerminalBranchPlacer(
                directionStep(random, currentDirection), distToFoliage);
            builder.addAll(terminalPlacer.placeBranch(logPlacer, foliagePlacer, random, currentPos.immutable()));

            return builder.build();
        }

        protected Vec3i getSubBranchDirection(@NotNull RandomSource random, Vec3 direction) {
            int r = random.nextInt();
            if (Mth.abs(r % 2) == 0) {
                return directionStep(random, new Vec3(direction.z, 0.0, -direction.x).normalize());
            } else {
                return directionStep(random, new Vec3(-direction.z, 0.0, direction.x).normalize());
            }
        }

        protected Vec3 curve(Vec3 direction) {
            double curvingFactor = 0.1;
            return new Vec3(direction.x * (1.0 - curvingFactor) + finalDirection.x * curvingFactor,
                    direction.y * (1.0 - curvingFactor) + finalDirection.y * curvingFactor,
                    direction.z * (1.0 - curvingFactor) + finalDirection.z * curvingFactor).normalize();
        }

    }

    public static Vec3i locateAttachment(@NotNull RandomSource random, Vec3 direction, int steps, int lim) {
        Vec3i ret = new Vec3i(0, 0, 0);
        while (steps-- > 0) {
            ret = ret.offset(directionStep(random, new Vec3(
                Mth.abs(ret.getX()) < lim ? direction.x : 0.0,
                Mth.abs(ret.getY()) < lim ? direction.y : 0.0,
                Mth.abs(ret.getZ()) < lim ? direction.z : 0.0)));
        }
        return ret;
    }

    public static Vec3i directionStep(@NotNull RandomSource random, Vec3 direction) {
        double wx = direction.x * direction.x;
        double wy = direction.y * direction.y;
        double wz = direction.z * direction.z;
        double sum = wx + wy + wz;
        if (Math.abs(sum) < 1.0E-6) return Vec3i.ZERO;
        double r = random.nextDouble() * sum;
        if (r < wx) {
            return new Vec3i((int)Math.signum(direction.x), 0, 0);
        } else if (r < wx + wy) {
            return new Vec3i(0, (int)Math.signum(direction.y), 0);
        } else {
            return new Vec3i(0, 0, (int)Math.signum(direction.z));
        }
    }

    public static Vec3i extendToSide(@NotNull RandomSource random, Vec3i original) {
        return original.getX() == 0 ? new Vec3i(random.nextIntBetweenInclusive(-1, 1), 0, original.getZ())
            : new Vec3i(original.getX(), 0, random.nextIntBetweenInclusive(-1, 1));
    }

    public static Vec3i horizontalOffset(@NotNull RandomSource random) {
        int x = random.nextIntBetweenInclusive(-1, 1);
        int z = x == 0 ? (random.nextIntBetweenInclusive(-1, 1)) : 0;
        return new Vec3i(x, 0, z);
    }

    public static int randInt(@NotNull RandomSource random, int origin, int range) {
        return random.nextIntBetweenInclusive(origin, origin + range - 1);
    }

}
