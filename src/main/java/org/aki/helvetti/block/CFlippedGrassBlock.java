package org.aki.helvetti.block;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A flipped grass block that spreads like normal grass and changes color based on biome.
 * This block behaves identically to vanilla grass but uses custom textures.
 */
public class CFlippedGrassBlock extends SpreadingSnowyDirtBlock {
    
    public static final MapCodec<CFlippedGrassBlock> CODEC = simpleCodec(CFlippedGrassBlock::new);
    
    public CFlippedGrassBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected MapCodec<? extends SpreadingSnowyDirtBlock> codec() {
        return CODEC;
    }

    /**
     * Set initial state when placed, checking if there's snow below.
     */
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockState belowState = context.getLevel().getBlockState(context.getClickedPos().below());
        return this.defaultBlockState().setValue(SNOWY, isSnowySetting(belowState));
    }

    /**
     * Update snowy state when neighboring blocks change.
     * Check below instead of above for flipped behavior.
     */
    @Override
    public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction facing, 
                                   @Nonnull BlockState facingState, @Nonnull LevelAccessor level, 
                                   @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        if (facing == Direction.DOWN) {
            return state.setValue(SNOWY, isSnowySetting(facingState));
        }
        // Don't call super to avoid vanilla grass behavior checking above for snow
        return state;
    }

    /**
     * Check if this grass block can stay alive.
     * This flipped grass block degrades to dirt only when compressed from below.
     */
    private static boolean canBeGrass(BlockState state, LevelReader levelReader, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = levelReader.getBlockState(belowPos);
        // Degrade to dirt if there's a solid block below compressing it
        return belowState.isAir() || !belowState.isSolidRender(levelReader, belowPos);
    }

    private static boolean isSnowySetting(BlockState state) {
        return false;   // Do we need flipped snow blocks?
    }

    /**
     * Check if dirt can become flipped grass.
     * Flipped grass needs space below (not compressed from below).
     */
    private static boolean canPropagate(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        return canBeGrass(state, level, pos) && !level.getFluidState(belowPos).is(net.minecraft.tags.FluidTags.WATER);
    }

    /**
     * Random tick for flipped grass spreading and death mechanics.
     * Flipped grass spreads downward instead of upward.
     */
    @Override
    public void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        if (!canBeGrass(state, level, pos)) {
            // Turn back to dirt if compressed from below
            level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
        } else {
            // Try to spread to nearby dirt blocks (preferring downward spread)
            if (level.getMaxLocalRawBrightness(pos.below()) >= 9) {
                BlockState grassState = this.defaultBlockState();

                for (int i = 0; i < 4; i++) {
                    BlockPos targetPos = pos.offset(
                        random.nextInt(3) - 1,
                        random.nextInt(5) - 3,  // This creates a downward bias (-3 to +1)
                        random.nextInt(3) - 1
                    );

                    BlockState targetState = level.getBlockState(targetPos);
                    if (targetState.is(Blocks.DIRT) && canPropagate(grassState, level, targetPos)) {
                        level.setBlockAndUpdate(
                            targetPos,
                            grassState.setValue(SNOWY, isSnowySetting(level.getBlockState(targetPos.below())))
                        );
                    }
                }
            }
        }
    }
}
