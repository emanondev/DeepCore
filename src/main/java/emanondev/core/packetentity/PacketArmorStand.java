package emanondev.core.packetentity;

import java.util.Collection;
import java.util.EnumMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import emanondev.core.UtilsInventory;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class PacketArmorStand extends PacketEntity {
    private boolean hasArms;

    private boolean hasBasePlate;

    private boolean isMarker;

    private boolean hasGravity;

    private boolean isSmall;

    private boolean isInvulnerable;

    private boolean isVisible;

    private EulerAngle rightArmPose;

    private EulerAngle headPose;

    private final EnumMap<EquipmentSlot, ItemStack> equip = new EnumMap<>(EquipmentSlot.class);

    private BaseComponent customName;

    private boolean customNameVisible;

    private Vector velocity;

    PacketArmorStand(Location location, PacketManager manager) {
        super(location, manager);
        this.hasArms = false;
        this.hasBasePlate = true;
        this.isMarker = false;
        this.hasGravity = true;
        this.isSmall = false;
        this.isInvulnerable = false;
        this.isVisible = true;
        this.rightArmPose = new EulerAngle(0.0D, 0.0D, 0.0D);
        this.headPose = new EulerAngle(0.0D, 0.0D, 0.0D);
        this.customName = new TextComponent();
        this.customNameVisible = false;
        this.velocity = new Vector(0.0D, 0.0D, 0.0D);
    }

    public int cacheCode() {
        int prime = 17;
        int result = super.cacheCode();
        result = prime * result + (this.hasArms ? 5351 : 8923);
        result = prime * result + (this.hasBasePlate ? 2861 : 6607);
        result = prime * result + (this.isMarker ? 9199 : 3163);
        result = prime * result + (this.hasGravity ? 6719 : 2753);
        result = prime * result + (this.isSmall ? 1373 : 3037);
        result = prime * result + (this.isInvulnerable ? 2111 : 2251);
        result = prime * result + (this.isVisible ? 6779 : 6679);
        result = prime * result + ((this.rightArmPose == null) ? 0 : this.rightArmPose.hashCode());
        result = prime * result + ((this.headPose == null) ? 0 : this.headPose.hashCode());
        for (ItemStack item : equip.values())
            result = prime * result + ((item == null) ? 0 : item.hashCode());
        result = prime * result + ((this.customName == null) ? 0 : this.customName.hashCode());
        result = prime * result + (this.customNameVisible ? 6199 : 8647);
        result = prime * result + ((this.velocity == null) ? 0 : this.velocity.hashCode());
        return result;
    }

    public PacketArmorStand setAsHologram(String displayName) {
        return this.setCustomName(displayName).setInvulnerable(true).setSmall(true).setMarker(true).setSilent(true)
                .setGravity(false).setVisible(false).setCustomNameVisible(true);
    }

    public PacketArmorStand setSilent(boolean value) {
        return (PacketArmorStand) super.setSilent(value);
    }

    public PacketArmorStand setCustomName(String customName) {
        this.customName = new TextComponent(customName);
        return this;
    }

    public PacketArmorStand setCustomName(BaseComponent customName) {
        this.customName = customName;
        return this;
    }

    public BaseComponent getCustomName() {
        return this.customName;
    }

    public PacketArmorStand setCustomNameVisible(boolean bool) {
        this.customNameVisible = bool;
        return this;
    }

    public boolean isCustomNameVisible() {
        return this.customNameVisible;
    }

    public PacketArmorStand setArms(boolean bool) {
        this.hasArms = bool;
        return this;
    }

    public boolean hasArms() {
        return this.hasArms;
    }

    public PacketArmorStand setBasePlate(boolean bool) {
        this.hasBasePlate = bool;
        return this;
    }

    public boolean hasBasePlate() {
        return this.hasBasePlate;
    }

    public PacketArmorStand setMarker(boolean bool) {
        this.isMarker = bool;
        return this;
    }

    public boolean isMarker() {
        return this.isMarker;
    }

    public PacketArmorStand setGravity(boolean bool) {
        this.hasGravity = bool;
        return this;
    }

    public boolean hasGravity() {
        return this.hasGravity;
    }

    public PacketArmorStand setSmall(boolean bool) {
        this.isSmall = bool;
        return this;
    }

    public boolean isSmall() {
        return this.isSmall;
    }

    public PacketArmorStand setInvulnerable(boolean bool) {
        this.isInvulnerable = bool;
        return this;
    }

    public boolean isInvulnerable() {
        return this.isInvulnerable;
    }

    public PacketArmorStand setVisible(boolean bool) {
        this.isVisible = bool;
        return this;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public PacketArmorStand setRightArmPose(EulerAngle angle) {
        if (this.lock)
            return this;
        this.rightArmPose = angle;
        return this;
    }

    public EulerAngle getRightArmPose() {
        return this.rightArmPose;
    }

    public PacketArmorStand setHeadPose(EulerAngle angle) {
        if (this.lock)
            return this;
        this.headPose = angle;
        return this;
    }

    public EulerAngle getHeadPose() {
        return this.headPose;
    }

    public PacketArmorStand setItem(EquipmentSlot slot, ItemStack item) {
        if (UtilsInventory.isAirOrNull(item))
            equip.remove(slot);
        return this;
    }

    public ItemStack getItem(EquipmentSlot slot) {
        return equip.get(slot);
    }

    public PacketArmorStand setVelocity(Vector vector) {
        this.velocity = vector.clone();
        return this;
    }

    public Vector getVelocity() {
        return this.velocity;
    }

    public WrappedDataWatcher getWrappedDataWatcher() {
        return WatchableCollection.getWatchableCollection(this);
    }

    public double getHeight() {
        return this.isSmall ? 0.5D : 1.975D;
    }

    @Override
    protected void handleRemovePackets(Collection<? extends Player> players) {
        getManager().removeArmorStand(players, this);
    }

    @Override
    protected void handleSpawnPackets(Collection<? extends Player> players) {
        getManager().spawnArmorStand(players, this);
    }

    @Override
    protected void handleUpdatePackets(Collection<? extends Player> players) {
        getManager().updateArmorStand(players, this);
    }

    public PacketArmorStand updateOnlyMeta() {
        return updateOnlyMeta(active);
    }

    public PacketArmorStand updateOnlyMeta(Collection<? extends Player> players) {
        return updateOnlyMeta(players, false);
    }

    public PacketArmorStand updateOnlyMeta(Collection<? extends Player> players, boolean bypassCache) {
        if (!bypassCache) {
            if (cache != null) {
                if (cache == this.cacheCode()) {
                    return this;
                }
            }
        }
        getManager().updateArmorStandOnlyMeta(players, this);
        return this;
    }

}