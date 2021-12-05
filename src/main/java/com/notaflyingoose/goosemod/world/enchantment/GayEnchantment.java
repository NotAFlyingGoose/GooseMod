package com.notaflyingoose.goosemod.world.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Random;

public class GayEnchantment extends Enchantment {
    protected GayEnchantment(Rarity rarity, EquipmentSlot... slots) {
        super(rarity, EnchantmentCategory.BREAKABLE, slots);
    }

    public int getMinCost(int level) {
        return 5 + (level - 1) * 8;
    }

    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean canEnchant(ItemStack level) {
        return true;
    }

}
