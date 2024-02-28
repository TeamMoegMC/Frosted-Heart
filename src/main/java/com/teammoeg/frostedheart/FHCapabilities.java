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

package com.teammoeg.frostedheart;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.teammoeg.frostedheart.capability.FHCapability;
import com.teammoeg.frostedheart.climate.WorldClimate;
import com.teammoeg.frostedheart.climate.chunkheatdata.ChunkHeatData;
import com.teammoeg.frostedheart.climate.data.DeathInventoryData;
import com.teammoeg.frostedheart.climate.player.PlayerTemperatureData;
import com.teammoeg.frostedheart.content.foods.dailykitchen.WantedFoodCapability;
import com.teammoeg.frostedheart.content.steamenergy.capabilities.HeatEndpoint;
import com.teammoeg.frostedheart.research.inspire.EnergyCore;
import com.teammoeg.frostedheart.scenario.runner.ScenarioConductor;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.NonNullSupplier;

public class FHCapabilities {
	private static List<FHCapability<?>> capabilities=new ArrayList<>();
	public static final FHCapability<WorldClimate> CLIMATE_DATA=register(WorldClimate.class);
	public static final FHCapability<DeathInventoryData> DEATH_INV=register(DeathInventoryData.class);
	public static final FHCapability<PlayerTemperatureData> PLAYER_TEMP=register(PlayerTemperatureData.class);
	public static final FHCapability<EnergyCore> ENERGY=register(EnergyCore.class);
	public static final FHCapability<ScenarioConductor> SCENARIO=register(ScenarioConductor.class);
	public static final FHCapability<ChunkHeatData> CHUNK_HEAT=register(ChunkHeatData.class);
	public static final FHCapability<HeatEndpoint> HEAT_EP=register(HeatEndpoint.class);
	public static final FHCapability<WantedFoodCapability> WANTED_FOOD=register(WantedFoodCapability.class);
	
	public static void setup() {
		for(FHCapability<?> cap:capabilities)
			cap.setup();
	}
	public static <T extends INBTSerializable<CompoundNBT>> FHCapability<T> register(Class<T> capClass){
		Constructor<T> ctor;
		try {
			try {	
				ctor=capClass.getConstructor();
			}catch(NoSuchMethodException ex) {
				try {
					ctor=capClass.getDeclaredConstructor();
				} catch (NoSuchMethodException e) {
					throw new IllegalArgumentException("No no-arg constructor found for capability "+capClass.getSimpleName());
				}
			}
		}catch(SecurityException ex) {
			throw new RuntimeException(ex);
		}
		ctor.setAccessible(true);
		final Constructor<T> fctor=ctor;
		FHCapability<T> cap=new FHCapability<>(capClass,()->{
			try {
				return fctor.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Can not create capability "+capClass.getSimpleName());
			}
		});
		capabilities.add(cap);
		return cap;
	}
	public static <T extends INBTSerializable<CompoundNBT>> FHCapability<T> register(Class<T> capClass,NonNullSupplier<T> sup){
		FHCapability<T> cap=new FHCapability<>(capClass,sup);
		capabilities.add(cap);
		return cap;
	}
}
