package emanondev.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;

import de.myzelyam.api.vanish.VanishAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public abstract class CoreCommand extends Command implements PluginIdentifiableCommand {

	private final CorePlugin plugin;
	private final YMLConfig config;
	private final String id;

	private final Permission permission;

	/**
	 * Returns the ID of the Command.
	 * 
	 * @return Command ID.
	 */
	public String getID() {
		return id;
	}

	/**
	 * Returns config file of this Command.
	 * 
	 * @return Config file of this Command.
	 */
	public YMLConfig getConfig() {
		return config;
	}

	/**
	 * Returns the owner of this PluginIdentifiableCommand.
	 * 
	 * @return Plugin that owns this Command.
	 */
	@Override
	public CorePlugin getPlugin() {
		return plugin;
	}

	/**
	 * Returns the permission associated to the command.
	 * 
	 * @return the permission associated to the command
	 */
	public @Nullable Permission getCommandPermission() {
		return permission;
	}

	/**
	 * Construct a new Command. Note: id is used for both config file of the command
	 * and default command name.
	 * 
	 * @param id
	 *            Command ID.
	 * @param plugin
	 *            Plugin that own this Command.
	 * @param permission
	 *            Permission required to use this Command if exists.
	 * @param defaultDescription
	 *            Default description of the Command, might be updated on Command
	 *            file.
	 */
	public CoreCommand(@Nonnull String id, @Nonnull CorePlugin plugin, @Nullable Permission permission,
			@Nullable String defaultDescription) {
		this(id, plugin, permission, defaultDescription, null);
	}

	/**
	 * Construct a new Command. Note: id is used for both config file of the command
	 * and default command name.
	 * 
	 * @param id
	 *            Command ID.
	 * @param plugin
	 *            Plugin that own this Command.
	 * @param permission
	 *            Permission required to use this Command if exists.
	 * @param defaultDescription
	 *            Default description of the Command, might be updated on Command
	 *            file.
	 * @param defaultAliases
	 *            Default aliases of the Command, might be changed on Command file
	 *            and applied with Server restart or Plugin restart.
	 */
	public CoreCommand(@Nonnull String id, @Nonnull CorePlugin plugin, @Nullable Permission permission,
			@Nullable String defaultDescription, @Nullable List<String> defaultAliases) {
		super(plugin.getConfig("Commands/" + id.toLowerCase()).loadString("info.name", id.toLowerCase()).toLowerCase());
		if (id.isEmpty() || id.contains(" "))
			throw new IllegalArgumentException("Invalid id");
		this.id = id.toLowerCase();

		this.plugin = plugin;
		this.config = this.plugin.getConfig("Commands/" + this.id);
		this.permission = permission;
		List<String> tempAliases = new ArrayList<String>();
		for (String alias : config.loadStringList("info.aliases",
				defaultAliases == null ? Collections.emptyList() : defaultAliases)) {
			if (alias == null)
				new NullPointerException("Null alias while creating command '" + id + "' (" + getName() + ")")
						.printStackTrace();
			if (alias.contains(" "))
				new IllegalArgumentException(
						"Invalid alias '" + alias + "' while creating command '" + id + "' (" + getName() + ")")
								.printStackTrace();
			if (tempAliases.contains(alias.toLowerCase()))
				new IllegalArgumentException(
						"Alias used twice '" + alias + "' while creating command '" + id + "' (" + getName() + ")")
								.printStackTrace();
			tempAliases.add(alias.toLowerCase());
		}
		this.setAliases(tempAliases);
		this.setDescription(config.loadMessage("info.description", defaultDescription, true));
		this.setUsage(config.loadMessage("info.usage", "&cUsage: /" + getName(), true));
		if (permission != null) {
			// plugin.registerPermission(permission);
			this.setPermission(permission.getName());
			this.setPermissionMessage(
					config.loadMessage("info.permission-message", getDefaultPermissionMessage(), true));
		}
	}

	/**
	 * Override this only if you wish to change message shown on command fail used
	 * as default<br>
	 * Note: default is used only if the config file of the command has not value
	 * set for this message
	 * 
	 * @return defaultPermissionMessage
	 */
	protected String getDefaultPermissionMessage() {
		return permission == null ? (ChatColor.RED + "You lack of permissions")
				: (ChatColor.RED + "You lack of permission " + permission.getName());
	}

	/**
	 * Execute the command.
	 * 
	 * @param sender
	 *            Source object which is executing this command
	 * @param alias
	 *            The alias of the command used
	 * @param args
	 *            All arguments passed to the command, split via ' '
	 * @return true
	 */
	@Override
	public final boolean execute(@Nonnull CommandSender sender, @Nonnull String alias, @Nonnull String[] args) {
		if (this.getCommandPermission() != null && !sender.hasPermission(this.getCommandPermission())) {
			this.permissionLackNotify(sender, this.getCommandPermission());
			return true;
		}
		onExecute(sender, alias, args);
		return true;
	}

	/**
	 * Execute the command.
	 * 
	 * @param sender
	 *            Source object which is executing this command
	 * @param alias
	 *            The alias of the command used
	 * @param args
	 *            All arguments passed to the command, split via ' '
	 */
	public abstract void onExecute(@Nonnull CommandSender sender, @Nonnull String alias, @Nonnull String[] args);

	/**
	 * Executed on tab completion for this command, returning a list of options the
	 * player can tab through.
	 * 
	 * @param sender
	 *            Source object which is executing this command
	 * @param alias
	 *            the alias being used
	 * @param args
	 *            All arguments passed to the command, split via ' '
	 * @param location
	 *            The position looked at by the sender, or null if none
	 * @return a list of tab-completions for the specified arguments. This will
	 *         never be null. List may be immutable.
	 * @throws IllegalArgumentException
	 *             if sender, alias, or args is null
	 */
	@Override
	public final List<String> tabComplete(@Nonnull CommandSender sender, @Nonnull String alias, @Nonnull String[] args,
			@Nullable Location location) {
		Validate.isTrue(sender != null && alias != null && args != null);
		List<String> val = onComplete(sender, alias, args, location);
		return val == null ? new ArrayList<>() : val;
	}

	/**
	 * Executed on tab completion for this command, returning a list of options the
	 * player can tab through.
	 * 
	 * @param sender
	 *            Source object which is executing this command
	 * @param alias
	 *            the alias being used
	 * @param args
	 *            All arguments passed to the command, split via ' '
	 * @return a list of tab-completions for the specified arguments. This will
	 *         never be null. List may be immutable.
	 * @throws IllegalArgumentException
	 *             if sender, alias, or args is null
	 */
	@Override
	public final List<String> tabComplete(@Nonnull CommandSender sender, @Nonnull String alias,
			@Nonnull String[] args) {
		Validate.isTrue(sender != null && alias != null && args != null);
		return onComplete(sender, alias, args, null);
	}

	/**
	 * Executed on tab completion for this command, returning a list of options the
	 * player can tab through.
	 * 
	 * @param sender
	 *            Source object which is executing this command
	 * @param alias
	 *            the alias being used
	 * @param args
	 *            All arguments passed to the command, split via ' '
	 * @param loc
	 *            sender location
	 * @return a list of tab-completions for the specified arguments. May be null.
	 * @throws IllegalArgumentException
	 *             if sender, alias, or args is null
	 */
	public abstract List<String> onComplete(@Nonnull CommandSender sender, @Nonnull String alias,
			@Nonnull String[] args, @Nullable Location loc);

	/**
	 * Gets text based on sender language.<br>
	 * Shortcut for
	 * {@link #getPlugin()}.{@link CorePlugin#getLanguageConfig(CommandSender)
	 * getLanguageConfig(sender)}.{@link YMLConfig#loadString(String, String, org.bukkit.entity.Player, boolean, String...)
	 * loadString(path, def, sender, true, args)}
	 * 
	 * @param sender
	 *            Target of the message, also used for PAPI compability.
	 * @param path
	 *            Path to get the message.
	 * @param def
	 *            Default message
	 * @param args
	 *            Holders and Replacers in the format
	 *            ["holder#1","replacer#1","holder#2","replacer#2"...]
	 * @return Message based on sender language
	 */
	@Deprecated
	public @Nullable String loadLanguageMessage(@Nullable CommandSender sender, @Nonnull String path,
			@Nullable String def, String... args) {
		return getPlugin().getLanguageConfig(sender).loadMessage(path, def, true, args);
	}

	/**
	 * Send to sender a text based on sender language.<br>
	 * No message is send if text is null or empty<br>
	 * Shortcut for
	 * sender.sendMessage({@link #loadLanguageMessage(CommandSender, String, String, String...)
	 * loadLanguageMessage(sender, path, def, args)})
	 * 
	 * @param sender
	 *            Target of the message, also used for PAPI compability.
	 * @param path
	 *            Path to get the message.
	 * @param def
	 *            Default message
	 * @param args
	 *            Holders and Replacers in the format
	 *            ["holder#1","replacer#1","holder#2","replacer#2"...]
	 */
	@Deprecated
	public void sendLanguageMessage(CommandSender sender, String path, String def, String... args) {
		String msg = loadLanguageMessage(sender, path, def, args);
		if (msg != null && !msg.isEmpty())
			sender.sendMessage(msg);
	}

	/**
	 * @param arg
	 *            argument to read
	 * @return double value of the string or null if parsing failed
	 */
	protected @Nullable Double readDouble(@Nonnull String arg) {
		try {
			return Double.valueOf(arg);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * @param arg
	 *            argument to read
	 * @return target player or null if player is offline or vanished
	 */
	@Deprecated
	protected @Nullable Player readPlayer(@Nonnull String arg) {
		Player p = Bukkit.getPlayer(arg);
		if (p==null)
			return null;
		if (Hooks.isVanishEnabled())
			return VanishAPI.isInvisible(p)?null:p;
		return p;
	}

	/**
	 * @param sender
	 *            who sended the command
	 * @param arg
	 *            argument to read
	 * @return target player or null if player is offline or vanished
	 */
	protected @Nullable Player readPlayer(CommandSender sender,@Nonnull String arg) {
		Player p = Bukkit.getPlayer(arg);
		if (p==null)
			return null;
		if (Hooks.isVanishEnabled()) {
			if (sender instanceof Player)
				return VanishAPI.canSee((Player) sender, p)?p:null;
			return VanishAPI.isInvisible(p)?null:p;
		}
		return p;
	}

	/**
	 * @param arg
	 *            argument to read
	 * @return target player or null if player never joined the server
	 */
	protected @Nullable OfflinePlayer readOfflinePlayer(@Nonnull String arg) {
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(arg);
		if (player.getLastPlayed() == 0)
			return null;
		return player;
	}

	/**
	 * 
	 * @param arg
	 *            argument to read
	 * @return int value of the string or null if parsing failed
	 */
	protected @Nullable Integer readInt(@Nonnull String arg) {
		try {
			return Integer.valueOf(arg);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param sender
	 *            sender
	 * @param prefix
	 *            prefix to match, case insensitive
	 * @return a list of player names from online players for the given prefix
	 *         filthering visible players if vanishAPI is active
	 */
	protected @Nonnull List<String> completePlayerNames(@Nullable CommandSender sender, @Nullable String prefix) {
		return completePlayerNames(sender, prefix, Bukkit.getOnlinePlayers());
	}

	/**
	 * 
	 * @param sender
	 *            the sender
	 * @param prefix
	 *            prefix to match, case insensitive
	 * @param players
	 *            which player names are considered?
	 * @return a list of player names from the given collection for the given prefix
	 *         filthering visible players id vanishAPI is active
	 */
	protected @Nonnull List<String> completePlayerNames(@Nullable CommandSender sender, @Nullable String prefix,
			@Nullable Collection<? extends Player> players) {
		if (players == null)
			return new ArrayList<>();
		if (prefix != null)
			prefix = prefix.toLowerCase();
		else
			prefix = "";
		List<String> list = new ArrayList<>();

		if (Hooks.isVanishEnabled() && sender != null && (sender instanceof Player)) {
			for (Player p : players)
				if (p.getName().toLowerCase().startsWith(prefix) && VanishAPI.canSee((Player) sender, p))
					list.add(p.getName());
		} else
			for (Player p : players)
				if (p.getName().toLowerCase().startsWith(prefix))
					list.add(p.getName());
		return list;
	}

	protected @Nonnull <K> List<String> complete(@Nullable String prefix, @Nullable Collection<K> values,
			@Nonnull Function<K, String> eval) {
		return complete(prefix, values, eval, null);
	}

	protected @Nonnull <K> List<String> complete(@Nullable String prefix, @Nullable Collection<K> values,
			@Nonnull Function<K, String> eval, @Nullable Predicate<K> isValid) {
		if (values == null)
			return new ArrayList<>();
		List<String> results = new ArrayList<>();
		if (prefix == null || prefix.isEmpty()) {
			for (K val : values)
				try {
					if (isValid == null || isValid.test(val)) {
						String value = eval.apply(val);
						if (value != null)
							results.add(value);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			return results;
		}
		prefix = prefix.toLowerCase();
		for (K val : values)
			try {
				if (isValid == null || isValid.test(val)) {
					String value = eval.apply(val);
					if (value != null && value.toLowerCase().startsWith(prefix))
						results.add(value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		return results;
	}

	/**
	 * @param <K>
	 *            the class of the enum
	 * @param prefix
	 *            prefix to match, case insensitive
	 * @param type
	 *            class of enums
	 * @return a list of lowcased string from enums of class type matching prefix is
	 *         true (ignoring caps)
	 */
	protected @Nonnull <K extends Enum<K>> List<String> complete(@Nullable String prefix, @Nonnull Class<K> type) {
		List<String> results = new ArrayList<>();
		if (prefix == null || prefix.isEmpty()) {
			for (Enum<K> e : type.getEnumConstants())
				results.add(e.toString().toLowerCase());
			return results;
		}
		prefix = prefix.toLowerCase();
		for (K e : type.getEnumConstants())
			if (e.toString().toLowerCase().startsWith(prefix))
				results.add(e.toString().toLowerCase());
		return results;
	}

	/**
	 * 
	 * @param <K>
	 *            the class of the enum
	 * @param prefix
	 *            prefix to match, case insensitive
	 * @param type
	 *            class of enums
	 * @param predicate
	 *            filther
	 * @return a list of lowcased string from enums of class type matching prefix
	 *         and predicate.apply() is true (ignoring caps)
	 */
	protected @Nonnull <K extends Enum<K>> List<String> complete(@Nullable String prefix, @Nonnull Class<K> type,
			@Nonnull Predicate<K> predicate) {
		List<String> results = new ArrayList<>();
		if (prefix == null || prefix.isEmpty()) {
			for (K e : type.getEnumConstants())
				if (predicate.test(e))
					results.add(e.name().toLowerCase());
			return results;
		}
		prefix = prefix.toLowerCase();
		for (K e : type.getEnumConstants())
			if (e.name().toLowerCase().startsWith(prefix))
				if (predicate.test(e))
					results.add(e.name().toLowerCase());
		return results;
	}

	/**
	 * 
	 * @param prefix
	 *            prefix to match, case insensitive
	 * @param elements
	 *            elements to match
	 * @return a list of strings matching prefix
	 */
	protected @Nonnull List<String> complete(@Nullable String prefix, @Nullable Collection<String> elements) {
		if (elements == null)
			return new ArrayList<>();
		if (prefix == null || prefix.isEmpty())
			return new ArrayList<>(elements);
		prefix = prefix.toLowerCase();
		List<String> results = new ArrayList<>();
		for (String e : elements)
			if (e.toLowerCase().startsWith(prefix))
				results.add(e.toString());
		return results;
	}

	/**
	 * 
	 * @param prefix
	 *            prefix to match, case insensitive
	 * @param elements
	 *            elements to match
	 * @param predicate
	 *            function to choose if elements should be selected or discarded
	 * @return a list of strings matching prefix
	 */
	protected @Nonnull List<String> complete(@Nullable String prefix, @Nullable Collection<String> elements,
			Predicate<String> predicate) {
		if (elements == null)
			return new ArrayList<>();
		List<String> results = new ArrayList<>();
		if (prefix == null || prefix.isEmpty()) {
			for (String e : elements)
				if (predicate.test(e))
					results.add(e);
			return results;
		}
		prefix = prefix.toLowerCase();
		for (String e : elements)
			if (e.toLowerCase().startsWith(prefix) && predicate.test(e))
				results.add(e.toString());
		return results;
	}

	/**
	 * 
	 * @param prefix
	 *            prefix to match, case insensitive
	 * @param elements
	 *            elements to match
	 * @return a list of strings matching prefix
	 */
	protected @Nonnull List<String> complete(@Nullable String prefix, @Nullable String[] elements) {
		if (elements == null)
			return new ArrayList<>();
		if (prefix == null || prefix.isEmpty())
			return Arrays.asList(elements);
		prefix = prefix.toLowerCase();
		List<String> results = new ArrayList<>();
		for (String e : elements)
			if (e.toLowerCase().startsWith(prefix))
				results.add(e.toString());
		return results;
	}

	protected @Nonnull List<String> completeBoolean(@Nullable String prefix) {
		List<String> results = new ArrayList<>();
		if (prefix == null || prefix.isEmpty()) {
			results.add("false");
			results.add("true");
			return results;
		}
		prefix = prefix.toLowerCase();
		if ("true".startsWith(prefix))
			results.add("true");
		if ("false".startsWith(prefix))
			results.add("false");
		return results;
	}

	/**
	 * 
	 * @param sender
	 *            the sender
	 * @param permission
	 *            the permission
	 * @return true if permission is null or sender has permission
	 */
	protected boolean hasPermission(@Nonnull Permissible sender, @Nullable Permission permission) {
		if (permission == null)
			return true;
		if (sender == null)
			throw new NullPointerException();
		return sender.hasPermission(permission);
	}

	/**
	 * @param sender
	 *            the sender
	 * @param permission
	 *            the permission
	 * @return true if permission is null or sender has permission
	 * @see #hasPermission(Permissible, Permission)
	 */
	@Deprecated
	protected boolean hasPermission(@Nonnull Permissible sender, @Nullable String permission) {
		if (permission == null)
			return true;
		if (sender == null)
			throw new NullPointerException();
		return sender.hasPermission(permission);
	}

	/**
	 * Notify sender the lack of permission as specified at
	 * 'generic.lack_permission' path of language file
	 * 
	 * @param sender who lack of permission
	 * @param perm lacking permission
	 */
	protected void permissionLackNotify(@Nonnull CommandSender sender,@Nonnull Permission perm) {
		getPlugin().getLanguageConfig(sender).loadMessage("generic.lack_permission",
				"&cYou lack of permission %permission%", "%permission%", perm.getName());
	}

	/**
	 * Notify sender the command may be used by players only
	 * 
	 * @param sender who
	 */
	protected void playerOnlyNotify(CommandSender sender) {
		getPlugin().getLanguageConfig(sender).loadMessage("generic.players_only", "&cCommand for players only");
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def,
			boolean color, @Nullable CommandSender target, String... holders) {
		UtilsMessages.sendMessage(reicever, loadMessage(reicever, path, def, color, target, holders));
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 * @return message and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color,
			@Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("command." + this.getID() + "." + path, def, color,
				target, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			boolean color, @Nullable CommandSender target, String... holders) {
		UtilsMessages.sendMessage(reicever, loadMessage(reicever, path, def, color, target, holders));
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 * @return message and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			boolean color, @Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("command." + this.getID() + "." + path, def, color,
				target, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def,
			boolean color, String... holders) {
		sendMessageFeedback(reicever, path, def, color, reicever, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			boolean color, String... holders) {
		sendMessageFeedback(reicever, path, def, color, reicever, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def,
			String... holders) {
		sendMessageFeedback(reicever, path, def, true, reicever, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			String... holders) {
		sendMessageFeedback(reicever, path, def, true, reicever, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 */
	protected void sendMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def,
			@Nullable CommandSender target, String... holders) {
		sendMessageFeedback(reicever, path, def, true, target, holders);
	}

	/**
	 * Sends a message to reicever
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 */
	protected void sendMultiMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			@Nullable CommandSender target, String... holders) {
		sendMessageFeedback(reicever, path, def, true, target, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 * @return message and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color,
			String... holders) {
		return loadMessage(reicever, path, def, color, reicever, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 * @return message and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			boolean color, String... holders) {
		return loadMessage(reicever, path, def, color, reicever, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 * @return message and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable String def,
			String... holders) {
		return loadMessage(reicever, path, def, true, reicever, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 * @return message and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			String... holders) {
		return loadMessage(reicever, path, def, true, reicever, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 * @return message and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable String def,
			@Nullable CommandSender target, String... holders) {
		return loadMessage(reicever, path, def, true, target, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 * @return message and set default if absent
	 */
	protected String loadMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			@Nullable CommandSender target, String... holders) {
		return loadMessage(reicever, path, def, true, target, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param action Default click action type
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
			boolean color, @Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadComponentMessage("command." + this.getID() + "." + path,
				defMessage, defHover, defClick, action, color, target, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param action Default click action type
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
			boolean color, String... holders) {
		return loadComponentMessage(reicever, path, defMessage, defHover, defClick, action, color, reicever, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param action Default click action type
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
			@Nullable CommandSender target, String... holders) {
		return loadComponentMessage(reicever, path, defMessage, defHover, defClick, action, true, target, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param action Default click action type
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
			String... holders) {
		return loadComponentMessage(reicever, path, defMessage, defHover, defClick, action, true, reicever, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, boolean color,
			String... holders) {
		return loadComponentMessage(reicever, path, defMessage, defHover, defClick, null, color, reicever, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick,
			@Nullable CommandSender target, String... holders) {
		return loadComponentMessage(reicever, path, defMessage, defHover, defClick, null, true, target, holders);
	}

	/**
	 * Returns message and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message and set default if absent
	 */
	protected @Nullable ComponentBuilder loadComponentMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, String... holders) {
		return loadComponentMessage(reicever, path, defMessage, defHover, defClick, null, true, reicever, holders);
	}

	/**
	 * Get message list and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return message list and set default if absent
	 */
	protected @Nullable List<String> loadMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable List<String> def, boolean color, String... holders) {
		return loadMultiMessage(reicever, path, def, color, reicever, holders);
	}

	/**
	 * Get message list and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 * @return message list and set default if absent
	 */
	protected @Nullable List<String> loadMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable List<String> def, @Nullable CommandSender target, String... holders) {
		return loadMultiMessage(reicever, path, def, true, target, holders);
	}

	/**
	 * Get message list and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 * @return message list and set default if absent
	 */
	protected @Nullable List<String> loadMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable List<String> def, String... holders) {
		return loadMultiMessage(reicever, path, def, true, reicever, holders);
	}

	/**
	 * Get message list and set default if absent
	 * 
	 * @param reicever message target
	 * @param path
	 *            final configuration path is
	 *            <b>'command.'+this.getID()+'.'+path</b>
	 * @param def Default message
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"... additional placeholders as couples holder, value
	 * @return message list and set default if absent
	 */
	protected @Nullable List<String> loadMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable List<String> def, boolean color, @Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMultiMessage("command." + this.getID() + "." + path, def,
				color, reicever, holders);
	}

	@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def,
			boolean color, @Nullable CommandSender target, String... holders) {
		UtilsMessages.sendMessage(reicever, getMessage(reicever, path, def, color, target, holders));
	}

	@Deprecated
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color,
			@Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("command." + this.getID() + "." + path, def, color,
				target, holders);
	}

	@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			boolean color, @Nullable CommandSender target, String... holders) {
		UtilsMessages.sendMessage(reicever, getPlugin().getLanguageConfig(reicever)
				.loadMessage("command." + this.getID() + "." + path, def, color, target, holders));
	}

	@Deprecated
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, boolean color,
			@Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("command." + this.getID() + "." + path, def, color,
				target, holders);
	}

	@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def,
			boolean color, String... holders) {
		giveMessageFeedback(reicever, path, def, color, reicever, holders);
	}

	@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			boolean color, String... holders) {
		giveMessageFeedback(reicever, path, def, color, reicever, holders);
	}

	@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def,
			String... holders) {
		giveMessageFeedback(reicever, path, def, true, reicever, holders);
	}

	@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			String... holders) {
		giveMessageFeedback(reicever, path, def, true, reicever, holders);
	}

	@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable String def,
			@Nullable CommandSender target, String... holders) {
		giveMessageFeedback(reicever, path, def, true, target, holders);
	}

	@Deprecated
	protected void giveMessageFeedback(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			@Nullable CommandSender target, String... holders) {
		giveMessageFeedback(reicever, path, def, true, target, holders);
	}

	@Deprecated

	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, boolean color,
			String... holders) {
		return getMessage(reicever, path, def, color, reicever, holders);
	}

	@Deprecated
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def, boolean color,
			String... holders) {
		return getMessage(reicever, path, def, color, reicever, holders);
	}

	@Deprecated
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable String def, String... holders) {
		return getMessage(reicever, path, def, true, reicever, holders);
	}

	@Deprecated
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			String... holders) {
		return getMessage(reicever, path, def, true, reicever, holders);
	}

	@Deprecated
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable String def,
			@Nullable CommandSender target, String... holders) {
		return getMessage(reicever, path, def, true, target, holders);
	}

	@Deprecated
	protected String getMessage(CommandSender reicever, @Nonnull String path, @Nullable List<String> def,
			@Nullable CommandSender target, String... holders) {
		return getMessage(reicever, path, def, true, target, holders);
	}

	@Deprecated

	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
			boolean color, @Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMessage("command." + this.getID() + "." + path, defMessage,
				defHover, defClick, action, color, target, holders);
	}

	@Deprecated
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
			boolean color, String... holders) {
		return getComponent(reicever, path, defMessage, defHover, defClick, action, color, reicever, holders);
	}

	@Deprecated
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
			@Nullable CommandSender target, String... holders) {
		return getComponent(reicever, path, defMessage, defHover, defClick, action, true, target, holders);
	}

	@Deprecated
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action,
			String... holders) {
		return getComponent(reicever, path, defMessage, defHover, defClick, action, true, reicever, holders);
	}

	@Deprecated
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, boolean color,
			String... holders) {
		return getComponent(reicever, path, defMessage, defHover, defClick, null, color, reicever, holders);
	}

	@Deprecated
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick,
			@Nullable CommandSender target, String... holders) {
		return getComponent(reicever, path, defMessage, defHover, defClick, null, true, target, holders);
	}

	@Deprecated
	protected @Nullable ComponentBuilder getComponent(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable String defMessage, @Nullable String defHover, @Nullable String defClick, String... holders) {
		return getComponent(reicever, path, defMessage, defHover, defClick, null, true, reicever, holders);
	}

	@Deprecated
	protected @Nullable List<String> getMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable List<String> def, boolean color, String... holders) {
		return getMultiMessage(reicever, path, def, color, reicever, holders);
	}

	@Deprecated
	protected @Nullable List<String> getMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable List<String> def, @Nullable CommandSender target, String... holders) {
		return getMultiMessage(reicever, path, def, true, target, holders);
	}

	@Deprecated
	protected @Nullable List<String> getMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable List<String> def, String... holders) {
		return getMultiMessage(reicever, path, def, true, reicever, holders);
	}

	@Deprecated
	protected @Nullable List<String> getMultiMessage(@Nonnull CommandSender reicever, @Nonnull String path,
			@Nullable List<String> def, boolean color, @Nullable CommandSender target, String... holders) {
		return getPlugin().getLanguageConfig(reicever).loadMultiMessage("command." + this.getID() + "." + path, def,
				color, reicever, holders);
	}

}
