package com.notaflyingoose.goosemod.entities;

import com.notaflyingoose.goosemod.blocks.ModBlocks;
import com.notaflyingoose.goosemod.entities.boss.herobrine.HerobrineBoss;
import com.notaflyingoose.goosemod.entities.vehicle.ChestBoat;
import com.notaflyingoose.goosemod.entities.vehicle.EnderChestBoat;
import com.notaflyingoose.goosemod.entities.vehicle.FurnaceBoat;
import com.notaflyingoose.goosemod.entities.vehicle.LongBoat;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModEntityTypes {
    public static final EntityType<Crewmate> CREWMATE = (EntityType<Crewmate>) EntityType.Builder.of(Crewmate::new, MobCategory.CREATURE)
            .sized(1, 1.5f)
            .clientTrackingRange(10)
            .build("crewmate")
            .setRegistryName("crewmate");
    public static final EntityType<Imposter> IMPOSTER = (EntityType<Imposter>) EntityType.Builder.of(Imposter::new, MobCategory.MONSTER)
            .sized(1, 1.5f)
            .build("imposter")
            .setRegistryName("imposter");
    public static final EntityType<Heisenberg> HEISENBERG = (EntityType<Heisenberg>) EntityType.Builder.of(Heisenberg::new, MobCategory.MISC)
            .sized(0.6F, 1.95F)
            .build("heisenberg")
            .setRegistryName("heisenberg");
    public static final EntityType<HerobrineBoss> HEROBRINE = (EntityType<HerobrineBoss>) EntityType.Builder.of(HerobrineBoss::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(0.6F, 1.95F)
            .build("herobrine")
            .setRegistryName("herobrine");
    public static final EntityType<RedDragon> RED_DRAGON = (EntityType<RedDragon>) EntityType.Builder.of(RedDragon::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(10.0F, 8.0F)
            .build("red_dragon")
            .setRegistryName("red_dragon");
    public static final EntityType<DragonSkeleton> DRAGON_SKELETON = (EntityType<DragonSkeleton>) EntityType.Builder.of(DragonSkeleton::new, MobCategory.MONSTER)
            .fireImmune()
            .sized(10.0F, 8.0F)
            .build("dragon_skeleton")
            .setRegistryName("dragon_skeleton");
    public static final EntityType<PrimedC4> C4 = (EntityType<PrimedC4>) EntityType.Builder.of(PrimedC4::new, MobCategory.MISC)
            .fireImmune()
            .sized(0.98F, 0.98F)
            .clientTrackingRange(10)
            .updateInterval(10)
            .build("c4")
            .setRegistryName("c4");
    public static final EntityType<Lemur> LEMUR = (EntityType<Lemur>) EntityType.Builder.of(Lemur::new, MobCategory.CREATURE)
            .sized(0.6F, 0.7F)
            .clientTrackingRange(10)
            .build("lemur")
            .setRegistryName("lemur");
    public static final EntityType<Goose> GOOSE = (EntityType<Goose>) EntityType.Builder.of(Goose::new, MobCategory.CREATURE)
            .sized(0.9F, 1.4F)
            .clientTrackingRange(10)
            .build("goose")
            .setRegistryName("goose");
    public static final EntityType<ThrownGooseEgg> GOOSE_EGG = (EntityType<ThrownGooseEgg>) EntityType.Builder.<ThrownGooseEgg>of(ThrownGooseEgg::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build("goose_egg")
            .setRegistryName("goose_egg");
    public static final EntityType<Mimic> MIMIC = (EntityType<Mimic>) EntityType.Builder.of(Mimic::new, MobCategory.MONSTER)
            .sized(0.9375f, 0.9375f)
            .build("mimic")
            .setRegistryName("mimic");
/*    public static final EntityType<CopperGolem> COPPER_GOLEM = (EntityType<CopperGolem>) EntityType.Builder.of(CopperGolem::new, MobCategory.CREATURE)
            .sized(1F, 1F)
            .clientTrackingRange(10)
            .build("copper_golem")
            .setRegistryName("copper_golem");*/

    public static final EntityType<ChestBoat> CHEST_BOAT = (EntityType<ChestBoat>) EntityType.Builder.<ChestBoat>of(ChestBoat::new, MobCategory.MISC)
            .sized(1.375F, 0.5625F)
            .clientTrackingRange(10)
            .build("chest_boat")
            .setRegistryName("chest_boat");
    public static final EntityType<EnderChestBoat> ENDER_CHEST_BOAT = (EntityType<EnderChestBoat>) EntityType.Builder.<EnderChestBoat>of(EnderChestBoat::new, MobCategory.MISC)
            .sized(1.375F, 0.5625F)
            .clientTrackingRange(10)
            .build("ender_chest_boat")
            .setRegistryName("ender_chest_boat");
    public static final EntityType<FurnaceBoat> FURNACE_BOAT = (EntityType<FurnaceBoat>) EntityType.Builder.<FurnaceBoat>of(FurnaceBoat::new, MobCategory.MISC)
            .sized(1.375F, 0.5625F)
            .clientTrackingRange(10)
            .build("furnace_boat")
            .setRegistryName("furnace_boat");
    public static final EntityType<LongBoat> LONG_BOAT = (EntityType<LongBoat>) EntityType.Builder.<LongBoat>of(LongBoat::new, MobCategory.MISC)
            .sized(2.375F, 0.5625F)
            .clientTrackingRange(10)
            .build("long_boat")
            .setRegistryName("long_boat");

}
