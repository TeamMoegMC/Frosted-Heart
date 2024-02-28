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

package com.teammoeg.frostedheart.research;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.teammoeg.frostedheart.research.data.TeamResearchData;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class TeamCapability<T extends INBTSerializable<CompoundNBT>>{
	public static final Set<TeamCapability<?>> caps=new HashSet<>();
	String id;
	Function<TeamResearchData,T> factory;
	
	public TeamCapability(String id, Function<TeamResearchData, T> factory) {
		super();
		this.id = id;
		this.factory = factory;
		caps.add(this);
	}
	public T create(TeamResearchData dat) {
		return factory.apply(dat);
	}
	public String getId() {
		return id;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TeamCapability other = (TeamCapability) obj;
		return Objects.equals(id, other.id);
	};
}
