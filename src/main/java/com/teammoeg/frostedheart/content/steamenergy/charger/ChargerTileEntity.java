/*
 * Copyright (c) 2021 TeamMoeg
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
 */

package com.teammoeg.frostedheart.content.steamenergy.charger;

import java.util.Collection;
import java.util.List;

import com.teammoeg.frostedheart.FHTileTypes;
import com.teammoeg.frostedheart.base.block.FHBlockInterfaces;
import com.teammoeg.frostedheart.client.util.ClientUtils;
import com.teammoeg.frostedheart.content.recipes.CampfireDefrostRecipe;
import com.teammoeg.frostedheart.content.steamenergy.IChargable;
import com.teammoeg.frostedheart.content.steamenergy.INetworkConsumer;
import com.teammoeg.frostedheart.content.steamenergy.SteamNetworkHolder;
import com.teammoeg.frostedheart.util.FHUtils;

import blusunrize.immersiveengineering.common.blocks.IEBaseTileEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmokingRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ChargerTileEntity extends IEBaseTileEntity implements
        INetworkConsumer, ITickableTileEntity, FHBlockInterfaces.IActiveState {
    public float power = 0;
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    public ChargerTileEntity() {
        super(FHTileTypes.CHARGER.get());
    }

    SteamNetworkHolder network = new SteamNetworkHolder();


    public ActionResultType onClick(PlayerEntity pe, ItemStack is) {
        if (is != null) {
            Item it = is.getItem();
            if (it instanceof IChargable) {
                power -= ((IChargable) it).charge(is, power);
                drawEffect();
                return ActionResultType.SUCCESS;
            }
            ChargerRecipe cr = ChargerRecipe.findRecipe(is);
            if (cr != null) {
                if (power >= cr.cost && is.getCount() >= cr.input.getCount()) {
                    if (!world.isRemote) {
                        power -= cr.cost;
                        is.setCount(is.getCount() - cr.input.getCount());
                        ItemStack gain = cr.output.copy();
                        FHUtils.giveItem(pe, gain);
                        markDirty();
                        this.markContainingBlockForUpdate(null);
                    }
                    drawEffect();
                    return ActionResultType.SUCCESS;
                }
            }

            if (power >= 100) {
                List<SmokingRecipe> irs = this.world.getRecipeManager().getRecipesForType(IRecipeType.SMOKING);
                for (SmokingRecipe sr : irs) {
                    if (sr.getIngredients().iterator().next().test(is)) {
                        if (!world.isRemote) {
                            power -= sr.getCookTime() / 20;
                            splitAndSpawnExperience(pe.getEntityWorld(), pe.getPosition(), sr.getExperience());
                            is.setCount(is.getCount() - 1);
                            ItemStack gain = sr.getCraftingResult(null).copy();
                            FHUtils.giveItem(pe, gain);
                            markDirty();
                            this.markContainingBlockForUpdate(null);
                        }
                        drawEffect();
                        return ActionResultType.SUCCESS;
                    }
                }
            }
            {
                Collection<CampfireDefrostRecipe> irs = CampfireDefrostRecipe.recipeList.values();
                for (CampfireDefrostRecipe sr : irs) {
                    if (sr.getIngredient().test(is)) {
                        if (!world.isRemote) {
                            power -= sr.getCookTime() / 80;
                            splitAndSpawnExperience(pe.getEntityWorld(), pe.getPosition(), sr.getExperience());
                            is.setCount(is.getCount() - 1);
                            ItemStack gain = sr.getCraftingResult(null).copy();
                            FHUtils.giveItem(pe, gain);
                            markDirty();
                            this.markContainingBlockForUpdate(null);
                        }
                        drawEffect();
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        }
        return ActionResultType.PASS;
    }

    private static void splitAndSpawnExperience(World world, BlockPos pos, float experience) {
        int i = MathHelper.floor(experience);
        float f = MathHelper.frac(experience);
        if (f != 0.0F && Math.random() < f) {
            ++i;
        }

        while (i > 0) {
            int j = ExperienceOrbEntity.getXPSplit(i);
            i -= j;
            world.addEntity(new ExperienceOrbEntity(world, pos.getX(), pos.getY(), pos.getZ(), j));
        }

    }

    public void drawEffect() {
        if (world != null && world.isRemote) {
            ClientUtils.spawnSteamParticles(world, this.getPos());
        }
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        power = nbt.getFloat("power");
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        nbt.putFloat("power", power);
    }

    @Override
    public boolean connect(Direction to, int dist) {

        return network.reciveConnection(world, pos, to, dist);
    }


    public float getMaxPower() {
        return 20000F;
    }

    public Direction getDirection() {
        return this.getBlockState().get(BlockStateProperties.FACING);
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (network.isValid()) {
                network.tick();
                float actual = network.drainHeat(Math.min(200, (getMaxPower() - power) / 0.8F));
                if (actual > 0) {
                    power += actual * 0.8;
                    this.setActive(true);
                    markDirty();
                    this.markContainingBlockForUpdate(null);
                } else
                    this.setActive(false);
            } else this.setActive(false);
        } else if (getIsActive()) {
            ClientUtils.spawnSteamParticles(this.getWorld(), pos);
        }
    }

    @Override
    public boolean canConnectAt(Direction dir) {
        Direction bd = this.getBlockState().get(BlockStateProperties.FACING);
        return dir == bd || (bd != Direction.DOWN && dir == Direction.DOWN) || (bd == Direction.UP && dir == Direction.NORTH) || (bd == Direction.DOWN && dir == Direction.SOUTH);
    }

    @Override
    public SteamNetworkHolder getHolder() {
        return network;
    }
}
