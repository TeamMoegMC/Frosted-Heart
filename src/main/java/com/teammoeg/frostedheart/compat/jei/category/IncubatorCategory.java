/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Immersive Industry.
 *
 * Immersive Industry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Immersive Industry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Immersive Industry. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.frostedheart.compat.jei.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cannolicatfish.rankine.init.RankineItems;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.frostedheart.FHBlocks;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.client.util.ClientUtils;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.content.incubator.IncubateRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class IncubatorCategory implements IRecipeCategory<IncubateRecipe> {
    public static ResourceLocation UID = new ResourceLocation(FHMain.MODID, "incubator");
    private IDrawable BACKGROUND;
    private IDrawable ICON;


    private IDrawableAnimated FIRE;
    private IDrawableAnimated PROC;
    private IDrawableAnimated EFF;

    public IncubatorCategory(IGuiHelper guiHelper) {
        ResourceLocation guiMain = new ResourceLocation(FHMain.MODID, "textures/gui/incubator.png");
        this.ICON = guiHelper.createDrawableIngredient(new ItemStack(FHBlocks.incubator1));
        this.BACKGROUND = guiHelper.createDrawable(guiMain, 4, 4, 164, 72);
        IDrawableStatic tfire = guiHelper.createDrawable(guiMain, 198,64, 14, 14);
        this.FIRE = guiHelper.createAnimatedDrawable(tfire, 80, IDrawableAnimated.StartDirection.TOP, true);
        IDrawableStatic tproc = guiHelper.createDrawable(guiMain, 176,0, 32, 29);
        this.PROC = guiHelper.createAnimatedDrawable(tproc, 240, IDrawableAnimated.StartDirection.LEFT, false);
        IDrawableStatic teff = guiHelper.createDrawable(guiMain, 207,29,9, 35);
        this.EFF = guiHelper.createAnimatedDrawable(teff, 240, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends IncubateRecipe> getRecipeClass() {
        return IncubateRecipe.class;
    }


    public String getTitle() {
        return (new TranslationTextComponent("gui.jei.category." + FHMain.MODID + ".incubator").getString());
    }

    @Override
    public void draw(IncubateRecipe recipe, MatrixStack transform, double mouseX, double mouseY) {
        FIRE.draw(transform, 31, 31);
        PROC.draw(transform,76,24);
        EFF.draw(transform,15,31);
        
        String burnTime;
        if(recipe.time<1000||recipe.time%60!=0)
        	burnTime= String.valueOf(recipe.time) + " s";
        else
        	burnTime= String.valueOf(recipe.time/60) + " m";
        int width=ClientUtils.mc().fontRenderer.getStringWidth(burnTime);
        ClientUtils.mc().fontRenderer.drawString(transform, burnTime, 162-width, 62, 0xFFFFFF);
    }

    public boolean isMouseIn(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseY >= y
                && mouseX < x + w && mouseY < y + h;
    }

    @Override
    public List<ITextComponent> getTooltipStrings(IncubateRecipe recipe, double mouseX, double mouseY) {
        List<ITextComponent> tooltip = new ArrayList<>();
        return tooltip;
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
    public void setIngredients(IncubateRecipe recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.FLUID, Arrays.asList(Arrays.asList(new FluidStack(Fluids.WATER,recipe.water))));
        ingredients.setInputLists(VanillaTypes.ITEM,Arrays.asList(Arrays.asList(new ItemStack(RankineItems.QUICKLIME.get())),Arrays.asList(recipe.catalyst.getMatchingStacks()),Arrays.asList(recipe.input.getMatchingStacks())));
        if(!recipe.output.isEmpty())
        ingredients.setOutputLists(VanillaTypes.ITEM, Arrays.asList(Arrays.asList(recipe.output)));
        if(!recipe.output_fluid.isEmpty())
        ingredients.setOutputLists(VanillaTypes.FLUID, Arrays.asList(Arrays.asList(recipe.output_fluid)));
    }


    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IncubateRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
        guiFluidStacks.init(0, true, 57, 16, 16, 46, recipe.water * 5, false,null);
        guiFluidStacks.addTooltipCallback((s,o,i,t)->{
        	if(s==0)
        		t.add(GuiUtils.translateGui("mb_per_sec",recipe.water));
        	if(recipe.isFood&&s==1)
        		t.add(GuiUtils.translateGui("per_food_value",recipe.output_fluid.getAmount()));
        });
        guiFluidStacks.init(1, true, 113, 16, 16, 46, recipe.water* 5, false,null);
        guiFluidStacks.set(ingredients);
        if(!recipe.output_fluid.isEmpty())
        guiFluidStacks.set(1, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
        guiItemStacks.init(0, true, 29, 47);
        
        guiItemStacks.init(1, true, 11, 12);
        guiItemStacks.addTooltipCallback((s,o,i,t)->{
        	if(s==1&&!recipe.consume_catalyst)
        		t.add(GuiUtils.translateGui("not_consume"));
        	else if(s==2&&recipe.isFood)
        		t.add(GuiUtils.translateGui("any_food"));
        });
        guiItemStacks.init(2, true, 29, 12);
        guiItemStacks.init(3, false, 138, 31);
        guiItemStacks.set(ingredients);
        if(!recipe.output.isEmpty())
        	guiItemStacks.set(3, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
   
    }
}
