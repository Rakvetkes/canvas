package org.aki.helvetti.constants;

import net.minecraft.world.entity.EntityType;
import java.util.Set;

/**
 * Entity-related constants
 */
public final class CEntityConstants {
    private CEntityConstants() {} // Prevent instantiation
    
    /**
     * Built-in blacklist of entity types that cannot be rendered inverted
     * These are technical entities without proper models or that would break rendering
     * 
     * Note: This is a hardcoded blacklist and cannot be configured
     * For user-configurable entity blacklisting, use the data/helvetti/tags/entity_types/non_invertible.json tag
     */
    public static final Set<EntityType<?>> BUILTIN_NON_INVERTIBLE_TYPES = Set.of(
        EntityType.MARKER,
        EntityType.ITEM_DISPLAY,
        EntityType.BLOCK_DISPLAY,
        EntityType.TEXT_DISPLAY
    );
    
    /**
     * Synchronization interval for entity inversion state (in ticks)
     * Entities sync their inversion state to clients every this many ticks
     * Lower values = more network traffic but smoother updates
     * Higher values = less network traffic but potential visual delays
     */
    public static final int SYNC_INTERVAL_TICKS = 20;
}
