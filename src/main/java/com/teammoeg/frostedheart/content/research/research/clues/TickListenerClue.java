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

package com.teammoeg.frostedheart.content.research.research.clues;

import com.teammoeg.frostedheart.base.team.TeamDataHolder;
import com.teammoeg.frostedheart.content.research.ResearchListeners;
import com.teammoeg.frostedheart.content.research.data.TeamResearchData;

import net.minecraft.entity.player.ServerPlayerEntity;

public abstract class TickListenerClue extends ListenerClue {

    public TickListenerClue() {
        super();
    }



    public TickListenerClue(BaseData data) {
		super(data);
		// TODO Auto-generated constructor stub
	}



	public TickListenerClue(String name, float contribution) {
        super(name, contribution);
    }

    public TickListenerClue(String name, String desc, String hint, float contribution) {
        super(name, desc, hint, contribution);
    }

    @Override
    public void initListener(TeamDataHolder t) {
        ResearchListeners.getTickClues().add(this, t.getId());
    }

    public abstract boolean isCompleted(TeamResearchData t, ServerPlayerEntity player);

    @Override
    public void removeListener(TeamDataHolder t) {
        ResearchListeners.getTickClues().remove(this, t.getId());
    }

    public final void tick(TeamResearchData t, ServerPlayerEntity player) {
        if (!t.isClueTriggered(this))
            if (this.isCompleted(t, player)) {
                this.setCompleted(t, true);

            }
    }

}
