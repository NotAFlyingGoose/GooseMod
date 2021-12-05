package com.notaflyingoose.goosemod.entities;

import com.notaflyingoose.goosemod.items.ModItems;
import com.notaflyingoose.goosemod.world.ModSoundEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class Goose extends Animal {
    public static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
    static final Predicate<ItemEntity> ALLOWED_ITEMS = (itemEntity) -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive();
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    public float flapping = 1.0F;
    private float nextFlap = 1.0F;
    public int eggTime = this.random.nextInt(6000) + 6000;
    private int ticksSinceEaten;
    private float interestedAngle;
    private float interestedAngleO;
    private static final EntityDataAccessor<CompoundTag> DATA_FRIENDS = SynchedEntityData.defineId(Goose.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Goose.class, EntityDataSerializers.BYTE);

    public Goose(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setCanPickUpLoot(true);
        //this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.1F, 0.5F, false);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        CompoundTag tag = new CompoundTag();
        ListTag friends = new ListTag();
        tag.put("entities", friends);
        this.entityData.define(DATA_FRIENDS, tag);
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    private void setFlag(int p_28533_, boolean p_28534_) {
        if (p_28534_) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) | p_28533_));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) & ~p_28533_));
        }

    }

    private boolean getFlag(int p_28609_) {
        return (this.entityData.get(DATA_FLAGS_ID) & p_28609_) != 0;
    }

    public CompoundTag getFriends() {
        return this.entityData.get(DATA_FRIENDS);
    }

    public void setFriends(CompoundTag friends) {
        this.entityData.set(DATA_FRIENDS, friends);
    }

    public void addFriend(LivingEntity entity) {
        if (entity == null)
            return;
        CompoundTag tag = this.entityData.get(DATA_FRIENDS);
        ListTag friends = (ListTag) tag.get("entities");
        IntTag id = IntTag.valueOf(entity.getId());
        if (friends == null || friends.contains(id))
            return;

        friends.add(id);
        tag.put("entity", friends);
        this.entityData.set(DATA_FRIENDS, tag);
    }

    public void removeFriend(LivingEntity entity) {
        if (entity == null)
            return;
        CompoundTag tag = this.entityData.get(DATA_FRIENDS);
        ListTag friends = (ListTag) tag.get("entities");
        IntTag id = IntTag.valueOf(entity.getId());
        if (friends == null || !friends.contains(id))
            return;

        friends.remove(id);
        tag.put("entity", friends);
        this.entityData.set(DATA_FRIENDS, tag);
    }

    public boolean isFriend(LivingEntity entity) {
        if (entity == null)
            return false;
        CompoundTag tag = this.entityData.get(DATA_FRIENDS);
        ListTag friends = (ListTag) tag.get("entities");
        IntTag id = IntTag.valueOf(entity.getId());
        return friends != null && friends.contains(id);
    }

    public void setIsInterested(boolean p_28617_) {
        this.setFlag(8, p_28617_);
    }

    public boolean isInterested() {
        return this.getFlag(8);
    }

    protected PathNavigation createNavigation(Level level) {
        return super.createNavigation(level);
        //return new Goose.GoosePathNavigation(this, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.0D, FOOD_ITEMS, false));
        this.goalSelector.addGoal(2, new Goose.GooseSearchForItemsGoal());
        this.goalSelector.addGoal(3, new Goose.GooseIntimidateGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new MoveTowardsTargetGoal(this, 0.9D, 1.1F));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(6, new Goose.GooseGoToWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Chicken.class, 6.0F));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Goose.class, 6.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !isFriend(this.mob.getLastHurtByMob());
            }
        });
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, 5, true, false, entity -> (!isFriend(entity) && entity.attackable())));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 5, true, false, entity -> (!isFriend(entity) && entity.attackable())));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, 5, true, false, entity -> (!(entity instanceof Goose) && !isFriend(entity) && entity.attackable())));
    }

    protected void spawnParticlesAroundSelf(ParticleOptions particles) {
        for(int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(particles, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
        }
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (isFriend(target))
            return;
        super.setTarget(target);
    }

    public void handleEntityEvent(byte event) {
        if (event == 13) {
            this.spawnParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
        } else if (event == 7) {
            this.spawnParticlesAroundSelf(ParticleTypes.HEART);
        } else if (event == 6) {
            this.spawnParticlesAroundSelf(ParticleTypes.SMOKE);
        } else {
            super.handleEntityEvent(event);
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (this.level.isClientSide) {
            return isFood(itemstack) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (isFood(itemstack)) {
                if (!player.getAbilities().instabuild && this.getHealth() < this.getMaxHealth()) {
                    itemstack.shrink(1);
                    this.heal((float)item.getFoodProperties().getNutrition());
                    return InteractionResult.SUCCESS;
                }
                if (this.getHealth() == this.getMaxHealth() && !isFriend(player)) {
                    if (this.random.nextInt(6) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                        CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer) player, this);
                        this.addFriend(player);
                        this.navigation.stop();
                        this.setTarget(null);
                        this.level.broadcastEntityEvent(this, (byte) 7);
                        return InteractionResult.SUCCESS;
                    } else {
                        this.level.broadcastEntityEvent(this, (byte) 6);
                        return InteractionResult.SUCCESS;
                    }
                }

            }

            return super.mobInteract(player, hand);
        }
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("EggLayTime")) {
            this.eggTime = nbt.getInt("EggLayTime");
        }
        setFriends((CompoundTag) nbt.get("Friends"));
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.put("Friends", getFriends());
        nbt.putInt("EggLayTime", this.eggTime);
    }

    public boolean canStandOnFluid(Fluid p_33893_) {
        return p_33893_.is(FluidTags.WATER);
    }

    public static AttributeSupplier createCustomAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.ATTACK_DAMAGE, 1)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.FOLLOW_RANGE, 16)
                .build();
    }

    protected float getStandingEyeHeight(Pose p_28251_, EntityDimensions p_28252_) {
        return this.isBaby() ? p_28252_.height * 0.85F : p_28252_.height * 0.92F;
    }

    public void aiStep() {
        super.aiStep();

        if (!this.level.isClientSide && this.isAlive() && this.isEffectiveAi()) {
            ++this.ticksSinceEaten;
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (this.canEat(itemstack)) {
                if (this.ticksSinceEaten > 600) {
                    ItemStack itemstack1 = itemstack.finishUsingItem(this.level, this);
                    if (!itemstack1.isEmpty()) {
                        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack1);
                    }

                    this.ticksSinceEaten = 0;
                } else if (this.ticksSinceEaten > 560 && this.random.nextFloat() < 0.1F) {
                    this.playSound(this.getEatingSound(itemstack), 1.0F, 1.0F);
                    this.level.broadcastEntityEvent(this, (byte)45);
                }
            }

            LivingEntity livingentity = this.getTarget();
            if (livingentity == null || !livingentity.isAlive()) {
                this.setIsInterested(false);
            }

            this.oFlap = this.flap;
            this.oFlapSpeed = this.flapSpeed;
            this.flapSpeed = (float) ((double) this.flapSpeed + (double) (this.onGround ? -1 : 4) * 0.3D);
            this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
            if (!this.onGround && this.flapping < 1.0F) {
                this.flapping = 1.0F;
            }

            this.flapping = (float) ((double) this.flapping * 0.9D);
            Vec3 vec3 = this.getDeltaMovement();
            if (!this.onGround && vec3.y < 0.0D) {
                this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
            }

            this.flap += this.flapping * 2.0F;
            if (!this.isBaby() && --this.eggTime <= 0) {
                this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.spawnAtLocation(ModItems.GOOSE_EGG);
                this.eggTime = this.random.nextInt(6000) + 6000;
            }
        }
    }

    void clearStates() {
        this.setIsInterested(false);
    }


    private boolean canEat(ItemStack p_28598_) {
        return p_28598_.getItem().isEdible() && this.getTarget() == null && this.onGround && !this.isSleeping();
    }

    public void tick() {
        super.tick();
        this.interestedAngleO = this.interestedAngle;
        if (this.isInterested()) {
            this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
        } else {
            this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
        }
        this.floatGoose();
        this.checkInsideBlocks();
    }

    private void floatGoose() {
        if (this.isInWater()) {
            CollisionContext collisioncontext = CollisionContext.of(this);
            if (collisioncontext.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true) && !this.level.getFluidState(this.blockPosition().above()).is(FluidTags.WATER)) {
                this.onGround = true;
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D).add(0.0D, 0.1D, 0.0D));
            }
        }

    }

    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    protected void onFlap() {
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    public boolean causeFallDamage(float p_148875_, float p_148876_, DamageSource p_148877_) {
        return false;
    }

    public float getHeadRollAngle(float p_28621_) {
        return Mth.lerp(p_28621_, this.interestedAngleO, this.interestedAngle) * 0.11F * (float)Math.PI;
    }

    protected void pickUpItem(ItemEntity p_28514_) {
        ItemStack itemstack = p_28514_.getItem();
        if (this.canHoldItem(itemstack)) {
            int i = itemstack.getCount();
            if (i > 1) {
                this.dropItemStack(itemstack.split(i - 1));
            }

            this.spitOutItem(this.getItemBySlot(EquipmentSlot.MAINHAND));
            this.onItemPickup(p_28514_);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.split(1));
            this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2.0F;
            this.take(p_28514_, itemstack.getCount());
            p_28514_.discard();
            this.ticksSinceEaten = 0;
        }

    }

    public boolean canHoldItem(ItemStack p_28578_) {
        Item item = p_28578_.getItem();
        ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        boolean foodFlag = true;
        if (item.isEdible())
            if (item.getFoodProperties().isMeat())
                foodFlag = false;
        boolean eggFlag = !item.equals(ModItems.GOOSE_EGG) && !item.equals(Items.EGG);
        return itemstack.isEmpty() || this.ticksSinceEaten > 0 && foodFlag && eggFlag && !itemstack.getItem().isEdible();
    }

    private void spitOutItem(ItemStack p_28602_) {
        if (!p_28602_.isEmpty() && !this.level.isClientSide) {
            ItemEntity itementity = new ItemEntity(this.level, this.getX() + this.getLookAngle().x, this.getY() + 1.0D, this.getZ() + this.getLookAngle().z, p_28602_);
            itementity.setPickUpDelay(40);
            itementity.setThrower(this.getUUID());
            this.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.0F);
            this.level.addFreshEntity(itementity);
        }
    }

    private void dropItemStack(ItemStack p_28606_) {
        ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), p_28606_);
        this.level.addFreshEntity(itementity);
    }

    protected void dropAllDeathLoot(DamageSource p_28536_) {
        ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!itemstack.isEmpty()) {
            this.spawnAtLocation(itemstack);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        super.dropAllDeathLoot(p_28536_);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (source.getEntity() != null && source.getEntity() instanceof LivingEntity living && isFriend(living)) {
                removeFriend(living);
                this.level.broadcastEntityEvent(this, (byte)13);
            }
            return super.hurt(source, amount);
        }
    }

    public boolean isFood(ItemStack stack) {
        return FOOD_ITEMS.test(stack);
    }

    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.GOOSE_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_28262_) {
        return ModSoundEvents.GOOSE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return ModSoundEvents.GOOSE_DEATH;
    }

    protected void playStepSound(BlockPos p_28254_, BlockState p_28255_) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return ModEntityTypes.GOOSE.create(level);
    }

    public void positionRider(Entity rider) {
        super.positionRider(rider);
        float f = Mth.sin(this.yBodyRot * ((float)Math.PI / 180F));
        float f1 = Mth.cos(this.yBodyRot * ((float)Math.PI / 180F));
        float f2 = 0.1F;
        float f3 = 0.0F;
        rider.setPos(this.getX() + (double)(0.1F * f), this.getY(0.5D) + rider.getMyRidingOffset() + 0.0D, this.getZ() - (double)(0.1F * f1));
        if (rider instanceof LivingEntity) {
            ((LivingEntity)rider).yBodyRot = this.yBodyRot;
        }

    }

    class GooseIntimidateGoal extends MeleeAttackGoal {
        public GooseIntimidateGoal(Goose mob, double speed, boolean followIfNotSeen) {
            super(mob, speed, followIfNotSeen);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !Goose.this.isFriend(Goose.this.getTarget());
        }

        @Override
        public void start() {
            super.start();
            mob.setAggressive(true);
        }

        @Override
        public void stop() {
            super.stop();
            mob.setAggressive(false);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target, double dist) {
            if (mob.level.random.nextInt(100) != 0) {
                if (target instanceof Mob mob)
                    mob.setLastHurtByMob(this.mob);
            } else {
                super.checkAndPerformAttack(target, dist);
            }
        }
    }

    class GooseSearchForItemsGoal extends Goal {
        public GooseSearchForItemsGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (!Goose.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                return false;
            } else if (Goose.this.getTarget() == null && Goose.this.getLastHurtByMob() == null) {
                if (Goose.this.getRandom().nextInt(10) != 0) {
                    return false;
                } else {
                    List<ItemEntity> list = Goose.this.level.getEntitiesOfClass(ItemEntity.class, Goose.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Goose.ALLOWED_ITEMS);
                    return !list.isEmpty() && Goose.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
                }
            } else {
                return false;
            }
        }

        public void tick() {
            List<ItemEntity> list = Goose.this.level.getEntitiesOfClass(ItemEntity.class, Goose.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Goose.ALLOWED_ITEMS);
            ItemStack itemstack = Goose.this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (itemstack.isEmpty() && !list.isEmpty()) {
                Goose.this.getNavigation().moveTo(list.get(0), (double)1.2F);
            }

        }

        public void start() {
            List<ItemEntity> list = Goose.this.level.getEntitiesOfClass(ItemEntity.class, Goose.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Goose.ALLOWED_ITEMS);
            if (!list.isEmpty()) {
                Goose.this.getNavigation().moveTo(list.get(0), (double)1.2F);
            }

        }
    }

    static class GooseGoToWaterGoal extends MoveToBlockGoal {
        private final Goose goose;

        GooseGoToWaterGoal(Goose p_33955_, double p_33956_) {
            super(p_33955_, p_33956_, 8, 2);
            this.goose = p_33955_;
        }

        public BlockPos getMoveToTarget() {
            return this.blockPos;
        }

        public boolean canContinueToUse() {
            return !this.goose.isInLava() && this.isValidTarget(this.goose.level, this.blockPos);
        }

        public boolean canUse() {
            return !this.goose.isInLava() && super.canUse();
        }

        public boolean shouldRecalculatePath() {
            return this.tryTicks % 20 == 0;
        }

        protected boolean isValidTarget(LevelReader p_33963_, BlockPos p_33964_) {
            return p_33963_.getBlockState(p_33964_).is(Blocks.WATER) && p_33963_.getBlockState(p_33964_.above()).isPathfindable(p_33963_, p_33964_, PathComputationType.LAND);
        }
    }

    static class GoosePathNavigation extends GroundPathNavigation {
        GoosePathNavigation(Goose mob, Level level) {
            super(mob, level);
        }

        protected boolean canUpdatePath() {
            return true;
        }

        protected PathFinder createPathFinder(int p_30298_) {
            this.nodeEvaluator = new WalkNodeEvaluator();
            this.nodeEvaluator.setCanOpenDoors(false);
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, p_30298_);
        }

        protected boolean hasValidPathType(BlockPathTypes p_33974_) {
            return p_33974_ == BlockPathTypes.WATER || super.hasValidPathType(p_33974_);
        }

        public boolean isStableDestination(BlockPos pos) {
            return this.level.getBlockState(pos).is(Blocks.WATER) || super.isStableDestination(pos);
        }
    }

}
