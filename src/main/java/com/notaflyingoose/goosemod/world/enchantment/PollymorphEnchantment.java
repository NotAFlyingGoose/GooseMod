package com.notaflyingoose.goosemod.world.enchantment;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class PollymorphEnchantment extends Enchantment {
    private static final Logger LOGGER = LogManager.getLogger();
    protected PollymorphEnchantment(Rarity rarity, EquipmentSlot... slots) {
        super(rarity, EnchantmentCategory.BREAKABLE, slots);
    }

    public int getMinCost(int level) {
        return 100;
    }

    public int getMaxCost(int level) {
        return 200;
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean canEnchant(ItemStack stack) {
        ResourceLocation id = stack.getItem().getRegistryName();
        return id != null && id.toString().equals("minecraft:stick");
    }

}
