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

package com.teammoeg.frostedheart.content.climate.player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.frostedheart.data.FHDataManager;
import com.teammoeg.frostedheart.content.climate.data.BlockTempData;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.server.level.ServerLevel;

/**
 * A simulator built on Alphagem618's heat conducting model
 * This simulates heat conduction in a small area around player
 * And would take the area out of minecraft logic to optimize calculations.
 *
 * @author khjxiaogu
 * @author Alphagem618
 */
public class SurroundingTemperatureSimulator {
    /**
     * Extract block data into shape and temperature, other data are disposed.
     */
    private static class CachedBlockInfo {
        VoxelShape shape;
        List<AABB> aabbList;
        float temperature;
        boolean exposeToAir;
        BlockState bs;

        public CachedBlockInfo(VoxelShape shape, float temperature, boolean exposeToAir, BlockState bs) {
            super();
            this.shape = shape;
            this.aabbList=shape.toAabbs();
            this.temperature = temperature;
            this.exposeToAir = exposeToAir;
            this.bs = bs;
        }

        public CachedBlockInfo(VoxelShape shape, boolean exposeToAir, BlockState bs) {
            super();
            this.shape = shape;
            this.aabbList=shape.toAabbs();
            this.exposeToAir = exposeToAir;
            this.bs = bs;
        }
    }

    public static final int range = 8;// through max range is 8, to avoid some rare issues, set it to 7 to keep count
    private static final int n = 4168;//number of particles
    private static final int rdiff = 10;//division of the unit square, changing this value would have no effect but improve precision
    private static final float v0 = .4f;//initial particle speed
    private static final VoxelShape EMPTY = Shapes.empty();
    private static final VoxelShape FULL = Shapes.block();
    private static Vec3[] speedVectors;// Vp, speed vector list, this list is constant and considered a distributed ball mesh.
    private static final int num_rounds = 20;//propagate time-to-live for each particles
    private static int[][] speedVectorByDirection = new int[6][];// index: ordinal value of outbounding facing

    static {// generate speed vector list
        Map<Direction, List<Integer>> lis = new EnumMap<>(Direction.class);
        List<Vec3> v3fs = new ArrayList<>();
        for (Direction dr : Direction.values()) {
            lis.put(dr, new ArrayList<>());
        }
        int o = 0;
        for (int i = -rdiff; i <= rdiff; ++i)
            for (int j = -rdiff; j <= rdiff; ++j)
                for (int k = -rdiff; k <= rdiff; ++k) {
                    if (i == 0 && j == 0 && k == 0)
                        continue; // ignore zero vector
                    float x = i * 1f / rdiff, y = j * 1f / rdiff, z = k * 1f / rdiff;
                    float r = Mth.sqrt(x * x + y * y + z * z);
                    if (r > 1)
                        continue; // ignore vectors out of the unit ball
                    Vec3 v3 = new Vec3(x / r * v0, y / r * v0, z / r * v0);
                    v3fs.add(v3);
                    if (v3.x > +0)
                        lis.get(Direction.EAST).add(o);
                    if (v3.x < -0)
                        lis.get(Direction.WEST).add(o);
                    if (v3.y > +0)
                        lis.get(Direction.UP).add(o);
                    if (v3.y < -0)
                        lis.get(Direction.DOWN).add(o);
                    if (v3.z > +0)
                        lis.get(Direction.SOUTH).add(o);
                    if (v3.z < -0)
                        lis.get(Direction.NORTH).add(o);
                    o++;
                }
        speedVectors = v3fs.toArray(new Vec3[o]);
        for (Direction dr : Direction.values()) {
            speedVectorByDirection[dr.ordinal()] = lis.get(dr).stream().mapToInt(t -> t).toArray();
        }
    }

    public LevelChunkSection[] sections = new LevelChunkSection[8];// index: bitset of xzy(1 stands for +)
    public Heightmap[] maps = new Heightmap[4]; // index: bitset of xz(1 stands for +)
    BlockPos origin;
    ServerLevel world;
    Random rnd;
    //RandomSequence rrnd;
    private Vec3[] Qpos = new Vec3[n];// Qpos, position of particle.
    private int[] vid = new int[n];// IDv, particle speed index in speed vector list, this lower random cost.
    //private double[] factor=


    public Map<BlockState, CachedBlockInfo> info = new HashMap<>();// state to info cache

    public Map<BlockPos, CachedBlockInfo> posinfo = new HashMap<>();// position to info cache

    public static void init() {

    }

    public SurroundingTemperatureSimulator(ServerPlayer player) {
        int sourceX = Mth.floor(player.getX()), sourceY = Mth.floor(player.getEyeY()), sourceZ = Mth.floor(player.getZ());
        // these are block position offset
        int offsetN = sourceZ - range;
        int offsetW = sourceX - range;
        int offsetD = sourceY - range;
        // these are chunk position offset
        int chunkOffsetW = offsetW >> 4;
        int chunkOffsetN = offsetN >> 4;
        int chunkOffsetD = offsetD >> 4;
        // get origin point(center of 8 sections)
        origin = new BlockPos((chunkOffsetW + 1) << 4, (chunkOffsetD + 1) << 4, (chunkOffsetN + 1) << 4);
        // fetch all sections to lower calculation cost
        int i = 0;
        world = player.serverLevel();
        for (int x = chunkOffsetW; x <= chunkOffsetW + 1; x++)
            for (int z = chunkOffsetN; z <= chunkOffsetN + 1; z++) {
                LevelChunk cnk = world.getChunk(x, z);
                LevelChunkSection[] css = cnk.getSections();
                maps[i / 2] = cnk.getOrCreateHeightmapUnprimed(Types.MOTION_BLOCKING_NO_LEAVES);
                if (css.length > chunkOffsetD && chunkOffsetD >= 0)
                    sections[i] = css[chunkOffsetD];
                if (css.length > chunkOffsetD + 1 && chunkOffsetD + 1 >= 0)
                    sections[i + 1] = css[chunkOffsetD + 1];
                i += 2;
            }
        rnd = new Random(player.blockPosition().asLong() ^ (world.getGameTime() >> 6));
    }

    /**
     * This fetch block in a delta location to origin,
     * x,y,z must be in range [-16,16)
     */
    public BlockState getBlock(int x, int y, int z) {
        if (x >= 16 || y >= 16 || z >= 16 || x < -16 || y < -16 || z < -16) // out of bounds
            return Blocks.AIR.defaultBlockState();
        int i = 0;
        if (x >= 0)
            i += 4;
        if (z >= 0)
            i += 2;
        if (y >= 0)
            i += 1;
        LevelChunkSection current = sections[i];
        if (current == null)
            return Blocks.AIR.defaultBlockState();
        try {
            return current.getBlockState(x & 15, y & 15, z & 15);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get block at" + x + "," + y + "," + z);
        }
    }

    public int getTopY(int x, int z) {
        if (x >= 16 || z >= 16 || x < -16 || z < -16) // out of bounds
            return 0;
        int i = 0;
        if (x >= 0)
            i += 2;
        if (z >= 0)
            i += 1;
        return maps[i].getFirstAvailable(x & 15, z & 15);
    }

    public int getOutboundSpeedFrom(Direction dir) {
        if (dir == null)
            return rnd.nextInt(speedVectors.length);
        int[] iis = speedVectorByDirection[dir.ordinal()];
        return iis[rnd.nextInt(iis.length)];
    }

    public Pair<Float, Float> getBlockTemperatureAndWind(double qx0, double qy0, double qz0) {
        float wind = 0;
        Vec3 q0 = new Vec3(qx0, qy0, qz0);
        for (int i = 0; i < n; ++i) // initialize position as the player's position and the speed (index)
        {
            Qpos[i] = q0;
            vid[i] = i;
        }
        /*System.out.println("=========start=========");
        for(int i=-1;i<=1;i++) {
        	StringBuilder sb=new StringBuilder();
        	 for(int j=-1;j<=1;j++)
        		 sb.append(getInfo(new BlockPos(qx0+i,qy0-2,qz0+j)).bs.getBlock().getRegistryName()).append(" , ");
        	 System.out.println(sb.toString());
        }*/
        float heat = 0;
        for (int round = 0; round < num_rounds; ++round) // time-to-live for each particle is `num_rounds`
        {
            for (int i = 0; i < n; ++i) // for all particles:
            {
                int nid = vid[i];
                Vec3 curspeed = speedVectors[vid[i]];
                Vec3 svec = Qpos[i];
                Vec3 dvec = svec.add(curspeed);
                BlockPos bpos = new BlockPos((int) dvec.x, (int) dvec.y, (int) dvec.z);
                CachedBlockInfo info = getInfoCached(bpos);

                VoxelShape shape = info.shape;
                BlockHitResult bhr = shape.clip(svec, dvec, bpos);
                if (shape != EMPTY && bhr != null && (shape == FULL || bhr.isInside())) {
                    BlockHitResult brtr = AABB.clip(info.aabbList, svec, dvec, bpos);
                    if (brtr != null) {
                        if (rnd.nextDouble() < 0.33f) {
                            nid = rnd.nextInt(speedVectors.length);
                        } else {
                            nid = getOutboundSpeedFrom(brtr.getDirection());
                        }
                    } else {
                        nid = rnd.nextInt(speedVectors.length);
                    }
                }
                Qpos[i] = dvec;
                vid[i] = nid;
                heat += (float) (getHeat(bpos) * Mth.lerp(Mth.clamp(-curspeed.y(), 0, 0.4) * 2.5, 1, 0.5)); // add heat
                wind += getAir(bpos) ? (float) Mth.lerp((Mth.clamp(Math.abs(curspeed.y()), 0.2, 0.8) - 0.2) / 0.6, 2, 0.5) : 0;
            }
        }
        return Pair.of(heat / n, wind / n);
    }

    /**
     * Get location temperature
     */
    private float getHeat(BlockPos bp) {
        return getInfoCached(bp).temperature;
    }

    private boolean getAir(BlockPos bp) {
        return getInfoCached(bp).exposeToAir;
    }

    /***
     * fetch without position cache, but with blockstate cache, blocks with the same
     * state should have same collider and heat.
     *
     */
    private CachedBlockInfo getInfo(BlockPos pos) {
        BlockPos ofregion = pos.subtract(origin);
        BlockState bs = getBlock(ofregion.getX(), ofregion.getY(), ofregion.getZ());
        return info.computeIfAbsent(bs, s -> getInfo(pos, s));
    }

    /**
     * Just fetch block temperature and collision without cache.
     * Position is only for getCollisionShape method, to avoid some TE based shape.
     */
    private CachedBlockInfo getInfo(BlockPos pos, BlockState bs) {
        boolean isExpose = getTopY(pos.getX(), pos.getZ()) < pos.getY();
        BlockTempData b = FHDataManager.getBlockData(bs.getBlock());
        if (b == null)
            return new CachedBlockInfo(bs.getBlockSupportShape(world, pos), isExpose, bs);
        float cblocktemp = 0;
        if (b.isLit()) {
            boolean litOrActive = bs.hasProperty(BlockStateProperties.LIT) && bs.getValue(BlockStateProperties.LIT);
            if (litOrActive)
                cblocktemp += b.getTemp();
        } else
            cblocktemp += b.getTemp();
        if (b.isLevel()) {
            if (bs.hasProperty(BlockStateProperties.LEVEL)) {
                cblocktemp *= (float) (bs.getValue(BlockStateProperties.LEVEL) + 1) / 16;
            } else if (bs.hasProperty(BlockStateProperties.LEVEL_COMPOSTER)) {
                cblocktemp *= (float) (bs.getValue(BlockStateProperties.LEVEL_COMPOSTER) + 1) / 9;
            } else if (bs.hasProperty(BlockStateProperties.LEVEL_FLOWING)) {
                cblocktemp *= (float) (bs.getValue(BlockStateProperties.LEVEL_FLOWING)) / 8;
            } else if (bs.hasProperty(BlockStateProperties.LEVEL_CAULDRON)) {
                cblocktemp *= (float) (bs.getValue(BlockStateProperties.LEVEL_CAULDRON) + 1) / 4;
            }
        }
        return new CachedBlockInfo(bs.getBlockSupportShape(world, pos), cblocktemp, isExpose, bs);
    }

    /***
     * Since a position is highly possible to be fetched for multiple times, add
     * cache in normal fetch
     */
    private CachedBlockInfo getInfoCached(BlockPos pos) {
        return posinfo.computeIfAbsent(pos, this::getInfo);
    }

    /**
     * Check if this location collides with block.
     */
    private Direction getHitingFace(double sx, double sy, double sz, double vx, double vy, double vz) {
        BlockPos bpos = new BlockPos((int) (sx + vx), (int) (sy + vy), (int) (sz + vz));
        CachedBlockInfo info = getInfoCached(bpos);
        if (info.shape == EMPTY)
            return null;
        Vec3 svec = new Vec3(sx, sy, sz);
        Vec3 vvec = new Vec3(sx + vx, sy + vy, sz + vz);
        BlockHitResult brtr = AABB.clip(info.shape.toAabbs(), svec, vvec, bpos);
        if (brtr != null)
            return brtr.getDirection();
        return null;
    }
    /*private boolean isBlockade(double x, double y, double z) {
        CachedBlockInfo info = getInfoCached(new BlockPos((int)x,(int) y, (int)z));
        if (info.shape == FULL)
            return true;
        if (info.shape == EMPTY)
            return false;
        double nx=Mth.frac(x),ny=Mth.frac(y),nz=Mth.frac(z);
        return info.shape.isFullWide(nx,ny,nz);
    }*/
}
