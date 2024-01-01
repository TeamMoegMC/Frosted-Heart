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

package com.teammoeg.frostedheart.research.machines;

import java.util.UUID;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import com.teammoeg.frostedheart.base.block.FHBaseBlock;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.climate.TemperatureCore;
import com.teammoeg.frostedheart.util.mixin.IOwnerTile;

import blusunrize.immersiveengineering.api.client.IModelOffsetProvider;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IInteractionObjectIE;
import blusunrize.immersiveengineering.common.util.Utils;
import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkHooks;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class DrawingDeskBlock extends FHBaseBlock implements IModelOffsetProvider {

    public static final BooleanProperty IS_NOT_MAIN = BooleanProperty.create("not_multi_main");
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    public static final BooleanProperty BOOK = BooleanProperty.create("has_book");

    static final VoxelShape shape = Block.box(0, 0, 0, 16, 15, 16);
    static final VoxelShape shape2 = Block.box(0, 0, 0, 16, 12, 16);

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos,
                                        CollisionContext context) {
        if (state.getValue(IS_NOT_MAIN))
            return shape2;
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(IS_NOT_MAIN))
            return shape2;
        return shape;
    }

    public DrawingDeskBlock(String name, Properties blockProps, BiFunction<Block, Item.Properties, Item> createItemBlock) {
        super(name, blockProps, createItemBlock);
        this.registerDefaultState(this.stateDefinition.any().setValue(IS_NOT_MAIN, false).setValue(BOOK, false));
        super.setLightOpacity(0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_NOT_MAIN, FACING, BOOK);
    }

    private static Direction getNeighbourDirection(boolean b, Direction directionIn) {
        return b == false ? directionIn : directionIn.getOpposite();
    }

    public static void setBlockhasbook(Level worldIn, BlockPos pos, BlockState state, boolean hasBook) {
        worldIn.setBlock(pos, state.setValue(BOOK, hasBook), 3);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getClockWise();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        return context.getLevel().getBlockState(blockpos1).canBeReplaced(context) ? this.defaultBlockState().setValue(FACING, direction) : null;
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        if (!worldIn.isClientSide && player.isCreative()) {
            boolean block = state.getValue(IS_NOT_MAIN);
            if (block == false) {
                BlockPos blockpos = pos.relative(getNeighbourDirection(state.getValue(IS_NOT_MAIN), state.getValue(FACING)));
                BlockState blockstate = worldIn.getBlockState(blockpos);
                if (blockstate.getBlock() == this && blockstate.getValue(IS_NOT_MAIN) == true) {
                    worldIn.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                }
            }
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == getNeighbourDirection(stateIn.getValue(IS_NOT_MAIN), stateIn.getValue(FACING)) && facingState.getBlock() != this) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!worldIn.isClientSide) {
            BlockPos blockpos = pos.relative(state.getValue(FACING));
            worldIn.setBlock(blockpos, state.setValue(IS_NOT_MAIN, true), 3);
            worldIn.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(worldIn, pos, 3);
        }
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        if (!state.getValue(IS_NOT_MAIN))
            return new DrawingDeskTileEntity();
        else return null;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return !state.getValue(IS_NOT_MAIN);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide && handIn == InteractionHand.MAIN_HAND && !player.isShiftKeyDown()) {
            if (!player.isCreative() && worldIn.getMaxLocalRawBrightness(pos) < 8) {
                player.displayClientMessage(GuiUtils.translateMessage("research.too_dark"), true);
            }
            else if (!player.isCreative() && TemperatureCore.getBodyTemperature(player) < -0.2) {
                player.displayClientMessage(GuiUtils.translateMessage("research.too_cold"), true);
            }
            else {
                if (state.getValue(IS_NOT_MAIN)) {
                    pos = pos.relative(getNeighbourDirection(state.getValue(IS_NOT_MAIN), state.getValue(FACING)));
                }
                BlockEntity ii = Utils.getExistingTileEntity(worldIn, pos);
                UUID crid=FTBTeamsAPI.getPlayerTeam((ServerPlayer)player).getId();
                IOwnerTile.trySetOwner(ii,crid);
                if(crid!=null&&crid.equals(IOwnerTile.getOwner(ii)))
                	NetworkHooks.openGui((ServerPlayer) player, (IInteractionObjectIE) ii, ii.getBlockPos());
                else 
                	player.displayClientMessage(GuiUtils.translateMessage("research.not_owned"), true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        super.triggerEvent(state, worldIn, pos, id, param);
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity != null && tileentity.triggerEvent(id, param);
    }

    @Override
    public BlockPos getModelOffset(BlockState arg0, Vec3i arg1) {
        if (arg0.getValue(IS_NOT_MAIN))
            return new BlockPos(1, 0, 0);
        return new BlockPos(0, 0, 0);
        //return null;
    }
}

