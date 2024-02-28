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
import com.teammoeg.frostedheart.research.data.TeamResearchData;
import com.teammoeg.frostedheart.research.gui.FHIcons;
import com.teammoeg.frostedheart.research.gui.FHIcons.FHIcon;
import com.teammoeg.frostedheart.util.FHUtils;
import com.teammoeg.frostedheart.util.client.GuiUtils;
import com.teammoeg.frostedheart.util.io.SerializeUtil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

/**
 * Reward the research team item rewards
 */
public class EffectItemReward extends Effect {

    List<ItemStack> rewards;

    public EffectItemReward(ItemStack... stacks) {
        super();
        rewards = new ArrayList<>();

        for (ItemStack stack : stacks) {
            rewards.add(stack);
        }
    }

    public EffectItemReward(JsonObject jo) {
        super(jo);
        rewards = SerializeUtil.parseJsonElmList(jo.get("rewards"), SerializeUtil::fromJson);
    }

    public EffectItemReward(PacketBuffer pb) {
        super(pb);
        rewards = SerializeUtil.readList(pb, PacketBuffer::readItemStack);
    }

    @Override
    public String getBrief() {
        if (rewards.isEmpty())
            return "Reward nothing";

        return "Reward " + rewards.get(0).getDisplayName().getString() + (rewards.size() > 1 ? " ..." : "");
    }

    @Override
    public FHIcon getDefaultIcon() {
        if (rewards.size() != 0) {
            return FHIcons.getStackIcons(rewards);
        }
        return FHIcons.nop();
    }

    @Override
    public IFormattableTextComponent getDefaultName() {
        return GuiUtils.translateGui("effect.item_reward");
    }

    @Override
    public List<ITextComponent> getDefaultTooltip() {
        List<ITextComponent> tooltip = new ArrayList<>();
        for (ItemStack stack : rewards) {
            if (stack.getCount() == 1)
                tooltip.add(stack.getDisplayName());
            else
                tooltip.add(((IFormattableTextComponent) stack.getDisplayName()).appendSibling(GuiUtils.str(" x " + stack.getCount())));
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

    @Override
    public JsonObject serialize() {
        JsonObject jo = super.serialize();
        jo.add("rewards", SerializeUtil.toJsonList(rewards, SerializeUtil::toJson));
        return jo;
    }

    @Override
    public void write(PacketBuffer buffer) {
        super.write(buffer);
        SerializeUtil.writeList2(buffer, rewards, PacketBuffer::writeItemStack);
    }
}
