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
import com.teammoeg.frostedheart.climate.chunkdata.ChunkData;
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
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

public class SoilThermometer extends FHBaseItem {
    public SoilThermometer(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
    	playerIn.displayClientMessage(GuiUtils.translateMessage("thermometer.testing"),true);
        playerIn.startUsingItem(handIn);
        if (playerIn instanceof ServerPlayerEntity&&playerIn.abilities.instabuild) {
            BlockRayTraceResult brtr = getPlayerPOVHitResult(worldIn, playerIn, FluidMode.ANY);
            if (brtr.getType() != Type.MISS)
            	FHTemperatureDisplayPacket.send((ServerPlayerEntity)playerIn,
            			"info.soil_thermometerbody", (int)(ChunkData.getTemperature(playerIn.level, brtr.getBlockPos())*10)/10f);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getItemInHand(handIn));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (worldIn.isClientSide) return stack;
        PlayerEntity entityplayer = entityLiving instanceof PlayerEntity ? (PlayerEntity) entityLiving : null;
        if (entityplayer instanceof ServerPlayerEntity) {
            BlockRayTraceResult brtr = getPlayerPOVHitResult(worldIn, entityplayer, FluidMode.ANY);
            if (brtr.getType() == Type.MISS) return stack;
            FHTemperatureDisplayPacket.send((ServerPlayerEntity)entityLiving,
        			"info.soil_thermometerbody", (int)(ChunkData.getTemperature(entityLiving.level, brtr.getBlockPos())*10)/10f);
        }
        return stack;
    }
    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    	tooltip.add(GuiUtils.translateTooltip("thermometer.usage").withStyle(TextFormatting.GRAY));
    }
    @Override
    public int getUseDuration(ItemStack stack) {
        return 100;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.SPEAR;
    }
}
