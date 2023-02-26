package com.teammoeg.frostedheart.trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.frostedheart.util.SerializeUtil;
import com.teammoeg.frostedheart.util.Writeable;

import net.minecraft.network.PacketBuffer;

public abstract class BaseData implements Writeable{
	private String id;
	int maxstore;
	float recover;
	int price;
	public List<PolicyAction> actions;
	public List<PolicyAction> soldactions=new ArrayList<>();
	public List<PolicyCondition> restockconditions=new ArrayList<>();
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
	}
	public BaseData(PacketBuffer pb) {
		id=pb.readString();
		maxstore=pb.readVarInt();
		recover=pb.readFloat();
		price=pb.readVarInt();
		this.actions=SerializeUtil.readList(pb,Actions::deserialize);
		this.soldactions=SerializeUtil.readList(pb,Actions::deserialize);
		this.restockconditions=SerializeUtil.readList(pb,Conditions::deserialize);
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
		return jo;
	}
	@Override
	public void write(PacketBuffer buffer) {
		buffer.writeString(id);
		buffer.writeVarInt(maxstore);
		buffer.writeFloat(recover);
		buffer.writeVarInt(price);
		SerializeUtil.writeList(buffer, actions, PolicyAction::write);
		SerializeUtil.writeList(buffer, soldactions, PolicyAction::write);
		SerializeUtil.writeList(buffer, restockconditions, PolicyCondition::write);
	}
	public static BaseData read(PacketBuffer pb) {
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
