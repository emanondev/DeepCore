package emanondev.core.packetentity;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import emanondev.core.message.DMessage;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public class PacketHologram extends PacketEntity {

    private boolean isSeeThrough = false;
    private boolean hasShadow = false;
    private float shadowStrength = 1;
    private float shadowRadius = 0;
    private DMessage text = null;


    private float viewRange = 32;
    private Display.Billboard billboard = Display.Billboard.CENTER;
    private Color backGroundColor;

    public PacketHologram(Location location, PacketManager manager) {
        super(location, manager);
    }

    @Override
    public EntityType getType() {
        return EntityType.TEXT_DISPLAY;
    }

    @Override
    protected void handleSpawnPackets(@NotNull Collection<Player> players) {
        getManager().spawnHologram(players, this);
    }

    @Override
    protected void handleUpdatePackets(@NotNull Collection<Player> players) {
        getManager().updateHologram(players, this);
    }

    public boolean isSeeThrough() {
        return this.isSeeThrough;
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
        return this.viewRange;
    }

    public @Nullable DMessage getText() {
        return this.text;
    }


    @Contract("_->this")
    public PacketHologram setSeeThrough(boolean seeThrough) {
        if (isSeeThrough != seeThrough) {
            isSeeThrough = seeThrough;
            shouldUpdateMeta = !active.isEmpty();
        }
        return this;
    }

    @Contract("_->this")
    public PacketHologram setHasShadow(boolean hasShadow) {
        if (this.hasShadow != hasShadow) {
            shouldUpdateMeta = !active.isEmpty();
            this.hasShadow = hasShadow;
        }
        return this;
    }

    @Contract("_->this")
    public PacketHologram setShadowStrength(float shadowStrength) {
        if (this.shadowStrength != shadowStrength) {
            shouldUpdateMeta = !active.isEmpty();
            this.shadowStrength = shadowStrength;
        }
        return this;
    }

    @Contract("_->this")
    public PacketHologram setShadowRadius(float shadowRadius) {
        if (this.shadowRadius != shadowRadius) {
            shouldUpdateMeta = !active.isEmpty();
            this.shadowRadius = shadowRadius;
        }
        return this;
    }

    @Contract("_->this")
    public PacketHologram setText(@NotNull DMessage text) {
        shouldUpdateMeta = !active.isEmpty();
        this.text = text;

        return this;
    }

    @Contract("_->this")
    public PacketHologram setViewRangeBlocks(float viewRange) {
        if (this.viewRange != viewRange) {
            shouldUpdateMeta = !active.isEmpty();
            this.viewRange = viewRange;
        }
        return this;
    }

    public @NotNull WrappedDataWatcher getWrappedDataWatcher() {
        if (dataWatcher == null)
            this.dataWatcher = new WrappedDataWatcher();
        byte bitmask = 0;
        bitmask = !this.isVisible() ? (byte) (bitmask | 0x20) : bitmask;
        if (bitmask != 0)
            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WatchableCollection.byteSerializer), bitmask);
        //dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(4, WatchableCollection.booleanSerializer), this.isSilent());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(16, WatchableCollection.floatSerializer), this.getViewRangeBlocks() / 64);
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(17, WatchableCollection.floatSerializer), this.getShadowRadius());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(18, WatchableCollection.floatSerializer), this.getShadowStrength());
        if (this.getText() != null)
            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(22, WatchableCollection.chatSerializer), WrappedChatComponent.fromJson(this.getText().toJson()));


        byte bitmask3 = (byte) this.getBillboard().ordinal();
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, WatchableCollection.byteSerializer), bitmask3);
        if (this.getBackGroundColor() != null)
            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(24, WatchableCollection.intSerializer),
                    this.getBackGroundColor().asARGB());

        byte bitmask2 = 0;
        bitmask2 = this.hasShadow() ? (byte) (bitmask2 | 0x01) : bitmask2;
        bitmask2 = this.isSeeThrough() ? (byte) (bitmask2 | 0x02) : bitmask2;
        bitmask2 = this.getBackGroundColor() == null ? (byte) (bitmask2 | 0x04) : bitmask2;
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(26, WatchableCollection.byteSerializer), bitmask2);
        return dataWatcher;
    }

    public @NotNull Display.Billboard getBillboard() {
        return billboard;
    }

    @Contract("_->this")
    public PacketHologram setBillboard(@NotNull Display.Billboard billboard) {
        if (this.billboard != billboard) {
            shouldUpdateMeta = !active.isEmpty();
            this.billboard = billboard;
        }
        return this;
    }

    @Contract("_->this")
    public PacketHologram setBackGroundColor(@Nullable Color backGroundColor) {
        if (!Objects.equals(this.backGroundColor, backGroundColor)) {
            shouldUpdateMeta = !active.isEmpty();
            this.backGroundColor = backGroundColor;
        }
        return this;
    }

    public @Nullable Color getBackGroundColor() {
        return this.backGroundColor;
    }
}
