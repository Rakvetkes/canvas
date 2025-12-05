package org.aki.helvetti.worldgen.structure.structures;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.aki.helvetti.CCanvasMain;

public final class CStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, CCanvasMain.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<CInvertedJigsawStructure>> INVERTED_JIGSAW = STRUCTURE_TYPES.register("inverted_jigsaw", () -> explicitStructureTypeCodec(CInvertedJigsawStructure.CODEC));

    private static <S extends Structure> StructureType<S> explicitStructureTypeCodec(MapCodec<S> codec) {
        return () -> codec;
    }

    public static void register(IEventBus eventBus) {
        STRUCTURE_TYPES.register(eventBus);
    }
}
