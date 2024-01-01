/*
 * Copyright (c) 2024 TeamMoeg
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

package com.teammoeg.frostedheart.mixin.minecraft;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.teammoeg.frostedheart.research.ResearchListeners;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
	@Overwrite
	public <C extends Container, T extends Recipe<C>> Optional<T> getRecipe(RecipeType<T> recipeTypeIn,
			C inventoryIn, Level worldIn) {
		if(recipeTypeIn==RecipeType.CRAFTING&&ForgeHooks.getCraftingPlayer()!=null) {
			return this.getRecipes(recipeTypeIn).values().stream().flatMap((recipe) -> {
				return Util.streamOptional(recipeTypeIn.matches(recipe, worldIn, inventoryIn));
			}).filter(t->ResearchListeners.canUseRecipe(ForgeHooks.getCraftingPlayer(), t)).findFirst();
		}
		return this.getRecipes(recipeTypeIn).values().stream().flatMap((recipe) -> {
			return Util.streamOptional(recipeTypeIn.matches(recipe, worldIn, inventoryIn));
		}).findFirst();
	}

	@Shadow
	abstract <C extends IInventory, T extends IRecipe<C>> Map<ResourceLocation, IRecipe<C>> getRecipes(IRecipeType<T> recipeTypeIn);
}
