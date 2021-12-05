package com.notaflyingoose.goosemod.world;

import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.ObfuscationReflectionUtils;
import com.notaflyingoose.goosemod.blocks.ModBlocks;
import com.notaflyingoose.goosemod.entities.ModEntityTypes;
import com.notaflyingoose.goosemod.world.structures.*;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.notaflyingoose.goosemod.world.ModWorldGeneration.Biomes.*;
import static com.notaflyingoose.goosemod.world.ModWorldGeneration.Features.*;
import static com.notaflyingoose.goosemod.world.ModWorldGeneration.Structures.*;

public class ModWorldGeneration {
    public static class Biomes {
        public static final ResourceKey<Biome> MAPLE_BIOME = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(GooseMod.MODID, "maple_forest"));
    }

    public static class Features {
        private static final BlockState MAPLE_LOG = ModBlocks.MAPLE_LOG.defaultBlockState();
        private static final BlockState MAPLE_SAPLING = ModBlocks.MAPLE_SAPLING.defaultBlockState();
        private static final BlockState ORANGE_MAPLE_LEAVES = ModBlocks.ORANGE_MAPLE_LEAVES.defaultBlockState();
        private static final BlockState RED_MAPLE_LEAVES = ModBlocks.RED_MAPLE_LEAVES.defaultBlockState();
        private static final BlockState YELLOW_MAPLE_LEAVES = ModBlocks.YELLOW_MAPLE_LEAVES.defaultBlockState();

        public static final ConfiguredFeature<TreeConfiguration, ?> ORANGE_MAPLE = Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(new SimpleStateProvider(MAPLE_LOG), new FancyTrunkPlacer(3, 11, 0), new SimpleStateProvider(ORANGE_MAPLE_LEAVES), new SimpleStateProvider(MAPLE_SAPLING), new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4), new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))).ignoreVines().build());
        public static final ConfiguredFeature<TreeConfiguration, ?> RED_MAPLE = Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(new SimpleStateProvider(MAPLE_LOG), new FancyTrunkPlacer(3, 11, 0), new SimpleStateProvider(RED_MAPLE_LEAVES), new SimpleStateProvider(MAPLE_SAPLING), new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4), new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))).ignoreVines().build());
        public static final ConfiguredFeature<TreeConfiguration, ?> YELLOW_MAPLE = Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(new SimpleStateProvider(MAPLE_LOG), new FancyTrunkPlacer(3, 11, 0), new SimpleStateProvider(YELLOW_MAPLE_LEAVES), new SimpleStateProvider(MAPLE_SAPLING), new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4), new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))).ignoreVines().build());

        //public static ImmutableList<OreConfiguration.TargetBlockState> MANHUNT_TARGET_LIST;
        //public static ConfiguredFeature<?, ?> MANHUNT;
    }

    public static class Structures {
        public static final StructureFeature<NoneFeatureConfiguration> RV_FEATURE = (StructureFeature<NoneFeatureConfiguration>) new RVFeature().setRegistryName(GooseMod.MODID, "rv");
        public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> RV_CONFIGURED;

        public static final StructureFeature<NoneFeatureConfiguration> LAIR_FEATURE = (StructureFeature<NoneFeatureConfiguration>) new DragonLairFeature().setRegistryName(GooseMod.MODID, "dragon_lair");
        public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> LAIR_CONFIGURED;

        public static final StructureFeature<NoneFeatureConfiguration> BIG_CABIN_FEATURE = (StructureFeature<NoneFeatureConfiguration>) new HerobrineBigCabinFeature().setRegistryName(GooseMod.MODID, "big_cabin");
        public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> BIG_CABIN_CONFIGURED;

        public static final StructureFeature<NoneFeatureConfiguration> SMALL_CABIN_FEATURE = (StructureFeature<NoneFeatureConfiguration>) new HerobrineSmallCabinFeature().setRegistryName(GooseMod.MODID, "small_cabin");
        public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> SMALL_CABIN_CONFIGURED;

        public static final StructureFeature<NoneFeatureConfiguration> ALTAR_FEATURE = (StructureFeature<NoneFeatureConfiguration>) new HerobrineAltarFeature().setRegistryName(GooseMod.MODID, "altar");
        public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> ALTAR_CONFIGURED;

        public static final StructureFeature<NoneFeatureConfiguration> SPIRE_FEATURE = (StructureFeature<NoneFeatureConfiguration>) new SpireFeature().setRegistryName(GooseMod.MODID, "nether_spire");
        public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> SPIRE_CONFIGURED;
    }

    private static final Logger LOGGER = LogManager.getLogger();
    public static void registerConfiguredFeatures() {
        // IGNORE NAMES
        //MANHUNT_TARGET_LIST = ImmutableList.of(OreConfiguration.target(OreConfiguration.Predicates.NATURAL_STONE, ModBlocks.MANHUNT.defaultBlockState()), OreConfiguration.target(OreConfiguration.Predicates.STONE_ORE_REPLACEABLES, ModBlocks.MANHUNT.defaultBlockState()));
        //MANHUNT = (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreConfiguration(MANHUNT_TARGET_LIST, 8)).rangeUniform(VerticalAnchor.absolute(16), VerticalAnchor.absolute(32))).squared()).count(31);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(GooseMod.MODID, "orange_maple"), ORANGE_MAPLE);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(GooseMod.MODID, "red_maple"), RED_MAPLE);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(GooseMod.MODID, "yellow_maple"), YELLOW_MAPLE);

        StructureFeature.STRUCTURES_REGISTRY.put(RV_FEATURE.getRegistryName().toString().toLowerCase(Locale.ROOT), RV_FEATURE);
        RV_CONFIGURED = RV_FEATURE.configured(NoneFeatureConfiguration.INSTANCE);
        LOGGER.info("RV_FEATURE: {}", RV_CONFIGURED);

        StructureFeature.STRUCTURES_REGISTRY.put(LAIR_FEATURE.getRegistryName().toString().toLowerCase(Locale.ROOT), LAIR_FEATURE);
        LAIR_CONFIGURED = LAIR_FEATURE.configured(NoneFeatureConfiguration.INSTANCE);
        LOGGER.info("LAIR_FEATURE: {}", LAIR_CONFIGURED);

        StructureFeature.STRUCTURES_REGISTRY.put(BIG_CABIN_FEATURE.getRegistryName().toString().toLowerCase(Locale.ROOT), BIG_CABIN_FEATURE);
        BIG_CABIN_CONFIGURED = BIG_CABIN_FEATURE.configured(NoneFeatureConfiguration.INSTANCE);
        LOGGER.info("BIG_CABIN_CONFIGURED: {}", BIG_CABIN_CONFIGURED);

        StructureFeature.STRUCTURES_REGISTRY.put(SMALL_CABIN_FEATURE.getRegistryName().toString().toLowerCase(Locale.ROOT), SMALL_CABIN_FEATURE);
        SMALL_CABIN_CONFIGURED = SMALL_CABIN_FEATURE.configured(NoneFeatureConfiguration.INSTANCE);
        LOGGER.info("SMALL_CABIN_CONFIGURED: {}", SMALL_CABIN_CONFIGURED);

        StructureFeature.STRUCTURES_REGISTRY.put(ALTAR_FEATURE.getRegistryName().toString().toLowerCase(Locale.ROOT), ALTAR_FEATURE);
        ALTAR_CONFIGURED = ALTAR_FEATURE.configured(NoneFeatureConfiguration.INSTANCE);
        LOGGER.info("ALTAR_CONFIGURED: {}", ALTAR_CONFIGURED);

        StructureFeature.STRUCTURES_REGISTRY.put(SPIRE_FEATURE.getRegistryName().toString().toLowerCase(Locale.ROOT), SPIRE_FEATURE);
        SPIRE_CONFIGURED = SPIRE_FEATURE.configured(NoneFeatureConfiguration.INSTANCE);
        LOGGER.info("SPIRE_CONFIGURED: {}", SPIRE_CONFIGURED);
    }

    @Mod.EventBusSubscriber(modid = GooseMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus {
        private static final Logger LOGGER = LogManager.getLogger();

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onBiomeLoad(BiomeLoadingEvent event) {
            if (event.getName().equals(MAPLE_BIOME.location()))
            {
                event.getGeneration().getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION).add(() -> ORANGE_MAPLE);
                event.getGeneration().getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION).add(() -> RED_MAPLE);
                event.getGeneration().getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION).add(() -> YELLOW_MAPLE);
                //Do work here
            }
            // custom ore
            //event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> MANHUNT);
            // add the rv to biomes
            switch (event.getCategory()) {
                case JUNGLE -> event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntityTypes.LEMUR, 100, 5, 10));
                case RIVER -> event.getSpawns().addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(ModEntityTypes.GOOSE, 1, 1, 1));
                case DESERT, MESA -> event.getGeneration().addStructureStart(RV_CONFIGURED);
                case EXTREME_HILLS -> event.getGeneration().addStructureStart(LAIR_CONFIGURED);
                case TAIGA -> event.getGeneration().addStructureStart(BIG_CABIN_CONFIGURED).addStructureStart(SMALL_CABIN_CONFIGURED);
                case FOREST -> {
                    if (event.getName().toString().equals("minecraft:dark_forest"))
                        event.getGeneration().addStructureStart(ALTAR_CONFIGURED);
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = GooseMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        private static final Logger LOGGER = LogManager.getLogger();

        @SubscribeEvent
        public static void loadComplete(FMLLoadCompleteEvent event) {
            NoiseGeneratorSettings generator = ObfuscationReflectionUtils.getPrivateStaticFieldOfType(NoiseGeneratorSettings.class, NoiseGeneratorSettings.class);
            generator.structureSettings().structureConfig().put(RV_FEATURE, new StructureFeatureConfiguration(32, 8, 21010516));
            generator.structureSettings().structureConfig().put(LAIR_FEATURE, new StructureFeatureConfiguration(80, 20, 21010517));
            generator.structureSettings().structureConfig().put(BIG_CABIN_FEATURE, new StructureFeatureConfiguration(48, 10, 21010518));
            generator.structureSettings().structureConfig().put(SMALL_CABIN_FEATURE, new StructureFeatureConfiguration(48, 10, 21010519));
            generator.structureSettings().structureConfig().put(ALTAR_FEATURE, new StructureFeatureConfiguration(48, 10, 21010520));
        }
    }

}
