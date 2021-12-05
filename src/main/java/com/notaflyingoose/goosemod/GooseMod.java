package com.notaflyingoose.goosemod;

import com.google.common.collect.ImmutableMap;
import com.notaflyingoose.goosemod.blocks.ModBlocks;
import com.notaflyingoose.goosemod.entities.ModEntityTypes;
import com.notaflyingoose.goosemod.entities.boss.herobrine.HerobrineBoss;
import com.notaflyingoose.goosemod.fluids.NitroglycerinFluid;
import com.notaflyingoose.goosemod.items.ModItems;
import com.notaflyingoose.goosemod.world.ModWorldGeneration;
import com.notaflyingoose.goosemod.world.enchantment.ThunderEnchantment;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(GooseMod.MODID)
public class GooseMod {
    public static final String MODID = "goosemod";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public GooseMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModWorldGeneration.registerConfiguredFeatures();
            AxeItem.STRIPPABLES = new ImmutableMap.Builder<Block, Block>()
                    .putAll(AxeItem.STRIPPABLES)
                    .put(ModBlocks.MAPLE_LOG, ModBlocks.STRIPPED_MAPLE_LOG)
                    .put(ModBlocks.MAPLE_WOOD, ModBlocks.STRIPPED_MAPLE_WOOD)
                    .build();
            Minecraft.getInstance().getBlockColors().register(
                    (state, tint, pos, number) -> 0xff4422,
                    ModBlocks.RED_MAPLE_LEAVES);
            Minecraft.getInstance().getBlockColors().register(
                    (state, tint, pos, number) -> 0xff7700,
                    ModBlocks.ORANGE_MAPLE_LEAVES);
            Minecraft.getInstance().getBlockColors().register(
                    (state, tint, pos, number) -> 0xffAA00,
                    ModBlocks.YELLOW_MAPLE_LEAVES);
            Minecraft.getInstance().getItemColors().register(
                    (stack, number) -> 0xff4422,
                    ModItems.RED_MAPLE_LEAVES);
            Minecraft.getInstance().getItemColors().register(
                    (stack, number) -> 0xff7700,
                    ModItems.ORANGE_MAPLE_LEAVES);
            Minecraft.getInstance().getItemColors().register(
                    (stack, number) -> 0xffAA00,
                    ModItems.YELLOW_MAPLE_LEAVES);
            //BiomeManager.addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(ModWorldGeneration.Biomes.MAPLE_BIOME, 10));
            BiomeManager.addAdditionalOverworldBiomes(ModWorldGeneration.Biomes.MAPLE_BIOME);
        });
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    static class EventListener {

        @SubscribeEvent
        public static void blockPlaceEvent(BlockEvent.EntityPlaceEvent event) {
            if (!event.getPlacedBlock().is(Blocks.FIRE))
                return;
            Level level = (Level) event.getWorld();
            if (event.getPos().getY() < level.getMaxBuildHeight() && event.getPos().getY() > level.getMinBuildHeight() && level.hasChunkAt(event.getPos()) && level.getDifficulty() != Difficulty.PEACEFUL) {
                BlockPattern blockpattern = HerobrineBoss.getOrCreateShrine();
                BlockPattern.BlockPatternMatch patternMatch = blockpattern.find(level, event.getPos());
                if (patternMatch != null) {
                    for(int i = 0; i < blockpattern.getWidth(); ++i) {
                        for(int j = 0; j < blockpattern.getHeight(); ++j) {
                            BlockInWorld blockinworld = patternMatch.getBlock(i, j, 0);
                            level.setBlock(blockinworld.getPos(), Blocks.AIR.defaultBlockState(), 2);
                            level.levelEvent(2001, blockinworld.getPos(), Block.getId(blockinworld.getState()));
                        }
                    }

                    LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(level);
                    lightningbolt.moveTo(Vec3.atBottomCenterOf(event.getPos()));
                    level.addFreshEntity(lightningbolt);

                    if (level.getEntitiesOfClass(HerobrineBoss.class, AABB.unitCubeFromLowerCorner(Vec3.atBottomCenterOf(event.getPos())).inflate(64, 10.0D, 64)).isEmpty()) {
                        HerobrineBoss herobrine = ModEntityTypes.HEROBRINE.create(level);
                        BlockPos blockpos = patternMatch.getBlock(1, 3, 1).getPos();
                        herobrine.moveTo(blockpos.getX() + 0.5D, blockpos.getY() + 0.55D, blockpos.getZ() + 0.5D, patternMatch.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
                        herobrine.yBodyRot = patternMatch.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
                        for (ServerPlayer serverplayer : level.getEntitiesOfClass(ServerPlayer.class, herobrine.getBoundingBox().inflate(50.0D))) {
                            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, herobrine);
                        }
                        level.addFreshEntity(herobrine);
                        for(int k = 0; k < blockpattern.getWidth(); ++k) {
                            for(int l = 0; l < blockpattern.getHeight(); ++l) {
                                for(int m = 0; m < blockpattern.getDepth(); ++m) {
                                    level.setBlockAndUpdate(patternMatch.getBlock(k, l, m).getPos(), Blocks.AIR.defaultBlockState());
                                }
                            }
                        }
                    }

                }
            }
        }

        @SubscribeEvent
        public static void playerRightClickItem(PlayerInteractEvent.RightClickBlock event) {
            Level level = event.getWorld();
            ItemStack itemstack = event.getItemStack();
            if (!itemstack.is(Items.FLINT_AND_STEEL))
                return;
            if (!level.isClientSide)
                NitroglycerinFluid.ignite(level, event.getPos(), event.getPlayer());
        }

        @SubscribeEvent
        public static void playerHitBlock(PlayerInteractEvent.LeftClickBlock event) {
            Level level = event.getWorld();
            ItemStack itemstack = event.getItemStack();
            if (itemstack.is(Items.AIR))
                return;
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemstack);
            for (Enchantment e : enchantments.keySet()) {
                if (!(e instanceof ThunderEnchantment))
                    continue;
                for (int i = 0; i < enchantments.get(e); i++) {
                    LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(level);
                    lightningbolt.moveTo(Vec3.atBottomCenterOf(event.getPos()));
                    level.addFreshEntity(lightningbolt);
                }

                if (enchantments.get(e) > 2) {
                    List<LivingEntity> entities = level.getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat(), event.getPlayer(),
                            event.getPlayer().getBoundingBox().inflate(20.0D, 8.0D, 20.0D));
                    entities.stream().sorted((e1, e2) -> (int) (e1.distanceToSqr(event.getPlayer()) - e2.distanceToSqr(event.getPlayer())))
                            .forEach(new Consumer<>() {
                                int times = 0;

                                @Override
                                public void accept(LivingEntity livingEntity) {
                                    if (times >= 3)
                                        return;
                                    LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(level);
                                    lightningbolt.moveTo(Vec3.atBottomCenterOf(livingEntity.getOnPos()));
                                    level.addFreshEntity(lightningbolt);
                                    times++;
                                }
                            });
                }
                return;
            }
        }

        @SubscribeEvent
        public static void livingEntityAttack(LivingAttackEvent event) {
            String sourceId = event.getSource().getMsgId();
            if (!sourceId.equals("player") && !sourceId.equals("mob") && !sourceId.equals("generic") || !(event.getSource() instanceof EntityDamageSource))
                return;
            EntityDamageSource source = (EntityDamageSource) event.getSource();
            LivingEntity attacking = (LivingEntity) event.getEntity();
            if (attacking.isDeadOrDying())
                return;
            LivingEntity attacker = (LivingEntity) source.getEntity();

            ItemStack holding = attacker.getMainHandItem();

            //check enchants
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(holding);

            for (Enchantment e : enchantments.keySet()) {
                if (e instanceof ThunderEnchantment) {
                    for (int i = 0; i < enchantments.get(e); i++) {
                        LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(attacking.level);
                        lightningbolt.moveTo(Vec3.atBottomCenterOf(attacking.getOnPos()));
                        attacking.level.addFreshEntity(lightningbolt);
                    }
                }
            }

            if (holding.is(ModItems.TOTEM_OF_POLLYMORPHING)) {
                if (attacking instanceof Enemy || attacking instanceof Player) {
                    //attacking.remove(Entity.RemovalReason.DISCARDED);
                    attacking.hurt(DamageSource.indirectMagic(attacking, attacker), Float.MAX_VALUE);
                    if (attacking.isDeadOrDying()) {
                        Parrot parrot = EntityType.PARROT.create(attacking.level);
                        parrot.setPosRaw(attacking.getX(), attacking.getY() + attacking.getEyeHeight(), attacking.getZ());
                        parrot.setXRot(attacking.getXRot());
                        parrot.setYRot(attacking.getYRot());
                        parrot.setCustomName(attacking.getName());
                        attacker.level.addFreshEntity(parrot);
                        attacker.addEffect(new MobEffectInstance(MobEffects.POISON, 5, 10));
                    }
                    if (attacker instanceof Player)
                        Minecraft.getInstance().gameRenderer.displayItemActivation(holding.copy());
                    holding.shrink(1);
                } else if (attacker instanceof Player player) {
                    player.displayClientMessage(new TranslatableComponent("item.goosemod.totem_of_pollymorphing.not_monster"), true);
                    event.setCanceled(true);
                }
            }
        }
    }

}
