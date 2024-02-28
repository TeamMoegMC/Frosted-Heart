/*
 * Copyright (c) 2024 TeamMoeg
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

package com.teammoeg.frostedheart.util;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryUtils {
	public static ResourceLocation getRegistryName(Item v) {
		return ForgeRegistries.ITEMS.getKey(v);
	}
	public static ResourceLocation getRegistryName(Block v) {
		return ForgeRegistries.BLOCKS.getKey(v);
	}
	public static ResourceLocation getRegistryName(Fluid v) {
		return ForgeRegistries.FLUIDS.getKey(v);
	}
	public static ResourceLocation getRegistryName(Biome b) {
		return b.getRegistryName();
	}
	public static Item getItem(ResourceLocation rl) {
		return ForgeRegistries.ITEMS.getValue(rl);
	}
	public static Item getItemThrow(ResourceLocation rl) {
		if(!ForgeRegistries.ITEMS.containsKey(rl))
			new IllegalStateException("Item: " + rl + " does not exist");
		return ForgeRegistries.ITEMS.getValue(rl);
	}
	public static Effect getEffect(ResourceLocation rl) {
		return ForgeRegistries.POTIONS.getValue(rl);
	}
	public static ResourceLocation getRegistryName(Structure<NoFeatureConfig> observatory) {
		return observatory.getRegistryName();
	}
	public static ResourceLocation getRegistryName(VillagerProfession prof) {
		return prof.getRegistryName();
	}
	public static ResourceLocation getRegistryName(EntityType<?> e) {
		return e.getRegistryName();
	}
	public static ResourceLocation getRegistryName(Enchantment enchID) {
		return ForgeRegistries.ENCHANTMENTS.getKey(enchID);
	}
	public static Block getBlock(ResourceLocation resourceLocation) {
		return ForgeRegistries.BLOCKS.getValue(resourceLocation);
	}
	public static VillagerProfession getProfess(ResourceLocation resourceLocation) {
		return ForgeRegistries.PROFESSIONS.getValue(resourceLocation);
	}
	public static Fluid getFluid(ResourceLocation resourceLocation) {
		return ForgeRegistries.FLUIDS.getValue(resourceLocation);
	}
	public static Collection<Block> getBlocks() {
		return ForgeRegistries.BLOCKS.getValues();
	}
	public static Collection<Item> getItems() {
		return ForgeRegistries.ITEMS.getValues();
	}
	public static Enchantment getEnchantment(ResourceLocation enchID) {
		return ForgeRegistries.ENCHANTMENTS.getValue(enchID);
	}
	public static EntityType<?> getEntity(ResourceLocation resourceLocation) {
		return ForgeRegistries.ENTITIES.getValue(resourceLocation);
	}
	public static Collection<EntityType<?>> getEntities() {
		return ForgeRegistries.ENTITIES.getValues();
	}
}
