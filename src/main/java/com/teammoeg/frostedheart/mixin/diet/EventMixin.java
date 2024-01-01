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

package com.teammoeg.frostedheart.mixin.diet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.teammoeg.frostedheart.util.FixedDietProvider;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.api.IDietTracker;
import top.theillusivec4.diet.common.capability.DietCapabilityEventsListener;
import top.theillusivec4.diet.common.capability.PlayerDietTracker;

@Mixin(DietCapabilityEventsListener.class)
public class EventMixin {
	@Overwrite(remap = false)
	public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {

		if (evt.getObject() instanceof Player) {
			final IDietTracker tracker = new PlayerDietTracker((Player) evt.getObject());
			final LazyOptional<IDietTracker> capability = LazyOptional.of(() -> tracker);
			evt.addCapability(DietCapability.DIET_TRACKER_ID, new FixedDietProvider(capability));
		}
	}
	
}
