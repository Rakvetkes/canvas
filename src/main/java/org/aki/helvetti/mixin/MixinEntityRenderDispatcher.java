package org.aki.helvetti.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.aki.helvetti.entity.CEntityInversionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to flip entity rendering when inverted
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
            if (CEntityInversionManager.shouldBeRenderedUpsideDown(entity)) {
                // Flip the entity by scaling Y by -1
                poseStack.scale(1.0f, -1.0f, 1.0f);
                
                // Adjust vertical position so entity stays at same ground level
                poseStack.translate(0.0, -entity.getBbHeight(), 0.0);
            }
        } catch (Exception e) {
            // Log error but don't break rendering
            // The outer try-catch in EntityRenderDispatcher will handle this
            throw new RuntimeException("Failed to apply inversion rendering for " + entity.getType(), e);
        }
    }
}
