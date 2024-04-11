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

package com.teammoeg.frostedheart.content.trade;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkHooks;

public class TradeHandler {
    public static void openTradeScreen(ServerPlayerEntity spe, FHVillagerData vd) {
        vd.update(spe.getServerWorld(), spe);
        NetworkHooks.openGui(spe, vd, e -> {
            e.writeVarInt(vd.parent.getEntityId());
            CompoundNBT tag = new CompoundNBT();
            e.writeCompoundTag(vd.serializeForSend(tag));
            tag = new CompoundNBT();
            e.writeCompoundTag(vd.getRelationDataForRead(spe).serialize(tag));
            vd.getRelationShip(spe).write(e);
        });
    }

}
