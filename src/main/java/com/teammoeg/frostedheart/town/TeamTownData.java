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

package com.teammoeg.frostedheart.town;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import com.teammoeg.frostedheart.research.TeamCapability;
import com.teammoeg.frostedheart.research.data.TeamResearchData;

import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Town data for a whole team.
 */
public class TeamTownData implements INBTSerializable<CompoundNBT>{
	public static final TeamCapability<TeamTownData> CAPABILITY=new TeamCapability<>("town",TeamTownData::new);
    /**
     * Resource generated from resident
     */
    Map<TownResourceType, Integer> resources = new EnumMap<>(TownResourceType.class);
    /**
     * Resource provided by player
     */
    Map<TownResourceType, Integer> backupResources = new EnumMap<>(TownResourceType.class);
    Map<BlockPos, TownWorkerData> blocks = new LinkedHashMap<>();
    TeamResearchData team;

    public TeamTownData(TeamResearchData team) {
        super();
        this.team = team;
    }


    public CompoundNBT getTownBlockData(BlockPos pos) {
        TownWorkerData twd = blocks.get(pos);
        if (twd == null)
            return null;
        return twd.getWorkData();
    }

    public void registerTownBlock(BlockPos pos, ITownBlockTE tile) {
        TownWorkerData data = blocks.computeIfAbsent(pos, TownWorkerData::new);
        data.fromBlock(tile);
    }

    public void removeTownBlock(BlockPos pos) {
        blocks.remove(pos);
    }
	@Override
	public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (TownWorkerData v : blocks.values()) {
            list.add(v.serialize());
        }
        nbt.put("blocks", list);
        
        CompoundNBT list2 = new CompoundNBT();
        for (Entry<TownResourceType, Integer> v : resources.entrySet()) {
            if (v.getValue() != null && v.getValue() != 0)
                list2.putInt(v.getKey().getKey(), v.getValue());

        }
        nbt.put("resource", list2);
        CompoundNBT list3 = new CompoundNBT();
        for (Entry<TownResourceType, Integer> v : backupResources.entrySet()) {
            if (v.getValue() != null && v.getValue() != 0)
                list3.putInt(v.getKey().getKey(), v.getValue());
        }
        nbt.put("backupResource", list2);
        return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT data) {
        for (INBT i : data.getList("blocks", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT nbt = (CompoundNBT) i;
            TownWorkerData t = new TownWorkerData(nbt);
            blocks.put(t.getPos(), t);
        }
        CompoundNBT rec = data.getCompound("resource");
        for (String i : rec.keySet()) {
            resources.put(TownResourceType.from(i), rec.getInt(i));
        }
	}

    /**
     * This tick only works per 20 tick.
     */
    public void tick(ServerWorld world) {
        PriorityQueue<TownWorkerData> pq = new PriorityQueue<TownWorkerData>(Comparator.comparingLong(TownWorkerData::getPriority).reversed());
        blocks.values().removeIf(v -> {
            BlockPos pos = v.getPos();
            v.loaded = false;
            if (world.isBlockLoaded(pos)) {
                v.loaded = true;
                BlockState bs = world.getBlockState(pos);
                TileEntity te = Utils.getExistingTileEntity(world, pos);
                TownWorkerType twt = v.getType();
                if (twt.getBlock() != bs.getBlock() || te == null || !(te instanceof ITownBlockTE) || !((ITownBlockTE) te).isWorkValid()) {
                    return true;
                }
            }
            return false;
        });
        for (TownWorkerData v : blocks.values()) {
            pq.add(v);
        }
        PlayerTown itt = new PlayerTown(this);
        for (TownWorkerData t : pq) {
            t.firstWork(itt);
        }
        for (TownWorkerData t : pq) {
            t.beforeWork(itt);
        }
        for (TownWorkerData t : pq) {
            t.work(itt);
        }
        for (TownWorkerData t : pq) {
            t.afterWork(itt);
        }
        for (TownWorkerData t : pq) {
            t.lastWork(itt);
        }
        for (TownWorkerData t : pq) {
            t.setData(world);
        }
        itt.finishWork();
    }


}
