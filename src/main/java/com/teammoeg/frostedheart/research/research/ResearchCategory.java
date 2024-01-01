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

package com.teammoeg.frostedheart.research.research;

import java.util.HashMap;
import java.util.Map;

import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.client.util.GuiUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;

public enum ResearchCategory {

    RESCUE("rescue"),
    LIVING("living"),
    PRODUCTION("production"),
    ARS("ars"),
    EXPLORATION("exploration");
    private ResourceLocation id;
    private TranslatableComponent name;
    private TranslatableComponent desc;
    private ResourceLocation icon;
	public static Map<ResourceLocation, ResearchCategory> ALL = new HashMap<>();
	static {
		for(ResearchCategory rc:ResearchCategory.values())
		ResearchCategory.ALL.put(rc.id, rc);
	}
    ResearchCategory(String id) {
        this.id = FHMain.rl(id);
        this.name = GuiUtils.translateResearchCategoryName(id);
        this.desc = GuiUtils.translateResearchCategoryDesc(id);
        this.icon = FHMain.rl("textures/gui/research/category/" + id + ".png");
        //FHMain.rl("textures/gui/research/category/background/" + id + ".png");
        
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public TranslatableComponent getDesc() {
        return desc;
    }

    public TranslatableComponent getName() {
        return name;
    }

    public ResourceLocation getId() {
        return id;
    }

}
