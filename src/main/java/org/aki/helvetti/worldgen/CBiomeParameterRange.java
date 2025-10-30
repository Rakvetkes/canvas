package org.aki.helvetti.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.Climate;

/**
 * Represents a range condition for climate parameters
 * Used to match biomes based on sequential rule checking
 */
public record CBiomeParameterRange(
    float minTemperature,
    float maxTemperature,
    float minHumidity,
    float maxHumidity,
    float minContinentalness,
    float maxContinentalness,
    float minErosion,
    float maxErosion,
    float minWeirdness,
    float maxWeirdness,
    float minDepth,
    float maxDepth
) {
    public static final Codec<CBiomeParameterRange> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.FLOAT.optionalFieldOf("min_temperature", -2.0f).forGetter(CBiomeParameterRange::minTemperature),
            Codec.FLOAT.optionalFieldOf("max_temperature", 2.0f).forGetter(CBiomeParameterRange::maxTemperature),
            Codec.FLOAT.optionalFieldOf("min_humidity", -2.0f).forGetter(CBiomeParameterRange::minHumidity),
            Codec.FLOAT.optionalFieldOf("max_humidity", 2.0f).forGetter(CBiomeParameterRange::maxHumidity),
            Codec.FLOAT.optionalFieldOf("min_continentalness", -2.0f).forGetter(CBiomeParameterRange::minContinentalness),
            Codec.FLOAT.optionalFieldOf("max_continentalness", 2.0f).forGetter(CBiomeParameterRange::maxContinentalness),
            Codec.FLOAT.optionalFieldOf("min_erosion", -2.0f).forGetter(CBiomeParameterRange::minErosion),
            Codec.FLOAT.optionalFieldOf("max_erosion", 2.0f).forGetter(CBiomeParameterRange::maxErosion),
            Codec.FLOAT.optionalFieldOf("min_weirdness", -2.0f).forGetter(CBiomeParameterRange::minWeirdness),
            Codec.FLOAT.optionalFieldOf("max_weirdness", 2.0f).forGetter(CBiomeParameterRange::maxWeirdness),
            Codec.FLOAT.optionalFieldOf("min_depth", -2.0f).forGetter(CBiomeParameterRange::minDepth),
            Codec.FLOAT.optionalFieldOf("max_depth", 2.0f).forGetter(CBiomeParameterRange::maxDepth)
        ).apply(instance, CBiomeParameterRange::new)
    );

    /**
     * Check if the given climate target point matches this parameter range
     */
    public boolean matches(Climate.TargetPoint targetPoint) {
        return isInRange(targetPoint.temperature(), minTemperature, maxTemperature)
            && isInRange(targetPoint.humidity(), minHumidity, maxHumidity)
            && isInRange(targetPoint.continentalness(), minContinentalness, maxContinentalness)
            && isInRange(targetPoint.erosion(), minErosion, maxErosion)
            && isInRange(targetPoint.weirdness(), minWeirdness, maxWeirdness)
            && isInRange(targetPoint.depth(), minDepth, maxDepth);
    }

    private boolean isInRange(long value, float min, float max) {
        float normalized = Climate.unquantizeCoord(value);
        return normalized >= min && normalized <= max;
    }
}
