package emanondev.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public interface YMLSection extends ConfigurationSection {

	/**
	 * Returns the file path of this config.
	 * 
	 * @return the file path starting by
	 *         {@link #getPlugin()}.{@link JavaPlugin#getDataFolder()
	 *         getDataFolder()}.
	 */
	String getFileName();

	/**
	 * Return the plugin associated with this Config.
	 * 
	 * @return the plugin associated with this Config
	 */
	JavaPlugin getPlugin();

	/**
	 * Reload config object in RAM to that of the file.<br>
	 * Lose any unsaved changes.<br>
	 * 
	 * @return true if file existed
	 */
	boolean reload();

	/**
	 * Save the config object in RAM to the file.<br>
	 * Overwrites any changes that the configurator has made to the file unless
	 * {@link #reload()} has been called since.
	 */
	void save();

	/**
	 * Save the config object in RAM to the file.<br>
	 * Overwrites any changes that the configurator has made to the file unless
	 * {@link #reload()} has been called since. Save Asynchronusly if possible.
	 */
	void saveAsync();

	/**
	 * Get the file of the config.
	 * 
	 * @return the file associated to the config
	 */
	@NotNull File getFile();

	/**
	 * Gets a set of sub keys at path.
	 * 
	 * @param path Path of the Object
	 * @return sub keys at selected path
	 */
	@NotNull Set<String> getKeys(@NotNull String path);

	/**
	 * Gets the object from the config or set the default.<br>
	 * Get the object from path, if the object is null or of different class default
	 * value is returned and saved on disk else return the object
	 * 
	 * @param <T> Object type
	 * @param path Path of the Object
	 * @param def Default Object
	 * @param clazz Object class
	 * @return object or default
	 */
	@SuppressWarnings("unchecked")
	default <T> T load(String path, T def, Class<T> clazz) {
		Object value = get(path);
		if (value == null) {
			if (def == null)
				return null;
			else {
				set(path, def);
				// saveAsync();
				return def;
			}
		}
		if (!clazz.isInstance(value)) {
			set(path, def);
			// saveAsync();
			return def;
		}
		return (T) value;
	}

	/**
	 * Returns true if the config has been changed since the last change
	 * 
	 * @return true if the config has been changed since the last change
	 */
	boolean isDirty();

	/**
	 * Sets specified Object on selected path
	 */
	void set(@NotNull String path, @Nullable Object value);

	/**
	 * Gets the object from the config or default.<br>
	 * Get the object from path, if object at selected path is null or of another
	 * class, default value is returned
	 * 
	 * @param <T> Object type
	 * @param path Path of the Object
	 * @param def Default Object
	 * @param clazz Object class
	 * @return object or default
	 */
	@SuppressWarnings("unchecked")
	default <T> T get(String path, T def, Class<T> clazz) {
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
	 * internal use only
	 * 
	 * @param path Path of the Object
	 * @return Object at specified path
	 */
	@Deprecated
	Object get(@NotNull String path);

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or of another
	 * class, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable SoundInfo loadSoundInfo(@NotNull String path, @Nullable SoundInfo def) {
		return load(path, def, SoundInfo.class);
	}

	default @Nullable SoundInfo getSoundInfo(@NotNull String path, @Nullable SoundInfo def) {
		return get(path, def, SoundInfo.class);
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or of another
	 * class, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
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
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable Boolean getBoolean(@NotNull String path, @Nullable Boolean def) {
		return get(path, def, Boolean.class);
	}
	
	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or of another
	 * class, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable Boolean loadBoolean(@NotNull String path, @Nullable Boolean def) {
		return load(path, def, Boolean.class);
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or of another
	 * class, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable Integer loadInteger(@NotNull String path, @Nullable Integer def) {
		Number val = load(path, def, Number.class);
		return val == null ? null : val.intValue();
	}

	default @Nullable Integer getInteger(@NotNull String path, @Nullable Integer def) {
		Number val = get(path, def, Number.class);
		return val == null ? null : val.intValue();
	}
	

	default @Nullable PlayerSnapshot loadPlayerSnapshot(@NotNull String path, @Nullable PlayerSnapshot def) {
		return load(path, def, PlayerSnapshot.class);
	}

	default @Nullable PlayerSnapshot getPlayerSnapshot(@NotNull String path, @Nullable PlayerSnapshot def) {
		return get(path, def, PlayerSnapshot.class);
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or of another
	 * class, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable Long loadLong(@NotNull String path, @Nullable Long def) {
		Number val = load(path, def, Number.class);
		return val == null ? null : val.longValue();
	}

	default @Nullable String loadMessage(@NotNull String path, @Nullable String def,
			@Nullable CommandSender target, String... holders) {
		return loadMessage(path, def, true, target, holders);
	}

	default @Nullable String loadMessage(@NotNull String path, @Nullable List<String> def,
			@Nullable CommandSender target, String... holders) {
		return loadMessage(path, def, true, target, holders);
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

	default @Nullable String loadMessage(@NotNull String path, @Nullable String def, String... holders) {
		return loadMessage(path, def, true, null, holders);
	}

	default @Nullable String loadMessage(@NotNull String path, @Nullable List<String> def, String... holders) {
		return loadMessage(path, def, true, null, holders);
	}

	default @Nullable ComponentBuilder loadComponentMessage(@NotNull String path, @Nullable String defMessage,
			@Nullable String defHover, @Nullable String defClick, String... holders) {
		return loadComponentMessage(path, defMessage, defHover, defClick, null, true, null, holders);
	}

	default @Nullable ComponentBuilder loadMessage(@NotNull String path, @Nullable String defMessage,
			@Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, String... holders) {
		return loadComponentMessage(path, defMessage, defHover, defClick, action, true, null, holders);
	}

	default @Nullable String loadMessage(@NotNull String path, @Nullable String def, boolean color,
			String... holders) {
		return loadMessage(path, def, color, null, holders);
	}

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
	
	default @Nullable ComponentBuilder loadComponentMessage(@NotNull String path, @Nullable String defMessage,
			@Nullable String defHover, @Nullable String defClick, boolean color, String... holders) {
		return loadComponentMessage(path, defMessage, defHover, defClick, null, color, null, holders);
	}
	default @Nullable ComponentBuilder loadComponentMessage(@NotNull String path, @Nullable String defMessage,
			@Nullable String defHover, @Nullable String defClick, ClickEvent.Action action, boolean color,
			String... holders) {
		return loadComponentMessage(path, defMessage, defHover, defClick, action, color, null, holders);
	}

	default @Nullable String loadMessage(@NotNull String path, @Nullable String def, boolean color,
			@Nullable CommandSender target, String... holders) {
		if (holders.length > 0) {
			if (!this.contains(path + "_HOLDERS")) {
				StringBuilder build = new StringBuilder();
				for (int i = 0; i < holders.length; i += 2)
					build.append(holders[i]).append(" ");
				this.set(path + "_HOLDERS", build.substring(0, build.length() - 1));
			}
		}

		return UtilsString.fix(load(path, def, String.class), target instanceof Player ? ((Player) target) : null,
				color, holders);
	}

	@SuppressWarnings("unchecked")
	default @Nullable String loadMessage(@NotNull String path, @Nullable List<String> def, boolean color,
			@Nullable CommandSender target, String... holders) {
		if (holders.length > 0) {
			if (!this.contains(path + "_HOLDERS")) {
				StringBuilder build = new StringBuilder();
				for (int i = 0; i < holders.length; i += 2)
					build.append(holders[i]).append(" ");
				this.set(path + "_HOLDERS", build.substring(0, build.length() - 1));
			}
		}

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

	default @Nullable List<String> loadMultiMessage(@NotNull String path, @Nullable List<String> def,
			boolean color, String... holders) {
		return loadMultiMessage(path, def, color, null, holders);
	}

	default @Nullable List<String> loadMultiMessage(@NotNull String path, @Nullable List<String> def,
			@Nullable CommandSender target, String... holders) {
		return loadMultiMessage(path, def, true, target, holders);
	}

	default @Nullable List<String> loadMultiMessage(@NotNull String path, @Nullable List<String> def,
			String... holders) {
		return loadMultiMessage(path, def, true, null, holders);
	}

	@SuppressWarnings("unchecked")
	default @Nullable List<String> loadMultiMessage(@NotNull String path, @Nullable List<String> def,
			boolean color, @Nullable CommandSender target, String... holders) {
		if (holders.length > 0) {
			if (!this.contains(path + "_HOLDERS")) {
				StringBuilder build = new StringBuilder();
				for (int i = 0; i < holders.length; i += 2)
					build.append(holders[i]).append(" ");
				this.set(path + "_HOLDERS", build.substring(0, build.length() - 1));
			}
		}
		try {
			return UtilsString.fix(load(path, def, List.class), target instanceof Player ? ((Player) target) : null,
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
		if (holders.length > 0) {
			if (!this.contains(path + ".USABLE_HOLDERS")) {
				StringBuilder build = new StringBuilder();
				for (int i = 0; i < holders.length; i += 2)
					build.append(holders[i]).append(" ");
				this.set(path + ".USABLE_HOLDERS", build.substring(0, build.length() - 1));
			}
		}

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
		if (hover != null && !hover.isEmpty())
			comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
		if (click != null && !click.isEmpty())
			comp.event(new ClickEvent(action, click));
		return comp;
	}

	default @Nullable ComponentBuilder loadComponentMessage(@NotNull String path, @Nullable String defMessage,
			@Nullable String defHover, @Nullable String defClick, boolean color, @Nullable CommandSender target,
			String... holders) {
		return loadComponentMessage(path, defMessage, defHover, defClick, null, color, target, holders);
	}

	default @Nullable ComponentBuilder loadComponentMessage(@NotNull String path, @Nullable String defMessage,
			@Nullable String defHover, @Nullable String defClick, @Nullable ClickEvent.Action action, boolean color,
			@Nullable CommandSender target, String... holders) {
		if (holders.length > 0) {
			if (!this.contains(path + ".USABLE_HOLDERS")) {
				StringBuilder build = new StringBuilder();
				for (int i = 0; i < holders.length; i += 2)
					build.append(holders[i]).append(" ");
				this.set(path + ".USABLE_HOLDERS", build.substring(0, build.length() - 1));
			}
		}

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
		if (hover != null && !hover.isEmpty())
			comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
		if (click != null && !click.isEmpty())
			comp.event(new ClickEvent(action, click));
		return comp;
	}

	/**
	 * 
	 * @param path Path of the Object
	 * @param defMessage Default message
	 * @param defHover Default hover message
	 * @param defClick Default click suggestion
	 * @param action Default click action type
	 * @param color Whether or not translate color codes
	 * @param target Player target for PlaceHolderAPI holders
	 * @param holders Additional placeholders to replace in the format "holder1", "value1", "holder2", "value2"...
	 * @return null if message is null or empty
	 */
	@Deprecated
	default @Nullable ComponentBuilder loadMessage(@NotNull String path, @Nullable List<String> defMessage,
			@Nullable List<String> defHover, @Nullable String defClick, @Nullable ClickEvent.Action action,
			boolean color, @Nullable CommandSender target, String... holders) {
		if (holders.length > 0) {
			if (!this.contains(path + ".USABLE_HOLDERS")) {
				StringBuilder build = new StringBuilder();
				for (int i = 0; i < holders.length; i += 2)
					build.append(holders[i]).append(" ");
				this.set(path + ".USABLE_HOLDERS", build.substring(0, build.length() - 1));
			}
		}

		String message = String.join("\n", UtilsString.fix(loadStringList(path + ".message", defMessage),
				target instanceof Player ? ((Player) target) : null, color, holders));
		if (message == null || message.isEmpty())
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
		if (click != null && !click.isEmpty())
			comp.event(new ClickEvent(action, click));
		return comp;
	}
	

	default @Nullable ComponentBuilder loadComponentMessage(@NotNull String path, @Nullable List<String> defMessage,
			@Nullable List<String> defHover, @Nullable String defClick, @Nullable ClickEvent.Action action,
			boolean color, @Nullable CommandSender target, String... holders) {
		if (holders.length > 0) {
			if (!this.contains(path + ".USABLE_HOLDERS")) {
				StringBuilder build = new StringBuilder();
				for (int i = 0; i < holders.length; i += 2)
					build.append(holders[i]).append(" ");
				this.set(path + ".USABLE_HOLDERS", build.substring(0, build.length() - 1));
			}
		}

		String message = String.join("\n", UtilsString.fix(loadStringList(path + ".message", defMessage),
				target instanceof Player ? ((Player) target) : null, color, holders));
		if (message == null || message.isEmpty())
			return null;

		String hover = String.join("\n",
				UtilsString.fix(loadStringList(path + ".hover", defHover == null ? new ArrayList<>() : defHover),
						target instanceof Player ? ((Player) target) : null, color, holders));
		String click = UtilsString.fix(loadString(path + ".click", defClick == null ? "" : defClick),
				target instanceof Player ? ((Player) target) : null, false, holders);
		action = loadEnum(path + ".click_action", action == null ? ClickEvent.Action.SUGGEST_COMMAND : action,
				ClickEvent.Action.class);
		ComponentBuilder comp = new ComponentBuilder(message);
		if (hover != null && !hover.isEmpty())
			comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
		if (click != null && !click.isEmpty())
			comp.event(new ClickEvent(action, click));
		return comp;
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or of another
	 * class, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default ItemStack loadItemStack(String path, ItemStack def) {
		return load(path, def, ItemStack.class);
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or of another
	 * class, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable String loadString(@NotNull String path, @Nullable String def) {
		return load(path, def, String.class);
	}

	default @Nullable String getString(@NotNull String path, @Nullable String def) {
		return get(path, def, String.class);
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or of another
	 * class, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
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

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or of another
	 * class, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @NotNull Set<String> loadStringSet(@NotNull String path, @Nullable Collection<String> def) {
		return new LinkedHashSet<>(loadStringList(path,
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

	default @NotNull Set<String> getStringSet(@NotNull String path, @Nullable Collection<String> def) {
		return new HashSet<>(getStringList(path,
				def == null ? new ArrayList<>() : (def instanceof List ? (List<String>) def : new ArrayList<>(def))));
	}

	/**
	 * assumes that enum costants are all uppercased
	 * 
	 * @param <T>
	 *            the class of the enum
	 * @param path Path of the Object
	 * @param clazz
	 *            the class of the enum
	 * @param def Default Object
	 * @return if path lead to a string attemps return the matching Enum value, if
	 *         the string is empty return def
	 */
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
	 * assumes that enum costants are all uppercased null enum values contained in
	 * def might be lost
	 * 
	 * @param <T>
	 *            the class of the enum
	 * @param path Path of the Object
	 * @param clazz
	 *            the class of the enum
	 * @param def Default Object
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
		if (from == null || from.isEmpty())
			return destination;
		for (String value : from) {
			T val;
			try {
				if (value == null || value.isEmpty())
					val = null;
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
	 * assumes that enum costants are all uppercased null enum values contained in
	 * def might be lost
	 * 
	 * @param <T>
	 *            the class of the enum
	 * @param path Path of the Object
	 * @param clazz
	 *            the class of the enum
	 * @param def Default Object
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
		if (from == null || from.isEmpty())
			return destination;
		for (String value : from) {
			T val;
			try {
				if (value == null || value.isEmpty())
					val = null;
				val = Enum.valueOf(clazz, value);
			} catch (IllegalArgumentException e) {
				try {
					val = (T) Enum.valueOf(clazz, value.toUpperCase());
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
		if (from == null || from.isEmpty())
			return destination;
		for (String value : from) {
			T val;
			try {
				if (value == null || value.isEmpty())
					val = null;
				val = (T) Enum.valueOf(clazz, value);
			} catch (IllegalArgumentException e) {
				try {
					val = (T) Enum.valueOf(clazz, value.toUpperCase());
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
	 * Get the object from path, if object at selected path is null or of another
	 * class, default value is set and returned
	 * 
	 * @param <T> Map values type
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	@SuppressWarnings("unchecked")
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
			} catch (Exception e) {
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
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or not a String,
	 * default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable Sound loadSound(@NotNull String path, @Nullable Sound def) {
		return loadEnum(path, def, Sound.class);
	}

	default @Nullable Sound getSound(@NotNull String path, @Nullable Sound def) {
		return getEnum(path, def, Sound.class);
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or not a String,
	 * default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable Material loadMaterial(@NotNull String path, @Nullable Material def) {
		return loadEnum(path, def, Material.class);
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or not a String
	 * List, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable List<Material> loadMaterialList(@NotNull String path, @Nullable Collection<Material> def) {
		return loadEnumList(path, def, Material.class);
	}
	

	default @Nullable List<Material> getMaterialList(@NotNull String path, @Nullable Collection<Material> def) {
		return getEnumList(path, def, Material.class);
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or not a String
	 * List, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable EnumSet<Material> loadMaterialSet(@NotNull String path,
			@Nullable Collection<Material> def) {
		return loadEnumSet(path, def, Material.class);
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or not a String,
	 * default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable EntityType loadEntityType(@NotNull String path, @Nullable EntityType def) {
		return loadEnum(path, def, EntityType.class);
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or not a String
	 * List, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable List<EntityType> loadEntityTypeList(@NotNull String path,
			@Nullable Collection<EntityType> def) {
		return loadEnumList(path, def, EntityType.class);
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or not a String
	 * List, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @Nullable EnumSet<EntityType> loadEntityTypeSet(@NotNull String path,
			@Nullable Collection<EntityType> def) {
		return loadEnumSet(path, def, EntityType.class);
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or not a String
	 * List, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default @NotNull ItemFlag[] loadItemFlags(@NotNull String path, ItemFlag[] def) {
		return loadEnumSet(path, def == null ? null : Arrays.asList(def), ItemFlag.class).toArray(new ItemFlag[0]);
	}

	default BoundingBox getBoundingBox(String path) {
		return getBoundingBox(path, null);
	}

	default BoundingBox getBoundingBox(String path, BoundingBox def) {
		return get(path, def, BoundingBox.class).clone();
	}

	/**
	 * Gets the object from the config or set default.<br>
	 * Get the object from path, if object at selected path is null or of another
	 * class, default value is set and returned
	 * 
	 * @param path Path of the Object
	 * @param def Default Object
	 * @return object or default
	 */
	default BoundingBox loadBoundingBox(String path, BoundingBox def) {
		return load(path, def, BoundingBox.class).clone();
	}

	/**
	 * Creates a ConfigurationSection at the specified path, with specified values.
	 * 
	 * Any value that was previously set at this path will be overwritten. If the
	 * previous value was itself a ConfigurationSection, it will be orphaned.
	 * 
	 * @param path Path of the Object
	 * @return Newly created section
	 * @see #loadSection(String, Map)
	 */
	@Deprecated
	YMLSection createSection(@NotNull String path);

	/**
	 * Creates a ConfigurationSection at the specified path, with specified values.
	 * 
	 * Any value that was previously set at this path will be overwritten. If the
	 * previous value was itself a ConfigurationSection, it will be orphaned.
	 * 
	 * @param path Path of the Object
	 * @param map
	 *            The values to used.
	 * @return Newly created section
	 * @see #loadSection(String, Map)
	 */
	@Deprecated
	YMLSection createSection(@NotNull String path, @NotNull Map<?, ?> map);

	/**
	 * Gets the requested ConfigurationSection by path.
	 * 
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
	 * Gets the parent YMLSection that directly contains this
	 * YMLSection.
	 * 
	 * For any Configuration themselves, this will return null.
	 * 
	 * If the section is no longer contained within its parent for any reason, such
	 * as being replaced with a different value, this may return null.
	 * 
	 * @return Parent section containing this section.
	 */
	@Nullable YMLSection getParent();

	/**
	 * Gets the equivalent YMLSection from the default Configuration
	 * defined in getRoot().
	 * 
	 * If the root contains no defaults, or the defaults doesn't contain a value for
	 * this path, or the value at this path is not a YMLSection then this
	 * will return null.
	 * 
	 * @return Equivalent section in root configuration
	 */
	YMLSection getDefaultSection();

	/**
	 * 
	 * @param path Path of the Object
	 * @return YMLSection at specified path or a new generated section
	 */
	@NotNull default YMLSection loadSection(String path) {
		return loadSection(path, null);
	}

	@NotNull default YMLSection loadSection(String path, Map<String, Object> def) {
		if (this.isConfigurationSection(path))
			return getConfigurationSection(path);
		return def == null ? createSection(path) : createSection(path, def);
	}

	void setNoDirty(String path, Object value);

	default <T extends Enum<T>> void setEnumsAsStringList(String path, Collection<T> enumValues) {
		if (enumValues == null) {
			set(path, null);
			return;
		}
		List<String> list = new ArrayList<>();
		for (Enum<T> value : enumValues)
			list.add(value == null ? null : value.name());
		set(path, list);
	}

	default <T extends Enum<T>> void setEnumAsString(String path, T enumValue) {
		set(path, enumValue == null ? null : enumValue.name());
	}

	default List<Integer> getIntegerList(String path, List<Integer> def) {
		List<Integer> val = getIntegerList(path);
		if (val.isEmpty() && def != null)
			return def;
		return val;
	}

}
