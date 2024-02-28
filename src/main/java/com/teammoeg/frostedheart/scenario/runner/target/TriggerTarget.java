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

package com.teammoeg.frostedheart.scenario.runner.target;

import com.teammoeg.frostedheart.scenario.runner.IScenarioThread;
import com.teammoeg.frostedheart.scenario.runner.IScenarioTrigger;

public class TriggerTarget implements IScenarioTrigger,IScenarioTarget {
	IScenarioTrigger original;
	IScenarioTarget target;
	public TriggerTarget(IScenarioTrigger trigger, IScenarioTarget target) {
		super();
		this.original = trigger;
		this.target = target;
	}

	@Override
	public void apply(IScenarioThread conductor) {
		target.apply(conductor);
	}

	@Override
	public boolean test(IScenarioThread t) {
		return original.test(t);
	}

	@Override
	public boolean use() {
		return original.use();
	}

	@Override
	public boolean canUse() {
		return original.canUse();
	}

	@Override
	public boolean isAsync() {
		return original.isAsync();
	}

}
