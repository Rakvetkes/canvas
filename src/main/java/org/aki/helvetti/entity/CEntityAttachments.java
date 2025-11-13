package org.aki.helvetti.entity;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.aki.helvetti.CCanvasMain;

import java.util.function.Supplier;

/**
 * Registry for entity data attachments
 */
public final class CEntityAttachments {
    
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CCanvasMain.MODID);
    
    public static final Supplier<AttachmentType<CEntityInversionData>> ENTITY_INVERSION_DATA = 
        ATTACHMENT_TYPES.register("entity_inversion_data", 
            () -> AttachmentType.builder(CEntityInversionData::new)
                .serialize(CEntityInversionData.CODEC)
                .build()
        );
    
    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
