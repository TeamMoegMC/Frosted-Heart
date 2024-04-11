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

package com.teammoeg.frostedheart.content.research.research.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.frostedheart.content.research.ResearchListeners;
import com.teammoeg.frostedheart.content.research.data.TeamResearchData;
import com.teammoeg.frostedheart.content.research.gui.FHIcons;
import com.teammoeg.frostedheart.content.research.gui.FHIcons.FHIcon;
import com.teammoeg.frostedheart.util.TranslateUtils;
import com.teammoeg.frostedheart.util.io.CodecUtil;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

/**
 * Allows the research team to use certain machines
 */
public class EffectUse extends Effect {
	public static final Codec<EffectUse> CODEC=RecordCodecBuilder.create(t->t.group(Effect.BASE_CODEC.forGetter(Effect::getBaseData),
	Codec.list(CodecUtil.registryCodec(()->Registry.BLOCK)).fieldOf("blocks").forGetter(o->o.blocks))
	.apply(t,EffectUse::new));
    List<Block> blocks;

    public EffectUse(BaseData data, List<Block> blocks) {
		super(data);
		this.blocks = new ArrayList<>(blocks);
	}

	EffectUse() {
        super();
        this.blocks = new ArrayList<>();
    }

    public EffectUse(Block... blocks) {
        super();
        this.blocks = new ArrayList<>();
        this.blocks.addAll(Arrays.asList(blocks));
    }

    @Override
    public String getBrief() {
        if (blocks.isEmpty())
            return "Use nothing";
        return "Use " + blocks.get(0).getTranslatedName().getString() + (blocks.size() > 1 ? " ..." : "");
    }

    @Override
    public FHIcon getDefaultIcon() {
        return FHIcons.getIcon(FHIcons.getIcon(blocks.toArray(new Block[0])), FHIcons.getDelegateIcon("hand"));
    }

    @Override
    public IFormattableTextComponent getDefaultName() {
        return TranslateUtils.translateGui("effect.use");
    }


    @Override
    public List<ITextComponent> getDefaultTooltip() {
        List<ITextComponent> tooltip = new ArrayList<>();
        for (Block b : blocks) {
            tooltip.add(b.getTranslatedName());
        }

        return tooltip;
    }

    @Override
    public boolean grant(TeamResearchData team, PlayerEntity triggerPlayer, boolean isload) {
        team.block.addAll(blocks);
        return true;
    }

    @Override
    public void init() {
        ResearchListeners.block.addAll(blocks);
    }

    @Override
    public void revoke(TeamResearchData team) {
        team.block.removeAll(blocks);
    }
}
