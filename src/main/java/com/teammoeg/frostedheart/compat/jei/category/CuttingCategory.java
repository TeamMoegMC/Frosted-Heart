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

package com.teammoeg.frostedheart.compat.jei.category;

import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.teammoeg.frostedheart.FHItems;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.compat.jei.CuttingRecipe;
import com.teammoeg.frostedheart.compat.jei.JEICompat;
import com.teammoeg.frostedheart.util.TranslateUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CuttingCategory implements IRecipeCategory<CuttingRecipe> {
    public static ResourceLocation UID = new ResourceLocation(FHMain.MODID, "knife_cutting");
    public static List<Item> matching;
    public static final ResourceLocation ktag = new ResourceLocation(FHMain.MODID, "knife");
    private IDrawable BACKGROUND;
    private IDrawable ICON;

    public CuttingCategory(IGuiHelper guiHelper) {
        this.ICON = new DoubleItemIcon(() -> new ItemStack(Items.IRON_SWORD), () -> new ItemStack(FHItems.brown_mushroombed.get()));
        this.BACKGROUND = new EmptyBackground(120, 50);
    }

    @Override
    public void draw(CuttingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics transform, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SLOT.render(transform, 8, 4);
        AllGuiTextures.JEI_DOWN_ARROW.render(transform, 29, 7);
        AllGuiTextures.JEI_SLOT.render(transform, 34, 24);
        AllGuiTextures.JEI_ARROW.render(transform, 54, 28);
        AllGuiTextures.JEI_SLOT.render(transform, 96, 24);
    }

    @Override
    public IDrawable getBackground() {
        return BACKGROUND;
    }


    @Override
    public IDrawable getIcon() {
        return ICON;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, CuttingRecipe cuttingRecipe, IFocusGroup iFocusGroup) {

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 8, 4).addIngredients(cuttingRecipe.getIngredients().get(0));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 34, 24).addIngredients(cuttingRecipe.getIngredients().get(1));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 96, 24).addItemStack(cuttingRecipe.getResultItem(null));

    }

    @Override
    public RecipeType<CuttingRecipe> getRecipeType() {
        return JEICompat.Cutting;
    }

    public Component getTitle() {
        return TranslateUtils.translate("gui.jei.category." + FHMain.MODID + ".knife_cutting");
    }
}
