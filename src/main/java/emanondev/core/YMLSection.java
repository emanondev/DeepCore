package emanondev.core;

import emanondev.core.util.ItemUtility;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public interface YMLSection extends ConfigurationSection {

    /**
     * Returns the file path of this config.
     *
     * @return the file path starting by
     * {@link #getPlugin()}.{@link JavaPlugin#getDataFolder()
     * getDataFolder()}.
     */
    String getFileName();

    /**
     * Reload config object in RAM to that of the file.<br>
     * Lose any unsaved changes.<br>
     *
     * @return true if file existed
     */
    boolean reload();

    /**
     * Gets a set of sub keys at path.
     *
     * @param path Path of the Object
     * @return sub keys at selected path
     */
    @NotNull
    Set<String> getKeys(@NotNull String path);

    /**
     * Returns true if the config has been changed since the last change
     *
     * @return true if the config has been changed since the last change
     */
    boolean isDirty();

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default @Nullable SoundInfo loadSoundInfo(@NotNull String path, @Nullable SoundInfo def) {
        return load(path, def, SoundInfo.class);
    }

    /**
     * Gets the object from the config or set the default.<br>
     * Get the object from path, if the object is null or of different class default
     * value is returned and saved on disk else return the object
     *
     * @param <T>   Object type
     * @param path  Path of the Object
     * @param def   Default Object
     * @param clazz Object class
     * @return object or default
     */
    @SuppressWarnings("unchecked")
    @Contract("_, !null, _ -> !null")
    default <T> T load(@NotNull String path, T def, Class<T> clazz) {
        Object value = get(path);
        if (value == null) {
            if (def == null)
                return null;
            else {
                set(path, def);
                return def;
            }
        }
        if (!clazz.isInstance(value)) {
            set(path, def);
            return def;
        }
        return (T) value;
    }

    default @Nullable SoundInfo getSoundInfo(@NotNull String path) {
        return getSoundInfo(path, null);
    }

    @Contract("_, !null -> !null")
    default @Nullable SoundInfo getSoundInfo(@NotNull String path, @Nullable SoundInfo def) {
        return get(path, def, SoundInfo.class);
    }

    /**
     * Gets the object from the config or default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is returned
     *
     * @param <T>   Object type
     * @param path  Path of the Object
     * @param def   Default Object
     * @param clazz Object class
     * @return object or default
     */
    @SuppressWarnings("unchecked")
    @Contract("_, !null, _ -> !null")
    default <T> T get(@NotNull String path, T def, Class<T> clazz) {
        Object value = get(path);
        if (value == null) {
            return def;
        }
        if (!clazz.isInstance(value)) {
            return def;
        }
        return (T) value;
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default @Nullable Double loadDouble(@NotNull String path, @Nullable Double def) {
        Number val = load(path, def, Number.class);
        return val == null ? null : val.doubleValue();
    }

    /**
     * Gets the object from the config or default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default @Nullable Boolean getBoolean(@NotNull String path, @Nullable Boolean def) {
        return get(path, def, Boolean.class);
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default @Nullable Boolean loadBoolean(@NotNull String path, @Nullable Boolean def) {
        return load(path, def, Boolean.class);
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default @Nullable Integer loadInteger(@NotNull String path, @Nullable Integer def) {
        Number val = load(path, def, Number.class);
        return val == null ? null : val.intValue();
    }

    @Contract("_, !null -> !null")
    default @Nullable Integer getInteger(@NotNull String path, @Nullable Integer def) {
        Number val = get(path, def, Number.class);
        return val == null ? null : val.intValue();
    }

    @Contract("_, !null -> !null")
    default @Nullable PlayerSnapshot loadPlayerSnapshot(@NotNull String path, @Nullable PlayerSnapshot def) {
        return load(path, def, PlayerSnapshot.class);
    }

    @Contract("_, !null -> !null")
    default @Nullable PlayerSnapshot getPlayerSnapshot(@NotNull String path, @Nullable PlayerSnapshot def) {
        return get(path, def, PlayerSnapshot.class);
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default @Nullable Long loadLong(@NotNull String path, @Nullable Long def) {
        Number val = load(path, def, Number.class);
        return val == null ? null : val.longValue();
    }

    @Contract("_, !null, _, _ -> !null")
    default @Nullable String loadMessage(@NotNull String path, @Nullable String def,
                                         @Nullable CommandSender target, String... holders) {
        return loadMessage(path, def, true, target, holders);
    }

    @Contract("_, !null, _, _, _ -> !null")
    default @Nullable String loadMessage(@NotNull String path, @Nullable String def, boolean color,
                                         @Nullable CommandSender target, String... holders) {
        holdersCheck(path, holders);

        return UtilsString.fix(load(path, def, String.class), target instanceof Player ? ((Player) target) : null,
                color, holders);
    }

    private void holdersCheck(String path, String... holders) {
        if (holders.length > 0 && this.getComments(path).isEmpty()) {
            StringBuilder build = new StringBuilder("PlaceHolders: ");
            for (int i = 0; i < holders.length; i += 2)
                build.append(holders[i]).append(" ");
            this.setComments(path, List.of(build.substring(0, build.length() - 1)));
            saveAsync();
        }
    }

    /**
     * Save the config object in RAM to the file.<br>
     * Overwrites any changes that the configurator has made to the file unless
     * {@link #reload()} has been called since. Save asynchronously if possible.
     */
    void saveAsync();

    @Contract("_, !null, _, _ -> !null")
    default @Nullable String loadMessage(@NotNull String path, @Nullable List<String> def,
                                         @Nullable CommandSender target, String... holders) {
        return loadMessage(path, def, true, target, holders);
    }

    @SuppressWarnings("unchecked")
    @Contract("_, !null, _, _, _ -> !null")
    default @Nullable String loadMessage(@NotNull String path, @Nullable List<String> def, boolean color,
                                         @Nullable CommandSender target, String... holders) {
        holdersCheck(path, holders);

        try {
            return UtilsString.fix(String.join("\n", load(path, def, List.class)),
                    target instanceof Player ? ((Player) target) : null, color, holders);
        } catch (Exception e) {
            e.printStackTrace();
            return def == null ? null
                    : UtilsString.fix(String.join("\n", def), target instanceof Player ? ((Player) target) : null,
                    color, holders);
        }
    }

    @Deprecated
    default @Nullable ComponentBuilder loadMessage(@NotNull String path, @Nullable String defMessage,
                                                   @Nullable String defHover, @Nullable String defClick, @Nullable CommandSender target, String... holders) {
        return loadMessage(path, defMessage, defHover, defClick, null, true, target, holders);
    }

    @Deprecated
    default @Nullable ComponentBuilder loadMessage(@NotNull String path, @Nullable String defMessage,
                                                   @Nullable String defHover, @Nullable String defClick, @Nullable CommandSender target,
                                                   ClickEvent.Action action, String... holders) {
        return loadMessage(path, defMessage, defHover, defClick, action, true, target, holders);
    }

    @Contract("_, !null, _ -> !null")
    default @Nullable String loadMessage(@NotNull String path, @Nullable String def, String... holders) {
        return loadMessage(path, def, true, null, holders);
    }

    @Contract("_, !null, _ -> !null")
    default @Nullable String loadMessage(@NotNull String path, @Nullable List<String> def, String... holders) {
        return loadMessage(path, def, true, null, holders);
    }

    @Contract("_, !null, _, _, _ -> !null")
    default @Nullable ComponentBuilder loadComponentMessage(@NotNull String path, @Nullable String defMessage,
                                                            @Nullable String defHover, @Nullable String defClick, String... holders) {
        return loadComponentMessage(path, defMessage, defHover, defClick, null, true, null, holders);
    }

    @Contract("_, !null, _, _, _, _ -> !null")
    default @Nullable ComponentBuilder loadMessage(@NotNull String path, @Nullable String defMessage,
                                                   @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, String... holders) {
        return loadComponentMessage(path, defMessage, defHover, defClick, action, true, null, holders);
    }

    @Contract("_, !null, _, _ -> !null")
    default @Nullable String loadMessage(@NotNull String path, @Nullable String def, boolean color,
                                         String... holders) {
        return loadMessage(path, def, color, null, holders);
    }

    @Contract("_, !null, _, _ -> !null")
    default @Nullable String loadMessage(@NotNull String path, @Nullable List<String> def, boolean color,
                                         String... holders) {
        return loadMessage(path, def, color, null, holders);
    }

    @Deprecated
    default @Nullable ComponentBuilder loadMessage(@NotNull String path, @Nullable String defMessage,
                                                   @Nullable String defHover, @Nullable String defClick, boolean color, String... holders) {
        return loadComponentMessage(path, defMessage, defHover, defClick, null, color, null, holders);
    }

    @Deprecated
    default @Nullable ComponentBuilder loadMessage(@NotNull String path, @Nullable String defMessage,
                                                   @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, boolean color,
                                                   String... holders) {
        return loadComponentMessage(path, defMessage, defHover, defClick, action, color, null, holders);
    }

    @Contract("_, !null, _, _, _, _ -> !null")
    default @Nullable ComponentBuilder loadComponentMessage(@NotNull String path, @Nullable String defMessage,
                                                            @Nullable String defHover, @Nullable String defClick, boolean color, String... holders) {
        return loadComponentMessage(path, defMessage, defHover, defClick, null, color, null, holders);
    }

    @Contract("_, !null, _, _, _, _, _ -> !null")
    default @Nullable ComponentBuilder loadComponentMessage(@NotNull String path, @Nullable String defMessage,
                                                            @Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, boolean color,
                                                            String... holders) {
        return loadComponentMessage(path, defMessage, defHover, defClick, action, color, null, holders);
    }

    @Contract("_, !null, _, _ -> !null")
    default @Nullable List<String> loadMultiMessage(@NotNull String path, @Nullable List<String> def,
                                                    boolean color, String... holders) {
        return loadMultiMessage(path, def, color, null, holders);
    }

    @SuppressWarnings("unchecked")
    @Contract("_, !null, _, _, _ -> !null")
    default @Nullable List<String> loadMultiMessage(@NotNull String path, @Nullable List<String> def,
                                                    boolean color, @Nullable CommandSender target, String... holders) {
        holdersCheck(path, holders);

        try {
            return UtilsString.fix(load(path, def, List.class), target instanceof Player ? ((Player) target) : null,
                    color, holders);
        } catch (Exception e) {
            e.printStackTrace();
            return def == null ? null
                    : UtilsString.fix(def, target instanceof Player ? ((Player) target) : null, color, holders);
        }
    }

    @Contract("_, !null, _, _ -> !null")
    default @Nullable List<String> loadMultiMessage(@NotNull String path, @Nullable List<String> def,
                                                    @Nullable CommandSender target, String... holders) {
        return loadMultiMessage(path, def, true, target, holders);
    }

    @Contract("_, !null, _ -> !null")
    default @Nullable List<String> loadMultiMessage(@NotNull String path, @Nullable List<String> def,
                                                    String... holders) {
        return loadMultiMessage(path, def, true, null, holders);
    }

    @Contract("_, _, _, _ -> !null")
    default @Nullable List<String> getMultiMessage(@NotNull String path, boolean color,
                                                   @Nullable CommandSender target, String... holders) {
        return getMultiMessage(path, Collections.emptyList(), color, target, holders);
    }

    @SuppressWarnings("unchecked")
    @Contract("_, !null, _, _, _ -> !null")
    default @Nullable List<String> getMultiMessage(@NotNull String path, @Nullable List<String> def,
                                                   boolean color, @Nullable CommandSender target, String... holders) {
        holdersCheck(path, holders);

        try {
            return UtilsString.fix(get(path, def, List.class), target instanceof Player ? ((Player) target) : null,
                    color, holders);
        } catch (Exception e) {
            e.printStackTrace();
            return def == null ? null
                    : UtilsString.fix(def, target instanceof Player ? ((Player) target) : null, color, holders);
        }
    }

    @Deprecated
    default @Nullable ComponentBuilder loadMessage(@NotNull String path, @Nullable String defMessage,
                                                   @Nullable String defHover, @Nullable String defClick, boolean color, @Nullable CommandSender target,
                                                   String... holders) {
        return loadMessage(path, defMessage, defHover, defClick, null, color, target, holders);
    }

    @Deprecated
    default @Nullable ComponentBuilder loadMessage(@NotNull String path, @Nullable String defMessage,
                                                   @Nullable String defHover, @Nullable String defClick, @Nullable ClickEvent.Action action, boolean color,
                                                   @Nullable CommandSender target, String... holders) {
        holdersCheck(path, holders);

        String message = UtilsString.fix(loadString(path + ".message", defMessage),
                target instanceof Player ? ((Player) target) : null, color, holders);
        if (message == null || message.isEmpty())
            return null;

        String hover = UtilsString.fix(loadString(path + ".hover", defHover == null ? "" : defHover),
                target instanceof Player ? ((Player) target) : null, color, holders);
        String click = UtilsString.fix(loadString(path + ".click", defClick == null ? "" : defClick),
                target instanceof Player ? ((Player) target) : null, color, holders);
        action = loadEnum(path + ".click_action", action == null ? ClickEvent.Action.SUGGEST_COMMAND : action,
                ClickEvent.Action.class);
        ComponentBuilder comp = new ComponentBuilder(message);
        if (!hover.isEmpty())
            comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        if (!click.isEmpty())
            comp.event(new ClickEvent(action, click));
        return comp;
    }

    @Contract("_, !null, _, _, _, _, _ -> !null")
    default @Nullable ComponentBuilder loadComponentMessage(@NotNull String path, @Nullable String defMessage,
                                                            @Nullable String defHover, @Nullable String defClick, boolean color, @Nullable CommandSender target,
                                                            String... holders) {
        return loadComponentMessage(path, defMessage, defHover, defClick, null, color, target, holders);
    }

    @Contract("_, !null, _, _, _, _, _, _ -> !null")
    default @Nullable ComponentBuilder loadComponentMessage(@NotNull String path, @Nullable String defMessage,
                                                            @Nullable String defHover, @Nullable String defClick, @Nullable ClickEvent.Action action, boolean color,
                                                            @Nullable CommandSender target, String... holders) {
        holdersCheck(path, holders);

        String message = UtilsString.fix(loadString(path + ".message", defMessage),
                target instanceof Player ? ((Player) target) : null, color, holders);
        if (message == null || message.isEmpty())
            return new ComponentBuilder();

        String hover = UtilsString.fix(loadString(path + ".hover", defHover == null ? "" : defHover),
                target instanceof Player ? ((Player) target) : null, color, holders);
        String click = UtilsString.fix(loadString(path + ".click", defClick == null ? "" : defClick),
                target instanceof Player ? ((Player) target) : null, color, holders);
        action = loadEnum(path + ".click_action", action == null ? ClickEvent.Action.SUGGEST_COMMAND : action,
                ClickEvent.Action.class);
        ComponentBuilder comp = new ComponentBuilder(message);
        if (!hover.isEmpty())
            comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
        if (!click.isEmpty())
            comp.event(new ClickEvent(action, click));
        return comp;
    }

    /**
     * @param path       Path of the Object
     * @param defMessage Default message
     * @param defHover   Default hover message
     * @param defClick   Default click suggestion
     * @param action     Default click action type
     * @param color      Whether translate color codes or not
     * @param target     Player target for PlaceHolderAPI holders
     * @param holders    Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
     * @return null if message is null or empty
     */
    @Deprecated
    default @Nullable ComponentBuilder loadMessage(@NotNull String path, @Nullable List<String> defMessage,
                                                   @Nullable List<String> defHover, @Nullable String defClick, @Nullable ClickEvent.Action action,
                                                   boolean color, @Nullable CommandSender target, String... holders) {
        holdersCheck(path, holders);

        String message = String.join("\n", UtilsString.fix(loadStringList(path + ".message", defMessage),
                target instanceof Player ? ((Player) target) : null, color, holders));
        if (message.isEmpty())
            return null;

        String hover = String.join("\n",
                UtilsString.fix(loadStringList(path + ".hover", defHover == null ? new ArrayList<>() : defHover),
                        target instanceof Player ? ((Player) target) : null, color, holders));
        String click = UtilsString.fix(loadString(path + ".click", defClick == null ? "" : defClick),
                target instanceof Player ? ((Player) target) : null, false, holders);
        action = loadEnum(path + ".click_action", action == null ? ClickEvent.Action.SUGGEST_COMMAND : action,
                ClickEvent.Action.class);
        ComponentBuilder comp = new ComponentBuilder(message);
        if (!hover.isEmpty())
            comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
        if (!click.isEmpty())
            comp.event(new ClickEvent(action, click));
        return comp;
    }

    @Contract("_, !null, _, _, _, _, _, _ -> !null")
    default @Nullable ComponentBuilder loadComponentMessage(@NotNull String path, @Nullable List<String> defMessage,
                                                            @Nullable List<String> defHover, @Nullable String defClick, @Nullable ClickEvent.Action action,
                                                            boolean color, @Nullable CommandSender target, String... holders) {
        holdersCheck(path, holders);

        String message = String.join("\n", UtilsString.fix(loadStringList(path + ".message", defMessage),
                target instanceof Player ? ((Player) target) : null, color, holders));
        if (message.isEmpty())
            return new ComponentBuilder();

        String hover = String.join("\n",
                UtilsString.fix(loadStringList(path + ".hover", defHover == null ? new ArrayList<>() : defHover),
                        target instanceof Player ? ((Player) target) : null, color, holders));
        String click = UtilsString.fix(loadString(path + ".click", defClick == null ? "" : defClick),
                target instanceof Player ? ((Player) target) : null, false, holders);
        action = loadEnum(path + ".click_action", action == null ? ClickEvent.Action.SUGGEST_COMMAND : action,
                ClickEvent.Action.class);
        ComponentBuilder comp = new ComponentBuilder(message);
        if (!hover.isEmpty())
            comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
        if (!click.isEmpty())
            comp.event(new ClickEvent(action, click));
        return comp;
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default ItemStack loadItemStack(@NotNull String path, ItemStack def) {
        return load(path, def, ItemStack.class);
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    default @NotNull Set<String> loadStringSet(@NotNull String path, @Nullable Collection<String> def) {
        return new LinkedHashSet<>(loadStringList(path,
                def == null ? new ArrayList<>() : (def instanceof List ? (List<String>) def : new ArrayList<>(def))));
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @SuppressWarnings("unchecked")
    default @NotNull List<String> loadStringList(@NotNull String path, @Nullable Collection<String> def) {
        try {
            return load(path,
                    def == null ? new ArrayList<>() : (def instanceof List ? (List<String>) def : new ArrayList<>(def)),
                    List.class);
        } catch (Exception e) {
            e.printStackTrace();
            return def == null ? new ArrayList<>() : (def instanceof List ? (List<String>) def : new ArrayList<>(def));
        }
    }

    default @NotNull Set<String> getStringSet(@NotNull String path, @Nullable Collection<String> def) {
        return new HashSet<>(getStringList(path,
                def == null ? new ArrayList<>() : (def instanceof List ? (List<String>) def : new ArrayList<>(def))));
    }

    @SuppressWarnings("unchecked")
    default @NotNull List<String> getStringList(@NotNull String path, @Nullable Collection<String> def) {
        try {
            return get(path,
                    def == null ? new ArrayList<>() : (def instanceof List ? (List<String>) def : new ArrayList<>(def)),
                    List.class);
        } catch (Exception e) {
            e.printStackTrace();
            return def == null ? new ArrayList<>() : (def instanceof List ? (List<String>) def : new ArrayList<>(def));
        }
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is set and returned
     *
     * @param <T>  Map values type
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @SuppressWarnings("unchecked")
    @Contract("_, !null -> !null")
    default @Nullable <T> Map<String, T> loadMap(@NotNull String path, @Nullable Map<String, T> def) {
        try {
            if (!this.contains(path)) {
                set(path, def);
                save();
                return def;
            }

            Map<String, Object> subMap = ((ConfigurationSection) this.get(path)).getValues(true);
            try {
                return (Map<String, T>) subMap;
            } catch (Exception ignored) {
            }

            Map<String, T> result = new LinkedHashMap<>();
            for (String key : subMap.keySet()) {
                try {
                    result.put(key, (T) subMap.get(key));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return def;
        }
    }

    /**
     * Save the config object in RAM to the file.<br>
     * Overwrites any changes that the configurator has made to the file unless
     * {@link #reload()} has been called since.
     */
    void save();

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or not a String,
     * default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default @Nullable Sound loadSound(@NotNull String path, @Nullable Sound def) {
        String soundRaw = loadString(path, def == null ? null : def.getKey().toString());
        if (soundRaw == null)
            return null;
        String[] split = soundRaw.split(":");
        if (split.length != 2) {
            new IllegalArgumentException("Sound has wrong namespacedkey at '" + path + ":' on file "
                    + getFile().getName() + " returning default").printStackTrace();
            return def;
        }
        Sound sound = Registry.SOUNDS.get(new NamespacedKey(split[0], split[1]));
        return sound == null ? def : sound;
    }

    /**
     * assumes that enum constants are all uppercase
     *
     * @param <T>   the class of the enum
     * @param path  Path of the Object
     * @param clazz the class of the enum
     * @param def   Default Object
     * @return if path lead to a string attempts return the matching Enum value, if
     * the string is empty return def
     */
    @Contract("_, !null, _ -> !null")
    default @Nullable <T extends Enum<T>> T loadEnum(@NotNull String path, @Nullable T def,
                                                     @NotNull Class<T> clazz) {
        String value = loadString(path, def == null ? null : def.name());
        try {
            if (value == null || value.isEmpty())
                return def;
            return Enum.valueOf(clazz, value);
        } catch (IllegalArgumentException e) {
            try {
                return Enum.valueOf(clazz, value.toUpperCase());
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                new IllegalArgumentException("Value has wrong type or wrong value at '" + path + ":' on file "
                        + getFile().getName() + "; can't find value for '" + value + "' from enum '" + clazz.getName()
                        + "' using default").printStackTrace();
            }
        }
        return def;
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default @Nullable String loadString(@NotNull String path, @Nullable String def) {
        return load(path, def, String.class);
    }

    /**
     * Get the file of the config.
     *
     * @return the file associated to the config
     */
    @NotNull
    File getFile();

    @Contract("_, !null -> !null")
    default @Nullable Sound getSound(@NotNull String path, @Nullable Sound def) {
        String soundRaw = getString(path, def == null ? null : def.getKey().toString());
        if (soundRaw == null)
            return null;
        String[] split = soundRaw.split(":");
        if (split.length != 2) {
            new IllegalArgumentException("Sound has wrong namespacedkey at '" + path + ":' on file "
                    + getFile().getName() + " returning default").printStackTrace();
            return def;
        }
        Sound sound = Registry.SOUNDS.get(new NamespacedKey(split[0], split[1]));
        return sound == null ? def : sound;
    }

    @Contract("_, !null, _ -> !null")
    default @Nullable <T extends Enum<T>> T getEnum(@NotNull String path, @Nullable T def,
                                                    @NotNull Class<T> clazz) {
        String value = getString(path, def == null ? null : def.name());
        try {
            if (value == null || value.isEmpty())
                return def;
            return Enum.valueOf(clazz, value);
        } catch (IllegalArgumentException e) {
            try {
                return Enum.valueOf(clazz, value.toUpperCase());
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                new IllegalArgumentException("Value has wrong type or wrong value at '" + path + ":' on file "
                        + getFile().getName() + "; can't find value for '" + value + "' from enum '" + clazz.getName()
                        + "' using default").printStackTrace();
            }
        }
        return def;
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or not a String,
     * default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default @Nullable Material loadMaterial(@NotNull String path, @Nullable Material def) {
        return loadEnum(path, def, Material.class);
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or not a String,
     * default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default @Nullable Material getMaterial(@NotNull String path, @Nullable Material def) {
        return getEnum(path, def, Material.class);
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or not a String
     * List, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    default @NotNull List<Material> loadMaterialList(@NotNull String path, @Nullable Collection<Material> def) {
        return loadEnumList(path, def, Material.class);
    }

    /**
     * assumes that enum constants are all uppercase null enum values contained in
     * def might be lost
     *
     * @param <T>   the class of the enum
     * @param path  Path of the Object
     * @param clazz the class of the enum
     * @param def   Default Object
     * @return the value found or default if none
     */
    default @NotNull <T extends Enum<T>> List<T> loadEnumList(@NotNull String path, @Nullable Collection<T> def,
                                                              @NotNull Class<T> clazz) {
        ArrayList<T> destination = new ArrayList<>();
        List<String> from;
        if (def == null)
            from = null;
        else {
            from = new ArrayList<>();
            for (T enumValue : def)
                from.add(enumValue.name());
        }
        from = loadStringList(path, from);
        if (from.isEmpty())
            return destination;
        for (String value : from) {
            T val;
            try {
                if (value == null || value.isEmpty())
                    val = null;
                else
                    val = Enum.valueOf(clazz, value);
            } catch (IllegalArgumentException e) {
                try {
                    val = Enum.valueOf(clazz, value.toUpperCase());
                } catch (IllegalArgumentException e2) {
                    e2.printStackTrace();
                    new IllegalArgumentException("Value has wrong type or wrong value at '" + path + ":' on file "
                            + getFile().getName() + "; can't find value for '" + value + "' from enum '"
                            + clazz.getName() + "' skipping it").printStackTrace();
                    val = null;
                }
            }
            if (val != null)
                destination.add(val);
        }
        return destination;
    }

    default @NotNull List<Material> getMaterialList(@NotNull String path, @Nullable Collection<Material> def) {
        return getEnumList(path, def, Material.class);
    }

    /**
     * assumes that enum constants are all uppercase null enum values contained in
     * def might be lost
     *
     * @param <T>   the class of the enum
     * @param path  Path of the Object
     * @param clazz the class of the enum
     * @param def   Default Object
     * @return the value found or default if none
     */
    default @NotNull <T extends Enum<T>> List<T> getEnumList(@NotNull String path, @Nullable Collection<T> def,
                                                             @NotNull Class<T> clazz) {
        ArrayList<T> destination = new ArrayList<>();
        List<String> from;
        if (def == null)
            from = null;
        else {
            from = new ArrayList<>();
            for (T enumValue : def)
                from.add(enumValue.name());
        }
        from = getStringList(path, from);
        if (from.isEmpty())
            return destination;
        for (String value : from) {
            T val;
            try {
                if (value == null || value.isEmpty())
                    val = null;
                else
                    val = Enum.valueOf(clazz, value);
            } catch (IllegalArgumentException e) {
                try {
                    val = Enum.valueOf(clazz, value.toUpperCase());
                } catch (IllegalArgumentException e2) {
                    e2.printStackTrace();
                    new IllegalArgumentException("Value has wrong type or wrong value at '" + path + ":' on file "
                            + getFile().getName() + "; can't find value for '" + value + "' from enum '"
                            + clazz.getName() + "' skipping it").printStackTrace();
                    val = null;
                }
            }
            if (val != null)
                destination.add(val);
        }
        return destination;
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or not a String
     * List, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    default @NotNull EnumSet<Material> loadMaterialSet(@NotNull String path,
                                                       @Nullable Collection<Material> def) {
        return loadEnumSet(path, def, Material.class);
    }

    default @NotNull <T extends Enum<T>> EnumSet<T> loadEnumSet(@NotNull String path,
                                                                @Nullable Collection<T> def, @NotNull Class<T> clazz) {
        EnumSet<T> destination = EnumSet.noneOf(clazz);
        List<String> from;
        if (def == null)
            from = null;
        else {
            from = new ArrayList<>();
            for (T enumValue : def)
                from.add(enumValue.name());
        }
        from = loadStringList(path, from);
        if (from.isEmpty())
            return destination;
        for (String value : from) {
            T val;
            try {
                if (value == null || value.isEmpty())
                    val = null;
                else
                    val = Enum.valueOf(clazz, value);
            } catch (IllegalArgumentException e) {
                try {
                    val = Enum.valueOf(clazz, value.toUpperCase());
                } catch (IllegalArgumentException e2) {
                    e2.printStackTrace();
                    new IllegalArgumentException("Value has wrong type or wrong value at '" + path + ":' on file "
                            + getFile().getName() + "; can't find value for '" + value + "' from enum '"
                            + clazz.getName() + "' skipping it").printStackTrace();
                    val = null;
                }
            }
            if (val != null)
                destination.add(val);
        }
        return destination;
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or not a String,
     * default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default @Nullable EntityType loadEntityType(@NotNull String path, @Nullable EntityType def) {
        return loadEnum(path, def, EntityType.class);
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or not a String,
     * default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default @Nullable EntityType getEntityType(@NotNull String path, @Nullable EntityType def) {
        return getEnum(path, def, EntityType.class);
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or not a String
     * List, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    default @NotNull List<EntityType> loadEntityTypeList(@NotNull String path,
                                                         @Nullable Collection<EntityType> def) {
        return loadEnumList(path, def, EntityType.class);
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or not a String
     * List, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    default @NotNull EnumSet<EntityType> loadEntityTypeSet(@NotNull String path,
                                                           @Nullable Collection<EntityType> def) {
        return loadEnumSet(path, def, EntityType.class);
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or not a String
     * List, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    default @NotNull ItemFlag[] loadItemFlags(@NotNull String path, ItemFlag[] def) {
        return loadEnumSet(path, def == null ? null : Arrays.asList(def), ItemFlag.class).toArray(new ItemFlag[0]);
    }

    default @Nullable BoundingBox getBoundingBox(@NotNull String path) {
        return getBoundingBox(path, null);
    }

    @Contract("_, !null -> !null")
    default BoundingBox getBoundingBox(@NotNull String path, BoundingBox def) {
        return get(path, def, BoundingBox.class).clone();
    }

    /**
     * Gets the object from the config or set default.<br>
     * Get the object from path, if object at selected path is null or of another
     * class, default value is set and returned
     *
     * @param path Path of the Object
     * @param def  Default Object
     * @return object or default
     */
    @Contract("_, !null -> !null")
    default BoundingBox loadBoundingBox(@NotNull String path, BoundingBox def) {
        return load(path, def, BoundingBox.class).clone();
    }

    /**
     * Gets the parent YMLSection that directly contains this
     * YMLSection.
     * <p>
     * For any Configuration themselves, this will return null.
     * <p>
     * If the section is no longer contained within its parent for any reason, such
     * as being replaced with a different value, this may return null.
     *
     * @return Parent section containing this section.
     */
    @Nullable
    YMLSection getParent();

    /**
     * internal use only
     *
     * @param path Path of the Object
     * @return Object at specified path
     */
    @Deprecated
    Object get(@NotNull String path);

    /**
     * Sets specified Object on selected path
     */
    void set(@NotNull String path, @Nullable Object value);

    /**
     * Creates a ConfigurationSection at the specified path, with specified values.
     * <p>
     * Any value that was previously set at this path will be overwritten. If the
     * previous value was itself a ConfigurationSection, it will be orphaned.
     *
     * @param path Path of the Object
     * @return Newly created section
     * @see #loadSection(String, Map)
     */
    @Deprecated
    @NotNull
    YMLSection createSection(@NotNull String path);

    /**
     * Creates a ConfigurationSection at the specified path, with specified values.
     * <p>
     * Any value that was previously set at this path will be overwritten. If the
     * previous value was itself a ConfigurationSection, it will be orphaned.
     *
     * @param path Path of the Object
     * @param map  The values to used.
     * @return Newly created section
     * @see #loadSection(String, Map)
     */
    @Deprecated
    @NotNull
    YMLSection createSection(@NotNull String path, @NotNull Map<?, ?> map);

    @Contract("_, !null -> !null")
    default @Nullable String getString(@NotNull String path, @Nullable String def) {
        return get(path, def, String.class);
    }

    /**
     * Gets the requested ConfigurationSection by path.
     * <p>
     * If the ConfigurationSection does not exist but a default value has been
     * specified, this will return the default value. If the ConfigurationSection
     * does not exist and no default value was specified, this will return null.
     *
     * @param path Path of the Object
     * @return Requested ConfigurationSection.
     * @see #loadSection(String, Map)
     */
    @Deprecated
    YMLSection getConfigurationSection(@NotNull String path);

    /**
     * Gets the equivalent YMLSection from the default Configuration
     * defined in getRoot().
     * <p>
     * If the root contains no defaults, or the defaults doesn't contain a value for
     * this path, or the value at this path is not a YMLSection then this
     * will return null.
     *
     * @return Equivalent section in root configuration
     */
    YMLSection getDefaultSection();

    /**
     * @param path Path of the Object
     * @return YMLSection at specified path or a new generated section
     */
    @NotNull
    default YMLSection loadSection(@NotNull String path) {
        return loadSection(path, null);
    }

    @NotNull
    default YMLSection loadSection(@NotNull String path, Map<String, Object> def) {
        if (this.isConfigurationSection(path))
            return getConfigurationSection(path);
        return def == null ? createSection(path) : createSection(path, def);
    }

    void setNoDirty(@NotNull String path, Object value);

    default <T extends Enum<T>> void setEnumsAsStringList(@NotNull String path, Collection<T> enumValues) {
        if (enumValues == null) {
            set(path, null);
            return;
        }
        List<String> list = new ArrayList<>();
        for (Enum<T> value : enumValues)
            list.add(value == null ? null : value.name());
        set(path, list);
    }

    default <T extends Enum<T>> void setEnumAsString(@NotNull String path, T enumValue) {
        set(path, enumValue == null ? null : enumValue.name());
    }

    @NotNull
    default List<Integer> getIntegerList(@NotNull String path, List<Integer> def) {
        List<Integer> val = getIntegerList(path);
        if (val.isEmpty() && def != null)
            return def;
        return val;
    }


    @Contract("_,!null->!null")
    @Nullable
    default ItemBuilder getGuiItem(@NotNull String path, @Nullable ItemStack def) {
        if (this.get(path) instanceof ItemStack) { //backward compability
            return new ItemBuilder(getItemStack(path, def));
        }
        YMLSection section = this.loadSection(path);
        ItemBuilder b = ItemUtility.convertItem(section);
        if (b != null)
            return b;

        ItemMeta meta = def == null ? null : def.getItemMeta();
        return new ItemBuilder(section.getMaterial("material", def == null ? Material.STONE : def.getType()))
                .hideAllFlags().addEnchantment(Enchantment.LURE, section.getBoolean("glow",
                        def != null && def.getEnchantments().size() > 0) ? 1 : 0)
                .setAmount(section.getInteger("amount", def == null ? 1 : def.getAmount()))
                .setUnbreakable(section.getBoolean("unbreakable", def != null && meta.isUnbreakable()))
                .setCustomModelData(section.getInteger("customModelData",
                        meta.hasCustomModelData() ? meta.getCustomModelData() : null))
                .setDamage(section.getInteger("damage", def == null ? 0 : ((meta instanceof Damageable) ? ((Damageable) meta).getDamage() : 0)));
    }

    @Contract("_,!null->!null")
    @Nullable
    default ItemBuilder getGuiItem(@NotNull String path, @Nullable ItemBuilder def) {
        return getGuiItem(path, def == null ? null : def.build());
    }

    @Contract("_,!null->!null")
    @Nullable
    default ItemBuilder loadGuiItem(@NotNull String path, @Nullable ItemStack def) {
        if (this.get(path) instanceof ItemStack) { //backward compability
            return new ItemBuilder(getItemStack(path, def));
        }
        YMLSection section = this.loadSection(path);
        ItemBuilder b = ItemUtility.convertItem(section);
        if (b != null)
            return b;

        ItemMeta meta = def == null ? null : def.getItemMeta();
        b = new ItemBuilder(section.loadMaterial("material", def == null ? Material.STONE : def.getType())).hideAllFlags();
        b.glow(section.getBoolean("glow", null));
        if (def == null || def.getAmount() == 1)
            b.setAmount(section.getInteger("amount", 1));
        else
            b.setAmount(section.loadInteger("amount", 1));
        if (meta != null && meta.isUnbreakable())
            b.setUnbreakable(section.loadBoolean("unbreakable", true));
        else
            b.setUnbreakable(section.getBoolean("unbreakable", false));
        if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() != 0)
            b.setCustomModelData(section.loadInteger("customModelData", meta.getCustomModelData()));
        else
            b.setCustomModelData(section.getInteger("customModelData", null));
        if (meta instanceof Damageable d && d.getDamage() != 0)
            b.setCustomModelData(section.loadInteger("damage", d.getDamage()));
        else
            b.setCustomModelData(section.getInteger("damage", 0));
        return b;
    }


    @Contract("_,!null->!null")
    @Nullable
    default ItemBuilder loadGuiItem(@NotNull String path, @Nullable ItemBuilder def) {
        return loadGuiItem(path, def == null ? null : def.build());
    }

    /**
     * Paths are material glow amount unbreakable customModelData damage
     *
     * @param path        path to item config
     * @param defMaterial default material
     * @return gui item
     */
    @NotNull
    default ItemBuilder loadGuiItem(@NotNull String path, @NotNull Material defMaterial) {
        return loadGuiItem(path, defMaterial, false, 1, false);
    }

    /**
     * Paths are material glow amount unbreakable customModelData damage
     *
     * @param path        path to item config
     * @param defMaterial default material
     * @param defGlow     default glow value
     * @return gui item
     */
    @NotNull
    default ItemBuilder loadGuiItem(@NotNull String path, @NotNull Material defMaterial, boolean defGlow) {
        return loadGuiItem(path, defMaterial, defGlow, 1, false);
    }

    /**
     * Paths are material glow amount unbreakable customModelData damage
     *
     * @param path        path to item config
     * @param defMaterial default material
     * @param defGlow     default glow value
     * @param defAmount   default amount
     * @return gui item
     */
    @NotNull
    default ItemBuilder loadGuiItem(@NotNull String path, @NotNull Material defMaterial, boolean defGlow, int defAmount) {
        return loadGuiItem(path, defMaterial, defGlow, defAmount, false);
    }

    /**
     * Paths are material glow amount unbreakable customModelData damage
     *
     * @param path           path to item config
     * @param defMaterial    default material
     * @param defGlow        default glow value
     * @param defAmount      default amount
     * @param defUnbreakable default unbreakable value
     * @return gui item
     */
    @NotNull
    default ItemBuilder loadGuiItem(@NotNull String path, @NotNull Material defMaterial, boolean defGlow, int defAmount, boolean defUnbreakable) {
        if (this.get(path) instanceof ItemStack)
            return new ItemBuilder(loadItemStack(path, new ItemStack(Material.STONE)));
        YMLSection section = this.loadSection(path);
        return new ItemBuilder(section.loadMaterial("material", defMaterial))
                .hideAllFlags().glow(section.getBoolean("glow", defGlow))
                .setAmount(section.getInteger("amount", defAmount)).setUnbreakable(section.getBoolean("unbreakable", defUnbreakable))
                .setCustomModelData(section.getInteger("customModelData", 0))
                .setDamage(section.getInteger("damage", 0));
    }

    /**
     * Returns target object or default.<br>
     * This method works like {@link #getInt(String)} but also keep tracks of null objects:
     * notify console and leave track on a file if object is null.
     * This method helps the developer to find and fix not generated default configurations, without hardcoding defaults.
     * WARNINGS: do not use this method for objects which may be null nor for objects not supposed to be on default configurations.
     *
     * @param path path to object
     * @return target object or default, also if object is null tracks it on file and console
     */
    default int getTrackInt(@NotNull String path) {
        return getTrack(path, 0, Integer.class);
    }

    /**
     * Returns target object or default.<br>
     * This method works like {@link #get(String, Object, Class)} but also keep tracks of null objects:
     * notify console and leave track on a file if object is null.
     * This method helps the developer to find and fix not generated default configurations, without hardcoding defaults.
     * WARNINGS: do not use this method for objects which may be null nor for objects not supposed to be on default configurations.
     *
     * @param path  path to object
     * @param def   default value
     * @param clazz class type
     * @param <T>   class type
     * @return target object or default, also if object is null tracks it on file and console
     */
    default <T> @NotNull T getTrack(@NotNull String path, @NotNull T def, @NotNull Class<T> clazz) {
        T val = this.get(path, null, clazz);
        if (val == null) {
            if (this.getPlugin() instanceof CorePlugin plugin) {
                plugin.logProblem("value undefined at " + getFile().getPath() + " " +
                        (this.getCurrentPath() == null || this.getCurrentPath().isEmpty() ? "" : this.getCurrentPath() + ".") + path);
                plugin.logOnFile("undefined_configs", getFile().getPath() + " " + (this.getCurrentPath() == null || this.getCurrentPath().isEmpty() ? "" : this.getCurrentPath() + ".") + path);
            }
        }
        return val == null ? def : val;
    }

    /**
     * Return the plugin associated with this Config.
     *
     * @return the plugin associated with this Config
     */
    JavaPlugin getPlugin();

    /**
     * Returns target object or default.<br>
     * This method works like {@link #getDouble(String)} (String)} but also keep tracks of null objects:
     * notify console and leave track on a file if object is null.
     * This method helps the developer to find and fix not generated default configurations, without hardcoding defaults.
     * WARNINGS: do not use this method for objects which may be null nor for objects not supposed to be on default configurations.
     *
     * @param path path to object
     * @return target object or default, also if object is null tracks it on file and console
     */
    default double getTrackDouble(@NotNull String path) {
        return getTrack(path, 0D, Double.class);
    }

    /**
     * Returns target object or default.<br>
     * This method works like {@link #getLong(String)} but also keep tracks of null objects:
     * notify console and leave track on a file if object is null.
     * This method helps the developer to find and fix not generated default configurations, without hardcoding defaults.
     * WARNINGS: do not use this method for objects which may be null nor for objects not supposed to be on default configurations.
     *
     * @param path path to object
     * @return target object or default, also if object is null tracks it on file and console
     */
    default long getTrackLong(@NotNull String path) {
        return getTrack(path, 0L, Long.class);
    }

    /**
     * Returns target object or default.<br>
     * This method works like {@link #getSoundInfo(String)} but also keep tracks of null objects:
     * notify console and leave track on a file if object is null.
     * This method helps the developer to find and fix not generated default configurations, without hardcoding defaults.
     * WARNINGS: do not use this method for objects which may be null nor for objects not supposed to be on default configurations.
     *
     * @param path path to object
     * @return target object or default, also if object is null tracks it on file and console
     */
    default @NotNull SoundInfo getTrackSoundInfo(@NotNull String path) {
        return getTrack(path, SoundInfo.getSelfExperiencePickup(), SoundInfo.class);
    }

    default @NotNull String getTrackMessage(@NotNull String path, @Nullable CommandSender sender, String... holders) {
        holdersCheck(path, holders);
        return UtilsString.fix(getTrackString(path), sender instanceof Player ? (Player) sender : null, true, holders);
    }

    /**
     * Returns target object or default.<br>
     * This method works like {@link #getString(String)} but also keep tracks of null objects:
     * notify console and leave track on a file if object is null.
     * This method helps the developer to find and fix not generated default configurations, without hardcoding defaults.
     * WARNINGS: do not use this method for objects which may be null nor for objects not supposed to be on default configurations.
     *
     * @param path path to object
     * @return target object or default, also if object is null tracks it on file and console
     */
    default @NotNull String getTrackString(@NotNull String path) {
        return getTrack(path, "", String.class);
    }

    default @NotNull List<String> getTrackMultiMessage(@NotNull String path, @Nullable CommandSender sender, String... holders) {
        holdersCheck(path, holders);
        return UtilsString.fix(getTrackStringList(path), sender instanceof Player ? (Player) sender : null, true, holders);
    }

    /**
     * Returns target object or default.<br>
     * This method works like {@link #getStringList(String)} but also keep tracks of null objects:
     * notify console and leave track on a file if object is null.
     * This method helps the developer to find and fix not generated default configurations, without hardcoding defaults.
     * WARNINGS: do not use this method for objects which may be null nor for objects not supposed to be on default configurations.
     *
     * @param path path to object
     * @return target object or default, also if object is null tracks it on file and console
     */
    default @NotNull List<String> getTrackStringList(@NotNull String path) {
        return getTrack(path, List.of(path), List.class);
    }

    /**
     * Returns target object or default.<br>
     * This method works like {@link #getMaterial(String, Material)} but also keep tracks of null objects:
     * notify console and leave track on a file if object is null.
     * This method helps the developer to find and fix not generated default configurations, without hardcoding defaults.
     * WARNINGS: do not use this method for objects which may be null nor for objects not supposed to be on default configurations.
     *
     * @param path path to object
     * @return target object or default, also if object is null tracks it on file and console
     */
    default @NotNull Material getTrackMaterial(@NotNull String path) {
        return getTrackEnum(path, Material.STONE, Material.class);
    }

    /**
     * Returns target object or default.<p>
     * This method works like {@link #getEnum(String, Enum, Class) getEnum(String, Enum, EnumClass)} but also keep tracks of null objects:
     * notify console and leave track on a file if object is null.
     * This method helps the developer to find and fix not generated default configurations, without hardcoding defaults.
     * WARNINGS: do not use this method for objects which may be null nor for objects not supposed to be on default configurations.
     *
     * @param path path to object
     * @return target object or default, also if object is null tracks it on file and console
     */
    default @NotNull <T extends Enum<T>> T getTrackEnum(@NotNull String path, @NotNull T def, @NotNull Class<T> clazz) {
        String val = getTrackString(path);
        try {
            return Enum.valueOf(clazz, val);
        } catch (Exception e) {
            try {
                return Enum.valueOf(clazz, val.toUpperCase());
            } catch (Exception ignored) {
                if (this.getPlugin() instanceof CorePlugin plugin) {
                    plugin.logProblem("value invalid at " + getFile().getPath() + " " +
                            (this.getCurrentPath() == null || this.getCurrentPath().isEmpty() ? "" : this.getCurrentPath() + ".") + path);
                    plugin.logOnFile("undefined_configs", getFile().getPath() + " " + (this.getCurrentPath() == null || this.getCurrentPath().isEmpty() ? "" : this.getCurrentPath() + ".") + path + " (invalid enum value)");
                }
            }
        }
        return def;
    }

    /**
     * Returns target object or default.<p>
     * This method works like {@link #getEntityType(String, EntityType)} but also keep tracks of null objects:
     * notify console and leave track on a file if object is null.
     * This method helps the developer to find and fix not generated default configurations, without hardcoding defaults.
     * WARNINGS: do not use this method for objects which may be null nor for objects not supposed to be on default configurations.
     *
     * @param path path to object
     * @return target object or default, also if object is null tracks it on file and console
     */
    default @NotNull EntityType getTrackEntityType(@NotNull String path) {
        return getTrackEnum(path, EntityType.SHEEP, EntityType.class);
    }

    default String getMessage(@NotNull String path, @Nullable CommandSender target, String... holders) {
        return getMessage(path, null, true, target, holders);
    }

    default String getMessage(
            @NotNull String path, @Nullable String def, boolean color,
            @Nullable CommandSender target, String... holders) {
        holdersCheck(path, holders);
        try {
            return UtilsString.fix(get(path, def, String.class),
                    target instanceof Player ? ((Player) target) : null, color, holders);
        } catch (Exception e) {
            e.printStackTrace();
            return def == null ? null
                    : UtilsString.fix(String.join("\n", def), target instanceof Player ? ((Player) target) : null,
                    color, holders);
        }
    }

    default String getMessage(
            @NotNull String path, @Nullable String def,
            @Nullable CommandSender target, String... holders) {
        return getMessage(path, def, true, target, holders);
    }

    default String getMessage(@NotNull String path, boolean color, @Nullable CommandSender target, String... holders) {
        return getMessage(path, null, color, target, holders);
    }
}
