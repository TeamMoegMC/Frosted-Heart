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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.frostedheart.base.team.TeamDataHolder;

/**
 * Very Custom Clue trigger by code or manually.
 */
public class CustomClue extends Clue {
	public static final Codec<CustomClue> CODEC=RecordCodecBuilder.create(t->t.group(
		Clue.BASE_CODEC.forGetter(o->o.getData())
		).apply(t,CustomClue::new));
    public CustomClue() {
        super();
    }


    public CustomClue(BaseData data) {
		super(data);
		// TODO Auto-generated constructor stub
	}


	public CustomClue(String name, float contribution) {
        super(name, contribution);
    }

    public CustomClue(String name, String desc, String hint, float contribution) {
        super(name, desc, hint, contribution);
    }

    @Override
    public void end(TeamDataHolder team) {
    }

    @Override
    public String getBrief() {
        return "Custom " + getDescriptionString();
    }

    @Override
    public String getId() {
        return "custom";
    }

    @Override
    public void init() {
    }

    @Override
    public void start(TeamDataHolder team) {
    }

}
