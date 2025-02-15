package emanondev.core.utility;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public interface CompletionHelper {

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
    default <T extends Enum<T>> List<String> complete(final @NotNull String prefix,
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
    default <T extends Enum<T>> List<String> complete(final @NotNull String prefix,
                                                      final @NotNull Class<T> enumClass,
                                                      final @Nullable Predicate<T> predicate) {
        return CompleteUtility.complete(prefix, enumClass, predicate);
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
    default List<String> complete(final @NotNull String prefix,
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
    default List<String> complete(final @NotNull String prefix,
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
    default List<String> complete(final @NotNull String prefix,
                                  final @Nullable Collection<String> list,
                                  final @Nullable Predicate<String> predicate) {
        return CompleteUtility.complete(prefix, list, predicate);
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
    default <T> List<String> complete(final @NotNull String prefix,
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
    default <T> List<String> complete(final @NotNull String prefix,
                                      final @Nullable Collection<T> list,
                                      final @NotNull Function<T, String> converter,
                                      final @Nullable Predicate<T> predicate) {
        return CompleteUtility.complete(prefix, list, converter, predicate);
    }

    /**
     * Retrieves a list of online player names that begin with the specified prefix.
     * The search is case-insensitive.
     *
     * @param prefix The prefix to match against player names.
     * @return A list of player names that start with the given prefix.
     */
    @NotNull
    default List<String> completePlayerNames(final @NotNull String prefix) {
        return completePlayerNames(null, prefix, Bukkit.getOnlinePlayers(), null);
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
    default List<String> completePlayerNames(final @Nullable CommandSender forWho,
                                             final @NotNull String prefix) {
        return completePlayerNames(forWho, prefix, Bukkit.getOnlinePlayers(), null);
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
    default List<String> completePlayerNames(final @Nullable CommandSender forWho,
                                             final @NotNull String prefix,
                                             final @Nullable Predicate<Player> predicate) {
        return completePlayerNames(forWho, prefix, Bukkit.getOnlinePlayers(), predicate);
    }

    /**
     * Retrieves a list of online player names that match the given prefix.
     * The search is case-insensitive. This method allows specifying a command sender,
     * which can be used for visibility checks.
     *
     * @param forWho     The command sender requesting the completion (can be null).
     * @param prefix     The prefix to match against player names.
     * @param players    The collection of players to search within.
     * @return A list of matching player names after applying the filter.
     */
    @NotNull
    default List<String> completePlayerNames(final @Nullable CommandSender forWho,
                                             final @NotNull String prefix,
                                             final @Nullable Collection<? extends Player> players) {
        if (players == null) {
            return List.of();
        }
        return completePlayerNames(forWho, prefix, players, null);
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
    default List<String> completePlayerNames(final @Nullable CommandSender forWho,
                                             final @NotNull String prefix,
                                             final @NotNull Collection<? extends Player> players,
                                             final @Nullable Predicate<Player> predicate) {
        return CompleteUtility.completePlayers(forWho, prefix, players, predicate);
    }


    /**
     * @param prefix prefix to match, case-insensitive
     * @return a list of boolean strings matching prefix
     */
    @NotNull
    default List<String> completeBoolean(@Nullable String prefix) {
        if (prefix == null || prefix.isEmpty())
            return List.of("false", "true");
        List<String> results = new ArrayList<>();
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        if ("true".startsWith(prefix))
            results.add("true");
        if ("false".startsWith(prefix))
            results.add("false");
        return results;
    }

}
