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

package com.teammoeg.frostedheart.research.clues;

import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.research.ResearchListeners;
import com.teammoeg.frostedheart.research.data.TeamResearchData;

import dev.ftb.mods.ftbteams.data.Team;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

public abstract class TickListenerClue extends ListenerClue {

    public TickListenerClue(String name, String desc, String hint, float contribution) {
        super(name, desc, hint, contribution);
    }

    public TickListenerClue(String name, float contribution) {
        super(name, contribution);
    }

    public TickListenerClue(JsonObject jo) {
        super(jo);
    }

    public TickListenerClue(PacketBuffer pb) {
        super(pb);
    }

    public TickListenerClue() {
        super();
    }

    @Override
    public void initListener(Team t) {
        ResearchListeners.getTickClues().add(this, t);
    }

    @Override
    public void removeListener(Team t) {
        ResearchListeners.getTickClues().remove(this, t);
    }

    public final void tick(TeamResearchData t, ServerPlayerEntity player) {
        if (!t.isClueTriggered(this))
            if (this.isCompleted(t, player)) {
                this.setCompleted(t, true);

            }
    }

    public abstract boolean isCompleted(TeamResearchData t, ServerPlayerEntity player);

}
