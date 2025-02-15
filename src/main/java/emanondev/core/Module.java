package emanondev.core;

import emanondev.core.util.FileLogger;
import emanondev.core.utility.ConsoleHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public abstract class Module implements Listener, ConsoleHelper, FileLogger {

    private final CorePlugin plugin;

    private final String id;
    private final String description;

    public Module(final @NotNull String id, final @NotNull CorePlugin plugin, final String description) {
        if (!UtilsString.isValidID(id))
            throw new IllegalArgumentException("invalid module id");
        this.id = id;
        this.plugin = plugin;
        this.description = description;
        getPlugin().getConfig("modules.yml").loadString(this.getID() + ".description", this.description);
    }

    /**
     * Returns module plugin
     *
     * @return module plugin
     */
    public final @NotNull CorePlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Returns module identifier
     *
     * @return module identifier
     */
    @NotNull
    public final String getID() {
        return this.id;
    }

    /**
     * Returns module description
     *
     * @return module description
     */
    @Nullable
    public final String getDescription() {
        return this.description;
    }

    @NotNull
    public YMLConfig getConfig() {
        return plugin.getConfig("modules" + File.separator + this.getID() + ".yml");
    }

    @NotNull
    public YMLConfig getConfig(final String fileName) {
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
    public YMLSection getLanguageSection(final @Nullable CommandSender who) {
        return getPlugin().getLanguageConfig(who).loadSection("module." + this.getID());
    }
}
