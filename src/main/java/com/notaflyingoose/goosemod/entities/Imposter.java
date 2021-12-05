package com.notaflyingoose.goosemod.entities;

import com.notaflyingoose.goosemod.items.ModItems;
import com.notaflyingoose.goosemod.world.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class Imposter extends Monster {
    private static final EntityDataAccessor<Byte> DATA_COLOR_ID = SynchedEntityData.defineId(Imposter.class, EntityDataSerializers.BYTE);
    public static final Ingredient TEMPTATION_ITEMS = Ingredient.of(ModItems.BIG_MICK, ModItems.DREAM_MASK);

    private static final EntityDataAccessor<Boolean> DATA_MOUTH_OPEN = SynchedEntityData.defineId(Imposter.class, EntityDataSerializers.BOOLEAN);

    public Imposter(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier createCustomAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 32)
                .build();
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLOR_ID) & 15);
    }

    public void setColor(DyeColor p_29856_) {
        byte b0 = this.entityData.get(DATA_COLOR_ID);
        this.entityData.set(DATA_COLOR_ID, (byte)(b0 & 240 | p_29856_.getId() & 15));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_29835_, DifficultyInstance p_29836_, MobSpawnType p_29837_, @Nullable SpawnGroupData p_29838_, @Nullable CompoundTag p_29839_) {
        this.setColor(Crewmate.getRandomAmogusColor(p_29835_.getRandom()));
        return super.finalizeSpawn(p_29835_, p_29836_, p_29837_, p_29838_, p_29839_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_MOUTH_OPEN, false);
        this.entityData.define(DATA_COLOR_ID, (byte)0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.1, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Crewmate.class, 20));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6));
        this.goalSelector.addGoal(4, new Imposter.ImposterKillGoal<>(this, Player.class, false));
        this.goalSelector.addGoal(4, new Imposter.ImposterKillGoal<>(this, Crewmate.class, false));
        this.goalSelector.addGoal(5, new Imposter.ImposterKillGoal<>(this, Animal.class, false));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public boolean isMouthOpen() {
        return this.entityData.get(DATA_MOUTH_OPEN);
    }

    public void setMouthOpen(boolean mouthOpen) {
        this.entityData.set(DATA_MOUTH_OPEN, mouthOpen);
    }

    public float getHeadEatAngleScale(float partialTicks) {
        if (isMouthOpen()) {
            return 1.25f;
        } else {
            return 0;
        }
        /*if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
            float f = ((this.eatAnimationTick - 4) - partialTicks) / 32.0F;
            return -(((float)Math.PI / 5F) + 0.21991149F * Mth.sin(f * 28.7F));
        } else {
            return this.eatAnimationTick > 0 ? ((float)Math.PI / 5F) : this.getXRot() * ((float)Math.PI / 180F);
        }*/
    }

    @Override
    protected int getExperienceReward(Player player) {
        return 5 + this.level.random.nextInt(10);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.IMPOSTER_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.IMPOSTER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.IMPOSTER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSoundEvents.IMPOSTER_STEP, 0.15F, 1.0F);
    }

    public static class ImposterKillGoal<T extends LivingEntity> extends TargetGoal {
        protected final Class<T> targetType;
        private final Imposter mob;
        private final Level level;
        private boolean close;
        private LivingEntity target;
        protected TargetingConditions targetConditions;
        private double stuckX, stuckY, stuckZ;

        public ImposterKillGoal(Imposter mob, Class<T> targetType, boolean checkSight) {
            this(mob, targetType, checkSight, false);
        }

        public ImposterKillGoal(Imposter mob, Class<T> targetType, boolean checkSight, boolean nearbyOnly) {
            this(mob, targetType, checkSight, nearbyOnly, null);
        }

        public ImposterKillGoal(Imposter mob, Class<T> targetType, boolean checkSight, boolean nearbyOnly, Predicate<LivingEntity> targetPredicate) {
            super(mob, checkSight, nearbyOnly);
            this.mob = mob;
            this.targetType = targetType;
            this.level = mob.level;
            this.setFlags(EnumSet.of(Flag.TARGET));
            this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(targetPredicate);
        }

        public boolean canUse() {
            if (this.mob.getRandom().nextInt(1000) != 0) {
                return false;
            } else {
                this.findTarget();
                return this.target != null;
            }
        }

        protected AABB getTargetSearchArea(double p_26069_) {
            return this.mob.getBoundingBox().inflate(p_26069_, 4.0D, p_26069_);
        }

        protected void findTarget() {
            if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
                this.target = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), (p_148152_) -> true), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            } else {
                this.target = this.mob.level.getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            }

        }

        public void start() {
            this.level.broadcastEntityEvent(this.mob, (byte)10);
            this.mob.setTarget(this.target);
            super.start();
        }

        public void setTarget(@Nullable LivingEntity p_26071_) {
            this.target = p_26071_;
        }

        public void stop() {
            this.mob.setMouthOpen(false);
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        public boolean canContinueToUse() {
            return this.target.isAlive();
        }

        public boolean isClose() {
            return this.close;
        }

        public void tick() {
            float distance = this.mob.distanceTo(this.target);
            this.close = distance < 2;
            if (close) {
                this.mob.getNavigation().moveTo(this.target, 1.5);
                //this.mob.getNavigation().stop();
                this.mob.getLookControl().setLookAt(this.target.getX(), this.target.getY(), this.target.getZ());
                //this.target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 10));
                if (this.target instanceof Mob tm)
                    tm.getLookControl().setLookAt(this.mob);
                this.target.moveTo(stuckX, stuckY, stuckZ);
                this.target.hurt(DamageSource.mobAttack(this.mob), 1);
                //this.level.addParticle(ParticleTypes.EXPLOSION, this.target.getX(), this.target.getY() + 0.5D, this.target.getZ(), 0.0D, 0.0D, 0.0D);
            } else {
                this.stuckX = this.target.getX();
                this.stuckY = this.target.getY();
                this.stuckZ = this.target.getZ();
                this.mob.setMouthOpen(true);
                this.mob.getNavigation().moveTo(this.target, 1.1);
            }
        }
    }
}
