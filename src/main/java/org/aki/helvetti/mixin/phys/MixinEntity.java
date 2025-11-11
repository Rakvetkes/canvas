package org.aki.helvetti.mixin.phys;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;

import org.aki.helvetti.entity.CInversionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;

/**
 * Mixin to modify Entity behavior based on the entity's inversion state
 * - Inverts gravity for upside-down entities
 * - Inverts vertical collision detection direction
 * - Modifies fall damage calculation for inverted entities
 * - Adjusts foot position detection for inverted entities
 * - Changes supporting block detection area for inverted entities
 */
@Mixin(Entity.class)
public abstract class MixinEntity {
    
    @Shadow
    protected boolean verticalCollision;
    
    @Shadow
    protected boolean verticalCollisionBelow;

    @Shadow
    private float eyeHeight;

    // Gravity inversion
    @ModifyReturnValue(method = "getGravity", at = @At("RETURN"))
    private double modifyGravity(double original) {
        Entity entity = (Entity) (Object) this;
        return CInversionManager.isLogicallyInverted(entity) ? -original : original;
    }
    

    // Vertical collision direction inversion
    @Inject(
        method = "move",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/Entity;verticalCollisionBelow:Z",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void modifyVerticalCollisionBelow(MoverType pMoverType, Vec3 pos, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        
        if (CInversionManager.isLogicallyInverted(entity)) {
            // For inverted entities, reassign verticalCollisionBelow based on inverted logic
            this.verticalCollisionBelow = this.verticalCollision && pos.y > 0.0;
        }
    }


    // inversion of Y movement for fall damage calculation
    @ModifyVariable(
        method = "checkFallDamage",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private double modifyCheckFallDamageY(double y) {
        Entity entity = (Entity) (Object) this;
        return CInversionManager.isLogicallyInverted(entity) ? -y : y;
    }
    

    // inversion of Y offset for foot position detection
    @ModifyVariable(
        method = "getOnPos(F)Lnet/minecraft/core/BlockPos;",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private float modifyGetOnPosYOffset(float yOffset) {
        Entity entity = (Entity) (Object) this;
        return CInversionManager.isLogicallyInverted(entity) ? -yOffset : yOffset;
    }
    

    // modify supporting block detection area for inverted entities
    @ModifyVariable(
        method = "checkSupportingBlock",
        at = @At(
            value = "STORE",
            ordinal = 0
        ),
        ordinal = 1,
        name = "aabb1"
    )
    private AABB modifyCheckSupportingBlockAABB(AABB aabb1, boolean onGround, Vec3 movement) {
        Entity entity = (Entity) (Object) this;

        if (!CInversionManager.isLogicallyInverted(entity) || !onGround) {
            return aabb1;
        }
        
        AABB originalBB = entity.getBoundingBox();
        return new AABB(
            aabb1.minX,
            originalBB.maxY,
            aabb1.minZ,
            aabb1.maxX,
            originalBB.maxY + 1.0E-6,
            aabb1.maxZ
        );
    }


    /** Adjust methods directly related to basic eye height fields for inverted entities
     *  For Minecraft 1.21 (Neoforge 21.0.167), a solution involves modifying:
     *  getEyeHeight(), getEyeHeight(Pose), getEyeY()
     */
    @ModifyReturnValue(method = "getEyeHeight()F", at = @At("RETURN"))
    private float modifyGetEyeHeight(float original) {
        Entity entity = (Entity) (Object) this;
        return CInversionManager.isLogicallyInverted(entity) ? entity.getBbHeight() - this.eyeHeight : original;
    }

    @ModifyReturnValue(method = "getEyeHeight(Lnet/minecraft/world/entity/Pose;)F", at = @At("RETURN"))
    private float modifyGetEyeHeightPose(float original, Pose pose) {
        Entity entity = (Entity) (Object) this;
        return CInversionManager.isLogicallyInverted(entity) ? entity.getBbHeight() - original : original;
    }

    @ModifyReturnValue(method = "getEyeY()D", at = @At("RETURN"))
    private double modifyGetEyeY(double original) {
        Entity entity = (Entity) (Object) this;
        return CInversionManager.isLogicallyInverted(entity) ? entity.getY() + entity.getBbHeight() - this.eyeHeight : original;
    }
    // TODO: Do I need to do anything about leashing points?
    // Logical layer... which means no shouldBeRenderedInverted() used here... I guess?

    
    // Passenger position correction when riding inverted entities
    @ModifyArgs(
        method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity$MoveFunction;accept(Lnet/minecraft/world/entity/Entity;DDD)V"
        )
    )
    private void modifyPositionRiderArgs(Args args) {
        Entity vehicle = (Entity) (Object) this;
        if (CInversionManager.isLogicallyInverted(vehicle)) {
            double deltaY = (double) args.get(2) - vehicle.getY();
            double baseY = vehicle.getY() - deltaY - ((Entity) args.get(0)).getBbHeight();
            args.set(2, baseY + vehicle.getBbHeight());
        }
    }
    
    @ModifyReturnValue(method = "getDismountLocationForPassenger", at = @At("RETURN"))
    private Vec3 modifyDismountLocationForPassenger(Vec3 original, LivingEntity passenger) {
        Entity entity = (Entity) (Object) this;
        if (CInversionManager.isLogicallyInverted(entity)) {
            double newY = entity.getBoundingBox().minY - passenger.getBbHeight();
            return new Vec3(original.x, newY, original.z);
        }
        return original;
    }
    // TODO: Check nearby ground positions for real vehicles... look at those overrides. :)
}
