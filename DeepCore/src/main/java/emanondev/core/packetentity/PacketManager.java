package emanondev.core.packetentity;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

public class PacketManager {
	
	private final Plugin plugin;
	public static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();
	public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);


	public PacketManager(Plugin plugin) {
		if (plugin==null)
			throw new NullPointerException();
		this.plugin = plugin;
		WatchableCollection.setup();
	}
	
	private Set<PacketEntity> packetEntities = Collections.newSetFromMap(
	        new WeakHashMap<PacketEntity, Boolean>());
	

	public void sendHandMovement(List<Player> players, Player entity) {
		PacketContainer packet1 = PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ANIMATION);
		packet1.getModifier().writeDefaults();
		packet1.getIntegers().write(0, entity.getEntityId());
		packet1.getIntegers().write(1, 0);
		
		if (!plugin.isEnabled()) {
			return;
		}
		Bukkit.getScheduler().runTask(plugin, () -> {
			try {
	        	for (Player player : players) {
	        		PROTOCOL_MANAGER.sendServerPacket(player, packet1);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	public PacketItem getPacketItem(Location loc) {
		PacketItem p = new PacketItem(loc,this);
		packetEntities.add(p);
		return p;
	}

	public PacketItemFrame getPacketItemFrame(Location loc) {
		PacketItemFrame p = new PacketItemFrame(loc,this);
		packetEntities.add(p);
		return p;
	}

	public PacketArmorStand getPacketArmorStand(Location loc) {
		PacketArmorStand p = new PacketArmorStand(loc,this);
		packetEntities.add(p);
		return p;
	}
	public Plugin getPlugin() {
		return plugin;
	}

	public void clearAll() {
		for (PacketEntity pEntity:packetEntities)
			pEntity.remove();
	}

}
