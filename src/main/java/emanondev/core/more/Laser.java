package emanondev.core.more;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A whole class to create Guardian Beams by reflection <br>
 * Inspired by the API <a href=
 * "https://www.spigotmc.org/resources/guardianbeamapi.18329">GuardianBeamAPI</a><br>
 * <b>1.9 -&gt; 1.14</b>
 *
 * @author SkytAsul
 * @see <a href="https://github.com/SkytAsul/GuardianBeam">GitHub page</a>
 */
public class Laser {
    private final int duration;
    private final int distanceSquared;
    private Location start;
    private Location end;

    private final Object createGuardianPacket;
    private final Object createSquidPacket;
    private final Object teamAddPacket;
    private final Object destroyPacket;
    private final Object fakeDataWatcher;

    private final int squid;
    private final UUID squidUUID;
    private final int guardian;
    private final UUID guardianUUID;

    private BukkitRunnable run;
    private HashSet<Player> show = new HashSet<>();
    private final JavaPlugin plugin;

    /**
     * Create a Laser instance
     *
     * @param plugin   the plugin used
     * @param start    Location where laser will starts
     * @param end      Location where laser will ends
     * @param duration Duration of laser in seconds (<i>-1 if infinite</i>)
     * @param distance Distance where laser will be visible
     * @throws ReflectiveOperationException if something goes wrong
     */
    public Laser(JavaPlugin plugin, Location start, Location end, int duration, int distance)
            throws ReflectiveOperationException {
        this.plugin = plugin;
        this.start = start;
        this.end = end;
        this.duration = duration;
        distanceSquared = distance * distance;

        fakeDataWatcher = Packets.createFakeDataWatcher();

        createSquidPacket = Packets.createPacketSquidSpawn(end, fakeDataWatcher);
        squid = (int) Packets.getField(Packets.packetSpawn, "a", createSquidPacket);
        squidUUID = (UUID) Packets.getField(Packets.packetSpawn, "b", createSquidPacket);

        createGuardianPacket = Packets.createPacketGuardianSpawn(start, fakeDataWatcher, squid);
        guardian = (int) Packets.getField(Packets.packetSpawn, "a", createGuardianPacket);
        guardianUUID = (UUID) Packets.getField(Packets.packetSpawn, "b", createGuardianPacket);

        teamAddPacket = Packets.createPacketTeamAddEntities(squidUUID, guardianUUID);
        destroyPacket = Packets.createPacketRemoveEntities(squid, guardian);
    }

    /**
     * show the laser
     */
    public void start() {
        Validate.isTrue(run == null, "Task already started");
        run = new BukkitRunnable() {
            int time = duration;

            @Override
            public void run() {
                try {
                    if (time == 0) {
                        cancel();
                        return;
                    }
                    for (Player p : start.getWorld().getPlayers()) {
                        if (isCloseEnough(p.getLocation())) {
                            if (!show.contains(p)) {
                                sendStartPackets(p);
                                show.add(p);
                            }
                        } else if (show.contains(p)) {
                            Packets.sendPacket(p, destroyPacket);
                            show.remove(p);
                        }
                    }
                    if (time != -1)
                        time--;
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                super.cancel();
                try {
                    for (Player p : show) {
                        Packets.sendPacket(p, destroyPacket);
                    }
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
                run = null;
            }
        };
        run.runTaskTimer(plugin, 0L, 20L);
    }

    /**
     * remove the laser
     */
    public void stop() {
        Validate.isTrue(run != null, "Task not started");
        run.cancel();
    }

    /**
     * move the laser start location
     *
     * @param location - new start location
     * @throws ReflectiveOperationException if something goes wrong
     */
    public void moveStart(Location location) throws ReflectiveOperationException {
        this.start = location;
        Object packet = Packets.createPacketMoveEntity(start, guardian);
        for (Player p : show) {
            Packets.sendPacket(p, packet);
        }
    }

    /**
     * move the laser end location
     *
     * @param location - new end location
     * @throws ReflectiveOperationException if something goes wrong
     */
    public void moveEnd(Location location) throws ReflectiveOperationException {
        this.end = location;
        Object packet = Packets.createPacketMoveEntity(end, squid);
        for (Player p : show) {
            Packets.sendPacket(p, packet);
        }
    }

    /**
     * @return is laser showing
     */
    public boolean isStarted() {
        return run != null;
    }

    private void sendStartPackets(Player p) throws ReflectiveOperationException {
        Packets.sendPacket(p, createSquidPacket);
        Packets.sendPacket(p, createGuardianPacket);
        Packets.sendPacket(p, Packets.packetTeamCreate);
        Packets.sendPacket(p, teamAddPacket);
    }

    private boolean isCloseEnough(Location location) {
        return start.distanceSquared(location) <= distanceSquared || end.distanceSquared(location) <= distanceSquared;
    }

    private static class Packets {
        private static int lastIssuedEID = 2000000000;

        static int generateEID() {
            return lastIssuedEID++;
        }

        private static String[] versions = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
                .split(",")[3].substring(1).split("_");
        private static int version = Integer.parseInt(versions[1]); // 1.X
        private static int versionMinor = Integer.parseInt(versions[2].substring(1)); // 1.X.Y
        private static String npack = "net.minecraft.server."
                + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        private static String cpack = Bukkit.getServer().getClass().getPackage().getName() + ".";
        private static Object fakeSquid;
        private static Object packetTeamCreate;
        private static Constructor<?> watcherConstructor;
        private static Method watcherName;
        private static Method watcherRegister;
        private static Class<?> packetSpawn;
        private static Class<?> packetRemove;
        private static Class<?> packetTeleport;
        private static Class<?> packetTeam;
        private static String watcherName1;
        private static String watcherName2;
        private static String watcherName3;
        private static int squidID;
        private static int guardianID;

        static {
            try {
                if (version < 13) {
                    watcherName1 = "Z";
                    watcherName2 = "bA";
                    watcherName3 = "bB";
                    squidID = 94;
                    guardianID = 68;
                } else if (version == 13) {
                    watcherName1 = "ac";
                    watcherName2 = "bF";
                    watcherName3 = "bG";
                    squidID = 70;
                    guardianID = 28;
                } else if (version == 14) {
                    watcherName1 = "W";
                    watcherName2 = "b";
                    watcherName3 = "bD";
                    squidID = 73;
                    guardianID = 30;
                } else if (version == 15) {
                    watcherName1 = "T";
                    watcherName2 = "b";
                    watcherName3 = "bA";
                    squidID = 74;
                    guardianID = 31;
                } else if (version == 16 && versionMinor < 2) {
                    watcherName1 = "T";
                    watcherName2 = "b";
                    watcherName3 = "d";
                    squidID = 74;
                    guardianID = 31;
                } else {
                    watcherName1 = "S";
                    watcherName2 = "b";
                    watcherName3 = "d";
                    squidID = 81;
                    guardianID = 31;
                }
                Object world = Class.forName(cpack + "CraftWorld").getDeclaredMethod("getHandle")
                        .invoke(Bukkit.getWorlds().get(0));
                Object[] entityConstructorParams = version < 14 ? new Object[]{world}
                        : new Object[]{Class.forName(npack + "EntityTypes").getDeclaredField("SQUID").get(null),
                        world};
                fakeSquid = getMethod(Class.forName(cpack + "entity.CraftSquid"), "getHandle")
                        .invoke(Class.forName(cpack + "entity.CraftSquid").getDeclaredConstructors()[0]
                                .newInstance(null, Class.forName(npack + "EntitySquid").getDeclaredConstructors()[0]
                                        .newInstance(entityConstructorParams)));
                watcherConstructor = Class.forName(npack + "DataWatcher")
                        .getDeclaredConstructor(Class.forName(npack + "Entity"));
                watcherName = getMethod(Class.forName(npack + "DataWatcher"), "set");
                watcherRegister = getMethod(Class.forName(npack + "DataWatcher"), "register");
                packetSpawn = Class.forName(npack + "PacketPlayOutSpawnEntityLiving");
                packetRemove = Class.forName(npack + "PacketPlayOutEntityDestroy");
                packetTeleport = Class.forName(npack + "PacketPlayOutEntityTeleport");
                packetTeam = Class.forName(npack + "PacketPlayOutScoreboardTeam");

                packetTeamCreate = packetTeam.newInstance();
                setField(packetTeamCreate, "a", "noclip");
                setField(packetTeamCreate, "i", 0);
                setField(packetTeamCreate, "f", "never");
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }

        public static void sendPacket(Player p, Object packet) throws ReflectiveOperationException {
            Object entityPlayer = Class.forName(cpack + "entity.CraftPlayer").getDeclaredMethod("getHandle").invoke(p);
            Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);
            playerConnection.getClass().getDeclaredMethod("sendPacket", Class.forName(npack + "Packet"))
                    .invoke(playerConnection, packet);
        }

        public static Object createFakeDataWatcher() throws ReflectiveOperationException {
            return watcherConstructor.newInstance(fakeSquid);
        }

        public static Object createPacketSquidSpawn(Location location, Object watcher)
                throws ReflectiveOperationException {
            Object packet = packetSpawn.newInstance();
            setField(packet, "a", generateEID());
            setField(packet, "b", UUID.randomUUID());
            setField(packet, "c", squidID);
            setField(packet, "d", location.getX());
            setField(packet, "e", location.getY());
            setField(packet, "f", location.getZ());
            setField(packet, "j", (byte) (location.getYaw() * 256.0F / 360.0F));
            setField(packet, "k", (byte) (location.getPitch() * 256.0F / 360.0F));
            try {
                watcherName.invoke(watcher, getField(Class.forName(npack + "Entity"), watcherName1, null), (byte) 32);
            } catch (InvocationTargetException ex) {
                watcherRegister.invoke(watcher, getField(Class.forName(npack + "Entity"), watcherName1, null),
                        (byte) 32);
            }
            setField(packet, "m", watcher);
            return packet;
        }

        public static Object createPacketGuardianSpawn(Location location, Object watcher, int squidId)
                throws ReflectiveOperationException {
            Object packet = packetSpawn.newInstance();
            setField(packet, "a", generateEID());
            setField(packet, "b", UUID.randomUUID());
            setField(packet, "c", guardianID);
            setField(packet, "d", location.getX());
            setField(packet, "e", location.getY());
            setField(packet, "f", location.getZ());
            setField(packet, "j", (byte) (location.getYaw() * 256.0F / 360.0F));
            setField(packet, "k", (byte) (location.getPitch() * 256.0F / 360.0F));
            watcherName.invoke(watcher, getField(Class.forName(npack + "Entity"), watcherName1, null), (byte) 32);
            if (version > 13)
                setField(watcher, "registrationLocked", false);
            try {
                watcherName.invoke(watcher, getField(Class.forName(npack + "EntityGuardian"), watcherName2, null),
                        false);
            } catch (InvocationTargetException ex) {
                watcherRegister.invoke(watcher, getField(Class.forName(npack + "EntityGuardian"), watcherName2, null),
                        false);
            }
            try {
                watcherName.invoke(watcher, getField(Class.forName(npack + "EntityGuardian"), watcherName3, null),
                        squidId);
            } catch (InvocationTargetException ex) {
                watcherRegister.invoke(watcher, getField(Class.forName(npack + "EntityGuardian"), watcherName3, null),
                        squidId);
            }
            setField(packet, "m", watcher);
            return packet;
        }

        public static Object createPacketRemoveEntities(int squidId, int guardianId)
                throws ReflectiveOperationException {
            Object packet = packetRemove.newInstance();
            setField(packet, "a", new int[]{squidId, guardianId});
            return packet;
        }

        public static Object createPacketMoveEntity(Location location, int entityId)
                throws ReflectiveOperationException {
            Object packet = packetTeleport.newInstance();
            setField(packet, "a", entityId);
            setField(packet, "b", location.getX());
            setField(packet, "c", location.getY());
            setField(packet, "d", location.getZ());
            setField(packet, "e", (byte) (location.getYaw() * 256.0F / 360.0F));
            setField(packet, "f", (byte) (location.getPitch() * 256.0F / 360.0F));
            setField(packet, "g", true);
            return packet;
        }

        @SuppressWarnings("unchecked")
        public static Object createPacketTeamAddEntities(UUID squidUUID, UUID guardianUUID)
                throws ReflectiveOperationException {
            Object packet = packetTeam.newInstance();
            setField(packet, "a", "noclip");
            setField(packet, "i", 3);
            Collection<String> players = (Collection<String>) getField(packetTeam, "h", packet);
            players.add(squidUUID.toString());
            players.add(guardianUUID.toString());
            return packet;
        }

        private static Method getMethod(Class<?> clazz, String name) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(name))
                    return m;
            }
            return null;
        }

        private static void setField(Object instance, String name, Object value) throws ReflectiveOperationException {
            Validate.notNull(instance);
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, value);
        }

        private static Object getField(Class<?> clazz, String name, Object instance)
                throws ReflectiveOperationException {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(instance);
        }
    }
}
