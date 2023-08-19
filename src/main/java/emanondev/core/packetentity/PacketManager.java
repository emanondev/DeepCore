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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PacketManager {

    private final Plugin plugin;
    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private final Set<PacketEntity> packetEntities = Collections.newSetFromMap(new WeakHashMap<>());

    public PacketManager(Plugin plugin) {
        if (plugin == null)
            throw new NullPointerException();
        this.plugin = plugin;
        WatchableCollection.setup();
        if (!GameVersion.isNewerEqualsTo(1, 16, 0))
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

    public PacketItem getPacketItem(Location l) {
        PacketItem p = new PacketItem(l, this);
        packetEntities.add(p);
        return p;
    }

    public PacketArmorStand getPacketArmorStand(Location l) {
        PacketArmorStand p = new PacketArmorStand(l, this);
        packetEntities.add(p);
        return p;
    }

    public PacketItemFrame getPacketItemFrame(Location l) {
        PacketItemFrame p = new PacketItemFrame(l, this);
        packetEntities.add(p);
        return p;
    }

    public void spawnArmorStand(Collection<Player> players, PacketArmorStand entity) {
        PacketContainer packet1 = protocolManager.createPacket(GameVersion.isNewerEqualsTo(1, 19, 0) ? PacketType.Play.Server.SPAWN_ENTITY : PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        packet1.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
        if (packet1.getUUIDs().size() > 0)
            packet1.getUUIDs().write(0, entity.getUniqueId());
        int i = 1;
        if (GameVersion.isOlderThan(1, 19, 0)) {
            packet1.getIntegers().write(i++, 1);
        } else {
            packet1.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        }
        packet1.getIntegers().write(i++, (int) (entity.getVelocity().getX() * 8000.0D));
        packet1.getIntegers().write(i++, (int) (entity.getVelocity().getY() * 8000.0D));
        packet1.getIntegers().write(i++, (int) (entity.getVelocity().getZ() * 8000.0D));
        packet1.getDoubles().write(0, entity.getLocation().getX());
        packet1.getDoubles().write(1, entity.getLocation().getY());
        packet1.getDoubles().write(2, entity.getLocation().getZ());
        packet1.getBytes().write(0, (byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F));
        packet1.getBytes().write(1, (byte) (int) (entity.getLocation().getPitch() * 256.0F / 360.0F));
        packet1.getBytes().write(2, (byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F)); //what?
        if (packet1.getIntegers().size() > i)
            packet1.getIntegers().write(i, 0);
        PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet2.getIntegers().write(0, entity.getEntityId());
        WrappedDataWatcher wpw = entity.updateAndGetWrappedDataWatcher();
        WatchableCollection.writeMetadataPacket(packet2, wpw);

        /*
        List<ValuePairs<EquipmentSlot, ItemStack>> equipments = new ArrayList<>();
        equipments.add(new ValuePairs(EquipmentSlot.HAND, entity.getItemInMainHand()));
        equipments.add(new ValuePairs(EquipmentSlot.HEAD, entity.getHelmet()));
        PacketContainer[] packet3 = nms.createEntityEquipmentPacket(entity.getEntityId(), equipments);
        */
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
        //end

        if (!plugin.isEnabled())
            return;
        boolean finalHasItems = hasItems;
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : players) {
                if (player.isOnline()) {
                    protocolManager.sendServerPacket(player, packet1);
                    protocolManager.sendServerPacket(player, packet2);
                    if (finalHasItems)
                    //for (PacketContainer packet : packet3)
                        protocolManager.sendServerPacket(player, packet3);
                }
            }
        });
    }


/* 19-8-23
    protected void spawnArmorStand(Collection<? extends Player> players, PacketArmorStand entity) {
        PacketContainer packet1 = protocolManager.createPacket(GameVersion.isNewerEqualsTo(1, 19, 0) ? PacketType.Play.Server.SPAWN_ENTITY : PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        packet1.getIntegers().write(0, entity.getEntityId());
        if (packet1.getUUIDs().size() > 0)
            packet1.getUUIDs().write(0, entity.getUniqueId());
        int i = 1;
        if (GameVersion.isOlderThan(1, 19, 0)) {
            packet1.getIntegers().write(i++, 1);
        } else {
            packet1.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        }
        packet1.getIntegers().write(i++, (int) (entity.getVelocity().getX() * 8000.0D));
        packet1.getIntegers().write(i++, (int) (entity.getVelocity().getY() * 8000.0D));
        packet1.getIntegers().write(i++, (int) (entity.getVelocity().getZ() * 8000.0D));
        packet1.getDoubles().write(0, entity.getLocation().getX());
        packet1.getDoubles().write(1, entity.getLocation().getY());
        packet1.getDoubles().write(2, entity.getLocation().getZ());
        packet1.getBytes().write(0, (byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F));
        packet1.getBytes().write(1, (byte) (int) (entity.getLocation().getPitch() * 256.0F / 360.0F));
        packet1.getBytes().write(2, (byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F)); //why?
        if (packet1.getIntegers().size() > i)
            packet1.getIntegers().write(i, 0);
        PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet2.getIntegers().write(0, entity.getEntityId());
        WrappedDataWatcher wpw = entity.updateAndGetWrappedDataWatcher();
        packet2.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());

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

        if (!plugin.isEnabled())
            return;
        boolean finalHasItems = hasItems;
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet1);
                protocolManager.sendServerPacket(player, packet2);
                if (finalHasItems)
                    protocolManager.sendServerPacket(player, packet3);

            }
        });
    }*/

    protected void updateArmorStand(Collection<? extends Player> players, PacketArmorStand entity) {
        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet1.getIntegers().write(0, entity.getEntityId());
        packet1.getDoubles().write(0, entity.getLocation().getX());
        packet1.getDoubles().write(1, entity.getLocation().getY());
        packet1.getDoubles().write(2, entity.getLocation().getZ());
        packet1.getBytes().write(0, (byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F));
        packet1.getBytes().write(1, (byte) (int) (entity.getLocation().getPitch() * 256.0F / 360.0F));
        PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet2.getIntegers().write(0, entity.getEntityId());
        WrappedDataWatcher wpw = entity.updateAndGetWrappedDataWatcher();
        packet2.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());


        PacketContainer packet3 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> data = new ArrayList<>();
        packet3.getIntegers().write(0, entity.getEntityId());
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = entity.getItem(slot);
            data.add(new Pair<>(equipmentSlotToWrapper(slot), item == null ? new ItemStack(Material.AIR) : item));
        }
        packet3.getSlotStackPairLists().write(0, data);

        if (!plugin.isEnabled())
            return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet1);
                protocolManager.sendServerPacket(player, packet2);
                protocolManager.sendServerPacket(player, packet3);
            }
        });
    }

    protected void updateArmorStandOnlyMeta(Collection<? extends Player> players, PacketArmorStand entity) {
        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet1.getIntegers().write(0, entity.getEntityId());
        WrappedDataWatcher wpw = entity.updateAndGetWrappedDataWatcher();
        packet1.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
        if (!plugin.isEnabled())
            return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : players)
                protocolManager.sendServerPacket(player, packet1);
        });
    }

    protected void removeArmorStand(Collection<? extends Player> players, PacketArmorStand entity) {
        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet1.getIntegers().write(0, 1);
        packet1.getIntegerArrays().write(0,new int[]{entity.getEntityId()});
        if (!plugin.isEnabled())
            return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : players)
                protocolManager.sendServerPacket(player, packet1);
        }, 1L);
    }

    protected void spawnItem(Collection<? extends Player> players, PacketItem entity) {
        if (entity.getItemStack().getType().equals(Material.AIR))
            return;
        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet1.getIntegers().write(0, entity.getEntityId());
        if (packet1.getUUIDs().size() > 0)
            packet1.getUUIDs().write(0, entity.getUniqueId());
        if (GameVersion.isNewerEqualsTo(1, 19, 0)) {
            int i = 1;
            packet1.getEntityTypeModifier().write(0, entity.getType());
            packet1.getIntegers().write(i++, (int) (entity.getVelocity().getX() * 8000.0D));
            packet1.getIntegers().write(i++, (int) (entity.getVelocity().getY() * 8000.0D));
            packet1.getIntegers().write(i++, (int) (entity.getVelocity().getZ() * 8000.0D));
            Location location = entity.getLocation();
            packet1.getDoubles().write(0, location.getX());
            packet1.getDoubles().write(1, location.getY());
            packet1.getDoubles().write(2, location.getZ());
            packet1.getBytes().write(0, (byte) (int) (location.getYaw() * 256.0F / 360.0F));
            packet1.getBytes().write(1, (byte) (int) (location.getPitch() * 256.0F / 360.0F));
            packet1.getBytes().write(2, (byte) (int) (location.getYaw() * 256.0F / 360.0F));
            if (packet1.getIntegers().size() > i)
                packet1.getIntegers().write(i, 0);
        } else {
            packet1.getIntegers().write(1, (int) (entity.getVelocity().getX() * 8000.0D));
            packet1.getIntegers().write(2, (int) (entity.getVelocity().getY() * 8000.0D));
            packet1.getIntegers().write(3, (int) (entity.getVelocity().getZ() * 8000.0D));
            Location location = entity.getLocation();
            packet1.getIntegers().write(4, (int) (location.getPitch() * 256.0F / 360.0F));
            packet1.getIntegers().write(5, (int) (location.getYaw() * 256.0F / 360.0F));
            packet1.getEntityTypeModifier().write(0, entity.getType());
            packet1.getIntegers().write(6, 1);
            packet1.getUUIDs().write(0, entity.getUniqueId());
            packet1.getDoubles().write(0, location.getX());
            packet1.getDoubles().write(1, location.getY());
            packet1.getDoubles().write(2, location.getZ());
        }
        PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet2.getIntegers().write(0, entity.getEntityId());
        WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
        packet2.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
        PacketContainer packet3 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
        packet3.getIntegers().write(0, entity.getEntityId());
        packet3.getIntegers().write(1, (int) (entity.getVelocity().getX() * 8000.0D));
        packet3.getIntegers().write(2, (int) (entity.getVelocity().getY() * 8000.0D));
        packet3.getIntegers().write(3, (int) (entity.getVelocity().getZ() * 8000.0D));
        if (!plugin.isEnabled())
            return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet1);
                protocolManager.sendServerPacket(player, packet2);
                protocolManager.sendServerPacket(player, packet3);
            }
        });
    }

    protected void updateItem(Collection<? extends Player> players, PacketItem entity) {
        if (entity.getItemStack().getType().equals(Material.AIR))
            return;
        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet1.getIntegers().write(0, entity.getEntityId());
        WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
        packet1.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
        PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet2.getIntegers().write(0, entity.getEntityId());
        packet2.getDoubles().write(0, entity.getLocation().getX());
        packet2.getDoubles().write(1, entity.getLocation().getY());
        packet2.getDoubles().write(2, entity.getLocation().getZ());
        packet2.getBytes().write(0, (byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F));
        packet2.getBytes().write(1, (byte) (int) (entity.getLocation().getPitch() * 256.0F / 360.0F));
        PacketContainer packet3 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
        packet3.getIntegers().write(0, entity.getEntityId());
        packet3.getIntegers().write(1, (int) (entity.getVelocity().getX() * 8000.0D));
        packet3.getIntegers().write(2, (int) (entity.getVelocity().getY() * 8000.0D));
        packet3.getIntegers().write(3, (int) (entity.getVelocity().getZ() * 8000.0D));
        if (!plugin.isEnabled())
            return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet1);
                protocolManager.sendServerPacket(player, packet2);
                protocolManager.sendServerPacket(player, packet3);
            }
        });
    }

    protected void removeItem(Collection<? extends Player> players, PacketItem entity) {
        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet1.getIntegers().write(0, 1);
        packet1.getIntegerArrays().write(0,new int[]{entity.getEntityId()});
        if (!plugin.isEnabled())
            return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : players)
                protocolManager.sendServerPacket(player, packet1);
        }, 1L);
    }

    protected void spawnItemFrame(Collection<? extends Player> players, PacketItemFrame entity) {
        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet1.getIntegers().write(0, entity.getEntityId());
        packet1.getIntegers().write(1, 0);
        packet1.getIntegers().write(2, 0);
        packet1.getIntegers().write(3, 0);
        packet1.getIntegers().write(4, (int) (entity.getPitch() * 256.0F / 360.0F));
        packet1.getIntegers().write(5, (int) (entity.getYaw() * 256.0F / 360.0F));

        packet1.getEntityTypeModifier().write(0, entity.getType());
        packet1.getIntegers().write(6, getItemFrameData(entity));

        packet1.getUUIDs().write(0, entity.getUniqueId());
        Location location = entity.getLocation();
        packet1.getDoubles().write(0, location.getX());
        packet1.getDoubles().write(1, location.getY());
        packet1.getDoubles().write(2, location.getZ());
        PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet2.getIntegers().write(0, entity.getEntityId());
        WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
        packet2.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
        if (!plugin.isEnabled())
            return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet1);
                protocolManager.sendServerPacket(player, packet2);
            }
        });
    }

    protected int getItemFrameData(PacketItemFrame frame) {
        return switch (frame.getAttachedFace()) {
            case DOWN -> 0;
            case UP -> 1;
            case NORTH -> 2;
            case SOUTH -> 3;
            case WEST -> 4;
            case EAST -> 5;
            default -> 0;
        };
    }

    protected void updateItemFrame(Collection<? extends Player> players, PacketItemFrame entity) {
        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet1.getIntegers().write(0, entity.getEntityId());
        WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
        packet1.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
        if (!plugin.isEnabled())
            return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : players)
                protocolManager.sendServerPacket(player, packet1);
        });
    }

    protected void removeItemFrame(Collection<? extends Player> players, PacketItemFrame entity) {
        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet1.getIntegers().write(0, 1);
        packet1.getIntegerArrays().write(0,new int[]{entity.getEntityId()});
        if (!plugin.isEnabled())
            return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : players)
                protocolManager.sendServerPacket(player, packet1);
        }, 1L);
    }

    void trackPacketEntity(PacketEntity p) {
        packetEntities.add(p);
    }
}
