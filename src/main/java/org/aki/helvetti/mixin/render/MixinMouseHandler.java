package org.aki.helvetti.mixin.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;

import org.aki.helvetti.entity.CInversionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Mixin to invert mouse input when the player is in an inverted state.
 * This handles both mouse X (left/right) and mouse Y (up/down) movement.
 * 
 * Injection point: Right before player.turn() is called, after tutorial.onMouse().
 * This ensures the tutorial system receives correct input while the player's view is inverted.
 */
@Mixin(MouseHandler.class)
public abstract class MixinMouseHandler {

    @Shadow @Final private Minecraft minecraft;

    /**
     * Inverts both mouse X and Y deltas when player is inverted.
     * This is injected right before the player.turn() call in turnPlayer().
     * 
     * @param args Arguments passed to player.turn(double, double)
     *             args.get(0) = mouse X delta (left/right)
     *             args.get(1) = mouse Y delta (up/down) with invertY already applied
     */
    @ModifyArgs(
        method = "turnPlayer(D)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
        )
    )
    private void invertMouseInput(Args args) {
        LocalPlayer player = this.minecraft.player;
        if (player != null && CInversionManager.isLogicallyInverted(player)) {
            // Invert both X (left/right) and Y (up/down) movement
            args.set(0, -(double)args.get(0));
            args.set(1, -(double)args.get(1));
        }
    }
}
