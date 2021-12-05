package com.notaflyingoose.goosemod.entities.vehicle;

import com.notaflyingoose.goosemod.entities.ModEntityTypes;
import com.notaflyingoose.goosemod.items.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FurnaceBoat extends AbstractShip {
    private static final EntityDataAccessor<Integer> DATA_ID_FUEL = SynchedEntityData.defineId(FurnaceBoat.class, EntityDataSerializers.INT);
    public double xPush;
    public double zPush;
    private static final Ingredient INGREDIENT = Ingredient.of(Items.COAL, Items.CHARCOAL);

    public FurnaceBoat(EntityType<? extends FurnaceBoat> type, Level level) {
        super(type, level);
    }

    public FurnaceBoat(Level level, double x, double y, double z) {
        super(ModEntityTypes.FURNACE_BOAT, level, x, y, z);
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            ItemStack itemstack = player.getItemInHand(hand);
            int fuel = getFuelTicks();
            if (INGREDIENT.test(itemstack) && fuel + 3600 <= 32000) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                fuel += 3600;
            }

            if (fuel > 0) {
                this.xPush = this.getX() - player.getX();
                this.zPush = this.getZ() - player.getZ();
            }
            setFuelTicks(fuel);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.interact(player, hand);
    }

    @Override
    protected int getMaxPassengers() {
        return 1;
    }

    public void tick() {
        super.tick();
        Boat.Status status = getStatus();
        if (!this.level.isClientSide()) {
            if (getFuelTicks() > 0) {
                if (status == Boat.Status.UNDER_WATER || status == Boat.Status.UNDER_FLOWING_WATER)
                    setFuelTicks(0);
                else
                    setFuelTicks(getFuelTicks()-1);
            }
        }

        if (getFuelTicks() <= 0) {
            this.speedMultiplier = 0.9f;
        } else {
            this.speedMultiplier = 1.07f;
        }

        if (getFuelTicks() > 0 && this.random.nextInt(4) == 0) {
            this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8D, this.getZ(), 0.0D, 0.0D, 0.0D);
        }

    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_FUEL, 0);
    }

    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Fuel", getFuelTicks());
    }

    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setFuelTicks(tag.getInt("Fuel"));
    }

    @Override
    public BlockState getDisplayBlockState() {
        return Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.SOUTH).setValue(FurnaceBlock.LIT, this.hasFuel());
    }

    protected boolean hasFuel() {
        return getFuelTicks() > 0;
    }

    protected int getFuelTicks() {
        return this.entityData.get(DATA_ID_FUEL);
    }

    protected void setFuelTicks(int ticks) {
        this.entityData.set(DATA_ID_FUEL, ticks);
    }

    public Item getDropItem() {
        return switch (this.getBoatType()) {
            default -> ModItems.OAK_FURNACE_BOAT;
            case SPRUCE -> ModItems.SPRUCE_FURNACE_BOAT;
            case BIRCH -> ModItems.BIRCH_FURNACE_BOAT;
            case JUNGLE -> ModItems.JUNGLE_FURNACE_BOAT;
            case ACACIA -> ModItems.ACACIA_FURNACE_BOAT;
            case DARK_OAK -> ModItems.DARK_OAK_FURNACE_BOAT;
        };
    }
}
