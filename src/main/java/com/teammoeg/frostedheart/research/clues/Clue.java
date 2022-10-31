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

import java.util.UUID;
import java.util.function.Supplier;

import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.network.PacketHandler;
import com.teammoeg.frostedheart.research.AutoIDItem;
import com.teammoeg.frostedheart.research.FHResearch;
import com.teammoeg.frostedheart.research.Research;
import com.teammoeg.frostedheart.research.data.FHResearchDataManager;
import com.teammoeg.frostedheart.research.data.TeamResearchData;
import com.teammoeg.frostedheart.research.gui.FHTextUtil;
import com.teammoeg.frostedheart.research.network.FHClueProgressSyncPacket;
import com.teammoeg.frostedheart.util.Writeable;

import dev.ftb.mods.ftbteams.data.Team;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * "Clue" for researches, contributes completion percentage for some
 * researches.6
 * Clue can be trigger if any research is researchable(finished item commit)
 */
public abstract class Clue extends AutoIDItem implements Writeable {
    float contribution;// percentage, range (0,1]
    String name = "";
    String desc = "";
    String hint = "";
    String nonce;
    boolean showContribute;
    public Supplier<Research> parent;
    boolean required=false;
    public float getResearchContribution() {
        return contribution;
    }

    public Clue(String name, String desc, String hint, float contribution) {
        super();
        this.contribution = contribution;
        this.name = name;
        this.desc = desc;
        this.hint = hint;
        this.nonce = Long.toHexString(UUID.randomUUID().getMostSignificantBits());
    }

    public Clue(String name, float contribution) {
        this(name, "", "", contribution);
    }

    public Clue(JsonObject jo) {
        super();
        if (jo.has("name"))
            this.name = jo.get("name").getAsString();
        if (jo.has("desc"))
            this.desc = jo.get("desc").getAsString();
        if (jo.has("hint"))
            this.hint = jo.get("hint").getAsString();
        this.contribution = jo.get("value").getAsFloat();
        this.nonce = jo.get("id").getAsString();
        if(jo.has("required"))
        	this.required=jo.get("required").getAsBoolean();
    }

    public Clue(PacketBuffer pb) {
        super();
        this.name = pb.readString();
        this.desc = pb.readString();
        this.hint = pb.readString();
        this.contribution = pb.readFloat();
        this.nonce = pb.readString();
        this.required=pb.readBoolean();
    }

    public Clue() {
        this.nonce = Long.toHexString(UUID.randomUUID().getMostSignificantBits());
    }

    public void setCompleted(Team team, boolean trig) {
        FHResearchDataManager.INSTANCE.getData(team.getId()).setClueTriggered(this, trig);
        if (trig)
            end(team);
        else
            start(team);
        this.sendProgressPacket(team);
    }

    @OnlyIn(Dist.CLIENT)
    public void setCompleted(boolean trig) {
        TeamResearchData.getClientInstance().setClueTriggered(this, trig);
        ;
    }

    public boolean isCompleted(TeamResearchData data) {
        return data.isClueTriggered(this);
    }

    public boolean isCompleted(Team team) {
        return FHResearchDataManager.INSTANCE.getData(team.getId()).isClueTriggered(this);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isCompleted() {
        return TeamResearchData.getClientInstance().isClueTriggered(this);
    }

    /**
     * send progress packet to client
     * should not called manually
     */
    public void sendProgressPacket(Team team) {
        FHClueProgressSyncPacket packet = new FHClueProgressSyncPacket(team.getId(), this);
        for (ServerPlayerEntity spe : team.getOnlineMembers())
            PacketHandler.send(PacketDistributor.PLAYER.with(() -> spe), packet);
    }

    /**
     * called when researches load finish
     */
    public abstract void init();

    /**
     * called when this clue's research has started
     */
    public abstract void start(Team team);

    /**
     * Stop detection when clue is completed
     */
    public abstract void end(Team team);

    public ITextComponent getName() {
        return FHTextUtil.get(name, "clue", () -> this.getLId() + ".name");
    }

    public ITextComponent getDescription() {
        return FHTextUtil.getOptional(desc, "clue", () -> this.getLId() + ".desc");
    }

    public ITextComponent getHint() {
        return FHTextUtil.getOptional(hint, "clue", () -> this.getLId() + ".hint");
    }

    @Override
    public JsonObject serialize() {
        JsonObject jo = new JsonObject();
        jo.addProperty("type", this.getId());
        if (!name.isEmpty())
            jo.addProperty("name", name);
        if (!desc.isEmpty())
            jo.addProperty("desc", desc);
        if (!hint.isEmpty())
            jo.addProperty("hint", hint);
        jo.addProperty("value", contribution);
        jo.addProperty("id", nonce);
        if(required)
        	jo.addProperty("required", required);
        return jo;
    }

    public abstract String getId();

    @Override
    public void write(PacketBuffer buffer) {
        Clues.writeId(this, buffer);
        buffer.writeString(name);
        buffer.writeString(desc);
        buffer.writeString(hint);
        buffer.writeFloat(contribution);
        buffer.writeString(nonce);
        buffer.writeBoolean(required);
    }


    @Override
    public final String getType() {
        return "clue";
    }

    @Override
    public String getNonce() {
        return nonce;
    }

    private void deleteInTree() {
        FHResearchDataManager.INSTANCE.getAllData().forEach(t -> t.getTeam().ifPresent(this::end));
    }

    public void edit() {
        deleteInTree();
    }

    public void deleteSelf() {
        deleteInTree();
        FHResearch.clues.remove(this);
    }

    public void delete() {
        deleteSelf();
        if (parent != null) {
            Research r = parent.get();
            if (r != null) {
                r.getClues().remove(this);
            }
        }
    }

    void setNewId(String id) {
        if (!id.equals(this.nonce)) {
            delete();
            this.nonce = id;
            FHResearch.clues.register(this);
            if (parent != null) {
                Research r = parent.get();
                if (r != null) {
                    r.attachClue(this);
                    r.doIndex();
                }
            }
        }
    }

    public void setCompleted(TeamResearchData trd, boolean trig) {
        trd.setClueTriggered(this, trig);
        trd.getTeam().ifPresent(team -> {
            if (trig)
                end(team);
            else
                start(team);
            this.sendProgressPacket(team);
        });
    }
    /**
     * Get brief string describe this clue for show in editor.
     *
     * @return brief<br>
     */
    public abstract String getBrief();
    public String getBriefDesc() {
    	return "   "+(this.required?"required ":"")+"+"+(int)(this.contribution*100)+"%";
    };
	public boolean isRequired() {
		return required;
	}
}
