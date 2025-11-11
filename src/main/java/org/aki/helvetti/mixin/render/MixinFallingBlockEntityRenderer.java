package org.aki.helvetti.mixin.render;

import org.aki.helvetti.client.CInversionManagerClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.item.FallingBlockEntity;

import net.minecraft.client.renderer.entity.FallingBlockRenderer;

// interesting issue #4!
@Mixin(FallingBlockRenderer.class)
public abstract class MixinFallingBlockEntityRenderer {
    
    // just cancellation
    // inject after PoseStack.translate()
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void afterTranslate(
        FallingBlockEntity entity,
        float entityYaw,
        float partialTicks,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        CallbackInfo ci
    ) {
        if (CInversionManagerClient.isRenderedInvertedly(entity)) {
            poseStack.translate(0.0, entity.getBbHeight(), 0.0);
            poseStack.scale(1.0f, -1.0f, 1.0f);
        }
    }

}
