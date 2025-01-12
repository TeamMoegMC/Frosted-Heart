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

package com.teammoeg.frostedheart.content.climate.effect;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.teammoeg.frostedheart.bootstrap.reference.FHDamageTypes;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class HypothermiaEffect extends MobEffect {
    public HypothermiaEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return ImmutableList.of();
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {

        int k = 60 >> Math.max(amplifier - 2, 0);
        if (k > 1) {
            return duration % k == 0;
        }
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn instanceof ServerPlayer) {
            ((ServerPlayer) entityLivingBaseIn).causeFoodExhaustion(amplifier < 2 ? 0.044f * (amplifier + 1) : 0.132f);
            if (amplifier > 1) {
                if (entityLivingBaseIn.getHealth() > 20.0F) {
                    entityLivingBaseIn.hurt(FHDamageTypes.createSource(entityLivingBaseIn.level(), FHDamageTypes.HYPOTHERMIA, entityLivingBaseIn), 1F);
                } else if (entityLivingBaseIn.getHealth() > 10.0F) {
                    entityLivingBaseIn.hurt(FHDamageTypes.createSource(entityLivingBaseIn.level(), FHDamageTypes.HYPOTHERMIA, entityLivingBaseIn), 0.5F);
                } else if (entityLivingBaseIn.getHealth() > 5.0F) {
                    entityLivingBaseIn.hurt(FHDamageTypes.createSource(entityLivingBaseIn.level(), FHDamageTypes.HYPOTHERMIA, entityLivingBaseIn), 0.3F);
                } else {
                    entityLivingBaseIn.hurt(FHDamageTypes.createSource(entityLivingBaseIn.level(), FHDamageTypes.HYPOTHERMIA, entityLivingBaseIn), 0.2F);
                }
            }
        }
    }
}
