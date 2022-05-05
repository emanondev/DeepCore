package emanondev.core;

import emanondev.core.util.ConsoleLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class CoreListener implements Listener, ConsoleLogger {

    private final String id;
    private final CorePlugin plugin;

    public String getID() {
        return id;
    }

    public CorePlugin getPlugin() {
        return plugin;
    }

    public CoreListener(@NotNull CorePlugin plugin, @NotNull String id) {
        if (!UtilsString.isLowcasedValidID(id))
            throw new IllegalArgumentException();
        this.id = id;
        this.plugin = plugin;
    }

    public void log(String log) {
        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&',
                        ChatColor.DARK_BLUE + "[" + ChatColor.WHITE + getPlugin().getName() + ChatColor.DARK_BLUE + "|Listener "
                                + ChatColor.WHITE + getID() + ChatColor.DARK_BLUE + "] " + ChatColor.WHITE + log));
    }
}
