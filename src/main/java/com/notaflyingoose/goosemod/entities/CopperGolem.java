package com.notaflyingoose.goosemod.entities;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class CopperGolem extends TamableAnimal {

    protected CopperGolem(EntityType<? extends CopperGolem> p_27508_, Level p_27509_) {
        super(p_27508_, p_27509_);
    }

    public static AttributeSupplier createCustomAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5)
                .build();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(5, new CopperGolem.CopperGolemPressButtomGoal(1F, 16, 6));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (p_28879_) -> {
            return p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper);
        }));
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            this.setOrderedToSit(false);
            if (entity != null && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
                amount = (amount + 1.0F) / 2.0F;
            }

            return super.hurt(source, amount);
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (this.level.isClientSide) {
            return itemstack.is(Items.COPPER_INGOT) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (itemstack.is(Items.COPPER_INGOT)) {
                if (!player.getAbilities().instabuild && this.getHealth() < this.getMaxHealth()) {
                    itemstack.shrink(1);
                    this.heal((float)item.getFoodProperties().getNutrition());
                    return InteractionResult.SUCCESS;
                }
                if (this.getHealth() == this.getMaxHealth() && !isOwnedBy(player) && !isTame()) {
                    if (this.random.nextInt(6) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                        CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer) player, this);
                        this.tame(player);
                        this.setOrderedToSit(true);
                        this.navigation.stop();
                        this.setTarget(null);
                        this.level.broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level.broadcastEntityEvent(this, (byte) 6);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            InteractionResult interactionresult = super.mobInteract(player, hand);
            if ((!interactionresult.consumesAction() || this.isBaby()) && this.isOwnedBy(player) && hand == InteractionHand.MAIN_HAND) {
                this.setOrderedToSit(!this.isOrderedToSit());
                LOGGER.info(this.isOrderedToSit() ? "Sitting" : "Standing");
                this.jumping = false;
                this.navigation.stop();
                this.setTarget((LivingEntity) null);
                return InteractionResult.SUCCESS;
            }
            return interactionresult;
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    public class CopperGolemPressButtomGoal extends MoveToBlockGoal {

        public CopperGolemPressButtomGoal(double speedMod, int searchRange, int verticalSearchRange) {
            super(CopperGolem.this, speedMod, searchRange, verticalSearchRange);
        }

        public double acceptedDistance() {
            return 4.0D;
        }

        public boolean shouldRecalculatePath() {
            return this.tryTicks % 100 == 0;
        }

        protected boolean isValidTarget(LevelReader p_28680_, BlockPos p_28681_) {
            BlockState blockstate = p_28680_.getBlockState(p_28681_);
            return blockstate.is(Blocks.STONE_BUTTON) && !blockstate.getValue(ButtonBlock.POWERED);
        }

        public void tick() {
            if (this.isReachedTarget()) {
                this.onReachedTarget();
            }
            super.tick();
        }

        protected void onReachedTarget() {
            BlockState blockstate = CopperGolem.this.level.getBlockState(this.blockPos);
            if (blockstate.is(Blocks.STONE_BUTTON)) {
                ((ButtonBlock) blockstate.getBlock()).press(blockstate, CopperGolem.this.level, this.blockPos);
            }
        }

        public boolean canUse() {
            return !CopperGolem.this.isOrderedToSit() && super.canUse();
        }
    }
}
