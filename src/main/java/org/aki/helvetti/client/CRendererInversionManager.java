package org.aki.helvetti.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.aki.helvetti.CCanvasTags;
import org.aki.helvetti.feature.CEntityInversionManager;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
public final class CRendererInversionManager {

    public static final Set<EntityType<?>> BUILTIN_NON_INVERTIBLE_TYPES = Set.of(
            EntityType.MARKER,
            EntityType.ITEM_DISPLAY,
            EntityType.BLOCK_DISPLAY,
            EntityType.TEXT_DISPLAY
    );

    /** Check if the local player's view is inverted */
    public static boolean isViewInverted() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && CEntityInversionManager.isLogicallyInverted(player);
    }

    /** Apply a rotation around the facing axis defined by a Y rotation */
    public static void facialSpaghetti(PoseStack poseStack, float yRot) {
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-yRot));
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(yRot));
    }

    /** Rotate a target position 180 degrees around an axis defined by a Y rotation from an origin point */
    public static Vec3 facialSpaghetti(Vec3 origin, Vec3 target, float axisYRot) {
        // Convert the Y rotation to radians and calculate the axis direction
        double yaw = Math.toRadians(axisYRot);

        // The rotation axis points in the direction defined by axisYRot on the horizontal plane
        // For Y rotation, the forward direction is (-sin(yaw), 0, cos(yaw))
        Vec3 axis = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));

        // Get the vector from origin to target
        Vec3 relativePos = target.subtract(origin);

        // Rotate the relative position 180 degrees around the axis using Rodrigues' rotation formula
        // For 180 degrees, the formula simplifies: v' = 2(axis · v)axis - v
        double dotProduct = axis.dot(relativePos);
        Vec3 rotated = axis.scale(2.0 * dotProduct).subtract(relativePos);

        // Return the rotated position in world space
        return origin.add(rotated);
    }

    /** Check if the given entity should be rendered in an inverted manner */
    public static boolean isRenderedInversely(Entity entity) {
        if (entity == null) {
            return false;
        }

        EntityType<?> type = entity.getType();

        if (BUILTIN_NON_INVERTIBLE_TYPES.contains(type)
            || type.is(CCanvasTags.NOT_RENDERED_INVERSELY)) {
            return false;
        }

        return CEntityInversionManager.isLogicallyInverted(entity);
    }

}
