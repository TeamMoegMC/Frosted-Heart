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

package com.teammoeg.frostedheart.content.steamenergy;

import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.teammoeg.frostedheart.FHTileTypes;
import com.teammoeg.frostedheart.base.block.FHBaseBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class DebugHeaterBlock extends FHBaseBlock implements ISteamEnergyBlock {
    public DebugHeaterBlock(String name, Properties blockProps,
                            BiFunction<Block, net.minecraft.world.item.Item.Properties, Item> createItemBlock) {
        super(name, blockProps, createItemBlock);
    }


    @Nullable
    @Override
    public BlockEntity createTileEntity(@Nonnull BlockState state, @Nonnull BlockGetter world) {
        return FHTileTypes.DEBUGHEATER.get().create();
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.LEVEL_FLOWING);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
                                             InteractionHand hand, BlockHitResult hit) {
        InteractionResult superResult = super.use(state, world, pos, player, hand, hit);
        if (superResult.consumesAction() || player.isShiftKeyDown())
            return superResult;
        ItemStack item = player.getItemInHand(hand);
        if (item.getItem().equals(Item.byBlock(this))) {
            state = state.cycle(BlockStateProperties.LEVEL_FLOWING);
            world.setBlockAndUpdate(pos, state);
            player.displayClientMessage(new TextComponent(String.valueOf(state.getValue(BlockStateProperties.LEVEL_FLOWING))), true);
            return InteractionResult.SUCCESS;
        }
        return superResult;
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    @Override
    public boolean canConnectFrom(LevelAccessor world, BlockPos pos, BlockState state, Direction dir) {
        return true;
    }
}
