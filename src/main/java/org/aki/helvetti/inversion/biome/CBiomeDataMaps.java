package org.aki.helvetti.inversion.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import org.aki.helvetti.CCanvasMain;

public final class CBiomeDataMaps {
    public static final DataMapType<Biome, CBiomeInversionData> INVERSION_DATA = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(CCanvasMain.MODID, "biome_inversion"),
            Registries.BIOME,
            CBiomeInversionData.CODEC
        ).synced(CBiomeInversionData.CODEC, true)
        .build();

    public static void register(RegisterDataMapTypesEvent event) {
        event.register(INVERSION_DATA);
    }
}
