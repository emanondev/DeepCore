package emanondev.core.packetentity;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class WatchableCollection {

    public static final WrappedDataWatcher.Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);

    public static final WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);

    public static final WrappedDataWatcher.Serializer intSerializer = WrappedDataWatcher.Registry.get(Integer.class);

    public static final WrappedDataWatcher.Serializer itemSerializer = WrappedDataWatcher.Registry.getItemStackSerializer(false);

    public static final WrappedDataWatcher.Serializer optChatSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
    public static final WrappedDataWatcher.Serializer chatSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer(false);

    public static final WrappedDataWatcher.Serializer vectorSerializer = WrappedDataWatcher.Registry.getVectorSerializer();
    public static final WrappedDataWatcher.Serializer floatSerializer = WrappedDataWatcher.Registry.get(Float.class);
    public static final WrappedDataWatcher.Serializer blockDataSerializer = WrappedDataWatcher.Registry.getBlockDataSerializer(false);


    public static WrappedDataWatcher getWatchableCollection(PacketItem item) {
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        byte bitmask = 0;
        bitmask = item.isGlowing() ? (byte) (bitmask | 0x40) : bitmask;
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, byteSerializer), bitmask);
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, optChatSerializer), Optional.of(WrappedChatComponent.fromJson(ComponentSerializer.toString(item.getCustomName())).getHandle()));
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, booleanSerializer), item.isCustomNameVisible());
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, booleanSerializer), !item.hasGravity());
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(8, itemSerializer), item.getItemStack());
        return watcher;
    }

    public static WrappedDataWatcher getWatchableCollection(PacketItemFrame frame) {
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(4, booleanSerializer), frame.isSilent());
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(8, itemSerializer), frame.getItem());
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(9, intSerializer), frame.getFrameRotation());
        return watcher;
    }


    public static void writeMetadataPacket(PacketContainer packet, WrappedDataWatcher watcher) {
        packet.getDataValueCollectionModifier().write(0, toDataValueList(watcher));
    }


    public static List<WrappedDataValue> toDataValueList(WrappedDataWatcher wrappedDataWatcher) {
        List<WrappedWatchableObject> watchableObjectList = wrappedDataWatcher.getWatchableObjects();
        List<WrappedDataValue> wrappedDataValues = new ArrayList<>(watchableObjectList.size());
        for (WrappedWatchableObject wrappedWatchableObject : wrappedDataWatcher.getWatchableObjects()) {
            WrappedDataWatcher.WrappedDataWatcherObject wrappedDataWatcherObject = wrappedWatchableObject.getWatcherObject();
            wrappedDataValues.add(new WrappedDataValue(wrappedDataWatcherObject.getIndex(), wrappedDataWatcherObject.getSerializer(), wrappedWatchableObject.getRawValue()));
        }
        return wrappedDataValues;
    }
}