package com.notaflyingoose.goosemod.blocks;

import com.notaflyingoose.goosemod.entities.NetherReactorCoreBlockEntity;
import com.notaflyingoose.goosemod.world.structures.SpireFeature;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class NetherReactorCore extends BaseEntityBlock {
    private static BlockPattern stableReactorPattern;
    private static BlockPattern initializedReactorPattern;
    protected static final Logger LOGGER = LogManager.getLogger();
    public static final EnumProperty<ReactorState> REACTOR_STATE = EnumProperty.create("reactor_state", ReactorState.class);

    public NetherReactorCore() {
        super(BlockBehaviour.Properties.of(Material.METAL)
                .strength(25, 1200)
                .isValidSpawn((p_61031_, p_61032_, p_61033_, p_61034_) -> false)
                .requiresCorrectToolForDrops()
                .lightLevel((p_50804_) -> 10));
        this.registerDefaultState(this.stateDefinition.any().setValue(REACTOR_STATE, ReactorState.STABLE));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(REACTOR_STATE);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(REACTOR_STATE) == ReactorState.STABLE) {
            if (!isValidReactor(level, pos, ReactorState.STABLE)) {
                player.displayClientMessage(new TranslatableComponent("block.goosemod.nether_reactor_core.wrong_pattern"), true);
                return InteractionResult.PASS;
            } else if (pos.getY() > 96) {
                player.displayClientMessage(new TranslatableComponent("block.goosemod.nether_reactor_core.too_high"), true);
                return InteractionResult.PASS;
            } else if (pos.getY() < 4) {
                player.displayClientMessage(new TranslatableComponent("block.goosemod.nether_reactor_core.too_low"), true);
                return InteractionResult.PASS;
            }

            if (level.isClientSide) {
                spawnParticles(level, pos, ParticleTypes.SOUL_FIRE_FLAME);
                return InteractionResult.SUCCESS;
            } else {
                loadSpire((ServerLevel) level, pos, 1);
                setReactorState(level, pos, state, ReactorState.INITIALIZED);
                level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.3F, 1);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    public static void loadSpire(ServerLevel level, BlockPos pos, float integrity) {
        StructureManager structuremanager = level.getStructureManager();
        StructureTemplate template = structuremanager.get(SpireFeature.SpirePiece.STRUCTURE_LOCATION).get();
        Random random = new Random(Util.getMillis());

        StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setIgnoreEntities(false);
        if (integrity < 1.0F) {
            structureplacesettings.clearProcessors().addProcessor(new BlockRotProcessor(Mth.clamp(integrity, 0.0F, 1.0F))).setRandom(random);
        }

        BlockPos blockpos1 = pos.offset(-8, -3, -8);
        template.placeInWorld(level, blockpos1, blockpos1, structureplacesettings, random, 2);
    }

    @Override
    public void neighborChanged(BlockState myState, Level level, BlockPos myPos, Block neighborBlock, BlockPos neighborPos, boolean aBoolean) {
        super.neighborChanged(myState, level, myPos, neighborBlock, neighborPos, aBoolean);
        if (myState.getValue(REACTOR_STATE) == ReactorState.INITIALIZED && !isValidReactor(level, myPos, ReactorState.INITIALIZED)) {
            setReactorState(level, myPos, myState, ReactorState.FINISHED);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        if (state.getValue(REACTOR_STATE) != ReactorState.STABLE) {
            return List.of(new ItemStack(Items.IRON_INGOT, RANDOM.nextInt(5)+1),
                    new ItemStack(Items.DIAMOND, RANDOM.nextInt(3)+1));
        }
        return List.of();
    }

    public static void spawnParticles(Level level, BlockPos pos, ParticleOptions particles) {
        for(int i = 0; i < 16; ++i) {
            double d0 = level.random.nextGaussian() * 0.02D;
            double d1 = level.random.nextGaussian() * 0.02D;
            double d2 = level.random.nextGaussian() * 0.02D;
            double rX = pos.getX() + 0.5 * (8.0D * level.random.nextDouble() - 4.0D) + 0.5;
            double rY = pos.getY() + 0.5 * (8.0D * level.random.nextDouble() - 4.0D) * level.random.nextDouble() + 0.5;
            double rZ = pos.getZ() + 0.5 * (8.0D * level.random.nextDouble() - 4.0D) + 0.5;
            level.addParticle(particles, rX, rY, rZ, d0, d1, d2);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NetherReactorCoreBlockEntity(pos, state);
    }

    public RenderShape getRenderShape(BlockState p_48727_) {
        return RenderShape.MODEL;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntityTypes.NETHER_REACTOR_CORE, level.isClientSide ? NetherReactorCoreBlockEntity::clientTick : NetherReactorCoreBlockEntity::serverTick);
    }

    @Override
    public MutableComponent getName() {
        return new TranslatableComponent(this.getDescriptionId() + ".stable");
    }

    public static boolean isValidReactor(Level level, BlockPos pos, ReactorState state) {
        if (state == ReactorState.STABLE) {
            BlockPos abovePos = pos.above();
            boolean top = level.getBlockState(abovePos.north().east()).is(Blocks.AIR) &&
                    level.getBlockState(abovePos.north()).is(Blocks.COBBLESTONE) &&
                    level.getBlockState(abovePos.north().west()).is(Blocks.AIR) &&

                    level.getBlockState(abovePos.east()).is(Blocks.COBBLESTONE) &&
                    level.getBlockState(abovePos).is(Blocks.COBBLESTONE) &&
                    level.getBlockState(abovePos.west()).is(Blocks.COBBLESTONE) &&

                    level.getBlockState(abovePos.south().east()).is(Blocks.AIR) &&
                    level.getBlockState(abovePos.south()).is(Blocks.COBBLESTONE) &&
                    level.getBlockState(abovePos.south().west()).is(Blocks.AIR);
            if (!top)
                return false;
            boolean middle = level.getBlockState(pos.north().east()).is(Blocks.COBBLESTONE) &&
                    level.getBlockState(pos.north()).is(Blocks.AIR) &&
                    level.getBlockState(pos.north().west()).is(Blocks.COBBLESTONE) &&

                    level.getBlockState(pos.east()).is(Blocks.AIR) &&
                    level.getBlockState(pos).is(ModBlocks.NETHER_REACTOR_CORE) &&
                    level.getBlockState(pos.west()).is(Blocks.AIR) &&

                    level.getBlockState(pos.south().east()).is(Blocks.COBBLESTONE) &&
                    level.getBlockState(pos.south()).is(Blocks.AIR) &&
                    level.getBlockState(pos.south().west()).is(Blocks.COBBLESTONE);
            if (!middle)
                return false;
            BlockPos belowPos = pos.below();
            return level.getBlockState(belowPos.north().east()).is(Blocks.GOLD_BLOCK) &&
                    level.getBlockState(belowPos.north()).is(Blocks.COBBLESTONE) &&
                    level.getBlockState(belowPos.north().west()).is(Blocks.GOLD_BLOCK) &&

                    level.getBlockState(belowPos.east()).is(Blocks.COBBLESTONE) &&
                    level.getBlockState(belowPos).is(Blocks.COBBLESTONE) &&
                    level.getBlockState(belowPos.west()).is(Blocks.COBBLESTONE) &&

                    level.getBlockState(belowPos.south().east()).is(Blocks.GOLD_BLOCK) &&
                    level.getBlockState(belowPos.south()).is(Blocks.COBBLESTONE) &&
                    level.getBlockState(belowPos.south().west()).is(Blocks.GOLD_BLOCK);
        }
        else if (state == ReactorState.INITIALIZED) {
            BlockPos abovePos = pos.above();
            boolean top = level.getBlockState(abovePos.north().east()).is(Blocks.AIR) &&
                    level.getBlockState(abovePos.north()).is(ModBlocks.GLOWING_OBSIDIAN) &&
                    level.getBlockState(abovePos.north().west()).is(Blocks.AIR) &&

                    level.getBlockState(abovePos.east()).is(ModBlocks.GLOWING_OBSIDIAN) &&
                    level.getBlockState(abovePos).is(ModBlocks.GLOWING_OBSIDIAN) &&
                    level.getBlockState(abovePos.west()).is(ModBlocks.GLOWING_OBSIDIAN) &&

                    level.getBlockState(abovePos.south().east()).is(Blocks.AIR) &&
                    level.getBlockState(abovePos.south()).is(ModBlocks.GLOWING_OBSIDIAN) &&
                    level.getBlockState(abovePos.south().west()).is(Blocks.AIR);
            if (!top)
                return false;
            boolean middle = level.getBlockState(pos.north().east()).is(ModBlocks.GLOWING_OBSIDIAN) &&
                    level.getBlockState(pos.north()).is(Blocks.AIR) &&
                    level.getBlockState(pos.north().west()).is(ModBlocks.GLOWING_OBSIDIAN) &&

                    level.getBlockState(pos.east()).is(Blocks.AIR) &&
                    level.getBlockState(pos).is(ModBlocks.NETHER_REACTOR_CORE) &&
                    level.getBlockState(pos.west()).is(Blocks.AIR) &&

                    level.getBlockState(pos.south().east()).is(ModBlocks.GLOWING_OBSIDIAN) &&
                    level.getBlockState(pos.south()).is(Blocks.AIR) &&
                    level.getBlockState(pos.south().west()).is(ModBlocks.GLOWING_OBSIDIAN);
            if (!middle)
                return false;
            BlockPos belowPos = pos.below();
            return level.getBlockState(belowPos.north().east()).is(ModBlocks.GLOWING_OBSIDIAN) &&
                    level.getBlockState(belowPos.north()).is(ModBlocks.GLOWING_OBSIDIAN) &&
                    level.getBlockState(belowPos.north().west()).is(ModBlocks.GLOWING_OBSIDIAN) &&

                    level.getBlockState(belowPos.east()).is(ModBlocks.GLOWING_OBSIDIAN) &&
                    level.getBlockState(belowPos).is(ModBlocks.GLOWING_OBSIDIAN) &&
                    level.getBlockState(belowPos.west()).is(ModBlocks.GLOWING_OBSIDIAN) &&

                    level.getBlockState(belowPos.south().east()).is(ModBlocks.GLOWING_OBSIDIAN) &&
                    level.getBlockState(belowPos.south()).is(ModBlocks.GLOWING_OBSIDIAN) &&
                    level.getBlockState(belowPos.south().west()).is(ModBlocks.GLOWING_OBSIDIAN);
        }
        return true;
    }

    public static void setReactorState(Level level, BlockPos pos, BlockState state, ReactorState reactorState) {
        if (reactorState == ReactorState.INITIALIZED) {
            BlockPos abovePos = pos.above();
            BlockState glowing_obsidian = ModBlocks.GLOWING_OBSIDIAN.defaultBlockState();
            BlockState air = Blocks.AIR.defaultBlockState();
            level.setBlock(abovePos.north().east(), air, 3);
            level.setBlock(abovePos.north(), glowing_obsidian, 3);
            level.setBlock(abovePos.north().west(), air, 3);

            level.setBlock(abovePos.east(), glowing_obsidian, 3);
            level.setBlock(abovePos, glowing_obsidian, 3);
            level.setBlock(abovePos.west(), glowing_obsidian, 3);

            level.setBlock(abovePos.south().east(), air, 3);
            level.setBlock(abovePos.south(), glowing_obsidian, 3);
            level.setBlock(abovePos.south().west(), air, 3);

            level.setBlock(pos.north().east(), glowing_obsidian, 3);
            level.setBlock(pos.north(), air, 3);
            level.setBlock(pos.north().west(), glowing_obsidian, 3);

            level.setBlock(pos.east(), air, 3);
            //
            level.setBlock(pos.west(), air, 3);

            level.setBlock(pos.south().east(), glowing_obsidian, 3);
            level.setBlock(pos.south(), air, 3);
            level.setBlock(pos.south().west(), glowing_obsidian, 3);

            BlockPos belowPos = pos.below();
            level.setBlock(belowPos.north().east(), glowing_obsidian, 3);
            level.setBlock(belowPos.north(), glowing_obsidian, 3);
            level.setBlock(belowPos.north().west(), glowing_obsidian, 3);

            level.setBlock(belowPos.east(), glowing_obsidian, 3);
            level.setBlock(belowPos, glowing_obsidian, 3);
            level.setBlock(belowPos.west(), glowing_obsidian, 3);

            level.setBlock(belowPos.south().east(), glowing_obsidian, 3);
            level.setBlock(belowPos.south(), glowing_obsidian, 3);
            level.setBlock(belowPos.south().west(), glowing_obsidian, 3);
        }
        else if (reactorState == ReactorState.FINISHED) {
            Direction[] horizontalDirections = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
            BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();
            BlockPos.MutableBlockPos setPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY()+1, pos.getZ());
            level.setBlock(setPos, obsidian, 3);
            for (int i = 0; i < 3; i++) {
                for (Direction direction : horizontalDirections) {
                    level.setBlock(setPos.relative(direction), obsidian, 3);
                }
                level.setBlock(setPos.north().east(), obsidian, 3);
                level.setBlock(setPos.north().west(), obsidian, 3);
                level.setBlock(setPos.south().east(), obsidian, 3);
                level.setBlock(setPos.south().west(), obsidian, 3);
                setPos.move(Direction.DOWN);
            }
            level.setBlock(setPos.above(), obsidian, 3);
        }
        level.setBlock(pos, state.setValue(REACTOR_STATE, reactorState), 3);
    }

    public static BlockPattern getOrCreateStableReactorPattern() {
        if (stableReactorPattern == null) {
            stableReactorPattern = BlockPatternBuilder.start().aisle(" # ", "###", " # ").aisle("# #", " $ ", "# #").aisle("@#@", "###", "@#@")
                    .where('#', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.COBBLESTONE)))
                    .where('@', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.GOLD_BLOCK)))
                    .where('$', BlockInWorld.hasState(BlockPredicate.forBlock(ModBlocks.NETHER_REACTOR_CORE))).build();
        }

        return stableReactorPattern;
    }

    public static BlockPattern getOrCreateInitializedReactorPattern() {
        if (stableReactorPattern == null) {
            stableReactorPattern = BlockPatternBuilder.start().aisle(" # ", "###", " # ").aisle("# #", " $ ", "# #").aisle("###", "###", "###")
                    .where('#', BlockInWorld.hasState(BlockPredicate.forBlock(ModBlocks.GLOWING_OBSIDIAN)))
                    .where('$', BlockInWorld.hasState(BlockPredicate.forBlock(ModBlocks.NETHER_REACTOR_CORE))).build();
        }

        return stableReactorPattern;
    }

    public enum ReactorState implements StringRepresentable {
        STABLE("stable"),
        INITIALIZED("initialized"),
        FINISHED("finished");

        private final String name;

        ReactorState(String p_61339_) {
            this.name = p_61339_;
        }

        public String toString() {
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }

}
