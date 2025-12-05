package org.aki.helvetti.registered;

import org.aki.helvetti.CCanvasMain;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public final class CCanvasTags {
    
    /**
     * Tag for entities that cannot be rendered inverted
     */
    public static final TagKey<EntityType<?>> NOT_RENDERED_INVERSELY = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(CCanvasMain.MODID, "not_rendered_inversely"));
            
    public static final TagKey<Block> INVERTED_TERRAIN = TagKey.create(Registries.BLOCK,
        ResourceLocation.fromNamespaceAndPath(CCanvasMain.MODID, "inverted_terrain"));
        
}
