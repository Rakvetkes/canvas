package org.aki.helvetti.constants;

/**
 * Non-configurable world generation constants
 * These values are tightly coupled to the world generation logic
 * and should not be changed without thorough testing
 */
public final class CWorldGenConstants {
    private CWorldGenConstants() {} // Prevent instantiation
    
    /**
     * The Y-level used as the mirror point for terrain inversion
     * This is a structural constant that affects world structure
     */
    public static final int MIRROR_LEVEL = 200;
    
    /**
     * Multiplier for erosion values in carving noise calculation
     * Used to control the influence of erosion on terrain flipping
     */
    public static final double EROSION_MULTIPLIER = 0.1;
    
    /**
     * Multiplier for calculating carving depth from noise values
     * Controls how much terrain is carved based on noise
     */
    public static final double CARVING_DEPTH_MULTIPLIER = 2000.0;
    
    /**
     * Continentalness ranges that trigger terrain flipping
     * Each range is [min, max] inclusive
     * These ranges define where inverted terrain appears
     */
    public static final double[][] CONTINENTALNESS_RANGES = {
        {-0.895, -0.805}, {-0.695, -0.605}, {-0.495, -0.405},
        {-0.295, -0.205}, {0.105, 0.195}, {0.305, 0.395}, {0.505, 0.595}
    };
}
