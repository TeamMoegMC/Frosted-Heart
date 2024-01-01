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

package com.teammoeg.frostedheart.base.block;

import java.util.function.BiFunction;

import javax.annotation.Nullable;

import com.teammoeg.frostedheart.FHContent;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.content.steamenergy.ISteamEnergyBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.TickPriority;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class FluidPipeBlock<T extends FluidPipeBlock<T>> extends PipeBlock implements SimpleWaterloggedBlock {
    Class<T> type;
    public final String name;
    protected int lightOpacity;

    public ResourceLocation createRegistryName() {
        return new ResourceLocation(FHMain.MODID, name);
    }

    public T setLightOpacity(int opacity) {
        lightOpacity = opacity;
        return (T) this;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        if (state.isSolidRender(worldIn, pos))
            return lightOpacity;
        else
            return state.propagatesSkylightDown(worldIn, pos) ? 0 : 1;
    }

    public FluidPipeBlock(Class<T> type, String name, Properties blockProps, BiFunction<Block, Item.Properties, Item> createItemBlock) {
        super(4 / 16f, blockProps);
        this.name = name;
        lightOpacity = 15;

        ResourceLocation registryName = createRegistryName();
        setRegistryName(registryName);

        FHContent.registeredFHBlocks.add(this);
        Item item = createItemBlock.apply(this, new Item.Properties().tab(FHMain.itemGroup));
        if (item != null) {
            item.setRegistryName(registryName);
            FHContent.registeredFHItems.add(item);
        }
        this.type = type;

        BlockState defaultState = defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false);
        for (Direction d : Direction.values())
            defaultState = defaultState.setValue(PROPERTY_BY_DIRECTION.get(d), false);
        this.registerDefaultState(defaultState);
    }


    public BlockState getAxisState(Axis axis) {
        BlockState defaultState = defaultBlockState();
        for (Direction d : Direction.values())
            defaultState = defaultState.setValue(PROPERTY_BY_DIRECTION.get(d), d.getAxis() == axis);
        return defaultState;
    }

    @Nullable
    private Axis getAxis(BlockGetter world, BlockPos pos, BlockState state) {
        if (!type.isInstance(state.getBlock())) return null;
        for (Axis axis : Axis.values()) {
            Direction d1 = Direction.get(AxisDirection.NEGATIVE, axis);
            Direction d2 = Direction.get(AxisDirection.POSITIVE, axis);
            boolean openAt1 = isOpenAt(state, d1);
            boolean openAt2 = isOpenAt(state, d2);
            if (openAt1 && openAt2) {
                return axis;
            }
        }
        return null;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    public boolean canConnectTo(LevelAccessor world, BlockPos neighbourPos, BlockState neighbour, Direction direction) {
        if (neighbour.getBlock() instanceof ISteamEnergyBlock && ((ISteamEnergyBlock) neighbour.getBlock()).canConnectFrom(world, neighbourPos, neighbour, direction))
            return true;

        return false;
    }

    public boolean shouldDrawRim(LevelAccessor world, BlockPos pos, BlockState state,
                                 Direction direction) {
        if (!isOpenAt(state, direction))
            return false;
        BlockPos offsetPos = pos.relative(direction);
        BlockState facingState = world.getBlockState(offsetPos);
        if (!type.isInstance(facingState.getBlock()))
            return true;
        if (!canConnectTo(world, offsetPos, facingState, direction))
            return true;
        if (!isCornerOrEndPipe(world, pos, state))
            return false;
        if (getAxis(world, pos, facingState) != null)
            return true;
        if (!shouldDrawCasing(world, pos, state) && shouldDrawCasing(world, offsetPos, facingState))
            return true;
        if (isCornerOrEndPipe(world, offsetPos, facingState))
            return direction.getAxisDirection() == AxisDirection.POSITIVE;
        return true;
    }

    public boolean isOpenAt(BlockState state, Direction direction) {
        return state.getValue(PROPERTY_BY_DIRECTION.get(direction));
    }

    public boolean isCornerOrEndPipe(BlockAndTintGetter world, BlockPos pos, BlockState state) {
        return (type.isInstance(state.getBlock())) && getAxis(world, pos, state) == null
                && !shouldDrawCasing(world, pos, state);
    }

    public boolean shouldDrawCasing(BlockAndTintGetter world, BlockPos pos, BlockState state) {
        if (!type.isInstance(state.getBlock()))
            return false;
        Axis axis = getAxis(world, pos, state);
        if (axis == null) return false;
        for (Direction direction : Direction.values())
            if (direction.getAxis() != axis && isOpenAt(state, direction))
                return true;
        return false;
    }

    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, BlockStateProperties.WATERLOGGED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState FluidState = context.getLevel()
                .getFluidState(context.getClickedPos());
        return updateBlockState(defaultBlockState(), context.getNearestLookingDirection(), null, context.getLevel(),
                context.getClickedPos()).setValue(BlockStateProperties.WATERLOGGED,
                Boolean.valueOf(FluidState.getType() == Fluids.WATER));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState,
                                          LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED))
            world.getLiquidTicks()
                    .scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        if (isOpenAt(state, direction) && neighbourState.hasProperty(BlockStateProperties.WATERLOGGED))
            world.getBlockTicks()
                    .scheduleTick(pos, this, 1, TickPriority.HIGH);
        return updateBlockState(state, direction, direction.getOpposite(), world, pos);
    }

    public BlockState updateBlockState(BlockState state, Direction preferredDirection, @Nullable Direction ignore,
                                       LevelAccessor world, BlockPos pos) {

        for (Direction d : Direction.values())
            if (d != ignore) {
                state = state.setValue(PROPERTY_BY_DIRECTION.get(d), canConnectTo(world, pos.relative(d), world.getBlockState(pos.relative(d)), d));
            }
        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false)
                : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }


}