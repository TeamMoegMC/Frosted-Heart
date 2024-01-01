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

public class SetFlagAction implements PolicyAction {
	String name;
	public SetFlagAction(JsonObject jo) {
		name=jo.get("name").getAsString();
	}

	public SetFlagAction(FriendlyByteBuf buffer) {
		name=buffer.readUtf();
	}

	public SetFlagAction(String name) {
		this.name=name;
	}
	@Override
	public JsonElement serialize() {
		JsonObject jo=new JsonObject();
		Actions.writeType(this, jo);
		jo.addProperty("name",name);
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		Actions.writeId(this, buffer);
		buffer.writeUtf(name);
	}

	@Override
	public void deal(FHVillagerData data, int num) {
		data.flags.computeIfAbsent(name,k->1);
	}

}
