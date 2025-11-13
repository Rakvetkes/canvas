package org.aki.helvetti.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.aki.helvetti.CCanvasMain;

/**
 * Event handler for entity tick events
 * Updates entity inversion state every tick
 */
@EventBusSubscriber(modid = CCanvasMain.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CEntityHandler {
    
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        
        // Only update on server side - client receives state via sync packets
        if (!entity.level().isClientSide()) {
            CInversionManager.updateInversionState(entity);
        }
    }
    
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        // Initialize inversion data on join
        CInversionManager.initializeOnJoin(entity);

        // When inverted, player will be pushed into ground if reentering game sneaking under a slab
        // This cannot completely solve the issue, still happening randomly
        // I gave up trying to fix it properly for now :)
        if (entity instanceof Player && CInversionManager.isLogicallyInverted(entity)) {
            entity.setPose(Pose.SWIMMING);
        }
    }

}
