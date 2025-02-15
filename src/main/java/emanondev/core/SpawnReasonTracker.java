package emanondev.core;

import emanondev.core.utility.ReflectionUtility;
import emanondev.core.utility.VersionUtility;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.EnumMap;
import java.util.List;

/**
 * This class keeps tracks of mob spawning reasons
 * @author emanon
 */
public class SpawnReasonTracker implements Listener {
    private static final String metaName = "SpawnReason";
    private static final EnumMap<SpawnReason, FixedMetadataValue> fixedMetas = loadMetas();

    private static EnumMap<SpawnReason, FixedMetadataValue> loadMetas() {
        EnumMap<SpawnReason, FixedMetadataValue> map = new EnumMap<>(SpawnReason.class);
        for (SpawnReason reason : SpawnReason.values()) {
            map.put(reason, new FixedMetadataValue(CoreMain.get(), reason.toString()));
        }
        return map;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private static void handler(CreatureSpawnEvent event) {
        if (VersionUtility.hasPaperAPI())
            return;
        event.getEntity().setMetadata(metaName, fixedMetas.get(event.getSpawnReason()));
    }

    /**
     * @param entity - target entity
     * @return the reason why this entity was spawned, when there is no trace of
     * spawning reason {@code SpawnReason.DEFAULT} is returned
     */
    public static SpawnReason getSpawnReason(Entity entity) {
        if (VersionUtility.hasPaperAPI()) {
            return (SpawnReason) ReflectionUtility.invokeMethod(entity, "getEntitySpawnReason");
        }
        try {
            if (!entity.hasMetadata(metaName))
                throw new NullPointerException();
            List<MetadataValue> list = entity.getMetadata(metaName);
            return SpawnReason.valueOf(list.get(0).asString());
        } catch (Exception e) {
            return SpawnReason.DEFAULT;
        }
    }

}
