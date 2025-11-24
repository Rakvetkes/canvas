package org.aki.helvetti;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import org.aki.helvetti.worldgen.CLelyetiaBiomeSource;
import org.aki.helvetti.worldgen.structure.placement.CLandmarkManager;
import org.aki.helvetti.feature.CBiomeInversionManager;

@EventBusSubscriber(modid = CCanvasMain.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CServerEventHandler {

    @SubscribeEvent
    static void onServerAboutToStart(ServerAboutToStartEvent event) {
        CLandmarkManager.initializeCache(event.getServer());
        CLelyetiaBiomeSource.initializeAllCaches();
    }

    @SubscribeEvent
    static void onServerStarting(ServerStartingEvent event) {
        CCanvasMain.LOGGER.info("<Alacy> server here");
    }

    @SubscribeEvent
    static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(CBiomeInversionManager.createLoader());
    }

    @SubscribeEvent
    static void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                CLandmarkManager.initializeCache(server);
                CLelyetiaBiomeSource.initializeAllCaches();
            }
        }
    }

}
