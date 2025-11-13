package org.aki.helvetti.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.network.PacketDistributor;
import org.aki.helvetti.CConfig;
import org.aki.helvetti.network.CEntityInversionSyncPacket;
import org.aki.helvetti.worldgen.CLelyetiaBiomeSource;

import java.util.Optional;

/**
 * Manager class for entity inversion state
 * Handles the configurable timing logic and biome condition checking
 */
public final class CInversionManager {

    /* BOTH SERVER AND CLIENT-SIDE */

    /** Check if the entity should be inverted based on the biome at the given position */
    public static boolean shouldBeInverted(Level level, BlockPos blockPos) {
        // Just biome check
        Optional<ResourceKey<Biome>> biomeKey = level.getBiome(blockPos).unwrapKey();
        return biomeKey.map(CLelyetiaBiomeSource.INVERTED_BIOMES::contains)
            .orElse(false);
    }

    /** Check if the entity should be inverted based on its current position */
    public static boolean shouldBeInverted(Entity entity) {
        if (entity == null || entity.level() == null) {
            return false;
        }
        return shouldBeInverted(entity.level(), entity.blockPosition());
    }

    /** Logically inverted state check */
    public static boolean isLogicallyInverted(Entity entity) {
        return entity.getData(CEntityAttachments.ENTITY_INVERSION_DATA).isInverted();
    }

    /** initialize the data field */
    public static void initializeOnJoin(Entity entity) {
        Optional<CEntityInversionData> data = entity.getExistingData(CEntityAttachments.ENTITY_INVERSION_DATA);
        if (data.isEmpty()) {
            setDefaultState(entity);
        } else if (!entity.level().isClientSide()) {
            syncToClients(entity, data.get().isInverted(), data.get().getTransitionTicks());
        }
    }

    /** Set without the timer */
    public static void setDefaultState(Entity entity) {
        CEntityInversionData data = new CEntityInversionData(shouldBeInverted(entity), 0);
        entity.setData(CEntityAttachments.ENTITY_INVERSION_DATA, data);
    }


    /* SERVER-SIDE ONLY */

    /**
     * Update the entity's inversion state based on the condition
     * The state changes after the condition is continuously met/unmet for the configured transition time
     * 
     * transitionTicks logic:
     * - Increments when the condition differs from current state (accumulating transition progress)
     * - Resets to 0 when condition matches current state or when transition completes
     * - When threshold (requiredTicks from config) is reached, state flips to match the condition
     * 
     * @param entity The entity to update
     */
    public static void updateInversionState(Entity entity) {
        CEntityInversionData data = entity.getData(CEntityAttachments.ENTITY_INVERSION_DATA);
        boolean conditionMet = shouldBeInverted(entity);
        boolean isInverted = data.isInverted();
        int transitionTicks = data.getTransitionTicks();
        
        boolean shouldTransit = false;
        boolean dataChanged = false;
        
        if (conditionMet != isInverted) {
            // Condition differs from current state, accumulate transition progress
            transitionTicks++;
            dataChanged = true;
            if (transitionTicks >= data.getRequiredTicks()) {
                // Threshold reached, flip the state
                isInverted = conditionMet;
                transitionTicks = 0;
                shouldTransit = true;
            }
        } else if (transitionTicks != 0) {
            // Condition matches current state, reset any accumulated transition progress
            transitionTicks = 0;
            dataChanged = true;
        }
        
        // Create new data instance if anything changed (this triggers serialization)
        if (dataChanged || shouldTransit) {
            CEntityInversionData newData = new CEntityInversionData(isInverted, transitionTicks);
            entity.setData(CEntityAttachments.ENTITY_INVERSION_DATA, newData);
        }
        
        // Sync to client when state changes or periodically (configurable interval for smooth client updates)
        int syncInterval = CConfig.ENTITY_SYNC_INTERVAL.get();
        if (!entity.level().isClientSide() && (shouldTransit || entity.tickCount % syncInterval == 0)) {
            syncToClients(entity, isInverted, transitionTicks);
        }
    }
    
    /**
     * Send the entity's inversion data to all tracking clients
     * 
     * @param entity The entity whose data to sync
     * @param isInverted Whether the entity is inverted
     * @param transitionTicks The transition tick counter (non-negative)
     */
    private static void syncToClients(Entity entity, boolean isInverted, int transitionTicks) {
        CEntityInversionSyncPacket packet = new CEntityInversionSyncPacket(
            entity.getId(),
            isInverted,
            transitionTicks
        );
        
        // Special handling for players: they don't track themselves!
        if (entity instanceof ServerPlayer serverPlayer) {
            // Send to the player themselves
            PacketDistributor.sendToPlayer(serverPlayer, packet);
        }
        
        // Send to all other players tracking this entity
        PacketDistributor.sendToPlayersTrackingEntity(entity, packet);
    }
}
