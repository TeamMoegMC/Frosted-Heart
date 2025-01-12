package com.teammoeg.frostedheart.content.steamenergy.creative;

import com.teammoeg.frostedheart.content.steamenergy.ConnectorNetworkRevalidator;
import com.teammoeg.frostedheart.content.steamenergy.HeatEndpoint;
import com.teammoeg.frostedheart.content.steamenergy.HeatNetwork;
import com.teammoeg.frostedheart.content.steamenergy.NetworkConnector;
import com.teammoeg.frostedheart.content.steamenergy.debug.DebugHeaterTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class HeatManagerBlockEntity extends HeatBlockEntity implements NetworkConnector {
    HeatNetwork manager;
    ConnectorNetworkRevalidator<HeatManagerBlockEntity> networkHandler = new ConnectorNetworkRevalidator<>(this);
    public HeatManagerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        manager = new HeatNetwork( () -> {
            for (Direction d : Direction.values()) {
                manager.connectTo(level, worldPosition.relative(d),getBlockPos(), d.getOpposite());
            }
        });
        endpoint = new HeatEndpoint(-1, Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        heatcap = LazyOptional.of(() -> endpoint);
    }

    @Override
    public void tick() {
        super.tick();
        if(!endpoint.hasValidNetwork())
            manager.addEndpoint(heatcap.cast(), 0, getLevel(), getBlockPos());
        manager.tick(level);
        networkHandler.tick();
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        manager.save(tag, false);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        manager.load(tag, false);
    }

    @Override
    @Nonnull
    public HeatNetwork getNetwork() {
        return networkHandler.hasNetwork()?networkHandler.getNetwork():manager;
    }

    @Override
    public boolean canConnectTo(Direction to) {
        return true;
    }

    @Override
    public void setNetwork(HeatNetwork network) {
        networkHandler.setNetwork(network);
    }

    @Override
    public void destroy() {
        super.destroy();
        manager.invalidate(level);
    }
}
