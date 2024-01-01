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

package com.teammoeg.frostedheart.trade.policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.trade.FHVillagerData;
import com.teammoeg.frostedheart.trade.policy.snapshot.PolicySnapshot;
import com.teammoeg.frostedheart.util.SerializeUtil;
import com.teammoeg.frostedheart.util.Writeable;

import net.minecraft.network.FriendlyByteBuf;

public abstract class BaseData implements Writeable{
	private String id;
	public int maxstore;
	float recover;
	public int price;
	public List<PolicyAction> actions;
	public List<PolicyAction> soldactions=new ArrayList<>();
	public List<PolicyCondition> restockconditions=new ArrayList<>();
	boolean hideStockout;
	public BaseData(String id, int maxstore, float recover, int price,PolicyAction...restock) {
		super();
		this.id = id;
		this.maxstore = maxstore;
		this.recover = recover;
		this.price = price;
		this.actions=new ArrayList<>(Arrays.asList(restock));
	}
	public BaseData(JsonObject jo) {
		id=jo.get("id").getAsString();
		maxstore=jo.get("store").getAsInt();
		recover=jo.get("recover").getAsFloat();
		price=jo.get("price").getAsInt();
		this.actions=SerializeUtil.parseJsonList(jo.get("actions"),Actions::deserialize);
		this.soldactions=SerializeUtil.parseJsonList(jo.get("use_actions"),Actions::deserialize);
		this.restockconditions=SerializeUtil.parseJsonList(jo.get("restock_condition"),Conditions::deserialize);
		if(jo.has("hide_stockout"))
			hideStockout=jo.get("hide_stockout").getAsBoolean();
	}
	public BaseData(FriendlyByteBuf pb) {
		id=pb.readUtf();
		maxstore=pb.readVarInt();
		recover=pb.readFloat();
		price=pb.readVarInt();
		this.actions=SerializeUtil.readList(pb,Actions::deserialize);
		this.soldactions=SerializeUtil.readList(pb,Actions::deserialize);
		this.restockconditions=SerializeUtil.readList(pb,Conditions::deserialize);
		hideStockout=pb.readBoolean();
	}
	
	public void tick(int deltaDay,FHVillagerData data) {
		//System.out.println("try recover for "+id+" : "+deltaDay);
		if(deltaDay>0&&canRestock(data)) {
			float curstore=data.storage.getOrDefault(getId(),0f);
			int recDay=Math.min((int) Math.ceil((maxstore-curstore)/recover),deltaDay);
			float val=Math.min(recover*recDay+curstore,maxstore);
			data.storage.put(getId(),val);
			actions.forEach(c->c.deal(data, recDay));
		}
	}
	public boolean canRestock(FHVillagerData fhvd) {
		System.out.println("testing conditions "+restockconditions);
		System.out.println("Flags:"+fhvd.flags);
		boolean res=restockconditions.stream().allMatch(c->c.test(fhvd));
		System.out.println("result:"+res);
		return res;
	}
	public abstract String getType();
	public String getId() {
		return id+"_"+getType();
	}
	public abstract void fetch(PolicySnapshot shot,Map<String,Float> data);
	@Override
	public JsonElement serialize() {
		JsonObject jo=new JsonObject();
		jo.addProperty("id", id);
		jo.addProperty("store", maxstore);
		jo.addProperty("recover",recover);
		jo.addProperty("price", price);
		jo.add("actions",SerializeUtil.toJsonList(actions, PolicyAction::serialize));
		jo.add("use_actions",SerializeUtil.toJsonList(soldactions, PolicyAction::serialize));
		jo.add("restock_condition",SerializeUtil.toJsonList(restockconditions, PolicyCondition::serialize));
		jo.addProperty("hide_stockout", hideStockout);
		
		return jo;
	}
	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(id);
		buffer.writeVarInt(maxstore);
		buffer.writeFloat(recover);
		buffer.writeVarInt(price);
		SerializeUtil.writeList(buffer, actions, PolicyAction::write);
		SerializeUtil.writeList(buffer, soldactions, PolicyAction::write);
		SerializeUtil.writeList(buffer, restockconditions, PolicyCondition::write);
		buffer.writeBoolean(hideStockout);
	}
	public static BaseData read(FriendlyByteBuf pb) {
		switch(pb.readVarInt()) {
		case 1:return new ProductionData(pb);
		case 2:return new DemandData(pb);
		default:return new NopData(pb);
		}
	}
	public static BaseData read(JsonObject jo) {
		if(jo.has("produce"))
			return new ProductionData(jo);
		else if(jo.has("demand"))
			return new DemandData(jo);
		return new NopData(jo);
			
	}
	public void execute(FHVillagerData data,int count) {
		soldactions.forEach(c->c.deal(data, count));
	}
	@Override
	public String toString() {
		return "BaseData [id=" + id + ", maxstore=" + maxstore + ", recover=" + recover + ", price=" + price + "]";
	}
}
