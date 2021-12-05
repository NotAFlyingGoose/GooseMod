package com.notaflyingoose.goosemod.gui;

import com.notaflyingoose.goosemod.blocks.ModBlocks;
import com.notaflyingoose.goosemod.world.crafting.MixerContainer;
import com.notaflyingoose.goosemod.world.crafting.MixerRecipe;
import com.notaflyingoose.goosemod.world.crafting.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class MixerMenu extends AbstractContainerMenu {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResultContainer resultSlots = new ResultContainer();
    final MixerContainer inputSlots = new MixerContainer(this, 4) {
        public void setChanged() {
            super.setChanged();
            MixerMenu.this.slotsChanged(this);
        }
    };
    private final ContainerLevelAccess access;
    private final Inventory inventory;
    private final Player player;

    public MixerMenu(int id, Inventory inventory) {
        this(id, inventory, ContainerLevelAccess.NULL);
    }

    public MixerMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.MIXER, id);
        this.access = access;
        this.inventory = inventory;
        this.player = inventory.player;
        this.addSlot(new Slot(this.resultSlots, 0, 124, 35) {
            private int removeCount;

            public boolean mayPlace(ItemStack p_40178_) {
                return false;
            }

            public ItemStack remove(int p_40173_) {
                if (this.hasItem()) {
                    this.removeCount += Math.min(p_40173_, this.getItem().getCount());
                }

                return super.remove(p_40173_);
            }

            protected void onQuickCraft(ItemStack p_40180_, int p_40181_) {
                this.removeCount += p_40181_;
                this.checkTakeAchievements(p_40180_);
            }

            protected void onSwapCraft(int p_40183_) {
                this.removeCount += p_40183_;
            }

            protected void checkTakeAchievements(ItemStack p_40185_) {
                if (this.removeCount > 0) {
                    p_40185_.onCraftedBy(MixerMenu.this.player.level, MixerMenu.this.player, this.removeCount);
                    net.minecraftforge.fmllegacy.hooks.BasicEventHooks.firePlayerCraftingEvent(MixerMenu.this.player, p_40185_, MixerMenu.this.inputSlots);
                }

                if (this.container instanceof RecipeHolder) {
                    ((RecipeHolder)this.container).awardUsedRecipes(MixerMenu.this.player);
                }

                this.removeCount = 0;
            }

            public void onTake(Player p_150638_, ItemStack p_150639_) {
                this.checkTakeAchievements(p_150639_);
                net.minecraftforge.common.ForgeHooks.setCraftingPlayer(p_150638_);
                NonNullList<ItemStack> nonnulllist = p_150638_.level.getRecipeManager().getRemainingItemsFor(ModRecipeTypes.MIXER, MixerMenu.this.inputSlots, p_150638_.level);
                net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);
                for(int i = 0; i < nonnulllist.size(); ++i) {
                    ItemStack itemstack = MixerMenu.this.inputSlots.getItem(i);
                    ItemStack itemstack1 = nonnulllist.get(i);
                    if (!itemstack.isEmpty()) {
                        MixerMenu.this.inputSlots.removeItem(i, 1);
                        itemstack = MixerMenu.this.inputSlots.getItem(i);
                    }

                    if (!itemstack1.isEmpty()) {
                        if (itemstack.isEmpty()) {
                            MixerMenu.this.inputSlots.setItem(i, itemstack1);
                        } else if (ItemStack.isSame(itemstack, itemstack1) && ItemStack.tagMatches(itemstack, itemstack1)) {
                            itemstack1.grow(itemstack.getCount());
                            MixerMenu.this.inputSlots.setItem(i, itemstack1);
                        } else if (!MixerMenu.this.player.getInventory().add(itemstack1)) {
                            MixerMenu.this.player.drop(itemstack1, false);
                        }
                    }
                }

            }
        });

        this.addSlot(new Slot(this.inputSlots, 0, 48, 53) {
            public boolean mayPlace(ItemStack stack) {
                List<MixerRecipe> recipes = MixerMenu.this.player.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.MIXER);
                for (MixerRecipe recipe : recipes) {
                    for (ItemStack item : recipe.getContainer().getItems())
                        if (stack.is(item.getItem()))
                            return true;
                }
                return false;
            }
        });
        for(int i = 1; i < 4; ++i) {
            this.addSlot(new Slot(this.inputSlots, i, 12 + i * 18, 17));
        }

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }

    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        this.access.execute((level, blockPos) -> {
            updateAssembleSlots(level);
        });

    }

    private void updateAssembleSlots(Level level) {
        if (!level.isClientSide) {
            ServerPlayer serverplayer = (ServerPlayer)player;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<MixerRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(ModRecipeTypes.MIXER, this.inputSlots, level);
            if (optional.isPresent()) {
                MixerRecipe labrecipe = optional.get();
                if (this.resultSlots.setRecipeUsed(level, serverplayer, labrecipe)) {
                    itemstack = labrecipe.assemble(this.inputSlots);
                }
            }

            this.resultSlots.setItem(0, itemstack);
            setRemoteSlot(0, itemstack);
            serverplayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemstack));
        }

        this.broadcastChanges();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> {
            this.clearContainer(player, this.inputSlots);
        });
    }

    @Override
    public boolean stillValid(Player p_39368_) {
        return stillValid(this.access, p_39368_, ModBlocks.MIXER);
    }

    public ItemStack quickMoveStack(Player player, int slotId) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotId == 0) {
                this.access.execute((level, pos) -> itemstack1.getItem().onCraftedBy(itemstack1, level, player));
                if (!this.moveItemStackTo(itemstack1, 10, 41, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (slotId >= 5 && slotId < 41) {
                if (!this.moveItemStackTo(itemstack1, 1, 10, false)) {
                    if (slotId < 37) {
                        if (!this.moveItemStackTo(itemstack1, 32, 41, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemstack1, 5, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 5, 41, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            if (slotId == 0) {
                player.drop(itemstack1, false);
            }
        }

        return itemstack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack p_39381_, Slot p_39382_) {
        return p_39382_.container != this.resultSlots && super.canTakeItemForPickAll(p_39381_, p_39382_);
    }
}
