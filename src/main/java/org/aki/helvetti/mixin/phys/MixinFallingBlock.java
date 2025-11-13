package org.aki.helvetti.mixin.phys;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.FallingBlock;
import org.aki.helvetti.entity.CInversionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FallingBlock.class)
public abstract class MixinFallingBlock {
    
    // entitizing correction for inverted blocks
    // original condition:
    //   isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()
    // condition when inverted:
    //   isFree(level.getBlockState(pos.above())) && -pos.getY() >= -level.getMaxBuildHeight()
    // done by 1. replacement 2. negation 3. replacement
    
    // BlockPos.below()
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;below()Lnet/minecraft/core/BlockPos;",
            ordinal = 0
        )
    )
    private BlockPos cal1(BlockPos original, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) ServerLevel level) {
        return CInversionManager.shouldBeInverted(level, pos) ? pos.above() : original;
    }

    // BlockPos.getY()
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;getY()I",
            ordinal = 0
        )
    )
    private int cal2(int original, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) ServerLevel level) {
        return CInversionManager.shouldBeInverted(level, pos) ? -original : original;
    }

    // Level.getMinBuildHeight()
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getMinBuildHeight()I",
            ordinal = 0
        )
    )
    private int cal3(int original, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) ServerLevel level) {
        return CInversionManager.shouldBeInverted(level, pos) ? -level.getMaxBuildHeight() : original;
    }

}
