package emanondev.core.packetentity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import emanondev.core.util.GameVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PacketManager {

    private final Plugin plugin;
    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private final Set<PacketEntity> packetEntities = Collections.newSetFromMap(new WeakHashMap<>());

    public PacketManager(Plugin plugin) {
        if (plugin == null)
            throw new NullPointerException();
        this.plugin = plugin;
        if (!GameVersion.isNewerEqualsTo(1, 19, 3))
            throw new IllegalStateException("unsupported version");
    }

    private static EnumWrappers.ItemSlot equipmentSlotToWrapper(EquipmentSlot slot) {
        return switch (slot) {
            case CHEST -> EnumWrappers.ItemSlot.CHEST;
            case FEET -> EnumWrappers.ItemSlot.FEET;
            case HAND -> EnumWrappers.ItemSlot.MAINHAND;
            case HEAD -> EnumWrappers.ItemSlot.HEAD;
            case LEGS -> EnumWrappers.ItemSlot.LEGS;
            case OFF_HAND -> EnumWrappers.ItemSlot.OFFHAND;
            default -> throw new IllegalStateException("unable to check equipment slot");
        };
    }

    public void clearAll() {
        for (PacketEntity pEntity : packetEntities)
            pEntity.remove();
    }

    public PacketItem getPacketItem(@NotNull Location l) {
        PacketItem p = new PacketItem(l, this);
        packetEntities.add(p);
        return p;
    }

    public PacketHologram getPacketHologram(@NotNull Location l) {
        PacketHologram p = new PacketHologram(l, this);
        packetEntities.add(p);
        return p;
    }

    public PacketArmorStand getPacketArmorStand(@NotNull Location l) {
        PacketArmorStand p = new PacketArmorStand(l, this);
        packetEntities.add(p);
        return p;
    }

    public PacketItemFrame getPacketItemFrame(@NotNull Location l) {
        PacketItemFrame p = new PacketItemFrame(l, this);
        packetEntities.add(p);
        return p;
    }

    public PacketDisplayItem getPacketDisplayItem(@NotNull Location l) {
        PacketDisplayItem p = new PacketDisplayItem(l, this);
        packetEntities.add(p);
        return p;
    }

    public PacketDisplayBlock getPacketDisplayBlock(@NotNull Location l) {
        PacketDisplayBlock p = new PacketDisplayBlock(l, this);
        packetEntities.add(p);
        return p;
    }

    protected void spawnArmorStand(Collection<Player> players, PacketArmorStand entity) {
        PacketContainer packet1 = spawnEntityPacket(entity);

        PacketContainer packet2 = entityMetadataPacket(entity);

        PacketContainer packet3 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> data = new ArrayList<>();
        packet3.getIntegers().write(0, entity.getEntityId());
        boolean hasItems = false;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = entity.getItem(slot);
            if (item != null && !item.getType().isAir()) { //what if item is changed to air/null?
                hasItems = true;
                data.add(new Pair<>(equipmentSlotToWrapper(slot), item));
            }
        }
        packet3.getSlotStackPairLists().write(0, data);
        if (!hasItems)
            packet3=null;
        sendPackets(players,packet1,packet2,packet3);
    }

    protected void updateArmorStand(Collection<? extends Player> players, PacketArmorStand entity) {
        PacketContainer packet1 = entity.shouldUpdateMeta() ? entityMetadataPacket(entity) : null;
        PacketContainer packet2 = entity.shouldUpdateMeta() ? entityMetadataPacket(entity) : null;

        PacketContainer packet3 = null;
        if (entity.shouldUpdateMeta()) {
            packet3 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> data = new ArrayList<>();
            packet3.getIntegers().write(0, entity.getEntityId());
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack item = entity.getItem(slot);
                data.add(new Pair<>(equipmentSlotToWrapper(slot), item == null ? new ItemStack(Material.AIR) : item));
            }
            packet3.getSlotStackPairLists().write(0, data);
        }

        sendPackets(players,packet1,packet2,packet3);
    }

    protected void spawnItem(Collection<? extends Player> players, PacketItem entity) {
        if (entity.getItemStack().getType().equals(Material.AIR))
            return;
        PacketContainer packet1 = spawnEntityPacket(entity, entity.getVelocity());
        PacketContainer packet2 = entityMetadataPacket(entity);
        PacketContainer packet3 = entityVelocityPacket(entity, entity.getVelocity());

        sendPackets(players,packet1,packet2,packet3);
    }

    protected void updateItem(Collection<? extends Player> players, PacketItem entity) {
        if (entity.getItemStack().getType().equals(Material.AIR))
            return;
        PacketContainer packet1 = entity.shouldUpdateMeta() ? entityMetadataPacket(entity) : null;
        PacketContainer packet2 = entity.shouldUpdateMeta() ? entityMetadataPacket(entity) : null;
        PacketContainer packet3 = entityVelocityPacket(entity, entity.getVelocity());
        sendPackets(players,packet1,packet2,packet3);
    }

    protected void spawnItemFrame(Collection<? extends Player> players, PacketItemFrame entity) {
        PacketContainer packet1 = spawnEntityPacket(entity, null, switch (entity.getAttachedFace()) {
            //           case DOWN -> 0;
            case UP -> 1;
            case NORTH -> 2;
            case SOUTH -> 3;
            case WEST -> 4;
            case EAST -> 5;
            default -> 0;
        });
        PacketContainer packet2 = entityMetadataPacket(entity);
        sendPackets(players,packet1,packet2);
    }

    protected void updateItemFrame(Collection<? extends Player> players, PacketItemFrame entity) {
        PacketContainer packet1 = entity.shouldUpdateMeta() ? entityMetadataPacket(entity) : null;
        sendPackets(players,packet1);
    }

    protected void entityDestroyPacket(Collection<? extends Player> players, PacketEntity entity) {
        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet1.getIntLists().write(0, List.of(entity.getEntityId()));
        if (!plugin.isEnabled())
            return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : players)
                protocolManager.sendServerPacket(player, packet1);
        }, 1L);
    }

    protected void spawnHologram(Collection<? extends Player> players, PacketHologram entity) {
        PacketContainer packet1 = spawnEntityPacket(entity);
        PacketContainer packet2 = entityMetadataPacket(entity);
        PacketContainer packet3 = entityVelocityPacket(entity, new Vector(0, 0, 0));

        sendPackets(players,packet1,packet2,packet3);
    }

    protected void updateHologram(Collection<? extends Player> players, PacketHologram entity) {
        PacketContainer packet1 = entity.shouldUpdatePosition() ? entityTeleportPacket(entity) : null;
        PacketContainer packet2 = entity.shouldUpdateMeta() ? entityMetadataPacket(entity) : null;
        sendPackets(players,packet1,packet2);
    }

    protected void spawnDisplayBlock(Collection<Player> players, PacketDisplayBlock entity) {
        PacketContainer packet1 = spawnEntityPacket(entity);
        PacketContainer packet2 = entityMetadataPacket(entity);
        PacketContainer packet3 = entityVelocityPacket(entity, new Vector(0, 0, 0));

        sendPackets(players,packet1,packet2,packet3);
    }

    protected void updateDisplayBlock(Collection<Player> players, PacketDisplayBlock entity) {
        PacketContainer packet1 = entity.shouldUpdatePosition() ? entityTeleportPacket(entity) : null;
        PacketContainer packet2 = entity.shouldUpdateMeta() ? entityMetadataPacket(entity) : null;
        sendPackets(players,packet1,packet2);
    }


    protected void spawnDisplayItem(Collection<Player> players, PacketDisplayItem entity) {
        PacketContainer packet1 = spawnEntityPacket(entity);
        PacketContainer packet2 = entityMetadataPacket(entity);
        PacketContainer packet3 = entityVelocityPacket(entity, new Vector(0, 0, 0));

        sendPackets(players,packet1,packet2,packet3);
    }

    protected void updateDisplayItem(Collection<Player> players, PacketDisplayItem entity) {
        PacketContainer packet1 = entity.shouldUpdatePosition() ? entityTeleportPacket(entity) : null;
        PacketContainer packet2 = entity.shouldUpdateMeta() ? entityMetadataPacket(entity) : null;
        sendPackets(players,packet1,packet2);
    }

    void trackPacketEntity(PacketEntity p) {
        packetEntities.add(p);
    }

    private PacketContainer spawnEntityPacket(PacketEntity entity) {
        return spawnEntityPacket(entity, null, 0);
    }

    private PacketContainer spawnEntityPacket(PacketEntity entity, Vector speed) {
        return spawnEntityPacket(entity, speed, 0);
    }

    private PacketContainer spawnEntityPacket(PacketEntity entity, Vector speed, int data) {
        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet1.getIntegers().write(0, entity.getEntityId());
        packet1.getUUIDs().write(0, entity.getUniqueId());
        packet1.getEntityTypeModifier().write(0, entity.getType());

        packet1.getIntegers().write(1, data);//Data
        if (speed == null) {
            packet1.getIntegers().write(2, 0); //speed
            packet1.getIntegers().write(3, 0); //speed
            packet1.getIntegers().write(4, 0); //speed
        } else {
            packet1.getIntegers().write(2, (int) (speed.getX() * 8000.0D));
            packet1.getIntegers().write(3, (int) (speed.getY() * 8000.0D));
            packet1.getIntegers().write(4, (int) (speed.getZ() * 8000.0D));
        }

        Location location = entity.getLocation();
        packet1.getDoubles().write(0, location.getX());
        packet1.getDoubles().write(1, location.getY());
        packet1.getDoubles().write(2, location.getZ());
        packet1.getBytes().write(0, (byte) (int) (location.getYaw() * 256.0F / 360.0F));
        packet1.getBytes().write(1, (byte) (int) (location.getPitch() * 256.0F / 360.0F));
        packet1.getBytes().write(2, (byte) (int) (location.getYaw() * 256.0F / 360.0F));
        return packet1;
    }

    private PacketContainer entityMetadataPacket(PacketEntity entity) {
        PacketContainer tempPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        tempPacket.getIntegers().write(0, entity.getEntityId());
        WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
        WatchableCollection.writeMetadataPacket(tempPacket, wpw);
        return tempPacket;
    }

    private PacketContainer entityTeleportPacket(PacketEntity entity) {
        PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet2.getIntegers().write(0, entity.getEntityId());
        packet2.getDoubles().write(0, entity.getLocation().getX());
        packet2.getDoubles().write(1, entity.getLocation().getY());
        packet2.getDoubles().write(2, entity.getLocation().getZ());
        packet2.getBytes().write(0, (byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F));
        packet2.getBytes().write(1, (byte) (int) (entity.getLocation().getPitch() * 256.0F / 360.0F));
        return packet2;
    }

    private PacketContainer entityVelocityPacket(PacketEntity entity, @NotNull Vector speed) {
        PacketContainer packet3 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
        packet3.getIntegers().write(0, entity.getEntityId());
        packet3.getIntegers().write(1, (int) (speed.getX() * 8000.0D));
        packet3.getIntegers().write(2, (int) (speed.getY() * 8000.0D));
        packet3.getIntegers().write(3, (int) (speed.getZ() * 8000.0D));
        return packet3;
    }

    private void sendPackets(Collection<? extends Player> targets,final PacketContainer... packets){
        if (!plugin.isEnabled())
            return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : targets) {
                for (PacketContainer packet:packets)
                    if (packet != null)
                        protocolManager.sendServerPacket(player, packet);
            }
        });
    }
}
