package org.aki.helvetti.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.aki.helvetti.client.CInversionManagerClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// interesting issue #2!
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer {

    // Pitch correction for inverted entities
    @WrapOperation(
        method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;lerp(FFF)F",
            ordinal = 0  // First Mth.lerp() call - calculates pitch
        )
    )
    private float wrapPitchLerp(float delta, float start, float end,
                                 Operation<Float> original,
                                 @Local(argsOnly = true) LivingEntity entity) {
        float pitch = original.call(delta, start, end);
        return CInversionManagerClient.isRenderedInversely(entity) ? -pitch : pitch;
    }

}
