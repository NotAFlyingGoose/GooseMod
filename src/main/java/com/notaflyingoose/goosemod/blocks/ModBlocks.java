package com.notaflyingoose.goosemod.blocks;

import com.notaflyingoose.goosemod.fluids.ModFluids;
import com.notaflyingoose.goosemod.world.ModMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.OakTreeGrower;
import net.minecraft.world.level.block.grower.SpruceTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class ModBlocks {
    private static Method createLeaves = null;

    public static final LiquidBlock NITROGLYCERIN = (LiquidBlock) new LiquidBlock(ModFluids.NITROGLYCERIN, BlockBehaviour.Properties.of(ModMaterials.NITROGLYCERIN, MaterialColor.COLOR_LIGHT_GREEN).noCollission().strength(100).noDrops()).setRegistryName("nitroglycerin");

    public static final Block DEBUG = new DebugBlock().setRegistryName("debug");
    //public static final Block MANHUNT = new OreBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(30.0F, 1200.0F)).setRegistryName("manhunt");
    //public static final Block DEEPSLATE_MANHUNT = new OreBlock(BlockBehaviour.Properties.copy(MANHUNT).color(MaterialColor.DEEPSLATE).strength(60.0F, 1200.0F).sound(SoundType.DEEPSLATE)).setRegistryName("deepslate_manhunt");
    public static final Block C4 = new C4Block().setRegistryName("c4");
    public static final Block MIXER = new MixerBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)).setRegistryName("mixer");
    public static final Block DRAGON_SKULL = new DragonSkullBlock(BlockBehaviour.Properties.of(Material.DECORATION).strength(1)).setRegistryName("dragon_skull");
    public static final Block NETHER_REACTOR_CORE = new NetherReactorCore().setRegistryName("nether_reactor_core");
    public static final Block GLOWING_OBSIDIAN = new MagmaBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).lightLevel((p_50804_) -> {
        return 3;
    }).randomTicks().isValidSpawn((p_152645_, p_152646_, p_152647_, p_152648_) -> {
        return p_152648_.fireImmune();
    }).hasPostProcess((p_61036_, p_61037_, p_61038_) -> true).emissiveRendering((p_61036_, p_61037_, p_61038_) -> true)).setRegistryName("glowing_obsidian");

    public static final Block MAPLE_PLANKS = new Block(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_YELLOW).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName("maple_planks");
    public static final Block MAPLE_SAPLING = new SaplingBlock(new MapleTreeGrower(), BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)).setRegistryName("maple_sapling");
    public static final Block MAPLE_LOG = log(MaterialColor.TERRACOTTA_YELLOW, MaterialColor.TERRACOTTA_CYAN).setRegistryName("maple_log");
    public static final Block STRIPPED_MAPLE_LOG = log(MaterialColor.TERRACOTTA_YELLOW, MaterialColor.TERRACOTTA_YELLOW).setRegistryName("stripped_maple_log");
    public static final Block MAPLE_WOOD = new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, MAPLE_LOG.defaultMaterialColor()).strength(2.0F).sound(SoundType.WOOD)).setRegistryName("maple_wood");
    public static final Block STRIPPED_MAPLE_WOOD = new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, MAPLE_LOG.defaultMaterialColor()).strength(2.0F).sound(SoundType.WOOD)).setRegistryName("stripped_maple_wood");
    public static final Block ORANGE_MAPLE_LEAVES = leaves(SoundType.GRASS).setRegistryName("orange_maple_leaves");
    public static final Block RED_MAPLE_LEAVES = leaves(SoundType.GRASS).setRegistryName("red_maple_leaves");
    public static final Block YELLOW_MAPLE_LEAVES = leaves(SoundType.GRASS).setRegistryName("yellow_maple_leaves");
    public static final Block MAPLE_SIGN = new StandingSignBlock(BlockBehaviour.Properties.of(Material.WOOD, MAPLE_LOG.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD), ModWoodTypes.MAPLE).setRegistryName("maple_sign");
    public static final Block MAPLE_WALL_SIGN = new WallSignBlock(BlockBehaviour.Properties.of(Material.WOOD, MAPLE_LOG.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD).dropsLike(MAPLE_SIGN), ModWoodTypes.MAPLE).setRegistryName("maple_wall_sign");
    public static final Block MAPLE_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of(Material.WOOD, MAPLE_PLANKS.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)).setRegistryName("maple_pressure_plate");
    public static final Block MAPLE_TRAPDOOR = new TrapDoorBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_YELLOW).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn((BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> entityType) -> false)).setRegistryName("maple_trapdoor");
    public static final Block MAPLE_STAIRS = new StairBlock(MAPLE_PLANKS.defaultBlockState(), BlockBehaviour.Properties.copy(MAPLE_PLANKS)).setRegistryName("maple_stairs");
    public static final Block POTTED_MAPLE_SAPLING = new FlowerPotBlock(MAPLE_SAPLING, BlockBehaviour.Properties.of(Material.DECORATION).instabreak().noOcclusion()).setRegistryName("potted_maple_sapling");
    public static final Block MAPLE_BUTTON = new WoodButtonBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)).setRegistryName("maple_button");
    public static final Block MAPLE_SLAB = new SlabBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_YELLOW).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName("maple_slab");
    public static final Block MAPLE_FENCE_GATE = new FenceGateBlock(BlockBehaviour.Properties.of(Material.WOOD, MAPLE_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName("maple_fence_gate");
    public static final Block MAPLE_FENCE = new FenceBlock(BlockBehaviour.Properties.of(Material.WOOD, MAPLE_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName("maple_fence");
    public static final Block MAPLE_DOOR = new DoorBlock(BlockBehaviour.Properties.of(Material.WOOD, MAPLE_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()).setRegistryName("maple_door");

    private static RotatedPillarBlock log(MaterialColor top, MaterialColor side) {
        return new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, (state) -> {
            return state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? top : side;
        }).strength(2.0F).sound(SoundType.WOOD));
    }

    private static LeavesBlock leaves(SoundType sound) {
        if (createLeaves == null) {
            for (Method m : Blocks.class.getDeclaredMethods()) {
                Parameter[] pType = m.getParameters();
                if (pType.length == 1 && pType[0].getType().isAssignableFrom(SoundType.class)) {
                    createLeaves = m;
                    createLeaves.setAccessible(true);
                    break;
                }
            }
        }
        try {
            return (LeavesBlock) createLeaves.invoke(null, sound);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
