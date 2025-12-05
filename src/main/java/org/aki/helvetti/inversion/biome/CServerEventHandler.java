package org.aki.helvetti.inversion.biome;

import org.aki.helvetti.CCanvasMain;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@EventBusSubscriber(modid = CCanvasMain.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CServerEventHandler {

    @SubscribeEvent
    static void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
            CCanvasMain.LOGGER.info("<Alacy> marking biomes updated");
            CBiomeInversionManager.onTagsUpdated(event.getRegistryAccess());
        }
    }

}
