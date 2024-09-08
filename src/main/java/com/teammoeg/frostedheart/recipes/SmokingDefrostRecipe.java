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

import java.util.Random;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

public class SmokingDefrostRecipe extends SmokingRecipe implements DefrostRecipe {

    public static class Serializer extends DefrostRecipe.Serializer<SmokingDefrostRecipe> {

        public Serializer() {
            super(SmokingDefrostRecipe::new);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SmokingDefrostRecipe recipe) {
            super.toNetwork(buffer, recipe);
            buffer.writeFloat(recipe.getExperience());
            buffer.writeVarInt(recipe.getCookingTime());
        }

    }
    public static RegistryObject<RecipeType<SmokingDefrostRecipe>> TYPE;
    public static RegistryObject<RecipeSerializer<SmokingDefrostRecipe>> SERIALIZER;

    ItemStack[] iss;

    Random recipeRNG = new Random();

    public SmokingDefrostRecipe(ResourceLocation p_i50030_1_, String p_i50030_2_, Ingredient p_i50030_3_,
                                ItemStack[] results, float p_i50030_5_, int p_i50030_6_) {
        super(p_i50030_1_, p_i50030_2_, CookingBookCategory.MISC, p_i50030_3_, ItemStack.EMPTY, p_i50030_5_, p_i50030_6_);
        this.iss = results;
    }

    @Override
    public ItemStack assemble(Container inv,RegistryAccess registry) {
        if (iss.length == 0) return ItemStack.EMPTY;
        return iss[recipeRNG.nextInt(getIss().length)].copy();
    }

    public Ingredient getIngredient() {
        return super.ingredient;
    }


    public ItemStack[] getIss() {
        return iss;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registry) {
        //ItemStack is=DistExecutor.unsafeCallWhenOn(Dist.CLIENT,()->(()->new ItemStack(FHItems.random_seeds)));
        //if(is==null)


        return assemble(null,registry);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
