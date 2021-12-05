package com.notaflyingoose.goosemod.world.enchantment;

import com.notaflyingoose.goosemod.GooseMod;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {
    public static final Enchantment GAY = new GayEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.values()).setRegistryName(GooseMod.MODID, "gay");
    public static final Enchantment THUNDER = new ThunderEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND).setRegistryName(GooseMod.MODID, "thunder");
    //public static final Enchantment POLLYMORPH = new PollymorphEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND).setRegistryName(GooseMod.MODID, "pollymorph");
}
