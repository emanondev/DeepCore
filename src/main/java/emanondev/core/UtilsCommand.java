package emanondev.core;

import emanondev.core.util.VanishApi;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * @see emanondev.core.utility.CompleteUtility
 * @see emanondev.core.utility.ReadUtility
 */
@Deprecated
public final class UtilsCommand {

    private UtilsCommand() {
        throw new AssertionError();
    }

    /**
     * @param arg argument to read
     * @return double value of the string or null
     */
    public static @Nullable Double readDouble(@NotNull String arg) {
        try {
            return Double.valueOf(arg);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param arg argument to read
     * @return target player or null if player is offline
     */
    public static @Nullable Player readPlayer(@NotNull String arg) {
        return Bukkit.getPlayer(arg);
    }

    /**
     * @param arg argument to read
     * @return target player or null if player never joined the server
     */
    public static @Nullable OfflinePlayer readOfflinePlayer(@NotNull String arg) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(arg);
        if (player.getLastPlayed() == 0)
            return null;
        return player;
    }

    /**
     * @param arg argument to read
     * @return int value of the string or null
     */
    public static @Nullable Integer readInt(@NotNull String arg) {
        try {
            return Integer.valueOf(arg);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param sender sender
     * @param prefix prefix to match, case-insensitive
     * @return a list of player names from online players for the given prefix
     * filtering visible player ids vanishAPI is active
     */
    public static @NotNull List<String> completePlayerNames(@Nullable CommandSender sender, @Nullable String prefix) {
        return completePlayerNames(sender, prefix, Bukkit.getOnlinePlayers());
    }

    /**
     * @param sender  the sender
     * @param prefix  prefix to match, case-insensitive
     * @param players which player names are considered?
     * @return a list of player names from the given collection for the given prefix
     * filtering visible player ids vanishAPI is active
     */
    public static @NotNull List<String> completePlayerNames(@Nullable CommandSender sender, @Nullable String prefix,
                                                            @Nullable Collection<? extends Player> players) {
        if (prefix != null)
            prefix = prefix.toLowerCase(Locale.ENGLISH);
        else
            prefix = "";
        List<String> list = new ArrayList<>();
        if (players == null)
            return list;

        if (Hooks.isVanishEnabled() && (sender instanceof Player)) {
            for (Player p : players)
                if (p.getName().toLowerCase(Locale.ENGLISH).startsWith(prefix) && VanishApi.canSee(sender, p))
                    list.add(p.getName());
        } else
            for (Player p : players)
                if (p.getName().toLowerCase(Locale.ENGLISH).startsWith(prefix))
                    list.add(p.getName());
        return list;
    }

    @Deprecated
    public static @NotNull <K> List<String> complete(@Nullable String prefix, @Nullable Collection<K> values,
                                                     @NotNull Function<K, String> eval, @Nullable com.google.common.base.Predicate<K> isValid) {
        List<String> results = new ArrayList<>();
        if (values == null)
            return results;
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
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        for (K val : values)
            try {
                if (isValid == null || isValid.apply(val)) {
                    String value = eval.apply(val);
                    if (value.toLowerCase(Locale.ENGLISH).startsWith(prefix))
                        results.add(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return results;
    }

    public static @NotNull <K> List<String> complete(@Nullable String prefix, @Nullable Collection<K> values,
                                                     @NotNull Function<K, String> eval, @Nullable Predicate<K> isValid) {
        List<String> results = new ArrayList<>();
        if (values == null)
            return results;
        if (prefix == null || prefix.isEmpty()) {
            for (K val : values)
                try {
                    if (isValid == null || isValid.test(val))
                        results.add(eval.apply(val));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return results;
        }
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        for (K val : values)
            try {
                if (isValid == null || isValid.test(val)) {
                    String value = eval.apply(val);
                    if (value.toLowerCase(Locale.ENGLISH).startsWith(prefix))
                        results.add(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return results;
    }

    /**
     * @param <K>    the class of the enum
     * @param prefix prefix to match, case-insensitive
     * @param type   class of enums
     * @return a list of low-cased string from enums of class type matching prefix is
     * true (ignoring caps)
     */
    public static @NotNull <K extends Enum<K>> List<String> complete(@Nullable String prefix, @NotNull Class<K> type) {
        List<String> results = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) {
            for (Enum<K> e : type.getEnumConstants())
                results.add(e.toString().toLowerCase(Locale.ENGLISH));
            return results;
        }
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        for (K e : type.getEnumConstants())
            if (e.toString().toLowerCase(Locale.ENGLISH).startsWith(prefix))
                results.add(e.toString().toLowerCase(Locale.ENGLISH));
        return results;
    }

    /**
     * @param <K>       the class of the enum
     * @param prefix    prefix to match, case-insensitive
     * @param type      class of enums
     * @param predicate filter
     * @return a list of low-cased string from enums of class type matching prefix
     * and predicate.apply() is true (ignoring caps)
     */
    @Deprecated
    public static @NotNull <K extends Enum<K>> List<String> complete(@Nullable String prefix, @NotNull Class<K> type,
                                                                     @NotNull com.google.common.base.Predicate<K> predicate) {
        List<String> results = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) {
            for (K e : type.getEnumConstants())
                if (predicate.apply(e))
                    results.add(e.toString().toLowerCase(Locale.ENGLISH));
            return results;
        }
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        for (K e : type.getEnumConstants())
            if (e.toString().toLowerCase(Locale.ENGLISH).startsWith(prefix))
                if (predicate.apply(e))
                    results.add(e.toString().toLowerCase(Locale.ENGLISH));
        return results;
    }

    /**
     * @param <K>       the class of the enum
     * @param prefix    prefix to match, case-insensitive
     * @param type      class of enums
     * @param predicate filter
     * @return a list of low-cased string from enums of class type matching prefix
     * and predicate.apply() is true (ignoring caps)
     */
    public static @NotNull <K extends Enum<K>> List<String> complete(@Nullable String prefix, @NotNull Class<K> type,
                                                                     @NotNull Predicate<K> predicate) {
        List<String> results = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) {
            for (K e : type.getEnumConstants())
                if (predicate.test(e))
                    results.add(e.toString().toLowerCase(Locale.ENGLISH));
            return results;
        }
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        for (K e : type.getEnumConstants())
            if (e.toString().toLowerCase(Locale.ENGLISH).startsWith(prefix))
                if (predicate.test(e))
                    results.add(e.toString().toLowerCase(Locale.ENGLISH));
        return results;
    }

    /**
     * @param prefix   prefix to match, case-insensitive
     * @param elements elements to match
     * @return a list of strings matching prefix
     */
    public static @NotNull List<String> complete(@Nullable String prefix, @Nullable Collection<String> elements) {
        if (elements == null)
            return new ArrayList<>();
        if (prefix == null || prefix.isEmpty())
            return new ArrayList<>(elements);
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        List<String> results = new ArrayList<>();
        for (String e : elements)
            if (e.toLowerCase(Locale.ENGLISH).startsWith(prefix))
                results.add(e);
        return results;
    }

    /**
     * @param prefix   prefix to match, case-insensitive
     * @param elements elements to match
     * @return a list of strings matching prefix
     */
    public static @NotNull List<String> complete(@Nullable String prefix, @Nullable String[] elements) {
        if (prefix == null || prefix.isEmpty())
            return Arrays.asList(elements);
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        List<String> results = new ArrayList<>();
        if (elements == null)
            return results;
        for (String e : elements)
            if (e != null && e.toLowerCase(Locale.ENGLISH).startsWith(prefix))
                results.add(e);
        return results;
    }

    /**
     * @param sender     the sender
     * @param permission the permission
     * @return true if permission is null or sender has permission
     */
    public static boolean hasPermission(@NotNull Permissible sender, @Nullable Permission permission) {
        if (permission == null)
            return true;
        return sender.hasPermission(permission);
    }

    /**
     * @param sender     the sender
     * @param permission the permission
     * @return true if permission is null or sender has permission
     * @see #hasPermission(Permissible, Permission)
     */
    @Deprecated
    public static boolean hasPermission(@NotNull Permissible sender, @Nullable String permission) {
        if (permission == null)
            return true;
        return sender.hasPermission(permission);
    }
}
