package emanondev.core.utility;

import emanondev.core.Hooks;
import emanondev.core.util.VanishApi;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public final class CompleteUtility {

    private static final int MAX_COMPLETES = 200;

    private CompleteUtility() {
        throw new UnsupportedOperationException();
    }

    /**
     * Completes a prefix based on an enum class. This method returns a list of enum constant names
     * (in lowercase) that start with the provided prefix. The comparison is case-insensitive.
     *
     * @param prefix     The prefix to match against the enum constant names.
     * @param enumClass  The enum class to extract the values from.
     * @param <T>        The type of the enum.
     * @return A list of matching enum constant names (in lowercase).
     */
    @NotNull
    public static <T extends Enum<T>> List<String> complete(final @NotNull String prefix,
                                                            final @NotNull Class<T> enumClass) {
        return complete(prefix, enumClass, null);
    }

    /**
     * Completes a prefix based on an enum class with an additional filtering predicate.
     * This method returns a list of enum constant names (in lowercase) that start with the provided prefix
     * and satisfy the predicate condition. The comparison is case-insensitive.
     *
     * @param prefix     The prefix to match against the enum constant names.
     * @param enumClass       The enum class to extract the values from.
     * @param predicate  A predicate used to filter the enum constants.
     * @param <T>        The type of the enum.
     * @return A list of matching enum constant names (in lowercase) that satisfy the predicate.
     */
    @NotNull
    public static <T extends Enum<T>> List<String> complete(final @NotNull String prefix,
                                                            final @NotNull Class<T> enumClass,
                                                            final @Nullable Predicate<T> predicate) {
        String upPrefix = prefix.toUpperCase(Locale.ENGLISH);
        ArrayList<String> results = new ArrayList<>();
        for (T element : enumClass.getEnumConstants()) {
            if (!element.name().toUpperCase(Locale.ENGLISH).startsWith(upPrefix)) {
                continue;
            }
            if (predicate != null && !predicate.test(element)) {
                continue;
            }
            results.add(element.toString().toLowerCase(Locale.ENGLISH));
            if (results.size() > MAX_COMPLETES) {
                return results;
            }
        }
        return results;
    }

    /**
     * Completes a prefix based on a list of strings. This method returns a list of strings from the provided
     * list that start with the given prefix. The comparison is case-insensitive.
     *
     * @param prefix The prefix to match against the strings in the list.
     * @param list   The list of strings to search through.
     * @return A list of matching strings that start with the prefix.
     */
    @NotNull
    public static List<String> complete(final @NotNull String prefix,
                                        final @Nullable String... list) {
        if (list == null) {
            return List.of();
        }
        return complete(prefix, Arrays.asList(list), (Predicate<String>) null);
    }

    /**
     * Completes a prefix based on a collection of strings. This method returns a list of strings from the
     * provided collection that start with the given prefix. The comparison is case-insensitive.
     *
     * @param prefix The prefix to match against the strings in the collection.
     * @param list   The collection of strings to search through.
     * @return A list of matching strings that start with the prefix.
     */
    @NotNull
    public static List<String> complete(final @NotNull String prefix,
                                        final @Nullable Collection<String> list) {
        return complete(prefix, list, (Predicate<String>) null);
    }

    /**
     * Completes a prefix based on a collection of strings. This method returns a list of strings from the
     * provided collection that start with the given prefix. The comparison is case-insensitive.
     *
     * @param prefix The prefix to match against the strings in the collection.
     * @param list   The collection of strings to search through.
     * @return A list of matching strings that start with the prefix.
     */
    @NotNull
    public static List<String> complete(final @NotNull String prefix,
                                        final @Nullable Collection<String> list,
                                        final @Nullable Predicate<String> predicate) {
        if (list == null) {
            return List.of();
        }
        String lowPrefix = prefix.toLowerCase(Locale.ENGLISH);
        ArrayList<String> results = new ArrayList<>();
        for (String value : list) {
            if (!value.toLowerCase(Locale.ENGLISH).startsWith(lowPrefix)) {
                continue;
            }
            if (predicate != null && !predicate.test(value)) {
                continue;
            }

            results.add(value);
            if (results.size() > MAX_COMPLETES) {
                return results;
            }

        }
        return results;
    }

    /**
     * Completes a prefix based on a collection of objects, using a provided converter function to extract
     * a string value from each object. This method returns a list of matching strings that start with the prefix.
     * The comparison is case-insensitive.
     *
     * @param prefix    The prefix to match against the strings derived from the objects.
     * @param list      The collection of objects to search through.
     * @param converter A function used to convert each object to a string for comparison.
     * @param <T>       The type of the objects in the collection.
     * @return A list of matching strings derived from the objects that start with the prefix.
     */
    @NotNull
    public static <T> List<String> complete(final @NotNull String prefix,
                                            final @Nullable Collection<T> list,
                                            final @NotNull Function<T, String> converter) {
        return complete(prefix, list, converter, null);
    }

    /**
     * Completes a prefix based on a collection of objects, using a provided converter function to extract
     * a string value from each object. This method returns a list of matching strings that start with the prefix.
     * The comparison is case-insensitive.
     *
     * @param prefix    The prefix to match against the strings derived from the objects.
     * @param list      The collection of objects to search through.
     * @param converter A function used to convert each object to a string for comparison.
     * @param predicate A filter to allowed values
     * @param <T>       The type of the objects in the collection.
     * @return A list of matching strings derived from the objects that start with the prefix.
     */
    @NotNull
    public static <T> List<String> complete(final @NotNull String prefix,
                                            final @Nullable Collection<T> list,
                                            final @NotNull Function<T, String> converter,
                                            final @Nullable Predicate<T> predicate) {
        if (list == null) {
            return List.of();
        }
        String lowPrefix = prefix.toLowerCase(Locale.ENGLISH);
        ArrayList<String> results = new ArrayList<>();
        for (T value : list) {
            if (predicate != null && !predicate.test(value)) {
                continue;
            }
            String textValue;
            try {
                textValue = converter.apply(value);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if (!textValue.toLowerCase(Locale.ENGLISH).startsWith(lowPrefix)) {
                continue;
            }
            results.add(textValue);
            if (results.size() > MAX_COMPLETES) {
                return results;
            }
        }
        return results;
    }

    /**
     * Retrieves a list of online player names that begin with the specified prefix.
     * The search is case-insensitive.
     *
     * @param prefix The prefix to match against player names.
     * @return A list of player names that start with the given prefix.
     */
    @NotNull
    public static List<String> completePlayers(final @NotNull String prefix) {
        return completePlayers(null, prefix, Bukkit.getOnlinePlayers(), null);
    }

    /**
     * Retrieves a list of online player names that begin with the specified prefix.
     * The search is case-insensitive. This method allows specifying a command sender,
     * which can be used for visibility checks.
     *
     * @param forWho The command sender requesting the completion (can be null).
     * @param prefix The prefix to match against player names.
     * @return A list of matching player names.
     */
    @NotNull
    public static List<String> completePlayers(final @Nullable CommandSender forWho,
                                               final @NotNull String prefix) {
        return completePlayers(forWho, prefix, Bukkit.getOnlinePlayers(), null);
    }

    /**
     * Retrieves a list of online player names that match the given prefix, applying an optional filter.
     * The search is case-insensitive. This method allows specifying a command sender,
     * which can be used for visibility checks.
     *
     * @param forWho     The command sender requesting the completion (can be null).
     * @param prefix     The prefix to match against player names.
     * @param predicate  A filter to apply to the player list (can be null).
     * @return A list of matching player names after applying the filter.
     */
    @NotNull
    public static List<String> completePlayers(final @Nullable CommandSender forWho,
                                               final @NotNull String prefix,
                                               final @Nullable Predicate<Player> predicate) {
        return completePlayers(forWho, prefix, Bukkit.getOnlinePlayers(), predicate);
    }

    /**
     * Retrieves a filtered list of player names that begin with the specified prefix.
     * The search is case-insensitive and can be customized using a provided player collection
     * and an optional predicate. This method allows specifying a command sender,
     * which can be used for visibility checks.
     *
     * @param forWho     The command sender requesting the completion (can be null).
     * @param prefix     The prefix to match against player names.
     * @param players    The collection of players to search within.
     * @param predicate  An optional filter to apply to the players.
     * @return A list of matching player names after applying the filter and visibility checks.
     */
    @NotNull
    public static List<String> completePlayers(final @Nullable CommandSender forWho,
                                               final @NotNull String prefix,
                                               final @NotNull Collection<? extends Player> players,
                                               final @Nullable Predicate<Player> predicate) {
        String lowPrefix = prefix.toLowerCase(Locale.ENGLISH);
        List<String> list = new ArrayList<>();
        for (Player p : players) {
            if (predicate != null && !predicate.test(p)) {
                continue;
            }
            if (!p.getName().toLowerCase(Locale.ENGLISH).startsWith(lowPrefix)) {
                continue;
            }
            if (Hooks.isVanishEnabled() && !VanishApi.canSee(forWho, p)) {
                continue;
            }
            list.add(p.getName());
        }
        return list;
    }
}
