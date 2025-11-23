package org.aki.helvetti.mixin.phys;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;

import org.aki.helvetti.feature.CEntityInversionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FallingBlockEntity.class)
public abstract class MixinFallingBlockEntity {
    
    // unentitizing correction for inverted blocks
    // injection point: FallingBlockEntity#tick() ground check
    
    // BlockPos.below()
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;below()Lnet/minecraft/core/BlockPos;",
            ordinal = 0
        )
    )
    private BlockPos cal1(BlockPos original, @Local(ordinal = 0) BlockPos pos) {
        Entity self = (Entity) (Object) this;
        return CEntityInversionManager.isLogicallyInverted(self) ? pos.above() : original;
    }

}
