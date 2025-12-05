package org.aki.helvetti.worldgen.structure.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

import javax.annotation.Nonnull;

import org.aki.helvetti.inversion.CBlockInversionManager;

import java.util.List;
import java.util.Optional;

public class CInvertedJigsawStructure extends Structure {
    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    // private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final List<PoolAliasBinding> poolAliases;
    private final DimensionPadding dimensionPadding;
    private final LiquidSettings liquidSettings;

    public static final MapCodec<CInvertedJigsawStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Structure.settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
            Codec.intRange(0, 7).fieldOf("size").forGetter(structure -> structure.maxDepth),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
            // Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
            Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(structure -> structure.poolAliases),
            DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING).forGetter(structure -> structure.dimensionPadding),
            LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings)
        ).apply(instance, CInvertedJigsawStructure::new)
    );

    public CInvertedJigsawStructure(
        Structure.StructureSettings settings,
        Holder<StructureTemplatePool> startPool,
        Optional<ResourceLocation> startJigsawName,
        int maxDepth,
        HeightProvider startHeight,
        boolean useExpansionHack,
        // Optional<Heightmap.Types> projectStartToHeightmap,
        int maxDistanceFromCenter,
        List<PoolAliasBinding> poolAliases,
        DimensionPadding dimensionPadding,
        LiquidSettings liquidSettings
    ) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        // this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.poolAliases = poolAliases;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
    }

    @Override
    @Nonnull
    public Optional<Structure.GenerationStub> findGenerationPoint(@Nonnull Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int x = chunkPos.getMinBlockX();
        int z = chunkPos.getMinBlockZ();
        int y = CBlockInversionManager.MIRROR_LEVEL - context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int heightAdjustment = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos blockPos = new BlockPos(x, y - heightAdjustment, z);
        return JigsawPlacement.addPieces(
            context,
            this.startPool,
            this.startJigsawName,
            this.maxDepth,
            blockPos,
            this.useExpansionHack,
            Optional.empty(),       // Forced use of the calculated Y
            this.maxDistanceFromCenter,
            PoolAliasLookup.create(this.poolAliases, blockPos, context.seed()),
            this.dimensionPadding,
            this.liquidSettings
        );
    }

    @Override
    public StructureType<?> type() {
        return CStructureTypes.INVERTED_JIGSAW.get();
    }
}
