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

package com.teammoeg.frostedheart.research.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.research.data.FHResearchDataManager;
import com.teammoeg.frostedheart.research.data.TeamResearchData;
import com.teammoeg.frostedheart.research.gui.FHIcons;
import com.teammoeg.frostedheart.research.gui.FHIcons.FHIcon;
import com.teammoeg.frostedheart.util.SerializeUtil;

import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

/**
 * Reward the research team executes command
 */
public class EffectExperience extends Effect {

	int exp;

	public EffectExperience(int xp) {
		super();
		exp=xp;
	}

	public EffectExperience(JsonObject jo) {
		super(jo);
		exp=jo.get("experience").getAsInt();
	}

	public EffectExperience(FriendlyByteBuf pb) {
		super(pb);
		exp=pb.readVarInt();
	}

	@Override
	public void init() {

	}

	@Override
	public boolean grant(TeamResearchData team, Player triggerPlayer, boolean isload) {
		if (triggerPlayer == null || isload)
			return false;

		triggerPlayer.giveExperiencePoints(getRId());

		return true;
	}
	@Override
	public void revoke(TeamResearchData team) {

	}

	@Override
	public JsonObject serialize() {
		JsonObject jo = super.serialize();
		jo.addProperty("experience", exp);
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		super.write(buffer);
		buffer.writeVarInt(exp);
	}

	@Override
	public FHIcon getDefaultIcon() {
		return FHIcons.getIcon(Items.EXPERIENCE_BOTTLE);
	}

	@Override
	public MutableComponent getDefaultName() {
		return GuiUtils.translateGui("effect.exp");
	}

	@Override
	public List<Component> getDefaultTooltip() {
		List<Component> tooltip = new ArrayList<>();
		tooltip.add(GuiUtils.str("+"+exp));
		return tooltip;
	}

	@Override
	public String getBrief() {
	
		return "Experience " + exp;
	}
}
