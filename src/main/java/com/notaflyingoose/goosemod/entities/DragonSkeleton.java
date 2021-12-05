package com.notaflyingoose.goosemod.entities;

import com.notaflyingoose.goosemod.client.model.RedDragonPart;
import com.notaflyingoose.goosemod.items.ModItems;
import com.notaflyingoose.goosemod.world.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

public class DragonSkeleton extends Monster {

    public DragonSkeleton(EntityType<? extends Monster> entity, Level level) {
        super(entity, level);
    }

    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
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
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.1f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5f)
                .build();
    }

    public void tick() {
        super.tick();
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hurt = super.doHurtTarget(entity);
        if (hurt)
            entity.setRemainingFireTicks(100);
        return hurt;
    }

    protected MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
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
        if (DamageSource.OUT_OF_WORLD.equals(source)) {
            return super.hurt(source, amount);
        } else if (!this.isInvulnerableTo(source)) {
            if (source.getEntity() != null && source.getEntity() instanceof LivingEntity entity) {
                Item item = entity.getMainHandItem().getItem();
                if (item instanceof PickaxeItem || item instanceof AxeItem || item instanceof SwordItem)
                    return super.hurt(source, amount);
            } else if (DamageSource.IN_FIRE.equals(source) || DamageSource.ON_FIRE.equals(source))
                return super.hurt(source, amount);
        }
        return false;
    }

    protected void dropCustomDeathLoot(DamageSource p_31464_, int p_31465_, boolean p_31466_) {
        super.dropCustomDeathLoot(p_31464_, p_31465_, p_31466_);
        ItemStack stack = new ItemStack(ModItems.DRAGON_SKULL);
        ItemEntity itementity = this.spawnAtLocation(stack);
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }
    }

}
