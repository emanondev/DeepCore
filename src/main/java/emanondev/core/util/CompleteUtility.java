package emanondev.core.util;

import de.myzelyam.api.vanish.VanishAPI;
import de.myzelyam.supervanish.SuperVanish;
import emanondev.core.Hooks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public interface CompleteUtility {

    /**
     * @param sender sender
     * @param prefix prefix to match, case-insensitive
     * @return a list of player names from online players for the given prefix
     * filtering visible players if vanishAPI is active
     */
    default @NotNull List<String> completePlayerNames(@Nullable CommandSender sender, @Nullable String prefix) {
        return completePlayerNames(sender, prefix, Bukkit.getOnlinePlayers());
    }

    /**
     * @param sender  the sender
     * @param prefix  prefix to match, case-insensitive
     * @param players which player names are considered?
     * @return a list of player names from the given collection for the given prefix
     * filtering visible players if vanishAPI is active
     */
    default @NotNull List<String> completePlayerNames(@Nullable CommandSender sender, @Nullable String prefix,
                                                      @Nullable Collection<? extends Player> players) {
        if (players == null)
            return Collections.emptyList();
        if (prefix != null)
            prefix = prefix.toLowerCase(Locale.ENGLISH);
        else
            prefix = "";
        List<String> list = new ArrayList<>();

        if (Hooks.isVanishEnabled() && (sender instanceof Player)) {
            for (Player p : players)
                try {
                    if (p.getName().toLowerCase(Locale.ENGLISH).startsWith(prefix) && VanishAPI.canSee((Player) sender, p))
                        list.add(p.getName());
                } catch (Exception e) {//HOTFIX
                    e.printStackTrace();
                    boolean vanished = false;
                    for (MetadataValue meta : p.getMetadata("vanished")) {
                        if (meta.asBoolean())
                            vanished = true;
                    }
                    if (!vanished)
                        list.add(p.getName());
                }
        } else
            for (Player p : players)
                if (p.getName().toLowerCase(Locale.ENGLISH).startsWith(prefix))
                    list.add(p.getName());
        return list;
    }

    /**
     * @param prefix prefix to match, case-insensitive
     * @param values values to match
     * @param eval   how to evaluate values as strings
     * @return list of strings matching prefix
     */
    default @NotNull <K> List<String> complete(@Nullable String prefix, @Nullable Collection<K> values,
                                               @NotNull Function<K, String> eval) {
        return complete(prefix, values, eval, null);
    }

    /**
     * @param prefix  prefix to match, case-insensitive
     * @param values  values to match
     * @param eval    how to evaluate values as strings
     * @param isValid filter on values
     * @return list of strings matching prefix
     */
    default @NotNull <K> List<String> complete(@Nullable String prefix, @Nullable Collection<K> values,
                                               @NotNull Function<K, String> eval, @Nullable Predicate<K> isValid) {
        if (values == null)
            return Collections.emptyList();
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
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        for (K val : values)
            try {
                if (isValid == null || isValid.test(val)) {
                    String value = eval.apply(val);
                    if (value != null && value.toLowerCase(Locale.ENGLISH).startsWith(prefix))
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
    default @NotNull <K extends Enum<K>> List<String> complete(@Nullable String prefix, @NotNull Class<K> type) {
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
    default @NotNull <K extends Enum<K>> List<String> complete(@Nullable String prefix, @NotNull Class<K> type,
                                                               @NotNull Predicate<K> predicate) {
        List<String> results = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) {
            for (K e : type.getEnumConstants())
                if (predicate.test(e))
                    results.add(e.name().toLowerCase(Locale.ENGLISH));
            return results;
        }
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        for (K e : type.getEnumConstants())
            if (e.name().toLowerCase(Locale.ENGLISH).startsWith(prefix))
                if (predicate.test(e))
                    results.add(e.name().toLowerCase(Locale.ENGLISH));
        return results;
    }

    /**
     * @param prefix   prefix to match, case-insensitive
     * @param elements elements to match
     * @return a list of strings matching prefix
     */
    default @NotNull List<String> complete(@Nullable String prefix, @Nullable Collection<String> elements) {
        if (elements == null)
            return Collections.emptyList();
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
     * @param prefix    prefix to match, case-insensitive
     * @param elements  elements to match
     * @param predicate function to choose if elements should be selected or discarded
     * @return a list of strings matching prefix
     */
    default @NotNull List<String> complete(@Nullable String prefix, @Nullable Collection<String> elements,
                                           @NotNull Predicate<String> predicate) {
        if (elements == null)
            return Collections.emptyList();
        List<String> results = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) {
            for (String e : elements)
                if (predicate.test(e))
                    results.add(e);
            return results;
        }
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        for (String e : elements)
            if (e.toLowerCase(Locale.ENGLISH).startsWith(prefix) && predicate.test(e))
                results.add(e);
        return results;
    }

    /**
     * @param prefix   prefix to match, case-insensitive
     * @param elements elements to match
     * @return a list of strings matching prefix
     */
    default @NotNull List<String> complete(@Nullable String prefix, @Nullable String[] elements) {
        if (elements == null)
            return new ArrayList<>();
        if (prefix == null || prefix.isEmpty())
            return Arrays.asList(elements);
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        List<String> results = new ArrayList<>();
        for (String e : elements)
            if (e != null && e.toLowerCase(Locale.ENGLISH).startsWith(prefix))
                results.add(e);
        return results;
    }

    /**
     * @param prefix prefix to match, case-insensitive
     * @return a list of boolean strings matching prefix
     */
    default @NotNull List<String> completeBoolean(@Nullable String prefix) {
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
