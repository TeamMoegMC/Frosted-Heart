/*
 * Copyright (c) 2022-2024 TeamMoeg
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

package com.teammoeg.frostedheart.mixin.rankine;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;

import com.cannolicatfish.rankine.world.trees.HoneyLocustTree;
import com.cannolicatfish.rankine.world.trees.WeepingWillowTree;
import com.teammoeg.frostedheart.util.FHUtils;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.grower.DarkOakTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.server.level.ServerLevel;

@Mixin({HoneyLocustTree.class,DarkOakTreeGrower.class,WeepingWillowTree.class})
public abstract class MixinLargeTree extends AbstractTreeGrower {
    @Override
    public boolean attemptGrowTree(ServerLevel world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state,
                                   Random rand) {
        if (FHUtils.canTreeGenerate(world, pos, rand,13))
            return super.attemptGrowTree(world, chunkGenerator, pos, state, rand);
        return false;
    }

}
