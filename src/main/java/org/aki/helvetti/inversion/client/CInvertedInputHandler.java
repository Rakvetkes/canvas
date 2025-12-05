package org.aki.helvetti.inversion.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import org.aki.helvetti.CCanvasMain;
import org.aki.helvetti.inversion.entity.CEntityInversionManager;

/**
 * Handles keyboard input inversion for players in inverted state.
 * 
 * When a player is in an inverted state:
 * - A/D keys are swapped (left/right strafe)
 * - Space/Shift keys are swapped (in flight mode)
 * 
 * Mouse input (up/down and left/right) is handled by MixinMouseHandler.
 */
@EventBusSubscriber(modid = CCanvasMain.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CInvertedInputHandler {

    /**
     * Inverts movement input (WASD and Space/Shift) when player is in inverted state.
     * This event fires after keyboard input is processed but before it's applied to player movement.
     */
    @SubscribeEvent
    static void onMovementInput(MovementInputUpdateEvent event) {
        Player playerEntity = event.getEntity();
        if (!(playerEntity instanceof LocalPlayer player)) {
            return;
        }

        // Check if player should be inverted
        if (!CEntityInversionManager.isLogicallyInverted(player)) {
            return;
        }

        // Get the input object
        var input = event.getInput();

        // Invert A/D (left/right strafe)
        float originalLeftImpulse = input.leftImpulse;
        boolean wasLeft = input.left;
        boolean wasRight = input.right;
        input.leftImpulse = -originalLeftImpulse;
        input.left = wasRight;
        input.right = wasLeft;
    }
}
