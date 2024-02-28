/*
 * Copyright (c) 2024 TeamMoeg
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

package com.teammoeg.frostedheart.climate;

import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.teammoeg.frostedheart.FHAttributes;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.climate.data.ArmorTempData;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class ArmorTempCurios implements ICurio {
	ArmorTempData data;
	public ArmorTempCurios(ArmorTempData data) {
		this.data=data;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
		
		Multimap<Attribute, AttributeModifier> mm=HashMultimap.create();

        
        if(data!=null) {
        	String amd=FHMain.MODID+":armor_data";
        	
        	if(data.getInsulation()!=0)
        		mm.put(FHAttributes.INSULATION.get(), new AttributeModifier(uuid,amd, data.getInsulation(), Operation.ADDITION));
        	if(data.getColdProof()!=0)
        		mm.put(FHAttributes.WIND_PROOF.get(), new AttributeModifier(uuid,amd, data.getColdProof(), Operation.ADDITION));
        	if(data.getHeatProof()!=0)
        		mm.put(FHAttributes.HEAT_PROOF.get(), new AttributeModifier(uuid,amd, data.getHeatProof(), Operation.ADDITION));
        }
		return ICurio.super.getAttributeModifiers(slotContext, uuid);
	}

}
