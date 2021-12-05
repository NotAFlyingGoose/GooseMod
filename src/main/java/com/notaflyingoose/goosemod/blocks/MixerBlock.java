package com.notaflyingoose.goosemod.blocks;

import com.notaflyingoose.goosemod.world.ModStats;
import com.notaflyingoose.goosemod.gui.MixerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MixerBlock extends Block {
    private static final Component CONTAINER_TITLE = new TranslatableComponent("goosemod.container.mixer");

    public MixerBlock(Properties properties) {
        super(properties);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            player.awardStat(ModStats.INTERACT_WITH_MIXER);
            return InteractionResult.CONSUME;
        }
    }

    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((id, inventory, player) -> new MixerMenu(id, inventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }
}
