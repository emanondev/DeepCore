package emanondev.core.packetentity;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import emanondev.core.MCVersion;

public class PacketManager {

	public PacketManager(Plugin plugin) {
		if (plugin == null)
			throw new NullPointerException();
		this.plugin = plugin;
		WatchableCollection.setup();
		if (!MCVersion.getCurrentVersion().isNewerOrEqualTo(MCVersion.V1_16))
			throw new IllegalStateException("unsupported version");
	}

	private final Plugin plugin;

	private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

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

	/*
	 * public void sendHandMovement(Collection<? extends Player> players, Player
	 * entity) { if (!InteractionVisualizer.handMovementEnabled.booleanValue())
	 * return; PacketContainer packet1 =
	 * protocolManager.createPacket(PacketType.Play.Server.ANIMATION);
	 * packet1.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
	 * packet1.getIntegers().write(1, Integer.valueOf(0)); if (!plugin.isEnabled())
	 * return; Bukkit.getScheduler().runTask(plugin, () -> { try {
	 * protocolManager.sendServerPacket(entity, packet1); } catch
	 * (InvocationTargetException e) { e.printStackTrace(); } }); }
	 */

	protected void spawnArmorStand(Collection<? extends Player> players, PacketArmorStand entity) {
		PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		packet1.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		packet1.getIntegers().write(1, Integer.valueOf(1));
		packet1.getIntegers().write(2, Integer.valueOf((int) (entity.getVelocity().getX() * 8000.0D)));
		packet1.getIntegers().write(3, Integer.valueOf((int) (entity.getVelocity().getY() * 8000.0D)));
		packet1.getIntegers().write(4, Integer.valueOf((int) (entity.getVelocity().getZ() * 8000.0D)));
		packet1.getDoubles().write(0, Double.valueOf(entity.getLocation().getX()));
		packet1.getDoubles().write(1, Double.valueOf(entity.getLocation().getY()));
		packet1.getDoubles().write(2, Double.valueOf(entity.getLocation().getZ()));
		packet1.getBytes().write(0, Byte.valueOf((byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F)));
		packet1.getBytes().write(1, Byte.valueOf((byte) (int) (entity.getLocation().getPitch() * 256.0F / 360.0F)));
		packet1.getBytes().write(2, Byte.valueOf((byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F)));
		PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet2.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
		packet2.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
		PacketContainer packet3 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
		packet3.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));

		List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairs = new ArrayList<>();
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack item = entity.getItem(slot);
			if (item != null && !item.getType().isAir())
				pairs.add(new Pair<>(equipmentSlotToWrapper(slot), item));
		}

		packet3.getSlotStackPairLists().write(0, pairs);

		if (!plugin.isEnabled())
			return;
		Bukkit.getScheduler().runTask(plugin, () -> {
			try {
				for (Player player : players) {
					protocolManager.sendServerPacket(player, packet1);
					protocolManager.sendServerPacket(player, packet2);
					protocolManager.sendServerPacket(player, packet3);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	private final static EnumWrappers.ItemSlot equipmentSlotToWrapper(EquipmentSlot slot) {
		switch (slot) {
		case CHEST:
			return EnumWrappers.ItemSlot.CHEST;
		case FEET:
			return EnumWrappers.ItemSlot.FEET;
		case HAND:
			return EnumWrappers.ItemSlot.MAINHAND;
		case HEAD:
			return EnumWrappers.ItemSlot.HEAD;
		case LEGS:
			return EnumWrappers.ItemSlot.LEGS;
		case OFF_HAND:
			return EnumWrappers.ItemSlot.OFFHAND;
		}
		throw new IllegalStateException("unable to check equipment slot");
	}

	protected void updateArmorStand(Collection<? extends Player> players, PacketArmorStand entity) {
		PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
		packet1.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		packet1.getDoubles().write(0, Double.valueOf(entity.getLocation().getX()));
		packet1.getDoubles().write(1, Double.valueOf(entity.getLocation().getY()));
		packet1.getDoubles().write(2, Double.valueOf(entity.getLocation().getZ()));
		packet1.getBytes().write(0, Byte.valueOf((byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F)));
		packet1.getBytes().write(1, Byte.valueOf((byte) (int) (entity.getLocation().getPitch() * 256.0F / 360.0F)));
		PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet2.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
		packet2.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
		PacketContainer packet3 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
		packet3.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairs = new ArrayList<>();
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack item = entity.getItem(slot);
			if (item != null && !item.getType().isAir())
				pairs.add(new Pair<>(equipmentSlotToWrapper(slot), item));
		}
		packet3.getSlotStackPairLists().write(0, pairs);

		if (!plugin.isEnabled())
			return;
		Bukkit.getScheduler().runTask(plugin, () -> {
			try {
				for (Player player : players) {
					protocolManager.sendServerPacket(player, packet1);
					protocolManager.sendServerPacket(player, packet2);
					protocolManager.sendServerPacket(player, packet3);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	protected void updateArmorStandOnlyMeta(Collection<? extends Player> players, PacketArmorStand entity) {
		PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet1.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
		packet1.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
		if (!plugin.isEnabled())
			return;
		Bukkit.getScheduler().runTask(plugin, () -> {
			try {
				for (Player player : players)
					protocolManager.sendServerPacket(player, packet1);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	protected void removeArmorStand(Collection<? extends Player> players, PacketArmorStand entity) {
		PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet1.getIntegerArrays().write(0, new int[] { entity.getEntityId() });
		if (!plugin.isEnabled())
			return;
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			try {
				for (Player player : players)
					protocolManager.sendServerPacket(player, packet1);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}, 1L);
	}

	protected void spawnItem(Collection<? extends Player> players, PacketItem entity) {
		if (entity.getItemStack().getType().equals(Material.AIR))
			return;
		PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		packet1.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		packet1.getIntegers().write(1, Integer.valueOf((int) (entity.getVelocity().getX() * 8000.0D)));
		packet1.getIntegers().write(2, Integer.valueOf((int) (entity.getVelocity().getY() * 8000.0D)));
		packet1.getIntegers().write(3, Integer.valueOf((int) (entity.getVelocity().getZ() * 8000.0D)));
		packet1.getIntegers().write(4, Integer.valueOf((int) (entity.getLocation().getPitch() * 256.0F / 360.0F)));
		packet1.getIntegers().write(5, Integer.valueOf((int) (entity.getLocation().getYaw() * 256.0F / 360.0F)));
		
		packet1.getEntityTypeModifier().write(0, entity.getType());
		packet1.getIntegers().write(6, Integer.valueOf(1));
		
		packet1.getUUIDs().write(0, entity.getUniqueId());
		Location location = entity.getLocation();
		packet1.getDoubles().write(0, Double.valueOf(location.getX()));
		packet1.getDoubles().write(1, Double.valueOf(location.getY()));
		packet1.getDoubles().write(2, Double.valueOf(location.getZ()));
		PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet2.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
		packet2.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
		PacketContainer packet3 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
		packet3.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		packet3.getIntegers().write(1, Integer.valueOf((int) (entity.getVelocity().getX() * 8000.0D)));
		packet3.getIntegers().write(2, Integer.valueOf((int) (entity.getVelocity().getY() * 8000.0D)));
		packet3.getIntegers().write(3, Integer.valueOf((int) (entity.getVelocity().getZ() * 8000.0D)));
		if (!plugin.isEnabled())
			return;
		Bukkit.getScheduler().runTask(plugin, () -> {
			try {
				for (Player player : players) {
					protocolManager.sendServerPacket(player, packet1);
					protocolManager.sendServerPacket(player, packet2);
					protocolManager.sendServerPacket(player, packet3);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	protected void updateItem(Collection<? extends Player> players, PacketItem entity) {
		if (entity.getItemStack().getType().equals(Material.AIR))
			return;
		PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet1.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
		packet1.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
		PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
		packet2.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		packet2.getDoubles().write(0, Double.valueOf(entity.getLocation().getX()));
		packet2.getDoubles().write(1, Double.valueOf(entity.getLocation().getY()));
		packet2.getDoubles().write(2, Double.valueOf(entity.getLocation().getZ()));
		packet2.getBytes().write(0, Byte.valueOf((byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F)));
		packet2.getBytes().write(1, Byte.valueOf((byte) (int) (entity.getLocation().getPitch() * 256.0F / 360.0F)));
		PacketContainer packet3 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
		packet3.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		packet3.getIntegers().write(1, Integer.valueOf((int) (entity.getVelocity().getX() * 8000.0D)));
		packet3.getIntegers().write(2, Integer.valueOf((int) (entity.getVelocity().getY() * 8000.0D)));
		packet3.getIntegers().write(3, Integer.valueOf((int) (entity.getVelocity().getZ() * 8000.0D)));
		if (!plugin.isEnabled())
			return;
		Bukkit.getScheduler().runTask(plugin, () -> {
			try {
				for (Player player : players) {
					protocolManager.sendServerPacket(player, packet1);
					protocolManager.sendServerPacket(player, packet2);
					protocolManager.sendServerPacket(player, packet3);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	protected void removeItem(Collection<? extends Player> players, PacketItem entity) {
		PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet1.getIntegerArrays().write(0, new int[] { entity.getEntityId() });
		if (!plugin.isEnabled())
			return;
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			try {
				for (Player player : players)
					protocolManager.sendServerPacket(player, packet1);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}, 1L);
	}

	protected void spawnItemFrame(Collection<? extends Player> players, PacketItemFrame entity) {
		PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		packet1.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		packet1.getIntegers().write(1, Integer.valueOf(0));
		packet1.getIntegers().write(2, Integer.valueOf(0));
		packet1.getIntegers().write(3, Integer.valueOf(0));
		packet1.getIntegers().write(4, Integer.valueOf((int) (entity.getPitch() * 256.0F / 360.0F)));
		packet1.getIntegers().write(5, Integer.valueOf((int) (entity.getYaw() * 256.0F / 360.0F)));
		
		packet1.getEntityTypeModifier().write(0, entity.getType());
		packet1.getIntegers().write(6, Integer.valueOf(getItemFrameData(entity)));
		
		packet1.getUUIDs().write(0, entity.getUniqueId());
		Location location = entity.getLocation();
		packet1.getDoubles().write(0, Double.valueOf(location.getX()));
		packet1.getDoubles().write(1, Double.valueOf(location.getY()));
		packet1.getDoubles().write(2, Double.valueOf(location.getZ()));
		PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet2.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
		packet2.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
		if (!plugin.isEnabled())
			return;
		Bukkit.getScheduler().runTask(plugin, () -> {
			try {
				for (Player player : players) {
					protocolManager.sendServerPacket(player, packet1);
					protocolManager.sendServerPacket(player, packet2);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	protected int getItemFrameData(PacketItemFrame frame) {
		switch (frame.getAttachedFace()) {
		case DOWN:
			return 0;
		case UP:
			return 1;
		case NORTH:
			return 2;
		case SOUTH:
			return 3;
		case WEST:
			return 4;
		case EAST:
			return 5;
		default:
			break;
		}
		return 0;
	}

	protected void updateItemFrame(Collection<? extends Player> players, PacketItemFrame entity) {
		PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet1.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
		WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
		packet1.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
		if (!plugin.isEnabled())
			return;
		Bukkit.getScheduler().runTask(plugin, () -> {
			try {
				for (Player player : players)
					protocolManager.sendServerPacket(player, packet1);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	protected void removeItemFrame(Collection<? extends Player> players, PacketItemFrame entity) {
		PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet1.getIntegerArrays().write(0, new int[] { entity.getEntityId() });
		if (!plugin.isEnabled())
			return;
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			try {
				for (Player player : players)
					protocolManager.sendServerPacket(player, packet1);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}, 1L);
	}

	private Set<PacketEntity> packetEntities = Collections.newSetFromMap(new WeakHashMap<PacketEntity, Boolean>());

	void trackPacketEntity(PacketEntity p) {
		packetEntities.add(p);
	}
}
