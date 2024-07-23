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

package com.teammoeg.frostedheart.world.civilization.orbit.spacecraft;

import java.util.Random;

import com.mojang.serialization.Codec;
import com.teammoeg.frostedheart.FHMain;

import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;

public class SpacecraftFeature extends Feature<NoFeatureConfig> {
    public SpacecraftFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      /*  List<Integer> listX = IntStream.rangeClosed(chunkpos.getXStart(), chunkpos.getXEnd()).boxed().collect(Collectors.toList());
        Collections.shuffle(listX, rand);
        List<Integer> listZ = IntStream.rangeClosed(chunkpos.getZStart(), chunkpos.getZEnd()).boxed().collect(Collectors.toList());
        Collections.shuffle(listZ, rand);*/

        //for(Integer integerX : listX) {
        // for(Integer integerZ : listZ) {
        BlockPos start = new BlockPos(pos.getX() - 9, pos.getY() - 1 /*generator.getNoiseHeightMinusOne(pos.getX(),pos.getZ(), Heightmap.Type.WORLD_SURFACE_WG)*/, pos.getZ() - 7);

        //if (reader.isAirBlock(blockpos$mutable) || reader.getBlockState(blockpos$mutable).getCollisionShapeUncached(reader, blockpos$mutable).isEmpty()) {
        Rotation rot = Rotation.getRandom(rand);

        PlacementSettings settings = (new PlacementSettings()).setRotationPivot(new BlockPos(/*9*/9, 2, 7/*8*/)).setRotation(rot).setMirror(Mirror.NONE).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        settings.keepLiquids = false;
        Template template = reader.getLevel().getStructureManager().get(new ResourceLocation(FHMain.MODID, "relic/spacecraft"));
        MutableBoundingBox boundingBox = template.getBoundingBox(settings, start);


        Vector3i vector3i = boundingBox.getCenter();

        //                        FHMain.LOGGER.debug( "spacecraft at " + (start.getX()) + " " + start.getY() + " " + (start.getZ())+" "+rot);
        return template.placeInWorld(reader, start, new BlockPos(vector3i.getX(), vector3i.getY(), vector3i.getZ()), settings, reader.getRandom(), 2);
        //}
        // }
        // }
    }
}