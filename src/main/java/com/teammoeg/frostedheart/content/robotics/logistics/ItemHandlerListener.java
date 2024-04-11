package com.teammoeg.frostedheart.content.robotics.logistics;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerListener implements IItemHandler, IItemHandlerModifiable {
	ItemStackHandler handler;
	ItemChangeListener listener;


	public ItemHandlerListener(ItemStackHandler handler, ItemChangeListener listener) {
		super();
		this.handler = handler;
		this.listener = listener;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		handler.setStackInSlot(slot, stack);
		listener.onSlotChange(slot, stack);
	}

	@Override
	public int getSlots() {
		return handler.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return handler.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		int oldCount=handler.getStackInSlot(slot).getCount();
		ItemStack reminder=handler.insertItem(slot, stack, simulate);
		int newCount=handler.getStackInSlot(slot).getCount();
		
		if(!simulate&&oldCount!=newCount) {
			if(oldCount!=0) 
				listener.onCountChange(slot, oldCount, newCount);
			else
				listener.onSlotChange(slot, handler.getStackInSlot(slot));
		}
		return reminder;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		int origCount=handler.getStackInSlot(slot).getCount();
		ItemStack reminder=handler.extractItem(slot, amount, simulate);
		int newCount=handler.getStackInSlot(slot).getCount();
		if(!simulate&&origCount!=newCount) {
			if(newCount==0)
				listener.onSlotClear(slot);
			else 
				listener.onCountChange(slot, origCount, newCount);
		}
		return reminder;
	}

	@Override
	public int getSlotLimit(int slot) {
		return handler.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return handler.isItemValid(slot, stack);
	}
	

}
