package emanondev.core.packetentity;

import java.util.Optional;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import emanondev.core.MCVersion;
import net.md_5.bungee.chat.ComponentSerializer;

class WatchableCollection {
	
	private static int metaversion = MCVersion.getCurrentVersion().getMetaVersion();
	  
	  private static WrappedDataWatcher.Serializer booleanSerializer;
	  
	  private static WrappedDataWatcher.Serializer stringSerializer;
	  
	  private static WrappedDataWatcher.Serializer byteSerializer;
	  
	  private static WrappedDataWatcher.Serializer intSerializer;
	  
	  private static WrappedDataWatcher.Serializer itemSerializer;
	  
	  private static WrappedDataWatcher.Serializer optChatSerializer;
	  
	  private static WrappedDataWatcher.Serializer vectorSerializer;
	  
	  public static void setup() {
	    booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
	    stringSerializer = WrappedDataWatcher.Registry.get(String.class);
	    byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
	    intSerializer = WrappedDataWatcher.Registry.get(Integer.class);
	    itemSerializer = WrappedDataWatcher.Registry.getItemStackSerializer(false);
	    if (!MCVersion.getCurrentVersion().isLegacy())
	      optChatSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true); 
	    vectorSerializer = WrappedDataWatcher.Registry.getVectorSerializer();
	  }
	  
	  public static WrappedDataWatcher getWatchableCollection(PacketArmorStand stand) {
	    WrappedDataWatcher watcher = new WrappedDataWatcher();
	    byte bitmask = 0;
	    bitmask = !stand.isVisible() ? (byte)(bitmask | 0x20) : bitmask;
	    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, byteSerializer), Byte.valueOf(bitmask));
	    switch (metaversion) {
	      case 0:
	        if (stand.getCustomName() != null && !stand.getCustomName().toPlainText().equals("")) {
	          watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, stringSerializer), stand.getCustomName().toLegacyText());//LanguageUtils.convert(stand.getCustomName(), InteractionVisualizer.language).toLegacyText());
	          break;
	        } 
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, stringSerializer), "");
	        break;
	      case 1:
	      case 2:
	      case 3:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, optChatSerializer), Optional.of(WrappedChatComponent.fromJson(ComponentSerializer.toString(stand.getCustomName())).getHandle()));
	        break;
	    } 
	    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, booleanSerializer), Boolean.valueOf(stand.isCustomNameVisible()));
	    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(4, booleanSerializer), Boolean.valueOf(stand.isSilent()));
	    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, booleanSerializer), Boolean.valueOf(!stand.hasGravity()));
	    byte standbitmask = 0;
	    standbitmask = stand.isSmall() ? (byte)(standbitmask | 0x1) : standbitmask;
	    standbitmask = stand.hasArms() ? (byte)(standbitmask | 0x4) : standbitmask;
	    standbitmask = !stand.hasBasePlate() ? (byte)(standbitmask | 0x8) : standbitmask;
	    standbitmask = stand.isMarker() ? (byte)(standbitmask | 0x10) : standbitmask;
	    switch (metaversion) {
	      case 0:
	      case 1:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(11, byteSerializer), Byte.valueOf(standbitmask));
	        break;
	      case 2:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(13, byteSerializer), Byte.valueOf(standbitmask));
	        break;
	      case 3:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, byteSerializer), Byte.valueOf(standbitmask));
	        break;
	    } 
	    Vector3F headrotation = new Vector3F();
	    headrotation.setX((float)Math.toDegrees(stand.getHeadPose().getX()));
	    headrotation.setY((float)Math.toDegrees(stand.getHeadPose().getY()));
	    headrotation.setZ((float)Math.toDegrees(stand.getHeadPose().getZ()));
	    switch (metaversion) {
	      case 0:
	      case 1:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(12, vectorSerializer), headrotation);
	        break;
	      case 2:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, vectorSerializer), headrotation);
	        break;
	      case 3:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, vectorSerializer), headrotation);
	        break;
	    } 
	    Vector3F rightarmrotation = new Vector3F();
	    rightarmrotation.setX((float)Math.toDegrees(stand.getRightArmPose().getX()));
	    rightarmrotation.setY((float)Math.toDegrees(stand.getRightArmPose().getY()));
	    rightarmrotation.setZ((float)Math.toDegrees(stand.getRightArmPose().getZ()));
	    switch (metaversion) {
	      case 0:
	      case 1:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, vectorSerializer), rightarmrotation);
	        break;
	      case 2:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(17, vectorSerializer), rightarmrotation);
	        break;
	      case 3:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(18, vectorSerializer), rightarmrotation);
	        break;
	    } 
	    return watcher;
	  }
	  
	  public static WrappedDataWatcher getWatchableCollection(PacketItem item) {
	    WrappedDataWatcher watcher = new WrappedDataWatcher();
	    byte bitmask = 0;
	    bitmask = item.isGlowing() ? (byte)(bitmask | 0x40) : bitmask;
	    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, byteSerializer), Byte.valueOf(bitmask));
	    switch (metaversion) {
	      case 0:
	        if (item.getCustomName() != null && !item.getCustomName().toPlainText().equals("")) {
	          watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, stringSerializer),  item.getCustomName().toLegacyText());//LanguageUtils.convert(item.getCustomName(), InteractionVisualizer.language).toLegacyText());
	          break;
	        } 
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, stringSerializer), "");
	        break;
	      case 1:
	      case 2:
	      case 3:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, optChatSerializer), Optional.of(WrappedChatComponent.fromJson(ComponentSerializer.toString(item.getCustomName())).getHandle()));
	        break;
	    } 
	    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, booleanSerializer), Boolean.valueOf(item.isCustomNameVisible()));
	    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, booleanSerializer), Boolean.valueOf(!item.hasGravity()));
	    switch (metaversion) {
	      case 0:
	      case 1:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, itemSerializer), item.getItemStack());
	        break;
	      case 2:
	      case 3:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(7, itemSerializer), item.getItemStack());
	        break;
	    } 
	    return watcher;
	  }
	  
	  public static WrappedDataWatcher getWatchableCollection(PacketItemFrame frame) {
	    WrappedDataWatcher watcher = new WrappedDataWatcher();
	    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(4, booleanSerializer), Boolean.valueOf(frame.isSilent()));
	    switch (metaversion) {
	      case 0:
	      case 1:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, itemSerializer), frame.getItem());
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(7, intSerializer), Integer.valueOf(frame.getFrameRotation()));
	        break;
	      case 2:
	      case 3:
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(7, itemSerializer), frame.getItem());
	        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(8, intSerializer), Integer.valueOf(frame.getFrameRotation()));
	        break;
	    } 
	    return watcher;
	  }
	}
	
	
	/*
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
	}*/
	

