package org.aki.helvetti.mixin.render;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;

import org.aki.helvetti.client.CInversionManagerClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

// interesting issue #2!
@Mixin(ItemEntityRenderer.class)
public class MixinItemEntityRenderer {

    // A series of corrections to properly render inverted item entities
    // compensative translation
    @ModifyArgs(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
        )
    )
    private void modifyItemEntityTranslateArgs(Args args, @Local(ordinal = 0) ItemEntity entity) {
        if (CInversionManagerClient.isRenderedInvertedly(entity)) {
            args.set(1, -(float) args.get(1));
        }
    }
    
    // cancellation
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
            ordinal = 0,
            shift = At.Shift.BEFORE
        )
    )
    private void afterItemEntityTranslate(ItemEntity entity, float entityYaw,
        float partialTicks, PoseStack poseStack, MultiBufferSource buffer,
        int packedLight, CallbackInfo ci) {
        if (CInversionManagerClient.isRenderedInvertedly(entity)) {
            poseStack.scale(1.0f, -1.0f, 1.0f);
        }
    }

    // compensative rotation
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void afterItemEntityRotate(ItemEntity entity, float entityYaw,
        float partialTicks, PoseStack poseStack, MultiBufferSource buffer,
        int packedLight, CallbackInfo ci) {
        if (CInversionManagerClient.isViewInverted()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        }
    }

}
