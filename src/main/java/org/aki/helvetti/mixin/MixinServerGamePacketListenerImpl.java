package org.aki.helvetti.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.aki.helvetti.entity.CEntityInversionManager;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl {
    
    @ModifyVariable(
        method = "handleMovePlayer",
        at = @At(
            value = "STORE",
            ordinal = 0
        ),
        ordinal = 1
    )
    private boolean modifyInAirFlag(boolean flag4, @Local(ordinal = 7) double d7) {
        ServerGamePacketListenerImpl listener = (ServerGamePacketListenerImpl) (Object) this;
        return CEntityInversionManager.isEntityInverted(listener.getPlayer()) ? d7 < 0.0 : flag4;
    }

}
