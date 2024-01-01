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

package com.teammoeg.frostedheart.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.api.IDietTracker;
import top.theillusivec4.diet.common.capability.DietTrackerCapability;

public class FixedDietProvider implements ICapabilitySerializable<Tag> {

	private static final IDietTracker EMPTY_TRACKER = new DietTrackerCapability.EmptyDietTracker();

	final net.minecraftforge.common.util.LazyOptional<IDietTracker> capability;

	public FixedDietProvider(net.minecraftforge.common.util.LazyOptional<IDietTracker> capability) {
		this.capability = capability;
	}

	@Nonnull
	@Override
	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(@Nonnull Capability<T> cap,
			@Nullable Direction side) {

		if (DietCapability.DIET_TRACKER != null&&capability.isPresent()) {
			return DietCapability.DIET_TRACKER.orEmpty(cap, this.capability);
		}
		DietMod.LOGGER.error("Missing Diet capability!");
		return net.minecraftforge.common.util.LazyOptional.empty();
	}

	@Override
	public Tag serializeNBT() {

		if (DietCapability.DIET_TRACKER != null&&capability.isPresent()) {
			return DietCapability.DIET_TRACKER.writeNBT(capability.orElse(EMPTY_TRACKER), null);
		}
		DietMod.LOGGER.error("Missing Diet capability!");
		return new CompoundTag();
	}

	@Override
	public void deserializeNBT(Tag nbt) {

		if (DietCapability.DIET_TRACKER != null) {
			DietCapability.DIET_TRACKER.readNBT(capability.orElse(EMPTY_TRACKER), null, nbt);
		} else {
			DietMod.LOGGER.error("Missing Diet capability!");
		}
	}

}