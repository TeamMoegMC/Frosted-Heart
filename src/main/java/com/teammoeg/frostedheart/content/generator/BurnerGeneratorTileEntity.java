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

package com.teammoeg.frostedheart.content.generator;

import java.util.Random;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.teammoeg.frostedheart.base.block.FHBlockInterfaces;
import com.teammoeg.frostedheart.client.util.ClientUtils;
import com.teammoeg.frostedheart.research.data.ResearchVariant;
import com.teammoeg.frostedheart.util.ReferenceValue;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.core.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class BurnerGeneratorTileEntity<T extends BurnerGeneratorTileEntity<T>> extends AbstractGenerator<T> implements IIEInventory,
        FHBlockInterfaces.IActiveState, IEBlockInterfaces.IInteractionObjectIE, IEBlockInterfaces.IProcessTile, IEBlockInterfaces.IBlockBounds {

    @Override
    public boolean shouldUnique() {
        return true;
    }

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public int process = 0;
    public int processMax = 0;
    
    protected NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    protected ItemStack currentItem;

    public class GeneratorData implements ContainerData {
        public static final int MAX_BURN_TIME = 0;
        public static final int BURN_TIME = 1;

        @Override
        public int get(int index) {
            switch (index) {
                case MAX_BURN_TIME:
                    return processMax;
                case BURN_TIME:
                    return process;
                default:
                    throw new IllegalArgumentException("Unknown index " + index);
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case MAX_BURN_TIME:
                    processMax = value;
                    break;
                case BURN_TIME:
                    process = value;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown index " + index);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public BurnerGeneratorTileEntity(IETemplateMultiblock multiblockInstance, BlockEntityType<T> type, boolean hasRSControl, int temperatureLevelIn, int overdriveBoostIn, int rangeLevelIn) {
        super(multiblockInstance, type, hasRSControl);
        temperatureLevel = temperatureLevelIn;
        rangeLevel = rangeLevelIn;
        overdriveBoost = overdriveBoostIn;
    }

    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        process = nbt.getInt("process");
        processMax = nbt.getInt("processMax");
        
        if (!descPacket) {
            
            currentItem = ItemStack.of(nbt.getCompound("currentItem"));
            ContainerHelper.loadAllItems(nbt, inventory);
        }
        
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.putInt("process", process);
        nbt.putInt("processMax", processMax);
        
        if (!descPacket) {
            if (currentItem != null)
                nbt.put("current", currentItem.serializeNBT());
            else
                nbt.remove("current");
            ContainerHelper.saveAllItems(nbt, inventory);
        }
        
    }

    @Nonnull
    @Override
    public VoxelShape getBlockBounds(@Nullable CollisionContext ctx) {
        return Shapes.block();
    }

    @Override
    public boolean triggerEvent(int id, int arg) {
        if (id == 0)
            this.formed = arg == 1;
        setChanged();
        this.markContainingBlockForUpdate(null);
        return true;
    }

    @Override
    public void receiveMessageFromClient(CompoundTag message) {
        super.receiveMessageFromClient(message);
        if (message.contains("isWorking", Constants.NBT.TAG_BYTE))
            setWorking(message.getBoolean("isWorking"));
        if (message.contains("isOverdrive", Constants.NBT.TAG_BYTE))
            setOverdrive(message.getBoolean("isOverdrive"));
       /* if (message.contains("temperatureLevel", Constants.NBT.TAG_INT))
            setTemperatureLevel(message.getInt("temperatureLevel"));
        if (message.contains("rangeLevel", Constants.NBT.TAG_INT))
            setRangeLevel(message.getInt("rangeLevel"));*/
    }

    @Nonnull
    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
        return new IFluidTank[0];
    }

    @Override
    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource) {
        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int iTank, Direction side) {
        return false;
    }

    @Nullable
    @Override
    public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
        return master();
    }

    @Override
    public boolean canUseGui(Player player) {
        return formed;
    }

    @Override
    public int[] getCurrentProcessesStep() {
        T master = master();
        if (master != this && master != null)
            return master.getCurrentProcessesStep();
        return new int[]{processMax - process};
    }

    @Override
    public int[] getCurrentProcessesMax() {
        T master = master();
        if (master != this && master != null)
            return master.getCurrentProcessesMax();
        return new int[]{processMax};
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        T master = master();
        if (master != null)
            return master.inventory;
        return this.inventory;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack) {
        if (stack.isEmpty())
            return false;
        if (slot == INPUT_SLOT)
            return GeneratorRecipe.findRecipe(stack) != null;
        return false;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    LazyOptional<IItemHandler> invHandler = registerConstantCap(
            new IEInventoryHandler(2, this, 0, new boolean[]{true, false},
                    new boolean[]{false, true})
    );

    @Nonnull
    @Override
    public <X> LazyOptional<X> getCapability(@Nonnull Capability<X> capability, Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            T master = master();
            if (master != null)
                return master.invHandler.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Nullable
    public GeneratorRecipe getRecipe() {
        if (inventory.get(INPUT_SLOT).isEmpty())
            return null;
        GeneratorRecipe recipe = GeneratorRecipe.findRecipe(inventory.get(INPUT_SLOT));
        if (recipe == null)
            return null;
        if (inventory.get(OUTPUT_SLOT).isEmpty() || (ItemStack.isSame(inventory.get(OUTPUT_SLOT), recipe.output) &&
                inventory.get(OUTPUT_SLOT).getCount() + recipe.output.getCount() <= getSlotLimit(OUTPUT_SLOT))) {
            return recipe;
        }
        return null;
    }

    @Override
    public void onShutDown() {
        process = 0;
        processMax = 0;

        if (currentItem != null) {
            if (!inventory.get(OUTPUT_SLOT).isEmpty())
                inventory.get(OUTPUT_SLOT).grow(currentItem.getCount());
            else
                inventory.set(OUTPUT_SLOT, currentItem);
            currentItem = null;
        }
    }
    protected double getEfficiency() {
    	ReferenceValue<Double> eff=new ReferenceValue<>(0.7);
        getTeamData().ifPresent(t->{
        	eff.map(n->n+t.getVariantDouble(ResearchVariant.GENERATOR_EFFICIENCY));
        });
        return eff.getVal();
    }
    @Override
    protected void tickFuel() {
        // just finished process or during process
    	
        if (process > 0) {
            if (isOverdrive() && !isActualOverdrive()) {
                GeneratorRecipe recipe = getRecipe();
                if (recipe != null) {
                    int count = recipe.input.getCount();
                    if (inventory.get(INPUT_SLOT).getCount() >= 4 * count) {
                        Utils.modifyInvStackSize(inventory, INPUT_SLOT, -4 * count);
                        if (currentItem != null) {
                            if (!inventory.get(OUTPUT_SLOT).isEmpty())
                                inventory.get(OUTPUT_SLOT).grow(currentItem.getCount());
                            else
                                inventory.set(OUTPUT_SLOT, currentItem);
                            currentItem = null;
                        }
                        double effi=getEfficiency();
                        currentItem = recipe.output.copy();
                        currentItem.setCount(4 * currentItem.getCount());
                        this.process += (int)(recipe.time*effi) * 4;
                        this.processMax += (int)(recipe.time*effi) * 4;
                        setActualOverdrive(true);
                    }
                }
            }
            if (isActualOverdrive())
                process -= 4;
            else
                process--;
            this.setActive(true);
            this.setChanged();
            this.markContainingBlockForUpdate(null);
        }
        // process not started yet
        else {
            if (currentItem != null) {
                if (!inventory.get(OUTPUT_SLOT).isEmpty())
                    inventory.get(OUTPUT_SLOT).grow(currentItem.getCount());
                else
                    inventory.set(OUTPUT_SLOT, currentItem);
                currentItem = null;
            }
            GeneratorRecipe recipe = getRecipe();
            if (recipe != null) {
                int modifier = 1;
                if (isOverdrive() && inventory.get(INPUT_SLOT).getCount() >= 4 * recipe.input.getCount()) {
                    if (!isActualOverdrive())
                        this.setActualOverdrive(true);
                    modifier = 4;
                } else if (isActualOverdrive()) {
                    this.setActualOverdrive(false);
                }
                int count = recipe.input.getCount() * modifier;
                Utils.modifyInvStackSize(inventory, INPUT_SLOT, -count);
                currentItem = recipe.output.copy();
                currentItem.setCount(currentItem.getCount() * modifier);
                double effi=getEfficiency();
                this.process = (int)(recipe.time*effi) * modifier;
                this.processMax = process;
                setActive(true);
                this.setChanged();
                markContainingBlockForUpdate(null);
            } else {
            	if(this.processMax!=0) {
	                this.process = 0;
	                processMax = 0;
	                setActive(false);
	                this.setChanged();
	                markContainingBlockForUpdate(null);
            	}
            }
        }
    }


    @Override
    protected void tickEffects(boolean isActive) {

    }

	@Override
	public void tick() {
		
		super.tick();
		
	}

	@Override
	public void forEachBlock(Consumer<T> consumer) {
	}



}
