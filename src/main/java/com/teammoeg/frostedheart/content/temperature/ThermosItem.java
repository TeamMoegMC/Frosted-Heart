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

package com.teammoeg.frostedheart.content.temperature;

import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.content.contraptions.fluids.potion.PotionFluidHandler;
import com.teammoeg.frostedheart.FHContent;
import com.teammoeg.frostedheart.FHMain;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.climate.ITempAdjustFood;
import com.teammoeg.frostedheart.climate.data.FHDataManager;
import com.teammoeg.frostedheart.util.FHUtils;

import blusunrize.immersiveengineering.common.util.fluids.PotionFluid;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import net.minecraft.world.item.Item.Properties;

public class ThermosItem extends ItemFluidContainer implements ITempAdjustFood {
    final int unit;
    final boolean doAddItems;
    final String lang;
    public ThermosItem(String name,String lang, int capacity, int unit,boolean add) {
        super(new Properties().stacksTo(1).setNoRepair().durability(capacity).tab(FHMain.itemGroup).food(new FoodProperties.Builder().nutrition(1).saturationMod(1).build()), capacity);
        this.unit = unit;
        setRegistryName(FHMain.MODID, name);
        FHContent.registeredFHItems.add(this);
        doAddItems=add;
        this.lang=lang;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public int getUseDuration(ItemStack stack) {
        return canDrink(stack) ? 40 : 0;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return canDrink(stack) ? UseAnim.DRINK : UseAnim.NONE;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        Player entityplayer = entityLiving instanceof Player ? (Player) entityLiving : null;
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(data -> {
            FluidStack fs = data.drain(unit, IFluidHandler.FluidAction.EXECUTE);
            if (entityplayer != null) {
                entityplayer.awardStat(Stats.ITEM_USED.get(this));
                Fluid f = fs.getFluid();
                if (f instanceof com.simibubi.create.content.contraptions.fluids.potion.PotionFluid) {
                    for (MobEffectInstance ei : PotionUtils.getAllEffects(fs.getOrCreateTag()))
                    	FHUtils.applyEffectTo(ei, entityplayer);
                } else if (f instanceof PotionFluid) {
                    for (MobEffectInstance ei : PotionFluid.getType(fs).getEffects())
                    	FHUtils.applyEffectTo(ei, entityplayer);
                }
            }
        });

        updateDamage(stack);
        return stack;
    }

    public void updateDamage(ItemStack stack) {
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(data -> {
            int i = this.capacity - data.getFluidInTank(0).getAmount() >= 0 ? this.capacity - data.getFluidInTank(0).getAmount() : 0;
            stack.setDamageValue(i);
        });
    }

    public int getUnit() {
        return unit;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        updateDamage(stack);
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        HitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.SOURCE_ONLY);
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (raytraceresult.getType() == HitResult.Type.MISS) {
            playerIn.startUsingItem(handIn);
            return canDrink(playerIn.getItemInHand(handIn)) ? InteractionResultHolder.success(playerIn.getItemInHand(handIn)) : InteractionResultHolder.fail(playerIn.getItemInHand(handIn));
        }
        if (raytraceresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult) raytraceresult).getBlockPos();
            if (worldIn.getFluidState(blockpos).is(FluidTags.WATER)) {
                if (canFill(itemstack, Fluids.WATER)) {
                    worldIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    itemstack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(data -> {
                        data.fill(new FluidStack(Fluids.WATER, data.getTankCapacity(0)), IFluidHandler.FluidAction.EXECUTE);
                    });

                    return InteractionResultHolder.success(itemstack);
                }
            }
            playerIn.startUsingItem(handIn);
            return canDrink(playerIn.getItemInHand(handIn)) ? InteractionResultHolder.success(playerIn.getItemInHand(handIn)) : InteractionResultHolder.fail(playerIn.getItemInHand(handIn));
        }
        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
        	if(!doAddItems) {
        		super.fillItemCategory(group, items);
        		return;
        	}
            Tag<Fluid> tag = FluidTags.getAllTags().getTag(new ResourceLocation(FHMain.MODID, "drink"));
            ResourceLocation hidden = new ResourceLocation(FHMain.MODID, "hidden_drink");
            items.add(new ItemStack(this));
            if (tag == null) return;
            for (Fluid fluid : tag.getValues()) {
                if (fluid.getTags().contains(hidden)) continue;
                ItemStack itemStack = new ItemStack(this);
                itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(data -> {
                    data.fill(new FluidStack(fluid, data.getTankCapacity(0)), IFluidHandler.FluidAction.EXECUTE);
                });
                items.add(itemStack);
            }
        }
    }


    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    public Item getEmptyContainer() {
        return this;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidHandlerItemStack(stack, capacity) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return isFluidValid(0, fluid);
            }

            @Nonnull
            @Override
            public ItemStack getContainer() {
                return getFluid().isEmpty() ? new ItemStack(getEmptyContainer()) : this.container;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (stack.getFluid() instanceof PotionFluid || stack.getFluid() instanceof com.simibubi.create.content.contraptions.fluids.potion.PotionFluid) {
                    return true;
                }
                for (Fluid fluid : FluidTags.getAllTags().getTag(new ResourceLocation(FHMain.MODID, "drink")).getValues()) {
                    if (fluid == stack.getFluid()) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        ItemStack itemStack1 = itemStack.copy();
        itemStack1.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(data -> {
            FluidStack fs = data.drain(unit, IFluidHandler.FluidAction.EXECUTE);
        });


        return itemStack1;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(GuiUtils.translateTooltip("meme.thermos").withStyle(ChatFormatting.GRAY));

        if (stack.getTagElement(FLUID_NBT_KEY) != null) {
            FluidUtil.getFluidHandler(stack).ifPresent(f -> {
                tooltip.add(((BaseComponent) f.getFluidInTank(0).getDisplayName()).append(String.format(": %d / %dmB", f.getFluidInTank(0).getAmount(), capacity)).withStyle(ChatFormatting.GRAY));
                FluidStack fs = f.getFluidInTank(0);
                Fluid ft = fs.getFluid();
                if (ft instanceof com.simibubi.create.content.contraptions.fluids.potion.PotionFluid)
                    PotionFluidHandler.addPotionTooltip(fs, tooltip, 1);
                else if (ft instanceof PotionFluid)
                    ((PotionFluid) ft).addInformation(fs, tooltip);
                tooltip.add(new TranslatableComponent("tooltip.watersource.drink_unit").append(" : " + this.getUnit() + "mB").withStyle(ChatFormatting.GRAY));
            });
        }
    }

    public boolean canDrink(ItemStack is) {
        LazyOptional<IFluidHandlerItem> ih = FluidUtil.getFluidHandler(is);
        if (ih.isPresent())
            return (ih.resolve().get().getFluidInTank(0).getAmount() >= unit);
        return false;
    }

    public boolean canFill(ItemStack is, Fluid f) {
        LazyOptional<IFluidHandlerItem> ih = FluidUtil.getFluidHandler(is);
        if (ih.isPresent()) {
            IFluidHandlerItem ihr = ih.resolve().get();
            return ihr.getFluidInTank(0).isEmpty() || ihr.getFluidInTank(0).getFluid().isSame(f);
        }
        return false;
    }

    @Override
    public float getHeat(ItemStack is,float env) {
        LazyOptional<IFluidHandlerItem> ih = FluidUtil.getFluidHandler(is);
        if (ih.isPresent()) {
            IFluidHandlerItem f = ih.resolve().get();
            FluidStack fs = f.getFluidInTank(0);
            if (!fs.isEmpty()) {
                return FHDataManager.getDrinkHeat(fs);
            }
        }
        ;
        return 0;
    }

    @Override
    public float getMaxTemp(ItemStack is) {
        return 1;
    }

    @Override
    public float getMinTemp(ItemStack is) {
        return -1;
    }

	@Override
	public String getDescriptionId() {
		return lang;
	}


}
