package com.notaflyingoose.goosemod.entities;

import com.google.common.collect.Sets;
import com.notaflyingoose.goosemod.GooseMod;
import com.notaflyingoose.goosemod.client.model.RedDragonPart;
import com.notaflyingoose.goosemod.effects.ModMobEffects;
import com.notaflyingoose.goosemod.world.ModSoundEvents;
import com.notaflyingoose.goosemod.world.ModWorldGeneration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber(modid = GooseMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RedDragon extends Monster implements FlyingAnimal, RangedAttackMob {
    private RangedAttackGoal breathFireGoal;
    private MeleeAttackGoal meleeAttackGoal;
    private static final Logger LOGGER = LogManager.getLogger();
    private final HashSet<ServerPlayer> addToBossFight = new HashSet<>();
    private final ServerBossEvent bossEvent;
    protected static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(20.0D)
            .selector(LivingEntity::attackable);
    protected static final TargetingConditions DRAGON_CONDITIONS = TargetingConditions.forCombat().range(20.0D)
            .selector((entity) -> entity instanceof RedDragon);
    private static final EntityDataAccessor<Integer> DATA_DRAGON_BREATH_FIRE_TICKS = SynchedEntityData.defineId(RedDragon.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DRAGON_ANGRY = SynchedEntityData.defineId(RedDragon.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DRAGON_SLEEPING = SynchedEntityData.defineId(RedDragon.class, EntityDataSerializers.BOOLEAN);
    private static final HashSet<Block> PRECIOUS = Sets.newHashSet(Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.RAW_GOLD_BLOCK,
            Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.EMERALD_BLOCK, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.NETHERITE_BLOCK, Blocks.ANCIENT_DEBRIS);;
    public static final int TICKS_PER_FLAP = Mth.ceil(2.4166098F);
    public float oFlapTime;
    public float flapTime;
    private final RedDragonPart[] subEntities;
    public final RedDragonPart head;
    private BlockPos targetPosition;
    private RedDragon.DragonTalkGoal calmTalking;

    public RedDragon(EntityType<? extends Monster> entity, Level level) {
        super(entity, level);
        if (level.isClientSide) {
            this.breathFireGoal = new RangedAttackGoal(this, 1, 50, 20.0F) {
                @Override
                public boolean canUse() {
                    return super.canUse() && RedDragon.this.isAngry() && !RedDragon.this.isDragonSleeping();
                }

                @Override
                public void start() {
                    super.start();
                    level.broadcastEntityEvent(RedDragon.this, (byte) 100);
                }
            };
            this.meleeAttackGoal = new MeleeAttackGoal(this, 1, true) {
                @Override
                public boolean canUse() {
                    return super.canUse() && RedDragon.this.isAngry() && !RedDragon.this.isDragonSleeping();
                }

                @Override
                public void start() {
                    super.start();
                    level.broadcastEntityEvent(RedDragon.this, (byte) 101);
                }
            };
        }
        this.bossEvent = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);
        this.setHealth(this.getMaxHealth());
        this.moveControl = new FlyingMoveControl(this, 1, false);
        this.head = new RedDragonPart(this, "head", 1, 1);
        this.subEntities = new RedDragonPart[]{this.head};
        this.reassessAttackGoal();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.breathFireGoal = new RangedAttackGoal(this, 1, 50, 20.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && RedDragon.this.isAngry() && !RedDragon.this.isDragonSleeping();
            }

            @Override
            public void start() {
                super.start();
                level.broadcastEntityEvent(RedDragon.this, (byte) 100);
            }
        };
        this.meleeAttackGoal = new MeleeAttackGoal(this, 1, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && RedDragon.this.isAngry() && !RedDragon.this.isDragonSleeping();
            }

            @Override
            public void start() {
                super.start();
                level.broadcastEntityEvent(RedDragon.this, (byte) 101);
            }
        };

        //this.goalSelector.addGoal(1, new RedDragon.GlideAroundGoal(this));
        //this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.5));
        //this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        LOGGER.info(this.breathFireGoal);
        LOGGER.info(this.meleeAttackGoal);
        this.goalSelector.addGoal(1, this.meleeAttackGoal);
        this.goalSelector.addGoal(2, new RedDragon.DragonTalkGoal(this, "goosemod.red_dragon.talk.angry", 10, true));
        this.calmTalking = new RedDragon.DragonTalkGoal(this, "goosemod.red_dragon.talk.calm", 10, false);
        this.goalSelector.addGoal(2, this.calmTalking);
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.5F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !RedDragon.this.isDragonSleeping();
            }
        });
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 64, 0.5F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !RedDragon.this.isDragonSleeping();
            }
        });
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Animal.class, 64, 0.5F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !RedDragon.this.isDragonSleeping();
            }
        });
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !RedDragon.this.isDragonSleeping();
            }
        });

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, EnderDragon.class, 64, false, false, LivingEntity::attackable));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 64, false, false, LivingEntity::attackable));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EnderMan.class, 64, false, false, LivingEntity::attackable));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, RedDragon.class, 64, false, false, LivingEntity::attackable));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, 64, true, false, LivingEntity::attackable));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, 64, true, false, LivingEntity::attackable));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Animal.class, 64, true, false, LivingEntity::attackable));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, AgeableMob.class, 32, true, false, LivingEntity::attackable));

    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DRAGON_BREATH_FIRE_TICKS, 0);
        this.entityData.define(DATA_DRAGON_ANGRY, false);
        this.entityData.define(DATA_DRAGON_SLEEPING, false);
    }

    public void handleEntityEvent(byte event) {
        if (event == 100) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSoundEvents.DRAGON_BREATH, this.getSoundSource(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
        } else if (event == 101) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSoundEvents.DRAGON_ROAR, this.getSoundSource(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
        } else {
            super.handleEntityEvent(event);
        }
    }

    public int getBreathFireTicks() {
        return this.entityData.get(DATA_DRAGON_BREATH_FIRE_TICKS);
    }

    public void setBreathFireTicks(int ticks) {
        this.entityData.set(DATA_DRAGON_BREATH_FIRE_TICKS, ticks);
    }

    public boolean isAngry() {
        return this.entityData.get(DATA_DRAGON_ANGRY);
    }

    public void setAngry(boolean angry) {
        this.entityData.set(DATA_DRAGON_ANGRY, angry);
    }

    public boolean isDragonSleeping() {
        return this.entityData.get(DATA_DRAGON_SLEEPING);
    }

    public void setDragonSleeping(boolean sleeping) {
        this.entityData.set(DATA_DRAGON_SLEEPING, sleeping);
    }

    public void reassessAttackGoal() {
        if (!this.level.isClientSide) {
            LivingEntity target = this.getTarget();
            if (target != null)
                //LOGGER.info(target.distanceToSqr(this));
                if (target != null && target.distanceToSqr(this) > 8 * 8) {
                    AtomicBoolean has = new AtomicBoolean(false);
                    this.goalSelector.getAvailableGoals().forEach(wrappedGoal -> {
                        if (wrappedGoal.getGoal().equals(this.meleeAttackGoal))
                            has.set(true);
                    });
                    if (!has.get())
                        return;
                    this.goalSelector.removeGoal(this.meleeAttackGoal);
                    this.goalSelector.addGoal(1, this.breathFireGoal);
                    LOGGER.info("breath fire goal");
                } else {
                    AtomicBoolean has = new AtomicBoolean(false);
                    this.goalSelector.getAvailableGoals().forEach(wrappedGoal -> {
                        if (wrappedGoal.getGoal().equals(this.breathFireGoal))
                            has.set(true);
                    });
                    if (!has.get())
                        return;
                    this.goalSelector.removeGoal(this.breathFireGoal);
                    this.goalSelector.addGoal(1, this.meleeAttackGoal);
                    LOGGER.info("melee attack goal");
                }

        }
    }

    public void performRangedAttack(LivingEntity target, float baseDamage) {
        float dist = target.distanceTo(target);
        setBreathFireTicks(100);
        BlockPos on = getOnPos();
        Vec3 view = this.getViewVector(1.0F);
        double tx = target.getX() - (this.getX() + view.x * 4.0D);
        double ty = target.getY(0.5D) - (0.5D + this.getY(0.5D));
        double tz = target.getZ() - (this.getZ() + view.z * 4.0D);
        for (int i = 0; i < 20; i++) {
            double rx = (random.nextDouble() - random.nextDouble()) * 0.3;
            double ry = (random.nextDouble() - random.nextDouble()) * 0.3;
            double rz = (random.nextDouble() - random.nextDouble()) * 0.3;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(on.getX() + (tx + rx) * dist, on.getY() + (ty + ry) * dist, on.getZ() + (tz + rz) * dist);
            BlockState state = level.getBlockState(pos);
            BlockState below = level.getBlockState(pos.below());

            for (int j = 0; j < 16; j++) {
                if (state.isAir() || !state.getFluidState().isEmpty()) {
                    if (below.isAir() || !below.getFluidState().isEmpty()) {
                        pos.move(0, -1, 0);
                        state = below;
                        below = level.getBlockState(pos.below());
                    } else
                        break;
                } else {
                    pos.move(0, 1, 0);
                    state = level.getBlockState(pos.above());
                }
            }
            if (state.isAir()) {
                level.setBlock(pos.above(), Blocks.FIRE.defaultBlockState(), 3);
            } else if (!state.getFluidState().isEmpty() && state.getMaterial() != Material.LAVA) {
                level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
            } else {
                level.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
            }
        }
        if (!this.isSilent()) {
            level.levelEvent((Player)null, 1016, this.blockPosition(), 0);
        }

        LargeFireball largefireball = new LargeFireball(level, this, tx, ty, tz, 4);
        largefireball.setPos(this.getX() + view.x * 4.0D, this.getY(0.5D) + 0.5D, largefireball.getZ() + view.z * 4.0D);
        level.addFreshEntity(largefireball);

        if (dist > 8) {
            this.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.5);
        }
    }

    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }

    protected PathNavigation createNavigation(Level p_29417_) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_29417_);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    public boolean isFlying() {
        return !this.onGround && !isDragonSleeping();
    }

    public boolean isFlapping() {
        float f = Mth.cos(this.flapTime * ((float)Math.PI * 2F));
        float f1 = Mth.cos(this.oFlapTime * ((float)Math.PI * 2F));
        return f1 <= -0.3F && f >= -0.3F;
    }

    public boolean isPushable() {
        return false;
    }

    protected void doPush(Entity p_27415_) {
    }

    protected void pushEntities() {
    }

    public static AttributeSupplier createCustomAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 200)
                .add(Attributes.MOVEMENT_SPEED, 1)
                .add(Attributes.FLYING_SPEED, 1.5)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.ATTACK_DAMAGE, 7)
                .add(Attributes.ATTACK_KNOCKBACK, 0)
                .build();
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        DragonSkeleton skeleton = ModEntityTypes.DRAGON_SKELETON.create(level);
        skeleton.setPosRaw(this.getX(), this.getY() + 0.5, this.getZ());
        skeleton.setXRot(this.getXRot());
        skeleton.setYRot(this.getYRot());
        skeleton.setYBodyRot(this.yBodyRot);
        skeleton.setYHeadRot(this.yHeadRot);
        skeleton.setDeltaMovement(this.getDeltaMovement().multiply(5, 5, 5));
        level.addFreshEntity(skeleton);
    }

    public void tick() {
        super.tick();
    }

    private void tickPart(RedDragonPart part, double xOffset, double yOffset, double zOffset) {
        part.setPos(this.getX() + xOffset, this.getY() + yOffset, this.getZ() + zOffset);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return this.subEntities;
    }

    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.getHealth() < this.getMaxHealth() && this.tickCount % 50 == 0) {
            this.heal(1.0F);
        }
        bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.isAngry()) {
            for (ServerPlayer player : addToBossFight) {
                bossEvent.addPlayer(player);
                addToBossFight.remove(player);
            }
        }

        if (isNoAi() || isDragonSleeping() || Minecraft.getInstance().isPaused())
            return;

        if (this.isFlying()) {
            if (this.targetPosition != null && (!this.level.isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= this.level.getMinBuildHeight()) && this.random.nextInt(10) == 0 ) {
                this.targetPosition = null;
            }

            if (this.targetPosition == null|| this.targetPosition.closerThan(this.position(), 2.0D)) {
                LivingEntity target = this.getTarget();
                if (target == null) {
                    this.targetPosition = new BlockPos(
                            this.getX() + this.random.nextInt(32) - this.random.nextInt(32),
                            clamp((float) (this.getY() + this.random.nextInt(32) - this.random.nextInt(32)), 0, 260),
                            this.getZ() + this.random.nextInt(32) - this.random.nextInt(32));
                } else {
                    this.targetPosition = new BlockPos(
                            target.getX() + this.random.nextInt(8) - this.random.nextInt(8),
                            clamp((float) (target.getY() + this.random.nextInt(8) - this.random.nextInt(8)), 0, 260),
                            target.getZ() + this.random.nextInt(8) - this.random.nextInt(8));
                }
            }

            double d2 = this.targetPosition.getX() + 0.5D - this.getX();
            double d0 = this.targetPosition.getY() + 0.5D - this.getY();
            double d1 = this.targetPosition.getZ() + 0.5D - this.getZ();
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec31 = vec3.add(
                    (Math.signum(d2) * 0.5D - vec3.x) * 0.1F,
                    (Math.signum(d0) * 0.7F - vec3.y) * 0.1F,
                    (Math.signum(d1) * 0.5D - vec3.z) * 0.1F);
            this.setDeltaMovement(vec31);
            float f = (float) (Mth.atan2(vec31.z, vec31.x) * (180F / (float) Math.PI)) - 90.0F;
            float f1 = Mth.wrapDegrees(f - this.getYRot());
            this.zza = 0.5F;
            this.setYRot(this.getYRot() + f1);
        }
    }

    public float clamp(float val, float min, float max) {
        if (val >= min && val <= max) return val;
        return val < min ? min : max;
    }

    private boolean checkWalls(AABB aabb) {
        int minBlockX = Mth.floor(aabb.minX) - 1;
        int minBlockY = Mth.floor(aabb.minY);
        int minBlockZ = Mth.floor(aabb.minZ) - 1;
        int maxBlockX = Mth.floor(aabb.maxX) + 1;
        int maxBlockY = Mth.floor(aabb.maxY) + 1;
        int maxBlockZ = Mth.floor(aabb.maxZ) + 1;
        boolean flag = false;
        boolean flag1 = false;

        for (int blockX = minBlockX; blockX <= maxBlockX; ++blockX) {
            for (int blockY = minBlockY; blockY <= maxBlockY; ++blockY) {
                for (int blockZ = minBlockZ; blockZ <= maxBlockZ; ++blockZ) {
                    BlockPos blockpos = new BlockPos(blockX, blockY, blockZ);
                    BlockState blockstate = this.level.getBlockState(blockpos);
                    if (!blockstate.isAir() && blockstate.getMaterial() != Material.FIRE && (blockstate.getMaterial() == Material.WOOD || blockstate.getMaterial() == Material.STONE) && random.nextInt(6) == 1 && isAngry()) {
                        if (net.minecraftforge.common.ForgeHooks.canEntityDestroy(this.level, blockpos, this) && !BlockTags.DRAGON_IMMUNE.contains(blockstate.getBlock())) {
                            flag1 = this.level.removeBlock(blockpos, false) || flag1;
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            BlockPos blockpos1 = new BlockPos(minBlockX + this.random.nextInt(maxBlockX - minBlockX + 1), minBlockY + this.random.nextInt(maxBlockY - minBlockY + 1), minBlockZ + this.random.nextInt(maxBlockZ - minBlockZ + 1));
            //this.level.levelEvent(2008, blockpos1, 0);
        }

        return flag;
    }

    @Override
    public void aiStep() {
        this.processFlappingMovement();
        super.aiStep();

        if (isNoAi() || isDragonSleeping())
            return;

        this.reassessAttackGoal();

        if (!isAngry() && this.calmTalking != null && this.calmTalking.getSaid() >= 6)
            setAngry(true);

        if (!this.level.isClientSide) {
            this.checkWalls(this.getBoundingBox());
        }

        this.oFlapTime = this.flapTime;
        if (!isFlying()) {
            this.flapTime = 0;
        } else {
            this.flapTime += 0.05f;
        }

        Vec3 facing = this.getViewVector(1);
        this.tickPart(this.head, facing.x * 8, 0, facing.z * 8);
        //if (level.isClientSide) {
        int fireTick = getBreathFireTicks();
        if (fireTick > 0) {
            for (int i = 0; i < 20; i++) {
                double rx = (random.nextDouble() - random.nextDouble()) * 0.3;
                double ry = (random.nextDouble() - random.nextDouble()) * 0.6;
                double rz = (random.nextDouble() - random.nextDouble()) * 0.3;
                double px = this.head.getX() + 1 + (random.nextDouble() - 1) * 2;
                double py = this.head.getY() + 1 + (random.nextDouble() - 0.5) * 2;
                double pz = this.head.getZ() + 1 + (random.nextDouble() - 1) * 2;
                this.level.addParticle(ParticleTypes.FLAME, px, py, pz, facing.x + rx, facing.y + ry, facing.z + rz);
                setBreathFireTicks(fireTick - 1);
            }
        }
        //}
        /*Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1, 1, 1));
        }*/

    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hurt = super.doHurtTarget(entity);
        //if (hurt)
        //    entity.setRemainingFireTicks(100);
        return hurt;
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isDragonSleeping() ? ModSoundEvents.DRAGON_SLEEPING : ModSoundEvents.DRAGON_ROAR;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.DRAGON_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.DRAGON_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.15F, 1.0F);
    }

    public void onFlap() {
        if (this.level.isClientSide && !this.isSilent()) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
        }
    }

    public boolean causeFallDamage(float p_148702_, float p_148703_, DamageSource p_148704_) {
        return false;
    }

    protected void checkFallDamage(double p_27419_, boolean p_27420_, BlockState p_27421_, BlockPos p_27422_) {
    }

    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (isDragonSleeping()) {
                setDragonSleeping(false);
            }
            setAngry(true);
            return super.hurt(source, amount * (source.isProjectile() ? 0.5f : 1f));
        }
    }

    public boolean hurt(RedDragonPart dragonPart, DamageSource source, float amount) {
        return this.hurt(source, amount);
    }

    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        addToBossFight.add(player);
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        addToBossFight.remove(player);
        bossEvent.removePlayer(player);
    }

    public void setCustomName(@Nullable Component p_31476_) {
        super.setCustomName(p_31476_);
        this.bossEvent.setName(this.getDisplayName());
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setBreathFireTicks(nbt.getInt("BreathFireTicks"));
        setAngry(nbt.getBoolean("Angry"));
        setDragonSleeping(nbt.getBoolean("Sleeping"));
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("BreathFireTicks", getBreathFireTicks());
        nbt.putBoolean("Angry", isAngry());
        nbt.putBoolean("Sleeping", isDragonSleeping());
    }

    protected float getStandingEyeHeight(Pose p_27440_, EntityDimensions p_27441_) {
        return p_27441_.height / 2.0F;
    }

    static class DragonTalkGoal extends Goal {
        private int said = 0;
        private int tickCount = 10;
        private final RedDragon dragon;
        private final String key;
        private final int amount;
        private final boolean angered;

        DragonTalkGoal(RedDragon dragon, String key, int amount, boolean angered) {
            this.dragon = dragon;
            this.key = key;
            this.amount = amount;
            this.angered = angered;
        }

        @Override
        public boolean canUse() {
            tickCount--;
            //LOGGER.info(target);
            return dragon.isAngry() == angered && dragon.getTarget() instanceof Player && tickCount <= 0 && !dragon.isDragonSleeping();
        }

        @Override
        public void tick() {
            tickCount = 200;
            LivingEntity target = dragon.getTarget();
            String name = dragon.hasCustomName() && dragon.getCustomName() != null ? dragon.getCustomName().getString() : new TranslatableComponent("entity.goosemod.red_dragon").getString();
            String say = new TranslatableComponent(this.key + ".say" + (dragon.level.random.nextInt(this.amount) + 1)).getString();
            target.sendMessage(new TextComponent("<" + name + "> \u00A7l\u00A74" + say), target.getUUID());
            said++;
        }

        public int getSaid() {
            return said;
        }
    }

    @SubscribeEvent
    public static void checkBreakBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer().isCreative())
            return;
        Level level = event.getPlayer().level;
        if (!level.isClientSide) {
            RedDragon dragon = level.getNearestEntity(level.getEntitiesOfClass(RedDragon.class, event.getPlayer().getBoundingBox().inflate(8, 3.0D, 8), (p_148152_) -> true), RedDragon.DRAGON_CONDITIONS, event.getPlayer(), event.getPlayer().getX(), event.getPlayer().getEyeY(), event.getPlayer().getZ());
            if (dragon == null)
                return;
            BlockState state = event.getState();
            if (PRECIOUS.contains(state.getBlock())) {
                if (dragon.isDragonSleeping())
                    dragon.setDragonSleeping(false);
                else if (dragon.random.nextInt(2) == 0)
                    dragon.setAngry(true);
            }
            BlockPos blockpos = ((ServerLevel) level).findNearestMapFeature(ModWorldGeneration.Structures.LAIR_FEATURE, event.getPlayer().getOnPos(), 5, false);
            if (blockpos == null)
                return;
            if (blockpos.distSqr(event.getPlayer().getX(), event.getPlayer().getY(), event.getPlayer().getZ(), true) > 16384)
                return;
            if (PRECIOUS.contains(state.getBlock()) && level.random.nextInt(6) == 0) {
                MobEffectInstance mobeffectinstance1 = event.getPlayer().getEffect(ModMobEffects.DRAGON_SICKNESS);
                int i = 1;
                if (mobeffectinstance1 != null) {
                    i += mobeffectinstance1.getAmplifier();
                    event.getPlayer().removeEffectNoUpdate(ModMobEffects.DRAGON_SICKNESS);
                } else {
                    --i;
                }
                event.getPlayer().addEffect(new MobEffectInstance(ModMobEffects.DRAGON_SICKNESS, 120000, i, false, false, true));
            }
        }
    }
}
