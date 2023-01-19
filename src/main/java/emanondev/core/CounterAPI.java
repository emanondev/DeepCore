package emanondev.core;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class CounterAPI {

    public enum ResetTime {
        MINUTE, HOUR_QUARTER, HOUR, DAY, WEEK, MONTH, YEAR, PERMANENT;

        public long getId() {
            switch (this) {
                case MINUTE:
                    return Calendar.getInstance().getTimeInMillis() / 60000;
                case HOUR_QUARTER:
                    return Calendar.getInstance().getTimeInMillis() / 900000;
                case HOUR:
                    return Calendar.getInstance().getTimeInMillis() / 3600000;
                case DAY: {
                    Calendar cal = Calendar.getInstance();
                    return cal.get(Calendar.YEAR) * 512L + cal.get(Calendar.DAY_OF_YEAR);
                }
                case MONTH: {
                    Calendar cal = Calendar.getInstance();
                    return cal.get(Calendar.YEAR) * 16L + cal.get(Calendar.MONTH);
                }
                case PERMANENT:
                    return 0;
                case WEEK: {
                    Calendar cal = Calendar.getInstance();
                    return cal.get(Calendar.YEAR) * 64L + cal.get(Calendar.WEEK_OF_YEAR);
                }
                case YEAR:
                    return Calendar.getInstance().get(Calendar.YEAR);
                default:
                    throw new IllegalStateException();
            }
        }

        public long getPreviousId() {
            Calendar cal;
            switch (this) {
                case MINUTE:
                    return Calendar.getInstance().getTimeInMillis() / 60000L - 1;
                case HOUR_QUARTER:
                    return Calendar.getInstance().getTimeInMillis() / 900000L - 1;
                case HOUR:
                    return Calendar.getInstance().getTimeInMillis() / 3600000L - 1;
                case DAY:
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1);
                    return (long) cal.get(Calendar.YEAR) * 512L + (long) cal.get(Calendar.DAY_OF_YEAR);
                case MONTH:
                    cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, -1);
                    return (long) cal.get(Calendar.YEAR) * 16L + (long) cal.get(Calendar.MONTH);
                case PERMANENT:
                    return 0L;
                case WEEK:
                    cal = Calendar.getInstance();
                    cal.add(Calendar.WEEK_OF_YEAR, -1);
                    return (long) cal.get(Calendar.YEAR) * 64L + (long) cal.get(Calendar.WEEK_OF_YEAR);
                case YEAR:
                    return (long) Calendar.getInstance().get(Calendar.YEAR) - 1;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private final YMLConfig conf;
    private final ResetTime reset;

    CounterAPI(@NotNull CorePlugin plugin, @NotNull ResetTime reset) {
        this(plugin, reset, true);
    }

    @Contract("null, _, true -> fail")
    CounterAPI(CorePlugin plugin, @NotNull ResetTime reset, boolean persistent) {
        this.reset = reset;
        this.id = reset.getId();
        conf = persistent ? plugin.getConfig("counterData.yml") : null;
        if (conf != null)
            for (String uuid : conf.getKeys(reset.name() + "." + this.id)) {
                HashMap<String, Long> map = new HashMap<>();
                for (String counterId : conf.getKeys(reset.name() + "." + this.id + "." + uuid))
                    try {
                        long value = conf.getLong(reset.name() + "." + this.id + "." + uuid + "." + counterId, 0L);
                        map.put(counterId, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                counters.put(UUID.fromString(uuid), map);
            }
    }

    void save() {
        if (conf != null) {
            conf.set(reset.name(), null, false);
            if (id != reset.getId())
                return;
            for (UUID player : counters.keySet()) {
                HashMap<String, Long> values = counters.get(player);
                for (String id : values.keySet())
                    if (values.get(id) > 0)
                        conf.set(reset.name() + "." + this.id + "." + player.toString() + "." + id, values.get(id), false);
            }
        }
    }

    //private int day = Calendar.DAY_OF_YEAR;
    private long id;
    private final HashMap<UUID, HashMap<String, Long>> counters = new HashMap<>();

    public void setCounter(UUID uuid, String counterId, long amount) {
        if (id != reset.getId()) {
            counters.clear();
            id = reset.getId();
        }
        if (amount <= 0 && counters.containsKey(uuid))
            counters.get(uuid).remove(counterId);
        else {
            counters.computeIfAbsent(uuid, k -> new HashMap<>());
            counters.get(uuid).put(counterId, amount);
        }
    }

    public void addCounter(UUID uuid, String counterId, long amount) {
        if (amount < 0)
            throw new IllegalArgumentException();
        setCounter(uuid, counterId, getCounter(uuid, counterId) + amount);
    }

    public void reduceCounter(UUID uuid, String counterId, long amount) {
        if (amount < 0)
            throw new IllegalArgumentException();
        setCounter(uuid, counterId, getCounter(uuid, counterId) - amount);
    }

    public void removeCounter(UUID uuid, String counterId) {
        if (counters.get(uuid) != null)
            counters.get(uuid).remove(counterId);
    }

    public boolean hasCounter(UUID uuid, String counterId) {
        return getCounter(uuid, counterId) != 0;

    }

    public long getCounter(UUID uuid, String counterId) {
        if (id != reset.getId()) {
            counters.clear();
            id = reset.getId();
        }
        return counters.containsKey(uuid)
                ? (counters.get(uuid).getOrDefault(counterId, 0L))
                : 0L;
    }

    public void setCounter(OfflinePlayer player, String counterId, long amount) {
        setCounter(player.getUniqueId(), counterId, amount);
    }

    public void addCounter(OfflinePlayer player, String counterId, long amount) {
        addCounter(player.getUniqueId(), counterId, amount);
    }

    public void reduceCounter(OfflinePlayer player, String counterId, long amount) {
        reduceCounter(player.getUniqueId(), counterId, amount);
    }

    public void removeCounter(OfflinePlayer player, String counterId) {
        removeCounter(player.getUniqueId(), counterId);
    }

    public boolean hasCounter(OfflinePlayer player, String counterId) {
        return hasCounter(player.getUniqueId(), counterId);

    }

    public long getCounter(OfflinePlayer player, String counterId) {
        return getCounter(player.getUniqueId(), counterId);
    }


    public void setCounter(Block block, String counterId, long amount) {
        setCounter(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), counterId, amount);
    }

    public void addCounter(Block block, String counterId, long amount) {
        addCounter(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), counterId, amount);
    }

    public void reduceCounter(Block block, String counterId, long amount) {
        reduceCounter(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), counterId, amount);
    }

    public void removeCounter(Block block, String counterId) {
        removeCounter(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), counterId);
    }

    public boolean hasCounter(Block block, String counterId) {
        return hasCounter(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), counterId);

    }

    public long getCounter(Block block, String counterId) {
        return getCounter(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), counterId);
    }
}
