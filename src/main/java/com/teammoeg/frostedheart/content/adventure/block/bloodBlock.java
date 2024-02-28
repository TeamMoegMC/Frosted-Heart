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

package com.teammoeg.frostedheart.content.adventure.block;

import javax.annotation.Nullable;

import com.teammoeg.frostedheart.base.block.FHBaseBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class bloodBlock extends FHBaseBlock {
    private static IntegerProperty BLDT = IntegerProperty.create("bloodtype", 0, 3);
    private static IntegerProperty BLDC = IntegerProperty.create("bloodcolor", 0, 1);
    public bloodBlock(Properties blockProps) {
        super(blockProps);
        this.setDefaultState(this.stateContainer.getBaseState().with(BLDT, 0).with(BLDC, 0));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BLDC);
        builder.add(BLDT);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Integer finalType = Math.abs(RANDOM.nextInt()) % 4;
        Integer finalColor = Math.abs(RANDOM.nextInt()) % 2;
        BlockState newState = this.stateContainer.getBaseState().with(BLDT, finalType).with(BLDC, finalColor);
        worldIn.setBlockState(pos, newState);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return Block.makeCuboidShape(0, 0, 0, 16, 1, 16);
    }
}


