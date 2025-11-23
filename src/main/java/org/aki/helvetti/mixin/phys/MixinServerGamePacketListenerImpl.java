package org.aki.helvetti.mixin.phys;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import org.aki.helvetti.feature.CEntityInversionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

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
        return CEntityInversionManager.isLogicallyInverted(listener.getPlayer()) ? d7 < 0.0 : flag4;
    }

}
