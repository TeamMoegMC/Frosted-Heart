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

package com.teammoeg.frostedheart.world.geology.ore;

import java.util.BitSet;
import java.util.Random;

import com.cannolicatfish.rankine.blocks.RankineOreBlock;
import com.cannolicatfish.rankine.util.WorldgenUtils;
import com.mojang.serialization.Codec;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

public class FHOreFeature extends Feature<FHOreFeatureConfig> {
    public FHOreFeature(Codec<FHOreFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, FHOreFeatureConfig config) {

        if (pos.getY() > reader.getHeight
                (Heightmap.Types.OCEAN_FLOOR_WG, pos.getX(), pos.getZ()) + 3) {
            return false;
        }

        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for (int x = pos.getX() - 4; x < pos.getX() + 4; x += 2) {
            for (int y = pos.getY() - 2; y < pos.getY() + 2; ++y) {
                for (int z = pos.getZ() - 4; z < pos.getZ() + 4; z += 2) {
                    blockpos$mutable.set(x, y, z);
                    if (config.target.test(reader.getBlockState(blockpos$mutable), rand)) {
                        pos = blockpos$mutable;
                        float f = rand.nextFloat() * (float) Math.PI;
                        float f1 = config.size / 8.0F;
                        int i = Mth.ceil((config.size / 16.0F * 2.0F + 1.0F) / 2.0F);
                        double maxX = pos.getX() + Math.sin(f) * f1;
                        double minX = pos.getX() - Math.sin(f) * f1;
                        double maxZ = pos.getZ() + Math.cos(f) * f1;
                        double minZ = pos.getZ() - Math.cos(f) * f1;
                        int j = 2;
                        double maxY = pos.getY() + rand.nextInt(3) - 2;
                        double minY = pos.getY() + rand.nextInt(3) - 2;
                        int startX = pos.getX() - Mth.ceil(f1) - i;
                        int startY = pos.getY() - 2 - i;
                        int startZ = pos.getZ() - Mth.ceil(f1) - i;
                        int maxSizeXZ = 2 * (Mth.ceil(f1) + i);
                        int maxSizeY = 2 * (2 + i);

                        return this.generate(reader, rand, config, maxX, minX, maxZ, minZ, maxY, minY, startX, startY, startZ, maxSizeXZ, maxSizeY);
                    }
                }
            }
        }
        return false;
    }

    protected boolean generate(LevelAccessor worldIn, Random random, FHOreFeatureConfig config, double maxX, double minX,
                               double maxZ, double minZ, double maxY, double minY, int startX, int startY, int startZ, int maxSizeXZ, int maxSizeY) {
        int totalGenerated = 0;
        BitSet generated = new BitSet(maxSizeXZ * maxSizeY * maxSizeXZ);
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        int size = config.size;
        double[] boxes = new double[size * 4];

        for (int k = 0; k < size; ++k) {
            float sizeRatio = (float) k / (float) size;
            double rX = Mth.lerp(sizeRatio, maxX, minX);
            double rY = Mth.lerp(sizeRatio, maxY, minY);
            double rZ = Mth.lerp(sizeRatio, maxZ, minZ);
            double genSize = random.nextDouble() * size / 16.0D;
            double genAmount = ((Mth.sin((float) Math.PI * sizeRatio) + 1.0F) * genSize + 1.0D) / 2.0D;
            boxes[k * 4] = rX;
            boxes[k * 4 + 1] = rY;
            boxes[k * 4 + 2] = rZ;
            boxes[k * 4 + 3] = genAmount;
        }

        for (int i = 0; i < size - 1; ++i) {
            if (boxes[i * 4 + 3] > 0.0D) {//has generation amount
                for (int j = i + 1; j < size; ++j) {
                    if (boxes[j * 4 + 3] > 0.0D) {//has generation amount
                        double d12 = boxes[i * 4] - boxes[j * 4];//delta X
                        double d13 = boxes[i * 4 + 1] - boxes[j * 4 + 1];//delta Y
                        double d14 = boxes[i * 4 + 2] - boxes[j * 4 + 2];//delta Z
                        double d15 = boxes[i * 4 + 3] - boxes[j * 4 + 3];//delta gen amount
                        if (d15 * d15 > d12 * d12 + d13 * d13 + d14 * d14) {//generation difference too large
                            if (d15 > 0.0D) {//outer slice too much
                                boxes[j * 4 + 3] = -1D;//inner slice no gen
                            } else {//inner slice too much
                                boxes[i * 4 + 3] = -1D;//outer slice no gen
                            }
                        }
                    }
                }
            }
        }

        for (int iSlice = 0; iSlice < size; ++iSlice) {
            double genAmount = boxes[iSlice * 4 + 3];//amount
            if (genAmount >= 0D) {
                double sX = boxes[iSlice * 4];
                double sY = boxes[iSlice * 4 + 1];
                double sZ = boxes[iSlice * 4 + 2];
                int genBeginX = Math.max(Mth.floor(sX - genAmount), startX);
                int genBeginY = Math.max(Mth.floor(sY - genAmount), startY);
                int genBeginZ = Math.max(Mth.floor(sZ - genAmount), startZ);
                int genEndX = Math.max(Mth.floor(sX + genAmount), genBeginX);
                int genEndY = Math.max(Mth.floor(sY + genAmount), genBeginY);
                int genEndZ = Math.max(Mth.floor(sZ + genAmount), genBeginZ);

                for (int dx = genBeginX; dx <= genEndX; ++dx) {
                    double xAmount = (dx + 0.5D - sX) / genAmount;
                    if (xAmount * xAmount < 1.0D) {
                        for (int dy = genBeginY; dy <= genEndY; ++dy) {
                            double yAmount = (dy + 0.5D - sY) / genAmount;
                            if (xAmount * xAmount + yAmount * yAmount < 1.0D) {
                                for (int dz = genBeginZ; dz <= genEndZ; ++dz) {
                                    double zAmount = (dz + 0.5D - sZ) / genAmount;
                                    if (xAmount * xAmount + yAmount * yAmount + zAmount * zAmount < 1.0D) {
                                        int packedLocation = dx - startX + (dy - startY) * maxSizeXZ + (dz - startZ) * maxSizeXZ * maxSizeY;
                                        if (!generated.get(packedLocation)) {//do not overwrite
                                            generated.set(packedLocation);
                                            blockpos$mutable.set(dx, dy, dz);
                                            Block b = worldIn.getBlockState(blockpos$mutable).getBlock();
                                            if (config.target.test(worldIn.getBlockState(blockpos$mutable), random)) {
                                                if (WorldgenUtils.ORE_STONES.contains(b)) {
                                                    worldIn.setBlock(blockpos$mutable, config.state.getBlock().defaultBlockState().setValue(RankineOreBlock.TYPE, WorldgenUtils.ORE_STONES.indexOf(b)), 19);
                                                } else {
                                                    worldIn.setBlock(blockpos$mutable, config.state.getBlock().defaultBlockState(), 19);
                                                }
                                                ++totalGenerated;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return totalGenerated > 0;
    }
}