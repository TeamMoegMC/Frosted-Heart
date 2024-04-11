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

package com.teammoeg.frostedheart.content.research.network;

import java.util.function.Supplier;

import com.teammoeg.frostedheart.base.network.FHMessage;
import com.teammoeg.frostedheart.content.research.FHResearch;
import com.teammoeg.frostedheart.content.research.api.ResearchDataAPI;
import com.teammoeg.frostedheart.content.research.data.TeamResearchData;
import com.teammoeg.frostedheart.content.research.research.Research;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class FHEffectTriggerPacket implements FHMessage {
    private final int researchID;

    public FHEffectTriggerPacket(PacketBuffer buffer) {
        researchID = buffer.readVarInt();

    }

    public FHEffectTriggerPacket(Research r) {
        this.researchID = FHResearch.researches.getIntId(r);
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeVarInt(researchID);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {
            Research r = FHResearch.researches.getById(researchID);
            TeamResearchData trd = ResearchDataAPI.getData(context.get().getSender());
            ServerPlayerEntity spe = context.get().getSender();
            if (trd.getData(r).isCompleted()) {
            	
                r.grantEffects(trd, spe);
                r.sendProgressPacket(trd.getHolder());
            }
        });
        context.get().setPacketHandled(true);
    }
}
