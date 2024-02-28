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

package com.teammoeg.frostedheart.content.steamenergy.capabilities;

import com.teammoeg.frostedheart.FHCapabilities;
import com.teammoeg.frostedheart.content.steamenergy.HeatEnergyNetwork;
import com.teammoeg.frostedheart.content.steamenergy.INetworkConsumer;
import com.teammoeg.frostedheart.util.FHUtils;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public class HeatCapabilities {
    /**
     * Check can recive connect from direction.<br>
     *
     * @param to the to<br>
     * @return true, if can recive connect from direction
     */
    public static boolean canConnectAt(IWorld world,BlockPos pos,Direction to) {
    	return FHUtils.getExistingTileEntity(world, pos,INetworkConsumer.class)!=null||FHUtils.getCapability(world, pos, to, FHCapabilities.HEAT_EP.capability())!=null;
    	
    };


    /**
     * Connects to endpoint.<br>
     *
     * @param d        the direction connection from<br>
     * @param distance the distance<br>
     * @return true, if connected
     */
    public static boolean connect(HeatEnergyNetwork network,World w,BlockPos pos,Direction d, int distance) {
    	TileEntity te=FHUtils.getExistingTileEntity(w, pos);
    	if(te!=null) {
    		LazyOptional<HeatEndpoint> ep=te.getCapability(FHCapabilities.HEAT_EP.capability(), d);
    		if(ep.isPresent())
        		return ep.orElse(null).reciveConnection(w, pos, network, d, distance);
    		if(te instanceof INetworkConsumer)
    			return ((INetworkConsumer) te).tryConnectAt(network, d, distance);
    	}
    	return false;
    }


}
