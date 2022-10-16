/*
 * Copyright (c) 2022 TeamMoeg
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

package com.teammoeg.frostedheart.mixin.snow;

import com.teammoeg.frostedheart.climate.WorldClimate;
import com.teammoeg.frostedheart.climate.chunkdata.ChunkData;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snownee.snow.Hook;

//Mixin into mixin
@Mixin(Hook.class)
public class HookMixin {
    @Inject(at = @At("HEAD"), method = "canSurvive", remap = false, cancellable = true)
    private static void canSurvive(BlockState blockState, IWorldReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cbi) {
        float t = ChunkData.getTemperature(world, pos);
        if (t < WorldClimate.HEMP_GROW_TEMPERATURE || t > WorldClimate.VANILLA_PLANT_GROW_TEMPERATURE_MAX)
            cbi.setReturnValue(false);

    }
}
