package emanondev.core.packetentity;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import emanondev.core.MCVersion;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.Optional;

class WatchableCollection {

    private static final int metaversion = MCVersion.getCurrentVersion().getMetaVersion();

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

    public static WrappedDataWatcher getWatchableCollection(PacketArmorStand stand, WrappedDataWatcher watcher) {
        //WrappedDataWatcher watcher = new WrappedDataWatcher();
        // ###
        if (watcher == null)
            watcher = new WrappedDataWatcher();
        byte bitmask = 0;
        bitmask = !stand.isVisible() ? (byte) (bitmask | 0x20) : bitmask;
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, byteSerializer), bitmask);

        switch (metaversion) {
            case 0 -> {
                if (stand.getCustomName() != null && !stand.getCustomName().toPlainText().equals("")) {
                    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, stringSerializer), stand.getCustomName().toLegacyText());//LanguageUtils.convert(stand.getCustomName(), InteractionVisualizer.language).toLegacyText());
                    break;
                }
                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, stringSerializer), "");
            }
            case 1, 2, 3, 4 -> {
                if (stand.getCustomName() != null && !stand.getCustomName().toPlainText().equals("")) {
                    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, optChatSerializer), Optional.of(WrappedChatComponent.fromJson(ComponentSerializer.toString(stand.getCustomName())).getHandle()));
                    break;
                }
                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, optChatSerializer), Optional.empty());
            }
        }
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, booleanSerializer), stand.isCustomNameVisible());
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(4, booleanSerializer), stand.isSilent());
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, booleanSerializer), !stand.hasGravity());
        byte standbitmask = 0;
        standbitmask = stand.isSmall() ? (byte) (standbitmask | 0x1) : standbitmask;
        standbitmask = stand.hasArms() ? (byte) (standbitmask | 0x4) : standbitmask;
        standbitmask = !stand.hasBasePlate() ? (byte) (standbitmask | 0x8) : standbitmask;
        standbitmask = stand.isMarker() ? (byte) (standbitmask | 0x10) : standbitmask;


        switch (metaversion) {
            case 0, 1 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(11, byteSerializer), standbitmask);
            case 2 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(13, byteSerializer), standbitmask);
            case 3 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, byteSerializer), standbitmask);
            case 4 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, byteSerializer), standbitmask);
        }
        Vector3F headrotation = new Vector3F();
        headrotation.setX((float) Math.toDegrees(stand.getHeadPose().getX()));
        headrotation.setY((float) Math.toDegrees(stand.getHeadPose().getY()));
        headrotation.setZ((float) Math.toDegrees(stand.getHeadPose().getZ()));
        switch (metaversion) {
            case 0, 1 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(12, vectorSerializer), headrotation);
            case 2 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, vectorSerializer), headrotation);
            case 3 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, vectorSerializer), headrotation);
            case 4 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(16, vectorSerializer), headrotation);
        }
        Vector3F rightarmrotation = new Vector3F();
        rightarmrotation.setX((float) Math.toDegrees(stand.getRightArmPose().getX()));
        rightarmrotation.setY((float) Math.toDegrees(stand.getRightArmPose().getY()));
        rightarmrotation.setZ((float) Math.toDegrees(stand.getRightArmPose().getZ()));
        switch (metaversion) {
            case 0, 1 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, vectorSerializer), rightarmrotation);
            case 2 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(17, vectorSerializer), rightarmrotation);
            case 3 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(18, vectorSerializer), rightarmrotation);
            case 4 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(19, vectorSerializer), rightarmrotation);
        }
        return watcher;
    }

    public static WrappedDataWatcher getWatchableCollection(PacketItem item) {
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        byte bitmask = 0;
        bitmask = item.isGlowing() ? (byte) (bitmask | 0x40) : bitmask;
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, byteSerializer), bitmask);
        switch (metaversion) {
            case 0 -> {
                if (item.getCustomName() != null && !item.getCustomName().toPlainText().equals("")) {
                    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, stringSerializer), item.getCustomName().toLegacyText());//LanguageUtils.convert(item.getCustomName(), InteractionVisualizer.language).toLegacyText());
                    break;
                }
                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, stringSerializer), "");
            }
            case 1, 2, 3, 4 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, optChatSerializer), Optional.of(WrappedChatComponent.fromJson(ComponentSerializer.toString(item.getCustomName())).getHandle()));
        }
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, booleanSerializer), item.isCustomNameVisible());
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, booleanSerializer), !item.hasGravity());
        switch (metaversion) {
            case 0, 1 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, itemSerializer), item.getItemStack());
            case 2, 3 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(7, itemSerializer), item.getItemStack());
            case 4 -> watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(8, itemSerializer), item.getItemStack());
        }
        return watcher;
    }

    public static WrappedDataWatcher getWatchableCollection(PacketItemFrame frame) {
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(4, booleanSerializer), frame.isSilent());
        switch (metaversion) {
            case 0, 1 -> {
                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, itemSerializer), frame.getItem());
                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(7, intSerializer), frame.getFrameRotation());
            }
            case 2, 3 -> {
                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(7, itemSerializer), frame.getItem());
                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(8, intSerializer), frame.getFrameRotation());
            }
            case 4 -> {
                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(8, itemSerializer), frame.getItem());
                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(9, intSerializer), frame.getFrameRotation());
            }
        }
        return watcher;
    }
}