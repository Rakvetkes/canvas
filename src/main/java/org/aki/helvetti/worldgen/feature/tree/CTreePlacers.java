package org.aki.helvetti.worldgen.feature.tree;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.aki.helvetti.CCanvasMain;
import org.aki.helvetti.worldgen.feature.featuresize.CTwoLayersFeatureSizePlaceholder;
import org.aki.helvetti.worldgen.feature.tree.lelyetianbirch.CInvertedLelyetianBirchFoliagePlacer;
import org.aki.helvetti.worldgen.feature.tree.lelyetianbirch.CInvertedLelyetianBirchTrunkPlacer;
import org.aki.helvetti.worldgen.feature.tree.lelyetianbirch.CLelyetianBirchFoliagePlacer;
import org.aki.helvetti.worldgen.feature.tree.lelyetianbirch.CLelyetianBirchTrunkPlacer;
import org.aki.helvetti.worldgen.feature.tree.lelyetianmaple.CInvertedLelyetianMapleTrunkPlacer;
import org.aki.helvetti.worldgen.feature.tree.lelyetianmaple.CLelyetianMapleFoliagePlacer;
import org.aki.helvetti.worldgen.feature.tree.lelyetianmaple.CLelyetianMapleTrunkPlacer;

/**
 * Registry for custom tree placers (trunk placers, foliage placers, and feature sizes)
 */
public final class CTreePlacers {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, CCanvasMain.MODID);
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS = DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, CCanvasMain.MODID);
    public static final DeferredRegister<FeatureSizeType<?>> FEATURE_SIZES = DeferredRegister.create(Registries.FEATURE_SIZE_TYPE, CCanvasMain.MODID);


    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<CLelyetianBirchTrunkPlacer>> LELYETIAN_BIRCH_TRUNK_PLACER =
        TRUNK_PLACERS.register("lelyetian_birch_trunk_placer", () -> new TrunkPlacerType<>(CLelyetianBirchTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<CInvertedLelyetianBirchTrunkPlacer>> INVERTED_LELYETIAN_BIRCH_TRUNK_PLACER =
        TRUNK_PLACERS.register("inverted_lelyetian_birch_trunk_placer", () -> new TrunkPlacerType<>(CInvertedLelyetianBirchTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<CLelyetianMapleTrunkPlacer>> LELYETIAN_MAPLE_TRUNK_PLACER =
        TRUNK_PLACERS.register("lelyetian_maple_trunk_placer", () -> new TrunkPlacerType<>(CLelyetianMapleTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<CInvertedLelyetianMapleTrunkPlacer>> INVERTED_LELYETIAN_MAPLE_TRUNK_PLACER =
        TRUNK_PLACERS.register("inverted_lelyetian_maple_trunk_placer", () -> new TrunkPlacerType<>(CInvertedLelyetianMapleTrunkPlacer.CODEC));


    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<CLelyetianBirchFoliagePlacer>> LELYETIAN_BIRCH_FOLIAGE_PLACER =
        FOLIAGE_PLACERS.register("lelyetian_birch_foliage_placer", () -> new FoliagePlacerType<>(CLelyetianBirchFoliagePlacer.CODEC));
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<CInvertedLelyetianBirchFoliagePlacer>> INVERTED_LELYETIAN_BIRCH_FOLIAGE_PLACER =
        FOLIAGE_PLACERS.register("inverted_lelyetian_birch_foliage_placer", () -> new FoliagePlacerType<>(CInvertedLelyetianBirchFoliagePlacer.CODEC));
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<CLelyetianMapleFoliagePlacer>> LELYETIAN_MAPLE_FOLIAGE_PLACER =
        FOLIAGE_PLACERS.register("lelyetian_maple_foliage_placer", () -> new FoliagePlacerType<>(CLelyetianMapleFoliagePlacer.CODEC));


    public static final DeferredHolder<FeatureSizeType<?>, FeatureSizeType<CTwoLayersFeatureSizePlaceholder>> TWO_LAYERS_FEATURE_SIZE_PLACEHOLDER =
        FEATURE_SIZES.register("two_layers_feature_size_placeholder",
            () -> new FeatureSizeType<>(CTwoLayersFeatureSizePlaceholder.CODEC));


    public static void register(IEventBus modEventBus) {
        TRUNK_PLACERS.register(modEventBus);
        FOLIAGE_PLACERS.register(modEventBus);
        FEATURE_SIZES.register(modEventBus);
    }
}
