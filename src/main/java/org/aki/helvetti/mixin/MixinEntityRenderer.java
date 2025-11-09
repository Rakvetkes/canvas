package org.aki.helvetti.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.aki.helvetti.entity.CEntityInversionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to fix rendering issues for inverted entities
 */
@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {
    
    @Shadow @Final
    protected EntityRenderDispatcher entityRenderDispatcher;
    
    /**
     * Fix leash rendering for inverted entities
     * This allows the leash to render in normal coordinate space
     */
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
        if (CEntityInversionManager.shouldBeRenderedUpsideDown(entity)) {
            // Undo the Y-axis inversion
            poseStack.scale(1.0f, -1.0f, 1.0f);
        }
    }
    
    /**
     * Fix name tag rendering for inverted entities
     * This allows the name tag to render in normal coordinate space
     */
    @Inject(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void afterNameTagTranslate(Entity entity, net.minecraft.network.chat.Component displayName, 
                                      PoseStack poseStack, MultiBufferSource buffer, 
                                      int packedLight, float partialTick, CallbackInfo ci) {
        // Check if entity is inverted
        if (CEntityInversionManager.shouldBeRenderedUpsideDown(entity)) {
            // Undo the Y-axis inversion
            poseStack.scale(1.0f, -1.0f, 1.0f);
        }
    }
}
