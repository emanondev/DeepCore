package emanondev.core;

import emanondev.core.util.ConsoleLogger;
import emanondev.core.util.FileLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public abstract class Module implements Listener, ConsoleLogger, FileLogger {

    private final CorePlugin plugin;

    private final String id;
    private final String description;

    @Deprecated
    public Module(@NotNull String id, @NotNull CorePlugin plugin) {
        this(id, plugin, null);
    }

    public Module(@NotNull String id, @NotNull CorePlugin plugin, String description) {
        if (!UtilsString.isValidID(id))
            throw new IllegalArgumentException("invalid module id");
        this.id = id;
        this.plugin = plugin;
        this.description = description;
        getPlugin().getConfig("modules.yml").loadString(this.getID() + ".description", this.description);
    }

    /**
     * Returns module description
     *
     * @return module description
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * Returns module identifier
     *
     * @return module identifier
     */
    public final String getID() {
        return this.id;
    }

    /**
     * Returns module plugin
     *
     * @return module plugin
     */
    public final CorePlugin getPlugin() {
        return this.plugin;
    }

    public YMLConfig getConfig() {
        return plugin.getConfig("modules" + File.separator + this.getID() + ".yml");
    }

    public YMLConfig getConfig(String fileName) {
        return plugin.getConfig("modules" + File.separator + this.getID() + this.getID() + "_" + fileName);
    }

    public abstract void enable();

    public abstract void disable();

    public abstract void reload();

    /**
     * logs on console Message to print with plugin prefix
     *
     * @param log Message to print
     */
    @Override
    public void log(String log) {
        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&',
                        ChatColor.DARK_BLUE + "[" + ChatColor.WHITE + getPlugin().getName() + ChatColor.DARK_BLUE + "|"
                                + ChatColor.WHITE + getID() + ChatColor.DARK_BLUE + "] " + ChatColor.WHITE + log));
    }


    @NotNull
    public YMLSection getLanguageSection(@Nullable CommandSender who) {
        return getPlugin().getLanguageConfig(who).loadSection("module." + this.getID());
    }
}
