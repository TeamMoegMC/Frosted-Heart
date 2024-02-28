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

package com.teammoeg.frostedheart.scenario.commands;

import java.util.HashMap;
import java.util.Map;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.teammoeg.frostedheart.research.api.ResearchDataAPI;
import com.teammoeg.frostedheart.research.data.FHResearchDataManager;
import com.teammoeg.frostedheart.scenario.EventTriggerType;
import com.teammoeg.frostedheart.scenario.Param;
import com.teammoeg.frostedheart.scenario.runner.ScenarioVM;
import com.teammoeg.frostedheart.scenario.runner.target.ExecuteTarget;
import com.teammoeg.frostedheart.scenario.runner.target.OrTrigger;
import com.teammoeg.frostedheart.scenario.runner.target.VariantTargetTrigger;
import com.teammoeg.frostedheart.scenario.runner.target.trigger.MovementTrigger;
import com.teammoeg.frostedheart.util.FHUtils;
import com.teammoeg.frostedheart.util.RegistryUtils;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.server.management.OpEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class MCCommands {
	public void giveItem(ScenarioVM runner, @Param("i") String item, @Param("n") String nbt, @Param("c") int count) throws CommandSyntaxException {
		Item i = RegistryUtils.getItem(new ResourceLocation(item));
		if (count == 0) count = 1;
		ItemStack is = new ItemStack(i, count);
		if (nbt != null)
			is.setTag(JsonToNBT.getTagFromJson(nbt));
		FHUtils.giveItem(runner.getPlayer(), is);
	}

	public void setResearchAttribute(ScenarioVM runner, @Param("k") String key, @Param("v") double value) {
		ResearchDataAPI.putVariantDouble(runner.getPlayer(), key, value);
	}

	public void waitPlayerStart(ScenarioVM runner, @Param("s") String s, @Param("l") String l) {
		runner.addTrigger(new OrTrigger(new MovementTrigger(runner.getPlayer()), new VariantTargetTrigger().register(runner.getPlayer(), EventTriggerType.PLAYER_INTERACT)).setSync(),
			new ExecuteTarget(runner, s, l));
	}

	public void gameCommand(ScenarioVM runner,@Param("op")int op,@Param("asPlayer")int asp, @Param("cmd") @Param("command") String s) {
		Map<String, Object> overrides = new HashMap<>();
		ServerPlayerEntity triggerPlayer = runner.getPlayer();
		overrides.put("p", triggerPlayer.getGameProfile().getName());

		BlockPos pos = triggerPlayer.getPosition();
		overrides.put("x", pos.getX());
		overrides.put("y", pos.getY());
		overrides.put("z", pos.getZ());
		OpEntry opent=FHResearchDataManager.server.getPlayerList().getOppedPlayers().getEntry(triggerPlayer.getGameProfile());
		if(op>0)
			if(opent==null){
				FHResearchDataManager.server.getPlayerList().addOp(triggerPlayer.getGameProfile());
			}
		Commands cmds = FHResearchDataManager.server.getCommandManager();
		CommandSource source = asp>0?triggerPlayer.getCommandSource():FHResearchDataManager.server.getCommandSource();
		for (Map.Entry<String, Object> entry : overrides.entrySet()) {
			if (entry.getValue() != null) {
				s = s.replace("@" + entry.getKey(), entry.getValue().toString());
			}
		}
		cmds.handleCommand(source, s);
		if(op>0)
			if(opent==null){
				FHResearchDataManager.server.getPlayerList().removeOp(triggerPlayer.getGameProfile());
			}
	}
}
