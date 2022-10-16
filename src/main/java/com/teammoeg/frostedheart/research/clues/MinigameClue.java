/*
 * Copyright (c) 2022 TeamMoeg
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

package com.teammoeg.frostedheart.research.clues;

import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

public class MinigameClue extends CustomClue {
    private int level = 0;

    public MinigameClue(float contribution) {
        super("@clue." + FHMain.MODID + ".minigame", contribution);
    }

    public MinigameClue(JsonObject jo) {
        super(jo);
        setLevel(jo.get("level").getAsInt());
    }

    public MinigameClue(PacketBuffer pb) {
        super(pb);
        setLevel(pb.readVarInt());
    }

    MinigameClue() {
        super();
    }

    @Override
    public ITextComponent getName() {
        if (name != null && !name.isEmpty())
            return super.getName();
        return GuiUtils.translate("clue." + FHMain.MODID + ".minigame");
    }

    @Override
    public String getId() {
        return "game";
    }


    @Override
    public JsonObject serialize() {
        JsonObject jo = super.serialize();
        jo.addProperty("level", getLevel());
        return jo;
    }

    @Override
    public void write(PacketBuffer buffer) {
        super.write(buffer);
        buffer.writeVarInt(getLevel());

    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.min(Math.max(level, 0), 3);
    }

}
