package org.aki.helvetti;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class CCanvasTags {
    
    /**
     * Tag for entities that cannot be rendered inverted
     */
    public static final TagKey<EntityType<?>> NON_INVERTIBLE_ENTITIES = 
        TagKey.create(
            Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(CCanvasMain.MODID, "non_invertible")
        );
}
