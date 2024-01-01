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

package com.teammoeg.frostedheart.content.generator;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import com.teammoeg.frostedheart.base.block.FHBlockInterfaces;
import com.teammoeg.frostedheart.base.block.ManagedOwnerTile;
import com.teammoeg.frostedheart.climate.chunkdata.ChunkData;
import com.teammoeg.frostedheart.research.api.ResearchDataAPI;
import com.teammoeg.frostedheart.research.data.ResearchVariant;
import com.teammoeg.frostedheart.research.data.TeamResearchData;
import com.teammoeg.frostedheart.util.mixin.IOwnerTile;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import dev.ftb.mods.ftbteams.FTBTeams;
import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.FTBTeamsCommon;
import dev.ftb.mods.ftbteams.FTBTeamsForge;
import dev.ftb.mods.ftbteams.data.Team;
import dev.ftb.mods.ftbteams.data.TeamManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public abstract class AbstractGenerator<T extends AbstractGenerator<T>> extends MultiblockPartTileEntity<T> implements FHBlockInterfaces.IActiveState {

    public int temperatureLevel;
    public int rangeLevel;
    public int overdriveBoost;
    private boolean initialized;
    boolean isUserOperated;
    boolean isWorking;
    boolean isOverdrive;
    boolean isActualOverdrive;
    boolean isDirty;//mark if temperature change required
    boolean isLocked = false;
    private int checkInterval = 0;

    public AbstractGenerator(IETemplateMultiblock multiblockInstance, BlockEntityType<T> type, boolean hasRSControl) {
        super(multiblockInstance, type, hasRSControl);
    }

    public int getActualRange() {
        return (int) (8 + (getRangeLevel()) * 4);
    }
    public int getUpperBound() {
    	return Mth.ceil (getRangeLevel()*4);
    }
    public int getLowerBound() {
    	return Mth.ceil(getRangeLevel());
    }
    public int getActualTemp() {
        return (int) (getTemperatureLevel() * 10);
    }

    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        isWorking = nbt.getBoolean("isWorking");
        isOverdrive = nbt.getBoolean("isOverdrive");
        isActualOverdrive = nbt.getBoolean("Overdriven");
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.putBoolean("isWorking", isWorking);
        nbt.putBoolean("isOverdrive", isOverdrive);
        nbt.putBoolean("Overdriven", isActualOverdrive);
    }

    @Override
    public void disassemble() {
        ChunkData.removeTempAdjust(level, getBlockPos());
        if (shouldUnique() && master() != null)
            master().unregist();
        super.disassemble();
    }

    public void unregist() {
    	getTeamData().ifPresent(t->{
        if (!t.hasVariant(ResearchVariant.GENERATOR_LOCATION)) return;
        long pos = t.getVariantLong(ResearchVariant.GENERATOR_LOCATION);
        BlockPos bp = BlockPos.of(pos);
        if (bp.equals(this.worldPosition))
            t.removeVariant(ResearchVariant.GENERATOR_LOCATION);
    	});
    }

    public void regist() {
    	getTeamData().ifPresent(t->t.putVariantLong(ResearchVariant.GENERATOR_LOCATION,master().worldPosition.asLong()));
    }

    public void setOwner(UUID owner) {
        forEachBlock(s -> IOwnerTile.setOwner(s, owner));
    }
    protected Optional<Team> getTeam() {
    	UUID owner=getOwner();
    	if(owner!=null)
    		return Optional.ofNullable(TeamManager.INSTANCE.getTeamByID(owner));
    	return Optional.empty();
    }
    protected Optional<TeamResearchData> getTeamData() {
    	UUID owner=getOwner();
    	if(owner!=null)
    		return Optional.of(ResearchDataAPI.getData(owner));
    	return Optional.empty();
    }
    //
    public boolean shouldWork() {
    	return getTeamData().map(t->{
	        if(!t.building.has(super.multiblockInstance))return false;
	        if (!t.hasVariant(ResearchVariant.GENERATOR_LOCATION)) {
	            t.putVariantLong(ResearchVariant.GENERATOR_LOCATION, master().worldPosition.asLong());
	            return true;
	        }
	        long pos = t.getVariantLong(ResearchVariant.GENERATOR_LOCATION);
	        BlockPos bp = BlockPos.of(pos);
	        if (bp.equals(this.worldPosition))
	            return true;
	        return false;
    	}).orElse(false);
        
    }
    protected void tickControls() {}
    protected abstract void onShutDown();

    protected abstract void tickFuel();

    protected abstract void tickEffects(boolean isActive);

    public abstract boolean shouldUnique();

    @Override
    public void tick() {
        checkForNeedlessTicking();
        if(isDummy())return;
        // spawn smoke particle
        if (level != null && level.isClientSide && formed) {
            tickEffects(getIsActive());
        }
        
        tickControls();
        //user set shutdown
        if (isUserOperated())
            if (!level.isClientSide && formed && !isWorking()) {
                setAllActive(false);
                onShutDown();
                ChunkData.removeTempAdjust(level, getBlockPos());
            }
        
        if (!level.isClientSide && formed) {
        	if(isWorking()) {
	            if (shouldUnique()) {
	                if (checkInterval <= 0) {
	                    if (getOwner() != null)
	                        checkInterval = 10;
	                    isLocked = !shouldWork();
	                } else checkInterval--;
	            }
	            final boolean activeBeforeTick = getIsActive();
	            if (!isLocked)
	                tickFuel();
	            else
	                this.setActive(false);
	            // set activity status
	            final boolean activeAfterTick = getIsActive();
	            if (activeBeforeTick != activeAfterTick) {
	                this.setChanged();
	                if (activeAfterTick) {
	                    ChunkData.addPillarTempAdjust(level, getBlockPos(), getActualRange(), getUpperBound(),getLowerBound(),getActualTemp());
	                } else {
	                    ChunkData.removeTempAdjust(level, getBlockPos());
	                }
	                setAllActive(activeAfterTick);
	            } else if (activeAfterTick) {
	                if (isChanged() || !initialized) {
	                    initialized = true;
	                    markChanged(false);
	                    ChunkData.addPillarTempAdjust(level, getBlockPos(), getActualRange(), getUpperBound(),getLowerBound(), getActualTemp());
	                }
	            }
        	}else
        		shutdownTick();
        }

    }
    public void shutdownTick() {}
    public void setWorking(boolean working) {
        if (master() != null) {
            master().isWorking = working;
            setUserOperated(true);
        }
    }

    public boolean isWorking() {
        if (master() != null)
            return master().isWorking;
        return false;
    }

    public boolean isOverdrive() {
        if (master() != null)
            return master().isOverdrive;
        return false;
    }

    public void markChanged(boolean dirty) {
        if (master() != null)
            master().isDirty = dirty;
    }

    public boolean isChanged() {
        if (master() != null)
            return master().isDirty;
        return false;
    }

    public void setOverdrive(boolean overdrive) {
        if (master() != null) {
            setUserOperated(true);
            master().isOverdrive = overdrive;
        }
    }

    public boolean isActualOverdrive() {
        if (master() != null)
            return master().isActualOverdrive;
        return false;
    }

    public void setActualOverdrive(boolean isActualOverdrive) {
        if (master() != null) {
            markChanged(true);
            master().isActualOverdrive = isActualOverdrive;
        }
    }

    public float getTemperatureLevel() {
        if (master() != null)
            return master().temperatureLevel * (isActualOverdrive() ? master().overdriveBoost : 1);
        return 1;
    }

    public float getRangeLevel() {
        if (master() != null)
            return master().rangeLevel;
        return 1;
    }

    public boolean isUserOperated() {
        if (master() != null)
            return master().isUserOperated;
        return false;
    }

    public void setUserOperated(boolean isUserOperated) {
        if (master() != null)
            master().isUserOperated = isUserOperated;
    }

    protected void setAllActive(boolean state) {
        forEachBlock(s -> s.setActive(state));
    }

    public abstract void forEachBlock(Consumer<T> consumer);

    UUID getOwner() {
        return IOwnerTile.getOwner(this);
    }
}
