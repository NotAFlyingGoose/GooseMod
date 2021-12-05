package com.notaflyingoose.goosemod.world.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThunderEnchantment extends Enchantment {
    private static final Logger LOGGER = LogManager.getLogger();
    protected ThunderEnchantment(Rarity rarity, EquipmentSlot... slots) {
        super(rarity, EnchantmentCategory.DIGGER, slots);
    }

    public int getMinCost(int level) {
        return 50;
    }

    public int getMaxCost(int level) {
        return 100;
    }

    public int getMaxLevel() {
        return 3;
    }

    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof AxeItem && super.canEnchant(stack);
    }

}
