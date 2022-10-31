/*
 * Copyright (c) 2021 TeamMoeg
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
 */

package com.teammoeg.frostedheart.content.generator;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.FHMultiblocks;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class GeneratorRecipeSerializer extends IERecipeSerializer<GeneratorRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(FHMultiblocks.generator);
    }

    @Override
    public GeneratorRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = readOutput(json.get("result"));
        IngredientWithSize input = IngredientWithSize.deserialize(json.get("input"));
        int time = JSONUtils.getInt(json, "time");
        return new GeneratorRecipe(recipeId, output, input, time);
    }

    @Nullable
    @Override
    public GeneratorRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ItemStack output = buffer.readItemStack();
        IngredientWithSize input = IngredientWithSize.read(buffer);
        int time = buffer.readInt();
        return new GeneratorRecipe(recipeId, output, input, time);
    }

    @Override
    public void write(PacketBuffer buffer, GeneratorRecipe recipe) {
        buffer.writeItemStack(recipe.output);
        recipe.input.write(buffer);
        buffer.writeInt(recipe.time);
    }
}
