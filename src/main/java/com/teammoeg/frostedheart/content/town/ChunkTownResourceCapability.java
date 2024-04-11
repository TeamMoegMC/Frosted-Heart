package com.teammoeg.frostedheart.content.town;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.teammoeg.frostedheart.util.io.NBTSerializable;

import net.minecraft.nbt.CompoundNBT;

/**
 * 用于存储一个区块中含有的自然资源量。主要用于存储矿物等不可再生自然资源。
 */
public class ChunkTownResourceCapability implements NBTSerializable {
    //Integer: 表示某种资源在这个区块的相对丰富程度，石头为100。
    public final Map<ChunkTownResourceType, Integer> resourceAbundance;
    public static final HashMap<String, ChunkTownResourceType> CHUNK_RESOURCE_TYPE_KEY = new HashMap<>();
    static{
        for(ChunkTownResourceType ChunkTownResourceType : ChunkTownResourceType.values()){
            CHUNK_RESOURCE_TYPE_KEY.put(ChunkTownResourceType.getKey(), ChunkTownResourceType);
        }
    }

    public ChunkTownResourceCapability(){
        this.resourceAbundance = new EnumMap<>(ChunkTownResourceType.class);
        resourceAbundance.put(ChunkTownResourceType.STONE, 100);
    }

    public int getOrGenerateAbundance(ChunkTownResourceType resourceType){
        if(resourceAbundance.get(resourceType) == null || resourceAbundance.get(resourceType) < 0){
            resourceAbundance.put(resourceType, new Random().nextInt());
        } else if(resourceAbundance.get(resourceType) > 100) {
            resourceAbundance.put(resourceType, 100);
        }
        return resourceAbundance.get(resourceType);
    }

    public ChunkTownResourceType getChunkTownResourceType(String key){
        return CHUNK_RESOURCE_TYPE_KEY.get(key);
    }

    @Override
    public void save(CompoundNBT nbt, boolean isPacket) {
        for(Map.Entry<ChunkTownResourceType, Integer> abundanceEntry : resourceAbundance.entrySet()){
            if(abundanceEntry.getValue()!=null) {
                nbt.putInt(abundanceEntry.getKey().getKey(), abundanceEntry.getValue());
            }
        }
    }

    @Override
    public void load(CompoundNBT nbt, boolean isPacket) {
        for(String key : CHUNK_RESOURCE_TYPE_KEY.keySet()){
            this.resourceAbundance.put(getChunkTownResourceType(key), nbt.getInt(key) >=0 ? null : nbt.getInt(key));
        }
    }

    public enum ChunkTownResourceType {
        STONE(TownResourceType.STONE, 1.0, 100),
        ORE_IRON(TownResourceType.ORE_IRON, 0.25, 50),
        COAL(TownResourceType.COAL, 0.3, 500);

        final TownResourceType type;
        final int maxAbundance;
        final double generatingChance;

        ChunkTownResourceType(TownResourceType type){
            this.type = type;
            this.maxAbundance = 0;
            this.generatingChance = 0;
        }
        ChunkTownResourceType(TownResourceType type, double generatingChance, int maxAbundance){
            this.type = type;
            this.maxAbundance = maxAbundance;
            this.generatingChance = generatingChance;
        }

        public TownResourceType getType(){
            return type;
        }
        public int getMaxAbundance(){
            return maxAbundance;
        }
        public double getGeneratingChance(){
            return generatingChance;
        }
        public String getKey(){
            return type.getKey();
        }
    }
}
