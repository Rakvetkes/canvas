package org.aki.helvetti.mixin.world;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;

import org.aki.helvetti.worldgen.biomesources.CLelyetianBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to inject world seed into custom BiomeSources.
 */
@Mixin(ServerLevel.class)
public abstract class MixinServerLevel {

    /**
     * Injects the world seed into the BiomeSource if it extends CLelyetianBiomeSource.
     * This is required because BiomeSource does not natively support seed access,
     * but our custom biome source needs it for landmark placement logic.
     * 
     * Injection point: At the end of the constructor.
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectSeedToBiomeSource(CallbackInfo ci) {
        @SuppressWarnings("resource")
        ServerLevel level = (ServerLevel) (Object) this;
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        if (generator.getBiomeSource() instanceof CLelyetianBiomeSource lelyetianSource) {
            lelyetianSource.setSeed(level.getSeed());
        }
    }
}
