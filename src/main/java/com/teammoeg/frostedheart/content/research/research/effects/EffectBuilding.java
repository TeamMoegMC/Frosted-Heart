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
import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.frostedheart.content.research.ResearchListeners;
import com.teammoeg.frostedheart.content.research.data.TeamResearchData;
import com.teammoeg.frostedheart.content.research.gui.FHIcons;
import com.teammoeg.frostedheart.content.research.gui.FHIcons.FHIcon;
import com.teammoeg.frostedheart.util.TranslateUtils;
import com.teammoeg.frostedheart.util.client.ClientUtils;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.items.IEItems;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.gui.ManualScreen;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Allows forming multiblock
 */
public class EffectBuilding extends Effect {
	public static final Codec<EffectBuilding> CODEC=RecordCodecBuilder.create(t->t.group(Effect.BASE_CODEC.forGetter(Effect::getBaseData),
	ResourceLocation.CODEC.xmap(MultiblockHandler::getByUniqueName, IMultiblock::getUniqueName).fieldOf("multiblock").forGetter(o->o.multiblock))
	.apply(t,EffectBuilding::new));
    IMultiblock multiblock;

    EffectBuilding() {
        super();
    }

    public EffectBuilding(IETemplateMultiblock s, Block b) {
        super();
        super.icon = FHIcons.getIcon(b);
        tooltip.add("@" + b.getTranslationKey());
        multiblock = s;

    }

    public EffectBuilding(BaseData data, IMultiblock multiblock) {
		super(data);
		this.multiblock = multiblock;
	}

    @Override
    public String getBrief() {
        return "Build " + multiblock.getUniqueName();
    }

    @Override
    public FHIcon getDefaultIcon() {
        return FHIcons.getIcon(IEItems.Tools.hammer);
    }

    @Override
    public IFormattableTextComponent getDefaultName() {
        return TranslateUtils.translateGui("effect.building");
    }

    @Override
    public List<ITextComponent> getDefaultTooltip() {
        ArrayList<ITextComponent> ar = new ArrayList<>();
        String raw = multiblock.getUniqueName().toString();
        String namespace = raw.substring(0, raw.indexOf(':'));
        String multiblock = raw.substring(raw.indexOf('/') + 1);
        String key = "block." + namespace + "." + multiblock;
        ar.add(new TranslationTextComponent(key));
        return ar;
    }

    public IMultiblock getMultiblock() {
        return multiblock;
    }

    @Override
    public boolean grant(TeamResearchData team, PlayerEntity triggerPlayer, boolean isload) {
        team.building.add(multiblock);
        return true;

    }


    @Override
    public void init() {
        ResearchListeners.multiblock.add(multiblock);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onClick() {
        if (this.isGranted() && ClientUtils.getPlayer().inventory.hasAny(ImmutableSet.of(IEItems.Tools.manual))) {
            ResourceLocation loc = multiblock.getUniqueName();
            ResourceLocation manual = new ResourceLocation(loc.getNamespace(), loc.getPath().substring(loc.getPath().lastIndexOf("/") + 1));
            ManualScreen screen = ManualHelper.getManual().getGui();
            ManualEntry entry = ManualHelper.getManual().getEntry(manual);
            if (entry != null) {

                ClientUtils.mc().displayGuiScreen(screen);
                //System.out.println(manual);
                screen.setCurrentNode(entry.getTreeNode());
                screen.page = 0;
                screen.fullInit();
            }
        }
    }

    @Override
    public void reload() {
        multiblock = MultiblockHandler.getByUniqueName(multiblock.getUniqueName());
    }

    @Override
    public void revoke(TeamResearchData team) {
        team.building.remove(multiblock);
    }

}
