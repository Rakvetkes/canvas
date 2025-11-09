package org.aki.helvetti.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.aki.helvetti.entity.CEntityInversionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin to modify LivingEntity jump behavior for inverted entities
 * - Inverts jump power for upside-down entities
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    
    /**
     * Modifies the jump power (f variable) in jumpFromGround() method
     * for inverted entities to make them jump downward instead of upward.
     * Injects before getDeltaMovement() call.
     */
    @ModifyVariable(
        method = "jumpFromGround",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;",
            ordinal = 0
        ),
        ordinal = 0
    )
    private float modifyJumpPower(float jumpPower) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return CEntityInversionManager.isEntityInverted(entity) ? -jumpPower : jumpPower;
    }
}
