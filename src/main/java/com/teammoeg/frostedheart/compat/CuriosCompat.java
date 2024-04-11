/*
 * Copyright (c) 2021-2024 TeamMoeg
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

package com.teammoeg.frostedheart.compat;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.frostedheart.content.utility.heatervest.HeaterVestItem;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class CuriosCompat {
	static class EmptyIterator<T> implements Iterator<T>{

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public T next() {
			throw new NoSuchElementException();
		}
		
		
	}
    public static Iterable<ItemStack> getAllCuriosIfVisible(LivingEntity el) {
    	return () -> CuriosApi.getCuriosHelper().getCuriosHandler(el).resolve().map(o->o.getCurios().values().stream().flatMap(t->IntStream.range(0, t.getSlots()).mapToObj(i->t.getStacks().getStackInSlot(i))).iterator()).orElseGet(EmptyIterator::new);
    }
    public static Iterable<Pair<String, ItemStack>> getAllCuriosAndSlotsIfVisible(LivingEntity el) {
        return () -> CuriosApi.getCuriosHelper().getCuriosHandler(el).resolve().map(o->o.getCurios().entrySet().stream().flatMap(t->IntStream.range(0, t.getValue().getSlots()).mapToObj(i->Pair.of(t.getKey(),t.getValue().getStacks().getStackInSlot(i)))).iterator()).orElseGet(EmptyIterator::new);
    }

    public static ItemStack getCuriosIfVisible(LivingEntity living, SlotTypePreset slot, Predicate<ItemStack> predicate) {
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosHelper().getCuriosHandler(living);
        return optional.resolve()
                .flatMap(handler -> handler.getStacksHandler(slot.getIdentifier()))
                .filter(ICurioStacksHandler::isVisible)
                .map(stacksHandler -> {
                    for (int i = 0; i < stacksHandler.getSlots(); i++)
                        if (stacksHandler.getRenders().get(i)) {
                            ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                            if (predicate.test(stack))
                                return stack;
                        }
                    return ItemStack.EMPTY;
                }).orElse(ItemStack.EMPTY);
    }

    public static ItemStack getHeaterVest(LivingEntity living) {
        return getCuriosIfVisible(living, SlotTypePreset.BACK, stack -> stack.getItem() instanceof HeaterVestItem);
    }

    public static void sendIMCS() {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.BACK.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.CHARM.getMessageBuilder().build());
    }
}
