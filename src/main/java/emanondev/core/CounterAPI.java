package emanondev.core;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

/**
 * API for managing counters with reset periods.
 */
@Slf4j
public class CounterAPI {

    private final YMLConfig conf;
    private final ResetTime reset;
    private final HashMap<UUID, HashMap<String, Long>> counters = new HashMap<>();
    private long id;

    /**
     * Constructs a CounterAPI instance with persistent storage.
     *
     * @param plugin the core plugin instance
     * @param reset  the reset period for counters
     */
    CounterAPI(final @NotNull CorePlugin plugin, final @NotNull ResetTime reset) {
        this(plugin, reset, true);
    }

    /**
     * Constructs a CounterAPI instance.
     *
     * @param plugin     the core plugin instance
     * @param reset      the reset period for counters
     * @param persistent whether the counters should be persisted
     */
    @Contract("null, _, true -> fail")
    CounterAPI(CorePlugin plugin, @NotNull ResetTime reset, boolean persistent) {
        this.reset = reset;
        this.id = reset.getId();
        conf = persistent ? plugin.getConfig("counterData.yml") : null;
        if (conf != null)
            for (String uuid : conf.getKeys(reset.name() + "." + this.id)) {
                HashMap<String, Long> map = new HashMap<>();
                for (String counterId : conf.getKeys(reset.name() + "." + this.id + "." + uuid)) {
                    try {
                        long value = conf.getLong(reset.name() + "." + this.id + "."
                                + uuid + "." + counterId, 0L);
                        map.put(counterId, value);
                    } catch (Exception e) {
                        log.error("Unexpected error", e);
                    }
                }
                counters.put(UUID.fromString(uuid), map);
            }
    }

    void save() {
        if (conf != null) {
            conf.set(reset.name(), null, false);
            if (id != reset.getId()) {
                return;
            }
            for (UUID player : counters.keySet()) {
                HashMap<String, Long> values = counters.get(player);
                for (String id : values.keySet()) {
                    if (values.get(id) > 0) {
                        conf.set(reset.name() + "." + this.id + "." + player.toString() + "." + id, values.get(id), false);
                    }
                }
            }
        }
    }

    /**
     * Sets a counter for an offline player.
     *
     * @param player    the player
     * @param counterId the counter ID
     * @param amount    the counter value
     */
    public void setCounter(final @NotNull OfflinePlayer player, final @NotNull String counterId, final long amount) {
        setCounter(player.getUniqueId(), counterId, amount);
    }

    /**
     * Sets a counter for a UUID.
     *
     * @param uuid      the UUID
     * @param counterId the counter ID
     * @param amount    the counter value
     */
    public void setCounter(UUID uuid, String counterId, long amount) {
        if (id != reset.getId()) {
            counters.clear();
            id = reset.getId();
        }
        if (amount <= 0 && counters.containsKey(uuid)) {
            counters.get(uuid).remove(counterId);
        } else {
            counters.computeIfAbsent(uuid, k -> new HashMap<>());
            counters.get(uuid).put(counterId, amount);
        }
    }

    /**
     * Increases the counter for the specified player.
     *
     * @param player    the player whose counter should be incremented
     * @param counterId the ID of the counter
     * @param amount    the amount to add
     */
    public void addCounter(final @NotNull OfflinePlayer player, final @NotNull String counterId, final long amount) {
        addCounter(player.getUniqueId(), counterId, amount);
    }

    /**
     * Increases the counter for the specified UUID.
     *
     * @param uuid      the unique identifier of the player
     * @param counterId the ID of the counter
     * @param amount    the amount to add
     * @throws IllegalArgumentException if the amount is negative
     */
    public void addCounter(final @NotNull UUID uuid, final @NotNull String counterId, final long amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Amount cannot be negative");
        setCounter(uuid, counterId, getCounter(uuid, counterId) + amount);
    }

    /**
     * Retrieves the counter value for the specified UUID.
     *
     * @param uuid      the unique identifier of the player
     * @param counterId the ID of the counter
     * @return the counter value, or 0 if not set
     */
    public long getCounter(final @NotNull UUID uuid, final @NotNull String counterId) {
        if (id != reset.getId()) {
            counters.clear();
            id = reset.getId();
        }
        return counters.containsKey(uuid) ? counters.get(uuid).getOrDefault(counterId, 0L) : 0L;
    }

    /**
     * Decreases the counter for the specified player.
     *
     * @param player    the player whose counter should be reduced
     * @param counterId the ID of the counter
     * @param amount    the amount to subtract
     */
    public void reduceCounter(final @NotNull OfflinePlayer player, final @NotNull String counterId, final long amount) {
        reduceCounter(player.getUniqueId(), counterId, amount);
    }

    /**
     * Decreases the counter for the specified UUID.
     *
     * @param uuid      the unique identifier of the player
     * @param counterId the ID of the counter
     * @param amount    the amount to subtract
     * @throws IllegalArgumentException if the amount is negative
     */
    public void reduceCounter(final @NotNull UUID uuid, final @NotNull String counterId, final long amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Amount cannot be negative");
        setCounter(uuid, counterId, getCounter(uuid, counterId) - amount);
    }

    /**
     * Removes the counter for the specified player.
     *
     * @param player    the player whose counter should be removed
     * @param counterId the ID of the counter to remove
     */
    public void removeCounter(final @NotNull OfflinePlayer player, final @NotNull String counterId) {
        removeCounter(player.getUniqueId(), counterId);
    }

    /**
     * Removes the counter for the specified UUID.
     *
     * @param uuid      the unique identifier of the player
     * @param counterId the ID of the counter to remove
     */
    public void removeCounter(final @NotNull UUID uuid, final @NotNull String counterId) {
        if (counters.get(uuid) != null) {
            counters.get(uuid).remove(counterId);
        }
    }

    /**
     * Checks if the specified player has a counter.
     *
     * @param player    the player to check
     * @param counterId the ID of the counter
     * @return {@code true} if the counter exists and has a nonzero value, {@code false} otherwise
     */
    public boolean hasCounter(final @NotNull OfflinePlayer player, final @NotNull String counterId) {
        return hasCounter(player.getUniqueId(), counterId);
    }

    /**
     * Checks if the specified UUID has a counter.
     *
     * @param uuid      the unique identifier of the player
     * @param counterId the ID of the counter
     * @return {@code true} if the counter exists and has a nonzero value, {@code false} otherwise
     */
    public boolean hasCounter(final @NotNull UUID uuid, final @NotNull String counterId) {
        return getCounter(uuid, counterId) != 0;
    }

    /**
     * Retrieves the counter value for the specified player.
     *
     * @param player    the player whose counter should be retrieved
     * @param counterId the ID of the counter
     * @return the counter value, or 0 if not set
     */
    public long getCounter(final @NotNull OfflinePlayer player, final @NotNull String counterId) {
        return getCounter(player.getUniqueId(), counterId);
    }

    /**
     * Sets the counter for a block.
     *
     * @param block     the block to associate with the counter
     * @param counterId the ID of the counter
     * @param amount    the amount to set
     */
    public void setCounter(final @NotNull Block block, final @NotNull String counterId, final long amount) {
        setCounter(blockToUUID(block), counterId, amount);
    }

    /**
     * Increases the counter for a block.
     *
     * @param block     the block to associate with the counter
     * @param counterId the ID of the counter
     * @param amount    the amount to add
     */
    public void addCounter(final @NotNull Block block, final @NotNull String counterId, final long amount) {
        addCounter(blockToUUID(block), counterId, amount);
    }

    /**
     * Reduces the counter value associated with a block.
     *
     * @param block     the block whose counter should be reduced
     * @param counterId the ID of the counter
     * @param amount    the amount to subtract from the counter
     * @throws IllegalArgumentException if the amount is negative
     */
    public void reduceCounter(final @NotNull Block block, final @NotNull String counterId, final long amount) {
        reduceCounter(blockToUUID(block), counterId, amount);
    }

    /**
     * Removes the counter associated with a block.
     *
     * @param block     the block whose counter should be removed
     * @param counterId the ID of the counter to remove
     */
    public void removeCounter(final @NotNull Block block, final @NotNull String counterId) {
        removeCounter(blockToUUID(block), counterId);
    }

    /**
     * Checks if a block has an associated counter.
     *
     * @param block     the block to check
     * @param counterId the ID of the counter
     * @return {@code true} if the counter exists and has a nonzero value, {@code false} otherwise
     */
    public boolean hasCounter(final @NotNull Block block, final @NotNull String counterId) {
        return hasCounter(blockToUUID(block), counterId);
    }

    /**
     * Retrieves the counter value associated with a block.
     *
     * @param block     the block whose counter should be retrieved
     * @param counterId the ID of the counter
     * @return the counter value, or 0 if not set
     */
    public long getCounter(final @NotNull Block block, final @NotNull String counterId) {
        return getCounter(blockToUUID(block), counterId);
    }

    private UUID blockToUUID(Block block) {
        return new UUID((((long) block.getX()) << 32) + block.getZ(),
                (((long) block.getWorld().getName().hashCode()) << 8) + block.getY());
    }

    public enum ResetTime {
        MINUTE, HOUR_QUARTER, HOUR, DAY, WEEK, MONTH, YEAR, PERMANENT;

        public long getId() {
            return switch (this) {
                case MINUTE -> Calendar.getInstance().getTimeInMillis() / 60000;
                case HOUR_QUARTER -> Calendar.getInstance().getTimeInMillis() / 900000;
                case HOUR -> Calendar.getInstance().getTimeInMillis() / 3600000;
                case DAY -> {
                    Calendar cal = Calendar.getInstance();
                    yield cal.get(Calendar.YEAR) * 512L + cal.get(Calendar.DAY_OF_YEAR);
                }
                case MONTH -> {
                    Calendar cal = Calendar.getInstance();
                    yield cal.get(Calendar.YEAR) * 16L + cal.get(Calendar.MONTH);
                }
                case PERMANENT -> 0;
                case WEEK -> {
                    Calendar cal = Calendar.getInstance();
                    yield cal.get(Calendar.YEAR) * 64L + cal.get(Calendar.WEEK_OF_YEAR);
                }
                case YEAR -> Calendar.getInstance().get(Calendar.YEAR);
            };
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
}
