package com.notaflyingoose.goosemod.world.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.notaflyingoose.goosemod.GooseMod;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class MixerRecipe implements Recipe<MixerContainer> {
    private static final int MAX_SIZE = 3;
    private final ResourceLocation id;
    final String group;
    final ItemStack result;
    final Ingredient container;
    final NonNullList<Ingredient> ingredients;
    private final boolean isSimple;

    public MixerRecipe(ResourceLocation id, String group, ItemStack result, Ingredient container, NonNullList<Ingredient> ingredients) {
        this.id = id;
        this.group = group;
        this.result = result;
        this.container = container;
        this.ingredients = NonNullList.create();
        this.ingredients.add(container);
        this.ingredients.addAll(ingredients);
        this.isSimple = this.ingredients.stream().allMatch(Ingredient::isSimple);
    }

    public MixerRecipe(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> ingredients) {
        this.id = id;
        this.group = group;
        this.result = result;
        this.container = ingredients.get(0);
        this.ingredients = ingredients;
        this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.MIXER;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializer.MIXER_RECIPE;
    }

    public String getGroup() {
        return this.group;
    }

    public ItemStack getResultItem() {
        return this.result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public boolean matches(MixerContainer ingredients, Level level) {
        StackedContents stackedcontents = new StackedContents();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for(int j = 0; j < ingredients.getContainerSize(); ++j) {
            ItemStack itemstack = ingredients.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                if (isSimple)
                    stackedcontents.accountStack(itemstack, 1);
                else inputs.add(itemstack);
            }
        }

        return i == this.ingredients.size() && (isSimple ? stackedcontents.canCraft(this, null) : net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs,  this.ingredients) != null);
    }

    public ItemStack assemble(MixerContainer container) {
        return this.result.copy();
    }

    public boolean canCraftInDimensions(int p_44252_, int p_44253_) {
        return p_44252_ * p_44253_ >= this.ingredients.size();
    }

    public Ingredient getContainer() {
        return this.container;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MixerRecipe> {
        private static final ResourceLocation NAME = new ResourceLocation(GooseMod.MODID, "mixer");

        public MixerRecipe fromJson(ResourceLocation location, JsonObject json) {
            String s = GsonHelper.getAsString(json, "group", "");
            NonNullList<Ingredient> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for mixer recipe");
            } else if (ingredients.size() > MixerRecipe.MAX_SIZE) {
                throw new JsonParseException("Too many ingredients for mixer recipe. The maximum is " + MixerRecipe.MAX_SIZE);
            } else {
                ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
                Ingredient container = Ingredient.of(ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "container")));
                return new MixerRecipe(location, s, result, container, ingredients);
            }
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray jsonArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < jsonArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i));
                if (!ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        public MixerRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf bytes) {
            String s = bytes.readUtf();
            int i = bytes.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.fromNetwork(bytes));
            }

            ItemStack result = bytes.readItem();
            return new MixerRecipe(location, s, result, ingredients);
        }

        public void toNetwork(FriendlyByteBuf bytes, MixerRecipe recipe) {
            bytes.writeUtf(recipe.group);
            bytes.writeVarInt(recipe.ingredients.size());

            for(Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(bytes);
            }

            bytes.writeItem(recipe.result);
        }
    }

}
