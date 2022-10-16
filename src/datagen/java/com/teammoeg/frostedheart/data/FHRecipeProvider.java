/*
 * Copyright (c) 2021 TeamMoeg
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
 */

package com.teammoeg.frostedheart.data;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.cannolicatfish.rankine.init.RankineLists;
import com.teammoeg.frostedheart.FHItems;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.thermopolium.THPFluids;
import com.teammoeg.thermopolium.data.recipes.FoodValueRecipe;

import blusunrize.immersiveengineering.api.IETags;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

public class FHRecipeProvider extends RecipeProvider {
	private final HashMap<String, Integer> PATH_COUNT = new HashMap<>();

	public FHRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> out) {
		String[] ovride=new String[] {
				"dilute_soup",
				"nail_soup"
		};
		THPFluids.getAll().filter(o->!Arrays.stream(ovride).anyMatch(o.getRegistryName().getPath()::equals)).forEach(f-> {
			for(Item i:new Item[] {FHItems.advanced_thermos,FHItems.thermos})
				out.accept(new WaterLevelFluidRecipe(new ResourceLocation(FHMain.MODID,"water_level/"+f.getRegistryName().getPath()+"_"+i.getRegistryName().getPath()),Ingredient.fromItems(i),f,2,2));
		});
		THPFluids.getAll().filter(o->o.getRegistryName().getPath().equals("dilute_soup")).forEach(f-> {
			for(Item i:new Item[] {FHItems.advanced_thermos,FHItems.thermos})
				
				out.accept(new WaterLevelFluidRecipe(new ResourceLocation(FHMain.MODID,"water_level/"+f.getRegistryName().getPath()+"_"+i.getRegistryName().getPath()),Ingredient.fromItems(i),f,3,2));
		});
		for(Block i:RankineLists.MUSHROOM_BLOCKS) {
			Item mi=i.asItem();
			out.accept(new FoodValueRecipe(new ResourceLocation(FHMain.MODID,"food_values/"+mi.getRegistryName().getPath()),3,.5f,new ItemStack(mi),mi));
		}
		
		try (PrintStream ps=new PrintStream(FMLPaths.GAMEDIR.get()
				.resolve("../src/datagen/resources/data/frostedheart/data/food_healing.csv").toFile());Scanner sc = new Scanner(FMLPaths.GAMEDIR.get()
				.resolve("../src/datagen/resources/data/frostedheart/data/food_values.csv").toFile(), "UTF-8")) {
			if(sc.hasNextLine()) {
				sc.nextLine();
				while(sc.hasNextLine()) {
					String line=sc.nextLine();
					if(!line.isEmpty()) {
						String[] parts=line.split(",");
						ResourceLocation id=new ResourceLocation(FHMain.MODID,"diet_value/"+parts[0].replaceAll(":","/"));
						ResourceLocation item=new ResourceLocation(parts[0]);
						Item it=ForgeRegistries.ITEMS.getValue(item);
						if(it==null||it==Items.AIR) {
							System.out.println(item.toString()+" not exist");
							ps.println(item+","+parts[1]);
						}else {
							Food f=it.getFood();
							if(f==null)
								ps.println(item+","+parts[1]);
							else
								ps.println(item+","+f.getHealing());
						}
						DietValueBuilder dvb=new DietValueBuilder(id,item);
						for(int i=0;i<6;i++) {
							float f=Float.parseFloat(parts[i+2])*10f;
							if(i>=4)
								f*=1.5;
							if(f!=0)
								dvb.addGroup(i,f);
						}
						out.accept(dvb);
					}
				}
			}
		}
		
		// recipesGenerator(out);
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void recipesGenerator(@Nonnull Consumer<IFinishedRecipe> out) {
		GeneratorRecipeBuilder.builder(IETags.slag, 1).addInput(ItemTags.COALS).setTime(1000).build(out,
				toRL("generator/slag"));
	}

	private ResourceLocation toRL(String s) {
		if (!s.contains("/"))
			s = "crafting/" + s;
		if (PATH_COUNT.containsKey(s)) {
			int count = PATH_COUNT.get(s) + 1;
			PATH_COUNT.put(s, count);
			return new ResourceLocation(FHMain.MODID, s + count);
		}
		PATH_COUNT.put(s, 1);
		return new ResourceLocation(FHMain.MODID, s);
	}
}
