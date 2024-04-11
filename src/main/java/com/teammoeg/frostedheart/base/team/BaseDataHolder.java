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

package com.teammoeg.frostedheart.base.team;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.teammoeg.frostedheart.util.io.NBTSerializable;
import com.teammoeg.frostedheart.util.io.SerializeUtil;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;

public class BaseDataHolder<T extends BaseDataHolder<T>> implements SpecialDataHolder<T>, NBTSerializable {

	public BaseDataHolder() {
	}

	@Override
	public void save(CompoundNBT nbt, boolean isPacket) {
		SerializeUtil.toNBTMap(data.entrySet(), (t,p)->p.put(t.getKey().getId(),(INBT)t.getKey().saveData(NBTDynamicOps.INSTANCE, t.getValue())));
	}

	@Override
	public void load(CompoundNBT data, boolean isPacket) {
		for(SpecialDataType<?> tc:SpecialDataTypes.TYPE_REGISTRY) {
        	if(data.contains(tc.getId())) {
        		System.out.println(tc.getId());
        		SpecialData raw=tc.loadData(NBTDynamicOps.INSTANCE, data.get(tc.getId()));
        		raw.setHolder(this);
        		data.put(tc.getId(), data);
        	}
        }
	}
	
	Map<SpecialDataType,SpecialData> data=new HashMap<>();
	@SuppressWarnings("unchecked")
	public <U extends SpecialData> U getData(SpecialDataType<U> cap){
		U ret= (U) data.computeIfAbsent(cap,s->cap.create((T) this));
		ret.setHolder(this);
		return ret;
	}
	public <U extends SpecialData> U setData(SpecialDataType<U> cap, U data){
		this.data.put(cap, data);
		data.setHolder(this);
		return data;
	}
	public SpecialData getDataRaw(SpecialDataType<?> cap){
		SpecialData ret= data.computeIfAbsent(cap,s->cap.createRaw(this));
		ret.setHolder(this);
		return ret;
	}

	@Override
	public <U extends SpecialData> Optional<U> getOptional(SpecialDataType<U> cap) {
		
		return Optional.ofNullable((U)data.get(cap));
	}
}
