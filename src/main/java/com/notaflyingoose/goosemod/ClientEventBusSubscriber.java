package com.notaflyingoose.goosemod;

import com.google.common.collect.ImmutableSet;
import com.notaflyingoose.goosemod.blocks.ModBlockEntityTypes;
import com.notaflyingoose.goosemod.blocks.ModBlocks;
import com.notaflyingoose.goosemod.client.model.*;
import com.notaflyingoose.goosemod.client.model.ship.LongShipModel;
import com.notaflyingoose.goosemod.client.model.ship.SingleRiderShipModel;
import com.notaflyingoose.goosemod.client.render.*;
import com.notaflyingoose.goosemod.effects.ModMobEffects;
import com.notaflyingoose.goosemod.entities.*;
import com.notaflyingoose.goosemod.entities.RedDragon;
import com.notaflyingoose.goosemod.entities.boss.herobrine.HerobrineBoss;
import com.notaflyingoose.goosemod.entities.vehicle.AbstractShip;
import com.notaflyingoose.goosemod.fluids.ModFluids;
import com.notaflyingoose.goosemod.gui.MixerScreen;
import com.notaflyingoose.goosemod.items.ModItems;
import com.notaflyingoose.goosemod.items.ModSpawnEggItem;
import com.notaflyingoose.goosemod.world.ModWorldGeneration;
import com.notaflyingoose.goosemod.world.crafting.ModRecipeSerializer;
import com.notaflyingoose.goosemod.world.ModSoundEvents;
import com.notaflyingoose.goosemod.gui.ModMenuTypes;
import com.notaflyingoose.goosemod.world.enchantment.ModEnchantments;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Features;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.data.worldgen.SurfaceBuilders;
import net.minecraft.data.worldgen.biome.VanillaBiomes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = GooseMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LayerDefinition humanoidBase = LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64);
        LayerDefinition humanoidInnerArmor = LayerDefinition.create(PlayerModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION, false), 64, 32);
        LayerDefinition humanoidOuterArmor = LayerDefinition.create(PlayerModel.createMesh(LayerDefinitions.INNER_ARMOR_DEFORMATION, false), 64, 32);
        ForgeHooksClient.registerLayerDefinition(ModModelLayers.AMONGUS, AmongusModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(ModModelLayers.HEROBRINE, () -> humanoidBase);
        ForgeHooksClient.registerLayerDefinition(ModModelLayers.HEROBRINE_INNER_ARMOR, () -> humanoidInnerArmor);
        ForgeHooksClient.registerLayerDefinition(ModModelLayers.HEROBRINE_OUTER_ARMOR, () -> humanoidOuterArmor);
        ForgeHooksClient.registerLayerDefinition(ModModelLayers.HEISENBERG, () -> humanoidBase);
        ForgeHooksClient.registerLayerDefinition(ModModelLayers.HEISENBERG_INNER_ARMOR, () -> humanoidInnerArmor);
        ForgeHooksClient.registerLayerDefinition(ModModelLayers.HEISENBERG_OUTER_ARMOR, () -> humanoidOuterArmor);
        ForgeHooksClient.registerLayerDefinition(ModModelLayers.WYVERN, WyvernModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(ModModelLayers.LEMUR, LemurModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(ModModelLayers.GOOSE, GooseModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(ModModelLayers.MIMIC, MimicModel::createBodyLayer);
        //ForgeHooksClient.registerLayerDefinition(ModModelLayers.COPPER_GOLEM, CopperGolemModel::createBodyLayer);
        for(Boat.Type type : Boat.Type.values()) {
            ForgeHooksClient.registerLayerDefinition(ModModelLayers.createShipModelName(AbstractShip.Variant.CHEST, type), SingleRiderShipModel::createBodyLayer);
            ForgeHooksClient.registerLayerDefinition(ModModelLayers.createShipModelName(AbstractShip.Variant.ENDER_CHEST, type), SingleRiderShipModel::createBodyLayer);
            ForgeHooksClient.registerLayerDefinition(ModModelLayers.createShipModelName(AbstractShip.Variant.FURNACE, type), SingleRiderShipModel::createBodyLayer);
            ForgeHooksClient.registerLayerDefinition(ModModelLayers.createShipModelName(AbstractShip.Variant.LONG, type), LongShipModel::createBodyLayer);
        }

        EntityRenderers.register(ModEntityTypes.IMPOSTER, ImposterRenderer::new);
        EntityRenderers.register(ModEntityTypes.CREWMATE, CrewmateRenderer::new);
        EntityRenderers.register(ModEntityTypes.HEROBRINE, HerobrineRenderer::new);
        EntityRenderers.register(ModEntityTypes.HEISENBERG, HeisenbergRenderer::new);
        EntityRenderers.register(ModEntityTypes.RED_DRAGON, RedDragonRenderer::new);
        EntityRenderers.register(ModEntityTypes.DRAGON_SKELETON, DragonSkeletonRenderer::new);
        EntityRenderers.register(ModEntityTypes.LEMUR, LemurRenderer::new);
        EntityRenderers.register(ModEntityTypes.GOOSE, GooseRenderer::new);
        EntityRenderers.register(ModEntityTypes.GOOSE_EGG, ThrownItemRenderer::new);
        EntityRenderers.register(ModEntityTypes.C4, C4Renderer::new);
        EntityRenderers.register(ModEntityTypes.CHEST_BOAT, (context) -> new ShipRenderer(context, AbstractShip.Variant.CHEST));
        EntityRenderers.register(ModEntityTypes.ENDER_CHEST_BOAT, (context) -> new ShipRenderer(context, AbstractShip.Variant.ENDER_CHEST));
        EntityRenderers.register(ModEntityTypes.FURNACE_BOAT, (context) -> new ShipRenderer(context, AbstractShip.Variant.FURNACE));
        EntityRenderers.register(ModEntityTypes.LONG_BOAT, (context) -> new ShipRenderer(context, AbstractShip.Variant.LONG));
        EntityRenderers.register(ModEntityTypes.MIMIC, MimicRenderer::new);
        //EntityRenderers.register(ModEntityTypes.COPPER_GOLEM, CopperGolemRenderer::new);
    }

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        register(event.getRegistry(), ModBlocks.class);
        try {
            Field field = ObfuscationReflectionHelper.findField(BlockEntityType.class, "validBlocks");
            field.setAccessible(true);
            Set<Block> oldValidBlocks = (Set<Block>) field.get(BlockEntityType.SIGN);
            ImmutableSet.Builder<Block> newValidBlocks = new ImmutableSet.Builder<Block>().addAll(oldValidBlocks);
            newValidBlocks.add(ModBlocks.MAPLE_SIGN);
            newValidBlocks.add(ModBlocks.MAPLE_WALL_SIGN);
            field.set(BlockEntityType.SIGN, newValidBlocks.build());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.MAPLE_SAPLING, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.POTTED_MAPLE_SAPLING, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.MAPLE_DOOR, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.MAPLE_TRAPDOOR, RenderType.cutout());
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        register(event.getRegistry(), ModItems.class);
    }

    @SubscribeEvent
    public static void registerBiomes(final RegistryEvent.Register<Biome> event) {
        MobSpawnSettings.Builder spawning = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals(spawning);
        BiomeDefaultFeatures.commonSpawns(spawning);
        BiomeGenerationSettings.Builder generation = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.GRASS);
        BiomeDefaultFeatures.addDefaultOverworldLandStructures(generation);
        generation.addStructureStart(StructureFeatures.RUINED_PORTAL_STANDARD);
        BiomeDefaultFeatures.addDefaultCarvers(generation);
        BiomeDefaultFeatures.addDefaultLakes(generation);
        BiomeDefaultFeatures.addDefaultCrystalFormations(generation);
        BiomeDefaultFeatures.addDefaultMonsterRoom(generation);
        BiomeDefaultFeatures.addForestFlowers(generation);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(generation);
        BiomeDefaultFeatures.addDefaultOres(generation);
        BiomeDefaultFeatures.addDefaultSoftDisks(generation);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModWorldGeneration.Features.RED_MAPLE);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModWorldGeneration.Features.ORANGE_MAPLE);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModWorldGeneration.Features.YELLOW_MAPLE);

        BiomeDefaultFeatures.addDefaultFlowers(generation);
        BiomeDefaultFeatures.addForestGrass(generation);
        BiomeDefaultFeatures.addDefaultMushrooms(generation);
        BiomeDefaultFeatures.addDefaultExtraVegetation(generation);
        BiomeDefaultFeatures.addDefaultSprings(generation);
        BiomeDefaultFeatures.addSurfaceFreezing(generation);
        event.getRegistry().register(new Biome.BiomeBuilder()
                        .precipitation(Biome.Precipitation.RAIN)
                        .biomeCategory(Biome.BiomeCategory.FOREST)
                        .depth(0.25f)
                        .scale(0.3f)
                        .temperature(0.5f)
                        .downfall(0.25f)
                        .specialEffects((new BiomeSpecialEffects.Builder())
                                .waterColor(4159204)
                                .waterFogColor(329011)
                                .fogColor(12638463)
                                .skyColor(calculateSkyColor(0.6F))
                                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                        .generationSettings(generation.build())
                        .mobSpawnSettings(spawning.build())
                .build()
                .setRegistryName(ModWorldGeneration.Biomes.MAPLE_BIOME.getRegistryName()));

    }

    private static int calculateSkyColor(float p_127333_) {
        float f = p_127333_ / 3.0F;
        f = Mth.clamp(f, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
    }

    @SubscribeEvent
    public static void registerFluids(final RegistryEvent.Register<Fluid> event) {
        register(event.getRegistry(), ModFluids.class);
    }

    @SubscribeEvent
    public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
        register(event.getRegistry(), ModSoundEvents.class);
    }

    @SubscribeEvent
    public static void registerMobEffects(final RegistryEvent.Register<MobEffect> event) {
        register(event.getRegistry(), ModMobEffects.class);
    }

    @SubscribeEvent
    public static void registerEntityTypes(final RegistryEvent.Register<EntityType<?>> event) {
        register(event.getRegistry(), ModEntityTypes.class);
        ModSpawnEggItem.initSpawnEggs();
        DispenserBlock.registerBehavior(ModItems.GOOSE_EGG, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level p_123468_, Position p_123469_, ItemStack p_123470_) {
                return Util.make(new ThrownGooseEgg(p_123468_, p_123469_.x(), p_123469_.y(), p_123469_.z()), (p_123466_) -> {
                    p_123466_.setItem(p_123470_);
                });
            }
        });
    }

    @SubscribeEvent
    public static void registerBlockEntityTypes(final RegistryEvent.Register<BlockEntityType<?>> event) {
        register(event.getRegistry(), ModBlockEntityTypes.class);
    }

    @SubscribeEvent
    public static void registerMenuTypes(final RegistryEvent.Register<MenuType<?>> event) {
        register(event.getRegistry(), ModMenuTypes.class);
        MenuScreens.register(ModMenuTypes.MIXER, MixerScreen::new);
    }

    @SubscribeEvent
    public static void registerEnchantments(final RegistryEvent.Register<Enchantment> event) {
        register(event.getRegistry(), ModEnchantments.class);
    }

    @SubscribeEvent
    public static void registerRecipeSerializers(final RegistryEvent.Register<RecipeSerializer<?>> event) {
        register(event.getRegistry(), ModRecipeSerializer.class);
    }

    @SubscribeEvent
    public static void registerStructureFeatures(final RegistryEvent.Register<StructureFeature<?>> event) {
        register(event.getRegistry(), ModWorldGeneration.Structures.class);
    }

    @SubscribeEvent
    public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.GOOSE, Goose.createCustomAttributes());
        event.put(ModEntityTypes.CREWMATE, Crewmate.createCustomAttributes());
        event.put(ModEntityTypes.IMPOSTER, Imposter.createCustomAttributes());
        event.put(ModEntityTypes.HEISENBERG, Heisenberg.createCustomAttributes());
        event.put(ModEntityTypes.RED_DRAGON, RedDragon.createCustomAttributes());
        event.put(ModEntityTypes.DRAGON_SKELETON, DragonSkeleton.createCustomAttributes());
        event.put(ModEntityTypes.HEROBRINE, HerobrineBoss.createCustomAttributes());
        event.put(ModEntityTypes.LEMUR, Lemur.createCustomAttributes());
        event.put(ModEntityTypes.MIMIC, Mimic.createCustomAttributes());
        //event.put(ModEntityTypes.COPPER_GOLEM, CopperGolem.createCustomAttributes());
    }

    private static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isStatic(field.getModifiers()))
                continue;
            T value;
            try {
                value = (T) field.get(null);
                if (value == null) throw new NullPointerException();
                if (value.getRegistryName() == null) throw new NullRegistryNameException(field.getName() + " has no registry name!");
            } catch (NullPointerException | IllegalAccessException | ClassCastException e) {
                continue;
            } catch (NullRegistryNameException e) {
                e.printStackTrace();
                continue;
            }
            registry.register(value);
            LOGGER.debug("Registered field " + field.getName()+ " as " + value.getRegistryName() + " in " + registry.getRegistrySuperType().getName() + " registry");
        }
    }

}
