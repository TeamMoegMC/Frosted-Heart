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

package com.teammoeg.frostedheart.content.town.house;

import com.teammoeg.frostedheart.FHCapabilities;
import com.teammoeg.frostedheart.FHTileTypes;
import com.teammoeg.frostedheart.base.block.FHBaseTileEntity;
import com.teammoeg.frostedheart.base.block.FHBlockInterfaces;
import com.teammoeg.frostedheart.base.scheduler.ScheduledTaskTileEntity;
import com.teammoeg.frostedheart.base.scheduler.SchedulerQueue;
import com.teammoeg.frostedheart.content.steamenergy.capabilities.HeatConsumerEndpoint;
import com.teammoeg.frostedheart.content.town.TownTileEntity;
import com.teammoeg.frostedheart.content.town.TownWorkerType;
import com.teammoeg.frostedheart.content.town.resident.Resident;
import com.teammoeg.frostedheart.util.blockscanner.BlockScanner;
import com.teammoeg.frostedheart.util.client.ClientUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.*;

import static com.teammoeg.frostedheart.util.blockscanner.FloorBlockScanner.isHouseBlock;
import static net.minecraftforge.common.util.Constants.NBT.TAG_LONG;


/**
 * A house in the town.
 * <p>
 * Functionality:
 * - Provide a place for residents to live
 * - (Optional) Consume heat to add temperature based on the heat level
 * - Consume resources to maintain the house
 * - Check if the house structure is valid
 * - Compute comfort rating based on the house structure
 */
public class HouseTileEntity extends FHBaseTileEntity implements TownTileEntity, ITickableTileEntity,
        FHBlockInterfaces.IActiveState, ScheduledTaskTileEntity {

    /** The temperature at which the house is comfortable. */
    public static final double COMFORTABLE_TEMP_HOUSE = 24;
    public static final int MAX_TEMP_HOUSE = 50;
    public static final int MIN_TEMP_HOUSE = 0;
    public static final String TAG_NAME_OCCUPIED_AREA = "occupiedArea";

    /** Work data, stored in town. */
    private byte isValid = -1;
    private int maxResident = -1; // how many resident can live here
    public List<Resident> residents = new ArrayList<>();
    private int volume = -1;
    //private int decoration = -1;
    private int area = -1;
    private double temperature = -1;
    private Map<String, Integer> decorations = new HashMap<>();
    private double rating = -1;
    private Set<ColumnPos> occupiedArea;
    private double temperatureModifier = 0;
    private boolean addedToSchedulerQueue = false;

    /** Tile data, stored in tile entity. */
    HeatConsumerEndpoint endpoint = new HeatConsumerEndpoint(99,10,1);

    public HouseTileEntity() {
        super(FHTileTypes.HOUSE.get());
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public TownWorkerType getWorker() {
        return TownWorkerType.HOUSE;
    }

    /**
     * Check if work environment is valid.
     * <p>
     * For the house, this implies whether the house would accommodate the residents,
     * consume resources, and other.
     * <p>
     * Room structure should be valid.
     * Temperature should be within a reasonable range.
     */
    @Override
    public boolean isWorkValid() {
        if(this.isValid == -1) this.refresh();
        return this.isValid == 1;
    }

    public void refresh(){
        this.isValid = isStructureValid() && isStructureValid() ? (byte) 1 : (byte) 0;
    }

    public static ListNBT serializeOccupiedArea(Set<ColumnPos> occupiedArea) {
        ListNBT list = new ListNBT();
        for (ColumnPos pos : occupiedArea) {
            long posLong = BlockPos.pack(pos.x, 0, pos.z);
            list.add(LongNBT.valueOf(posLong));
        }
        return list;
    }
    public static Set<ColumnPos> deserializeOccupiedArea(CompoundNBT data) {
        Set<ColumnPos> occupiedArea = new HashSet<>();
        ListNBT list = data.getList(TAG_NAME_OCCUPIED_AREA, TAG_LONG);
        list.forEach(nbt -> {
            occupiedArea.add(new ColumnPos(BlockPos.unpackX(((LongNBT) nbt).getLong()), BlockPos.unpackZ(((LongNBT) nbt).getLong())));
        });
        return occupiedArea;
    }

    @Override
    public CompoundNBT getWorkData() {
        CompoundNBT data = new CompoundNBT();
        data.putByte("isValid", isValid);
        if(this.isValid == 1) {
            ListNBT residentList = new ListNBT();
            for (Resident resident : residents) {
                residentList.add(resident.serialize());
            }
            data.put("residents", residentList);
            data.putInt("maxResident", maxResident);
            data.putDouble("temperature", temperature);
            data.putInt("volume", volume);
            data.putInt("area", area);
            //data.putInt("decoration", decoration);
            data.putDouble("rating", rating);
            data.putDouble("temperatureModifier", temperatureModifier);
            data.put(TAG_NAME_OCCUPIED_AREA, serializeOccupiedArea(occupiedArea));
        }
        return data;
    }

    @Override
    public void setWorkData(CompoundNBT data) {
        isValid = data.getByte("isValid");
        if(isValid == 1) {
            residents = new ArrayList<>();
            ListNBT residentList = data.getList("residents", 10);
            for (int i = 0; i < residentList.size(); i++) {
                residents.add(new Resident().deserialize(residentList.getCompound(i)));
            }
            maxResident = data.getInt("maxResident");
            temperature = data.getDouble("temperature");
            volume = data.getInt("volume");
            area = data.getInt("area");
            //decoration = data.getInt("decoration");
            rating = data.getDouble("rating");
            temperatureModifier = data.getDouble("temperatureModifier");
            occupiedArea = deserializeOccupiedArea(data);
        }
    }

    @Override
    public Collection<ColumnPos> getOccupiedArea() {
        this.isWorkValid();
        return this.occupiedArea;
    }

    public int getMaxResident() {
        return isWorkValid() ? this.maxResident : 0;
    }

    public int getVolume() {
        return isWorkValid() ? this.volume : 0;
    }

    public int getArea() {
        return isWorkValid() ? this.area : 0;
    }

    public double getTemperature() {
        return isWorkValid() ? this.temperature : 0;
    }

    public double getRating() {
        if(this.isWorkValid()){
            if(this.rating == -1){
                return rating = this.computeRating();
            }
            return this.rating;
        }
        return 0;
    }

    public double getTemperatureModifier() {
        return isWorkValid() ? this.temperatureModifier : 0;
    }


    /**
     * Determine whether the house structure is well-defined.
     * <p>
     * Check room insulation
     * Check minimum volume
     * Check within generator range (or just check steam connection instead?)
     * <p>
     *
     * @return whether the house structure is valid
     */
    public boolean isStructureValid() {
        BlockPos housePos = this.getPos();
        List<BlockPos> doorPosSet = BlockScanner.getBlocksAdjacent(housePos, (pos) -> Objects.requireNonNull(world).getBlockState(pos).isIn(BlockTags.DOORS));
        if (doorPosSet.isEmpty()) return false;
        for (BlockPos doorPos : doorPosSet) {
            BlockPos floorBelowDoor = BlockScanner.getBlockBelow((pos)->!(Objects.requireNonNull(world).getBlockState(pos).isIn(BlockTags.DOORS)), doorPos);//找到门下面垫的的那个方块
            for (Direction direction : BlockScanner.PLANE_DIRECTIONS) {
                //FHMain.LOGGER.debug("HouseScanner: creating new HouseBlockScanner");
                assert floorBelowDoor != null;
                BlockPos startPos = floorBelowDoor.offset(direction);//找到门下方块旁边的方块
                //FHMain.LOGGER.debug("HouseScanner: start pos 1" + startPos);
                if (!HouseBlockScanner.isValidFloorOrLadder(Objects.requireNonNull(world), startPos)) {//如果门下方块旁边的方块不是合法的地板，找一下它下面的方块
                    if(!HouseBlockScanner.isValidFloorOrLadder(Objects.requireNonNull(world), startPos.down()) || isHouseBlock(world, startPos.up(2))){//如果它下面的方块也不是合法地板（或者梯子），或者门的上半部分堵了方块，就不找了。我们默认村民不能从两格以上的高度跳下来，也不能从一格高的空间爬过去
                        continue;
                    }
                    startPos = startPos.down();
                    //FHMain.LOGGER.debug("HouseScanner: start pos 2" + startPos);
                }
                HouseBlockScanner scanner = new HouseBlockScanner(this.world, startPos);
                if (scanner.scan()) {
                    //FHMain.LOGGER.debug("HouseScanner: scan successful");
                    this.volume = scanner.getVolume();
                    this.area = scanner.getArea();
                    this.decorations = scanner.getDecorations();
                    this.temperature = scanner.getTemperature();
                    this.occupiedArea = scanner.getOccupiedArea();
                    this.rating = computeRating();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine whether the house temperature is valid for work.
     * <p>
     * If connected to heat network, this always returns true.
     *
     * @return whether the temperature is valid
     */
    public boolean isTemperatureValid() {
        double effective = temperature + temperatureModifier;
        return effective >= MIN_TEMP_HOUSE && effective <= MAX_TEMP_HOUSE;
    }

    public double getEffectiveTemperature() {
        return temperature + temperatureModifier;
    }

    /**
     * Get a comfort rating based on how the house is built.
     * <p>
     * This would affect the mood of the residents on the next day.
     *
     * @return a rating in range of zero to one
     */
    private double computeRating() {
        return (calculateSpaceRating(this.volume, this.area) * (1+calculateDecorationRating(this.decorations, this.area))
                + calculateTemperatureRating(this.temperature + this.temperatureModifier)) / 3;
    }
    public static double calculateTemperatureRating(double temperature) {
        double tempDiff = Math.abs(COMFORTABLE_TEMP_HOUSE - temperature);
        return 0.017 + 1 / (1 + Math.exp(0.4 * (tempDiff - 10)));
    }
    private static double calculateDecorationRating(Map<?, Integer> decorations, int area) {
        double score = 0;
        for (Integer num : decorations.values()) {
            if (num + 0.32 > 0) { // Ensure the argument for log is positive
                score += Math.log(num + 0.32) * 1.75 + 0.9;
            } else {
                // Handle the case where num + 0.32 <= 0
                // For example, you could add a minimal score or skip adding to the score.
                score += 0; // Or some other handling logic
            }
        }
        return Math.min(1, score / (6 + area / 16.0f));
    }
    public static double calculateSpaceRating(int volume, int area) {
        double height = volume / (float) area;
        double score = area * (1.55 + Math.log(height - 1.6) * 0.6);
        return 1 - Math.exp(-0.024 * Math.pow(score, 1.11));
    }

    @Override
    public void tick() {
        assert world != null;
        if (!world.isRemote) {
            if (endpoint.tryDrainHeat(1)) {
                temperatureModifier = Math.max(endpoint.getTemperatureLevel() * 10, COMFORTABLE_TEMP_HOUSE);
                if (setActive(true)) {
                    markDirty();
                }
            } else {
                temperatureModifier = 0;
                if (setActive(false)) {
                    markDirty();
                }
            }
        } else if (getIsActive()) {
            ClientUtils.spawnSteamParticles(world, pos);
        }
        if(!this.addedToSchedulerQueue){
            SchedulerQueue.add(this);
            this.addedToSchedulerQueue = true;
        }
    }

    @Override
    public void readCustomNBT(CompoundNBT compoundNBT, boolean isPacket) {
        endpoint.load(compoundNBT, isPacket);
    }

    @Override
    public void writeCustomNBT(CompoundNBT compoundNBT, boolean isPacket) {
        endpoint.save(compoundNBT, isPacket);
    }

    LazyOptional<HeatConsumerEndpoint> endpointCap = LazyOptional.of(()-> endpoint);
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
        if(capability== FHCapabilities.HEAT_EP.capability() && facing == Direction.NORTH) {
            return endpointCap.cast();
        }
        return super.getCapability(capability, facing);
    }

    // ScheduledTaskTileEntity
    @Override
    public void executeTask() {
        this.refresh();
    }
    @Override
    public boolean isStillValid() {
        return this.isWorkValid();
    }
}