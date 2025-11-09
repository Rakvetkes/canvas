package org.aki.helvetti;

import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Configuration class for Canvas of Helvetti mod
 * Contains all user-configurable options
 */
public class CConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ==================== General Settings ====================
    
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ALACY_COMMENT = BUILDER
            .comment("What you want Alacy to say on startup. (Does anyone really read these??)")
            .defineListAllowEmpty("alacyComment", List.of("I don't like your look.", "Nice to seeee youuu"),
                    () -> "", obj -> obj instanceof String);


    // ==================== Entity Settings ====================
    
    static {
        BUILDER.push("entity");
    }
    
    public static final ModConfigSpec.IntValue ENTITY_INVERSION_TRANSITION_TIME = BUILDER
            .comment(
                "Time (in ticks) required for an entity to transition between inverted and normal states",
                "Default: 100 ticks (5 seconds)",
                "Lower values = faster transitions but more jarring",
                "Higher values = smoother transitions but slower response"
            )
            .defineInRange("transitionTimeTicks", 100, 0, 1200);
    
    public static final ModConfigSpec.IntValue ENTITY_SYNC_INTERVAL = BUILDER
            .comment(
                "How often (in ticks) to sync entity inversion state to clients",
                "Default: 20 ticks (1 second)",
                "Lower values = more network traffic but smoother visual updates",
                "Higher values = less network traffic but potential visual delays"
            )
            .defineInRange("syncIntervalTicks", 20, 1, 100);
    

    // ==================== World Generation Settings ====================
    
    static {
        BUILDER.pop();
        
        BUILDER.push("world_generation");
    }

    public static final ModConfigSpec.DoubleValue EROSION_MULTIPLIER = BUILDER
            .comment(
                "Multiplier for erosion values in carving noise calculation",
                "Controls the influence of erosion on terrain flipping",
                "Default: 0.1",
                "Higher values = more erosion influence"
            )
            .defineInRange("erosionMultiplier", 0.1, 0.0, 1.0);
    
    public static final ModConfigSpec.DoubleValue CARVING_DEPTH_MULTIPLIER = BUILDER
            .comment(
                "Multiplier for calculating carving depth from noise values",
                "Controls how much terrain is carved based on noise",
                "Default: 2000.0",
                "Higher values = deeper carving"
            )
            .defineInRange("carvingDepthMultiplier", 2000.0, 0.0, 10000.0);
    
    static {
        BUILDER.pop();
    }
    
    // ==================== Build Config Spec ====================
    
    static final ModConfigSpec SPEC = BUILDER.build();
}
