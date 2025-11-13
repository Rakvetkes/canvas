package org.aki.helvetti.mixin.render;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.aki.helvetti.client.CInversionManagerClient;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to inject into LevelRenderer.renderLevel() to apply a 180-degree rotation around the z-axis
 * to the frustumMatrix parameter, causing the entire world to be rendered upside down.
 * Only applies when the local player is in an inverted state.
 * 
 *  -we have interesting issues! see MixinEntityRenderDispatcher for details- :)
 * 
 */
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {
    
    /**
     * Inject at the HEAD of renderLevel() to modify the frustumMatrix parameter.
     * Applies a 180-degree rotation around the z-axis by left-multiplying the frustumMatrix
     * only when the local player (Minecraft.getInstance().player) is inverted.
     * 
     * Left multiplication (R × frustumMatrix) rotates in world space.
     * Right multiplication (frustumMatrix × R) would rotate in camera space.
     */
    @Inject(
        method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
        at = @At("HEAD")
    )
    private void onRenderLevelHead(
        DeltaTracker deltaTracker,
        boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightTexture lightTexture,
        Matrix4f frustumMatrix,
        Matrix4f projectionMatrix,
        CallbackInfo ci
    ) {        
        // Check if player exists and is inverted
        if (CInversionManagerClient.isViewInverted()) {
            // Create rotation matrix for 180 degrees around Z-axis
            Matrix4f rotation = new Matrix4f().rotateZ((float) Math.PI);
            
            // Left-multiply: rotation × frustumMatrix (rotates in world space)
            // This transforms the entire world coordinate system
            frustumMatrix.mulLocal(rotation);
        }
    }
}
