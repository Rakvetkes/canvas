package org.aki.helvetti.mixin.phys;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.aki.helvetti.entity.CInversionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


@Mixin(Player.class)
public abstract class MixinPlayer {

    // back off from edge adjustment for inverted players
    @ModifyArgs(
        method = "canFallAtLeast",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;<init>(DDDDDD)V")
    )
    private void modifyCanFallAtLeastAABBArgs(Args args, @Local(ordinal = 0) AABB aabb) {
        Player player = (Player) (Object) this;
        if (CInversionManager.isLogicallyInverted(player)) {
            args.set(1, aabb.maxY);
            args.set(4, aabb.maxY + 1.0E-5F);
        }
    }

    // alternated potential y-movement test for inverted players
    @ModifyExpressionValue(
        method = "maybeBackOffFromEdge",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/phys/Vec3;y:D", ordinal = 0)
    )
    private double modifyMaybeBackOffFromEdgeYValue(double original) {
        Player player = (Player) (Object) this;
        return CInversionManager.isLogicallyInverted(player) ? -original : original;
    }


    // pose change fitting test for inverted players
    @ModifyArgs(
        method = "canPlayerFitWithinBlocksAndEntitiesWhen",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityDimensions;makeBoundingBox(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;", ordinal = 0)
    )
    private void modifyCanPlayerFitWithinBlocksAndEntitiesWhenArgs(Args args, @Local(ordinal = 0, argsOnly = true) Pose pose) {
        Player player = (Player) (Object) this;
        if (CInversionManager.isLogicallyInverted(player)) {
            args.set(0, ((Vec3) args.get(0))
                .add(0.0, player.getBbHeight(), 0.0)
                .subtract(0.0, player.getDimensions(pose).height(), 0.0));
        }
    }

}
