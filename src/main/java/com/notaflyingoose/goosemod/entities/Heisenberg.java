package com.notaflyingoose.goosemod.entities;

import com.notaflyingoose.goosemod.effects.ModMobEffects;
import com.notaflyingoose.goosemod.items.ModItems;
import com.notaflyingoose.goosemod.world.ModSoundEvents;
import com.notaflyingoose.goosemod.world.ModStats;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class Heisenberg extends AbstractVillager {
    public static final Ingredient TEMPTATION_ITEMS = Ingredient.of(ModItems.METH, ModItems.MIXER, ModItems.KETAMINE);

    public Heisenberg(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier createCustomAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FOLLOW_RANGE, 64)
                .build();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.1, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    public boolean showProgressBar() {
        return false;
    }

    public InteractionResult mobInteract(Player p_35856_, InteractionHand p_35857_) {
        ItemStack itemstack = p_35856_.getItemInHand(p_35857_);
        if (!itemstack.is(ModItems.HEISENBERG_SPAWN_EGG) && this.isAlive() && !this.isTrading() && !this.isBaby()) {
            if (p_35857_ == InteractionHand.MAIN_HAND) {
                p_35856_.awardStat(ModStats.TALKED_TO_HEISENBERG);
            }

            if (this.getOffers().isEmpty()) {
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            } else {
                if (!this.level.isClientSide) {
                    this.setTradingPlayer(p_35856_);
                    this.openTradingScreen(p_35856_, this.getDisplayName(), 1);
                }

                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        } else {
            return super.mobInteract(p_35856_, p_35857_);
        }
    }

    protected void updateTrades() {
        MerchantOffers merchantoffers = this.getOffers();
        merchantoffers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(ModItems.METH, 1), 12, 30, 0.5f));
        merchantoffers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.KETAMINE, 1), 6, 10, 0.5f));
        merchantoffers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(ModItems.NITROGLYCERIN_BUCKET, 1), 3, 15, 0.5f));
        merchantoffers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.MIXER, 1), 3, 30, 0.5f));
    }

    protected void rewardTradeXp(MerchantOffer p_35859_) {
        if (p_35859_.shouldRewardExp()) {
            int i = 3 + this.random.nextInt(4);
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }

    }

    public boolean removeWhenFarAway(double p_35886_) {
        return false;
    }

    @Override
    protected int getExperienceReward(Player player) {
        return 5 + this.level.random.nextInt(10);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.HEISENBERG_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.HEISENBERG_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.HEISENBERG_DEATH;
    }

    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }

    public boolean canBeAffected(MobEffectInstance p_31495_) {
        return p_31495_.getEffect() == ModMobEffects.HIGH ? false : super.canBeAffected(p_31495_);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }
}
