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

package com.teammoeg.frostedheart.content.temperature;

import java.util.List;

import com.teammoeg.frostedheart.base.item.FHBaseItem;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.climate.TemperatureCore;
import com.teammoeg.frostedheart.network.climate.FHTemperatureDisplayPacket;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

public class ThermometerItem extends FHBaseItem {


    public ThermometerItem(String name, Properties properties) {
        super(name, properties);
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (worldIn.isClientSide) return stack;
        if (entityLiving instanceof ServerPlayerEntity) {
        	FHTemperatureDisplayPacket.send((ServerPlayerEntity)entityLiving,"info.thermometerbody",getTemperature((ServerPlayerEntity)entityLiving) / 10f + 37f);
        }

        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    	tooltip.add(GuiUtils.translateTooltip("thermometer.usage").withStyle(TextFormatting.GRAY));
        tooltip.add(GuiUtils.translateTooltip("meme.thermometerbody").withStyle(TextFormatting.GRAY));
    }

    public int getTemperature(ServerPlayerEntity p) {
        return (int) (TemperatureCore.getBodyTemperature(p) * 10);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
    	playerIn.displayClientMessage(GuiUtils.translateMessage("thermometer.testing"),true);
        playerIn.startUsingItem(handIn);
        if (playerIn instanceof ServerPlayerEntity&&playerIn.abilities.instabuild) {
        	FHTemperatureDisplayPacket.send((ServerPlayerEntity)playerIn,"info.thermometerbody",getTemperature((ServerPlayerEntity) playerIn) / 10f + 37f);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getItemInHand(handIn));
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getUseDuration(ItemStack stack) {
        return 100;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

}
