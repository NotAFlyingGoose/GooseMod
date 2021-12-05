package com.notaflyingoose.goosemod.entities.vehicle;

import com.notaflyingoose.goosemod.entities.ModEntityTypes;
import com.notaflyingoose.goosemod.items.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class LongBoat extends ContainerShip {

    public LongBoat(EntityType<? extends LongBoat> type, Level level) {
        super(type, level);
        this.waterFriction = 0.95f;
    }

    public LongBoat(Level level, double x, double y, double z) {
        super(ModEntityTypes.LONG_BOAT, level, x, y, z);
        this.waterFriction = 0.95f;
    }

    @Override
    public BlockState getDisplayBlockState() {
        return Blocks.CHEST.defaultBlockState();
    }

    @Override
    public float getDisplayBlockOffset() {
        return -26.5f;
    }

    public Item getDropItem() {
        return switch (this.getBoatType()) {
            default -> ModItems.OAK_LONG_BOAT;
            case SPRUCE -> ModItems.SPRUCE_LONG_BOAT;
            case BIRCH -> ModItems.BIRCH_LONG_BOAT;
            case JUNGLE -> ModItems.JUNGLE_LONG_BOAT;
            case ACACIA -> ModItems.ACACIA_LONG_BOAT;
            case DARK_OAK -> ModItems.DARK_OAK_LONG_BOAT;
        };
    }

    @Override
    protected int getMaxPassengers() {
        return 2;
    }

    @Override
    protected AbstractContainerMenu createMenu(int number, Inventory inventory) {
        return ChestMenu.sixRows(number, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return 9*6;
    }

    public void positionRider(Entity rider) {
        super.positionRider(rider);
        if (this.hasPassenger(rider)) {
            float f = 0.0F;
            float f1 = (float) ((this.isRemoved() ? (double) 0.01F : this.getPassengersRidingOffset()) + rider.getMyRidingOffset());
            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(rider);
                if (i != 0) {
                    f = 1F;
                }
            }
            if (rider instanceof Animal) {
                f = (float) ((double) f + 0.2D);
            }

            Vec3 vec3 = (new Vec3(f, 0.0D, 0.0D)).yRot(-this.getYRot() * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
            rider.setPos(this.getX() + vec3.x, this.getY() + (double) f1, this.getZ() + vec3.z);
            //rider.setYRot(rider.getYRot() + this.deltaRotation);
            //rider.setYHeadRot(rider.getYHeadRot() + this.deltaRotation/2);
            this.clampRotation(rider);
            if (rider instanceof Animal && this.getPassengers().size() > 1) {
                int j = rider.getId() % 2 == 0 ? 90 : 270;
                rider.setYBodyRot(((Animal)rider).yBodyRot + (float)j);
                rider.setYHeadRot(rider.getYHeadRot() + (float)j);
            }
        }
    }
}
