package com.notaflyingoose.goosemod.world.crafting;

import com.notaflyingoose.goosemod.GooseMod;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipeSerializer {
    public static final RecipeSerializer<MixerRecipe> MIXER_RECIPE = (RecipeSerializer<MixerRecipe>) new MixerRecipe.Serializer().setRegistryName(GooseMod.MODID, "mixer");
}
