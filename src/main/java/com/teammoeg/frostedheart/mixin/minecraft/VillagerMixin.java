/*
 * Copyright (c) 2022 TeamMoeg
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

import com.teammoeg.frostedheart.client.util.GuiUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VillagerEntity.class)
public abstract class VillagerMixin extends AbstractVillagerEntity {
    public VillagerMixin(EntityType<? extends AbstractVillagerEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Shadow
    protected abstract void shakeHead();

    @Shadow
    protected abstract void displayMerchantGui(PlayerEntity pe);


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
                this.shakeHead();
                playerIn.sendMessage(GuiUtils.translateMessage("village.unknown"), playerIn.getUniqueID());

            }

            //System.out.println("sent");
			/*if (!this.world.isRemote && !this.offers.isEmpty()) {
				this.displayMerchantGui(playerIn);
			}*/

            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        return super.getEntityInteractionResult(playerIn, hand);
    }
}
