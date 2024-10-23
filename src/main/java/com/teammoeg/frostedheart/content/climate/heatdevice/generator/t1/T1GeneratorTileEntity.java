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

package com.teammoeg.frostedheart.content.climate.heatdevice.generator.t1;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.simibubi.create.AllItems;
import com.teammoeg.frostedheart.FHMultiblocks;
import com.teammoeg.frostedheart.FHTileTypes;
import com.teammoeg.frostedheart.content.climate.heatdevice.generator.MasterGeneratorTileEntity;
import com.teammoeg.frostedheart.content.climate.heatdevice.generator.tool.GeneratorDriveHandler;
import com.teammoeg.frostedheart.util.client.ClientUtils;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.blocks.stone.AlloySmelterTileEntity;
import blusunrize.immersiveengineering.common.blocks.stone.BlastFurnaceTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public final class T1GeneratorTileEntity extends MasterGeneratorTileEntity<T1GeneratorTileEntity> {
    GeneratorDriveHandler generatorDriveHandler;
    private static BlockPos lastSupportPos;
    public T1GeneratorTileEntity() {
        super(FHMultiblocks.GENERATOR, FHTileTypes.GENERATOR_T1.get(), false);
        this.generatorDriveHandler = new GeneratorDriveHandler(level);
        lastSupportPos = new BlockPos(0,0,0);
    }
    public boolean isExistNeighborTileEntity() {
        Vec3i vec = this.multiblockInstance.getSize(level);
        int xLow = -1, xHigh = vec.getX(), yLow = 0, yHigh = vec.getY(), zLow = -1, zHigh = vec.getZ();
        int blastBlockCount = 0, alloySmelterCount = 0;
        for (int x = xLow; x <= xHigh; ++x)
            for (int y = yLow; y < yHigh; ++y)
                for (int z = zLow; z <= zHigh; ++z) {
                    BlockPos actualPos = getBlockPosForPos(new BlockPos(x, y, z));
                    // Enum a seamless NoUpandDown hollow cube
                    if ( ( (z>zLow && z<zHigh) && ((x==xLow) || (x==xHigh)) ) || ((z==zLow || z==zHigh) && (x>xLow && x<xHigh)) ) {
                        BlockEntity te = Utils.getExistingTileEntity(level, actualPos);
                        if (te instanceof BlastFurnaceTileEntity) {
                            if (++blastBlockCount == 9) {
                            	BlastFurnaceTileEntity master=((BlastFurnaceTileEntity) te).master();
                                lastSupportPos = master.getBlockPos();
                                return true;
                            }
                        }
                        if (te instanceof AlloySmelterTileEntity) {
                            if (++alloySmelterCount == 4) {
                                lastSupportPos = actualPos;
                                return true;
                            }
                        }
                    }
                }
        return false;
    }
    @Override
    protected void callBlockConsumerWithTypeCheck(Consumer<T1GeneratorTileEntity> consumer, BlockEntity te) {
        if (te instanceof T1GeneratorTileEntity)
            consumer.accept((T1GeneratorTileEntity) te);
    }



    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
       
    }

    @Override
    protected void tickEffects(boolean isActive) {
        if (isActive) {
            BlockPos blockpos = this.getBlockPos();
            Random random = level.random;
            if (random.nextFloat() < 0.2F) {
                //for (int i = 0; i < random.nextInt(2) + 2; ++i) {
                ClientUtils.spawnSmokeParticles(level, blockpos.relative(Direction.UP, 1));
                ClientUtils.spawnSmokeParticles(level, blockpos);
                ClientUtils.spawnFireParticles(level, blockpos);
                //}
            }
        }
    }
    @Override
    protected void tickDrives(boolean isActive) {
        if (isActive) {
            if (isExistNeighborTileEntity()) {
                this.generatorDriveHandler.checkExistOreAndUpdate(lastSupportPos);
            }
        }
    }
    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        
    }
	@Override
	public IETemplateMultiblock getNextLevelMultiblock() {
		return FHMultiblocks.GENERATOR_T2;
	}
}