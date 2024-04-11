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
import com.teammoeg.frostedheart.content.research.data.TeamResearchData;
import com.teammoeg.frostedheart.content.research.gui.FHIcons;
import com.teammoeg.frostedheart.content.research.gui.FHIcons.FHIcon;
import com.teammoeg.frostedheart.util.FHUtils;
import com.teammoeg.frostedheart.util.TranslateUtils;
import com.teammoeg.frostedheart.util.io.CodecUtil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

/**
 * Reward the research team item rewards
 */
public class EffectItemReward extends Effect {
	public static final Codec<EffectItemReward> CODEC=RecordCodecBuilder.create(t->t.group(Effect.BASE_CODEC.forGetter(Effect::getBaseData),
	Codec.list(CodecUtil.ITEMSTACK_CODEC).fieldOf("rewards").forGetter(o->o.rewards))
	.apply(t,EffectItemReward::new));
    List<ItemStack> rewards;

    public EffectItemReward(BaseData data, List<ItemStack> rewards) {
		super(data);
		this.rewards = new ArrayList<>(rewards);
	}

	public EffectItemReward(ItemStack... stacks) {
        super();
        rewards = new ArrayList<>();

        rewards.addAll(Arrays.asList(stacks));
    }


    @Override
    public String getBrief() {
        if (rewards.isEmpty())
            return "Reward nothing";

        return "Reward " + rewards.get(0).getDisplayName().getString() + (rewards.size() > 1 ? " ..." : "");
    }

    @Override
    public FHIcon getDefaultIcon() {
        if (!rewards.isEmpty()) {
            return FHIcons.getStackIcons(rewards);
        }
        return FHIcons.nop();
    }

    @Override
    public IFormattableTextComponent getDefaultName() {
        return TranslateUtils.translateGui("effect.item_reward");
    }

    @Override
    public List<ITextComponent> getDefaultTooltip() {
        List<ITextComponent> tooltip = new ArrayList<>();
        for (ItemStack stack : rewards) {
            if (stack.getCount() == 1)
                tooltip.add(stack.getDisplayName());
            else
                tooltip.add(((IFormattableTextComponent) stack.getDisplayName()).appendSibling(TranslateUtils.str(" x " + stack.getCount())));
        }
        return tooltip;
    }


    public List<ItemStack> getRewards() {
        return rewards;
    }

    @Override
    public boolean grant(TeamResearchData team, PlayerEntity triggerPlayer, boolean isload) {
        if (triggerPlayer == null || isload) return false;
        for (ItemStack s : rewards) {
            FHUtils.giveItem(triggerPlayer, s.copy());

        }
        return true;
    }

    @Override
    public void init() {

    }

    //We dont confiscate players items, that is totally unnecessary
    @Override
    public void revoke(TeamResearchData team) {

    }
}
