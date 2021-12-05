package com.notaflyingoose.goosemod.entities.vehicle;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;

public abstract class ContainerShip extends AbstractShip implements Container, MenuProvider {
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);

    public ContainerShip(EntityType<? extends ContainerShip> type, Level level) {
        super(type, level);
    }

    public ContainerShip(EntityType<? extends ContainerShip> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    public void remove(RemovalReason reason) {
        if (!this.level.isClientSide && reason.shouldDestroy()) {
            Containers.dropContents(this.level, this, this);
        }

        super.remove(reason);
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            InteractionResult ret = super.interact(player, hand);
            if (ret.consumesAction()) return ret;
            player.openMenu(this);
            if (!player.level.isClientSide) {
                this.gameEvent(GameEvent.CONTAINER_OPEN, player);
                PiglinAi.angerNearbyPiglins(player, true);
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.SUCCESS;
            }
        }
        return super.interact(player, hand);
    }

    protected void addAdditionalSaveData(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, this.itemStacks);
    }

    protected void readAdditionalSaveData(CompoundTag tag) {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.itemStacks);
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.itemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public ItemStack getItem(int p_38218_) {
        return this.itemStacks.get(p_38218_);
    }

    public ItemStack removeItem(int p_38220_, int p_38221_) {
        return ContainerHelper.removeItem(this.itemStacks, p_38220_, p_38221_);
    }

    public ItemStack removeItemNoUpdate(int p_38244_) {
        ItemStack itemstack = this.itemStacks.get(p_38244_);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.itemStacks.set(p_38244_, ItemStack.EMPTY);
            return itemstack;
        }
    }

    public void setItem(int p_38225_, ItemStack p_38226_) {
        this.itemStacks.set(p_38225_, p_38226_);
        if (!p_38226_.isEmpty() && p_38226_.getCount() > this.getMaxStackSize()) {
            p_38226_.setCount(this.getMaxStackSize());
        }

    }

    public SlotAccess getSlot(final int p_150257_) {
        return p_150257_ >= 0 && p_150257_ < this.getContainerSize() ? new SlotAccess() {
            public ItemStack get() {
                return ContainerShip.this.getItem(p_150257_);
            }

            public boolean set(ItemStack p_150265_) {
                ContainerShip.this.setItem(p_150257_, p_150265_);
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

    @Nullable
    public AbstractContainerMenu createMenu(int number, Inventory inventory, Player player) {
        if (player.isSpectator()) {
            return null;
        } else {
            return this.createMenu(number, inventory);
        }
    }

    protected abstract AbstractContainerMenu createMenu(int number, Inventory inventory);

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
