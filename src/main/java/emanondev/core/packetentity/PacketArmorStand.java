package emanondev.core.packetentity;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import emanondev.core.UtilsInventory;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Optional;

public class PacketArmorStand extends PacketEntity {
    private final EnumMap<EquipmentSlot, ItemStack> equip = new EnumMap<>(EquipmentSlot.class);
    private boolean hasArms;
    private boolean hasBasePlate;
    private boolean isMarker;
    private boolean hasGravity;
    private boolean isSmall;
    private boolean isInvulnerable;
    private EulerAngle rightArmPose;
    private EulerAngle headPose;
    private BaseComponent customName;

    private boolean customNameVisible;

    private Vector velocity;
    private boolean shouldUpdateEquipment = false;

    PacketArmorStand(Location location, PacketManager manager) {
        super(location, manager);
        this.hasArms = false;
        this.hasBasePlate = true;
        this.isMarker = false;
        this.hasGravity = true;
        this.isSmall = false;
        this.isInvulnerable = false;
        this.rightArmPose = new EulerAngle(0.0D, 0.0D, 0.0D);
        this.headPose = new EulerAngle(0.0D, 0.0D, 0.0D);
        this.customName = new TextComponent();
        this.customNameVisible = false;
        this.velocity = new Vector(0.0D, 0.0D, 0.0D);
    }

    @Override
    public EntityType getType() {
        return EntityType.ARMOR_STAND;
    }

    /**
     * @see PacketHologram
     */
    @Deprecated
    public PacketArmorStand setAsHologram(String displayName) {
        return this.setCustomName(displayName).setInvulnerable(true).setSmall(true).setMarker(true).setSilent(true)
                .setGravity(false).setVisible(false).setCustomNameVisible(true);
    }

    @Deprecated
    @Contract("_->this")
    public PacketArmorStand setGravity(boolean bool) {
        if (this.hasGravity != bool) {
            this.hasGravity = bool;
            shouldUpdateMeta = !active.isEmpty();
        }
        return this;
    }

    public PacketArmorStand setSilent(boolean value) {
        return (PacketArmorStand) super.setSilent(value);
    }

    public PacketArmorStand setVisible(boolean value) {
        return (PacketArmorStand) super.setVisible(value);
    }

    @Override
    protected void handleSpawnPackets(@NotNull Collection<Player> players) {
        getManager().spawnArmorStand(players, this);
    }

    @Override
    protected void handleUpdatePackets(@NotNull Collection<Player> players) {
        getManager().updateArmorStand(players, this);
    }

    public BaseComponent getCustomName() {
        return this.customName;
    }

    @Contract("_->this")
    public PacketArmorStand setCustomName(@NotNull String customName) {
        this.customName = new TextComponent(customName);
        return this;
    }

    @Contract("_->this")
    public PacketArmorStand setCustomName(@Nullable BaseComponent customName) {
        this.customName = customName;
        return this;
    }

    public boolean isCustomNameVisible() {
        return this.customNameVisible;
    }

    @Contract("_->this")
    public PacketArmorStand setCustomNameVisible(boolean bool) {
        if (this.customNameVisible != bool) {
            shouldUpdateMeta = !active.isEmpty();
            this.customNameVisible = bool;
        }
        return this;
    }

    @Contract("_->this")
    public PacketArmorStand setArms(boolean bool) {
        if (this.hasArms != bool) {
            shouldUpdateMeta = !active.isEmpty();
            this.hasArms = bool;
        }
        return this;
    }

    public boolean hasArms() {
        return this.hasArms;
    }

    @Contract("_->this")
    public PacketArmorStand setBasePlate(boolean bool) {
        if (this.hasBasePlate != bool) {
            shouldUpdateMeta = !active.isEmpty();
            this.hasBasePlate = bool;
        }
        return this;
    }

    public boolean hasBasePlate() {
        return this.hasBasePlate;
    }

    public boolean isMarker() {
        return this.isMarker;
    }

    @Contract("_->this")
    public PacketArmorStand setMarker(boolean bool) {
        if (this.isMarker != bool) {
            shouldUpdateMeta = !active.isEmpty();
            this.isMarker = bool;
        }
        return this;
    }

    @Deprecated
    public boolean hasGravity() {
        return this.hasGravity;
    }

    public boolean isSmall() {
        return this.isSmall;
    }

    @Contract("_->this")
    public PacketArmorStand setSmall(boolean bool) {
        if (this.isSmall != bool) {
            shouldUpdateMeta = !active.isEmpty();
            this.isSmall = bool;
        }
        return this;
    }

    public boolean isInvulnerable() {
        return this.isInvulnerable;
    }

    @Contract("_->this")
    public PacketArmorStand setInvulnerable(boolean bool) {
        if (this.isInvulnerable != bool) {
            shouldUpdateMeta = !active.isEmpty();
            this.isInvulnerable = bool;
        }
        return this;
    }

    public @NotNull EulerAngle getRightArmPose() {
        return this.rightArmPose;
    }

    @Contract("_->this")
    public PacketArmorStand setRightArmPose(EulerAngle angle) {
        if (this.rightArmPose != angle) {
            shouldUpdateMeta = !active.isEmpty();
            this.rightArmPose = angle;
        }
        return this;
    }

    public @NotNull EulerAngle getHeadPose() {
        return this.headPose;
    }

    @Contract("_->this")
    public PacketArmorStand setHeadPose(EulerAngle angle) {
        if (this.headPose != angle) {
            shouldUpdateMeta = !active.isEmpty();
            this.headPose = angle;
        }
        return this;
    }

    @Contract("_,_->this")
    public PacketArmorStand setItem(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        if (UtilsInventory.isAirOrNull(item))
            equip.remove(slot);
        else
            equip.put(slot, item);
        this.shouldUpdateEquipment = !active.isEmpty();
        return this;
    }

    public @Nullable ItemStack getItem(@NotNull EquipmentSlot slot) {
        return equip.get(slot);
    }

    @Deprecated
    public @NotNull Vector getVelocity() {
        return this.velocity;
    }

    @Deprecated
    @Contract("_->this")
    public PacketArmorStand setVelocity(@NotNull Vector vector) {
        this.velocity = vector.clone();
        return this;
    }
/*
    public WrappedDataWatcher updateAndGetWrappedDataWatcher() {
        dataWatcher = WatchableCollection.getWatchableCollection(this, getDataWatcher());
        return dataWatcher;
    }*/

    public WrappedDataWatcher getWrappedDataWatcher() {
        //WrappedDataWatcher watcher = new WrappedDataWatcher();
        // ###
        if (dataWatcher == null)
            dataWatcher = new WrappedDataWatcher();
        byte bitmask = 0;
        bitmask = !this.isVisible() ? (byte) (bitmask | 0x20) : bitmask;
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WatchableCollection.byteSerializer), bitmask);

        if (this.getCustomName() != null && !this.getCustomName().toPlainText().equals(""))
            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WatchableCollection.optChatSerializer), Optional.of(WrappedChatComponent.fromJson(ComponentSerializer.toString(this.getCustomName())).getHandle()));
        else
            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WatchableCollection.optChatSerializer), Optional.empty());

        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WatchableCollection.booleanSerializer), this.isCustomNameVisible());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(4, WatchableCollection.booleanSerializer), this.isSilent());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WatchableCollection.booleanSerializer), !this.hasGravity());
        byte standbitmask = 0;
        standbitmask = this.isSmall() ? (byte) (standbitmask | 0x1) : standbitmask;
        standbitmask = this.hasArms() ? (byte) (standbitmask | 0x4) : standbitmask;
        standbitmask = !this.hasBasePlate() ? (byte) (standbitmask | 0x8) : standbitmask;
        standbitmask = this.isMarker() ? (byte) (standbitmask | 0x10) : standbitmask;


        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, WatchableCollection.byteSerializer), standbitmask);

        Vector3F headrotation = new Vector3F((float) Math.toDegrees(this.getHeadPose().getX()),
                (float) Math.toDegrees(this.getHeadPose().getY()),
                (float) Math.toDegrees(this.getHeadPose().getZ()));
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(16, WatchableCollection.vectorSerializer), headrotation);

        Vector3F rightarmrotation = new Vector3F((float) Math.toDegrees(this.getRightArmPose().getX()),
                (float) Math.toDegrees(this.getRightArmPose().getY()),
                (float) Math.toDegrees(this.getRightArmPose().getZ()));
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(19, WatchableCollection.vectorSerializer), rightarmrotation);
        return dataWatcher;
    }

    public double getHeight() {
        return this.isSmall ? 0.5D : 1.975D;
    }


    @Contract("->this")
    public PacketArmorStand update() {
        update(active);
        shouldUpdateEquipment = false;
        return this;
    }

    public boolean shouldUpdateEquipment() {
        return shouldUpdateEquipment;
    }
}