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

package com.teammoeg.frostedheart.util.io.registry;

import java.util.function.Function;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.frostedheart.util.io.Writeable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class NBTSerializerRegistry<U extends Writeable> extends PacketBufferSerializerRegistry<U, Object, CompoundNBT> {


    public NBTSerializerRegistry() {
        super();
    }
    public U deserialize(CompoundNBT je) {
        Function<CompoundNBT, U> func = from.get(je.getString("type"));
        if (func == null)
            return null;
        return func.apply(je);
    }

    public U deserializeOrDefault(CompoundNBT je, U def) {
        Function<CompoundNBT, U> func = from.get(je.getString("type"));
        if (func == null)
            return def;
        return func.apply(je);
    }
    
	@Override
	protected void writeType(Pair<Integer, String> type, CompoundNBT obj) {
		obj.putString("type", type.getSecond());
	}
	@Override
	protected String readType(CompoundNBT obj) {
		// TODO Auto-generated method stub
		return obj.getString("type");
	}
	public void register(Class<? extends U> cls, String type, Function<CompoundNBT, U> json, Function<U, CompoundNBT> obj, Function<PacketBuffer, U> packet) {
		// TODO Auto-generated method stub
		super.register(cls, type, json, (t,c)->obj.apply(t), packet);
	}
	public CompoundNBT write(U fromObj) {
		// TODO Auto-generated method stub
		return super.write(fromObj, null);
	}
}
