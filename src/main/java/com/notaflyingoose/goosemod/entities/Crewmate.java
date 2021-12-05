package com.notaflyingoose.goosemod.entities;

import com.notaflyingoose.goosemod.items.ModItems;
import com.notaflyingoose.goosemod.world.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.Random;

public class Crewmate extends Animal {

    private static final EntityDataAccessor<Byte> DATA_COLOR_ID = SynchedEntityData.defineId(Crewmate.class, EntityDataSerializers.BYTE);
    public static final Ingredient TEMPTATION_ITEMS = Ingredient.of(ModItems.BIG_MICK);
    private int ticksUntilNextAlert;
    private static final UniformInt ALERT_INTERVAL = TimeUtil.rangeOfSeconds(4, 6);

    public Crewmate(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier createCustomAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 16)
                .build();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_COLOR_ID, (byte)0);
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLOR_ID) & 15);
    }

    public byte getColorRaw() {
        return this.entityData.get(DATA_COLOR_ID);
    }

    public void setColor(DyeColor p_29856_) {
        byte b0 = this.entityData.get(DATA_COLOR_ID);
        this.entityData.set(DATA_COLOR_ID, (byte)(b0 & 240 | p_29856_.getId() & 15));
    }

    public void setColorRaw(byte colorData) {
        this.entityData.set(DATA_COLOR_ID, colorData);
    }

    private DyeColor getOffspringColor(Animal parentA, Animal parentB) {
        DyeColor dyecolor = ((Crewmate)parentA).getColor();
        DyeColor dyecolor1 = ((Crewmate)parentB).getColor();
        CraftingContainer craftingcontainer = makeContainer(dyecolor, dyecolor1);
        return this.level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingcontainer, this.level)
                .map((p_29828_) -> p_29828_.assemble(craftingcontainer))
                .map(ItemStack::getItem).filter(DyeItem.class::isInstance)
                .map(DyeItem.class::cast)
                .map(DyeItem::getDyeColor).orElseGet(() -> this.level.random.nextBoolean() ? dyecolor : dyecolor1);
    }

    public static DyeColor getRandomAmogusColor(Random random) {
        int i = random.nextInt(95);
        if (i < 10) {
            return DyeColor.RED;
        } else if (i < 19) {
            return DyeColor.BLACK;
        } else if (i < 28) {
            return DyeColor.WHITE;
        } else if (i < 37) {
            return DyeColor.BLUE;
        } else if (i < 46) {
            return DyeColor.CYAN;
        } else if (i < 45) {
            return DyeColor.YELLOW;
        } else if (i < 54) {
            return DyeColor.PINK;
        } else if (i < 63) {
            return DyeColor.PURPLE;
        } else if (i < 72) {
            return DyeColor.ORANGE;
        } else if (i < 81) {
            return DyeColor.LIME;
        } else if (i < 90) {
            return DyeColor.GREEN;
        } else {
            return DyeColor.BROWN;
        }
    }

    private static CraftingContainer makeContainer(DyeColor colorA, DyeColor colorB) {
        CraftingContainer craftingcontainer = new CraftingContainer(new AbstractContainerMenu((MenuType)null, -1) {
            public boolean stillValid(Player p_29888_) {
                return false;
            }
        }, 2, 1);
        craftingcontainer.setItem(0, new ItemStack(DyeItem.byColor(colorA)));
        craftingcontainer.setItem(1, new ItemStack(DyeItem.byColor(colorB)));
        return craftingcontainer;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_29835_, DifficultyInstance p_29836_, MobSpawnType p_29837_, @Nullable SpawnGroupData p_29838_, @Nullable CompoundTag p_29839_) {
        this.setColor(Crewmate.getRandomAmogusColor(p_29835_.getRandom()));
        return super.finalizeSpawn(p_29835_, p_29836_, p_29837_, p_29838_, p_29839_);
    }

    @Override
    public Crewmate getBreedOffspring(ServerLevel level, AgeableMob mob) {
        Crewmate otherParent = (Crewmate)mob;
        Crewmate baby = ModEntityTypes.CREWMATE.create(level);
        baby.setColor(this.getOffspringColor(this, otherParent));
        return baby;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new BreedGoal(this, 5));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Crewmate.class, 10));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.getTarget() != null) {
            this.maybeAlertOthers();
        }
    }

    private void maybeAlertOthers() {
        if (this.ticksUntilNextAlert > 0) {
            --this.ticksUntilNextAlert;
        } else {
            if (this.getSensing().hasLineOfSight(this.getTarget())) {
                this.alertOthers();
            }

            this.ticksUntilNextAlert = ALERT_INTERVAL.sample(this.random);
        }
    }

    private void alertOthers() {
        double d0 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB aabb = AABB.unitCubeFromLowerCorner(this.position()).inflate(d0, 10.0D, d0);
        this.level.getEntitiesOfClass(Crewmate.class, aabb, EntitySelector.NO_SPECTATORS).stream()
                .filter((entity) -> entity != this)
                .filter((entity) -> entity.getTarget() == null)
                .filter((entity) -> !entity.isAlliedTo(this.getTarget()))
                .forEach((entity) -> entity.goalSelector.getRunningGoals()
                        .filter((goal) -> goal.getGoal() instanceof PanicGoal)
                        .forEach(WrappedGoal::start));
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setColorRaw(nbt.getByte("Color"));
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("Color", getColorRaw());
    }

    @Override
    protected int getExperienceReward(Player player) {
        return 1;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.CREWMATE_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.CREWMATE_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.CREWMATE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSoundEvents.CREWMATE_STEP, 0.15F, 1.0F);
    }

}
