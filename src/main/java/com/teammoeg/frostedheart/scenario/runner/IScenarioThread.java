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

package com.teammoeg.frostedheart.scenario.runner;

import java.util.Collection;

import com.teammoeg.frostedheart.scenario.parser.Scenario;
import com.teammoeg.frostedheart.scenario.runner.target.ExecuteStackElement;
import com.teammoeg.frostedheart.scenario.runner.target.IScenarioTarget;

import net.minecraft.entity.player.PlayerEntity;

public interface IScenarioThread {
	void setScenario(Scenario s);
	Scenario getScenario();
	void setNodeNum(int num);
	int getNodeNum();
	String getLang();
	void sendMessage(String s);
	void queue(IScenarioTarget t);
	void jump(IScenarioTarget acttrigger);
	RunStatus getStatus();
	PlayerEntity getPlayer();
	void setStatus(RunStatus waitclient);
	Collection<? extends ExecuteStackElement> getCallStack();
}
