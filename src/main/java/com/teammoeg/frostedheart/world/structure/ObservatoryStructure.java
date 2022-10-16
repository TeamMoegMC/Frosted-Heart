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

package com.teammoeg.frostedheart.world.structure;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class ObservatoryStructure extends Structure<NoFeatureConfig> {
    public ObservatoryStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }


    public Structure.IStartFactory<NoFeatureConfig> getStartFactory() {
        return ObservatoryStructure.Start::new;
    }

    @Override
    public GenerationStage.Decoration getDecorationStage() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }


    @Override
    protected boolean func_230363_a_(ChunkGenerator generator, BiomeProvider biomeprovider, long seed, SharedSeedRandom random, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig p_230363_10_) {
        BlockPos centerOfChunk = new BlockPos(chunkX * 16, 0, chunkZ * 16);

        int landHeight = generator.getNoiseHeight(centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
        if (landHeight < 100 || landHeight > 200) return false;
        IBlockReader columnOfBlocks = generator.func_230348_a_(centerOfChunk.getX(), centerOfChunk.getZ());

        BlockState topBlock = columnOfBlocks.getBlockState(centerOfChunk.up(landHeight));

        return topBlock.getFluidState().isEmpty();

    }

    public static class Start extends StructureStart<NoFeatureConfig> {
        public Start(Structure<NoFeatureConfig> p_i225819_1_, int p_i225819_2_, int p_i225819_3_, MutableBoundingBox boundingBox, int p_i225819_5_, long p_i225819_6_) {
            super(p_i225819_1_, p_i225819_2_, p_i225819_3_, boundingBox, p_i225819_5_, p_i225819_6_);
        }

        @Override
        public void func_230364_a_(DynamicRegistries dynamic, ChunkGenerator generator, TemplateManager template, int chunkX, int chunkZ, Biome biome, NoFeatureConfig config) {

            int x = chunkX << 4;
            int z = chunkZ << 4;
            int surfaceY = generator.getNoiseHeightMinusOne(x, z, Heightmap.Type.WORLD_SURFACE_WG);

            BlockPos blockpos = new BlockPos(x, surfaceY, z);

            Rotation rotation = Rotation.randomRotation(this.rand);
            this.components.add(new ObservatoryPiece(template, blockpos, rotation));

            this.recalculateStructureSize();
//            FHMain.LOGGER.log(Level.DEBUG, "Observatory at " + (blockpos.getX()) + " " + blockpos.getY() + " " + (blockpos.getZ()));
        }
    }
}
