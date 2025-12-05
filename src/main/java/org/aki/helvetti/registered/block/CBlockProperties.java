package org.aki.helvetti.registered.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class CBlockProperties {

    public static BlockBehaviour.Properties grassBlockProperties() {
        return BlockBehaviour.Properties.of()
            .mapColor(MapColor.GRASS)
            .randomTicks()
            .strength(0.6F)
            .sound(SoundType.GRASS);
    }

    public static BlockBehaviour.Properties leavesProperties() {
        return BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .strength(0.2F)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn(Blocks::ocelotOrParrot)
            .isSuffocating((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> false)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .isRedstoneConductor((state, level, pos) -> false);
    }

    public static BlockBehaviour.Properties glowingLeavesProperties() {
        return leavesProperties().lightLevel((state) -> 15);
    }

}
