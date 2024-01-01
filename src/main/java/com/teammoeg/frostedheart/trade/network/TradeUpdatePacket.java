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

package com.teammoeg.frostedheart.trade.network;

import java.util.function.Supplier;

import com.teammoeg.frostedheart.client.util.ClientUtils;
import com.teammoeg.frostedheart.trade.RelationList;
import com.teammoeg.frostedheart.trade.gui.TradeContainer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class TradeUpdatePacket {
	CompoundTag data;
	CompoundTag player;
	RelationList relations;
	boolean isReset;



	public TradeUpdatePacket(CompoundTag data, CompoundTag player, RelationList relations, boolean isReset) {
		super();
		this.data = data;
		this.player = player;
		this.relations = relations;
		this.isReset = isReset;
	}

	public TradeUpdatePacket(FriendlyByteBuf buffer) {
		data=buffer.readNbt();
		player=buffer.readNbt();
		relations=new RelationList();
		relations.read(buffer);
		isReset=buffer.readBoolean();
    }

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt(data);
		buffer.writeNbt(player);
		relations.write(buffer);
		buffer.writeBoolean(isReset);
	}
	
	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			Player player = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> ClientUtils::getPlayer);
			AbstractContainerMenu cont=player.containerMenu;
			if(cont instanceof TradeContainer) {
				TradeContainer trade=(TradeContainer) cont;
				trade.update(data,this.player,relations,isReset);
			}
		});
		context.get().setPacketHandled(true);
	}
}
