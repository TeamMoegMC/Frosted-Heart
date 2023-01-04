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

package com.teammoeg.frostedheart.research.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.teammoeg.frostedheart.FHMain;

import dev.ftb.mods.ftblibrary.util.ClientTextComponentUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class FHTextUtil {

    private FHTextUtil() {
    }
    @Nonnull
    public static ITextComponent get(String orig, String type, Supplier<String> pid) {
        if (orig==null||orig.length() == 0)
            return new TranslationTextComponent(type + "." + FHMain.MODID + "." + pid.get());
        if (orig.startsWith("@")) {
            if (orig.length() == 1)
                return new TranslationTextComponent(type + "." + FHMain.MODID + "." + pid.get());
            return new TranslationTextComponent(orig.substring(1));
        }

        return ClientTextComponentUtils.parse(orig);
    }

    @Nullable
    public static ITextComponent getOptional(String orig, String type, Supplier<String> pid) {
        if (orig==null||orig.length() == 0) {
        	String key=type + "." + FHMain.MODID + "." + pid.get();
    		if(I18n.hasKey(key))
    			return new TranslationTextComponent(key);
    		return null;
        }
        if (orig.startsWith("@")) {
            if (orig.length() == 1)
                return new TranslationTextComponent(type + "." + FHMain.MODID + "." + pid.get());
            return new TranslationTextComponent(orig.substring(1));
        }

        return ClientTextComponentUtils.parse(orig);
    }

    public static List<ITextComponent> get(List<String> orig, String type, Supplier<String> pid) {
        String s = pid.get();
        List<ITextComponent> li = new ArrayList<>();
        if(orig.isEmpty()) {
        	int i=0;
        	while(true) {
        		final int fi = i;
        		i++;
        		ITextComponent it=null;
        		it=getOptional(null, type, () -> s + "." + fi);
        		if(it!=null)
        			li.add(it);
        		else
        			return li;
        	}
        	
        }
        for (int i = 0; i < orig.size(); i++) {
            final int fi = i;
            li.add(get(orig.get(i), type, () -> s + "." + fi));
        }
        return li;
    }
}
