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

package com.teammoeg.frostedheart.content.steamenergy;

import com.teammoeg.frostedheart.FHContent;
import com.teammoeg.frostedheart.FHMain;

import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public class HeatDebugItem extends Item {
    public HeatDebugItem(String name) {
        super(new Properties().stacksTo(1).setNoRepair().tab(FHMain.itemGroup));
        setRegistryName(FHMain.MODID, name);
        FHContent.registeredFHItems.add(this);

    }

    public int getUseDuration(ItemStack stack) {
        return 1;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    //Dont add to creative tag
    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
    	
        HitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.SOURCE_ONLY);
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if(worldIn.isClientSide)return InteractionResultHolder.success(itemstack);
        if (raytraceresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult) raytraceresult).getBlockPos();
            BlockEntity te = Utils.getExistingTileEntity(worldIn, blockpos);
            if (te instanceof HeatController) {
                playerIn.sendMessage(new TextComponent("HeatProvider network=" + ((HeatController) te).getNetwork()), playerIn.getUUID());
            } else if (te instanceof EnergyNetworkProvider) {
                playerIn.sendMessage(new TextComponent("EnergyNetworkProvider network=" + ((EnergyNetworkProvider) te).getNetwork()), playerIn.getUUID());
            } else if (te instanceof INetworkConsumer) {
            	if(((INetworkConsumer) te).getHolder()!=null)
            		playerIn.sendMessage(new TextComponent("EnergyNetworkConsumer data=" + ((INetworkConsumer) te).getHolder()), playerIn.getUUID());
            }
            return InteractionResultHolder.success(itemstack);
        }
        return InteractionResultHolder.fail(itemstack);
    }
}
