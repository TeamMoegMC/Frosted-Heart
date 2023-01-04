package com.teammoeg.frostedheart.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.api.IDietTracker;
import top.theillusivec4.diet.common.capability.DietTrackerCapability;

public class FixedDietProvider implements ICapabilitySerializable<INBT> {

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
	public INBT serializeNBT() {

		if (DietCapability.DIET_TRACKER != null&&capability.isPresent()) {
			return DietCapability.DIET_TRACKER.writeNBT(capability.orElse(EMPTY_TRACKER), null);
		}
		DietMod.LOGGER.error("Missing Diet capability!");
		return new CompoundNBT();
	}

	@Override
	public void deserializeNBT(INBT nbt) {

		if (DietCapability.DIET_TRACKER != null) {
			DietCapability.DIET_TRACKER.readNBT(capability.orElse(EMPTY_TRACKER), null, nbt);
		} else {
			DietMod.LOGGER.error("Missing Diet capability!");
		}
	}

}