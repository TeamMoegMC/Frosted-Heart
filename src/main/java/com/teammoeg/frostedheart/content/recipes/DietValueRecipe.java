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

package com.teammoeg.frostedheart.content.recipes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.api.IDietGroup;

public class DietValueRecipe extends IESerializableRecipe {
    public static RecipeType<DietValueRecipe> TYPE;
    public static RegistryObject<IERecipeSerializer<DietValueRecipe>> SERIALIZER;
    final Map<String, Float> groups;
    Map<IDietGroup, Float> cache;
    public final Item item;

    public DietValueRecipe(ResourceLocation id, Item it) {
        this(id, new HashMap<>(), it);
    }

    public static Map<Item, DietValueRecipe> recipeList = Collections.emptyMap();

    public DietValueRecipe(ResourceLocation id, Map<String, Float> groups, Item it) {
        super(ItemStack.EMPTY, TYPE, id);
        this.groups = groups;
        this.item = it;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    public Map<IDietGroup, Float> getValues() {
        if (cache == null)
            cache = groups.entrySet().stream().collect(Collectors.toMap(e -> DietGroupCodec.getGroup(e.getKey()), e -> e.getValue()));
        return cache;
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return SERIALIZER.get();
    }

    @Override
    public String toString() {
        return "DietValueRecipe [groups=" + groups + ", item=" + item + "]";
    }
    public static class Serializer extends IERecipeSerializer<DietValueRecipe> {

        @Override
        public DietValueRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new DietValueRecipe(recipeId, DietGroupCodec.read(buffer), buffer.readRegistryId());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DietValueRecipe recipe) {
            DietGroupCodec.write(buffer, recipe.groups);
            buffer.writeRegistryId(recipe.item);
        }

        @Override
        public ItemStack getIcon() {
            return ItemStack.EMPTY;
        }

        @Override
        public DietValueRecipe readFromJson(ResourceLocation id, JsonObject json) {
            Map<String, Float> m = json.get("groups").getAsJsonObject().entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getAsFloat()));
            Item i = ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.get("item").getAsString()));
            if (i == null || i == Items.AIR)
                return null;
            return new DietValueRecipe(id, m, i);
        }

    }

}
