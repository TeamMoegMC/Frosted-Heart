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

package com.teammoeg.frostedheart.content.agriculture;

import com.teammoeg.frostedheart.content.climate.WorldClimate;
import com.teammoeg.frostedheart.content.climate.WorldTemperature;
import com.teammoeg.frostedheart.content.climate.WorldTemperature.TemperatureCheckResult;
import com.teammoeg.frostedheart.util.FHUtils;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

public class FHBerryBushBlock extends SweetBerryBushBlock {

    private int growTemperature;
    protected int growSpeed = 10;//0<growSpeed<50,growSpeed=50时具有原版浆果丛的生长速度

    public FHBerryBushBlock(int growTemperature, Properties properties) {
        super(properties);

        this.growTemperature = growTemperature;
    }//if you don't want to set growSpeed

    public FHBerryBushBlock(int growTemperature, Properties properties, int growSpeed) {
        super(properties);

        this.growTemperature = growTemperature;
        this.growSpeed = growSpeed;
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state) {
        return WorldTemperature.isSuitableForCrop(worldIn,pos,getGrowTemperature()+ WorldTemperature.BONEMEAL_TEMPERATURE).isSuitable();
    }


    public int getGrowTemperature() {
        return growTemperature;
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof LivingEntity && entityIn.getType() != EntityType.FOX && entityIn.getType() != EntityType.BEE) {
            entityIn.makeStuckInBlock(state, new Vec3(0.8F, 0.75D, 0.8F));
            if (!worldIn.isClientSide && state.getValue(AGE) > 0 && (entityIn.xOld != entityIn.getX() || entityIn.zOld != entityIn.getZ())) {
                double d0 = Math.abs(entityIn.getX() - entityIn.xOld);
                double d1 = Math.abs(entityIn.getZ() - entityIn.zOld);
                if (d0 >= (double) 0.003F || d1 >= (double) 0.003F) {
                
                    entityIn.hurt(worldIn.damageSources().sweetBerryBush(), 0.0F);//remove damage
                }
            }

        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        int i = state.getValue(AGE);
        TemperatureCheckResult res = WorldTemperature.isSuitableForCrop(worldIn,pos,getGrowTemperature(),getGrowTemperature()-5);
        if(!res.isValid())return;
        if(res.isRipedOff()) {
        	FHUtils.setToAirPreserveFluid(worldIn, pos);
        }else if (res.isDeadly()) {
            if (worldIn.getRandom().nextInt(3) == 0) {
                worldIn.setBlock(pos, this.defaultBlockState(), 2);
            }
        } else if(res.isSuitable())
        	if (i < 3 && worldIn.getRawBrightness(pos.above(), 0) >= 9 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt(50) < this.growSpeed)) {
	            worldIn.setBlock(pos, state.setValue(AGE, i + 1), 2);
	            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
        	}
    }

}
