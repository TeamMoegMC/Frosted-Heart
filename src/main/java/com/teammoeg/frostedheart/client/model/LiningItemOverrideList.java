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

package com.teammoeg.frostedheart.client.model;

import javax.annotation.Nullable;

import com.teammoeg.frostedheart.FHMain;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class LiningItemOverrideList extends ItemOverrides {

    public LiningItemOverrideList() {
        super();
    }

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity) {
        String s = ItemNBTHelper.getString(stack, "inner_cover");
        EquipmentSlot slotType = ((ArmorItem) stack.getItem()).getSlot();
        if (s.length() > 0 && slotType != null) {
            String liningType = new ResourceLocation(s).getPath();
            String slotName = "feet";
            if (slotType.getName().equals("feet")) {
                slotName = "feet";
            } else if (slotType.getName().equals("legs")) {
                slotName = "legs";
            } else if (slotType.getName().equals("chest")) {
                slotName = "torso";
            } else if (slotType.getName().equals("head")) {
                slotName = "helmet";
            }
            ResourceLocation textureLocation = FHMain.rl("item/lining_overlay/" + liningType + "_" + slotName);
            return new LiningFinalizedModel(originalModel, textureLocation);
        }
        return originalModel;
    }
}
