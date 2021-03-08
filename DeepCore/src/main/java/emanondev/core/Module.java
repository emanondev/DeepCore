package emanondev.core;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.ChatColor;

public abstract class Module implements Listener {

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

	/**
	 * logs on console message with plugin name and a green ✓ prefix
	 * 
	 * @param log Message to print
	 */
	public void logDone(String log) {
		logDone(ChatColor.GREEN, log);
	}

	/**
	 * logs on console message with plugin name and a colored ✓ prefix
	 * 
	 * @param log Message to print
	 * @param color Symbol color
	 */
	public void logDone(ChatColor color, String log) {
		log(color + "✓  " + ChatColor.WHITE + log);
	}

	/**
	 * logs on console message with plugin name and red ✗ prefix
	 * 
	 * @param log Message to print
	 */
	public void logProblem(String log) {
		logProblem(ChatColor.RED, log);
	}

	/**
	 * logs on console message with plugin name and colored ✗ prefix
	 * 
	 * @param log Message to print
	 * @param color Symbol color
	 */
	public void logProblem(ChatColor color, String log) {
		log(color + "✗  " + ChatColor.WHITE + log);
	}

	/**
	 * logs on console message with plugin name and yellow signal prefix
	 * 
	 * @param log Message to print
	 */
	public void logIssue(String log) {
		logIssue(ChatColor.YELLOW, log);
	}

	/**
	 * logs on console message with plugin name and colored signal prefix
	 * 
	 * @param log Message to print
	 * @param color Symbol color
	 */
	public void logIssue(ChatColor color, String log) {
		log(color + "⚠  " + ChatColor.WHITE + log);
	}

	/**
	 * logs on console message with plugin name and colored ✧ prefix
	 * 
	 * @param log Message to print
	 * @param color Symbol color
	 */
	public void logTetraStar(ChatColor color, String log) {
		log(color + "✧  " + ChatColor.WHITE + log);
	}

	/**
	 * logs on console message with plugin name and colored ☆ prefix
	 * 
	 * @param log Message to print
	 * @param color Symbol color
	 */
	public void logPentaStar(ChatColor color, String log) {
		log(color + "☆  " + ChatColor.WHITE + log);
	}
}
