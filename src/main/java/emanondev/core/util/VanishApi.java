package emanondev.core.util;

import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishApi {

    public static boolean canSee(CommandSender sender, Player target) {
        if (!(sender instanceof Player who))
            return true;
        return VanishAPI.canSee(who, target);
    }

    public static boolean isInvisible(Player p) {
        return VanishAPI.isInvisible(p);
    }
}
