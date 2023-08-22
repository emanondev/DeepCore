package emanondev.core.packetentity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PacketItemFrame extends PacketEntity {
    private ItemStack item;

    private BlockFace facing;

    private int frameRotation;

    public PacketItemFrame(Location location, PacketManager manager) {
        super(location, manager);
        this.item = new ItemStack(Material.AIR);
        this.facing = BlockFace.SOUTH;
        this.frameRotation = 0;
    }

    public EntityType getType() {
        return EntityType.ITEM_FRAME;
    }

    public BlockFace getAttachedFace() {
        return this.facing;
    }

    public float getYaw() {
        switch (this.facing) {
            case DOWN:
                return 0.0F;
            case EAST:
                return -90.0F;
            case NORTH:
                return 180.0F;
            case SOUTH:
                return 0.0F;
            case UP:
                return 0.0F;
            case WEST:
                return 90.0F;
            default:
                break;
        }
        return 0.0F;
    }

    public float getPitch() {
        switch (this.facing) {
            case DOWN:
                return 90.0F;
            case EAST:
                return 0.0F;
            case NORTH:
                return 0.0F;
            case SOUTH:
                return 0.0F;
            case UP:
                return -90.0F;
            case WEST:
                return 0.0F;
            default:
                break;
        }
        return 0.0F;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public PacketItemFrame setItem(ItemStack item) {
        this.item = item.clone();
        return this;
    }

    public BlockFace getFacingDirection() {
        return this.facing;
    }

    public PacketItemFrame setFacingDirection(BlockFace facing) {
        this.facing = facing;
        return this;
    }

    public int getFrameRotation() {
        return this.frameRotation;
    }

    public PacketItemFrame setFrameRotation(int rotation) {
        if (rotation >= 0 && rotation < 8) {
            this.frameRotation = rotation;
        } else {
            Bukkit.getLogger().severe("Item Frame Rotation must be between 0 and 7");
        }
        return this;
    }

    public WrappedDataWatcher getWrappedDataWatcher() {
        return WatchableCollection.getWatchableCollection(this);
    }

    public double getHeight() {
        return 0.75D;
    }

    @Override
    protected void handleSpawnPackets(@NotNull Collection<Player> players) {
        getManager().spawnItemFrame(players, this);
    }

    public int cacheCode() {
        int prime = 17;
        int result = super.cacheCode();
        result = prime * result + ((this.item == null) ? 0 : this.item.hashCode());
        result = prime * result + ((this.facing == null) ? 0 : this.facing.hashCode());
        result = prime * result + this.frameRotation;
        return result;
    }

    @Override
    protected void handleUpdatePackets(@NotNull Collection<Player> players) {
        getManager().updateItemFrame(players, this);
    }
}