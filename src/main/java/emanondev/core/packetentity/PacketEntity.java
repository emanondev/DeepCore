package emanondev.core.packetentity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public abstract class PacketEntity {

    private final int id;
    private final UUID uuid;
    private final PacketManager manager;
    protected boolean lock;
    protected WrappedDataWatcher dataWatcher;
    protected Collection<Player> active = new HashSet<>();
    protected Integer cache = null;
    private Location location;
    private boolean isSilent;

    public PacketEntity(Location location, PacketManager manager) {
        if (location == null || manager == null)
            throw new NullPointerException();
        this.manager = manager;
        this.id = (int) (Math.random() * Integer.MAX_VALUE);
        this.uuid = UUID.randomUUID();
        this.location = location.clone();
        this.lock = false;
        this.isSilent = false;
        this.manager.trackPacketEntity(this);
    }

    public WrappedDataWatcher getDataWatcher() {
        return dataWatcher;
    }

    public PacketEntity setRotation(float yaw, float pitch) {
        if (lock) {
            return this;
        }
        teleport(location.getWorld(), location.getX(), location.getY(), location.getZ(), yaw, pitch);
        return this;
    }

    public PacketEntity teleport(World world, double x, double y, double z, float yaw, float pitch) {
        this.location = new Location(world, x, y, z, yaw, pitch);
        return this;
    }

    public World getWorld() {
        return location.getWorld();
    }

    public PacketEntity teleport(Location location) {
        this.location = location.clone();
        return this;
    }

    public PacketEntity teleport(World world, double x, double y, double z) {
        this.location = new Location(world, x, y, z, location.getYaw(), location.getPitch());
        return this;
    }

    public Location getLocation() {
        return location.clone();
    }

    public PacketEntity setLocation(Location location) {
        this.location = location.clone();
        return this;
    }

    public boolean isSilent() {
        return isSilent;
    }

    public PacketEntity setSilent(boolean bool) {
        this.isSilent = bool;
        return this;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getEntityId() {
        return id;
    }

    public boolean isLocked() {
        return lock;
    }

    public PacketEntity setLocked(boolean bool) {
        this.lock = bool;
        return this;
    }

    public PacketManager getManager() {
        return manager;
    }

    public PacketEntity remove() {
        return remove(active);
    }

    public PacketEntity remove(Collection<Player> players) {
        if (players == active)
            players = new HashSet<>(players);
        active.removeAll(players);
        handleRemovePackets(players);
        return this;
    }

    protected abstract void handleRemovePackets(Collection<Player> players);

    public PacketEntity spawn(Collection<Player> players) {
        active.addAll(players);
        handleSpawnPackets(players);
        return this;
    }


    public PacketEntity spawn(Player player) {
        return spawn(List.of(player));
    }

    protected abstract void handleSpawnPackets(Collection<Player> players);

    public PacketEntity update() {
        return update(active);
    }

    public PacketEntity update(Collection<Player> players) {
        return update(players, false);
    }

    public PacketEntity update(Collection<Player> players, boolean bypassCache) {
        if (!bypassCache) {
            if (cache != null) {
                if (cache == this.cacheCode()) {
                    return this;
                }
            }
        }
        handleUpdatePackets(players);
        cache = this.cacheCode();
        return this;
    }

    public int cacheCode() {
        int prime = 17;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((lock) ? 1531 : 4021);
        result = prime * result + ((isSilent) ? 3301 : 4507);
        return result;
    }

    protected abstract void handleUpdatePackets(Collection<Player> players);

}