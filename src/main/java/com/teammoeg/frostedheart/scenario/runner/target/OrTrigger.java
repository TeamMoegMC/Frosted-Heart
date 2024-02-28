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

import java.util.Arrays;

import com.teammoeg.frostedheart.scenario.runner.IScenarioThread;
import com.teammoeg.frostedheart.scenario.runner.IScenarioTrigger;

public class OrTrigger implements IScenarioTrigger {
	IScenarioTrigger[] triggers;
	boolean async=true;
	public OrTrigger(IScenarioTrigger...iScenarioTriggers) {
		triggers=iScenarioTriggers;
	}

	@Override
	public boolean test(IScenarioThread t) {
		
		return Arrays.stream(triggers).map(a->a.test(t)).anyMatch(b->b);
	}
	public boolean isAsync() {
		return async;
	}
	@Override
	public boolean canUse() {
		return Arrays.stream(triggers).map(a->a.canUse()).anyMatch(b->b);
	}

	public OrTrigger setSync() {
		this.async = false;
		return this;
	}
	@Override
	public boolean use() {
		return Arrays.stream(triggers).map(a->a.use()).anyMatch(b->b);
	}


}
