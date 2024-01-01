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

package com.teammoeg.frostedheart.climate.chunkdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import com.teammoeg.frostedheart.climate.WorldClimate;
import com.teammoeg.frostedheart.crash.ClimateCrash;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ChunkData implements ICapabilitySerializable<CompoundTag> {
    public static final ChunkData EMPTY = new Immutable();

    public static ChunkData get(LevelAccessor world, BlockPos pos) {
        return get(world, new ChunkPos(pos));
    }

    /**
     * Called to get temperature when a world context is available.
     * on server, will either query capability falling back to cache, or query
     * provider to generate the data.
     * This method directly get temperature at any positions.
     */
    public static float getTemperature(LevelReader world, BlockPos pos) {
        return get(world, new ChunkPos(pos)).getTemperatureAtBlock(world, pos);
    }

    /**
     * Called to get temperature adjusts at location when a world context is available.
     * on server, will either query capability falling back to cache, or query
     * provider to generate the data.
     * This method directly get temperature adjusts at any positions.
     */
    public static Collection<ITemperatureAdjust> getAdjust(LevelReader world, BlockPos pos) {
        ArrayList<ITemperatureAdjust> al = new ArrayList<>(get(world, new ChunkPos(pos)).getAdjusters());
        al.removeIf(adj -> !adj.isEffective(pos));
        return al;
    }

    /**
     * Called to get chunk data when a world context is available.
     * If on client, will query capability, falling back to cache, and send request
     * packets if necessary
     * If on server, will either query capability falling back to cache, or query
     * provider to generate the data.
     */
    public static ChunkData get(LevelReader world, ChunkPos pos) {
        // Query cache first, picking the correct cache for the current logical side
        //ChunkData data = ChunkDataCache.get(world).get(pos);
        //if (data == null) {
        //System.out.println("no cache at"+pos);
        if (world instanceof LevelAccessor)
            return ((LevelAccessor) world).getChunkSource().isEntityTickingChunk(pos) ? getCapability(world.getChunk(pos.getWorldPosition()))
                    .orElse(ChunkData.EMPTY) : ChunkData.EMPTY;
        return world.hasChunk(pos.x, pos.z) ? getCapability(world.getChunk(pos.getWorldPosition()))
                .orElse(ChunkData.EMPTY) : ChunkData.EMPTY;
        //}
        //return data;
    }

    /**
     * Helper method, since lazy optionals and instanceof checks together are ugly
     */
    public static LazyOptional<ChunkData> getCapability(@Nullable ChunkAccess chunk) {
        if (chunk instanceof LevelChunk) {
            return ((LevelChunk) chunk).getCapability(ChunkDataCapabilityProvider.CAPABILITY);
        }
        return LazyOptional.empty();
    }

    /**
     * Used on a ServerWorld context to add temperature in certain 3D region in a
     * ChunkData instance
     * Updates server side cache first.
     */
    private static void addChunkAdjust(LevelAccessor world, ChunkPos chunkPos, ITemperatureAdjust adjx) {
        if (world != null && !world.isClientSide()) {
            ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z);
            ChunkData data = ChunkData.getCapability(chunk).orElseGet(() -> null);
            if (data != null) {
                data.adjusters.removeIf(adj -> adj.getCenterX() == adjx.getCenterX()
                        && adj.getCenterY() == adjx.getCenterY() && adj.getCenterZ() == adjx.getCenterZ());
                data.adjusters.add(adjx);
            }
        }
    }

    /**
     * Used on a ServerWorld context to set temperature in certain 3D region in a
     * ChunkData instance
     * Updates server side cache first. Then send a sync packet to every client.
     */
    private static void removeChunkAdjust(LevelAccessor world, ChunkPos chunkPos, BlockPos src) {
        if (world != null && !world.isClientSide()) {
            ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z);
            ChunkData data = ChunkData.getCapability(chunk).orElseGet(() -> null);
            if (data != null)
                data.adjusters.removeIf(adj -> adj.getCenterX() == src.getX() && adj.getCenterY() == src.getY()
                        && adj.getCenterZ() == src.getZ());
        }
    }

    /**
     * Used on a ServerWorld context to set temperature in certain 3D region in a
     * ChunkData instance
     * Updates server side cache first. Then send a sync packet to every client.
     */
    private static void removeChunkAdjust(LevelAccessor world, ChunkPos chunkPos, ITemperatureAdjust adj) {
        if (world != null && !world.isClientSide()) {
            ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z);
            ChunkData data = ChunkData.getCapability(chunk).orElseGet(() -> null);
            if (data != null)
                data.adjusters.remove(adj);
        }
    }

    /**
     * Used on a ServerWorld context to add temperature in a cubic region
     *
     * @param world   must be server side
     * @param heatPos the position of the heating block, at the center of the cube
     * @param range   the distance from the heatPos to the boundary
     * @param tempMod the temperature added
     * @deprecated use {@link addCubicTempAdjust}
     */
    @Deprecated
    public static void addTempToCube(LevelAccessor world, BlockPos heatPos, int range, byte tempMod) {
        addCubicTempAdjust(world, heatPos, range, tempMod);
    }

    /**
     * Used on a ServerWorld context to add temperature in a cubic region
     *
     * @param world   must be server side
     * @param heatPos the position of the heating block, at the center of the cube
     * @param range   the distance from the heatPos to the boundary
     * @param tempMod the temperature added
     */
    public static void addCubicTempAdjust(LevelAccessor world, BlockPos heatPos, int range, int tempMod) {
        removeTempAdjust(world, heatPos);//remove current first
        int sourceX = heatPos.getX(), sourceZ = heatPos.getZ();

        // these are block position offset
        int offsetN = sourceZ - range;
        int offsetS = sourceZ + range + 1;
        int offsetW = sourceX - range;
        int offsetE = sourceX + range + 1;

        // these are chunk position offset
        int chunkOffsetW = offsetW >>4;
        int chunkOffsetE = offsetE >>4;
        int chunkOffsetN = offsetN >>4;
        int chunkOffsetS = offsetS >>4;
        // add adjust to effected chunks
        ITemperatureAdjust adj = new CubicTemperatureAdjust(heatPos, range, tempMod);
        for (int x = chunkOffsetW; x <= chunkOffsetE; x++)
            for (int z = chunkOffsetN; z <= chunkOffsetS; z++) {
                ChunkPos cp = new ChunkPos(x, z);
                addChunkAdjust(world, cp, adj);
            }
    }

    /**
     * Used on a ServerWorld context to add temperature adjust.
     *
     * @param world must be server side
     * @param adj   adjust
     */
    public static void addTempAdjust(LevelAccessor world, ITemperatureAdjust adj) {

        int sourceX = adj.getCenterX(), sourceZ = adj.getCenterZ();
        removeTempAdjust(world, new BlockPos(sourceX, adj.getCenterY(), sourceZ));
        // these are block position offset
        int offsetN = sourceZ - adj.getRadius();
        int offsetS = sourceZ + adj.getRadius() + 1;
        int offsetW = sourceX - adj.getRadius();
        int offsetE = sourceX + adj.getRadius() + 1;

        // these are chunk position offset
        int chunkOffsetW = offsetW >>4;
        int chunkOffsetE = offsetE >>4;
        int chunkOffsetN = offsetN >>4;
        int chunkOffsetS = offsetS >>4;
        // add adjust to effected chunks
        for (int x = chunkOffsetW; x <= chunkOffsetE; x++)
            for (int z = chunkOffsetN; z <= chunkOffsetS; z++) {
                ChunkPos cp = new ChunkPos(x, z);
                addChunkAdjust(world, cp, adj);
            }
    }

    /**
     * Used on a ServerWorld context to add temperature in a piller region
     *
     * @param world   must be server side
     * @param heatPos the position of the heating block, at the center of the cube
     * @param range   the distance from the heatPos to the boundary
     * @param up y range above the plane
     * @param down y range below the plane
     * @param tempMod the temperature added
     */
    public static void addPillarTempAdjust(LevelAccessor world, BlockPos heatPos, int range,int up,int down, int tempMod) {
        removeTempAdjust(world, heatPos);
        int sourceX = heatPos.getX(), sourceZ = heatPos.getZ();

        // these are block position offset
        int offsetN = sourceZ - range;
        int offsetS = sourceZ + range + 1;
        int offsetW = sourceX - range;
        int offsetE = sourceX + range + 1;

        // these are chunk position offset
        int chunkOffsetW = offsetW >>4;
        int chunkOffsetE = offsetE >>4;
        int chunkOffsetN = offsetN >>4;
        int chunkOffsetS = offsetS >>4;
        // add adjust to effected chunks
        ITemperatureAdjust adj = new PillerTemperatureAdjust(heatPos, range, up, down, tempMod);
        for (int x = chunkOffsetW; x <= chunkOffsetE; x++)
            for (int z = chunkOffsetN; z <= chunkOffsetS; z++) {
                ChunkPos cp = new ChunkPos(x, z);
                addChunkAdjust(world, cp, adj);
            }
    }

    /**
     * Used on a ServerWorld context to reset a temperature area
     *
     * @param world   must be server side
     * @param heatPos the position of the heating block, at the center of the cube
     * @deprecated use {@link removeTempAdjust}
     */
    @Deprecated
    public static void resetTempToCube(LevelAccessor world, BlockPos heatPos) {
        removeTempAdjust(world, heatPos);
    }

    /**
     * Used on a ServerWorld context to reset a temperature area
     *
     * @param world   must be server side
     * @param heatPos the position of the heating block, at the center of the area
     */
    public static void removeTempAdjust(LevelAccessor world, BlockPos heatPos) {
        int sourceX = heatPos.getX(), sourceZ = heatPos.getZ();
        ChunkData cd = ChunkData.get(world, heatPos);
        ITemperatureAdjust oadj = cd.getAdjustAt(heatPos);
        if (oadj == null) return;
        int range = oadj.getRadius();

        // these are block position offset
        int offsetN = sourceZ - range;
        int offsetS = sourceZ + range + 1;
        int offsetW = sourceX - range;
        int offsetE = sourceX + range + 1;

        // these are chunk position offset
        int chunkOffsetW = offsetW >>4;
        int chunkOffsetE = offsetE >>4;
        int chunkOffsetN = offsetN >>4;
        int chunkOffsetS = offsetS >>4;

        for (int x = chunkOffsetW; x <= chunkOffsetE; x++)
            for (int z = chunkOffsetN; z <= chunkOffsetS; z++)
                removeChunkAdjust(world, new ChunkPos(x, z), heatPos);
    }

    /**
     * Used on a ServerWorld context to remove a temperature area
     *
     * @param world must be server side
     * @param adj   adjust
     */
    public static void removeTempAdjust(LevelAccessor world, ITemperatureAdjust adj) {
        int sourceX = adj.getCenterX(), sourceZ = adj.getCenterZ();

        // these are block position offset
        int offsetN = sourceZ - adj.getRadius();
        int offsetS = sourceZ + adj.getRadius() + 1;
        int offsetW = sourceX - adj.getRadius();
        int offsetE = sourceX + adj.getRadius() + 1;

        // these are chunk position offset
        int chunkOffsetW = offsetW >>4;
        int chunkOffsetE = offsetE >>4;
        int chunkOffsetN = offsetN >>4;
        int chunkOffsetS = offsetS >>4;
        for (int x = chunkOffsetW; x <= chunkOffsetE; x++)
            for (int z = chunkOffsetN; z <= chunkOffsetS; z++)
                removeChunkAdjust(world, new ChunkPos(x, z), adj);
    }

    private final LazyOptional<ChunkData> capability;
    private final ChunkPos pos;

    private List<ITemperatureAdjust> adjusters = new LinkedList<>();

    public ChunkData(ChunkPos pos) {
        this.pos = pos;
        this.capability = LazyOptional.of(() -> this);

        reset();
    }

    public ITemperatureAdjust getAdjustAt(BlockPos pos) {
        for (ITemperatureAdjust adj : adjusters) {
            if (adj.getCenterX() == pos.getX() && adj.getCenterY() == pos.getY() && adj.getCenterZ() == pos.getZ())
                return adj;
        }
        return null;
    }

    public Collection<ITemperatureAdjust> getAdjusters() {
        return adjusters;
    }

    public void setAdjusters(List<ITemperatureAdjust> adjusters) {
        this.adjusters.addAll(adjusters);
    }

    public ChunkPos getPos() {
        return pos;
    }

    /**
     * Get Temperature in a world at a location
     *
     * @param world world in
     * @param pos   position
     */
    float getTemperatureAtBlock(LevelReader world, BlockPos pos) {
        if (adjusters.isEmpty()) return WorldClimate.getWorldTemperature(world, pos);
        float ret = 0, tmp;
        for (ITemperatureAdjust adj : adjusters) {
            if (adj.isEffective(pos)) {
                tmp = adj.getValueAt(pos);
                if (tmp > ret)
                    ret = tmp;
            }
        }
        return WorldClimate.getWorldTemperature(world, pos) + ret;
    }


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return ChunkDataCapabilityProvider.CAPABILITY.orEmpty(cap, capability);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag nl = new ListTag();
        ClimateCrash.Last = this.getPos();
        for (ITemperatureAdjust adj : adjusters)
            nl.add(adj.serializeNBT());
        nbt.put("temperature", nl);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt != null) {
            // chunkMatrix.deserializeNBT(nbt.getCompound("temperature"));
            ClimateCrash.Last = this.getPos();
            ListTag nl = nbt.getList("temperature", 10);
            for (Tag nc : nl) {
                adjusters.add(ITemperatureAdjust.valueOf((CompoundTag) nc));
            }
        }
    }

    @Override
    public String toString() {
        return "ChunkData{ pos=" + pos + ",hashCode=" + Integer.toHexString(hashCode()) + '}';
    }

    private void reset() {
        adjusters.clear();

    }


    /**
     * Only used for the empty instance, this will enforce that it never leaks data
     * New empty instances can be constructed via constructor, EMPTY instance is
     * specifically for an immutable empty copy, representing invalid chunk data
     */
    private static final class Immutable extends ChunkData {
        private Immutable() {
            super(new ChunkPos(ChunkPos.INVALID_CHUNK_POS));
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public String toString() {
            return "ImmutableChunkData";
        }
    }

}
