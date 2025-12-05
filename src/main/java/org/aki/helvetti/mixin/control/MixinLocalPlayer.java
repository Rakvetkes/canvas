package org.aki.helvetti.mixin.control;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.client.player.LocalPlayer;

import org.aki.helvetti.inversion.entity.CEntityInversionManager;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {
    
    @ModifyArgs(
        method = "aiStep()V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;")
    )
    private void flyingControlCorrection(Args args) {
        LocalPlayer player = (LocalPlayer)(Object)this;
        
        // Check if player should be inverted
        if (CEntityInversionManager.isLogicallyInverted(player)) {
            double yDelta = args.get(1);
            args.set(1, -yDelta);
        }
    }

}
