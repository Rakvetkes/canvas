package org.aki.helvetti;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.aki.helvetti.block.CBlocks;
import org.aki.helvetti.entity.CEntityAttachments;
import org.aki.helvetti.item.CItems;
import org.aki.helvetti.worldgen.CBiomeSources;
import org.aki.helvetti.worldgen.CChunkGenerators;
import org.aki.helvetti.worldgen.CLelyetiaBiomeSource;
import org.aki.helvetti.worldgen.placement.CPlacementModifiers;
import org.aki.helvetti.worldgen.tree.CTreePlacers;
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

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("helvetti", () -> CreativeModeTab.builder()
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
        
        // Register custom biome sources
        CBiomeSources.register(modEventBus);
        
        // Register custom chunk generators
        CChunkGenerators.register(modEventBus);
        
        // Register custom tree placers
        CTreePlacers.register(modEventBus);
        
        // Register custom placement modifiers
        CPlacementModifiers.register(modEventBus);
        
        // Register entity attachments
        CEntityAttachments.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, CConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("Canvas of Helvetti initializing.");
        
        // Register inverted biomes
        event.enqueueWork(() -> {
            CLelyetiaBiomeSource.INVERTED_BIOMES.add(ResourceKey.create(Registries.BIOME, 
                ResourceLocation.fromNamespaceAndPath(MODID, "inverted_golden_woods")));
            CLelyetiaBiomeSource.INVERTED_BIOMES.add(ResourceKey.create(Registries.BIOME, 
                ResourceLocation.fromNamespaceAndPath(MODID, "inverted_sunlit_grove")));
            CLelyetiaBiomeSource.INVERTED_BIOMES.add(ResourceKey.create(Registries.BIOME, 
                ResourceLocation.fromNamespaceAndPath(MODID, "inverted_crimson_thicket")));
            CLelyetiaBiomeSource.INVERTED_BIOMES.add(ResourceKey.create(Registries.BIOME, 
                ResourceLocation.fromNamespaceAndPath(MODID, "inverted_vermilion_woods")));
            CLelyetiaBiomeSource.INVERTED_BIOMES.add(ResourceKey.create(Registries.BIOME, 
                ResourceLocation.fromNamespaceAndPath(MODID, "inverted_basilica")));
        });

        java.util.List<? extends String> comments = CConfig.ALACY_COMMENT.get();
        if (comments != null && !comments.isEmpty()) {
            String randomComment = comments.get(new java.util.Random().nextInt(comments.size()));
            LOGGER.info("<Alacy> {}", randomComment);
        }
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            // event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("Canvas of Helvetti server starting.");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = CCanvasMain.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("Canvas of Helvetti initializing on client.");
            LOGGER.info("<Alacy> We're currently surfing as {}", Minecraft.getInstance().getUser().getName());
        }
        
        @SubscribeEvent
        static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
            // Register biome-based color for flipped grass block
            // The block uses tintindex 0 for the grass-colored parts
            event.register((state, level, pos, tintIndex) -> {
                // Return grass color based on biome
                if (level != null && pos != null) {
                    return BiomeColors.getAverageGrassColor(level, pos);
                }
                return GrassColor.getDefaultColor();
            }, CBlocks.FLIPPED_GRASS_BLOCK.get());

            // Register biome-based color for leaf blocks
            event.register((state, level, pos, tintIndex) -> {
                if (level != null && pos != null) {
                    return BiomeColors.getAverageFoliageColor(level, pos);
                }
                return FoliageColor.getDefaultColor();
            }, CBlocks.LELYETIAN_BIRCH_LEAVES.get(),
               CBlocks.GLOWING_LELYETIAN_BIRCH_LEAVES.get(),
               CBlocks.LELYETIAN_MAPLE_LEAVES.get());
        }
        
        @SubscribeEvent
        static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            // Register biome-based color for flipped grass block item
            // Uses the default grass color for items
            event.register((stack, tintIndex) -> GrassColor.getDefaultColor()
                , CItems.FLIPPED_GRASS_BLOCK_ITEM.get());

            // Register foliage color for leaf items
            event.register((stack, tintIndex) -> FoliageColor.getDefaultColor()
                , CItems.LELYETIAN_BIRCH_LEAVES_ITEM.get(),
                  CItems.GLOWING_LELYETIAN_BIRCH_LEAVES_ITEM.get(),
                  CItems.LELYETIAN_MAPLE_LEAVES_ITEM.get());
        }
    }
}
