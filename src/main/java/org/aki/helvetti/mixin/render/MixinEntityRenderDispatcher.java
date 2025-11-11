package org.aki.helvetti.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;

import org.aki.helvetti.client.CInversionManagerClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to flip entity rendering when inverted
 * 
 * fixes currently applied (required jointly with LevelRender mixins):
 * 1. entity name tags & leashes
 * 2. item entities
 * 3. head direction
 * 4. falling blocks
 *
 *  -considering to do this in another way- :)
 *
 */
@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {
    
    @Inject(
        method = "render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
        )
    )
    private void beforeEntityRender(Entity entity, double camX, double camY, double camZ,
                                   float yRot, float partialTick,
                                   PoseStack poseStack, MultiBufferSource bufferSource,
                                   int packedLight, CallbackInfo ci) {
        try {
            // Check if entity is inverted and passes type checks
            if (CInversionManagerClient.isRenderedInvertedly(entity)) {
                poseStack.scale(1.0f, -1.0f, 1.0f);
                poseStack.translate(0.0, -entity.getBbHeight(), 0.0);
            }
        } catch (Exception e) {
            // Log error but don't break rendering
            // The outer try-catch in EntityRenderDispatcher will handle this
            throw new RuntimeException("Failed to apply inversion rendering for " + entity.getType(), e);
        }
    }
}
