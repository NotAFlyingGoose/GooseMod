package com.notaflyingoose.goosemod.entities;

import com.notaflyingoose.goosemod.world.ModSoundEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class Lemur extends Animal {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Lemur.class, EntityDataSerializers.BYTE);
    public static final Ingredient TEMPT_ITEMS = Ingredient.of(Items.COCOA_BEANS, Items.EMERALD);

    public Lemur(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier createCustomAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 16)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE, 16)
                .build();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return ModEntityTypes.LEMUR.create(level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Ocelot.class,  8, 1.1, 1.1));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.1));
        this.goalSelector.addGoal(3, new LemurAttackCocoaBeans(this, 1, 32));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1, TEMPT_ITEMS, false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 16));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (this.level.isClientSide) {
            return isFood(itemstack) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (isFood(itemstack)) {
                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal((float)item.getFoodProperties().getNutrition());
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            return super.mobInteract(player, hand);
        }
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(Items.COCOA_BEANS);
    }

    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            this.setClimbing(this.horizontalCollision);
        }

    }

    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean p_33820_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_33820_) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    public void makeStuckInBlock(BlockState state, Vec3 vec3) {
        if (!state.is(Blocks.JUNGLE_LEAVES)) {
            super.makeStuckInBlock(state, vec3);
        }
    }

    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource source) {
        return false;
    }

    protected float getStandingEyeHeight(Pose p_33799_, EntityDimensions p_33800_) {
        return 0.65F;
    }

    @Override
    protected int getExperienceReward(Player player) {
        return 1;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.LEMUR_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.LEMUR_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.LEMUR_DEATH;
    }

    class LemurAttackCocoaBeans extends RemoveBlockGoal {
        LemurAttackCocoaBeans(PathfinderMob mob, double speed, int verticalSearchRange) {
            super(Blocks.COCOA, mob, speed, verticalSearchRange);
        }

        public void playDestroyProgressSound(LevelAccessor p_34351_, BlockPos p_34352_) {
            p_34351_.playSound(null, p_34352_, ModSoundEvents.HEISENBERG_AMBIENT, SoundSource.AMBIENT, 0.5F, 0.9F + Lemur.this.random.nextFloat() * 0.2F);
        }

        public void playBreakSound(Level p_34348_, BlockPos p_34349_) {
            p_34348_.playSound(null, p_34349_, ModSoundEvents.HEISENBERG_DEATH, SoundSource.BLOCKS, 0.7F, 0.9F + p_34348_.random.nextFloat() * 0.2F);
        }

        public double acceptedDistance() {
            return 1.14D;
        }
    }

}
