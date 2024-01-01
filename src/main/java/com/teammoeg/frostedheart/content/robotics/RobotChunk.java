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

package com.teammoeg.frostedheart.content.robotics;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class RobotChunk implements ICapabilitySerializable<CompoundTag> {
	List<BlockPos> poss=new ArrayList<>();
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return null;
	}
	long hashCode(BlockPos bp) {
		return bp.getY()<<56+bp.getX()<<28+bp.getZ();
	}
	public void addContent() {
		
	}
	@Override
	public CompoundTag serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
	}

}
