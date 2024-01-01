/*
 * Copyright (c) 2021-2024 TeamMoeg
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

package com.teammoeg.frostedheart.content.generator;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.FHMultiblocks;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.RegistryObject;

public class GeneratorSteamRecipe extends IESerializableRecipe {
    public static RecipeType<GeneratorSteamRecipe> TYPE;
    public static RegistryObject<IERecipeSerializer<GeneratorSteamRecipe>> SERIALIZER;

    public GeneratorSteamRecipe(ResourceLocation id, FluidTagInput input,
                                float power, float tempMod, float rangeMod) {
        super(ItemStack.EMPTY, TYPE, id);
        this.input = input;
        this.power = power;
        this.tempMod = tempMod;
        this.rangeMod = rangeMod;
    }

    public final FluidTagInput input;
    public final float power;
    public final float tempMod;
    public final float rangeMod;


    @Override
    protected IERecipeSerializer getIESerializer() {
        return SERIALIZER.get();
    }

    // Initialized by reload listener
    public static Map<ResourceLocation, GeneratorSteamRecipe> recipeList = Collections.emptyMap();

    public static GeneratorSteamRecipe findRecipe(FluidStack input) {
        for (GeneratorSteamRecipe recipe : recipeList.values())
            if (recipe.input.testIgnoringAmount(input))
                return recipe;
        return null;
    }

    @Override
    public ItemStack getResultItem() {
        return super.outputDummy;
    }
    public static class Serializer extends IERecipeSerializer<GeneratorSteamRecipe> {
        @Override
        public ItemStack getIcon() {
            return new ItemStack(FHMultiblocks.generator);
        }

        @Override
        public GeneratorSteamRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
            FluidTagInput input = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "input"));
            float power = GsonHelper.getAsFloat(json, "energy");
            float tempMod = GsonHelper.getAsFloat(json, "temp_multiplier");
            float rangeMod = GsonHelper.getAsFloat(json, "range_multiplier");
            return new GeneratorSteamRecipe(recipeId, input, power, tempMod, rangeMod);
        }

        @Nullable
        @Override
        public GeneratorSteamRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            FluidTagInput input = FluidTagInput.read(buffer);
            float power = buffer.readFloat();
            float tempMod = buffer.readFloat();
            float rangeMod = buffer.readFloat();
            return new GeneratorSteamRecipe(recipeId, input, power, tempMod, rangeMod);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, GeneratorSteamRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeFloat(recipe.power);
            buffer.writeFloat(recipe.tempMod);
            buffer.writeFloat(recipe.rangeMod);
        }
    }
}
