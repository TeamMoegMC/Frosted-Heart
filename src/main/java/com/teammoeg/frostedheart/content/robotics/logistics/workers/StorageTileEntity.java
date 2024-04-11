package com.teammoeg.frostedheart.content.robotics.logistics.workers;

import com.teammoeg.frostedheart.base.block.FHBaseTileEntity;
import com.teammoeg.frostedheart.content.robotics.logistics.FilterSlot;
import com.teammoeg.frostedheart.content.robotics.logistics.ItemChangeListener;
import com.teammoeg.frostedheart.content.robotics.logistics.ItemHandlerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.items.ItemStackHandler;

public class StorageTileEntity extends FHBaseTileEntity implements ILogisticsStorage,ItemChangeListener{
	ItemStackHandler container=new ItemStackHandler(27);
	ItemHandlerListener handler=new ItemHandlerListener(container,this);
	FilterSlot filter;
	public StorageTileEntity(TileEntityType<? extends TileEntity> type) {
		super(type);
		filter=new FilterSlot();
	}

	@Override
	public ItemStackHandler getInventory() {
		return container;
	}

	@Override
	public boolean isValidFor(ItemStack stack) {
		return filter.isValidFor(stack);
	}


	@Override
	public void onSlotChange(int slot, ItemStack after) {
	}

	@Override
	public void onSlotClear(int slot) {
	}

	@Override
	public void onCountChange(int slot, int before, int after) {
	}

	@Override
	public void readCustomNBT(CompoundNBT arg0, boolean arg1) {
		container.deserializeNBT(arg0.getCompound("container"));
	}

	@Override
	public void writeCustomNBT(CompoundNBT arg0, boolean arg1) {
		arg0.put("container", container.serializeNBT());
	}

}
