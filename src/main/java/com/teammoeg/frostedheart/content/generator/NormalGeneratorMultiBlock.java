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

package com.teammoeg.frostedheart.content.generator;

import java.util.Random;

import com.teammoeg.frostedheart.base.block.FHStoneMultiblockBlock;
import com.teammoeg.frostedheart.base.block.ManagedOwnerTile;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.RegistryObject;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class NormalGeneratorMultiBlock<T extends MultiblockPartTileEntity<? super T>> extends FHStoneMultiblockBlock<T> {
    public NormalGeneratorMultiBlock(String name, RegistryObject type) {
        super(name, type);
    }

    public NormalGeneratorMultiBlock(String name, Properties props, RegistryObject<BlockEntityType<T>> type) {
        super(name, props, type);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(LIT)) {
            if (rand.nextInt(5) == 0) {
                worldIn.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
                                             InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            BlockEntity te = Utils.getExistingTileEntity(world, pos);
            if (te instanceof AbstractGenerator && ((AbstractGenerator) te).shouldUnique()&&!(player instanceof FakePlayer)) {
            	
                if (((AbstractGenerator) te).getOwner() == null) {
                	te=((AbstractGenerator) te).master();
                    ((AbstractGenerator) te).setOwner(FTBTeamsAPI.getPlayerTeam((ServerPlayer)player).getId());
                    ((AbstractGenerator) te).regist();
                }
                
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }

}
