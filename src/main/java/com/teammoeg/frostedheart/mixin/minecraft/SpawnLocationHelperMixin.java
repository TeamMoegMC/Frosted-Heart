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

package com.teammoeg.frostedheart.mixin.minecraft;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.SpawnLocationHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

@Mixin(SpawnLocationHelper.class)
public class SpawnLocationHelperMixin {
	/**
	 * @author khjxiaogu
	 * @reason To make spawn point more suitable
	 * */
	@Overwrite
	@Nullable
	public static BlockPos func_241092_a_(ServerWorld p_241092_0_, int p_241092_1_, int p_241092_2_,
			boolean p_241092_3_) {
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_241092_1_, 0, p_241092_2_);
		Biome biome = p_241092_0_.getBiome(blockpos$mutable);
		boolean flag = p_241092_0_.getDimensionType().getHasCeiling();
		BlockState blockstate = biome.getGenerationSettings().getSurfaceBuilderConfig().getTop();
		if (p_241092_3_ && !blockstate.getBlock().isIn(BlockTags.VALID_SPAWN)) {
			return null;
		}
		Chunk chunk = p_241092_0_.getChunk(p_241092_1_ >> 4, p_241092_2_ >> 4);
		int i = flag ? p_241092_0_.getChunkProvider().getChunkGenerator().getGroundHeight()
				: chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, p_241092_1_ & 15, p_241092_2_ & 15);
		if (i < 0) {
			return null;
		}
		int j = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE, p_241092_1_ & 15, p_241092_2_ & 15);
		if (j <= i && j > chunk.getTopBlockY(Heightmap.Type.OCEAN_FLOOR, p_241092_1_ & 15, p_241092_2_ & 15)) {
			return null;
		}
		for (int k = i + 3; k >= 0; --k) {
			blockpos$mutable.setPos(p_241092_1_, k, p_241092_2_);
			BlockState blockstate1 = p_241092_0_.getBlockState(blockpos$mutable);
			if (!blockstate1.getFluidState().isEmpty()) {
				break;
			}

			if (blockstate1.equals(blockstate)||blockstate1.getBlock().isIn(BlockTags.VALID_SPAWN)) {
				return blockpos$mutable.up().toImmutable();
			}
		}

		return null;
	}

}
