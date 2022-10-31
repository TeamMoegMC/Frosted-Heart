/*
 * Copyright (c) 2021 TeamMoeg
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
 */

package com.teammoeg.frostedheart.mixin.primalwinter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.alcatrazescapee.primalwinter.util.WeatherData;

import net.minecraftforge.common.util.LazyOptional;

@Mixin(WeatherData.class)
public interface WeatherDataAccess {
    @Accessor(remap = false)
    LazyOptional<WeatherData> getCapability();

    @Accessor(remap = false)
    boolean getAlreadySetWorldToWinter();

    @Accessor(remap = false)
    void setAlreadySetWorldToWinter(boolean flag);
}
