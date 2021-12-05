package com.notaflyingoose.goosemod.entities.boss.herobrine;

import com.notaflyingoose.goosemod.entities.boss.AbstractBoss;
import com.notaflyingoose.goosemod.items.ModItems;
import com.notaflyingoose.goosemod.world.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class HerobrineBoss extends AbstractBoss {
    @Nullable
    private static BlockPattern shrinePattern;
    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (entity) -> entity.getMobType() != MobType.UNDEAD && entity.attackable();
    private int attackAnimationTick;
    private boolean exploded;

    public HerobrineBoss(EntityType<? extends AbstractBoss> type, Level level) {
        super(type, level, BossEvent.BossBarColor.BLUE);
        this.getNavigation().setCanFloat(true);
        this.xpReward = 50;
        registerPhase(HerobrineBoss.class, HerobrineSummonMinionsPhase.class, 50, "SummonMinions");
        registerPhase(HerobrineBoss.class, HerobrineTeleportPhase.class, 100, "Teleport");
        registerPhase(HerobrineBoss.class, HerobrineLightningPhase.class, 50, "Lightning");
        registerPhase(HerobrineBoss.class, HerobrineMeteorPhase.class, 200, "Meteors");
    }

    public static AttributeSupplier createCustomAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 500)
                .add(Attributes.MOVEMENT_SPEED, 0.6)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ARMOR, 4)
                .add(Attributes.ATTACK_DAMAGE, 4)
                .add(Attributes.ATTACK_KNOCKBACK, 0.75f)
                .build();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        /*this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));*/

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        //this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 0, false, false, LIVING_ENTITY_SELECTOR));
    }

    @Override
    public void aiStep() {
        if (!this.getCurrentPhase().getInstance().getClass().equals(HerobrineTeleportPhase.class)) {
            Vec3 vec3 = this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
            if (!this.level.isClientSide && this.getAlternativeTarget() > 0) {
                Entity entity = this.level.getEntity(this.getAlternativeTarget());
                if (entity != null) {
                    double d0 = vec3.y;
                    if (this.getY() < entity.getY()) {
                        d0 = Math.max(0.0D, d0);
                        d0 = d0 + (0.3D - d0 * (double) 0.6F);
                    }

                    vec3 = new Vec3(vec3.x, d0, vec3.z);
                    Vec3 vec31 = new Vec3(entity.getX() - this.getX(), 0.0D, entity.getZ() - this.getZ());
                    if (vec31.horizontalDistanceSqr() > 9.0D) {
                        Vec3 vec32 = vec31.normalize();
                        vec3 = vec3.add(vec32.x * 0.3D - vec3.x * 0.6D, 0.0D, vec32.z * 0.3D - vec3.z * 0.6D);
                    }
                }
            }

            this.setDeltaMovement(vec3);
            if (vec3.horizontalDistanceSqr() > 0.05D) {
                this.setYRot((float) Mth.atan2(vec3.z, vec3.x) * (180F / (float) Math.PI) - 90.0F);
            }
        }

        super.aiStep();
        if (this.attackAnimationTick > 0) {
            --this.attackAnimationTick;
        }

        level.setRainLevel(100);
        level.setThunderLevel(100);
    }

    @Override
    protected SoundEvent getPhaseSoundEvent() {
        return null;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.isFall() || source.getMsgId().equals("lightningBolt") || source.isExplosion() || source.isFire();
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        double range = getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB aabb = AABB.unitCubeFromLowerCorner(position()).inflate(range, 10.0D, range);
        level.getEntitiesOfClass(Monster.class, aabb, LivingEntity::attackable).stream().filter((e) -> !(e instanceof HerobrineBoss)).forEach(LivingEntity::kill);
    }

    public boolean doHurtTarget(Entity entity) {
        startAttackAnimation();
        this.level.broadcastEntityEvent(this, (byte)4);
        float f = this.getAttackDamage();
        float f1 = (int)f > 0 ? f / 2.0F + this.random.nextInt((int)f) : f;
        boolean flag = entity.hurt(DamageSource.mobAttack(this), f1);
        if (flag) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.4F, 0.0D));
            this.doEnchantDamageEffects(this, entity);
        }

        return flag;
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    public boolean teleport() {
        double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D;
        double d2 = this.getY() + this.random.nextInt(16) - 8;
        double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D;
        return this.teleport(d1, d2, d3);
    }

    public boolean teleportTowards(Entity entity) {
        Vec3 vec3 = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
        vec3 = vec3.normalize();
        double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.x * 4.0D;
        double d2 = this.getY() + (this.random.nextInt(16) - 8) - vec3.y * 4.0D;
        double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.z * 4.0D;
        return this.teleport(d1, d2, d3);
    }

    private boolean teleport(double x, double y, double z) {
        BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos(x, y, z);

        while(blockpos.getY() > this.level.getMinBuildHeight() && !this.level.getBlockState(blockpos).getMaterial().blocksMotion()) {
            blockpos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level.getBlockState(blockpos);
        boolean flag = blockstate.getMaterial().blocksMotion();
        if (flag) {
            net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, x, y, z);
            if (event.isCanceled()) return false;
            return this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
        } else {
            return false;
        }
    }

    public static BlockPattern getOrCreateShrine() {
        if (shrinePattern == null) {
            shrinePattern = BlockPatternBuilder.start().aisle("   ", " # ", "   ").aisle(" & ", "&*&", " & ").aisle("%~%", "~%~", "%~%")
                    .where('#', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.FIRE)))
                    .where('*', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.NETHERRACK)))
                    .where('%', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.GOLD_BLOCK)))
                    .where('&', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.REDSTONE_TORCH)))
                    .where('~', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.NETHERITE_BLOCK))).build();
        }

        return shrinePattern;
    }

    public int getAttackAnimationTick() {
        return this.attackAnimationTick;
    }

    public void startAttackAnimation() {
        this.attackAnimationTick = 10;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source instanceof IndirectEntityDamageSource) {
            for(int i = 0; i < 64; ++i) {
                if (this.teleport()) {
                    return super.hurt(source, amount);
                }
            }

            return false;
        } else if (source != DamageSource.DROWN && !(source.getEntity() instanceof HerobrineBoss)) {
            Entity entity = source.getDirectEntity();
            if (entity instanceof AbstractArrow) {
                this.teleport();
                Entity cause = source.getEntity();
                if (cause instanceof Mob)
                    ((Mob) cause).setTarget(getTarget());
                return false;
            }
            if (entity instanceof Mob) {
                this.teleport();
                ((Mob) entity).setTarget(getTarget());
                return false;
            }

            if (entity != null && !(entity instanceof Player) && ((LivingEntity)entity).getMobType() == this.getMobType()) {
                return false;
            } else if (entity instanceof Player) {
                this.teleportTowards(entity);
                this.getLookControl().setLookAt(entity);
                if (this.distanceTo(entity) < 3)
                    this.doHurtTarget(entity);
                return super.hurt(source, amount);
            } else if (entity != null) {
                this.teleportTowards(entity);
                this.getLookControl().setLookAt(entity);
                this.doHurtTarget(entity);
                return false;
            }
            return super.hurt(source, amount);
        } else {
            return false;
        }
    }

    @SubscribeEvent
    public static void event(ItemTooltipEvent e) {
        e.getToolTip().add(new TextComponent("bruh"));
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource p_31464_, int p_31465_, boolean p_31466_) {
        super.dropCustomDeathLoot(p_31464_, p_31465_, p_31466_);
        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(ModItems.DRAGONITE_AXE);
            stack.enchant(ModEnchantments.THUNDER, 1);
            ModItems.addLore(stack, new TranslatableComponent("item.goosemod.dragonite_axe.description").getString());
            ItemEntity itementity = this.spawnAtLocation(stack);
            if (itementity != null) {
                itementity.setExtendedLifetime();
                itementity.setRemainingFireTicks(-1);
            } else {
                LOGGER.warn("itementity is null");
            }
        }
    }

    public boolean hasExploded() {
        return this.exploded;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }
}
