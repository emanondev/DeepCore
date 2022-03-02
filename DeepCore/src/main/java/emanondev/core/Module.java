package emanondev.core;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import emanondev.core.util.ConsoleLogger;
import net.md_5.bungee.api.ChatColor;

public abstract class Module implements Listener,ConsoleLogger {

	private final CorePlugin plugin;

	private final String id;
	private final String description;

	@Deprecated
	public Module(String id, CorePlugin plugin) {
		this(id, plugin, null);
	}

	public Module(String id, CorePlugin plugin, String description) {
		if (id == null || plugin == null)
			throw new NullPointerException();
		if (!UtilsString.isValidID(id))
			throw new IllegalArgumentException("invalid module id");
		this.id = id;
		this.plugin = plugin;
		this.description = description;
		getPlugin().getConfig("modules.yml").loadString(this.getID() + ".decription", this.description);
	}

	public final String getDescription() {
		return this.description;
	}

	public final String getID() {
		return this.id;
	}

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
	public void log(String log) {
		Bukkit.getConsoleSender()
				.sendMessage(ChatColor.translateAlternateColorCodes('&',
						ChatColor.DARK_BLUE + "[" + ChatColor.WHITE + getPlugin().getName() + ChatColor.DARK_BLUE + "|"
								+ ChatColor.WHITE + getID() + ChatColor.DARK_BLUE + "] " + ChatColor.WHITE + log));
	}
	

	public YMLSection getLanguageSection(@NotNull CommandSender who) {
		return getPlugin().getLanguageConfig(who).loadSection("module." + this.getID());
	}
}
