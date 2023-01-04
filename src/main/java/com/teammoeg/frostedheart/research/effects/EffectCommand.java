/*
 * Copyright (c) 2022 TeamMoeg
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

/**
 * Reward the research team executes command
 */
public class EffectCommand extends Effect {

	List<String> rewards;

	public EffectCommand(String... cmds) {
		super();
		rewards = new ArrayList<>();

		for (String stack : cmds) {
			rewards.add(stack);
		}
	}

	public EffectCommand(JsonObject jo) {
		super(jo);
		rewards = SerializeUtil.parseJsonElmList(jo.get("rewards"), JsonElement::getAsString);
	}

	public EffectCommand(PacketBuffer pb) {
		super(pb);
		rewards = SerializeUtil.readList(pb, PacketBuffer::readString);
	}

	@Override
	public void init() {

	}

	@Override
	public boolean grant(TeamResearchData team, PlayerEntity triggerPlayer, boolean isload) {
		if (triggerPlayer == null || isload)
			return false;

		Map<String, Object> overrides = new HashMap<>();
		overrides.put("p", triggerPlayer.getGameProfile().getName());

		BlockPos pos = triggerPlayer.getPosition();
		overrides.put("x", pos.getX());
		overrides.put("y", pos.getY());
		overrides.put("z", pos.getZ());

		overrides.put("t", team.getTeam().get().getStringID());
		Commands cmds = FHResearchDataManager.server.getCommandManager();
		CommandSource source = FHResearchDataManager.server.getCommandSource();
		for (String s : rewards) {

			for (Map.Entry<String, Object> entry : overrides.entrySet()) {
				if (entry.getValue() != null) {
					s = s.replace("@" + entry.getKey(), entry.getValue().toString());
				}
			}

			cmds.handleCommand(source, s);
		}

		return true;
	}

	// We dont redo command, it's not possible
	@Override
	public void revoke(TeamResearchData team) {

	}

	@Override
	public JsonObject serialize() {
		JsonObject jo = super.serialize();
		jo.add("rewards", SerializeUtil.toJsonList(rewards,JsonPrimitive::new));
		return jo;
	}

	@Override
	public void write(PacketBuffer buffer) {
		super.write(buffer);
		SerializeUtil.writeList2(buffer, rewards, PacketBuffer::writeString);
	}

	@Override
	public FHIcon getDefaultIcon() {
		return FHIcons.getIcon(Blocks.COMMAND_BLOCK);
	}

	@Override
	public IFormattableTextComponent getDefaultName() {
		return GuiUtils.translateGui("effect.command");
	}

	@Override
	public List<ITextComponent> getDefaultTooltip() {
		List<ITextComponent> tooltip = new ArrayList<>();
		return tooltip;
	}

	@Override
	public String getBrief() {
		if (rewards.isEmpty())
			return "No Command";

		return "Command " + rewards.get(0) + (rewards.size() > 1 ? " ..." : "");
	}
}
