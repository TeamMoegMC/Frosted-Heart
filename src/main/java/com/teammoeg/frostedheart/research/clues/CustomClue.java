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

import dev.ftb.mods.ftbteams.data.Team;
import net.minecraft.network.PacketBuffer;

/**
 * Very Custom Clue trigger by code or manually.
 */
public class CustomClue extends Clue {
    public CustomClue(String name, float contribution) {
        super(name, contribution);
    }

    public CustomClue(JsonObject jo) {
        super(jo);
    }

    public CustomClue(PacketBuffer pb) {
        super(pb);
    }

    public CustomClue(String name, String desc, String hint, float contribution) {
        super(name, desc, hint, contribution);
    }

    public CustomClue() {
        super();
    }

    @Override
    public String getId() {
        return "custom";
    }

    @Override
    public void init() {
    }

    @Override
    public void start(Team team) {
    }

    @Override
    public void end(Team team) {
    }
	@Override
	public String getBrief() {
		return "Custom "+getDescription().getString();
	}

}
