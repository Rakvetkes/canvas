package org.aki.helvetti.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.aki.helvetti.CCanvasMain;
import org.aki.helvetti.entity.CEntityAttachments;
import org.aki.helvetti.entity.CEntityInversionData;

import javax.annotation.Nonnull;

/**
 * Packet to sync entity inversion state from server to client
 */
public record CEntityInversionSyncPacket(int entityId, boolean isInverted, int transitionTicks) 
        implements CustomPacketPayload {
    
    public static final Type<CEntityInversionSyncPacket> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath(CCanvasMain.MODID, "entity_inversion_sync"));
    
    public static final StreamCodec<ByteBuf, CEntityInversionSyncPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        CEntityInversionSyncPacket::entityId,
        ByteBufCodecs.BOOL,
        CEntityInversionSyncPacket::isInverted,
        ByteBufCodecs.VAR_INT,
        CEntityInversionSyncPacket::transitionTicks,
        CEntityInversionSyncPacket::new
    );
    
    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    /**
     * Handle the packet on the client side
     */
    public static void handle(CEntityInversionSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                Entity entity = mc.level.getEntity(packet.entityId());
                if (entity != null) {                    
                    // Create new data instance with received values
                    CEntityInversionData newData = new CEntityInversionData(
                        packet.isInverted(), 
                        packet.transitionTicks()
                    );
                    entity.setData(CEntityAttachments.ENTITY_INVERSION_DATA, newData);
                }
            }
        });
    }
}
