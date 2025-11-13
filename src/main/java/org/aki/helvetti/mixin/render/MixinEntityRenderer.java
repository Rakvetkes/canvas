package org.aki.helvetti.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
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
        method = "renderLeash",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void afterPushPose(T entity, float partialTick, PoseStack poseStack,
                                    MultiBufferSource buffer, Entity leashHolder,
                                    CallbackInfo ci) {
        if (CInversionManagerClient.isRenderedInvertedly(entity)) {
            poseStack.translate(0.0f, entity.getBbHeight(), 0.0f);
            CInversionManagerClient.facialSpaghetti(poseStack, entity.getYRot());
        }
    }

    // holding position correction
    @ModifyExpressionValue(
        method = "renderLeash",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getRopeHoldPosition(F)Lnet/minecraft/world/phys/Vec3;",
            ordinal = 0
        )
    )
    private Vec3 modifyLeashHoldPosition(Vec3 original, T entity, float partialTick,
                                         PoseStack poseStack, MultiBufferSource buffer,
                                         Entity leashHolder) {
        if (CInversionManagerClient.isRenderedInvertedly(entity)) {
            Vec3 origin = leashHolder.getPosition(partialTick)
                .add(0.0, leashHolder.getBbHeight() / 2.0, 0.0);
            return CInversionManagerClient.facialSpaghetti(origin, original, leashHolder.getYRot());
        }
        return original;
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
            CInversionManagerClient.facialSpaghetti(poseStack, entity.getYRot());
        }
    }

    // compensation rotation
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
