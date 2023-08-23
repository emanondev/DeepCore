package emanondev.core.packetentity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public abstract class PacketEntity {

    private final int id;
    private final UUID uuid;
    private final PacketManager manager;
    protected WrappedDataWatcher dataWatcher;
    protected Collection<Player> active = new HashSet<>();
    protected boolean shouldUpdateMeta = false;
    private Location location;
    private boolean isSilent;
    private boolean isVisible = true;
    private boolean shouldUpdatePosition = false;

    public PacketEntity(@NotNull Location location, @NotNull PacketManager manager) {
        this.manager = manager;
        this.id = (int) (Math.random() * Integer.MAX_VALUE);
        this.uuid = UUID.randomUUID();
        this.location = location.clone();
        this.isSilent = false;
        this.manager.trackPacketEntity(this);
    }

    public abstract EntityType getType();

    public boolean isVisible() {
        return this.isVisible;
    }

    @Contract("_->this")
    public PacketEntity setVisible(boolean visible) {
        if (this.isVisible != visible) {
            shouldUpdateMeta = true;
            isVisible = visible;
        }
        return this;
    }

    public boolean shouldUpdatePosition() {
        return shouldUpdatePosition;
    }

    public boolean shouldUpdateMeta() {
        return shouldUpdateMeta;
    }

    public PacketEntity setRotation(float yaw, float pitch) {
        return setLocation(location.getWorld(), location.getX(), location.getY(), location.getZ(), yaw, pitch);
    }

    public PacketEntity setLocation(@NotNull World world, double x, double y, double z, float yaw, float pitch) {
        return setLocation(new Location(world, x, y, z, yaw, pitch));
    }

    public World getWorld() {
        return location.getWorld();
    }

    public PacketEntity setLocation(@NotNull World world, double x, double y, double z) {
        return setLocation(new Location(world, x, y, z, location.getYaw(), location.getPitch()));
    }

    public @NotNull Location getLocation() {
        return location.clone();
    }

    @Contract("_->this")
    public PacketEntity setLocation(Location location) {
        this.location = location.clone();
        shouldUpdatePosition = !active.isEmpty();
        return this;
    }

    public boolean isSilent() {
        return isSilent;
    }


    @Contract("_->this")
    public PacketEntity setSilent(boolean bool) {
        if (isSilent != bool) {
            this.isSilent = bool;
            shouldUpdateMeta = !active.isEmpty();
        }
        return this;
    }

    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    public int getEntityId() {
        return id;
    }

    public @NotNull PacketManager getManager() {
        return manager;
    }

    @Contract("->this")
    public PacketEntity remove() {
        return remove(active);
    }

    @Contract("_->this")
    public PacketEntity remove(@NotNull Collection<Player> players) {
        if (players == active)
            players = new HashSet<>(players);
        active.removeAll(players);
        handleRemovePackets(players);
        return this;
    }

    protected void handleRemovePackets(@NotNull Collection<Player> players) {
        getManager().entityDestroyPacket(players, this);
    }

    @Contract("_->this")
    public PacketEntity spawn(@NotNull Collection<Player> players) {
        active.addAll(players);
        handleSpawnPackets(players);
        return this;
    }

    @Contract("_->this")
    public PacketEntity spawn(@NotNull Player player) {
        return spawn(List.of(player));
    }

    protected abstract void handleSpawnPackets(@NotNull Collection<Player> players);

    @Contract("->this")
    public PacketEntity update() {
        update(active);
        shouldUpdatePosition = false;
        shouldUpdateMeta = false;
        return this;
    }

    @Contract("_->this")
    public PacketEntity update(@NotNull Collection<Player> players) {
        handleUpdatePackets(players);
        return this;
    }

    protected abstract void handleUpdatePackets(@NotNull Collection<Player> players);

    public abstract WrappedDataWatcher getWrappedDataWatcher();
}