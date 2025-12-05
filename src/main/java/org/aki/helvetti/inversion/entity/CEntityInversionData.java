package org.aki.helvetti.inversion.entity;

import org.aki.helvetti.registered.CCanvasConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Data class to store entity inversion state
 * This includes the isInverted flag and a single transition tick counter
 */
public class CEntityInversionData {
    private final boolean isInverted;
    private final int transitionTicks;  // Positive: ticks condition has been met; Negative: ticks condition has been unmet

    // Codec for serialization
    public static final Codec<CEntityInversionData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.BOOL.fieldOf("isInverted").forGetter(CEntityInversionData::isInverted),
            Codec.INT.fieldOf("transitionTicks").forGetter(CEntityInversionData::getTransitionTicks)
        ).apply(instance, CEntityInversionData::new)
    );
    
    // Default constructor for attachment creation
    public CEntityInversionData() {
        this(false, 0);
    }
    
    // Constructor for codec and general use
    public CEntityInversionData(boolean isInverted, int transitionTicks) {
        this.isInverted = isInverted;
        this.transitionTicks = transitionTicks;
    }
    
    public boolean isInverted() {
        return isInverted;
    }
    
    public int getTransitionTicks() {
        return transitionTicks;
    }
    
    /**
     * Get the required ticks for inversion transition from config
     * @return Number of ticks required for transition (default: 100)
     */
    public int getRequiredTicks() {
        return CCanvasConfig.ENTITY_INVERSION_TRANSITION_TIME.get();
    }
}
