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

package com.teammoeg.frostedheart.content.temperature.heatervest;

import java.util.List;

import javax.annotation.Nullable;

import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.base.item.FHBaseItem;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.climate.IHeatingEquipment;
import com.teammoeg.frostedheart.content.steamenergy.IChargable;

import blusunrize.immersiveengineering.common.util.EnergyHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.item.Item.Properties;

/**
 * Heater Vest: wear it to warm yourself from the coldness.
 * 加温背心：穿戴抵御寒冷
 */
public class HeaterVestItem extends FHBaseItem implements EnergyHelper.IIEEnergyItem, IHeatingEquipment, IChargable {

    public HeaterVestItem(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return 30000;
    }

    @Nullable
    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return FHMain.rl("textures/models/heater_vest.png").toString();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public HumanoidModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot,
                                    HumanoidModel _default) {
        return HeaterVestModel.getModel();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        String stored = this.getEnergyStored(stack) + "/" + this.getMaxEnergyStored(stack);
        list.add(GuiUtils.translateTooltip("charger.heat_vest").withStyle(ChatFormatting.GRAY));
        list.add(GuiUtils.translateTooltip("steam_stored", stored).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(new ItemStack(this));
            ItemStack is = new ItemStack(this);
            this.receiveEnergy(is, this.getMaxEnergyStored(is), false);
            items.add(is);
        }

    }

    @Override
    public float compute(ItemStack stack, float bodyTemp, float environmentTemp) {
        int energycost = 1;
        if (bodyTemp < 0.05) {
            float delta = 0.05F - bodyTemp;
            if (delta > 0.1)
                delta = 0.1F;
            float rex = Math.max(this.extractEnergy(stack, energycost + (int) (delta * 120F), false) - energycost, 0F);
            return rex / 120F;
        } else this.extractEnergy(stack, energycost, false);
        return 0;
    }

    @Override
    public float charge(ItemStack stack, float value) {
        return this.receiveEnergy(stack, (int) value, false);
    }

    @Override
    public float getMax(ItemStack stack) {
        return 0.1F;
    }

}
