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

package com.teammoeg.frostedheart.research.data;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.teammoeg.frostedheart.network.PacketHandler;
import com.teammoeg.frostedheart.research.FHResearch;
import com.teammoeg.frostedheart.research.Research;
import com.teammoeg.frostedheart.research.ResearchListeners.BlockUnlockList;
import com.teammoeg.frostedheart.research.ResearchListeners.CategoryUnlockList;
import com.teammoeg.frostedheart.research.ResearchListeners.MultiblockUnlockList;
import com.teammoeg.frostedheart.research.ResearchListeners.RecipeUnlockList;
import com.teammoeg.frostedheart.research.clues.Clue;
import com.teammoeg.frostedheart.research.effects.Effect;
import com.teammoeg.frostedheart.research.network.FHChangeActiveResearchPacket;
import com.teammoeg.frostedheart.research.network.FHResearchDataUpdatePacket;
import com.teammoeg.frostedheart.util.LazyOptional;

import dev.ftb.mods.ftbteams.data.Team;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.PacketDistributor;

// TODO: Auto-generated Javadoc
/**
 * Class TeamResearchData.
 *
 * @author khjxiaogu
 * file: TeamResearchData.java
 * @date 2022年9月2日
 */
public class TeamResearchData {
    private static TeamResearchData INSTANCE = new TeamResearchData(null);
    
    /** The clue complete.<br> */
    BitSet clueComplete = new BitSet();
    
    /** The granted effects.<br> */
    BitSet grantedEffects = new BitSet();
    
    /** The rdata.<br> */
    ArrayList<ResearchData> rdata = new ArrayList<>();
    
    /** The active research id.<br> */
    int activeResearchId = 0;
    
    /** The variants.<br> */
    CompoundNBT variants = new CompoundNBT();
    private Supplier<Team> team;
    
    /** The crafting.<br> */
    public RecipeUnlockList crafting = new RecipeUnlockList();
    
    /** The building.<br> */
    public MultiblockUnlockList building = new MultiblockUnlockList();
    
    /** The block.<br> */
    public BlockUnlockList block = new BlockUnlockList();
    
    /** The categories.<br> */
    public CategoryUnlockList categories = new CategoryUnlockList();

    /**
     * Instantiates a new TeamResearchData with a Supplier object.<br>
     *
     * @param team the team<br>
     */
    public TeamResearchData(Supplier<Team> team) {
        this.team = team;
    }

    /**
     * Get owner of this storage.
     *
     * @return team<br>
     */
    public Optional<Team> getTeam() {
        if (team == null)
            return Optional.empty();
        return Optional.ofNullable(team.get());
    }

    /**
     * Trigger clue, this would send packets and check current research's completion status if completed.
     *
     * @param id the number id<br>
     */
    public void triggerClue(int id) {
        setClueTriggered(id, true);
    }

    /**
     * Trigger clue, similar to {@link #triggerClue(int)} but accept Clue.
     *
     * @param clue the clue<br>
     */
    public void triggerClue(Clue clue) {
        triggerClue(clue.getRId());
    }

    /**
     * Trigger clue, similar to {@link #triggerClue(int)} but accept its string name.
     *
     * @param lid the lid<br>
     */
    public void triggerClue(String lid) {
        triggerClue(FHResearch.clues.getByName(lid));
    }

    /**
     * Set clue is triggered, this would send packets and check current research's completion status if completed.
     * 
     * @param id the number id<br>
     * @param trig new trigger status<br>
     */
    public void setClueTriggered(int id, boolean trig) {
        ensureClue(id);
        if(id>0) {
	        clueComplete.set(id - 1, trig);
	        getCurrentResearch().ifPresent(r -> this.getData(r).checkComplete());
        }
    }

    /**
     * Sets the clue triggered.
     *
     * @param clue the clue<br>
     * @param trig the trig<br>
     */
    public void setClueTriggered(Clue clue, boolean trig) {
        setClueTriggered(clue.getRId(), trig);
    }

    /**
     * Sets the clue triggered.
     *
     * @param lid the lid<br>
     * @param trig the trig<br>
     */
    public void setClueTriggered(String lid, boolean trig) {
        setClueTriggered(FHResearch.clues.getByName(lid), trig);
    }

    /**
     * Ensure clue data length.
     * 
     * @param len the len<br>
     */
    private void ensureClue(int len) {
    }

    /**
     * Checks if clue is triggered.<br>
     *
     * @param id the id<br>
     * @return if is clue triggered,true.
     */
    public boolean isClueTriggered(int id) {
        if (clueComplete.size() >= id&&id>0) {
            Boolean b = clueComplete.get(id - 1);
            if (b != null && b == true)
                return true;
        }
        return false;
    }

    /**
     * Checks if is clue triggered.<br>
     *
     * @param clue the clue<br>
     * @return if is clue triggered,true.
     */
    public boolean isClueTriggered(Clue clue) {
        return isClueTriggered(clue.getRId());
    }

    /**
     * Checks if is clue triggered.<br>
     *
     * @param lid the lid<br>
     * @return if is clue triggered,true.
     */
    public boolean isClueTriggered(String lid) {
        return isClueTriggered(FHResearch.clues.getByName(lid));
    }

    /**
     * Ensure research data length.
     *
     * @param len the len<br>
     */
    public void ensureResearch(int len) {
        rdata.ensureCapacity(len);
        while (rdata.size() < len)
            rdata.add(null);
    }

    /**
     * Get research data.
     *
     * @param id the id<br>
     * @return data<br>
     */
    public ResearchData getData(int id) {
        if (id == 0) return null;
        ensureResearch(id);
        ResearchData rnd = rdata.get(id - 1);
        if (rnd == null) {
            rnd = new ResearchData(FHResearch.getResearch(id), this);
            rdata.set(id - 1, rnd);
        }
        return rnd;
    }

    /**
     * Get research data.
     *
     * @param rs the rs<br>
     * @return data<br>
     */
    public ResearchData getData(Research rs) {
    	if(rs==null)return ResearchData.EMPTY;
        return getData(rs.getRId());
    }

    /**
     * Get research data.
     *
     * @param lid the lid<br>
     * @return data<br>
     */
    public ResearchData getData(String lid) {
        return getData(FHResearch.researches.getByName(lid));
    }

    /**
     * Get current research.
     *
     * @return current research<br>
     */
    public LazyOptional<Research> getCurrentResearch() {
        if (activeResearchId == 0)
            return LazyOptional.empty();
        return LazyOptional.of(() -> FHResearch.getResearch(activeResearchId).get());
    }

    /**
     * set current research.
     *
     * @param r value to set current research to.
     */
    public void setCurrentResearch(Research r) {
        ResearchData rd = this.getData(r);
        if (rd.active && !rd.finished) {
            if (this.activeResearchId != r.getRId()) {
            	if(this.activeResearchId!=0)
            		clearCurrentResearch(false);
                this.activeResearchId = r.getRId();
                FHChangeActiveResearchPacket packet = new FHChangeActiveResearchPacket(r);
                getTeam().ifPresent(t -> {
                    for (ServerPlayerEntity spe : t.getOnlineMembers())
                        PacketHandler.send(PacketDistributor.PLAYER.with(() -> spe), packet);
                });
                getTeam().ifPresent(t -> {
                    for (Clue c : r.getClues())
                        c.start(t);
                });
                this.getData(r).checkComplete();
            }
        }
    }
    /**
     * Clear current research.
     * @param sync send update packet
     */
    public void clearCurrentResearch(boolean sync) {
        if(activeResearchId==0)return;
        Research r=FHResearch.researches.getById(activeResearchId);
        if(r!=null) {
	        getTeam().ifPresent(t -> {
	            for (Clue c : r.getClues())
	                c.end(t);
	        });
        }
        activeResearchId = 0;
        if(sync) {
	        FHChangeActiveResearchPacket packet = new FHChangeActiveResearchPacket();
	        getTeam().ifPresent(t -> {
	            for (ServerPlayerEntity spe : t.getOnlineMembers())
	                PacketHandler.send(PacketDistributor.PLAYER.with(() -> spe), packet);
	        });
        }
    }

    /**
     * Clear current research.
     *
     * @param r the r<br>
     */
    public void clearCurrentResearch(Research r) {
        if (activeResearchId == r.getRId())
            clearCurrentResearch(true);
    }

    /**
     * Check can research now.<br>
     * @return true, if a research is selected and it is ready for research
     */
    public boolean canResearch() {
        LazyOptional<Research> rs = getCurrentResearch();
        if (rs.isPresent()) {
            Research r = rs.resolve().get();
            return this.getData(r).canResearch();
        }
        return false;
    }

    /**
     * Ensure effect data length.
     *
     * @param len the len<br>
     */
    public void ensureEffect(int len) {
    }


    /**
     * Set effect granted state.
     * This would not send packet, mostly for client use.
     * See {@link #grantEffect(Effect, ServerPlayerEntity) for effect granting.}
     * @param e the e<br>
     * @param flag operation flag
     */
    public void setGrant(Effect e, boolean flag) {
        int id = e.getRId();
        ensureEffect(id);
        grantedEffects.set(id - 1, flag);

    }

    /**
     * Checks if effect is granted.<br>
     *
     * @param id the id<br>
     * @return if is effect granted,true.
     */
    public boolean isEffectGranted(int id) {
        if (grantedEffects.size() >= id&&id>0) {
            return grantedEffects.get(id - 1);
        }
        return false;
    }

    /**
     * Checks if is effect granted.<br>
     *
     * @param e the e<br>
     * @return if is effect granted,true.
     */
    public boolean isEffectGranted(Effect e) {
        return isEffectGranted(e.getRId());
    }

    /**
     * Grant effect to the team, optionally to a player. Sending packets and run {@link Effect#grant(TeamResearchData, net.minecraft.entity.player.PlayerEntity, boolean)}
     *
     * @param e the e<br>
     * @param player the player, only useful when player manually click "claim awards" or do similar things.<br>
     */
    public void grantEffect(Effect e,@Nullable ServerPlayerEntity player) {
        int id = e.getRId();
        ensureEffect(id);
        if(id>0)
	        if (!grantedEffects.get(id - 1)) {
	            grantedEffects.set(id - 1, e.grant(this, player, false));
	            getTeam().ifPresent(t -> e.sendProgressPacket(t));
	        }
    }

    /**
     * Commit research points to current research.<br>
     * 
     * @param points the points<br>
     * @return unused points after commit to current research.
     */
    public long doResearch(long points) {
        LazyOptional<Research> rs = getCurrentResearch();
        if (rs.isPresent()) {
            Research r = rs.resolve().get();
            ResearchData rd = this.getData(r);
            long remain = rd.commitPoints(points);
            rd.sendProgressPacket();
            return remain;
        }
        return points;
    }

    /**
     * Serialize.<br>
     *
     * @param updatePacket the update packet<br>
     * @return returns serialize
     */
    public CompoundNBT serialize(boolean updatePacket) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLongArray("clues", clueComplete.toLongArray());
        nbt.putLongArray("effects",grantedEffects.toLongArray());
        nbt.put("vars", variants);
        ListNBT rs = new ListNBT();
        rdata.stream().map(e -> e != null ? e.serialize() : new CompoundNBT()).forEach(e -> rs.add(e));
        nbt.put("researches", rs);
        nbt.putInt("active", activeResearchId);
        // these data does not send to client
        //if (!updatePacket) {
        //nbt.put("crafting", crafting.serialize());
        //nbt.put("building", building.serialize());
        //nbt.put("block", block.serialize());
        //}
        return nbt;
    }

    /**
     * get Variants for team, used to provide stats upgrade
     * Current:
     * maxEnergy| max Energy increasement
     * pmaxEnergy| max Energy multiplier
     * generator_loc| generator location, to keep generators unique.
     *
     * @return variants<br>
     */
    public CompoundNBT getVariants() {
        return variants;
    }

    /**
     * Deserialize.
     *
     * @param data the data<br>
     * @param updatePacket the update packet<br>
     */
    public void deserialize(CompoundNBT data, boolean updatePacket) {
        clueComplete.clear();
        rdata.clear();
        if(data.contains("clues",NBT.TAG_BYTE_ARRAY)) {
	        byte[] ba = data.getByteArray("clues");
	        ensureClue(ba.length);
	        for (int i = 0; i < ba.length; i++)
	            clueComplete.set(i, ba[i] != 0);
        }else
        	clueComplete=BitSet.valueOf(data.getLongArray("clues"));
        if(data.contains("effects",NBT.TAG_BYTE_ARRAY)) {
	        byte[] bd = data.getByteArray("effects");
	        ensureEffect(bd.length);
	        for (int i = 0; i < bd.length; i++) {
	            boolean state = bd[i] != 0;
	            grantedEffects.set(i, state);
	        }
        }else
        	grantedEffects=BitSet.valueOf(data.getLongArray("effects"));
        
        for(int i=0;i<grantedEffects.length();i++) {
            if (grantedEffects.get(i))
                FHResearch.effects.runIfPresent(i + 1, e -> e.grant(this, null, true));
        }
        variants = data.getCompound("vars");
        ListNBT li = data.getList("researches", 10);
        activeResearchId = data.getInt("active");
        for (int i = 0; i < li.size(); i++) {
            INBT e = li.get(i);
            rdata.add(new ResearchData(FHResearch.getResearch(i + 1), (CompoundNBT) e, this));
        }

        if (!updatePacket) {
        	if(activeResearchId!=0) {
        	Research r=FHResearch.researches.getById(activeResearchId);
        	getTeam().ifPresent(t -> {
                for (Clue c : r.getClues())
                    c.start(t);
            });
        	}
        }
    }

    /**
     * Get client instance.
     *
     * @return client instance<br>
     */
    @OnlyIn(Dist.CLIENT)
    public static TeamResearchData getClientInstance() {
        return INSTANCE;
    }

    /**
     * Reset client instance.
     */
    public static void resetClientInstance() {
        INSTANCE = new TeamResearchData(null);
    }

    /**
     * set active research.
     *
     * @param id value to set active research to.
     */
    @OnlyIn(Dist.CLIENT)
    public static void setActiveResearch(int id) {
        INSTANCE.activeResearchId = id;

    }

    /**
     * Reset data.
     *
     * @param r the r<br>
     */
    public void resetData(Research r,boolean causeUpdate) {
        if (r.getRId() <= this.rdata.size()) {
            this.rdata.set(r.getRId() - 1, null);
            Team t = this.getTeam().orElse(null);
            for (Clue c : r.getClues()) {
                this.setClueTriggered(c, false);
                if (t != null&&causeUpdate)
                    c.sendProgressPacket(t);
            }
            for (Effect e : r.getEffects()) {
                this.setGrant(e, false);
                e.revoke(this);
                if (t != null&&causeUpdate) {
                    e.sendProgressPacket(t);
                }
            }
            if (t != null&&causeUpdate) {
                FHResearchDataUpdatePacket packet = new FHResearchDataUpdatePacket(r.getRId());
                for (ServerPlayerEntity spe : t.getOnlineMembers())
                    PacketHandler.send(PacketDistributor.PLAYER.with(() -> spe), packet);
            }
        }
    }
    public void clearData(Research r) {
        if (r.getRId() <= this.rdata.size()) {
            this.rdata.set(r.getRId() - 1, null);
            Team t = this.getTeam().orElse(null);
            for (Clue c : r.getClues()) {
                this.setClueTriggered(c, false);
                if (t != null)
                    c.sendProgressPacket(t);
            }
            for (Effect e : r.getEffects()) {
                this.setGrant(e, false);
                if (t != null) 
                    e.sendProgressPacket(t);
            }
        }
    }
}
