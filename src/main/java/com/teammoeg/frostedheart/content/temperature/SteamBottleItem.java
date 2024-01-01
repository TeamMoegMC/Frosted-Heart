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
import com.teammoeg.frostedheart.climate.IHeatingEquipment;
import com.teammoeg.frostedheart.climate.ITempAdjustFood;

import blusunrize.immersiveengineering.common.util.EnergyHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

public class SteamBottleItem extends FHBaseItem implements IHeatingEquipment, ITempAdjustFood, EnergyHelper.IIEEnergyItem {


    public SteamBottleItem(String name, Properties properties) {
        super(name, properties);
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        PlayerEntity entityplayer = entityLiving instanceof PlayerEntity ? (PlayerEntity) entityLiving : null;
        if (entityplayer == null || !entityplayer.abilities.instabuild) {
            stack.shrink(1);
        }

        if (entityplayer instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) entityplayer, stack);
        }

        if (entityplayer != null) {
            entityplayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (entityplayer == null || !entityplayer.abilities.instabuild) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (entityplayer != null) {
                entityplayer.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        String stored = this.getEnergyStored(stack) + "/" + this.getMaxEnergyStored(stack);
        tooltip.add(GuiUtils.translateTooltip("meme.steam_bottle").withStyle(TextFormatting.GRAY));
        tooltip.add(GuiUtils.translateTooltip("steam_stored", stored).withStyle(TextFormatting.GOLD));
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getUseDuration(ItemStack stack) {
        return 16;
    }

    @Override
    public void onCraftedBy(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCraftedBy(stack, worldIn, playerIn);
        this.receiveEnergy(stack, 240, false);
    }


    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.startUsingItem(handIn);
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getItemInHand(handIn));
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            ItemStack is = new ItemStack(this);
            this.receiveEnergy(is, this.getMaxEnergyStored(is), false);
            items.add(is);
        }

    }


    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return 240;
    }

    @Override
    public float getHeat(ItemStack is,float env) {
        return this.getEnergyStored(is) / 120;
    }

    @Override
    public float compute(ItemStack stack, float bodyTemp, float environmentTemp) {
        return this.extractEnergy(stack, 3, false) / 120;
    }

    @Override
    public float getMax(ItemStack stack) {
        return 0.025F;
    }

}
