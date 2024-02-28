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

package com.teammoeg.frostedheart.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class FHCapabilityProvider<C extends INBTSerializable<CompoundNBT>> implements ICapabilitySerializable<CompoundNBT>{
	LazyOptional<C> lazyCap;
	FHCapability<C> capability;
	public FHCapabilityProvider(FHCapability<C> capability) {
		super();
		this.capability = capability;
		this.lazyCap=capability.createCapability();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap==capability.capability()) {
			return lazyCap.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT() {
		return lazyCap.map(c->c.serializeNBT()).orElseGet(CompoundNBT::new);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		lazyCap.ifPresent(c->c.deserializeNBT(nbt));
	}

}
