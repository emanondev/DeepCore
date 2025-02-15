package emanondev.core.util;

import emanondev.core.CorePlugin;
import emanondev.core.utility.ConsoleHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class DRegistry<T extends DRegistryElement> implements Iterable<T>, ConsoleHelper {

    private final String name;
    private final LinkedHashMap<String, T> types = new LinkedHashMap<>();
    private final boolean doLog;
    private final CorePlugin plugin;

    /**
     * @throws IllegalArgumentException if name doesn't match <code>[a-zA-Z][_a-zA-Z0-9]*</code>
     */
    public DRegistry(@NotNull CorePlugin plugin, @NotNull String name, boolean doLog) {
        if (!Pattern.compile("[a-zA-Z][_a-zA-Z0-9]+").matcher(name).matches())
            throw new IllegalArgumentException();
        this.name = name;
        this.plugin = plugin;
        this.doLog = doLog;
    }

    public @NotNull String getRegistryId() {
        return name;
    }

    public @NotNull CorePlugin getPlugin() {
        return plugin;
    }

    /**
     * @return true if registry log on console relevant events
     */
    public boolean isLogging() {
        return doLog;
    }

    /**
     * @throws IllegalArgumentException if an element with the same id is already contained in the registry
     */
    public void register(@NotNull T element) {
        String id = element.getId();
        if (types.containsKey(id))
            throw new IllegalArgumentException();
        types.put(id, element);
        element.onRegistered(this);
        if (doLog) log("Registered &e" + id);
    }

    /**
     * @return true if element was unregistered
     */
    public boolean unregister(@NotNull T element) {
        return unregister(element.getId());
    }

    /**
     * @return true if element was unregistered
     */
    public boolean unregister(@NotNull String id) {
        T val = types.remove(id);
        if (val == null)
            return false;
        val.onUnregistered(this);
        if (doLog) log("Unregistered &e" + id);
        return true;
    }

    public void load() {
    }

    public void reload() {
        types.values().forEach(T::reload);
    }

    public @Nullable T get(@NotNull String id) {
        return types.get(id.toLowerCase(Locale.ENGLISH));
    }

    public @NotNull Collection<String> getIds() {
        return Collections.unmodifiableSet(types.keySet());
    }

    public @NotNull Set<T> getAll(Predicate<T> filter) {
        LinkedHashSet<T> set = new LinkedHashSet<>();
        for (T t : types.values())
            if (filter.test(t))
                set.add(t);
        return set;
    }

    public @NotNull Collection<T> getAll() {
        return Collections.unmodifiableCollection(types.values());
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return types.values().iterator();
    }

    /**
     * Performs the given action for each entry in this map until all entries have been processed or the action throws an exception. Unless otherwise specified by the implementing class, actions are performed in the order of entry set iteration (if an iteration order is specified.) Exceptions thrown by the action are relayed to the caller.
     *
     * @param action The action to be performed for each entry
     * @throws NullPointerException            if the specified action is null
     * @throws ConcurrentModificationException if an entry is found to be removed during iteration
     */
    public void forEach(BiConsumer<String, T> action) {
        types.forEach(action);
    }

    @Override
    public void log(String log) {
        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&',
                        ChatColor.DARK_BLUE + "[" + ChatColor.WHITE + plugin.getName() + ChatColor.DARK_BLUE + "|"
                                + ChatColor.WHITE + name + ChatColor.DARK_BLUE + "] " + ChatColor.WHITE + log));
    }
}
