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
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import org.aki.helvetti.inversion.CBlockInversionManager;
import org.aki.helvetti.inversion.biome.CBiomeDataMaps;
import org.aki.helvetti.inversion.entity.CEntityAttachments;
import org.aki.helvetti.registered.CCanvasBlocks;
import org.aki.helvetti.registered.CCanvasConfig;
import org.aki.helvetti.registered.CCanvasItems;
import org.aki.helvetti.worldgen.biomesources.CBiomeSources;
import org.aki.helvetti.worldgen.chunkgenerators.CChunkGenerators;
import org.aki.helvetti.worldgen.feature.placement.CPlacementModifiers;
import org.aki.helvetti.worldgen.feature.tree.CTreePlacers;
import org.aki.helvetti.worldgen.structure.placement.CStructurePlacementTypes;
import org.aki.helvetti.worldgen.structure.structures.CStructureTypes;
import org.aki.helvetti.worldgen.structure.formations.CFormationTypes;
import org.aki.helvetti.worldgen.structure.landmarks.CLandmarkTypes;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CCanvasMain.MODID)
public final class CCanvasMain {

    public static final String MODID = "helvetti";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.helvetti")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> CCanvasItems.FLIPPED_GRASS_BLOCK_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(CCanvasItems.FLIPPED_GRASS_BLOCK_ITEM.get());
                output.accept(CCanvasItems.LELYETIAN_BIRCH_LEAVES_ITEM.get());
                output.accept(CCanvasItems.GLOWING_LELYETIAN_BIRCH_LEAVES_ITEM.get());
                output.accept(CCanvasItems.LELYETIAN_MAPLE_LEAVES_ITEM.get());
            }).build());


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CCanvasMain(IEventBus modEventBus, ModContainer modContainer) {
        
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerDataMaps);

        // Registries I
        CCanvasBlocks.register(modEventBus);
        CCanvasItems.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // Worldgen Registration I
        CFormationTypes.register(modEventBus);
        CLandmarkTypes.register(modEventBus);
        CBiomeSources.register(modEventBus);
        CChunkGenerators.register(modEventBus);

        // Worldgen Registration II
        CTreePlacers.register(modEventBus);
        CPlacementModifiers.register(modEventBus);
        CStructurePlacementTypes.register(modEventBus);
        CStructureTypes.register(modEventBus);

        // Attachments Registration
        CEntityAttachments.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, CCanvasConfig.SPEC);

    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("<Alacy> setting you up");

        java.util.List<? extends String> comments = CCanvasConfig.ALACY_COMMENT.get();
        if (comments != null && !comments.isEmpty()) {
            String randomComment = comments.get(new java.util.Random().nextInt(comments.size()));
            LOGGER.info("<Alacy> {}", randomComment);
        }

        // Deferred Registry I
        CBlockInversionManager.registerFlippingList();

    }

    private void registerDataMaps(RegisterDataMapTypesEvent event) {
        // Register biome inversion data map
        CBiomeDataMaps.register(event);
    }

}
