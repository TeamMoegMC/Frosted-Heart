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

package com.teammoeg.frostedheart.content.agriculture;

import java.util.Random;
import java.util.function.BiFunction;

import com.teammoeg.frostedheart.FHContent;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.climate.WorldClimate;
import com.teammoeg.frostedheart.climate.chunkdata.ChunkData;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class FHCropBlock extends CropBlock {
    public final String name;
    private int growTemperature;

    public FHCropBlock(String name, int growTemperature, Properties builder, BiFunction<Block, Item.Properties, Item> createItemBlock) {
        super(builder);
        this.name = name;
        this.growTemperature = growTemperature;
        FHContent.registeredFHBlocks.add(this);
        ResourceLocation registryName = createRegistryName();
        setRegistryName(registryName);
        Item item = createItemBlock.apply(this, new Item.Properties().tab(FHMain.itemGroup));
        if (item != null) {
            item.setRegistryName(registryName);
            FHContent.registeredFHItems.add(item);
        }
    }

    public FHCropBlock(String name, int growTemperature, Properties builder) {
        super(builder);
        this.name = name;
        this.growTemperature = growTemperature;
        FHContent.registeredFHBlocks.add(this);
        ResourceLocation registryName = createRegistryName();
        setRegistryName(registryName);
        //custom BlockItem
    }


    public ResourceLocation createRegistryName() {
        return new ResourceLocation(FHMain.MODID, name);
    }

    public int getGrowTemperature() {
        return this.growTemperature;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
        if (!worldIn.isAreaLoaded(pos, 1))
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (worldIn.getRawBrightness(pos, 0) >= 9) {
            int i = this.getAge(state);
            float temp = ChunkData.getTemperature(worldIn, pos);
            if (temp < growTemperature) {
                if (temp < growTemperature-10&&worldIn.getRandom().nextInt(3) == 0) {
                    worldIn.setBlock(pos, this.defaultBlockState(), 2);
                }
            }else if(temp>WorldClimate.VANILLA_PLANT_GROW_TEMPERATURE_MAX) {
            	if (worldIn.getRandom().nextInt(3) == 0) {
                    worldIn.setBlock(pos, this.defaultBlockState(), 2);
                }
            }else  if (i < this.getMaxAge()) {
                float f = getGrowthSpeed(this, worldIn, pos);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int) (25.0F / f) + 1) == 0)) {
                    worldIn.setBlock(pos, this.getStateForAge(i + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, Random rand, BlockPos pos, BlockState state) {
        float temp = ChunkData.getTemperature(worldIn, pos);
        return temp >= growTemperature;
    }
}
