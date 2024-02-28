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

import net.minecraft.block.AbstractBlock;
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

public class PebbleBlock extends FHBaseBlock {
    private static Integer typeCount = 7;
    private static IntegerProperty TYPE = IntegerProperty.create("pebbletype", 0, typeCount - 1);
    private static Integer colorCount = 3;
    private static IntegerProperty COLOR = IntegerProperty.create("pebblecolor", 0, colorCount - 1);
    static final VoxelShape shape = Block.makeCuboidShape(0, 0, 0, 16, 9, 16);
    static final VoxelShape shape2 = Block.makeCuboidShape(0, 0, 0, 16, 7, 16);
    static final VoxelShape shape3 = Block.makeCuboidShape(0, 0, 0, 16, 5, 16);
    static final VoxelShape shape4 = Block.makeCuboidShape(0, 0, 0, 16, 2, 16);
    public PebbleBlock(AbstractBlock.Properties blockProps) {
        super( blockProps);
        this.setDefaultState(this.stateContainer.getBaseState().with(TYPE, 0).with(COLOR, 0));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
        builder.add(COLOR);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Integer finalType = Math.abs(RANDOM.nextInt()) % typeCount;
        Integer finalColor = Math.abs(RANDOM.nextInt()) % colorCount;
        BlockState newState = this.stateContainer.getBaseState().with(TYPE, finalType).with(COLOR, finalColor);
        worldIn.setBlockState(pos, newState);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
                                        ISelectionContext context) {
        if(state.get(TYPE) <= 1)return shape;
        if(state.get(TYPE) <= 2)return shape2;
        if(state.get(TYPE) <= 4)return shape3;
        return shape4;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if(state.get(TYPE) <= 1)return shape;
        if(state.get(TYPE) <= 2)return shape2;
        if(state.get(TYPE) <= 4)return shape3;
        return shape4;
    }
}
