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

package com.teammoeg.frostedheart.research.effects;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.compat.jei.JEICompat;
import com.teammoeg.frostedheart.research.ResearchListeners;
import com.teammoeg.frostedheart.research.data.TeamResearchData;
import com.teammoeg.frostedheart.research.gui.FHIcons;
import com.teammoeg.frostedheart.research.gui.FHIcons.FHIcon;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Allows the research team to use certain machines
 */
public class EffectShowCategory extends Effect {

    ResourceLocation cate;

    EffectShowCategory() {
        super();
    }

    public EffectShowCategory(ResourceLocation cat) {
        super();
        cate = cat;
    }

    public EffectShowCategory(JsonObject jo) {
        super(jo);
        cate = new ResourceLocation(jo.get("category").getAsString());
    }

    public EffectShowCategory(FriendlyByteBuf pb) {
        super(pb);
        cate=pb.readResourceLocation();

    }

    @Override
    public void init() {
        ResearchListeners.categories.add(cate);
    }

    @Override
    public boolean grant(TeamResearchData team, Player triggerPlayer, boolean isload) {
        team.categories.add(cate);
        return true;
    }

    @Override
    public void revoke(TeamResearchData team) {
        team.categories.remove(cate);
    }




    @Override
    public JsonObject serialize() {
        JsonObject jo = super.serialize();
        jo.addProperty("category", cate.toString());
        return jo;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        buffer.writeResourceLocation(cate);
    }


    @Override
    public FHIcon getDefaultIcon() {
        return FHIcons.getIcon(Blocks.CRAFTING_TABLE);
    }

    @Override
    public MutableComponent getDefaultName() {
        return GuiUtils.translateGui("effect.category");
    }

    @Override
    public List<Component> getDefaultTooltip() {
        List<Component> tooltip = new ArrayList<>();
        return tooltip;
    }

    @Override
    public String getBrief() {
        return "JEI Category " + cate.toString();
    }
    @OnlyIn(Dist.CLIENT)
	@Override
	public void onClick() {
		if(cate!=null)
			JEICompat.showJEICategory(cate);
	}
}
