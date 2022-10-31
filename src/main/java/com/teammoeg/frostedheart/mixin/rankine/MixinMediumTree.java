/*
 * Copyright (c) 2022 TeamMoeg
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

import com.cannolicatfish.rankine.world.trees.BlackBirchTree;
import com.cannolicatfish.rankine.world.trees.BlackWalnutTree;
import com.cannolicatfish.rankine.world.trees.CoconutPalmTree;
import com.cannolicatfish.rankine.world.trees.PinyonPineTree;
import com.cannolicatfish.rankine.world.trees.YellowBirchTree;
import com.teammoeg.frostedheart.util.FHUtils;

import net.minecraft.block.BlockState;
import net.minecraft.block.trees.Tree;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;

@Mixin({YellowBirchTree.class,PinyonPineTree.class,CoconutPalmTree.class,BlackWalnutTree.class, CoconutPalmTree.class,BlackBirchTree.class})
public abstract class MixinMediumTree extends Tree {
    @Override
    public boolean attemptGrowTree(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state,
                                   Random rand) {
        if (FHUtils.canTreeGenerate(world, pos, rand,7))
            return super.attemptGrowTree(world, chunkGenerator, pos, state, rand);
        return false;
    }

}
