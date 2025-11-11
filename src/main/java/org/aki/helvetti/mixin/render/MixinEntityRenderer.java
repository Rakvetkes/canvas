package org.aki.helvetti.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import org.aki.helvetti.client.CInversionManagerClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// interesting issue #1!
@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {
    
    @Shadow @Final
    protected EntityRenderDispatcher entityRenderDispatcher;
    
    // interesting issue fix 1: leash
    // cancellation
    @Inject(
        method = "renderLeash(Lnet/minecraft/world/entity/Entity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void afterLeashTranslate(T entity, float partialTick, PoseStack poseStack,
                                    MultiBufferSource buffer, Entity leashHolder,
                                    CallbackInfo ci) {
        // Check if entity is inverted
        if (CInversionManagerClient.isRenderedInvertedly(entity)) {
            // Undo the Y-axis inversion
            poseStack.scale(1.0f, -1.0f, 1.0f);
        }
    }
    
    // interesting issue fix 2: name tag
    // cancellation
    @Inject(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void afterNameTagTranslate(Entity entity, Component displayName, 
                                      PoseStack poseStack, MultiBufferSource buffer, 
                                      int packedLight, float partialTick, CallbackInfo ci) {
        if (CInversionManagerClient.isRenderedInvertedly(entity)) {
            poseStack.scale(1.0f, -1.0f, 1.0f);
        }
    }

    // compensative rotation
    @Inject(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void afterNameTagRotate(Entity entity, Component displayName, 
                                    PoseStack poseStack, MultiBufferSource buffer, 
                                    int packedLight, float partialTick, CallbackInfo ci) {
        if (CInversionManagerClient.isViewInverted()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        }
    }

}
