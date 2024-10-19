package com.teammoeg.frostedheart;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import blusunrize.immersiveengineering.common.gui.BlockEntityInventory;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.Lazy;

public abstract class FHBaseContainer<T extends BlockEntity> extends AbstractContainerMenu {
	public static class QuickMoveStackBuilder{
		private static record Range(int start,int end,boolean reverse){
			private Range(int slot) {
				this(slot,slot+1,false);
			}
			
		}
		List<Range> ranges=new ArrayList<>();
		private QuickMoveStackBuilder() {}
		
		public static QuickMoveStackBuilder begin() {
			return new QuickMoveStackBuilder();
		}
		public static QuickMoveStackBuilder first(int slot) {
			return begin().then(slot);
		}
		public static QuickMoveStackBuilder first(int beginInclusive,int endExclusive) {
			return begin().then(beginInclusive,endExclusive);
		}
		public QuickMoveStackBuilder then(int slot) {
			ranges.add(new Range(slot));
			return this;
		}
		public QuickMoveStackBuilder then(int beginInclusive,int endExclusive) {
			ranges.add(new Range(beginInclusive,endExclusive,false));
			return this;
		}
		public QuickMoveStackBuilder then(int beginInclusive,int endExclusive,boolean reversed) {
			ranges.add(new Range(beginInclusive,endExclusive,reversed));
			return this;
		}
		public Function<ItemStack,Boolean> build(FHBaseContainer t){
			return i->{
				for(Range r:ranges) {
					if(t.moveItemStackTo(i, r.start(), r.end(), r.reverse()))
						return true;
				}
				return false;
			};
			
		}
	}
	protected T blockEntity;
	public Container inv;

	public T getBlock() {
		return blockEntity;
	}

	protected FHBaseContainer(MenuType<?> pMenuType, T blockEntity, int pContainerId, int inv_start) {
		super(pMenuType, pContainerId);
		if (blockEntity instanceof IIEInventory)
			inv = new BlockEntityInventory(blockEntity, this);
		else if (blockEntity instanceof Container cont)
			inv = cont;
		INV_START = inv_start;
		this.blockEntity = blockEntity;

	}

	protected final int INV_START;
	protected static final int INV_SIZE = 36;
	protected static final int INV_QUICK = 27;
	protected Lazy<Function<ItemStack,Boolean>> moveFunction=Lazy.of(()->defineQuickMoveStack().build(this));
	protected void addPlayerInventory(Inventory inv, int dx, int dy, int quickBarY) {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new Slot(inv, j + i * 9 + 9, dx + j * 18, dy + i * 18));
		for (int i = 0; i < 9; i++)
			addSlot(new Slot(inv, i, dx + i * 18, quickBarY));
	}
	public QuickMoveStackBuilder defineQuickMoveStack() {
		return QuickMoveStackBuilder.first(0,INV_START);
	} 
	/*
	 * Logics for quick move inside the container; return true if succeed
	 */
	public boolean quickMoveIn(ItemStack slotStack) {
		return moveFunction.get().apply(slotStack);
	};

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			itemStack = slotStack.copy();
			if (index < INV_START) {
				if (!this.moveItemStackTo(slotStack, INV_START, INV_SIZE + INV_START, true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(slotStack, itemStack);
			} else if (index >= INV_START) {
				if (!quickMoveIn(slotStack)) {
					if (index < INV_QUICK + INV_START) {
						if (!this.moveItemStackTo(slotStack, INV_QUICK + INV_START, INV_SIZE + INV_START, false))
							return ItemStack.EMPTY;
					} else if (index < INV_SIZE + INV_START && !this.moveItemStackTo(slotStack, INV_START, INV_QUICK + INV_START, false))
						return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(slotStack, INV_START, INV_SIZE + INV_START, false)) {
				return ItemStack.EMPTY;
			}
			if (slotStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (slotStack.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(playerIn, slotStack);
		}
		return itemStack;
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return !blockEntity.isRemoved();
	}
	//modify: make public
	@Override
	public boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
		return super.moveItemStackTo(pStack, pStartIndex, pEndIndex, pReverseDirection);
	}
}