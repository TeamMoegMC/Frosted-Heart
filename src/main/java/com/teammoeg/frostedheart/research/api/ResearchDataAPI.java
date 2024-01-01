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

package com.teammoeg.frostedheart.research.api;

import java.util.UUID;

import com.teammoeg.frostedheart.research.data.FHResearchDataManager;
import com.teammoeg.frostedheart.research.data.ResearchVariant;
import com.teammoeg.frostedheart.research.data.TeamResearchData;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;

public class ResearchDataAPI {

    private ResearchDataAPI() {
    }

    public static TeamResearchData getData(Player id) {
    	if(id instanceof ServerPlayer)
    		return FHResearchDataManager.INSTANCE.getData(FTBTeamsAPI.getPlayerTeam((ServerPlayer)id).getId());
    	return TeamResearchData.getClientInstance();

    }

    public static boolean isResearchComplete(Player id,String research) {
    	if(id instanceof ServerPlayer)
    		return FHResearchDataManager.INSTANCE.getData(FTBTeamsAPI.getPlayerTeam((ServerPlayer)id).getId()).getData(research).isCompleted();
    	return TeamResearchData.getClientInstance().getData(research).isCompleted();
    }
    public static TeamResearchData getData(UUID id) {
        return FHResearchDataManager.INSTANCE.getData(id);

    }

    public static CompoundTag getVariants(Player id) {
    	if(id instanceof ServerPlayer)
        return FHResearchDataManager.INSTANCE.getData(FTBTeamsAPI.getPlayerTeam((ServerPlayer)id).getId()).getVariants();
    	return TeamResearchData.getClientInstance().getVariants();

    }
    
    public static CompoundTag getVariants(UUID id) {
        return FHResearchDataManager.INSTANCE.getData(id).getVariants();

    }
    public static long getVariantLong(Player id,ResearchVariant name) {
    	if(id instanceof ServerPlayer)
        return getVariantLong(FTBTeamsAPI.getPlayerTeam((ServerPlayer)id).getId(),name);
    	return TeamResearchData.getClientInstance().getVariants().getLong(name.getToken());

    }
    public static long getVariantLong(UUID id,ResearchVariant name) {
        return getVariants(id).getLong(name.getToken());
    }
    public static double getVariantDouble(Player id,ResearchVariant name) {
    	if(id instanceof ServerPlayer)
        return getVariantDouble(FTBTeamsAPI.getPlayerTeam((ServerPlayer)id).getId(),name);
    	return TeamResearchData.getClientInstance().getVariants().getDouble(name.getToken());

    }
    public static double getVariantDouble(UUID id,ResearchVariant name) {
        return getVariants(id).getDouble(name.getToken());
    }
    public static void putVariantLong(ServerPlayer id,ResearchVariant name,long val) {
        putVariantLong(FTBTeamsAPI.getPlayerTeam(id).getId(),name,val);
    }
    public static void putVariantLong(UUID id,ResearchVariant name,long val) {
        getVariants(id).putLong(name.getToken(),val);
    }
    public static void putVariantDouble(ServerPlayer id,ResearchVariant name,double val) {
    	putVariantDouble(FTBTeamsAPI.getPlayerTeam(id).getId(),name,val);
    }
    public static void putVariantDouble(UUID id,ResearchVariant name,double val) {
        getVariants(id).putDouble(name.getToken(),val);
    }
}
