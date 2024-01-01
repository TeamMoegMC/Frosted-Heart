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

package com.teammoeg.frostedheart.client.util;

import com.teammoeg.frostedheart.FHConfig;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.climate.TemperatureCore;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class GuiUtils {

    public static ResourceLocation makeTextureLocation(String name) {
        return FHMain.rl("textures/gui/" + name + ".png");
    }

    public static TextComponent str(String s) {
        return new TextComponent(s);
    }

    public static TranslatableComponent translateGui(String name, Object... args) {
        return new TranslatableComponent("gui." + FHMain.MODID + "." + name, args);
    }

    public static TranslatableComponent translateTooltip(String name, Object... args) {
        return new TranslatableComponent("tooltip." + FHMain.MODID + "." + name, args);
    }

    public static TranslatableComponent translateMessage(String name, Object... args) {
        return new TranslatableComponent("message." + FHMain.MODID + "." + name, args);
    }

    public static TranslatableComponent translateJeiCategory(String name, Object... args) {
        return new TranslatableComponent("gui.jei.category." + FHMain.MODID + "." + name, args);
    }

    public static TranslatableComponent translateResearchLevel(String name, Object... args) {
        return new TranslatableComponent("research.level." + FHMain.MODID + "." + name, args);
    }

    public static TranslatableComponent translateResearchCategoryName(String name, Object... args) {
        return new TranslatableComponent("research.category." + FHMain.MODID + "." + name, args);
    }

    public static TranslatableComponent translateResearchCategoryDesc(String name, Object... args) {
        return new TranslatableComponent("research.category.desc." + FHMain.MODID + "." + name, args);
    }

    public static Component translate(String string) {
        return new TranslatableComponent(string);
    }
    public static String toTemperatureIntString(float celsus) {
    	celsus=Math.max(-273.15f, celsus);
    	if(FHConfig.CLIENT.useFahrenheit.get())
			return ((int)((celsus*9/5+32)*10))/10+" °F";
		return ((int)(celsus*10))/10+" °C";
    }
    public static String toTemperatureFloatString(float celsus) {
    	celsus=Math.max(-273.15f, celsus);
    	if(FHConfig.CLIENT.useFahrenheit.get())
			return (celsus*9/5+32)+" °F";
		return celsus+" °C";
    }
    public static String toTemperatureDeltaIntString(float celsus) {
    	celsus=Math.max(-273.15f, celsus);
    	if(FHConfig.CLIENT.useFahrenheit.get())
			return ((int)((celsus*9/5)*10))/10+" °F";
		return ((int)(celsus*10))/10+" °C";
    }
    public static String toTemperatureDeltaFloatString(float celsus) {
    	celsus=Math.max(-273.15f, celsus);
    	if(FHConfig.CLIENT.useFahrenheit.get())
			return (celsus*9/5)+" °F";
		return celsus+" °C";
    }
}
