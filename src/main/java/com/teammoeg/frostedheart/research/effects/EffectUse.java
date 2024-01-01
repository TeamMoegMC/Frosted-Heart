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
import com.teammoeg.frostedheart.research.ResearchListeners;
import com.teammoeg.frostedheart.research.data.TeamResearchData;
import com.teammoeg.frostedheart.research.gui.FHIcons;
import com.teammoeg.frostedheart.research.gui.FHIcons.FHIcon;
import com.teammoeg.frostedheart.research.gui.TechIcons;
import com.teammoeg.frostedheart.util.SerializeUtil;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Allows the research team to use certain machines
 */
public class EffectUse extends Effect {

    List<Block> blocks;

    EffectUse() {
        super();
        this.blocks = new ArrayList<>();
    }

    public EffectUse(Block... blocks) {
        super();
        this.blocks = new ArrayList<>();
        for (Block b : blocks) {
            this.blocks.add(b);
        }
    }

    public EffectUse(JsonObject jo) {
        super(jo);
        blocks = SerializeUtil.parseJsonElmList(jo.get("blocks"), e -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(e.getAsString())));
    }

    public EffectUse(FriendlyByteBuf pb) {
        super(pb);
        blocks = SerializeUtil.readList(pb, p -> p.readRegistryIdUnsafe(ForgeRegistries.BLOCKS));

    }

    @Override
    public void init() {
        ResearchListeners.block.addAll(blocks);
    }

    @Override
    public boolean grant(TeamResearchData team, Player triggerPlayer, boolean isload) {
        team.block.addAll(blocks);
        return true;
    }

    @Override
    public void revoke(TeamResearchData team) {
        team.block.removeAll(blocks);
    }


    @Override
    public JsonObject serialize() {
        JsonObject jo = super.serialize();
        jo.add("blocks", SerializeUtil.toJsonStringList(blocks, Block::getRegistryName));
        return jo;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        SerializeUtil.writeList(buffer, blocks, (b, p) -> p.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, b));
    }

    @Override
    public FHIcon getDefaultIcon() {
        return FHIcons.getIcon(FHIcons.getIcon(blocks.toArray(new Block[0])), FHIcons.getDelegateIcon("hand"));
    }

    @Override
    public MutableComponent getDefaultName() {
        return GuiUtils.translateGui("effect.use");
    }

    @Override
    public List<Component> getDefaultTooltip() {
        List<Component> tooltip = new ArrayList<>();
        for (Block b : blocks) {
            tooltip.add(b.getName());
        }

        return tooltip;
    }

    @Override
    public String getBrief() {
        if (blocks.isEmpty())
            return "Use nothing";
        return "Use " + blocks.get(0).getName().getString() + (blocks.size() > 1 ? " ..." : "");
    }
}
