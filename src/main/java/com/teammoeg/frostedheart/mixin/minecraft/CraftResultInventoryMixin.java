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

package com.teammoeg.frostedheart.mixin.minecraft;

import org.spongepowered.asm.mixin.Mixin;

import com.teammoeg.frostedheart.research.ResearchListeners;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

@Mixin(ResultContainer.class)
public abstract class CraftResultInventoryMixin implements RecipeHolder, Container {

    public CraftResultInventoryMixin() {
    }

    @Override
    public boolean canUseRecipe(Level worldIn, ServerPlayer player, Recipe<?> recipe) {
        if (ResearchListeners.canUseRecipe(player, recipe))
            return RecipeHolder.super.canUseRecipe(worldIn, player, recipe);
        return false;
    }

}
