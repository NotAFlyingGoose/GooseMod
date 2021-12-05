package com.notaflyingoose.goosemod.entities;

import com.google.common.collect.Lists;
import com.notaflyingoose.goosemod.blocks.ModBlockEntityTypes;
import com.notaflyingoose.goosemod.blocks.NetherReactorCore;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class NetherReactorCoreBlockEntity extends BlockEntity {
    private final PiglinSpawner spawner = new PiglinSpawner();
    private long initializedTicks = 0;

    public NetherReactorCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.NETHER_REACTOR_CORE, pos, state);
    }

    public void load(CompoundTag nbt) {
        super.load(nbt);
    }

    public CompoundTag save(CompoundTag nbt) {
        super.save(nbt);
        return nbt;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, NetherReactorCoreBlockEntity blockEntity) {
        if (state.getValue(NetherReactorCore.REACTOR_STATE) == NetherReactorCore.ReactorState.INITIALIZED) {
            blockEntity.spawner.clientTick(level, pos);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, NetherReactorCoreBlockEntity blockEntity) {
        if (state.getValue(NetherReactorCore.REACTOR_STATE) == NetherReactorCore.ReactorState.INITIALIZED) {
            blockEntity.initializedTicks++;
            blockEntity.spawner.serverTick((ServerLevel) level, pos);
            if (blockEntity.initializedTicks > 900) {
                //NetherReactorCore.loadSpire((ServerLevel) level, pos, 0.7f);
                NetherReactorCore.setReactorState(level, pos, state, NetherReactorCore.ReactorState.FINISHED);
            }
        }
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 1, this.getUpdateTag());
    }

    public CompoundTag getUpdateTag() {
        CompoundTag compoundtag = this.save(new CompoundTag());
        compoundtag.remove("SpawnPotentials");
        return compoundtag;
    }

    public boolean triggerEvent(int p_59797_, int p_59798_) {
        return this.spawner.onEventTriggered(this.level, p_59797_) ? true : super.triggerEvent(p_59797_, p_59798_);
    }

    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public class PiglinSpawner {
        private static final Logger LOGGER = LogManager.getLogger();
        private int spawnDelay = 20;
        private final int minSpawnDelay = 300;
        private final int maxSpawnDelay = 400;
        private final int spawnCount = 5;
        private final int lootCount = 5;
        private final int maxNearbyEntities = 15;
        private final int requiredPlayerRange = 16;
        private final int spawnRange = 8;
        private final Random random = new Random();

        private boolean isNearPlayer(Level p_151344_, BlockPos p_151345_) {
            return p_151344_.hasNearbyAlivePlayer((double)p_151345_.getX() + 0.5D, (double)p_151345_.getY() + 0.5D, (double)p_151345_.getZ() + 0.5D, (double)this.requiredPlayerRange);
        }

        public void clientTick(Level level, BlockPos pos) {
            if (this.isNearPlayer(level, pos)) {
                NetherReactorCore.spawnParticles(level, pos, ParticleTypes.ASH);
                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                }
            }
        }

        public void serverTick(ServerLevel level, BlockPos pos) {
            if (this.isNearPlayer(level, pos)) {
                if (this.spawnDelay == -1) {
                    this.delay(level, pos);
                }

                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                } else {
                    boolean flag = false;

                    for(int i = 0; i < this.spawnCount; ++i) {
                        double d0 = (double)pos.getX() + (level.random.nextDouble() - level.random.nextDouble()) * (double)this.spawnRange + 0.5D;
                        double d1 = pos.getY() + level.random.nextInt(3) - 1;
                        double d2 = (double)pos.getZ() + (level.random.nextDouble() - level.random.nextDouble()) * (double)this.spawnRange + 0.5D;
                        if (level.noCollision(EntityType.ZOMBIFIED_PIGLIN.getAABB(d0, d1, d2)) && SpawnPlacements.checkSpawnRules(EntityType.ITEM, level, MobSpawnType.SPAWNER, new BlockPos(d0, d1, d2), level.getRandom())) {
                            ZombifiedPiglin piglin = EntityType.ZOMBIFIED_PIGLIN.create(level);
                            if (piglin == null) {
                                this.delay(level, pos);
                                return;
                            }
                            piglin.moveTo(d0, d1, d2);

                            int k = level.getEntitiesOfClass(piglin.getClass(), (new AABB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).inflate((double)this.spawnRange)).size();
                            if (k >= this.maxNearbyEntities) {
                                this.delay(level, pos);
                                return;
                            }

                            piglin.moveTo(piglin.getX(), piglin.getY(), piglin.getZ(), level.random.nextFloat() * 360.0F, 0.0F);
                            piglin.finalizeSpawn(level, level.getCurrentDifficultyAt(piglin.blockPosition()), MobSpawnType.SPAWNER, null, null);
                            piglin.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
                            if (NetherReactorCoreBlockEntity.this.level.getDifficulty() == Difficulty.HARD) {
                                piglin.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
                                piglin.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
                                piglin.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
                                piglin.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
                            }
                            Player nearestPlayer = NetherReactorCoreBlockEntity.this.level.getNearestPlayer(TargetingConditions.forCombat().selector(LivingEntity::canBeSeenAsEnemy), (float)piglin.getX(), (float)piglin.getY(), (float)piglin.getZ());
                            if (nearestPlayer != null)
                                piglin.setTarget(nearestPlayer);

                            if (!level.tryAddFreshEntityWithPassengers(piglin)) {
                                this.delay(level, pos);
                                return;
                            }

                            level.levelEvent(2004, pos, 0);
                            piglin.spawnAnim();

                            flag = true;
                        }
                    }

                    for(int i = 0; i < this.lootCount; ++i) {
                        double d0 = (double)pos.getX() + (level.random.nextDouble() - level.random.nextDouble()) * (double)this.spawnRange + 0.5D;
                        double d1 = (pos.getY() + level.random.nextInt(3) - 1);
                        double d2 = (double)pos.getZ() + (level.random.nextDouble() - level.random.nextDouble()) * (double)this.spawnRange + 0.5D;
                        if (level.noCollision(EntityType.PIG.getAABB(d0, d1, d2)) && SpawnPlacements.checkSpawnRules(EntityType.ITEM, level, MobSpawnType.SPAWNER, new BlockPos(d0, d1, d2), level.getRandom())) {
                            ItemEntity entity = new ItemEntity(level, d0, d1, d2, new ItemStack(getLootItem()));
                            entity.setDefaultPickUpDelay();
                            entity.setExtendedLifetime();
                            level.addFreshEntity(entity);
                        }
                    }

                    if (flag) {
                        this.delay(level, pos);
                    }

                }
            }
        }

        private Item getLootItem() {
            switch(random.nextInt(5)) {
                case 0 -> {
                    if (random.nextInt(2) == 0) {
                        if (random.nextInt(20) == 0) {
                            return Items.NETHER_STAR;
                        }
                        if (random.nextInt(4) == 0) {
                            return Items.NETHERITE_INGOT;
                        }
                        return Items.NETHERITE_SCRAP;
                    }
                    return Items.QUARTZ;
                }
                case 1 -> {
                    return switch (random.nextInt(9)) {
                        case 1 -> Items.RED_MUSHROOM;
                        case 2 -> Items.BROWN_MUSHROOM;
                        case 3 -> Items.SUGAR_CANE;
                        case 4 -> Items.BOW;
                        case 5 -> Items.BOWL;
                        case 6 -> Items.PUMPKIN_SEEDS;
                        case 7 -> Items.MELON_SEEDS;
                        default -> Items.NETHER_BRICK;
                    };
                }
                case 2 -> {
                    if (random.nextInt(4) == 0)
                        return Items.DIAMOND_BLOCK;
                    return Items.GOLD_BLOCK;
                }
                case 3 -> {
                    if (random.nextInt(4) == 0) {
                        if (random.nextInt(2) == 0)
                            return Items.GOLDEN_AXE;
                        else
                            return Items.GOLDEN_SWORD;
                    }
                    if (random.nextInt(4) == 0) {
                        return switch (random.nextInt(4)) {
                            case 1 -> Items.GOLDEN_HELMET;
                            case 2 -> Items.GOLDEN_CHESTPLATE;
                            case 3 -> Items.GOLDEN_LEGGINGS;
                            default -> Items.GOLDEN_BOOTS;
                        };
                    }
                    if (random.nextInt(2) == 0) {
                        return Items.GOLD_BLOCK;
                    }
                    return Items.GOLD_INGOT;
                }
                case 4 -> {
                    if (random.nextInt(2) == 0) {
                        return Items.QUARTZ_BLOCK;
                    }
                    return Items.QUARTZ;
                }
            };
            return Items.GLOWSTONE_DUST;
        }

        private void delay(Level p_151351_, BlockPos p_151352_) {
            if (this.maxSpawnDelay <= this.minSpawnDelay) {
                this.spawnDelay = this.minSpawnDelay;
            } else {
                this.spawnDelay = this.minSpawnDelay + this.random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
            }

            this.broadcastEvent(p_151351_, p_151352_, 1);
        }

        public boolean onEventTriggered(Level p_151317_, int p_151318_) {
            if (p_151318_ == 1) {
                if (p_151317_.isClientSide) {
                    this.spawnDelay = this.minSpawnDelay;
                }

                return true;
            } else {
                return false;
            }
        }

        public void broadcastEvent(Level p_151322_, BlockPos p_151323_, int p_151324_) {
            p_151322_.blockEvent(p_151323_, Blocks.SPAWNER, p_151324_, 0);
        }
    }
}
