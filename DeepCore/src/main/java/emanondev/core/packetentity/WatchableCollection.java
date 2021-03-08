package emanondev.core.packetentity;

import java.util.Optional;

import org.bukkit.Bukkit;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

class WatchableCollection {
	
	private static Serializer booleanSerializer;
	@SuppressWarnings("unused")
	private static Serializer stringSerializer;
	private static Serializer byteSerializer;
	private static Serializer intSerializer;
	private static Serializer itemSerializer;
	private static Serializer optChatSerializer;
	private static Serializer vectorSerializer;
	private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);
	
	public static void setup() {
		booleanSerializer = Registry.get(Boolean.class);
		stringSerializer = Registry.get(String.class);
		byteSerializer = Registry.get(Byte.class);
		intSerializer = Registry.get(Integer.class);
		itemSerializer = Registry.getItemStackSerializer(false);
		//if (!version.contains("legacy")) {
			optChatSerializer = Registry.getChatComponentSerializer(true);
		//}
		vectorSerializer = Registry.getVectorSerializer();
	}
	
	public static WrappedDataWatcher getWatchableCollection(PacketArmorStand stand) {
		WrappedDataWatcher watcher = new WrappedDataWatcher();
		
		byte bitmask = (byte) 0;
		bitmask = !stand.isVisible() ? (byte) (bitmask | 0x20) : bitmask;
		watcher.setObject(new WrappedDataWatcherObject(0, byteSerializer), bitmask);
		
		watcher.setObject(new WrappedDataWatcherObject(2, optChatSerializer), Optional.of(WrappedChatComponent.fromChatMessage(stand.getCustomName())[0].getHandle()));
		
		watcher.setObject(new WrappedDataWatcherObject(3, booleanSerializer), stand.isCustomNameVisible());
		watcher.setObject(new WrappedDataWatcherObject(4, booleanSerializer), stand.isSilent());
		watcher.setObject(new WrappedDataWatcherObject(5, booleanSerializer), !stand.hasGravity());
		
		byte standbitmask = (byte) 0;
		standbitmask = stand.isSmall() ? (byte) (standbitmask | 0x01) : standbitmask;
		standbitmask = stand.hasArms() ? (byte) (standbitmask | 0x04) : standbitmask;
		standbitmask = !stand.hasBasePlate() ? (byte) (standbitmask | 0x08) : standbitmask;
		standbitmask = stand.isMarker() ? (byte) (standbitmask | 0x10) : standbitmask;
		
		if (VERSION.contains("1_16")||VERSION.contains("1_15")) {
			watcher.setObject(new WrappedDataWatcherObject(14, byteSerializer), standbitmask);
			
		} else if (VERSION.contains("1_14")) {
			
			watcher.setObject(new WrappedDataWatcherObject(13, byteSerializer), standbitmask);
		} else if (VERSION.contains("1_13")) {
			
			watcher.setObject(new WrappedDataWatcherObject(11, byteSerializer), standbitmask);
		} else
			throw new UnsupportedOperationException();
		

		Vector3F headrotation = new Vector3F();
		headrotation.setX((float) Math.toDegrees(stand.getHeadPose().getX()));
		headrotation.setY((float) Math.toDegrees(stand.getHeadPose().getY()));
		headrotation.setZ((float) Math.toDegrees(stand.getHeadPose().getZ()));
		

		if (VERSION.contains("1_16")||VERSION.contains("1_15")) {
			watcher.setObject(new WrappedDataWatcherObject(15, vectorSerializer), headrotation);
			
		} else if (VERSION.contains("1_14")) {
			watcher.setObject(new WrappedDataWatcherObject(14, vectorSerializer), headrotation);
		} else if (VERSION.contains("1_13")) {
			watcher.setObject(new WrappedDataWatcherObject(12, vectorSerializer), headrotation);
		} else
			throw new UnsupportedOperationException();
		
		
		Vector3F rightarmrotation = new Vector3F();
		rightarmrotation.setX((float) Math.toDegrees(stand.getRightArmPose().getX()));
		rightarmrotation.setY((float) Math.toDegrees(stand.getRightArmPose().getY()));
		rightarmrotation.setZ((float) Math.toDegrees(stand.getRightArmPose().getZ()));
		

		if (VERSION.contains("1_16")||VERSION.contains("1_15")) {
			watcher.setObject(new WrappedDataWatcherObject(18, vectorSerializer), rightarmrotation);
		} else if (VERSION.contains("1_14")) {
			watcher.setObject(new WrappedDataWatcherObject(17, vectorSerializer), rightarmrotation);
		} else if (VERSION.contains("1_13")) {
			watcher.setObject(new WrappedDataWatcherObject(15, vectorSerializer), rightarmrotation);
		} else
			throw new UnsupportedOperationException();
		
		return watcher;
	}
	
	public static WrappedDataWatcher getWatchableCollection(PacketItem item) {
		WrappedDataWatcher watcher = new WrappedDataWatcher();
			
		byte bitmask = (byte) 0;
		bitmask = item.isGlowing() ? (byte) (bitmask | 0x40) : bitmask;
		watcher.setObject(new WrappedDataWatcherObject(0, byteSerializer), bitmask);
		
		watcher.setObject(new WrappedDataWatcherObject(2, optChatSerializer), Optional.of(WrappedChatComponent.fromChatMessage(item.getCustomName())[0].getHandle()));
		
		watcher.setObject(new WrappedDataWatcherObject(3, booleanSerializer), item.isCustomNameVisible());
		watcher.setObject(new WrappedDataWatcherObject(5, booleanSerializer), !item.hasGravity());

		if (VERSION.contains("1_16")||VERSION.contains("1_15")||VERSION.contains("1_14")) {
			watcher.setObject(new WrappedDataWatcherObject(7, itemSerializer), item.getItemStack());
		} else if (VERSION.contains("1_13")) {
			watcher.setObject(new WrappedDataWatcherObject(6, itemSerializer), item.getItemStack());
		} else
			throw new UnsupportedOperationException();
		return watcher;
	}
	
	public static WrappedDataWatcher getWatchableCollection(PacketItemFrame frame) {
		WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(new WrappedDataWatcherObject(4, booleanSerializer), frame.isSilent());
		if (VERSION.contains("1_16")||VERSION.contains("1_15")||VERSION.contains("1_14")) {
			watcher.setObject(new WrappedDataWatcherObject(7, itemSerializer), frame.getItem());
			watcher.setObject(new WrappedDataWatcherObject(8, intSerializer), frame.getFrameRotation());
		} else if (VERSION.contains("1_13")) {
			watcher.setObject(new WrappedDataWatcherObject(6, itemSerializer), frame.getItem());
			watcher.setObject(new WrappedDataWatcherObject(7, intSerializer), frame.getFrameRotation());
		} else
			throw new UnsupportedOperationException();
		
		return watcher;
	}

}
