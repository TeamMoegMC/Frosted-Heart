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

package com.teammoeg.frostedheart.content.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.FHItems;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class DismantleInnerRecipe extends SpecialRecipe {
    public static RegistryObject<IERecipeSerializer<DismantleInnerRecipe>> SERIALIZER;

    protected DismantleInnerRecipe(ResourceLocation id) {
        super(id);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInventory inv, World worldIn) {
        boolean hasArmor = false;
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (itemstack == null || itemstack.isEmpty()) {
                continue;
            }
            if (hasArmor)
                return false;
            EquipmentSlotType type = MobEntity.getEquipmentSlotForItem(itemstack);
            if (type != null && type != EquipmentSlotType.MAINHAND && type != EquipmentSlotType.OFFHAND) {
                if (itemstack.hasTag()) {
                    CompoundNBT cnbt = itemstack.getTag();
                    if (!cnbt.getBoolean("inner_bounded") && !cnbt.getString("inner_cover").isEmpty())
                        hasArmor = true;
                    else
                        return false;
                }
            } else
                return false;
        }
        return hasArmor;
    }

    public static ItemStack tryDismantle(ItemStack item) {
        EquipmentSlotType type = MobEntity.getEquipmentSlotForItem(item);
        if (type != null && type != EquipmentSlotType.MAINHAND && type != EquipmentSlotType.OFFHAND) {
            if (item.hasTag() && !item.getTag().getString("inner_cover").isEmpty())
                return DismantleInnerRecipe.getDismantledResult(item);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getDismantledResult(ItemStack armoritem) {
        if (armoritem.hasTag()) {
            CompoundNBT tags = armoritem.getTag();
            if (!tags.getBoolean("inner_bounded")) {
                ResourceLocation item = new ResourceLocation(tags.getString("inner_cover"));
                CompoundNBT tag = tags.getCompound("inner_cover_tag");
                Item buff = ForgeRegistries.ITEMS.getValue(item);
                if (buff == null)
                    return ItemStack.EMPTY;
                ItemStack buffitem = new ItemStack(buff);
                if (tag != null)
                    buffitem.setTag(tag);
                return buffitem;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingInventory inv) {
        ItemStack armoritem = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (itemstack != null && !itemstack.isEmpty()) {
                if (!armoritem.isEmpty())
                    return ItemStack.EMPTY;
                EquipmentSlotType type = MobEntity.getEquipmentSlotForItem(itemstack);
                if (type != null && type != EquipmentSlotType.MAINHAND && type != EquipmentSlotType.OFFHAND)
                    if (itemstack.hasTag()) {
                        CompoundNBT cnbt = itemstack.getTag();
                        if (!cnbt.getBoolean("inner_bounded") && cnbt.getString("inner_cover") != null)
                            armoritem = itemstack;
                        else
                            return ItemStack.EMPTY;
                    } else
                        return ItemStack.EMPTY;
            }
        }

        if (!armoritem.isEmpty()) {
            return getDismantledResult(armoritem);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getItem(i);
            if (!item.isEmpty()) {
                ItemStack real = item.copy();
                real.setCount(1);
                real.removeTagKey("inner_cover_tag");
                real.removeTagKey("inner_cover");
                real.removeTagKey("inner_bounded");
                nonnulllist.set(i, real);
            }
        }

        return nonnulllist;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER.get();
    }
    public static class Serializer extends IERecipeSerializer<DismantleInnerRecipe> {
        @Override
        public ItemStack getIcon() {
            return new ItemStack(FHItems.buff_coat);
        }

        @Override
        public DismantleInnerRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
            return new DismantleInnerRecipe(recipeId);
        }

        @Nullable
        @Override
        public DismantleInnerRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            return new DismantleInnerRecipe(recipeId);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, DismantleInnerRecipe recipe) {
        }
    }
}