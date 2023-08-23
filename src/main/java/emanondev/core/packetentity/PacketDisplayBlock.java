package emanondev.core.packetentity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public class PacketDisplayBlock extends PacketEntity {

    private boolean hasShadow = false;
    private float shadowStrength = 1;
    private float shadowRadius = 0;


    private float viewRangeBlocks = 32;
    private Display.Billboard billboard = Display.Billboard.FIXED;

    public @Nullable BlockData getBlockData() {
        return blockData;
    }

    private BlockData blockData;

    public PacketDisplayBlock(Location location, PacketManager manager) {
        super(location, manager);
    }

    @Override
    public EntityType getType() {
        return EntityType.BLOCK_DISPLAY;
    }

    @Override
    protected void handleSpawnPackets(@NotNull Collection<Player> players) {
        getManager().spawnDisplayBlock(players, this);
    }

    @Override
    protected void handleUpdatePackets(@NotNull Collection<Player> players) {
        getManager().updateDisplayBlock(players, this);
    }

    public boolean hasShadow() {
        return this.hasShadow;
    }

    public float getShadowStrength() {
        return this.shadowStrength;
    }

    public float getShadowRadius() {
        return this.shadowRadius;
    }

    public float getViewRangeBlocks() {
        return this.viewRangeBlocks;
    }

    @Contract("_->this")
    public PacketDisplayBlock setHasShadow(boolean hasShadow) {
        if (this.hasShadow != hasShadow) {
            shouldUpdateMeta = !active.isEmpty();
            this.hasShadow = hasShadow;
        }
        return this;
    }

    @Contract("_->this")
    public PacketDisplayBlock setShadowStrength(float shadowStrength) {
        if (this.shadowStrength != shadowStrength) {
            shouldUpdateMeta = !active.isEmpty();
            this.shadowStrength = shadowStrength;
        }
        return this;
    }

    @Contract("_->this")
    public PacketDisplayBlock setShadowRadius(float shadowRadius) {
        if (this.shadowRadius != shadowRadius) {
            shouldUpdateMeta = !active.isEmpty();
            this.shadowRadius = shadowRadius;
        }
        return this;
    }

    @Contract("_->this")
    public PacketDisplayBlock setBlockData(@NotNull BlockData blockData) {
        if (!Objects.equals(this.blockData,blockData)) {
            shouldUpdateMeta = !active.isEmpty();
            this.blockData = blockData;
        }
        return this;
    }

    @Contract("_->this")
    public PacketDisplayBlock setViewRangeBlocks(float viewRange) {
        if (this.viewRangeBlocks != viewRange) {
            shouldUpdateMeta = !active.isEmpty();
            this.viewRangeBlocks = viewRange;
        }
        return this;
    }

    public @NotNull WrappedDataWatcher getWrappedDataWatcher() {
        if (dataWatcher == null)
            this.dataWatcher = new WrappedDataWatcher();
        byte bitmask = 0;
        bitmask = !this.isVisible() ? (byte) (bitmask | 0x20) : bitmask;
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WatchableCollection.byteSerializer), bitmask);
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(16, WatchableCollection.floatSerializer), this.getViewRangeBlocks() / 64);
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(17, WatchableCollection.floatSerializer), this.getShadowRadius());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(18, WatchableCollection.floatSerializer), this.getShadowStrength());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(22, WatchableCollection.blockDataSerializer), this.getBlockData());

        byte bitmask3 = (byte) this.getBillboard().ordinal();
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, WatchableCollection.byteSerializer), bitmask3);


        return dataWatcher;
    }

    public @NotNull Display.Billboard getBillboard() {
        return billboard;
    }

    @Contract("_->this")
    public PacketDisplayBlock setBillboard(@NotNull Display.Billboard billboard) {
        if (this.billboard != billboard) {
            shouldUpdateMeta = !active.isEmpty();
            this.billboard = billboard;
        }
        return this;
    }
}
