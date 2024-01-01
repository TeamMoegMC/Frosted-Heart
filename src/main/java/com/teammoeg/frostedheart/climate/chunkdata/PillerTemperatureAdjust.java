/*
 * Copyright (c) 2021-2024 TeamMoeg
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

package com.teammoeg.frostedheart.climate.chunkdata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

/**
 * Spheric Temperature Adjust, would adjust temperature in a ball.
 */
public class PillerTemperatureAdjust extends CubicTemperatureAdjust {

    long r2;
    int upper;
    int lower;
    public PillerTemperatureAdjust(int cx, int cy, int cz, int r,int upper,int lower, int value) {
        super(cx, cy, cz, r, value);
        r2 = r * r;
        this.upper=upper;
        this.lower=lower;
    }

    public PillerTemperatureAdjust(FriendlyByteBuf buffer) {
        super(buffer);
        r2 = r * r;
        this.upper=buffer.readVarInt();
        this.lower=buffer.readVarInt();
    }

    public PillerTemperatureAdjust(CompoundTag nc) {
        super(nc);
        r2 = r * r;
        this.upper=nc.getInt("upper");
        this.lower=nc.getInt("lower");
    }

    public PillerTemperatureAdjust(BlockPos heatPos, int range,int u,int d, int tempMod) {
        super(heatPos, range, tempMod);
        r2 = r * r;
        this.upper=u;
        this.lower=d;
    }

    @Override
    public boolean isEffective(int x, int y, int z) {
    	if(y>upper+cy||y<lower+cy)return false;
        long l = (long) Math.pow(x - cx, 2);
        l += (long) Math.pow(z - cz, 2);
        return l <= r;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = serializeNBTData();
        nbt.putInt("type", 2);
        nbt.putInt("upper", upper);
        nbt.putInt("lower", lower);
        return nbt;
    }

    @Override
    public void serialize(FriendlyByteBuf buffer) {
        buffer.writeInt(2);
        serializeData(buffer);
    }

	@Override
	protected void serializeData(FriendlyByteBuf buffer) {
		super.serializeData(buffer);
		buffer.writeVarInt(upper);
		buffer.writeVarInt(lower);
	}

}
