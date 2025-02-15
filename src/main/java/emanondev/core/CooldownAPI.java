package emanondev.core;

import emanondev.core.utility.VersionUtility;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CooldownAPI {

    private final YMLConfig conf;
    private final Map<UUID, Map<String, Long>> cooldowns =
            VersionUtility.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();

    CooldownAPI(final @NotNull CorePlugin plugin) {
        this(plugin, true);
    }

    @Contract("null, true -> fail")
    CooldownAPI(final @Nullable CorePlugin plugin, final boolean persistent) {
        this(plugin, persistent, "cooldownData.yml");
    }

    @Contract("_, true, null -> fail; null, true, _ -> fail")
    CooldownAPI(final @Nullable CorePlugin plugin, final boolean persistent, final String fileName) {
        if (persistent && (plugin == null || fileName == null)) {
            throw new IllegalArgumentException();
        }
        long now = System.currentTimeMillis();
        conf = persistent ? plugin.getConfig(fileName) : null;
        if (conf != null) {
            for (String id : conf.getKeys("users")) {
                HashMap<String, Long> map = new HashMap<>();
                cooldowns.put(UUID.fromString(id), map);
                for (String cooldownId : conf.getKeys("users." + id)) {
                    try {
                        long value = conf.getLong("users." + id + "." + cooldownId, 0L);
                        if (value > now) {
                            map.put(cooldownId, value);
                        }
                    } catch (Exception e) {
                        log.error("Unexpected error", e);
                    }
                }
            }
        }
    }

    void save() {
        if (conf != null) {
            long now = System.currentTimeMillis();
            conf.set("users", null, false);
            for (UUID uuid : cooldowns.keySet()) {
                Map<String, Long> values = cooldowns.get(uuid);
                for (String id : values.keySet()) {
                    if (values.get(id) > now) {
                        conf.set("users." + uuid.toString() + "." + id, values.get(id), false);
                    }
                }
            }
            conf.save();
        }
    }

    /**
     * Sets a cooldown for a block with a specified duration.
     *
     * @param block      the block whose cooldown is to be set.
     * @param cooldownId  the ID of the cooldown.
     * @param duration    the duration of the cooldown.
     * @param timeUnit    the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void setCooldown(final @NotNull Block block,
                            final @NotNull String cooldownId,
                            final @Range(from = 0, to = Long.MAX_VALUE) long duration,
                            final @NotNull TimeUnit timeUnit) {
        setCooldown(blockToUUID(block), cooldownId, duration, timeUnit);
    }

    /**
     * Adds to the cooldown for a block.
     *
     * @param block      the block whose cooldown is to be added to.
     * @param cooldownId  the ID of the cooldown.
     * @param duration    the duration to add to the cooldown.
     * @param timeUnit    the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void addCooldown(final @NotNull Block block,
                            final @NotNull String cooldownId,
                            final @Range(from = 0, to = Long.MAX_VALUE) long duration,
                            final @NotNull TimeUnit timeUnit) {
        addCooldown(blockToUUID(block), cooldownId, duration, timeUnit);
    }

    /**
     * Reduces the cooldown for a block.
     *
     * @param block      the block whose cooldown is to be reduced.
     * @param cooldownId  the ID of the cooldown.
     * @param duration    the duration to reduce from the cooldown.
     * @param timeUnit    the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void reduceCooldown(final @NotNull Block block,
                               final @NotNull String cooldownId,
                               final @Range(from = 0, to = Long.MAX_VALUE) long duration,
                               final @NotNull TimeUnit timeUnit) {
        reduceCooldown(blockToUUID(block), cooldownId, duration, timeUnit);
    }

    /**
     * Removes a cooldown for a block.
     *
     * @param block      the block whose cooldown is to be removed.
     * @param cooldownId  the ID of the cooldown.
     */
    public void removeCooldown(final @NotNull Block block,
                               final @NotNull String cooldownId) {
        removeCooldown(blockToUUID(block), cooldownId);
    }

    /**
     * Checks if the given block has an active cooldown for the specified cooldown ID.
     *
     * @param block The offline block to check.
     * @param cooldownId The ID of the cooldown.
     * @return {@code true} if the block has an active cooldown, {@code false} otherwise.
     */
    public boolean hasCooldown(final @NotNull Block block,
                               final @NotNull String cooldownId) {
        return hasCooldown(blockToUUID(block), cooldownId);
    }

    /**
     * Retrieves the cooldown for a block and cooldown ID, converted to a specified time unit.
     *
     * @param block      the block whose cooldown is to be retrieved.
     * @param cooldownId  the ID of the cooldown.
     * @param timeUnit    the time unit to convert the cooldown to.
     * @return the cooldown duration in the specified time unit, or 0 if no cooldown is active.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public long getCooldown(final @NotNull Block block,
                            final @NotNull String cooldownId,
                            final @NotNull TimeUnit timeUnit) {
        return getCooldown(blockToUUID(block), cooldownId, timeUnit);
    }

    /**
     * Sets a cooldown for a player with a specified duration.
     *
     * @param player      the player whose cooldown is to be set.
     * @param cooldownId  the ID of the cooldown.
     * @param duration    the duration of the cooldown.
     * @param timeUnit    the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void setCooldown(final @NotNull OfflinePlayer player,
                            final @NotNull String cooldownId,
                            final @Range(from = 0, to = Long.MAX_VALUE) long duration,
                            final @NotNull TimeUnit timeUnit) {
        setCooldown(player.getUniqueId(), cooldownId, duration, timeUnit);
    }

    /**
     * Adds to the cooldown for a player.
     *
     * @param player      the player whose cooldown is to be added to.
     * @param cooldownId  the ID of the cooldown.
     * @param duration    the duration to add to the cooldown.
     * @param timeUnit    the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void addCooldown(final @NotNull OfflinePlayer player,
                            final @NotNull String cooldownId,
                            final @Range(from = 0, to = Long.MAX_VALUE) long duration,
                            final @NotNull TimeUnit timeUnit) {
        addCooldown(player.getUniqueId(), cooldownId, duration, timeUnit);
    }

    /**
     * Reduces the cooldown for a player.
     *
     * @param player      the player whose cooldown is to be reduced.
     * @param cooldownId  the ID of the cooldown.
     * @param duration    the duration to reduce from the cooldown.
     * @param timeUnit    the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void reduceCooldown(final @NotNull OfflinePlayer player,
                               final @NotNull String cooldownId,
                               final @Range(from = 0, to = Long.MAX_VALUE) long duration,
                               final @NotNull TimeUnit timeUnit) {
        reduceCooldown(player.getUniqueId(), cooldownId, duration, timeUnit);
    }

    /**
     * Removes a cooldown for a player.
     *
     * @param player      the player whose cooldown is to be removed.
     * @param cooldownId  the ID of the cooldown.
     */
    public void removeCooldown(final @NotNull OfflinePlayer player,
                               final @NotNull String cooldownId) {
        removeCooldown(player.getUniqueId(), cooldownId);
    }

    /**
     * Checks if the given player has an active cooldown for the specified cooldown ID.
     *
     * @param player The offline player to check.
     * @param cooldownId The ID of the cooldown.
     * @return {@code true} if the player has an active cooldown, {@code false} otherwise.
     */
    public boolean hasCooldown(final @NotNull OfflinePlayer player,
                               final @NotNull String cooldownId) {
        return hasCooldown(player.getUniqueId(), cooldownId);
    }

    /**
     * Retrieves the cooldown for a player and cooldown ID, converted to a specified time unit.
     *
     * @param player      the player whose cooldown is to be retrieved.
     * @param cooldownId  the ID of the cooldown.
     * @param timeUnit    the time unit to convert the cooldown to.
     * @return the cooldown duration in the specified time unit, or 0 if no cooldown is active.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public long getCooldown(final @NotNull OfflinePlayer player,
                            final @NotNull String cooldownId,
                            final @NotNull TimeUnit timeUnit) {
        return getCooldown(player.getUniqueId(), cooldownId, timeUnit);
    }

    /**
     * Sets a cooldown for a UUID with a specified duration.
     *
     * @param uuid      the UUID.
     * @param cooldownId  the ID of the cooldown.
     * @param duration    the duration of the cooldown.
     * @param timeUnit    the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void setCooldown(final @NotNull UUID uuid,
                            final @NotNull String cooldownId,
                            final @Range(from = 0, to = Long.MAX_VALUE) long duration,
                            final @NotNull TimeUnit timeUnit) {
        if (timeUnit.compareTo(TimeUnit.MILLISECONDS) < 0) {
            throw new UnsupportedOperationException("Time unit must be at least MILLISECONDS.");
        }
        if (duration <= 0 && cooldowns.containsKey(uuid)) {
            cooldowns.get(uuid).remove(cooldownId);
        } else {
            cooldowns.computeIfAbsent(uuid, k ->
                    VersionUtility.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>());
            cooldowns.get(uuid).put(cooldownId,
                    System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(duration, timeUnit));
        }
    }

    /**
     * Adds to the cooldown for a UUID.
     *
     * @param uuid      the UUID.
     * @param cooldownId  the ID of the cooldown.
     * @param duration    the duration to add to the cooldown.
     * @param timeUnit    the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void addCooldown(final @NotNull UUID uuid,
                            final @NotNull String cooldownId,
                            final @Range(from = 0, to = Long.MAX_VALUE) long duration,
                            final @NotNull TimeUnit timeUnit) {
        if (timeUnit.compareTo(TimeUnit.MILLISECONDS) < 0) {
            throw new UnsupportedOperationException("Time unit must be at least MILLISECONDS.");
        }
        setCooldown(uuid, cooldownId, getCooldown(uuid, cooldownId, TimeUnit.MILLISECONDS)
                + TimeUnit.MILLISECONDS.convert(duration, timeUnit), TimeUnit.MILLISECONDS);
    }

    /**
     * Reduces the cooldown for a UUID.
     *
     * @param uuid      the UUID.
     * @param cooldownId  the ID of the cooldown.
     * @param duration    the duration to reduce from the cooldown.
     * @param timeUnit    the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void reduceCooldown(final @NotNull UUID uuid,
                               final @NotNull String cooldownId,
                               final @Range(from = 0, to = Long.MAX_VALUE) long duration,
                               final @NotNull TimeUnit timeUnit) {
        if (timeUnit.compareTo(TimeUnit.MILLISECONDS) < 0) {
            throw new UnsupportedOperationException();
        }
        setCooldown(uuid, cooldownId, getCooldown(uuid, cooldownId, TimeUnit.MILLISECONDS)
                - TimeUnit.MILLISECONDS.convert(duration, timeUnit), TimeUnit.MILLISECONDS);
    }

    /**
     * Removes a cooldown for a UUID.
     *
     * @param uuid      the UUID whose cooldown is to be removed.
     * @param cooldownId  the ID of the cooldown.
     */
    public void removeCooldown(final @NotNull UUID uuid,
                               final @NotNull String cooldownId) {
        if (cooldowns.get(uuid) != null) {
            cooldowns.get(uuid).remove(cooldownId);
        }
    }

    /**
     * Checks if the player (by UUID) has an active cooldown for the specified cooldown ID.
     *
     * @param player The UUID of the player to check.
     * @param cooldownId The ID of the cooldown.
     * @return {@code true} if the player has an active cooldown, {@code false} otherwise.
     */
    public boolean hasCooldown(final @NotNull UUID player,
                               final @NotNull String cooldownId) {
        return getCooldown(player, cooldownId, TimeUnit.MILLISECONDS) > 0;
    }

    /**
     * Retrieves the cooldown for a UUID and cooldown ID, converted to a specified time unit.
     *
     * @param uuid      the UUID.
     * @param cooldownId  the ID of the cooldown.
     * @param timeUnit    the time unit to convert the cooldown to.
     * @return the cooldown duration in the specified time unit, or 0 if no cooldown is active.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public long getCooldown(final @NotNull UUID uuid,
                            final @NotNull String cooldownId,
                            final @NotNull TimeUnit timeUnit) {
        if (timeUnit.compareTo(TimeUnit.MILLISECONDS) < 0) {
            throw new UnsupportedOperationException("Time unit must be at least MILLISECONDS.");
        }
        long cooldownMS = cooldowns.containsKey(uuid) ?
                Math.max(0L, cooldowns.get(uuid).getOrDefault(cooldownId, 0L) - System.currentTimeMillis()) :
                0L;
        return timeUnit.convert(cooldownMS, TimeUnit.MILLISECONDS);
    }

    /**
     * as milliseconds
     */
    @Deprecated
    public void setCooldown(OfflinePlayer player, String cooldownId, long duration) {
        setCooldown(player.getUniqueId(), cooldownId, duration);
    }

    /**
     * as milliseconds
     */
    @Deprecated
    public void setCooldown(UUID uuid, String cooldownId, long duration) {
        if (duration <= 0 && cooldowns.containsKey(uuid))
            cooldowns.get(uuid).remove(cooldownId);
        else {
            cooldowns.computeIfAbsent(uuid, k -> new HashMap<>());
            cooldowns.get(uuid).put(cooldownId, System.currentTimeMillis() + duration);
        }
    }

    /**
     * as milliseconds
     */
    @Deprecated
    public void addCooldown(OfflinePlayer player, String cooldownId, long duration) {
        addCooldown(player.getUniqueId(), cooldownId, duration);
    }

    /**
     * as milliseconds
     */
    @Deprecated
    public void addCooldown(UUID uuid, String cooldownId, long duration) {
        if (duration < 0)
            throw new IllegalArgumentException();
        setCooldown(uuid, cooldownId, getCooldownMillis(uuid, cooldownId) + duration);
    }

    @Deprecated
    public long getCooldownMillis(UUID uuid, String cooldownId) {
        return cooldowns.containsKey(uuid) ? (cooldowns.get(uuid).containsKey(cooldownId) ?
                Math.max(0L, cooldowns.get(uuid).get(cooldownId) - System.currentTimeMillis()) : 0L) : 0L;
    }

    /**
     * as milliseconds
     */
    @Deprecated
    public void reduceCooldown(OfflinePlayer player, String cooldownId, long duration) {
        reduceCooldown(player.getUniqueId(), cooldownId, duration);
    }

    /**
     * as milliseconds
     */
    @Deprecated
    public void reduceCooldown(UUID uuid, String cooldownId, long duration) {
        if (duration < 0)
            throw new IllegalArgumentException();
        setCooldown(uuid, cooldownId, getCooldownMillis(uuid, cooldownId) - duration);
    }

    @Deprecated
    public void setCooldownSeconds(OfflinePlayer player, String cooldownId, long duration) {
        setCooldownSeconds(player.getUniqueId(), cooldownId, duration);
    }

    @Deprecated
    public void setCooldownSeconds(UUID uuid, String cooldownId, long duration) {
        setCooldown(uuid, cooldownId, duration * 1000);
    }

    @Deprecated
    public void addCooldownSeconds(OfflinePlayer player, String cooldownId, long duration) {
        addCooldownSeconds(player.getUniqueId(), cooldownId, duration);
    }

    @Deprecated
    public void addCooldownSeconds(UUID uuid, String cooldownId, long duration) {
        addCooldown(uuid, cooldownId, duration * 1000);
    }

    @Deprecated
    public void reduceCooldownSeconds(OfflinePlayer player, String cooldownId, long duration) {
        reduceCooldownSeconds(player.getUniqueId(), cooldownId, duration);
    }

    @Deprecated
    public void reduceCooldownSeconds(UUID uuid, String cooldownId, long duration) {
        reduceCooldown(uuid, cooldownId, duration * 1000);
    }

    @Deprecated
    public long getCooldownMillis(OfflinePlayer player, String cooldownId) {
        return getCooldownMillis(player.getUniqueId(), cooldownId);
    }

    @Deprecated
    public long getCooldownSeconds(OfflinePlayer player, String cooldownId) {
        return getCooldownSeconds(player.getUniqueId(), cooldownId);
    }

    @Deprecated
    public long getCooldownSeconds(UUID uuid, String cooldownId) {
        return getCooldownMillis(uuid, cooldownId) / 1000;
    }

    @Deprecated
    public long getCooldownMinutes(OfflinePlayer player, String cooldownId) {
        return getCooldownMinutes(player.getUniqueId(), cooldownId);
    }

    @Deprecated
    public long getCooldownMinutes(UUID uuid, String cooldownId) {
        return getCooldownMillis(uuid, cooldownId) / 60000;
    }

    @Deprecated
    public long getCooldownHours(OfflinePlayer player, String cooldownId) {
        return getCooldownHours(player.getUniqueId(), cooldownId);
    }

    @Deprecated
    public long getCooldownHours(UUID uuid, String cooldownId) {
        return getCooldownMillis(uuid, cooldownId) / 3600000;
    }

    /**
     * as milliseconds
     */
    @Deprecated
    public void setCooldown(Block block, String cooldownId, long duration) {
        setCooldown(blockToUUID(block), cooldownId, duration);
    }

    /**
     * as milliseconds
     */
    @Deprecated
    public void addCooldown(Block block, String cooldownId, long duration) {
        addCooldown(blockToUUID(block), cooldownId, duration);
    }

    /**
     * as milliseconds
     */
    @Deprecated
    public void reduceCooldown(Block block, String cooldownId, long duration) {
        reduceCooldown(blockToUUID(block), cooldownId, duration);
    }

    @Deprecated
    public void setCooldownSeconds(Block block, String cooldownId, long duration) {
        setCooldownSeconds(blockToUUID(block), cooldownId, duration);
    }

    @Deprecated
    public void addCooldownSeconds(Block block, String cooldownId, long duration) {
        addCooldownSeconds(blockToUUID(block), cooldownId, duration);
    }

    @Deprecated
    public void reduceCooldownSeconds(Block block, String cooldownId, long duration) {
        reduceCooldownSeconds(blockToUUID(block), cooldownId, duration);
    }

    @Deprecated
    public long getCooldownMillis(Block block, String cooldownId) {
        return getCooldownMillis(blockToUUID(block), cooldownId);
    }

    @Deprecated
    public long getCooldownSeconds(Block block, String cooldownId) {
        return getCooldownSeconds(blockToUUID(block), cooldownId);
    }

    @Deprecated
    public long getCooldownMinutes(Block block, String cooldownId) {
        return getCooldownMinutes(blockToUUID(block), cooldownId);
    }

    @Deprecated
    public long getCooldownHours(Block block, String cooldownId) {
        return getCooldownHours(blockToUUID(block), cooldownId);
    }

    private UUID blockToUUID(Block block) {
        return new UUID((((long) block.getX()) << 32) + block.getZ(),
                (((long) block.getWorld().getName().hashCode()) << 8) + block.getY());
    }
}