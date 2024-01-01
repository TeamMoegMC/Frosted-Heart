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

package com.teammoeg.frostedheart.mixin.minecraft;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.trade.FHVillagerData;
import com.teammoeg.frostedheart.trade.RelationList;
import com.teammoeg.frostedheart.trade.TradeHandler;
import com.teammoeg.frostedheart.util.VillagerDataHolder;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.level.Level;
import net.minecraft.world.server.ServerWorld;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager implements VillagerDataHolder {
	FHVillagerData fh$data=new FHVillagerData(getThis());
    public VillagerMixin(EntityType<? extends AbstractVillager> type, Level worldIn) {
        super(type, worldIn);
    }

    @Shadow
    protected abstract void shakeHead();
    @Shadow
    public abstract void setCustomer(@Nullable Player player);
    
    @Shadow
    protected abstract void displayMerchantGui(Player pe);

    private Villager getThis() {
    	return (Villager)(Object)this;
    }
	@Inject(at = @At("HEAD"), method = "writeAdditional")
	public void fh$writeAdditional(CompoundTag compound, CallbackInfo cbi) {
		CompoundTag cnbt=new CompoundTag();
		fh$data.serialize(cnbt);
		compound.put("fhdata",cnbt);
	}

	@Inject(at = @At("HEAD"), method = "readAdditional")
	public void fh$readAdditional(CompoundTag compound, CallbackInfo cbi) {
		fh$data.deserialize(compound.getCompound("fhdata"));
	}
	@Inject(at=@At(value="INVOKE",target="Lnet/minecraft/entity/merchant/villager/VillagerEntity;resetCustomer()V",remap=true,ordinal=0),method="updateAITasks",cancellable=true,remap=true,require=1)
	public void fh$updateTask(CallbackInfo cbi) {
		super.updateAITasks();
		cbi.cancel();
	}
    /**
     * @author khjxiaogu
     * @reason disable villager trade for our system
     */
    @Overwrite
    public ActionResultType getEntityInteractionResult(PlayerEntity playerIn, Hand hand) {
        ItemStack itemstack = playerIn.getHeldItem(hand);
        if (itemstack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.hasCustomer()
                && !this.isSleeping() && !playerIn.isSecondaryUseActive()) {
            if (this.isChild()) {
                this.shakeHead();
                return ActionResultType.func_233537_a_(this.world.isRemote);
            }
			/*boolean flag = this.getOffers().isEmpty();
			if (hand == Hand.MAIN_HAND) {
				if (flag && !this.world.isRemote) {
					this.shakeHead();
				}

				playerIn.addStat(Stats.TALKED_TO_VILLAGER);
			}*/
			/*if (flag) {
				return ActionResultType.func_233537_a_(this.world.isRemote);
			}*/
            if (!this.world.isRemote) {
            	//return fh$data.trade(playerIn);
            	/*fh$data.update((ServerWorld) super.world, playerIn);
            	RelationList list=fh$data.getRelationShip(playerIn);
            	if(list.sum()<-30) {
	                this.shakeHead();
	                playerIn.sendMessage(GuiUtils.translateMessage("village.unknown"), playerIn.getUniqueID());
            	}*/
            	playerIn.addStat(Stats.TALKED_TO_VILLAGER);
            	setCustomer(playerIn);
            	//System.out.println(this.getCustomer());
            	TradeHandler.openTradeScreen((ServerPlayerEntity) playerIn, fh$data);

            }

            //System.out.println("sent");
			/*if (!this.world.isRemote && !this.offers.isEmpty()) {
				this.displayMerchantGui(playerIn);
			}*/

            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        return super.getEntityInteractionResult(playerIn, hand);
    }

	@Override
	public FHVillagerData getFHData() {
		return fh$data;
	}
}
