package emanondev.core.packetentity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import emanondev.core.utility.VersionUtility;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PacketItem extends PacketEntity {

    private ItemStack item;

    private boolean hasGravity;

    private boolean isGlowing;

    private int pickupDelay;

    private BaseComponent customName;

    private boolean customNameVisible;

    private Vector velocity;

    PacketItem(Location location, PacketManager manager) {
        super(location, manager);
        this.item = new ItemStack(Material.STONE);
        this.hasGravity = false;
        this.pickupDelay = 0;
        this.customName = new TextComponent();
        this.customNameVisible = false;
        this.isGlowing = false;
        this.velocity = new Vector(0.0D, 0.0D, 0.0D);
    }

    @Override
    protected void handleSpawnPackets(@NotNull Collection<Player> players) {
        getManager().spawnItem(players, this);
    }

    @Override
    protected void handleUpdatePackets(@NotNull Collection<Player> players) {
        getManager().updateItem(players, this);
    }

    public BaseComponent getCustomName() {
        return this.customName;
    }

    public PacketItem setCustomName(String customName) {
        this.customName = new TextComponent(customName);
        return this;
    }

    public PacketItem setCustomName(BaseComponent customName) {
        this.customName = customName;
        return this;
    }

    public boolean isGlowing() {
        return this.isGlowing;
    }

    public PacketItem setGlowing(boolean bool) {
        this.isGlowing = bool;
        return this;
    }

    public boolean isCustomNameVisible() {
        return this.customNameVisible;
    }

    public PacketItem setCustomNameVisible(boolean bool) {
        this.customNameVisible = bool;
        return this;
    }

    public EntityType getType() {
        return VersionUtility.isNewerEquals(1, 20, 5) ?
                EntityType.ITEM : EntityType.valueOf("DROPPED_ITEM");
    }

    public PacketItem setItemStack(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            this.item = new ItemStack(Material.STONE);
            return this;
        }
        this.item = item.clone();
        return this;
    }

    public ItemStack getItemStack() {
        return this.item.clone();
    }

    public PacketItem setGravity(boolean bool) {
        this.hasGravity = bool;
        return this;
    }

    public boolean hasGravity() {
        return this.hasGravity;
    }

    public Vector getVelocity() {
        return this.velocity;
    }

    public PacketItem setVelocity(Vector vector) {
        this.velocity = vector.clone();
        return this;
    }

    public int getPickupDelay() {
        return this.pickupDelay;
    }

    public PacketItem setPickupDelay(int pickupDelay) {
        this.pickupDelay = pickupDelay;
        return this;
    }

    public WrappedDataWatcher getWrappedDataWatcher() {
        return WatchableCollection.getWatchableCollection(this);
    }

    public double getHeight() {
        return 0.25D;
    }
}