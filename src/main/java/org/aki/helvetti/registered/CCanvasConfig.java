package org.aki.helvetti.registered;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * Configuration class for Canvas of Helvetti mod
 * Contains all user-configurable options
 */
public final class CCanvasConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ==================== General Settings ====================
    
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ALACY_COMMENT = BUILDER
            .comment("What you want Alacy to say on startup. (Does anyone really read these??)")
            .defineListAllowEmpty("alacyComment", List.of("i do not like your look", "nice to seeee youuu"),
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
            
    
    static {
        BUILDER.pop();
    }
    
    // ==================== Build Config Spec ====================
    
    public static final ModConfigSpec SPEC = BUILDER.build();
}
