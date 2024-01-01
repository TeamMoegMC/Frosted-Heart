/*
 * Copyright (c) 2022-2024 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.teammoeg.frostedheart.content.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.FHItems;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;

public interface DefrostRecipe extends Recipe<Container> {
    Ingredient getIngredient();

    ItemStack[] getIss();
    public static class Serializer<T extends DefrostRecipe> extends IERecipeSerializer<T> {
        @FunctionalInterface
        public interface DRFactory<T extends DefrostRecipe> {
            T create(ResourceLocation p_i50030_1_, String p_i50030_2_, Ingredient p_i50030_3_, ItemStack[] results, float p_i50030_5_, int p_i50030_6_);
        }

        DRFactory<T> factory;

        public Serializer(DRFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public ItemStack getIcon() {
            return new ItemStack(FHItems.frozen_seeds);
        }

        public ItemStack readOutput(JsonElement json) {
            if (json.isJsonObject())
                return ShapedRecipe.itemFromJson(json.getAsJsonObject());
            String s1 = json.getAsString();
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            return new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
                return new IllegalStateException("Item: " + s1 + " does not exist");
            }));
        }

        @Override
        public T readFromJson(ResourceLocation recipeId, JsonObject json) {
            String s = GsonHelper.getAsString(json, "group", "");
            JsonElement jsonelement = GsonHelper.isArrayNode(json, "ingredient") ? GsonHelper.getAsJsonArray(json, "ingredient")
                    : GsonHelper.getAsJsonObject(json, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(jsonelement);
            ItemStack[] itemstacks = null;

            if (json.get("results") != null && json.get("results").isJsonArray()) {
                JsonArray ja = json.get("results").getAsJsonArray();
                itemstacks = new ItemStack[ja.size()];
                int i = -1;
                for (JsonElement je : ja)
                    itemstacks[++i] = readOutput(je);
            } else if (json.get("result") != null) {
                itemstacks = new ItemStack[1];
                itemstacks[0] = readOutput(json.get("result"));
            } else
                throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
            float f = GsonHelper.getAsFloat(json, "experience", 0.0F);
            int i = GsonHelper.getAsInt(json, "cookingtime", 100);
            return factory.create(recipeId, s, ingredient, itemstacks != null ? itemstacks : new ItemStack[0], f, i);
        }

        @Nullable
        @Override
        public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String s = buffer.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int itemlen = buffer.readVarInt();
            ItemStack[] itemstacks = new ItemStack[itemlen];
            for (int i = 0; i < itemlen; i++)
                itemstacks[i] = buffer.readItem();
            float f = buffer.readFloat();
            int i = buffer.readVarInt();
            return factory.create(recipeId, s, ingredient, itemstacks, f, i);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DefrostRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            recipe.getIngredient().toNetwork(buffer);
            buffer.writeVarInt(recipe.getIss().length);
            for (int i = 0; i < recipe.getIss().length; i++)
                buffer.writeItem(recipe.getIss()[i]);

        }
    }
}
