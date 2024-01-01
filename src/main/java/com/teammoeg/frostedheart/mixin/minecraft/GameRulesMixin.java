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

package com.teammoeg.frostedheart.mixin.minecraft;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.teammoeg.frostedheart.util.FHGameRule;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Key;
import net.minecraft.world.level.GameRules.Value;

@Mixin(GameRules.class)
public class GameRulesMixin implements FHGameRule {
    @Shadow
    private Map<Key<?>, Value<?>> rules;

    @Inject(method = "getBoolean", at = @At(value = "HEAD"), cancellable = true)
    public void disableWeatherCycle(GameRules.Key<GameRules.BooleanValue> key, CallbackInfoReturnable<Boolean> cir) {
        if (key == GameRules.DO_WEATHER_CYCLE) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean isWeatherCycle() {
        return ((BooleanValue) rules.get(GameRules.DO_WEATHER_CYCLE)).get();
    }
}
