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

package com.teammoeg.frostedheart.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.teammoeg.frostedheart.util.RegistryUtils;
import com.teammoeg.frostedheart.util.io.SerializeUtil;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class ShapelessCopyDataRecipe extends ShapelessRecipe implements IFinishedRecipe {
    public static class Serializer extends IERecipeSerializer<ShapelessCopyDataRecipe> {
        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for (int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        @Override
        public ItemStack getIcon() {
            return new ItemStack(Blocks.CRAFTING_TABLE);
        }

        public ShapelessCopyDataRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {

            NonNullList<Ingredient> nonnulllist = NonNullList.of(Ingredient.EMPTY,
                    SerializeUtil.readList(buffer, Ingredient::fromNetwork).toArray(new Ingredient[0]));
            ItemStack itemstack = buffer.readItem();
            return new ShapelessCopyDataRecipe(recipeId, itemstack, nonnulllist);
        }

        public ShapelessCopyDataRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
            NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getAsJsonArray(json, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonnulllist.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe the max is 9");
            } else {
                ItemStack itemstack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
                return new ShapelessCopyDataRecipe(recipeId, itemstack, nonnulllist);
            }
        }

        public void toNetwork(PacketBuffer buffer, ShapelessCopyDataRecipe recipe) {

            SerializeUtil.writeList(buffer, recipe.getIngredients(), Ingredient::toNetwork);
            buffer.writeItem(recipe.getResultItem());
        }
    }
    public static RegistryObject<IERecipeSerializer<ShapelessCopyDataRecipe>> SERIALIZER;

    public final Ingredient tool;

    public ShapelessCopyDataRecipe(ResourceLocation idIn, ItemStack out, NonNullList<Ingredient> materials) {
        super(idIn, "", out, materials);
        tool = materials.get(0);
    }

    @Override
    public ResourceLocation getAdvancementId() {
        return null;
    }


    @Override
    public JsonObject serializeAdvancement() {
        return null;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingInventory inv) {
        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack in = inv.getItem(j);
            if (tool.test(in)) {
                ItemStack out = super.assemble(inv);
                out.setTag(in.getTag());
                return out;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return getId();
    }

    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER.get();
    }

    @Override
    public void serializeRecipeData(JsonObject json) {

        JsonArray jsonarray = new JsonArray();

        for (Ingredient ingredient : this.getIngredients()) {
            jsonarray.add(ingredient.toJson());
        }

        json.add("ingredients", jsonarray);
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", RegistryUtils.getRegistryName(this.getResultItem().getItem()).toString());
        if (this.getResultItem().getCount() > 1) {
            jsonobject.addProperty("count", this.getResultItem().getCount());
        }

        json.add("result", jsonobject);
    }

}