package com.notaflyingoose.goosemod.entities.boss;

import com.notaflyingoose.goosemod.effects.ModMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractBoss extends Monster {
    protected static final Logger LOGGER = LogManager.getLogger();
    private final ServerBossEvent bossEvent;
    private static final EntityDataAccessor<Integer> DATA_TARGET = SynchedEntityData.defineId(AbstractBoss.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> DATA_PHASE = SynchedEntityData.defineId(AbstractBoss.class, EntityDataSerializers.BYTE);
    private BossPhase<?, ?>[] phases = new BossPhase[]{};
    private int phaseTickCount;
    private BossPhase<?, ?> currentPhase = null;
    protected static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(20.0D).selector(LivingEntity::attackable);

    protected AbstractBoss(EntityType<? extends Monster> entity, Level level, BossEvent.BossBarColor color) {
        super(entity, level);
        bossEvent = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), color, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);
        this.setHealth(this.getMaxHealth());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    public BossPhase<?, ?> getById(int id) {
        return id >= 0 && id < this.phases.length ? this.phases[id] : this.phases[0];
    }

    public int getPhases() {
        return this.phases.length;
    }

    protected <T extends AbstractBoss, I extends PhaseInstance> BossPhase<T, I> registerPhase(Class<T> bossClass, Class<I> instanceClass, int ticks, String name) {
        BossPhase<T, I> phase = new BossPhase<>(phases.length, bossClass, instanceClass, ticks, name);
        this.phases = Arrays.copyOf(phases, phases.length + 1);
        this.phases[phase.getId()] = phase;
        if (phase.getId() == 0) {
            setCurrentPhase(phase);
        }
        return phase;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TARGET, 0);
        this.entityData.define(DATA_PHASE, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.phaseTickCount = nbt.getInt("PhaseTicks");
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
        if (nbt.contains("BossPhase") && this.getPhases() > 0) {
            this.setCurrentPhase(getById(nbt.getInt("BossPhase")));
        }
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.getCurrentPhase() == null)
            nbt.putInt("BossPhase", 0);
        else
            nbt.putInt("BossPhase", this.getCurrentPhase().getId());
    }

    public void setCurrentPhase(BossPhase<?, ?> phase) {
        if (this.currentPhase == null || phase != this.currentPhase) {
            if (this.currentPhase != null) {
                this.currentPhase.getInstance().end();
            }

            phase.createInstance(this);
            if (this.level.isClientSide) this.getEntityData().set(DATA_PHASE, (byte) phase.getId());
            else this.currentPhase = phase;
            this.phaseTickCount = phase.getTicks();

            LOGGER.info("Boss is now in phase {} on the {}", phase, this.level.isClientSide ? "client" : "server");
            phase.getInstance().begin();
        }
    }

    public void setCurrentPhase(int id) {
        setCurrentPhase(getById(id));
    }

    public void setCurrentPhase() {
        BossPhase<?, ?> currentPhase = getCurrentPhase();
        if (this.getPhases() == 2) {
            if (currentPhase.getId() == 0) setCurrentPhase(1);
            else setCurrentPhase(0);
        } else if (this.getPhases() > 2) {
            int id;
            do {
                id = this.level.random.nextInt(getPhases());
            } while (id == currentPhase.getId());
            setCurrentPhase(id);
        }
    }

    protected BossPhase<?, ?> getCurrentPhase() {
        return !this.level.isClientSide ? this.currentPhase : this.getById(this.entityData.get(DATA_PHASE));
    }

    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    public void setCustomName(@Nullable Component p_31476_) {
        super.setCustomName(p_31476_);
        this.bossEvent.setName(this.getDisplayName());
    }


    public void aiStep() {
        super.aiStep();

        if (this.phaseTickCount > 0) this.phaseTickCount--;
        else setCurrentPhase();

        if (this.level.isClientSide) {
            if (getCurrentPhase() != null) {
                PhaseInstance phaseInstance = getCurrentPhase().getInstance();
                phaseInstance.doClientTick();
            }
        } else {
            BossPhase<?, ?> current = getCurrentPhase();
            if (current != null) {
                PhaseInstance phaseInstance = getCurrentPhase().getInstance();
                phaseInstance.doServerTick();
            }
        }
    }

    protected void customServerAiStep() {
        super.customServerAiStep();

        int target = this.getAlternativeTarget();
        if (target > 0) {
            LivingEntity livingentity = (LivingEntity)this.level.getEntity(target);
            if (livingentity == null || !this.canAttack(livingentity) || this.distanceToSqr(livingentity) > 900.0D || !this.hasLineOfSight(livingentity)) {
                this.setAlternativeTarget(0);
            }
        }
        if (target <= 0) {
            List<LivingEntity> list = this.level.getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0D, 8.0D, 20.0D));
            if (!list.isEmpty()) {
                LivingEntity newTarget = list.get(this.random.nextInt(list.size()));
                this.setAlternativeTarget(newTarget.getId());
            }
        }

        /*if (this.tickCount % 20 == 0) {
            this.heal(1.0F);
        }*/
        bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public LivingEntity getTarget() {
        List<Player> list = this.level.getNearbyPlayers(TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0D, 8.0D, 20.0D));
        if (!list.isEmpty()) {
            return list.get(this.random.nextInt(list.size()));
        }
        if (this.getAlternativeTarget() > 0)
            return (LivingEntity) this.level.getEntity(this.getAlternativeTarget());
        return super.getTarget();
    }

    public int getAlternativeTarget() {
        return this.entityData.get(DATA_TARGET);
    }

    public void setAlternativeTarget(int id) {
        this.entityData.set(DATA_TARGET, id);
    }

/*    public void tick() {
        super.tick();
        if (this.level.isClientSide && currentPhase != null) {
            BossPhase<?, ?> phase = this.getCurrentPhase();
            double d0 = phase.spellColor[0];
            double d1 = phase.spellColor[1];
            double d2 = phase.spellColor[2];
            float f = this.yBodyRot * ((float)Math.PI / 180F) + Mth.cos((float)this.tickCount * 0.6662F) * 0.25F;
            float f1 = Mth.cos(f);
            float f2 = Mth.sin(f);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + (double)f1 * 0.6D, this.getY() + 1.8D, this.getZ() + (double)f2 * 0.6D, d0, d1, d2);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - (double)f1 * 0.6D, this.getY() + 1.8D, this.getZ() - (double)f2 * 0.6D, d0, d1, d2);
        }

    }*/

    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }

    public boolean causeFallDamage(float p_149589_, float p_149590_, DamageSource p_149591_) {
        return false;
    }

    public boolean addEffect(MobEffectInstance effect, @Nullable Entity entity) {
        return false;
    }

    public boolean canChangeDimensions() {
        return false;
    }

    public boolean canBeAffected(MobEffectInstance effect) {
        return effect.getEffect() == ModMobEffects.HIGH ? false : super.canBeAffected(effect);
    }

    public int getPhaseTicks() {
        return this.phaseTickCount;
    }

    protected abstract SoundEvent getPhaseSoundEvent();
}
