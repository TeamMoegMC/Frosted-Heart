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

package com.teammoeg.frostedheart.content.temperature.handstoves;

import java.util.List;

import javax.annotation.Nullable;

import com.teammoeg.frostedheart.base.item.FHBaseItem;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.climate.IHeatingEquipment;
import com.teammoeg.frostedheart.util.FHUtils;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.tags.Tag;
import net.minecraft.tags.SerializationTags;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public class CoalHandStove extends FHBaseItem implements IHeatingEquipment {
    public final static int max_fuel = 800;

    public CoalHandStove(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        list.add(GuiUtils.translateTooltip("handstove.add_fuel").withStyle(ChatFormatting.GRAY));
        if (getAshAmount(stack) >= 800)
            list.add(GuiUtils.translateTooltip("handstove.trash_ash").withStyle(ChatFormatting.RED));
        list.add(GuiUtils.translateTooltip("handstove.fuel", getFuelAmount(stack) / 2).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public float compute(ItemStack stack, float bodyTemp, float environmentTemp) {
        int fuel = getFuelAmount(stack);
        if (fuel >= 2) {
            int ash = getAshAmount(stack);
            if (ash <= 800) {
                fuel--;
                ash++;
                setFuelAmount(stack, fuel);
                setAshAmount(stack, ash);
                if (bodyTemp < 0) {
                    return this.getMax(stack);
                }
            }
        }
        return 0;
    }

    public static int getAshAmount(ItemStack is) {
        return is.getOrCreateTag().getInt("ash");
    }

    public static int getFuelAmount(ItemStack is) {
        return is.getOrCreateTag().getInt("fuel");
    }

    public static void setAshAmount(ItemStack is, int v) {
        is.getOrCreateTag().putInt("ash", v);
        if (v >= max_fuel)
            is.getTag().putInt("CustomModelData", 2);
    }

    public static void setFuelAmount(ItemStack is, int v) {
        is.getOrCreateTag().putInt("fuel", v);
        if (v < 2)
            is.getTag().putInt("CustomModelData", 0);
        else
            is.getTag().putInt("CustomModelData", 1);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return getFuelAmount(stack) * 1.0D / max_fuel;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    ResourceLocation ashitem = new ResourceLocation("frostedheart", "ash");

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        InteractionResultHolder<ItemStack> FAIL = new InteractionResultHolder<>(InteractionResult.FAIL, stack);
        if (getAshAmount(playerIn.getItemInHand(handIn)) >= 800) {
            playerIn.startUsingItem(handIn);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        return FAIL;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        int ash = getAshAmount(stack);
        if (ash >= 800) {
            Tag<Item> item = SerializationTags.getInstance().getItems().getTag(ashitem);
            setAshAmount(stack, ash - 800);
            if (getFuelAmount(stack) < 2)
                stack.getTag().putInt("CustomModelData", 0);
            else
                stack.getTag().putInt("CustomModelData", 1);
            if (item != null && entityLiving instanceof Player && !item.getValues().isEmpty()) {
                ItemStack ret = new ItemStack(item.getValues().get(0));
                FHUtils.giveItem((Player) entityLiving, ret);
            }
        }
        return stack;
    }


    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public float getMax(ItemStack stack) {
        return getFuelAmount(stack) > 0 ? 0.015F : 0;
    }

    @Override
    public boolean canHandHeld() {
        return true;
    }
}
