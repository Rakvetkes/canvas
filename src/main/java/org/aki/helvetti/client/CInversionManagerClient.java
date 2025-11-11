package org.aki.helvetti.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import org.aki.helvetti.CCanvasTags;
import org.aki.helvetti.constants.CEntityConstants;
import org.aki.helvetti.entity.CInversionManager;

@OnlyIn(Dist.CLIENT)
public class CInversionManagerClient {
    
    public static boolean isViewInverted() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && CInversionManager.isLogicallyInverted(player);
    }

    /**
     * Check if the entity should be rendered invertedly
     * Uses a double blacklist filter: built-in blacklist + packet tag blacklist
     *
     * @param entity The entity to check
     * @return true if the entity should be rendered invertedly, false if it is in the blacklist
     */
    public static boolean isRenderedInvertedly(Entity entity) {
        if (entity == null) {
            return false;
        }

        EntityType<?> type = entity.getType();

        if (CEntityConstants.BUILTIN_NON_INVERTIBLE_TYPES.contains(type)
            || type.is(CCanvasTags.NON_INVERTIBLE_ENTITIES)) {
            return false;
        }

        return CInversionManager.isLogicallyInverted(entity);
    }

}
