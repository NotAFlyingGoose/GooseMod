package com.notaflyingoose.goosemod.items;

import com.google.common.base.Supplier;
import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.blocks.ModBlocks;
import com.notaflyingoose.goosemod.effects.ModMobEffects;
import com.notaflyingoose.goosemod.entities.ModEntityTypes;
import com.notaflyingoose.goosemod.entities.vehicle.ChestBoat;
import com.notaflyingoose.goosemod.entities.vehicle.EnderChestBoat;
import com.notaflyingoose.goosemod.entities.vehicle.FurnaceBoat;
import com.notaflyingoose.goosemod.entities.vehicle.LongBoat;
import com.notaflyingoose.goosemod.world.ModTabs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;

public class ModItems {
    public static final Item DEBUG = createBlockItem(ModBlocks.DEBUG);
    public static final Item NETHER_REACTOR_CORE = createBlockItem(ModBlocks.NETHER_REACTOR_CORE);
    public static final Item GLOWING_OBSIDIAN = createBlockItem(ModBlocks.GLOWING_OBSIDIAN);

    //public static final Item DREAMIUM_BLOCK = new BlockItem(ModBlocks.DREAMIUM_BLOCK, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS).fireResistant()).setRegistryName("dreamium_block");
    //public static final Item MANHUNT = new BlockItem(ModBlocks.MANHUNT, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS).fireResistant()).setRegistryName("manhunt");
    //public static final Item DEEPSLATE_MANHUNT = new BlockItem(ModBlocks.DEEPSLATE_MANHUNT, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS).fireResistant()).setRegistryName("deepslate_manhunt");
    public static final Item C4 = createBlockItem(ModBlocks.C4);
    public static final Item MIXER = createBlockItem(ModBlocks.MIXER);
    public static final Item DRAGON_SKULL = new BlockItem(ModBlocks.DRAGON_SKULL, new Item.Properties().tab(ModTabs.GOOSEMOD).fireResistant()).setRegistryName("dragon_skull");
    public static final Item MAPLE_PLANKS = createBlockItem(ModBlocks.MAPLE_PLANKS);
    public static final Item MAPLE_SAPLING = createBlockItem(ModBlocks.MAPLE_SAPLING);
    public static final Item MAPLE_LOG = createBlockItem(ModBlocks.MAPLE_LOG);
    public static final Item STRIPPED_MAPLE_LOG = createBlockItem(ModBlocks.STRIPPED_MAPLE_LOG);
    public static final Item MAPLE_WOOD = createBlockItem(ModBlocks.MAPLE_WOOD);
    public static final Item STRIPPED_MAPLE_WOOD = createBlockItem(ModBlocks.STRIPPED_MAPLE_WOOD);
    public static final Item ORANGE_MAPLE_LEAVES = createBlockItem(ModBlocks.ORANGE_MAPLE_LEAVES);
    public static final Item RED_MAPLE_LEAVES = createBlockItem(ModBlocks.RED_MAPLE_LEAVES);
    public static final Item YELLOW_MAPLE_LEAVES = createBlockItem(ModBlocks.YELLOW_MAPLE_LEAVES);
    public static final Item MAPLE_SIGN = new SignItem((new Item.Properties()).stacksTo(16).tab(ModTabs.GOOSEMOD), ModBlocks.MAPLE_SIGN, ModBlocks.MAPLE_WALL_SIGN).setRegistryName("maple_sign");
    public static final Item MAPLE_PRESSURE_PLATE = createBlockItem(ModBlocks.MAPLE_PRESSURE_PLATE);
    public static final Item MAPLE_TRAPDOOR = createBlockItem(ModBlocks.MAPLE_TRAPDOOR);
    public static final Item MAPLE_STAIRS = createBlockItem(ModBlocks.MAPLE_STAIRS);
    public static final Item MAPLE_BUTTON = createBlockItem(ModBlocks.MAPLE_BUTTON);
    public static final Item MAPLE_SLAB = createBlockItem(ModBlocks.MAPLE_SLAB);
    public static final Item MAPLE_FENCE_GATE = createBlockItem(ModBlocks.MAPLE_FENCE_GATE);
    public static final Item MAPLE_FENCE = createBlockItem(ModBlocks.MAPLE_FENCE);
    public static final Item MAPLE_DOOR = new DoubleHighBlockItem(ModBlocks.MAPLE_DOOR, (new Item.Properties()).tab(ModTabs.GOOSEMOD)).setRegistryName("maple_door");

    private static Item createBlockItem(Block block) {
        return new BlockItem(block, new Item.Properties().tab(ModTabs.GOOSEMOD)).setRegistryName(block.getRegistryName());
    }

    // SPAWN EGGS
    public static final Item CREWMATE_SPAWN_EGG = new ModSpawnEggItem(() -> ModEntityTypes.CREWMATE, 0xAA0000, 0x0000FF, new Item.Properties().tab(ModTabs.GOOSEMOD)).setRegistryName("crewmate_spawn_egg");
    public static final Item IMPOSTER_SPAWN_EGG = new ModSpawnEggItem(() -> ModEntityTypes.IMPOSTER, 0x990000, 0x00257F, new Item.Properties().tab(ModTabs.GOOSEMOD)).setRegistryName("imposter_spawn_egg");
    public static final Item HEISENBERG_SPAWN_EGG = new ModSpawnEggItem(() -> ModEntityTypes.HEISENBERG, 0x6CA652, 0x049DC3, new Item.Properties().tab(ModTabs.GOOSEMOD)).setRegistryName("heisenberg_spawn_egg");
    public static final Item LEMUR_SPAWN_EGG = new ModSpawnEggItem(() -> ModEntityTypes.LEMUR, 0xEBEBEB, 0x1A1A1B, new Item.Properties().tab(ModTabs.GOOSEMOD)).setRegistryName("lemur_spawn_egg");
    public static final Item GOOSE_SPAWN_EGG = new ModSpawnEggItem(() -> ModEntityTypes.GOOSE, 0xECF8FD, 0xFF9B25, new Item.Properties().tab(ModTabs.GOOSEMOD)).setRegistryName("goose_spawn_egg");
    public static final Item MIMIC_SPAWN_EGG = new ModSpawnEggItem(() -> ModEntityTypes.MIMIC, 0xA26B23, 0x28241D, new Item.Properties().tab(ModTabs.GOOSEMOD)).setRegistryName("mimic_spawn_egg");

    // FOOD
    public static final Item BIG_MICK = new Item(new Item.Properties()
            .tab(ModTabs.GOOSEMOD)
            .food(new FoodProperties.Builder()
                    .nutrition(20)
                    .saturationMod(0.5f)
                    .meat()
                    .effect((Supplier<MobEffectInstance>) () -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1000, 2), 1)
                    .effect((Supplier<MobEffectInstance>) () -> new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1000, 2), 1)
                    .effect((Supplier<MobEffectInstance>) () -> new MobEffectInstance(MobEffects.HUNGER, 1000, 2), 1)
                    .effect((Supplier<MobEffectInstance>) () -> new MobEffectInstance(MobEffects.POISON, 1000, 0), 1)
                    .build())).setRegistryName("big_mick");
    public static final Item KETAMINE = new Item(new Item.Properties()
            .tab(ModTabs.GOOSEMOD)
            .food(new FoodProperties.Builder()
                    .nutrition(0)
                    .saturationMod(2)
                    .effect((Supplier<MobEffectInstance>) () -> new MobEffectInstance(ModMobEffects.HIGH, 120000, 1, false, false, true), 1)
                    .effect((Supplier<MobEffectInstance>) () -> new MobEffectInstance(MobEffects.CONFUSION, 1000, 3), 1)
                    .effect((Supplier<MobEffectInstance>) () -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1000, 255), 1)
                    .effect((Supplier<MobEffectInstance>) () -> new MobEffectInstance(MobEffects.BLINDNESS, 1000, 5), 1)
                    .build())).setRegistryName("ketamine");
    public static final Item METH = new Item(new Item.Properties()
            .tab(ModTabs.GOOSEMOD)
            .food(new FoodProperties.Builder()
                    .nutrition(0)
                    .saturationMod(2)
                    .effect((Supplier<MobEffectInstance>) () -> new MobEffectInstance(ModMobEffects.HIGH, 120000, 0, false, false, true), 1)
                    .effect((Supplier<MobEffectInstance>) () -> new MobEffectInstance(MobEffects.CONFUSION, 1000, 3), 1)
                    .effect((Supplier<MobEffectInstance>) () -> new MobEffectInstance(MobEffects.LEVITATION, 250, 1), 1f)
                    .effect((Supplier<MobEffectInstance>) () -> new MobEffectInstance(MobEffects.BLINDNESS, 1000, 5), 1)
                    .build())).setRegistryName("meth");
    public static final Item GOOSE_EGG = new GooseEggItem(new Item.Properties().stacksTo(16).tab(ModTabs.GOOSEMOD)).setRegistryName("goose_egg");
    public static final Item GOOSE_BREAST = new Item(new Item.Properties().tab(ModTabs.GOOSEMOD)
            .food(new FoodProperties.Builder()
                    .nutrition(4)
                    .saturationMod(0.4F)
                    .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F)
                    .meat()
                    .build()))
            .setRegistryName("goose");
    public static final Item COOKED_GOOSE_BREAST = new Item(new Item.Properties().tab(ModTabs.GOOSEMOD)
            .food(new FoodProperties.Builder()
                    .nutrition(8)
                    .saturationMod(0.8F)
                    .meat()
                    .build()))
            .setRegistryName("cooked_goose");

    // LIQUIDS
    public static final Item NITROGLYCERIN_BUCKET = new Item(new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("nitroglycerin_bucket");

    // NORMAL ITEMS
    public static final Item TOTEM_OF_POLLYMORPHING = new Item(new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD).rarity(Rarity.EPIC)).setRegistryName("totem_of_pollymorphing");
    public static final Item DRAGON_SCALES = new DragonScalesItem().setRegistryName("dragon_scales");

    // ARMOR
    public static final Item DREAM_MASK = new ArmorItem(ModArmorMaterials.DREAM, EquipmentSlot.HEAD,
            new Item.Properties().tab(ModTabs.GOOSEMOD)).setRegistryName("dream_mask");

    // TOOLS
    public static final Item DRAGONITE_SWORD = new SwordItem(ModItemTiers.DRAGONITE, 5, -2.6f,
            new Item.Properties().tab(ModTabs.GOOSEMOD).fireResistant()).setRegistryName("dragonite_sword");
    public static final Item DRAGONITE_SHOVEL = new ShovelItem(ModItemTiers.DRAGONITE, 0, -3.0f,
            new Item.Properties().tab(ModTabs.GOOSEMOD).fireResistant()).setRegistryName("dragonite_shovel");
    public static final Item DRAGONITE_PICKAXE = new PickaxeItem(ModItemTiers.DRAGONITE, 2, -2.8f,
            new Item.Properties().tab(ModTabs.GOOSEMOD).fireResistant()).setRegistryName("dragonite_pickaxe");
    public static final Item DRAGONITE_AXE = new AxeItem(ModItemTiers.DRAGONITE, 10, -3f,
            new Item.Properties().tab(ModTabs.GOOSEMOD).fireResistant()).setRegistryName("dragonite_axe");
    public static final Item DRAGONITE_HOE = new HoeItem(ModItemTiers.DRAGONITE, -5, -0f,
            new Item.Properties().tab(ModTabs.GOOSEMOD).fireResistant()).setRegistryName("dragonite_hoe");
    // ARMOR
    public static final Item DRAGONITE_HELMET = new ArmorItem(ModArmorMaterials.DRAGONITE, EquipmentSlot.HEAD,
            new Item.Properties().tab(ModTabs.GOOSEMOD).fireResistant()).setRegistryName("dragonite_helmet");
    public static final Item DRAGONITE_CHESTPLATE = new ArmorItem(ModArmorMaterials.DRAGONITE, EquipmentSlot.CHEST,
            new Item.Properties().tab(ModTabs.GOOSEMOD).fireResistant()).setRegistryName("dragonite_chestplate");
    public static final Item DRAGONITE_LEGGINGS = new ArmorItem(ModArmorMaterials.DRAGONITE, EquipmentSlot.LEGS,
            new Item.Properties().tab(ModTabs.GOOSEMOD).fireResistant()).setRegistryName("dragonite_leggings");
    public static final Item DRAGONITE_BOOTS = new ArmorItem(ModArmorMaterials.DRAGONITE, EquipmentSlot.FEET,
            new Item.Properties().tab(ModTabs.GOOSEMOD).fireResistant()).setRegistryName("dragonite_boots");
    //TODO: get a structure map and reverse-engineer the nbt data

    // BANNER PATTERNS
    public static final Item DRAGON_PATTERN = new BannerPatternItem(BannerPattern.create("goosemod_dragon", "goosemod_dragon", "goosemod_dragon", true), new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD).rarity(Rarity.EPIC)).setRegistryName("dragon_banner_pattern");
    public static final Item GOOSE_PATTERN = new BannerPatternItem(BannerPattern.create("goosemod_goose", "goosemod_goose", "goosemod_goose", true), new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("goose_banner_pattern");

    // BOATS
    public static final ShipItem.BoatEntitySupplier<ChestBoat> CHEST_BOAT_SUPPLIER = ChestBoat::new;
    public static final ShipItem.BoatEntitySupplier<EnderChestBoat> ENDER_CHEST_BOAT_SUPPLIER = EnderChestBoat::new;
    public static final ShipItem.BoatEntitySupplier<FurnaceBoat> FURNACE_BOAT_SUPPLIER = FurnaceBoat::new;
    public static final ShipItem.BoatEntitySupplier<LongBoat> LONG_BOAT_SUPPLIER = LongBoat::new;
    public static final Item OAK_CHEST_BOAT = new ShipItem<>(CHEST_BOAT_SUPPLIER, Boat.Type.OAK, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("oak_chest_boat");
    public static final Item SPRUCE_CHEST_BOAT = new ShipItem<>(CHEST_BOAT_SUPPLIER, Boat.Type.SPRUCE, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("spruce_chest_boat");
    public static final Item BIRCH_CHEST_BOAT = new ShipItem<>(CHEST_BOAT_SUPPLIER, Boat.Type.BIRCH, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("birch_chest_boat");
    public static final Item JUNGLE_CHEST_BOAT = new ShipItem<>(CHEST_BOAT_SUPPLIER, Boat.Type.JUNGLE, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("jungle_chest_boat");
    public static final Item ACACIA_CHEST_BOAT = new ShipItem<>(CHEST_BOAT_SUPPLIER, Boat.Type.ACACIA, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("acacia_chest_boat");
    public static final Item DARK_OAK_CHEST_BOAT = new ShipItem<>(CHEST_BOAT_SUPPLIER, Boat.Type.DARK_OAK, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("dark_oak_chest_boat");
    public static final Item OAK_ENDER_CHEST_BOAT = new ShipItem<>(ENDER_CHEST_BOAT_SUPPLIER, Boat.Type.OAK, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("oak_ender_chest_boat");
    public static final Item SPRUCE_ENDER_CHEST_BOAT = new ShipItem<>(ENDER_CHEST_BOAT_SUPPLIER, Boat.Type.SPRUCE, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("spruce_ender_chest_boat");
    public static final Item BIRCH_ENDER_CHEST_BOAT = new ShipItem<>(ENDER_CHEST_BOAT_SUPPLIER, Boat.Type.BIRCH, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("birch_ender_chest_boat");
    public static final Item JUNGLE_ENDER_CHEST_BOAT = new ShipItem<>(ENDER_CHEST_BOAT_SUPPLIER, Boat.Type.JUNGLE, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("jungle_ender_chest_boat");
    public static final Item ACACIA_ENDER_CHEST_BOAT = new ShipItem<>(ENDER_CHEST_BOAT_SUPPLIER, Boat.Type.ACACIA, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("acacia_ender_chest_boat");
    public static final Item DARK_OAK_ENDER_CHEST_BOAT = new ShipItem<>(ENDER_CHEST_BOAT_SUPPLIER, Boat.Type.DARK_OAK, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("dark_oak_ender_chest_boat");
    public static final Item OAK_FURNACE_BOAT = new ShipItem<>(FURNACE_BOAT_SUPPLIER, Boat.Type.OAK, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("oak_furnace_boat");
    public static final Item SPRUCE_FURNACE_BOAT = new ShipItem<>(FURNACE_BOAT_SUPPLIER, Boat.Type.SPRUCE, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("spruce_furnace_boat");
    public static final Item BIRCH_FURNACE_BOAT = new ShipItem<>(FURNACE_BOAT_SUPPLIER, Boat.Type.BIRCH, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("birch_furnace_boat");
    public static final Item JUNGLE_FURNACE_BOAT = new ShipItem<>(FURNACE_BOAT_SUPPLIER, Boat.Type.JUNGLE, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("jungle_furnace_boat");
    public static final Item ACACIA_FURNACE_BOAT = new ShipItem<>(FURNACE_BOAT_SUPPLIER, Boat.Type.ACACIA, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("acacia_furnace_boat");
    public static final Item DARK_OAK_FURNACE_BOAT = new ShipItem<>(FURNACE_BOAT_SUPPLIER, Boat.Type.DARK_OAK, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("dark_oak_furnace_boat");
    public static final Item OAK_LONG_BOAT = new ShipItem<>(LONG_BOAT_SUPPLIER, Boat.Type.OAK, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("oak_long_boat");
    public static final Item SPRUCE_LONG_BOAT = new ShipItem<>(LONG_BOAT_SUPPLIER, Boat.Type.SPRUCE, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("spruce_long_boat");
    public static final Item BIRCH_LONG_BOAT = new ShipItem<>(LONG_BOAT_SUPPLIER, Boat.Type.BIRCH, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("birch_long_boat");
    public static final Item JUNGLE_LONG_BOAT = new ShipItem<>(LONG_BOAT_SUPPLIER, Boat.Type.JUNGLE, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("jungle_long_boat");
    public static final Item ACACIA_LONG_BOAT = new ShipItem<>(LONG_BOAT_SUPPLIER, Boat.Type.ACACIA, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("acacia_long_boat");
    public static final Item DARK_OAK_LONG_BOAT = new ShipItem<>(LONG_BOAT_SUPPLIER, Boat.Type.DARK_OAK, new Item.Properties().stacksTo(1).tab(ModTabs.GOOSEMOD)).setRegistryName("dark_oak_long_boat");

    public static void addLore(ItemStack stack, String tag) {
        CompoundTag nbt = stack.getTag();
        CompoundTag display;
        ListTag lore;
        if (nbt.get("display") != null) {
            display = (CompoundTag) nbt.get("display");
            if (display.get("Lore") != null) {
                lore = (ListTag) display.get("Lore");
            } else {
                lore = new ListTag();
            }
        } else {
            lore = new ListTag();
            display = new CompoundTag();
        }
        lore.add(StringTag.valueOf("{\"text\": \"[tag]\"}".replace("[tag]", tag)));
        display.put("Lore", lore);
        nbt.put("display", display);
        stack.setTag(nbt);
    }

    public static class Tags {
        public static Tag<Item> CHEST_BOATS = ItemTags.createOptional(new ResourceLocation(GooseMod.MODID, "chest_boats"));
        public static Tag<Item> FURNACE_BOATS = ItemTags.createOptional(new ResourceLocation(GooseMod.MODID, "furnace_boats"));
        public static Tag<Item> ENDER_CHEST_BOATS = ItemTags.createOptional(new ResourceLocation(GooseMod.MODID, "ender_chest_boats"));
        public static Tag<Item> LONG_BOATS = ItemTags.createOptional(new ResourceLocation(GooseMod.MODID, "long_boats"));
    }
}
