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

package com.teammoeg.frostedheart.data;

import java.nio.file.Path;

import com.cannolicatfish.rankine.init.RankineTags;
import com.google.common.collect.ImmutableList;
import com.teammoeg.frostedheart.FHItems;
import com.teammoeg.frostedheart.FHMain;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.data.TagsProvider.Builder;

public class FHItemTagProvider extends TagsProvider<Item> {

	public FHItemTagProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, Registry.ITEM,FHMain.MODID, existingFileHelper);
	}


	@Override
	protected void addTags() {
		tag("colored_thermos").add(FHItems.allthermos.toArray(new Item[0]));
		
		tag("colored_advanced_thermos").add(FHItems.alladvthermos.toArray(new Item[0]));
		tag("thermos")
		.addTag(ItemTags.createOptional(mrl("colored_thermos")))
		.add(FHItems.thermos)
		.addTag(ItemTags.createOptional(mrl("colored_advanced_thermos")))
		.add(FHItems.advanced_thermos);
		tag("chicken_feed").addTag(RankineTags.Items.BREEDABLES_CHICKEN).addTag(ftag("seeds"));
		tag("cow_feed").addTag(RankineTags.Items.BREEDABLES_COW);
	}


	private Builder<Item> tag(String s) {
		return this.tag(ItemTags.createOptional(mrl(s)));
	}

	private Builder<Item> tag(ResourceLocation s) {
		return this.tag(ItemTags.createOptional(s));
	}

	private ResourceLocation rl(RegistryObject<Item> it) {
		return it.getId();
	}

	private ResourceLocation rl(String r) {
		return new ResourceLocation(r);
	}

	private ResourceLocation mrl(String s) {
		return new ResourceLocation(FHMain.MODID, s);
	}

	private ResourceLocation frl(String s) {
		return new ResourceLocation("forge", s);
	}

	private IOptionalNamedTag<Item> ftag(String s) {
		IOptionalNamedTag<Item> tag = ItemTags.createOptional(new ResourceLocation("forge", s));
		return tag;
	}

	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	@Override
	public String getName() {
		return FHMain.MODID + " item tags";
	}

	private Item item(String s) {
		Item i = ForgeRegistries.ITEMS.getValue(mrl(s));
		return i.asItem();// just going to cause trouble if not exists
	}




	@Override
	protected Path getPath(ResourceLocation id) {
		return this.generator.getOutputFolder()
				.resolve("data/" + id.getNamespace() + "/tags/items/" + id.getPath() + ".json");
	}
}
