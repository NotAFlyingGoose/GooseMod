package com.notaflyingoose.goosemod.entities.vehicle;

import com.notaflyingoose.goosemod.entities.ModEntityTypes;
import com.notaflyingoose.goosemod.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnderChestBoat extends AbstractShip {
    private static final Component CONTAINER_TITLE = new TranslatableComponent("entity.goosemod.ender_chest_boat");

    public EnderChestBoat(EntityType<? extends EnderChestBoat> type, Level level) {
        super(type, level);
    }

    public EnderChestBoat(Level level, double x, double y, double z) {
        super(ModEntityTypes.ENDER_CHEST_BOAT, level, x, y, z);
    }

    @Override
    public BlockState getDisplayBlockState() {
        return Blocks.ENDER_CHEST.defaultBlockState();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            Level level = player.level;
            PlayerEnderChestContainer enderChestContainer = player.getEnderChestInventory();
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            } else {
                //enderChestContainer.setActiveChest(enderchestblockentity);
                player.openMenu(new SimpleMenuProvider((p_53124_, p_53125_, p_53126_) -> {
                    return ChestMenu.threeRows(p_53124_, p_53125_, enderChestContainer);
                }, CONTAINER_TITLE));
                player.awardStat(Stats.OPEN_ENDERCHEST);
                PiglinAi.angerNearbyPiglins(player, true);
                return InteractionResult.CONSUME;
            }
        }
        return super.interact(player, hand);
    }

    public Item getDropItem() {
        return switch (this.getBoatType()) {
            default -> ModItems.OAK_ENDER_CHEST_BOAT;
            case SPRUCE -> ModItems.SPRUCE_ENDER_CHEST_BOAT;
            case BIRCH -> ModItems.BIRCH_ENDER_CHEST_BOAT;
            case JUNGLE -> ModItems.JUNGLE_ENDER_CHEST_BOAT;
            case ACACIA -> ModItems.ACACIA_ENDER_CHEST_BOAT;
            case DARK_OAK -> ModItems.DARK_OAK_ENDER_CHEST_BOAT;
        };
    }

    @Override
    protected int getMaxPassengers() {
        return 1;
    }
}
