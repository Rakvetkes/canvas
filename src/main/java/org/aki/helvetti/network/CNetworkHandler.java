package org.aki.helvetti.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.aki.helvetti.CCanvasMain;

/**
 * Network registration for custom packets
 */
@EventBusSubscriber(modid = CCanvasMain.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class CNetworkHandler {
    
    @SubscribeEvent
    static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(CCanvasMain.MODID)
            .versioned("1.0");
        
        // Register entity inversion sync packet (server -> client)
        registrar.playToClient(
            CEntityInversionSyncPacket.TYPE,
            CEntityInversionSyncPacket.STREAM_CODEC,
            CEntityInversionSyncPacket::handle
        );
    }
}
