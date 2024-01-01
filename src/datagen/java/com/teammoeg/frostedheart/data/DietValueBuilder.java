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

package com.teammoeg.frostedheart.data;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.content.recipes.DietGroupCodec;
import com.teammoeg.frostedheart.content.recipes.DietValueRecipe;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

public class DietValueBuilder implements IFinishedRecipe {
	final Map<String,Float> groups=new HashMap<>();
	public ResourceLocation out;
	public ResourceLocation rl;

	public DietValueBuilder(ResourceLocation rl,ResourceLocation out) {
		super();
		this.out = out;
		this.rl = rl;
	}
	public void addGroup(int i,float v) {
		groups.put(DietGroupCodec.groups[i],v);
		
	}
	@Override
	public void serializeRecipeData(JsonObject json) {
		JsonObject jo=new JsonObject();
		groups.entrySet().forEach(e->jo.addProperty(e.getKey(),e.getValue()));
		json.add("groups",jo);
		json.addProperty("item",out.toString());
	}

	@Override
	public ResourceLocation getId() {
		return rl;
	}

	@Override
	public JsonObject serializeAdvancement() {
		return null;
	}

	@Override
	public ResourceLocation getAdvancementId() {
		return null;
	}
	@Override
	public IRecipeSerializer<?> getType() {
		return DietValueRecipe.SERIALIZER.get();
	}

}
