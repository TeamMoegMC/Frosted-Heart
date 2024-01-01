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

package com.teammoeg.frostedheart.content.steamenergy.radiator;

import java.util.function.Consumer;

import com.teammoeg.frostedheart.FHMultiblocks;
import com.teammoeg.frostedheart.FHTileTypes;
import com.teammoeg.frostedheart.base.block.FHBlockInterfaces;
import com.teammoeg.frostedheart.client.util.ClientUtils;
import com.teammoeg.frostedheart.content.generator.AbstractGenerator;
import com.teammoeg.frostedheart.content.steamenergy.INetworkConsumer;
import com.teammoeg.frostedheart.content.steamenergy.SteamNetworkConsumer;
import com.teammoeg.frostedheart.content.steamenergy.SteamNetworkHolder;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IInteractionObjectIE;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class RadiatorTileEntity extends AbstractGenerator<RadiatorTileEntity> implements
        INetworkConsumer, IEBlockInterfaces.IInteractionObjectIE, IEBlockInterfaces.IProcessTile, FHBlockInterfaces.IActiveState, TickableBlockEntity {
    public int process = 0;
    public int processMax = 0;
    public float tempLevelLast;
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    public RadiatorTileEntity() {
        super(FHMultiblocks.RADIATOR, FHTileTypes.RADIATOR.get(), false);
    }

    SteamNetworkConsumer network = new SteamNetworkConsumer(3000,24);


    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        network.load(nbt);
        process = nbt.getInt("process");
        processMax = nbt.getInt("processMax");
        tempLevelLast = nbt.getFloat("temp");
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        network.save(nbt);
        nbt.putInt("process", process);
        nbt.putInt("processMax", processMax);
        nbt.putFloat("temp", tempLevelLast);
    }

    @Override
    public boolean connect(Direction to, int dist) {
        return network.reciveConnection(level, worldPosition, to, dist);
    }


    @Override
    public IInteractionObjectIE getGuiMaster() {
        return this;
    }

    @Override
    public boolean canUseGui(Player player) {
        return false;
    }


    @Override
    public int[] getCurrentProcessesStep() {
        return new int[]{processMax - process};
    }

    @Override
    public int[] getCurrentProcessesMax() {
        return new int[]{processMax};
    }

    @Override
    public boolean canConnectAt(Direction to) {
        return this.offsetToMaster.getY() == 0;
    }

    @Override
    protected void onShutDown() {
    }

    @Override
    protected void tickFuel() {
        network.tick();
        if (process > 0) {
            if (network.isValid())
                process -= network.getTemperatureLevel();
            else
                process -= tempLevelLast;
        } else if (network.isValid() && network.tryDrainHeat(4 * 160 * network.getTemperatureLevel())) {
            process = (int) (160 * network.getTemperatureLevel());
            processMax = (int) (160 * network.getTemperatureLevel());
            this.setActive(true);
        } else {
            this.setActive(false);
        }
        if (network.isValid() && tempLevelLast != network.getTemperatureLevel()) {
            tempLevelLast = network.getTemperatureLevel();
            this.markChanged(true);
        }
    }

    @Override
    public boolean isWorking() {
        return true;
    }

    @Override
    public int getActualRange() {
        return 8;
    }

    @Override
    public int getActualTemp() {
        return (int) (tempLevelLast * 10);
    }

    @Override
    protected void tickEffects(boolean isActive) {
        if (level != null && level.isClientSide && isActive && level.random.nextFloat() < 0.2) {
            ClientUtils.spawnSteamParticles(level, this.getBlockPos());
        }
    }

    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
        return new IFluidTank[0];
    }

    @Override
    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource) {
        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int iTank, Direction side) {
        return false;
    }

    @Override
    public void forEachBlock(Consumer<RadiatorTileEntity> consumer) {
        for (int y = 0; y < 3; ++y) {
            BlockPos actualPos = getBlockPosForPos(new BlockPos(0, y, 0));
            BlockEntity te = Utils.getExistingTileEntity(level, actualPos);
            if (te instanceof RadiatorTileEntity)
                consumer.accept((RadiatorTileEntity) te);
        }
    }

    @Override
    public boolean shouldUnique() {
        return false;
    }

    @Override
    public SteamNetworkHolder getHolder() {
        return network;
    }

	@Override
	public int getUpperBound() {
		return 4;
	}

	@Override
	public int getLowerBound() {
		return 1;
	}
}
