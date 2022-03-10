package emanondev.core;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class CooldownAPI {

    private final YMLConfig conf;

    CooldownAPI(@NotNull CorePlugin plugin) {
        this(plugin, true);
    }

    @Contract("null, true -> fail")
    CooldownAPI(CorePlugin plugin, boolean persistent) {
        this(plugin,persistent,"cooldownData.yml");
    }

    @Contract("_, true, null -> fail; null, true, _ -> fail")
    CooldownAPI(CorePlugin plugin, boolean persistent,String fileName) {
        long now = System.currentTimeMillis();
        conf = persistent ? plugin.getConfig(fileName) : null;
        if (conf!=null)
            for (String id : conf.getKeys("users")) {
                HashMap<String, Long> map = new HashMap<>();
                cooldowns.put(UUID.fromString(id), map);
                for (String cooldownId : conf.getKeys("users." + id))
                    try {
                        long value = conf.getLong("users." + id + "." + cooldownId, 0L);
                        if (value > now)
                            map.put(cooldownId, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
    }

    void save() {
        if (conf!=null) {
            long now = System.currentTimeMillis();
            conf.set("users", null, false);
            for (UUID uuid : cooldowns.keySet()) {
                HashMap<String, Long> values = cooldowns.get(uuid);
                for (String id : values.keySet())
                    if (values.get(id) > now)
                        conf.set("users." + uuid.toString() + "." + id, values.get(id), false);
            }
            conf.save();
        }
    }

    private final HashMap<UUID, HashMap<String, Long>> cooldowns = new HashMap<>();

    /**
     * as milliseconds
     */
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
    public void addCooldown(UUID uuid, String cooldownId, long duration) {
        if (duration < 0)
            throw new IllegalArgumentException();
        setCooldown(uuid, cooldownId, getCooldownMillis(uuid, cooldownId) + duration);
    }

    /**
     * as milliseconds
     */
    public void reduceCooldown(UUID uuid, String cooldownId, long duration) {
        if (duration < 0)
            throw new IllegalArgumentException();
        setCooldown(uuid, cooldownId, getCooldownMillis(uuid, cooldownId) - duration);
    }

    public void setCooldownSeconds(UUID uuid, String cooldownId, long duration) {
        setCooldown(uuid, cooldownId, duration * 1000);
    }

    public void addCooldownSeconds(UUID uuid, String cooldownId, long duration) {
        addCooldown(uuid, cooldownId, duration * 1000);
    }

    public void reduceCooldownSeconds(UUID uuid, String cooldownId, long duration) {
        reduceCooldown(uuid, cooldownId, duration * 1000);
    }

    public void removeCooldown(UUID uuid, String cooldownId) {
        if (cooldowns.get(uuid) != null)
            cooldowns.get(uuid).remove(cooldownId);
    }

    public boolean hasCooldown(UUID uuid, String cooldownId) {
        return getCooldownMillis(uuid, cooldownId) > 0;

    }

    public long getCooldownMillis(UUID uuid, String cooldownId) {
        return cooldowns.containsKey(uuid) ? (cooldowns.get(uuid).containsKey(cooldownId) ?
                Math.max(0L, cooldowns.get(uuid).get(cooldownId) - System.currentTimeMillis()) : 0L) : 0L;
    }

    public long getCooldownSeconds(UUID uuid, String cooldownId) {
        return getCooldownMillis(uuid, cooldownId) / 1000;
    }

    public long getCooldownMinutes(UUID uuid, String cooldownId) {
        return getCooldownMillis(uuid, cooldownId) / 60000;
    }

    public long getCooldownHours(UUID uuid, String cooldownId) {
        return getCooldownMillis(uuid, cooldownId) / 3600000;
    }

    /**
     * as milliseconds
     */
    public void setCooldown(OfflinePlayer player, String cooldownId, long duration) {
        setCooldown(player.getUniqueId(), cooldownId, duration);
    }

    /**
     * as milliseconds
     */
    public void addCooldown(OfflinePlayer player, String cooldownId, long duration) {
        addCooldown(player.getUniqueId(), cooldownId, duration);
    }

    /**
     * as milliseconds
     */
    public void reduceCooldown(OfflinePlayer player, String cooldownId, long duration) {
        reduceCooldown(player.getUniqueId(), cooldownId, duration);
    }

    public void setCooldownSeconds(OfflinePlayer player, String cooldownId, long duration) {
        setCooldownSeconds(player.getUniqueId(), cooldownId, duration);
    }

    public void addCooldownSeconds(OfflinePlayer player, String cooldownId, long duration) {
        addCooldownSeconds(player.getUniqueId(), cooldownId, duration);
    }

    public void reduceCooldownSeconds(OfflinePlayer player, String cooldownId, long duration) {
        reduceCooldownSeconds(player.getUniqueId(), cooldownId, duration);
    }

    public void removeCooldown(OfflinePlayer player, String cooldownId) {
        removeCooldown(player.getUniqueId(), cooldownId);
    }

    public boolean hasCooldown(OfflinePlayer player, String cooldownId) {
        return hasCooldown(player.getUniqueId(), cooldownId);
    }

    public long getCooldownMillis(OfflinePlayer player, String cooldownId) {
        return getCooldownMillis(player.getUniqueId(), cooldownId);
    }

    public long getCooldownSeconds(OfflinePlayer player, String cooldownId) {
        return getCooldownSeconds(player.getUniqueId(), cooldownId);
    }

    public long getCooldownMinutes(OfflinePlayer player, String cooldownId) {
        return getCooldownMinutes(player.getUniqueId(), cooldownId);
    }

    public long getCooldownHours(OfflinePlayer player, String cooldownId) {
        return getCooldownHours(player.getUniqueId(), cooldownId);
    }

    /**
     * as milliseconds
     */
    public void setCooldown(Block block, String cooldownId, long duration) {
        setCooldown(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), cooldownId, duration);
    }

    /**
     * as milliseconds
     */
    public void addCooldown(Block block, String cooldownId, long duration) {
        addCooldown(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), cooldownId, duration);
    }

    /**
     * as milliseconds
     */
    public void reduceCooldown(Block block, String cooldownId, long duration) {
        reduceCooldown(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), cooldownId, duration);
    }

    public void setCooldownSeconds(Block block, String cooldownId, long duration) {
        setCooldownSeconds(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), cooldownId, duration);
    }

    public void addCooldownSeconds(Block block, String cooldownId, long duration) {
        addCooldownSeconds(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), cooldownId, duration);
    }

    public void reduceCooldownSeconds(Block block, String cooldownId, long duration) {
        reduceCooldownSeconds(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), cooldownId, duration);
    }

    public void removeCooldown(Block block, String cooldownId) {
        removeCooldown(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), cooldownId);
    }

    public boolean hasCooldown(Block block, String cooldownId) {
        return hasCooldown(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), cooldownId);
    }

    public long getCooldownMillis(Block block, String cooldownId) {
        return getCooldownMillis(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), cooldownId);
    }

    public long getCooldownSeconds(Block block, String cooldownId) {
        return getCooldownSeconds(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), cooldownId);
    }

    public long getCooldownMinutes(Block block, String cooldownId) {
        return getCooldownMinutes(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), cooldownId);
    }

    public long getCooldownHours(Block block, String cooldownId) {
        return getCooldownHours(new UUID((((long) block.getX()) << 32) + block.getZ(), (((long) block.getWorld().getName().hashCode()) << 8) + block.getY()), cooldownId);
    }
}
