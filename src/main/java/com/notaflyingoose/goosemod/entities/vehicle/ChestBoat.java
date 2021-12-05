package com.notaflyingoose.goosemod.entities.vehicle;

import com.notaflyingoose.goosemod.entities.ModEntityTypes;
import com.notaflyingoose.goosemod.items.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ChestBoat extends ContainerShip {
    public ChestBoat(EntityType<? extends ChestBoat> type, Level level) {
        super(type, level);
    }

    public ChestBoat(Level level, double x, double y, double z) {
        super(ModEntityTypes.CHEST_BOAT, level, x, y, z);
    }

    @Override
    public BlockState getDisplayBlockState() {
        return Blocks.CHEST.defaultBlockState();
    }

    @Override
    protected AbstractContainerMenu createMenu(int number, Inventory inventory) {
        return ChestMenu.threeRows(number, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return 9*3;
    }

    public Item getDropItem() {
        return switch (this.getBoatType()) {
            default -> ModItems.OAK_CHEST_BOAT;
            case SPRUCE -> ModItems.SPRUCE_CHEST_BOAT;
            case BIRCH -> ModItems.BIRCH_CHEST_BOAT;
            case JUNGLE -> ModItems.JUNGLE_CHEST_BOAT;
            case ACACIA -> ModItems.ACACIA_CHEST_BOAT;
            case DARK_OAK -> ModItems.DARK_OAK_CHEST_BOAT;
        };
    }

    @Override
    protected int getMaxPassengers() {
        return 1;
    }
}
