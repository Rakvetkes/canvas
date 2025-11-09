package org.aki.helvetti.entity;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.aki.helvetti.CCanvasMain;
import java.util.Optional;

/**
 * Event handler for entity tick events
 * Updates entity inversion state every tick
 */
@EventBusSubscriber(modid = CCanvasMain.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CEntityHandler {
    
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        
        // Only update on server side - client receives state via sync packets
        if (!entity.level().isClientSide()) {
            CEntityInversionManager.updateInversionState(entity);
        }
    }
    
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        
        Optional<CEntityInversionData> existingData = entity.getExistingData(CEntityAttachments.ENTITY_INVERSION_DATA);
        if (existingData.isEmpty()) {
            CEntityInversionManager.setDefaultState(entity);
        } else if (!entity.level().isClientSide()) {
            CEntityInversionManager.syncOnJoin(entity);
        }
    }
}
