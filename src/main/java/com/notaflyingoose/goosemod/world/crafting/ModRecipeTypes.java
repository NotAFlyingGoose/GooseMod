package com.notaflyingoose.goosemod.world.crafting;

import com.notaflyingoose.goosemod.GooseMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipeTypes {
    public static final RecipeType<MixerRecipe> MIXER = register("mixer");

    static <T extends Recipe<?>> RecipeType<T> register(final String id) {
        return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(GooseMod.MODID, id), new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }

}
