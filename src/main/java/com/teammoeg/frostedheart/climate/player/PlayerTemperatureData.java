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

package com.teammoeg.frostedheart.climate.player;

import javax.annotation.Nullable;

import com.teammoeg.frostedheart.FHCapabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerTemperatureData implements INBTSerializable<CompoundNBT>  {
	float previousTemp;
	float bodyTemp;
	float envTemp;
	float feelTemp;
	public float smoothedBody;//Client only, smoothed body temperature
	public float smoothedBodyPrev;//Client only, smoothed body temperature
    
	
	public PlayerTemperatureData() {
	}
	public void load (CompoundNBT nbt) {
		previousTemp=nbt.getFloat("previous_body_temperature");
		bodyTemp=nbt.getFloat("bodytemperature");
		envTemp=nbt.getFloat("envtemperature");
		feelTemp=nbt.getFloat("feeltemperature");
	}
	public void save(CompoundNBT nc) {
        nc.putFloat("previous_body_temperature",previousTemp);
        nc.putFloat("bodytemperature",bodyTemp);
        nc.putFloat("envtemperature",envTemp);
        nc.putFloat("feeltemperature",feelTemp);
	}
	public void reset() {
		previousTemp=0;
		bodyTemp=0;
		envTemp=0;
		feelTemp=0;
		smoothedBody=0;
	}
    public void update(float body, float env,float feel) {
        // update delta before body
    	previousTemp=bodyTemp;
    	bodyTemp=body;
    	envTemp=env;
    	feelTemp=feel;
    }
    public static LazyOptional<PlayerTemperatureData> getCapability(@Nullable PlayerEntity player) {
        return FHCapabilities.PLAYER_TEMP.getCapability(player);
    }
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT saved=new CompoundNBT();
		save(saved);
		return saved;
	}
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		load(nbt);
	}
	public float getPreviousTemp() {
		return previousTemp;
	}
	public float getBodyTemp() {
		return bodyTemp;
	}
	public float getEnvTemp() {
		return envTemp;
	}
	public float getFeelTemp() {
		return feelTemp;
	}
	public void setPreviousTemp(float previousTemp) {
		this.previousTemp = previousTemp;
	}
	public void setBodyTemp(float bodyTemp) {
		this.bodyTemp = bodyTemp;
	}
	public void setEnvTemp(float envTemp) {
		this.envTemp = envTemp;
	}
	public void setFeelTemp(float feelTemp) {
		this.feelTemp = feelTemp;
	}
}
