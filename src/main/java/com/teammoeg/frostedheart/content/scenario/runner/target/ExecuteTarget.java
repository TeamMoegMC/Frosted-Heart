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

package com.teammoeg.frostedheart.content.scenario.runner.target;

import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.content.scenario.parser.Scenario;
import com.teammoeg.frostedheart.content.scenario.runner.IScenarioThread;

public class ExecuteTarget extends ScenarioTarget{

	private final String label;
	public ExecuteTarget(IScenarioThread par,String name, String label) {
		super(par,name);

		this.label = label;
	}
	public ExecuteTarget(Scenario sc, String label) {
		super(sc);

		this.label = label;
	}
	@Override
	public void apply(IScenarioThread runner) {
		super.apply(runner);
		if(label!=null) {
			Integer ps=runner.getScenario().labels.get(label);
			if(ps!=null) {
				runner.setNodeNum(ps);
			}else {
				FHMain.LOGGER.error ("Invalid label "+label );
			}
		}
	}

	@Override
	public String toString() {
		return "ExecuteTarget [label=" + label + ", getName()=" + getName() + "]";
	}

	
}