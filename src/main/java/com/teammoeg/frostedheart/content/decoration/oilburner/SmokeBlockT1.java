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

package com.teammoeg.frostedheart.content.decoration.oilburner;

import java.util.Random;
import java.util.function.BiFunction;

import com.teammoeg.frostedheart.base.block.FHBaseBlock;
import com.teammoeg.frostedheart.client.util.ClientUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public class SmokeBlockT1 extends FHBaseBlock {

    public SmokeBlockT1(String name, Properties blockProps,
                        BiFunction<Block, net.minecraft.item.Item.Properties, Item> createItemBlock) {
        super(name, blockProps, createItemBlock);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.animateTick(stateIn, worldIn, pos, rand);
        for (int i = 0; i < rand.nextInt(2) + 2; ++i) {
            ClientUtils.spawnSmokeParticles(worldIn, pos.above());
            ClientUtils.spawnFireParticles(worldIn, pos.above());
        }
    }
}
