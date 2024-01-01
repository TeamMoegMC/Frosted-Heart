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

package com.teammoeg.frostedheart.climate.data;

import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.climate.data.FHDataManager.FHDataType;

import net.minecraft.network.FriendlyByteBuf;

public class DataEntry {
    FHDataType type;
    String data;

    public DataEntry(FHDataType type, JsonObject data) {
        this.type = type;
        this.data = data.toString();
    }

    public DataEntry(FriendlyByteBuf buffer) {
        this.type = FHDataType.values()[buffer.readVarInt()];
        this.data = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(type.ordinal());
        buffer.writeUtf(data);
    }
}
