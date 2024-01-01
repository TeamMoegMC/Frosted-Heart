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

package com.teammoeg.frostedheart.mixin.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.util.mixin.IFeedStore;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

@Mixin(Sheep.class)
public abstract class SheepEntityMixin extends Animal implements IFeedStore{
	

	protected SheepEntityMixin(EntityType<? extends Animal> type, Level worldIn) {
		super(type, worldIn);
	}
	byte feeded = 0;
	private final static ResourceLocation cow_feed = new ResourceLocation(FHMain.MODID, "cow_feed");
	@Shadow
	public abstract void shear(SoundSource category);

	@Shadow
	public abstract boolean isShearable();

	@Inject(at = @At("HEAD"), method = "eatGrassBonus", remap = true)
	public void fh$eatGrass(CallbackInfo cbi) {
		if (feeded < 2)
			feeded++;
	}

	@Inject(at = @At("HEAD"), method = "writeAdditional")
	public void fh$writeAdditional(CompoundTag compound, CallbackInfo cbi) {
		compound.putByte("feed_stored", feeded);

	}

	@Inject(at = @At("HEAD"), method = "writeAdditional")
	public void fh$readAdditional(CompoundTag compound, CallbackInfo cbi) {
		feeded = compound.getByte("feed_stored");
	}


	@Inject(at=@At("HEAD"),method="getEntityInteractionResult",cancellable=true)
	public void fh$getEntityInteractionResult(Player playerIn, InteractionHand hand,CallbackInfoReturnable<InteractionResult> cbi) {
		ItemStack itemstack = playerIn.getHeldItem(hand);

		if (!this.isChild() && !itemstack.isEmpty() && itemstack.getItem().getTags().contains(cow_feed)) {
			if (feeded < 2) {
				feeded++;
				if (!this.world.isRemote)
					this.consumeItemFromStack(playerIn, itemstack);
				cbi.setReturnValue(ActionResultType.func_233537_a_(this.world.isRemote));
			}
		}
	}
	@Override
	public boolean consumeFeed() {
		if(feeded>0) {
			feeded--;
			return true;
		}
		return false;
	}
}
