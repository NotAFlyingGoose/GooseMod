package com.notaflyingoose.goosemod.entities;

import com.notaflyingoose.goosemod.world.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

public class Mimic extends Monster implements Container {
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(9*3, ItemStack.EMPTY);
    private static final EntityDataAccessor<Boolean> DATA_ANGRY = SynchedEntityData.defineId(Mimic.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ATTACKING_TARGET = SynchedEntityData.defineId(Mimic.class, EntityDataSerializers.INT);

    public Mimic(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setCanPickUpLoot(true);
    }

    public static AttributeSupplier createCustomAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 32)
                .build();
    }

    protected void pickUpItem(ItemEntity entity) {
        ItemStack itemstack = entity.getItem();
        for (int i = 0; i < this.getContainerSize(); i++) {
            SlotAccess slot = getSlot(i);
            ItemStack slotValue = slot.get();
            if (slotValue.is(Items.AIR) || (slotValue.sameItem(itemstack) && (slotValue.getCount() + itemstack.getCount()) < getMaxStackSize())) {
                if (!slotValue.sameItem(itemstack))
                    slot.set(itemstack);
                else
                    slotValue.grow(itemstack.getCount());
                this.onItemPickup(entity);
                this.take(entity, itemstack.getCount());
                entity.discard();
                return;
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ANGRY, false);
        this.entityData.define(DATA_ATTACKING_TARGET, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new Mimic.MimicKillGoal(this));
        this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6) {
            @Override
            public boolean canUse() {
                return super.canUse() && Mimic.this.isAngry();
            }
        });
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Animal.class, 6) {
            @Override
            public boolean canUse() {
                return super.canUse() && Mimic.this.isAngry();
            }
        });

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 16, true, true, LivingEntity::attackable));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 16, true, true, (entity) -> entity.attackable() && entity instanceof Npc));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 16, true, true, LivingEntity::attackable));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 16, true, true, (entity) -> entity.attackable() && !(entity instanceof Enemy)));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!player.isSecondaryUseActive()) {
            setAngry(true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void aiStep() {
        if (this.isNoAi())
            return;

        if (this.isAngry()) {
            LivingEntity attackingTarget = getAttackingTarget();
            boolean deadTarget = (attackingTarget != null &&
                    (attackingTarget.isDeadOrDying() || !attackingTarget.attackable() || !attackingTarget.canBeSeenAsEnemy()));
            if (deadTarget || (attackingTarget == null && random.nextInt(50)==0))
                setAngry(false);
        } else {
            BlockPos on = getOnPos();
            setPos(Vec3.atBottomCenterOf(on.above()));
            float rot = getDirection().toYRot();
            setYRot(rot);
            setYBodyRot(rot);
            setYHeadRot(rot);
        }

        super.aiStep();
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean isPushable() {
        return false;
    }

    public void push(Entity entity) {
        if (entity instanceof ItemEntity)
            return;

        if (entity instanceof Mimic) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entity);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    public boolean isAngry() {
        return this.entityData.get(DATA_ANGRY);
    }

    public void setAngry(boolean mouthOpen) {
        this.entityData.set(DATA_ANGRY, mouthOpen);
    }

    public LivingEntity getAttackingTarget() {
        int id = this.entityData.get(DATA_ATTACKING_TARGET);
        return id != 0 ? (LivingEntity) level.getEntity(id) : null;
    }

    public void setAttackingTarget(LivingEntity target) {
        this.entityData.set(DATA_ATTACKING_TARGET, target != null ? target.getId() : 0);
    }

    @Override
    protected int getExperienceReward(Player player) {
        return 5 + this.level.random.nextInt(10);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.equals(DamageSource.DROWN);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            setAngry(true);
            float multiplier = 0.6f;
            if (source.getMsgId().equals("player") && source.getMsgId().equals("mob") && source.getMsgId().equals("generic") || source instanceof EntityDamageSource) {
                ItemStack holding = ((LivingEntity) source.getEntity()).getMainHandItem();
                if (holding.getItem() instanceof AxeItem)
                    multiplier = 1.25f;
            }
            if (source.isFire())
                multiplier = 2f;
            return super.hurt(source, amount * multiplier);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isAngry() ? SoundEvents.CHEST_OPEN : null;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.CHEST_CLOSE;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.CHEST_OPEN, 0.15F, 1F);
    }

    @Override
    public void dropCustomDeathLoot(DamageSource p_21385_, int p_21386_, boolean p_21387_) {
        super.dropCustomDeathLoot(p_21385_, p_21386_, p_21387_);
        Containers.dropContents(this.level, this, this);
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, this.itemStacks);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.itemStacks);
    }

    @Override
    public int getContainerSize() {
        return 9*3;
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.itemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public ItemStack getItem(int slot) {
        return this.itemStacks.get(slot);
    }

    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.itemStacks, slot, amount);
    }

    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack itemstack = this.itemStacks.get(slot);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.itemStacks.set(slot, ItemStack.EMPTY);
            return itemstack;
        }
    }

    public void setItem(int slot, ItemStack stack) {
        this.itemStacks.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

    }

    public SlotAccess getSlot(final int p_150257_) {
        return p_150257_ >= 0 && p_150257_ < this.getContainerSize() ? new SlotAccess() {
            public ItemStack get() {
                return Mimic.this.getItem(p_150257_);
            }

            public boolean set(ItemStack p_150265_) {
                Mimic.this.setItem(p_150257_, p_150265_);
                return true;
            }
        } : super.getSlot(p_150257_);
    }

    public void setChanged() {
    }

    public boolean stillValid(Player p_38230_) {
        if (this.isRemoved()) {
            return false;
        } else {
            return !(p_38230_.distanceToSqr(this) > 64.0D);
        }
    }

    public void clearContent() {
        this.itemStacks.clear();
    }

    public static class MimicKillGoal extends Goal {
        private final Mimic mob;
        private final Level level;
        private boolean close;
        private LivingEntity target;

        public MimicKillGoal(Mimic mob) {
            this.mob = mob;
            this.level = mob.level;
        }

        public boolean canUse() {
            if (mob.getRandom().nextInt(100) == 0 || mob.isAngry()) {
                LivingEntity t = mob.getTarget();
                if (t != null && t.attackable()) {
                    this.target = t;
                    return true;
                }
            }
            return false;
        }

        @Override
        public void start() {
            this.mob.setAngry(true);
            mob.setAttackingTarget(this.target);
        }

        public void stop() {
            this.mob.setAngry(false);
            mob.setAttackingTarget(null);
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        public boolean canContinueToUse() {
            return this.target.isAlive() && this.target.attackable() && mob.isAngry();
        }

        public void tick() {
            double distance = this.mob.distanceToSqr(this.target);
            this.close = distance < 4;
            if (close) {
                this.mob.getNavigation().moveTo(this.target, 1.5);
                //this.mob.getNavigation().stop();
                this.mob.getLookControl().setLookAt(this.target.getX(), this.target.getY(), this.target.getZ());
                //this.target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 10));
                if (this.target instanceof Mob tm)
                    tm.getLookControl().setLookAt(this.mob);
                this.mob.setAngry(true);
                this.target.hurt(DamageSource.mobAttack(this.mob), 1);
                //this.level.addParticle(ParticleTypes.EXPLOSION, this.target.getX(), this.target.getY() + 0.5D, this.target.getZ(), 0.0D, 0.0D, 0.0D);
            } else {
                this.mob.getNavigation().moveTo(this.target, 1.1);
            }
        }
    }

    // Forge Start
    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (this.isAlive() && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));
    }
}
