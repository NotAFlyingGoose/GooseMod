package com.notaflyingoose.goosemod.items;

import com.notaflyingoose.goosemod.items.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
    DREAM("goosemod:dream", 16, new int[]{ 1, 4, 5, 2 }, 20,
            SoundEvents.ARMOR_EQUIP_LEATHER, 1, () -> Ingredient.of(Items.EMERALD),0.0f),
    DRAGONITE("goosemod:dragonite", 45, new int[]{ 4, 7, 9, 4 }, 20,
    SoundEvents.ARMOR_EQUIP_NETHERITE, 3, () -> Ingredient.of(ModItems.DRAGON_SCALES),0.5f);

    private final static int[] MAX_DAMAGE_ARRAY = new int[]{ 11, 16, 15, 13 };

    private final String name;
    private final int durability;
    private final int[] defense;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final int toughness;
    private final Supplier<Ingredient> repairMaterial;
    private final float knockbackResistance;

    ModArmorMaterials(String name, int durability, int[] defense, int enchantability, SoundEvent equipSound, int toughness, Supplier<Ingredient> repairMaterial, float knockbackResistance) {
        this.name = name;
        this.durability = durability;
        this.defense = defense;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.repairMaterial = repairMaterial;
        this.knockbackResistance = knockbackResistance;
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot) {
        return MAX_DAMAGE_ARRAY[slot.getIndex()] * this.durability;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot) {
        return this.defense[slot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairMaterial.get();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
