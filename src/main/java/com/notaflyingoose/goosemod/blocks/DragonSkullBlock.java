package com.notaflyingoose.goosemod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;

import javax.annotation.Nullable;

public class DragonSkullBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public DragonSkullBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_48781_) {
        return this.defaultBlockState().setValue(FACING, p_48781_.getNearestLookingDirection().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> states) {
        states.add(FACING);
    }

    /*public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SkullBlockEntity(pos, state);
    }*/

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_151992_, BlockState p_151993_, BlockEntityType<T> p_151994_) {
        return null;
    }

    public boolean isPathfindable(BlockState p_48750_, BlockGetter p_48751_, BlockPos p_48752_, PathComputationType p_48753_) {
        return false;
    }
}
