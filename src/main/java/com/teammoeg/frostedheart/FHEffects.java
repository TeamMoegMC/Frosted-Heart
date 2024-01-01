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

package com.teammoeg.frostedheart;

import java.util.ArrayList;
import java.util.List;

import com.teammoeg.frostedheart.effects.AnemiaEffect;
import com.teammoeg.frostedheart.effects.BaseEffect;
import com.teammoeg.frostedheart.effects.HyperthermiaEffect;
import com.teammoeg.frostedheart.effects.HypothermiaEffect;
import com.teammoeg.frostedheart.effects.IonEffect;
import com.teammoeg.frostedheart.effects.SaunaEffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.IForgeRegistry;

public class FHEffects {
    public static List<MobEffect> EFFECTS = new ArrayList<MobEffect>();

    public static final MobEffect HYPOTHERMIA = register("hypothermia", new HypothermiaEffect(MobEffectCategory.HARMFUL,0x57BDE8));
    public static final MobEffect HYPERTHERMIA = register("hyperthermia", new HyperthermiaEffect(MobEffectCategory.HARMFUL,0xFF9800));
    public static final MobEffect NYCTALOPIA = register("nyctalopia", new BaseEffect(MobEffectCategory.HARMFUL, 0x787dab) {
    });
    public static final MobEffect SCURVY = register("scurvy", new BaseEffect(MobEffectCategory.HARMFUL, 0xc47b34) {
    });
    public static final MobEffect ANEMIA = register("anemia", new AnemiaEffect(MobEffectCategory.HARMFUL, 0x571b1c) {
    });
    public static final MobEffect ION = register("ionizing_radiation", new IonEffect(MobEffectCategory.NEUTRAL, 0x92cbe5) {
    });
    public static final MobEffect WET = register("wet", new BaseEffect(MobEffectCategory.NEUTRAL, 816760296) {
    });
    public static final MobEffect SAD = register("lethargic", new BaseEffect(MobEffectCategory.NEUTRAL, 816760296) {
    });
    public static final MobEffect SAUNA = register("sauna", new SaunaEffect(MobEffectCategory.BENEFICIAL, 816760296) {
    });

    public static void registerAll(IForgeRegistry<MobEffect> registry) {
        for (MobEffect effect : EFFECTS) {
            registry.register(effect);
        }
    }

    public static MobEffect register(String name, MobEffect effect) {
        effect.setRegistryName(FHMain.rl(name));
        EFFECTS.add(effect);
        return effect;
    }

}
