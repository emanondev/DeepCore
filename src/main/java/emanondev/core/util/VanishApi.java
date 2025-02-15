package emanondev.core.util;

import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanishApi {

    public static boolean canSee(@Nullable CommandSender sender, @NotNull Player target) {
        if (!(sender instanceof Player who))
            return true;
        try {
            return VanishAPI.canSee(who, target);
        } catch (Throwable t) {
            t.printStackTrace();
            for (MetadataValue meta : target.getMetadata("vanished")) {
                if (meta.asBoolean()) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isInvisible(Player p) {
        return VanishAPI.isInvisible(p);
    }
}
