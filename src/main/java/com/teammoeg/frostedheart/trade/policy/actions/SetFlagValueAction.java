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

package com.teammoeg.frostedheart.trade.policy.actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.trade.FHVillagerData;
import com.teammoeg.frostedheart.trade.policy.Actions;
import com.teammoeg.frostedheart.trade.policy.PolicyAction;

import net.minecraft.network.FriendlyByteBuf;

public class SetFlagValueAction extends SetFlagAction {
	int value;
	public SetFlagValueAction(JsonObject jo) {
		super(jo);
		value=jo.get("value").getAsInt();
	}
	public SetFlagValueAction(String name, int value) {
		super(name);
		this.value = value;
	}
	public SetFlagValueAction(FriendlyByteBuf buffer) {
		super(buffer);
		value=buffer.readVarInt();
	}
	@Override
	public JsonElement serialize() {
		JsonObject jo=super.serialize().getAsJsonObject();
		jo.addProperty("value", value);
		return jo;
	}
	@Override
	public void write(FriendlyByteBuf buffer) {
		super.write(buffer);
		buffer.writeVarInt(value);
	}
	@Override
	public void deal(FHVillagerData data, int num) {
		if(value!=0)
			data.flags.put(name, value);
		else
			data.flags.remove(name);
	}


}
