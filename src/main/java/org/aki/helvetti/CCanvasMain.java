package org.aki.helvetti;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.aki.helvetti.block.CBlocks;
import org.aki.helvetti.entity.CEntityAttachments;
import org.aki.helvetti.feature.CBlockInversionManager;
import org.aki.helvetti.item.CItems;
import org.aki.helvetti.worldgen.CBiomeSources;
import org.aki.helvetti.worldgen.CChunkGenerators;
import org.aki.helvetti.worldgen.feature.placement.CPlacementModifiers;
import org.aki.helvetti.worldgen.feature.tree.CTreePlacers;
import org.aki.helvetti.worldgen.structure.placement.CStructurePlacementTypes;
import org.aki.helvetti.worldgen.structure.placement_2.CStructureTypes;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CCanvasMain.MODID)
public final class CCanvasMain {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "helvetti";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "helvetti" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.helvetti")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> CItems.FLIPPED_GRASS_BLOCK_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(CItems.FLIPPED_GRASS_BLOCK_ITEM.get());
                output.accept(CItems.LELYETIAN_BIRCH_LEAVES_ITEM.get());
                output.accept(CItems.GLOWING_LELYETIAN_BIRCH_LEAVES_ITEM.get());
                output.accept(CItems.LELYETIAN_MAPLE_LEAVES_ITEM.get());
            }).build());


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CCanvasMain(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        CBlocks.BLOCKS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so items get registered
        CItems.ITEMS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Initialize block inversion flipping list
        modEventBus.addListener(CBlockInversionManager::registerFlippingList);
        
        // Register custom biome sources
        CBiomeSources.register(modEventBus);
        
        // Register custom chunk generators
        CChunkGenerators.register(modEventBus);
        
        // Register custom tree placers
        CTreePlacers.register(modEventBus);
        
        // Register custom placement modifiers
        CPlacementModifiers.register(modEventBus);
        
        // Register custom structure placement types
        CStructurePlacementTypes.register(modEventBus);

        // Register custom structure types
        CStructureTypes.register(modEventBus);
        
        // Register entity attachments
        CEntityAttachments.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, CConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("<Alacy> setting you up");

        java.util.List<? extends String> comments = CConfig.ALACY_COMMENT.get();
        if (comments != null && !comments.isEmpty()) {
            String randomComment = comments.get(new java.util.Random().nextInt(comments.size()));
            LOGGER.info("<Alacy> {}", randomComment);
        }
    }


}
