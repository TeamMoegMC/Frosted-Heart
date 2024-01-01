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
import com.teammoeg.frostedheart.research.data.TeamResearchData;
import com.teammoeg.frostedheart.research.gui.FHIcons;
import com.teammoeg.frostedheart.research.gui.FHIcons.FHIcon;
import com.teammoeg.frostedheart.util.FHUtils;
import com.teammoeg.frostedheart.util.SerializeUtil;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

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

    public EffectItemReward(FriendlyByteBuf pb) {
        super(pb);
        rewards = SerializeUtil.readList(pb, FriendlyByteBuf::readItem);
    }

    @Override
    public void init() {

    }

    public List<ItemStack> getRewards() {
        return rewards;
    }

    @Override
    public boolean grant(TeamResearchData team, Player triggerPlayer, boolean isload) {
        if (triggerPlayer == null || isload) return false;
        for (ItemStack s : rewards) {
            FHUtils.giveItem(triggerPlayer, s.copy());

        }
        return true;
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
    public void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        SerializeUtil.writeList2(buffer, rewards, FriendlyByteBuf::writeItem);
    }

    @Override
    public FHIcon getDefaultIcon() {
        if (rewards.size() != 0) {
            return FHIcons.getStackIcons(rewards);
        }
        return FHIcons.nop();
    }

    @Override
    public MutableComponent getDefaultName() {
        return GuiUtils.translateGui("effect.item_reward");
    }

    @Override
    public List<Component> getDefaultTooltip() {
        List<Component> tooltip = new ArrayList<>();
        for (ItemStack stack : rewards) {
            if (stack.getCount() == 1)
                tooltip.add(stack.getHoverName());
            else
                tooltip.add(((MutableComponent) stack.getHoverName()).append(new TextComponent(" x " + stack.getCount())));
        }
        return tooltip;
    }

    @Override
    public String getBrief() {
        if (rewards.isEmpty())
            return "Reward nothing";

        return "Reward " + rewards.get(0).getHoverName().getString() + (rewards.size() > 1 ? " ..." : "");
    }
}
