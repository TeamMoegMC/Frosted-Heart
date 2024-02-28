/*
 * Copyright (c) 2024 TeamMoeg
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

package com.teammoeg.frostedheart.content.steamenergy.steamcore;

import java.util.Objects;

import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.teammoeg.frostedheart.FHCapabilities;
import com.teammoeg.frostedheart.FHConfig;
import com.teammoeg.frostedheart.base.block.FHBlockInterfaces;
import com.teammoeg.frostedheart.content.steamenergy.capabilities.HeatConsumerEndpoint;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class SteamCoreTileEntity extends GeneratingKineticTileEntity implements
        ITickableTileEntity, IHaveGoggleInformation,
        FHBlockInterfaces.IActiveState{
    public SteamCoreTileEntity(TileEntityType<?> type) {
        super(type);
        this.setLazyTickRate(20);
    }

    HeatConsumerEndpoint network = new HeatConsumerEndpoint(FHConfig.COMMON.steamCoreMaxPower.get().floatValue(),FHConfig.COMMON.steamCorePowerIntake.get().floatValue());
    
    public float getGeneratedSpeed(){
        float speed = FHConfig.COMMON.steamCoreGeneratedSpeed.get().floatValue();
        if(getIsActive()) return speed;
        return 0f;
    }

    public float calculateAddedStressCapacity() {
        if(getIsActive()) return FHConfig.COMMON.steamCoreCapacity.get().floatValue();
        return 0f;
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isRemote) {
            if(network.tryDrainHeat(FHConfig.COMMON.steamCorePowerIntake.get().floatValue())){
                this.setActive(true);
                if(this.getSpeed() == 0f){
                    this.updateGeneratedRotation();
                }
                markDirty();
            }else {
                this.setActive(false);
                this.updateGeneratedRotation();
            }
        }
    }

    public void lazyTick() {
        super.lazyTick();

    }


    LazyOptional<HeatConsumerEndpoint> heatcap=LazyOptional.of(()->network);
    @Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap==FHCapabilities.HEAT_EP.capability()&&side==this.getBlockState().get(BlockStateProperties.FACING).getOpposite()) {
			return heatcap.cast();
		}
		return super.getCapability(cap, side);
	}

	public Direction getDirection() {
        return this.getBlockState().get(BlockStateProperties.FACING);
    }

    @Override
    protected void fromTag(BlockState state, CompoundNBT tag, boolean client) {
        super.fromTag(state, tag, client);
        network.load(tag);
    }

    @Override
    protected void write(CompoundNBT tag, boolean client) {
        super.write(tag, client);
        network.save(tag);
    }

    @Override
    public BlockState getState() {
        return this.getBlockState();
    }

    @Override
    public void setState(BlockState blockState) {
        if (this.getWorldNonnull().getBlockState(this.pos) == this.getState()) {
            this.getWorldNonnull().setBlockState(this.pos, blockState);
        }
    }

    public World getWorldNonnull() {
        return (World) Objects.requireNonNull(super.getWorld());
    }
}
