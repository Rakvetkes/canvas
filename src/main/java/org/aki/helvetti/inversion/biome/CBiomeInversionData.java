package org.aki.helvetti.inversion.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public record CBiomeInversionData(Optional<ResourceKey<Biome>> normalBiome) {
    public static final Codec<CBiomeInversionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceKey.codec(Registries.BIOME).optionalFieldOf("normal_biome").forGetter(CBiomeInversionData::normalBiome)
    ).apply(instance, CBiomeInversionData::new));
    
    public static final CBiomeInversionData INDEPENDENT = new CBiomeInversionData(Optional.empty());
    
    public static CBiomeInversionData of(ResourceKey<Biome> normal) {
        return new CBiomeInversionData(Optional.of(normal));
    }
}
