package com.notaflyingoose.goosemod.fluids;

import com.google.common.collect.Lists;
import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.blocks.ModBlocks;
import com.notaflyingoose.goosemod.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.FluidAttributes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

public abstract class NitroglycerinFluid extends FlowingFluid {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_NITROGLYCERIN;
    }

    @Override
    public Fluid getSource() {
        return ModFluids.NITROGLYCERIN;
    }

    @Override
    protected boolean canConvertToSource() {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor accessor, BlockPos pos, BlockState state) {
        BlockEntity blockentity = state.hasBlockEntity() ? accessor.getBlockEntity(pos) : null;
        Block.dropResources(state, accessor, pos, blockentity);
    }

    @Override
    protected int getSlopeFindDistance(LevelReader p_76074_) {
        return 4;
    }

    @Override
    protected int getDropOff(LevelReader p_76087_) {
        return 3;
    }

    @Override
    public int getTickDelay(LevelReader p_76087_) {
        return 5;
    }

    @Override
    public Item getBucket() {
        return ModItems.NITROGLYCERIN_BUCKET;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter getter, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.is(ModFluids.Tags.NITROGLYCERIN);
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return ModBlocks.NITROGLYCERIN.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == ModFluids.NITROGLYCERIN || fluid == ModFluids.FLOWING_NITROGLYCERIN;
    }

    @Override
    protected FluidAttributes createAttributes() {
        return FluidAttributes.builder(new ResourceLocation(GooseMod.MODID ,"block/nitroglycerin_still"), new ResourceLocation(GooseMod.MODID ,"block/nitroglycerin_flow"))
                .translationKey("block.goosemod.nitroglycerin")
                .overlay(new ResourceLocation(GooseMod.MODID ,"block/nitroglycerin_overlay"))
                .density(3000)
                .viscosity(1000)
                .luminosity(15)
                .temperature(1300)
                .build(this);
    }

    public static void ignite(Level level, BlockPos pos, Entity responsible) {
        boolean exploding = false;
        for (Direction direction : Direction.values()) {
            BlockPos relativePos = pos.relative(direction);
            if (level.getFluidState(relativePos).is(ModFluids.Tags.NITROGLYCERIN)) {
                exploding = true;
                level.explode(responsible, relativePos.getX(), relativePos.getY(), relativePos.getZ(), 3, true, Explosion.BlockInteraction.DESTROY);
            }
        }
        if (exploding)
            NitroglycerinFluid.removeNearbyNitroglycerin(level, pos, responsible, 4, 0.25f);
    }

    private static boolean removeNearbyNitroglycerin(Level level, BlockPos pos, Entity responsible, int reach, float igniteChance) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple<>(pos, 0));
        int i = 0;

        while(!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getA();
            int j = tuple.getB();

            for(Direction direction : Direction.values()) {
                BlockPos blockpos1 = blockpos.relative(direction);
                BlockState blockstate = level.getBlockState(blockpos1);
                FluidState fluidstate = level.getFluidState(blockpos1);
                Material material = blockstate.getMaterial();
                if (fluidstate.is(ModFluids.Tags.NITROGLYCERIN)) {
                    if (blockstate.getBlock() instanceof BucketPickup && !((BucketPickup)blockstate.getBlock()).pickupBlock(level, blockpos1, blockstate).isEmpty()) {
                        if (level.random.nextFloat() < igniteChance)
                            ignite(level, blockpos1, responsible);
                        ++i;
                        if (j < reach) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    } else if (blockstate.getBlock() instanceof LiquidBlock) {
                        level.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < reach) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                        BlockEntity blockentity = blockstate.hasBlockEntity() ? level.getBlockEntity(blockpos1) : null;
                        Block.dropResources(blockstate, level, blockpos1, blockentity);
                        level.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < reach) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    }
                }
            }

            if (i > 64) {
                break;
            }
        }

        return i > 0;
    }

    public static class Source extends NitroglycerinFluid {

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }
    }

    public static class Flowing extends NitroglycerinFluid {

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(NitroglycerinFluid.LEVEL);
        }
    }
}
