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

package com.teammoeg.frostedheart.content.steamenergy.sauna;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.teammoeg.frostedheart.FHBlocks;
import com.teammoeg.frostedheart.FHEffects;
import com.teammoeg.frostedheart.FHTileTypes;
import com.teammoeg.frostedheart.base.block.FHBlockInterfaces;
import com.teammoeg.frostedheart.client.util.ClientUtils;
import com.teammoeg.frostedheart.client.util.GuiUtils;
import com.teammoeg.frostedheart.climate.TemperatureCore;
import com.teammoeg.frostedheart.content.steamenergy.INetworkConsumer;
import com.teammoeg.frostedheart.content.steamenergy.SteamNetworkHolder;
import com.teammoeg.frostedheart.research.inspire.EnergyCore;
import com.teammoeg.frostedheart.util.mixin.IOwnerTile;

import blusunrize.immersiveengineering.common.blocks.IEBaseTileEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IInteractionObjectIE;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.FTBTeamsCommon;
import dev.ftb.mods.ftbteams.data.Team;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class SaunaTileEntity extends IEBaseTileEntity implements
        INetworkConsumer, TickableBlockEntity, FHBlockInterfaces.IActiveState, IIEInventory, IInteractionObjectIE {

    private static final float POWER_CAP = 400;
    private static final float REFILL_THRESHOLD = 200;
    private static final int RANGE = 5;
    private static final int WALL_HEIGHT = 3;
    private static final Direction[] HORIZONTALS =
            new Direction[] { Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH };

    private float power = 0;
    private int remainTime = 0;
    private int maxTime = 0;
    private MobEffect effect = null;
    private int effectDuration = 0;
    private int effectAmplifier = 0;
    private boolean refilling = false;
    private boolean formed = false;
    Set<BlockPos> floor = new HashSet<>();
    Set<BlockPos> edges = new HashSet<>();
    SteamNetworkHolder network = new SteamNetworkHolder();

    protected NonNullList<ItemStack> inventory;
    private LazyOptional<IItemHandler> insertionCap;

    public SaunaTileEntity() {
        super(FHTileTypes.SAUNA.get());
        this.inventory = NonNullList.withSize(1, ItemStack.EMPTY);
        this.insertionCap = LazyOptional.of(() -> new IEInventoryHandler(1, this));
    }

    public float getPowerFraction() {
        return power / POWER_CAP;
    }

    public boolean isWorking() {
        return formed && power > 0;
    }

    public boolean hasMedicine() {
        return remainTime > 0 && effect != null;
    }

    public MobEffectInstance getEffectInstance() {
        if (effect != null) {
            return new MobEffectInstance(effect, effectDuration, effectAmplifier, true, true);
        } else {
            return null;
        }
    }

    public float getEffectTimeFraction() {
        return (float) remainTime / (float) maxTime;
    }

    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
        power = nbt.getFloat("power");
        remainTime = nbt.getInt("time");
        maxTime = nbt.getInt("maxTime");
        if (nbt.contains("effect")) {
            effect = MobEffect.byId(nbt.getInt("effect"));
            effectDuration = nbt.getInt("duration");
            effectAmplifier = nbt.getInt("amplifier");
        } else {
            effect = null;
            effectDuration = 0;
            effectAmplifier = 0;
        }
        refilling = nbt.getBoolean("refilling");
        formed = nbt.getBoolean("formed");
        ListTag floorNBT = nbt.getList("floor", Constants.NBT.TAG_COMPOUND);
        floor.clear();
        for (int i = 0; i < floorNBT.size(); i++) {
            BlockPos pos = NbtUtils.readBlockPos(floorNBT.getCompound(i));
            floor.add(pos);
        }
        this.inventory = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.inventory);
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
        nbt.putFloat("power", power);
        nbt.putInt("time", remainTime);
        nbt.putInt("maxTime", maxTime);
        if (effect != null) {
            nbt.putInt("effect", MobEffect.getId(effect));
            nbt.putInt("effectDuration", effectDuration);
            nbt.putInt("effectAmplifier", effectAmplifier);
        } else {
            nbt.remove("effect");
            nbt.remove("effectDuration");
            nbt.remove("effectAmplifier");
        }
        nbt.putBoolean("refilling", refilling);
        nbt.putBoolean("formed", formed);
        ListTag floorNBT = new ListTag();
        for (BlockPos pos : floor) {
            CompoundTag posNBT = NbtUtils.writeBlockPos(pos);
            floorNBT.add(posNBT);
        }
        ContainerHelper.saveAllItems(nbt, this.inventory);
        nbt.put("floor", floorNBT);
    }

    @Override
    public boolean connect(Direction to, int dist) {
        return network.reciveConnection(level, worldPosition, to, dist);
    }

    @Override
    public boolean canConnectAt(Direction to) {
        return to == Direction.DOWN;
    }

    @Override
    public SteamNetworkHolder getHolder() {
        return network;
    }

    @Override
    public void tick() {
        // server side logic
        if (!level.isClientSide) {
            // power logic
            if (network.isValid()) {
                network.tick();
                // start refill if power is below REFILL_THRESHOLD
                // keep refill until network is full
                if (refilling || power < REFILL_THRESHOLD) {
                    float actual = network.drainHeat(Math.min(200, (POWER_CAP - power) / 0.8F));
                    // during refill, grow power and show steam
                    if (actual > 0) {
                        refilling = true;
                        power += actual * 0.8F;
                        this.setActive(true);
                    }
                    // finished refill, check structure, grant effects, happens every 200 ticks
                    else {
                        formed = structureIsValid();
                        refilling = false;
                        this.setActive(false);
                    }
                }
                // if not refilling, consume power
                else {
                    power--;
                }
                setChanged();
                this.markContainingBlockForUpdate(null);
            } else this.setActive(false);

            // grant player effect if structure is valid
            if (formed && power > 0) {
                // consume medcine time
                remainTime = Math.max(0, remainTime - 1);
                // refill time if medicine exists
                ItemStack medicine = this.inventory.get(0);
                if (remainTime == 0) {
                    effect = null;
                    effectDuration = 0;
                    effectAmplifier = 0;
                    if (!medicine.isEmpty()) {
                        SaunaRecipe recipe = SaunaRecipe.findRecipe(medicine);
                        if (recipe != null) {
                            maxTime = recipe.time;
                            remainTime += recipe.time;
                            medicine.shrink(1);
                            effect = recipe.effect;
                            effectDuration = recipe.duration;
                            effectAmplifier = recipe.amplifier;
                        }
                    }
                }

                setChanged();
                this.markContainingBlockForUpdate(null);

                for (Player p : this.getLevel().players()) {
                    if (floor.contains(p.blockPosition().below())||floor.contains(p.blockPosition())) {
                        grantEffects((ServerPlayer) p);
                    }
                }
            }
        }
        // client side render
        else if (getIsActive()) {
            ClientUtils.spawnSteamParticles(this.getLevel(), worldPosition);
        }
    }

    public InteractionResult onClick(Player player) {
        if (!player.level.isClientSide) {
            if (formed) {
                // player.sendStatusMessage(GuiUtils.translateMessage("structure_formed"), true);
                NetworkHooks.openGui((ServerPlayer) player, this, this.getBlockPos());
            } else {
                player.displayClientMessage(GuiUtils.translateMessage("structure_not_formed"), true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void grantEffects(ServerPlayer p) {
        // add effect only if armor is not equipped
        if (p.getArmorCoverPercentage() > 0.0F) {
            return;
        }
        UUID owner=IOwnerTile.getOwner(this);
        if(owner==null)return;
        Team t=FTBTeamsAPI.getPlayerTeam(p);
        if(t==null||!t.getId().equals(owner))return;
        // add wet effect
        if (level.getGameTime() % 200L == 0L) {
            p.addEffect(new MobEffectInstance(FHEffects.WET, 200, 0, true, false));
        }
        
        // add sauna effect
        if (level.getGameTime() % 1000L == 0L && !p.hasEffect(FHEffects.SAUNA)) {
            // initial reward
            EnergyCore.addEnergy(p, 1000);
            // whole day reward
            p.addEffect(new MobEffectInstance(FHEffects.SAUNA, 23000, 0, true, false));
        }
        // add temperature
        float lenvtemp = TemperatureCore.getEnvTemperature(p);//get a smooth change in display
        float lbodytemp = TemperatureCore.getBodyTemperature(p);
        TemperatureCore.setTemperature(p, 1.01f * .01f + lbodytemp * .99f, 65 * .1f + lenvtemp * .9f);
        // add medical effect
        if (hasMedicine() && remainTime == 1) {
            p.addEffect(getEffectInstance());
        }
    }

    private boolean dist(BlockPos crn, BlockPos orig) {
        return Mth.abs(crn.getX() - orig.getX()) <= RANGE && Mth.abs(crn.getZ() - orig.getZ()) <= RANGE;
    }

    private void findNext(Level l, BlockPos crn, BlockPos orig, Set<BlockPos> poss, Set<BlockPos> edges) {
        if (dist(crn, orig)) {
            if (poss.add(crn)) {
                for (Direction dir : HORIZONTALS) {
                    BlockPos act = crn.relative(dir);
                    // if crn connected to plank
                    if (l.isLoaded(act) && (l.getBlockState(act).is(BlockTags.PLANKS) || l.getBlockState(act).getBlock().is(FHBlocks.sauna))) {
                        findNext(l, act, orig, poss, edges);
                    }
                    // otherwise, crn is an edge block
                    else {
                        edges.add(crn);
                    }
                }
            }
        }
    }

    private boolean structureIsValid() {
        floor.clear();
        edges.clear();
        // collect connected floor and edges
        findNext(this.getLevel(), this.getBlockPos(), this.getBlockPos(), floor, edges);
        // check wall exist for each edge block
        for (BlockPos pos : edges) {
            for (int y = 1; y <= WALL_HEIGHT; y++) {
                BlockState wall = level.getBlockState(pos.relative(Direction.UP, y));
                if (!wall.is(BlockTags.PLANKS) && !wall.is(BlockTags.DOORS)) {
                    return false;
                }
            }
            // remove edges from the actual floor player can stand on, since play can't stand on wall
            floor.remove(pos);
        }
        // check ceiling exist for each floor block
        for (BlockPos pos : floor) {
            BlockState ceiling = level.getBlockState(pos.relative(Direction.UP, WALL_HEIGHT));
            if (!ceiling.is(BlockTags.PLANKS) && !ceiling.is(BlockTags.TRAPDOORS)) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack itemStack) {
        return true;
    }

    @Override
    public int getSlotLimit(int i) {
        return 64;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.insertionCap.cast() : super.getCapability(capability, facing);
    }

    @Nullable
    @Override
    public IInteractionObjectIE getGuiMaster() {
        return this;
    }

    @Override
    public boolean canUseGui(Player playerEntity) {
        return true;
    }

    @Override
    public void receiveMessageFromServer(CompoundTag message) {
        super.receiveMessageFromServer(message);
    }
}
