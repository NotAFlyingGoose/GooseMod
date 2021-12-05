package com.notaflyingoose.goosemod.blocks;

import com.notaflyingoose.goosemod.world.ModWorldGeneration;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import javax.annotation.Nullable;
import java.util.Random;

public class MapleTreeGrower extends AbstractTreeGrower {
    @Nullable
    protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random p_60038_, boolean p_60039_) {
        int i = p_60038_.nextInt(3);
        if (i == 0) {
            return ModWorldGeneration.Features.ORANGE_MAPLE;
        } else if (i == 1) {
            return ModWorldGeneration.Features.RED_MAPLE;
        } else {
            return ModWorldGeneration.Features.YELLOW_MAPLE;
        }
    }
}
