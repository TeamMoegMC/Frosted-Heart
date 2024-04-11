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

package com.teammoeg.frostedheart.content.utility.incinerator;

import java.util.Random;

import com.teammoeg.frostedheart.FHTileTypes;
import com.teammoeg.frostedheart.base.block.FHBaseBlock;
import com.teammoeg.frostedheart.util.FHUtils;
import com.teammoeg.frostedheart.util.client.ClientUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

public class OilBurnerBlock extends FHBaseBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public OilBurnerBlock(Properties blockProps) {
        super(blockProps.setLightLevel(FHUtils.getLightValueLit(15)));
        this.setDefaultState(this.stateContainer.getBaseState().with(LIT, Boolean.FALSE));
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.animateTick(stateIn, worldIn, pos, rand);
        if (stateIn.get(LIT)) {
            for (int i = 0; i < rand.nextInt(2) + 2; ++i) {
                ClientUtils.spawnSmokeParticles(worldIn, pos.up());
                ClientUtils.spawnFireParticles(worldIn, pos.up());
            }
        }
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return FHTileTypes.OIL_BURNER.get().create();
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(LIT);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
                                             Hand handIn, BlockRayTraceResult hit) {
        if (FluidUtil.interactWithFluidHandler(player, handIn, worldIn, pos, hit.getFace()))
            return ActionResultType.SUCCESS;
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void onEntityWalk(World w, BlockPos p, Entity e) {
        if (w.getBlockState(p).get(LIT))
            if (e instanceof LivingEntity)
                e.setFire(60);
    }

}
