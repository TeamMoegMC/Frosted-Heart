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

package com.teammoeg.frostedheart.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;

public class TagLootCondition implements ILootCondition {
    public static LootConditionType TYPE;
    private Tags.IOptionalNamedTag<Block> tag;

    public TagLootCondition(Tags.IOptionalNamedTag<Block> tag) {
        this.tag = tag;
    }

    @SuppressWarnings("resource")
    @Override
    public boolean test(LootContext t) {
        if (t.has(LootParameters.ORIGIN)) {
            Vector3d v = t.get(LootParameters.ORIGIN);
            BlockPos bp = new BlockPos(v.x, v.y, v.z);
            World w = t.getWorld();
            BlockState bs = w.getBlockState(bp);

            return bs != null && tag.contains(bs.getBlock());
        }
        return false;
    }

    @Override
    public LootConditionType getConditionType() {
        return TYPE;
    }

    public static class Serializer implements ILootSerializer<TagLootCondition> {

        @Override
        public void serialize(JsonObject jsonObject, TagLootCondition matchTagCondition, JsonSerializationContext serializationContext) {
            jsonObject.addProperty("tag", matchTagCondition.tag.getName().toString());
        }

        @Nonnull
        @Override
        public TagLootCondition deserialize(JsonObject jsonObject, JsonDeserializationContext context) {
            Tags.IOptionalNamedTag<Block> optional = BlockTags.createOptional(new ResourceLocation(JSONUtils.getString(jsonObject, "tag")));
            return new TagLootCondition(optional);
        }
    }
}
