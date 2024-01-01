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

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public class SoilThermometer extends FHBaseItem {
    public SoilThermometer(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
    	playerIn.displayClientMessage(GuiUtils.translateMessage("thermometer.testing"),true);
        playerIn.startUsingItem(handIn);
        if (playerIn instanceof ServerPlayer&&playerIn.abilities.instabuild) {
            BlockHitResult brtr = getPlayerPOVHitResult(worldIn, playerIn, Fluid.ANY);
            if (brtr.getType() != Type.MISS)
            	FHTemperatureDisplayPacket.send((ServerPlayer)playerIn,
            			"info.soil_thermometerbody", (int)(ChunkData.getTemperature(playerIn.level, brtr.getBlockPos())*10)/10f);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        if (worldIn.isClientSide) return stack;
        Player entityplayer = entityLiving instanceof Player ? (Player) entityLiving : null;
        if (entityplayer instanceof ServerPlayer) {
            BlockHitResult brtr = getPlayerPOVHitResult(worldIn, entityplayer, Fluid.ANY);
            if (brtr.getType() == Type.MISS) return stack;
            FHTemperatureDisplayPacket.send((ServerPlayer)entityLiving,
        			"info.soil_thermometerbody", (int)(ChunkData.getTemperature(entityLiving.level, brtr.getBlockPos())*10)/10f);
        }
        return stack;
    }
    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    	tooltip.add(GuiUtils.translateTooltip("thermometer.usage").withStyle(ChatFormatting.GRAY));
    }
    @Override
    public int getUseDuration(ItemStack stack) {
        return 100;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }
}
