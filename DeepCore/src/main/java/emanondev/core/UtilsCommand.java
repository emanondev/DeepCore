package emanondev.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;

import com.google.common.base.Predicate;

import de.myzelyam.api.vanish.VanishAPI;

@Deprecated
public class UtilsCommand {

	UtilsCommand() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param arg
	 *            argument to read
	 * @return double value of the string or null
	 */
	public static @Nullable Double readDouble(@Nonnull String arg) {
		try {
			return Double.valueOf(arg);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param arg
	 *            argument to read
	 * @return target player or null if player is offline
	 */
	public static @Nullable Player readPlayer(@Nonnull String arg) {
		return Bukkit.getPlayer(arg);
	}

	/**
	 * @param arg
	 *            argument to read
	 * @return target player or null if player never joined the server
	 */
	public static @Nullable OfflinePlayer readOfflinePlayer(@Nonnull String arg) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(arg);
		if (player.getLastPlayed() == 0)
			return null;
		return player;
	}

	/**
	 * 
	 * @param arg
	 *            argument to read
	 * @return int value of the string or null
	 */
	public static @Nullable Integer readInt(@Nonnull String arg) {
		try {
			return Integer.valueOf(arg);
		} catch (Exception e) {
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
	 *         filthering visible players id vanishAPI is active
	 */
	public static @Nonnull List<String> completePlayerNames(@Nullable CommandSender sender, @Nullable String prefix) {
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
	public static @Nonnull List<String> completePlayerNames(@Nullable CommandSender sender, @Nullable String prefix,
			@Nullable Collection<? extends Player> players) {
		if (prefix != null)
			prefix = prefix.toLowerCase();
		else
			prefix = "";
		List<String> list = new ArrayList<>();
		if (players == null)
			return list;

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

	public static @Nonnull <K> List<String> complete(@Nullable String prefix, @Nullable Collection<K> values,
			@Nonnull Function<K, String> eval, @Nullable Predicate<K> isValid) {
		List<String> results = new ArrayList<>();
		if (prefix == null || prefix.isEmpty()) {
			for (K val : values)
				try {
					if (isValid == null || isValid.apply(val))
						results.add(eval.apply(val));
				} catch (Exception e) {
					e.printStackTrace();
				}
			return results;
		}
		prefix = prefix.toLowerCase();
		for (K val : values)
			try {
				if (isValid == null || isValid.apply(val)) {
					String value = eval.apply(val);
					if (value.toLowerCase().startsWith(prefix))
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
	public static @Nonnull <K extends Enum<K>> List<String> complete(@Nullable String prefix, @Nonnull Class<K> type) {
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
	public static @Nonnull <K extends Enum<K>> List<String> complete(@Nullable String prefix, @Nonnull Class<K> type,
			@Nonnull Predicate<K> predicate) {
		List<String> results = new ArrayList<>();
		if (prefix == null || prefix.isEmpty()) {
			for (K e : type.getEnumConstants())
				if (predicate.apply(e))
					results.add(e.toString().toLowerCase());
			return results;
		}
		prefix = prefix.toLowerCase();
		for (K e : type.getEnumConstants())
			if (e.toString().toLowerCase().startsWith(prefix))
				if (predicate.apply(e))
					results.add(e.toString().toLowerCase());
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
	public static @Nonnull List<String> complete(@Nullable String prefix, @Nullable Collection<String> elements) {
		if (prefix == null || prefix.isEmpty())
			return new ArrayList<>(elements);
		prefix = prefix.toLowerCase();
		List<String> results = new ArrayList<>();
		if (elements == null)
			return results;
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
	 * @return a list of strings matching prefix
	 */
	public static @Nonnull List<String> complete(@Nullable String prefix, @Nullable String[] elements) {
		if (prefix == null || prefix.isEmpty())
			return Arrays.asList(elements);
		prefix = prefix.toLowerCase();
		List<String> results = new ArrayList<>();
		if (elements == null)
			return results;
		for (String e : elements)
			if (e.toLowerCase().startsWith(prefix))
				results.add(e.toString());
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
	public static boolean hasPermission(@Nonnull Permissible sender, @Nullable Permission permission) {
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
	public static boolean hasPermission(@Nonnull Permissible sender, @Nullable String permission) {
		if (permission == null)
			return true;
		if (sender == null)
			throw new NullPointerException();
		return sender.hasPermission(permission);
	}
}
