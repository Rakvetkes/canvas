package org.aki.helvetti.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.Climate;

import java.util.List;

/**
 * Represents a range condition for climate parameters
 * Used to match biomes based on sequential rule checking
 * 
 * Each parameter is a list of ranges - a value matches if it falls within ANY of the ranges.
 */
public record CNoiseRange(
    List<Range> temperature,
    List<Range> humidity,
    List<Range> continentalness,
    List<Range> erosion,
    List<Range> weirdness,
    List<Range> depth
) {
    /**
     * Represents a single range [min, max]
     */
    public record Range(float min, float max) {
        public static final Codec<Range> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.FLOAT.fieldOf("min").forGetter(Range::min),
                Codec.FLOAT.fieldOf("max").forGetter(Range::max)
            ).apply(instance, Range::new)
        );
        
        /**
         * Default range that matches everything
         */
        public static final Range ALL = new Range(-2.0f, 2.0f);
        
        public boolean contains(float value) {
            return value >= min && value <= max;
        }
    }
    
    public static final Codec<CNoiseRange> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Range.CODEC.listOf().optionalFieldOf("temperature", List.of(Range.ALL)).forGetter(CNoiseRange::temperature),
            Range.CODEC.listOf().optionalFieldOf("humidity", List.of(Range.ALL)).forGetter(CNoiseRange::humidity),
            Range.CODEC.listOf().optionalFieldOf("continentalness", List.of(Range.ALL)).forGetter(CNoiseRange::continentalness),
            Range.CODEC.listOf().optionalFieldOf("erosion", List.of(Range.ALL)).forGetter(CNoiseRange::erosion),
            Range.CODEC.listOf().optionalFieldOf("weirdness", List.of(Range.ALL)).forGetter(CNoiseRange::weirdness),
            Range.CODEC.listOf().optionalFieldOf("depth", List.of(Range.ALL)).forGetter(CNoiseRange::depth)
        ).apply(instance, CNoiseRange::new)
    );

    /**
     * Check if the given climate target point matches this parameter range
     */
    public boolean matches(Climate.TargetPoint targetPoint) {
        return matchesAny(targetPoint.temperature(), temperature)
            && matchesAny(targetPoint.humidity(), humidity)
            && matchesAny(targetPoint.continentalness(), continentalness)
            && matchesAny(targetPoint.erosion(), erosion)
            && matchesAny(targetPoint.weirdness(), weirdness)
            && matchesAny(targetPoint.depth(), depth);
    }

    /**
     * Check if a value matches any of the ranges in the list
     */
    private boolean matchesAny(long value, List<Range> ranges) {
        float normalized = Climate.unquantizeCoord(value);
        return ranges.stream().anyMatch(range -> range.contains(normalized));
    }
}
