package com.teammoeg.frostedheart.content.robotics.logistics.workers;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public interface ILogisticsStorage {
	ItemStackHandler getInventory();
	boolean isValidFor(ItemStack stack);
}