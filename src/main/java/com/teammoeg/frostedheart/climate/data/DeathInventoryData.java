/*
 * Copyright (c) 2022-2024 TeamMoeg
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

import javax.annotation.Nullable;

import com.teammoeg.frostedheart.FHMain;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class DeathInventoryData implements ICapabilitySerializable<CompoundTag> {
    @CapabilityInject(DeathInventoryData.class)
    public static Capability<DeathInventoryData> CAPABILITY;
    private final LazyOptional<DeathInventoryData> capability;
    public static final ResourceLocation ID = new ResourceLocation(FHMain.MODID, "death_inventory");
    CopyInventory inv;
    boolean calledClone=false;
    private static class CopyInventory{
    	NonNullList<ItemStack> inv=NonNullList.withSize(9, ItemStack.EMPTY);
    	NonNullList<ItemStack> armor=NonNullList.withSize(4, ItemStack.EMPTY);
    	ItemStack offhand=ItemStack.EMPTY;
    	
		public CopyInventory(Inventory othis) {
            for (int i = 0; i < 9; i++) {
                ItemStack itemstack = othis.items.get(i);
                if (!itemstack.isEmpty()) {
                	if(itemstack.isDamageableItem() || !itemstack.getToolTypes().isEmpty()) {
                		inv.set(i,itemstack);
                		othis.setItem(i, ItemStack.EMPTY);
                	}
                }
            }
            ItemStack offhand=othis.offhand.get(0);
            if(offhand.isDamageableItem() || !offhand.getToolTypes().isEmpty()) {
            	this.offhand=offhand;
            	othis.offhand.set(0, ItemStack.EMPTY);
            }
            for(int i=0;i<4;i++) {
            	armor.set(i,othis.armor.get(i));
            	othis.armor.set(i, ItemStack.EMPTY);
            }
		}
		
		private CopyInventory(CompoundTag nbt) {
			super();
			ContainerHelper.loadAllItems(nbt.getCompound("main"), inv);
			ContainerHelper.loadAllItems(nbt.getCompound("armor"), armor);
			this.offhand=ItemStack.of(nbt.getCompound("off"));
		
		}

		public void restoreInventory(Inventory othis) {
			for(int i=0;i<9;i++) {
				ItemStack ret=inv.get(i);
				if(!ret.isEmpty())
				othis.setItem(i, ret);
			}
			for(int i=0;i<4;i++) {
				ItemStack ret=armor.get(i);
				if(!ret.isEmpty())
					othis.armor.set(i, ret);
			}
			if(offhand!=null&&!offhand.isEmpty()) {
				othis.offhand.set(0, offhand);
			}
		}
		public CompoundTag serializeNBT() {
			CompoundTag cnbto=new CompoundTag();
			
			
			cnbto.put("main",ContainerHelper.saveAllItems(new CompoundTag(),inv));
			cnbto.put("armor",ContainerHelper.saveAllItems(new CompoundTag(),armor));
			cnbto.put("off",offhand.serializeNBT());
			return cnbto;
		}

		public static CopyInventory deserializeNBT(CompoundTag nbt) {
			
			return new CopyInventory(nbt);
		}
    }

    public DeathInventoryData() {
        capability = LazyOptional.of(() -> this);
    }
    /**
     * Setup capability's serialization to disk.
     */
    public static void setup() {
        CapabilityManager.INSTANCE.register(DeathInventoryData.class, new Capability.IStorage<DeathInventoryData>() {
            public Tag writeNBT(Capability<DeathInventoryData> capability, DeathInventoryData instance, Direction side) {
                return instance.serializeNBT();
            }

            public void readNBT(Capability<DeathInventoryData> capability, DeathInventoryData instance, Direction side, Tag nbt) {
                instance.deserializeNBT((CompoundTag) nbt);
            }
        }, DeathInventoryData::new);
    }

    /**
     * Get ClimateData attached to this world
     *
     * @param player server or client
     * @return An instance of ClimateData if data exists on the world, otherwise return empty.
     */
    private static LazyOptional<DeathInventoryData> getCapability(@Nullable Player player) {
        if (player != null) {
            return player.getCapability(CAPABILITY);
        }
        return LazyOptional.empty();
    }

    /**
     * Get ClimateData attached to this world
     *
     * @param world server or client
     * @return An instance of ClimateData exists on the world, otherwise return a new ClimateData instance.
     */
    public static DeathInventoryData get(Player world) {
    	
        return getCapability(world).resolve().orElse(null);
    }
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap==CAPABILITY)
			return capability.cast();
		return LazyOptional.empty();
	}
	public void death(Inventory inv) {
		this.inv=new CopyInventory(inv);
	}
	public void alive(Inventory inv) {
		if(this.inv!=null) {
			this.inv.restoreInventory(inv);
			this.inv=null;
		}
	}
	public void copy(DeathInventoryData data) {
		this.inv=data.inv;
	}
	public void calledClone() {
		//System.out.println("called clone event");
		calledClone=true;
	}
	public void tryCallClone(Player pe) {
		//System.out.println("Detecting clone event");
		if(!calledClone) {
			//System.out.println("calling clone event");
			MinecraftForge.EVENT_BUS.post(new PlayerEvent.Clone(pe,pe,true));
		}
	}
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag cnbt=new CompoundTag();
		if(inv!=null)
			cnbt.put("inv",inv.serializeNBT());
		cnbt.putBoolean("cloned", calledClone);
		return cnbt;
	}
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		inv=null;
		calledClone=nbt.getBoolean("cloned");
		if(nbt.contains("data"))
			nbt.getList("data", 10).stream().map(t->(CompoundTag)t).forEach(e->{
				inv=CopyInventory.deserializeNBT(e.getCompound("inv"));
			});
		else if(nbt.contains("inv"))
			inv=CopyInventory.deserializeNBT(nbt.getCompound("inv"));
	}
	public void startClone() {
		calledClone=false;
	}

}
