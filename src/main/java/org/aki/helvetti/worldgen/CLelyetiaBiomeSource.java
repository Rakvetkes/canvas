package org.aki.helvetti.worldgen;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

/**
 * Custom BiomeSource for the Lelyetia dimension
 * Provides a multi-noise based biome distribution system
 */
public class CLelyetiaBiomeSource extends BiomeSource {
    public static final MapCodec<CLelyetiaBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.list(
                RecordCodecBuilder.<Pair<Climate.ParameterPoint, Holder<Biome>>>create(pairInstance ->
                    pairInstance.group(
                        Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst),
                        Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)
                    ).apply(pairInstance, Pair::of)
                )
            ).fieldOf("biomes").forGetter(source -> source.parameters.values())
        ).apply(instance, CLelyetiaBiomeSource::fromList)
    );

    private final Climate.ParameterList<Holder<Biome>> parameters;

    private CLelyetiaBiomeSource(Climate.ParameterList<Holder<Biome>> parameters) {
        this.parameters = parameters;
    }

    private static CLelyetiaBiomeSource fromList(List<Pair<Climate.ParameterPoint, Holder<Biome>>> list) {
        return new CLelyetiaBiomeSource(new Climate.ParameterList<>(list));
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.parameters.values().stream().map(Pair::getSecond);
    }

    @Override
    @Nonnull
    public Holder<Biome> getNoiseBiome(int x, int y, int z, @Nonnull Climate.Sampler sampler) {
        return this.parameters.findValue(sampler.sample(x, y, z));
    }
}
