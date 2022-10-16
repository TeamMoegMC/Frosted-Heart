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

import com.cannolicatfish.rankine.world.trees.BlackWalnutTree;
import com.cannolicatfish.rankine.world.trees.CoconutPalmTree;
import com.cannolicatfish.rankine.world.trees.WeepingWillowTree;
import com.cannolicatfish.rankine.world.trees.YellowBirchTree;
import com.teammoeg.frostedheart.util.FHUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.trees.Tree;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin({BlackWalnutTree.class, CoconutPalmTree.class,WeepingWillowTree.class,YellowBirchTree.class})
public abstract class MixinMediumTree extends Tree {
    @Override
    public boolean attemptGrowTree(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state,
                                   Random rand) {
        if (FHUtils.canSmallTreeGenerate(world, pos, rand))
            return super.attemptGrowTree(world, chunkGenerator, pos, state, rand);
        return false;
    }

}
