package emanondev.core.packetentity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Getter;
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
    @Getter
    private ItemStack item;

    private BlockFace facing;

    @Getter
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
        return switch (this.facing) {
            case DOWN -> 0.0F;
            case EAST -> -90.0F;
            case NORTH -> 180.0F;
            case SOUTH -> 0.0F;
            case UP -> 0.0F;
            case WEST -> 90.0F;
            default -> 0.0F;
        };
    }

    public float getPitch() {
        return switch (this.facing) {
            case DOWN -> 90.0F;
            case EAST -> 0.0F;
            case NORTH -> 0.0F;
            case SOUTH -> 0.0F;
            case UP -> -90.0F;
            case WEST -> 0.0F;
            default -> 0.0F;
        };
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

    @Override
    protected void handleUpdatePackets(@NotNull Collection<Player> players) {
        getManager().updateItemFrame(players, this);
    }
}