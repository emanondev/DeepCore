package emanondev.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

public class YMLConfig extends YamlConfiguration implements YMLSection {
	private JavaPlugin plugin;
	private File file;
	private String name;

	@Deprecated
	public YMLSection getConfigurationSection(String path) {
		return (YMLSection) super.getConfigurationSection(path);
	}

	public YMLSection getParent() {
		return (YMLSection) super.getParent();
	}

	@Override
	public YMLSection getDefaultSection() {
		return (YMLSection) super.getDefaultSection();
	}

	public YMLConfig getRoot() {
		return (YMLConfig) super.getRoot();
	}

	@Deprecated
	@NotNull
	public YMLSection createSection(@NotNull String path) {
		Validate.notEmpty(path, "Cannot create section at empty path");
		Configuration root = getRoot();
		if (root == null) {
			throw new IllegalStateException("Cannot create section without a root");
		}

		char separator = root.options().pathSeparator();

		int i1 = -1;
		YMLSection section = this;
		int i2;
		while ((i1 = path.indexOf(separator, i2 = i1 + 1)) != -1) {
			String node = path.substring(i2, i1);
			YMLSection subSection = section.getConfigurationSection(node);
			if (subSection == null) {
				section = section.createSection(node);
				continue;
			}
			section = subSection;
		}

		String key = path.substring(i2);
		if (section == this) {
			YMLSection result = new YMLSubSection(this, key);
			this.set(key, result);
			//this.map.put(key, result);//TODO i wanna die
			return result;
		}
		return section.createSection(key);
	}

	@NotNull
	public YMLSection createSection(@NotNull String path, @NotNull Map<?, ?> map) {
		return (YMLSection) super.createSection(path, map);
	}

	/**
	 * Returns the file path of this config.
	 * 
	 * @return the file path starting by
	 *         {@link #getPlugin()}.{@link JavaPlugin#getDataFolder()
	 *         getDataFolder()}.
	 */
	public String getFileName() {
		return name;
	}

	/**
	 * Return the plugin associated with this Config.
	 * 
	 * @return the plugin associated with this Config
	 */
	public JavaPlugin getPlugin() {
		return plugin;
	}

	/**
	 * Constructs a new Configuration File.<br>
	 * Used file is located at plugin.{@link JavaPlugin#getDataFolder()
	 * getDataFolder()}/{@link #fixName(String) YMLConfig.fixName(name)}.<br>
	 * If the plugin jar has a file on name path, that file is used to generate the
	 * config file.
	 * 
	 * @param plugin associated plugin to grab the folder
	 * @param name   raw name of the file subpath
	 * @throws NullPointerException     if name is null
	 * @throws IllegalArgumentException if name is empty
	 */
	public YMLConfig(@NotNull JavaPlugin plugin, @NotNull String name) {
		this(plugin, new File(plugin.getDataFolder(), fixName(name)));
		/*
		 * Validate.notNull(plugin, "plugin is null"); this.plugin = plugin; name =
		 * fixName(name); this.name = name; file = new File(plugin.getDataFolder(),
		 * name); reload();
		 */
	}

	public YMLConfig(@NotNull JavaPlugin plugin, @NotNull File file) {
		Validate.notNull(plugin, "plugin is null");
		Validate.notNull(plugin, "file is null");
		if (file.isDirectory())
			throw new IllegalStateException("file is a directory");
		this.plugin = plugin;
		this.file = file;
		this.name = file.getName();
		reload();
	}

	/**
	 * Fix the given name for a yaml file name
	 * 
	 * @param name the raw name of the file with or withouth .yml
	 * @return fixed name
	 * @throws NullPointerException     if name is null
	 * @throws IllegalArgumentException if name is empty
	 */
	public static String fixName(String name) {
		Validate.notNull(name, "YAML file must have a name!");
		Validate.notEmpty(name, "YAML file must have a name!");
		if (!name.endsWith(".yml"))
			name += ".yml";
		return name;
	}

	private boolean saveIfDirtyOnReload = true;

	public YMLConfig setSaveIfDirtyOnReload(boolean value) {
		saveIfDirtyOnReload = value;
		return this;
	}

	@Override
	public boolean reload() {
		if (dirty && saveIfDirtyOnReload) {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.translateAlternateColorCodes('&', "&1[&f" + getPlugin().getName()
							+ "&1] &e✓ &fForce saving file &e" + getFile().getPath() + "&f before reloading"));
			save();
		}
		boolean existed = file.exists();
		if (!file.exists()) {

			if (!file.getParentFile().exists()) { // Create parent folders if they don't exist
				file.getParentFile().mkdirs();
			}
			if (plugin.getResource(file.getName()) != null) {
				plugin.saveResource(file.getName(), true); // Save the one from the JAR if possible
			} else {
				try {
					file.createNewFile();
				} // Create a blank file if there's not one to copy from the JAR
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			this.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (plugin.getResource(file.getName()) != null) { // Set up defaults in case their config is broked.
			InputStreamReader defConfigStream = null;
			try {
				defConfigStream = new InputStreamReader(plugin.getResource(file.getName()), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			this.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
		}
		return existed;
	}

	@Override
	public void save() {
		boolean oldDirty = dirty;
		dirty = false;
		try {
			this.save(file);
		} catch (Exception e) {
			dirty = oldDirty;
			e.printStackTrace();
		}
	}

	private BukkitTask delayedSave = null;

	public void saveAsync() {
		if (dirty == false || delayedSave != null)
			return;
		try {

			delayedSave = new BukkitRunnable() {
				@Override
				public void run() {
					delayedSave = null;
					if (dirty)
						save();
				}
			}.runTaskLater(getPlugin(), 5L);

		} catch (Exception e) {
			e.printStackTrace();
			save();
		}
	}

	@Override
	public @NotNull File getFile() {
		return file;
	}

	@Override
	public @NotNull Set<String> getKeys(@NotNull String path) {
		if (path == null || path.isEmpty())
			return getKeys(false);
		ConfigurationSection section = this.getConfigurationSection(path);
		if (section == null)
			return new LinkedHashSet<String>();
		else
			return section.getKeys(false);
	}

	private boolean dirty = false;

	public boolean isDirty() {
		return dirty;
	}

	private boolean autosaveOnSet = true;

	public boolean getAutosaveOnSet() {
		return autosaveOnSet;
	}

	/**
	 * Should autosave when set method id used?
	 * 
	 * @param value the value to set
	 */
	public void setAutosaveOnSet(boolean value) {
		autosaveOnSet = value;
	}

	@Override
	public void set(@NotNull String path, Object value) {
		set(path, value, true);
	}

	public void set(String path, Object value, boolean save) {
		super.set(path, value);
		dirty = true;
		if (save && autosaveOnSet)
				if (plugin.isEnabled())
					saveAsync();
				else
					save();
	}

	@Override
	public void setNoDirty(String path, Object value) {
		super.set(path, value);
	}

	void setDirty() {
		dirty = true;
	}

	@SuppressWarnings("unchecked")
	public @NotNull List<String> getStringList(@NotNull String path, @Nullable List<String> def) {
		try {
			return get(path, def, List.class);
		} catch (Exception e) {
			e.printStackTrace();
			return def;
		}
	}

	public @Nullable FireworkEffect loadFireworkEffect(@NotNull String path, @Nullable FireworkEffect def) {
		try {
			Type type = loadFireworkEffectType(path + ".type", def == null ? null : def.getType());
			if (type == null)
				return def;
			Builder builder = FireworkEffect.builder().with(type)
					.flicker(loadBoolean(path + ".flicker", def == null ? false : def.hasFlicker()))
					.trail(loadBoolean(path + ".trail", def == null ? false : def.hasTrail()))
					.withColor(loadColors(path + ".colors", def == null ? Arrays.asList(Color.RED) : def.getColors()))
					.withFade(loadColors(path + ".fade_colors", def == null ? new ArrayList<>() : def.getFadeColors()));
			return builder.build();
		} catch (Exception e) {
			e.printStackTrace();
			new IllegalArgumentException(getError(path)).printStackTrace();
		}
		return def;
	}

	public @Nullable FireworkEffect getFireworkEffect(@NotNull String path, @Nullable FireworkEffect def) {
		try {
			Type type = getEnum(path + ".type", def == null ? null : def.getType(), FireworkEffect.Type.class);
			if (type == null)
				return def;
			Builder builder = FireworkEffect.builder().with(type)
					.flicker(getBoolean(path + ".flicker", def == null ? false : def.hasFlicker()))
					.trail(getBoolean(path + ".trail", def == null ? false : def.hasTrail()))
					.withColor(getColors(path + ".colors", def == null ? Arrays.asList(Color.RED) : def.getColors()))
					.withFade(getColors(path + ".fade_colors", def == null ? new ArrayList<>() : def.getFadeColors()));
			return builder.build();
		} catch (Exception e) {
			e.printStackTrace();
			new IllegalArgumentException(getError(path)).printStackTrace();
		}
		return def;
	}

	public @NotNull List<Color> loadColors(@NotNull String path, @Nullable Collection<Color> def) {
		return stringListToColorList(loadStringList(path, colorCollectionToStringList(def)));
	}

	public @NotNull List<Color> getColors(@NotNull String path, @Nullable Collection<Color> def) {
		return stringListToColorList(getStringList(path, colorCollectionToStringList(def)));
	}

	private ArrayList<String> colorCollectionToStringList(@Nullable Collection<Color> colors) {
		ArrayList<String> list = new ArrayList<>();
		if (colors != null)
			for (Color color : colors)
				list.add(color.getRed() + " " + color.getGreen() + " " + color.getBlue());
		return list;
	}

	private ArrayList<Color> stringListToColorList(@Nullable Collection<String> rgb) {
		ArrayList<Color> colors = new ArrayList<>();
		for (String color : rgb) {
			try {
				String[] args = color.split(" ");
				colors.add(Color.fromRGB(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2])));
			} catch (Exception e) {
				e.printStackTrace();
				new IllegalArgumentException("color rgb format example '0 20 255'").printStackTrace();
			}
		}
		return colors;
	}

	private @NotNull List<String> colorsToStringList(Collection<Color> colors) {
		ArrayList<String> list = new ArrayList<String>();
		if (colors != null)
			for (Color color : colors)
				list.add(color.getRed() + " " + color.getGreen() + " " + color.getBlue());
		return list;
	}

	public @Nullable Type loadFireworkEffectType(@NotNull String path, @Nullable Type def) {
		return this.loadEnum(path, def, Type.class);
	}

	private String getError(String path) {
		return "Value has wrong type or wrong value at '" + path + ":' on file " + file.getName();
	}

	/**
	 * Use {@link #loadInteger(String, Integer)}
	 * 
	 * @param path Path of the Object
	 * @param def  Default object
	 * @return int value or default
	 */
	@Deprecated
	public @Nullable int loadInt(@NotNull String path, @Nullable Integer def) {
		Number val = load(path, def, Number.class);
		return val == null ? 0 : val.intValue();
	}

	/**
	 * Load String value.<br>
	 * adds path+_HOLDERS if any exists to notify usable holders<br>
	 * target = null
	 * 
	 * @param path  Path of the Object
	 * @param def   Default object
	 * @param color Whether or not translate color codes
	 * @param args  holders and replacer
	 * @return the value found or default if none
	 * @see #loadString(String, String, Player, boolean, String...) loadString(path,
	 *      def, null, color, args)
	 * @see #load(String, Object, Class)
	 */
	@Deprecated
	public @Nullable String loadString(@NotNull String path, @Nullable String def, boolean color, String... args) {
		return loadString(path, def, null, color, args);
	}

	/**
	 * Load String value.<br>
	 * adds path+_HOLDERS if any exists to notify usable holders
	 * 
	 * @param path   Path of the Object
	 * @param def    Default object
	 * @param target Player target for PlaceHolderAPI holders
	 * @param color  Whether or not translate color codes
	 * @param args   holders and replacer
	 * @return the value found or default if none
	 * @see #loadString(String, String, Player, boolean, String...) loadString(path,
	 *      def, null, color, args)
	 * @see #load(String, Object, Class)
	 */
	@Deprecated
	public @Nullable String loadString(@NotNull String path, @Nullable String def, @Nullable Player target,
			boolean color, String... args) {
		if (args.length > 0) {
			if (!this.contains(path + "_HOLDERS")) {
				StringBuilder build = new StringBuilder("");
				for (int i = 0; i < args.length; i += 2)
					build.append(args[i] + " ");
				this.set(path + "_HOLDERS", build.substring(0, build.length() - 1));
			}
		}
		return UtilsString.fix(load(path, def, String.class), target, color, args);
	}

	@Deprecated
	public <T extends Enum<T>> T loadEnum(String path, Class<T> clazz, T def) {
		return loadEnum(path, def, clazz);
	}

	@Deprecated
	public List<Material> loadMaterials(String path, List<Material> def) {
		return loadMaterialList(path, def);
	}

	/**
	 * Get String value.<br>
	 * adds path+_HOLDERS if any exists to notify usable holders<br>
	 * 
	 * @param path   Path of the Object
	 * @param def    Default object
	 * @param target Player target for PlaceHolderAPI holders
	 * @param color  Whether or not translate color codes
	 * @param args   holders and replacer
	 * @return the value found or default if none
	 * @see #get(String, Object, Class)
	 * @deprecated use
	 *             {@link YMLSection#loadMessage(String, String, boolean, CommandSender, String...)}
	 */
	public @Nullable String getString(@NotNull String path, @Nullable String def, @Nullable Player target,
			boolean color, String... args) {
		if (args.length > 0 && this.contains(path))
			if (!this.contains(path + "_HOLDERS")) {
				StringBuilder build = new StringBuilder("");
				for (int i = 0; i < args.length; i += 2)
					build.append(args[i] + " ");
				this.set(path + "_HOLDERS", build.substring(0, build.length() - 1));
				save();
			}
		return UtilsString.fix(get(path, def, String.class), target, color, args);
	}

	/**
	 * Get String value.<br>
	 * adds path+_HOLDERS if any exists to notify usable holders<br>
	 * target = null
	 * 
	 * @param path  Path of the Object
	 * @param def   Default object
	 * @param color Whether or not translate color codes
	 * @param args  holders and replacer
	 * @return the value found or default if none
	 * @see #getString(String, String, Player, boolean, String...) getString(path,
	 *      def, null, color, args)
	 * @see #get(String, Object, Class)
	 * @deprecated use
	 *             {@link YMLSection#loadMessage(String, String, boolean, CommandSender, String...)}
	 */
	public @Nullable String getString(@NotNull String path, @Nullable String def, boolean color, String... args) {
		return this.getString(path, def, null, color, args);
	}

	/**
	 * target = null
	 * 
	 * @param path  Path of the Object
	 * @param def   Default object
	 * @param color Whether or not translate color codes
	 * @return the value found or default if none
	 * @deprecated use
	 *             {@link YMLSection#loadMultiMessage(String, List, boolean, CommandSender, String...)}
	 */
	public @NotNull List<String> loadStringList(@NotNull String path, @Nullable List<String> def, boolean color) {
		return loadStringList(path, def, null, color);
	}

	/**
	 * adds path+_HOLDERS if any exists to notify usable holders
	 * 
	 * @param path   Path of the Object
	 * @param def    Default object
	 * @param target Player target for PlaceHolderAPI holders
	 * @param color  Whether or not translate color codes
	 * @param args   holders and replacer
	 * @return the value found or default if none
	 * @deprecated use
	 *             {@link YMLSection#loadMultiMessage(String, List, boolean, CommandSender, String...)}
	 */
	@SuppressWarnings("unchecked")
	public @NotNull List<String> loadStringList(@NotNull String path, @Nullable List<String> def,
			@Nullable Player target, boolean color, String... args) {
		if (args.length > 0) {
			if (!this.contains(path + "_HOLDERS")) {
				StringBuilder build = new StringBuilder("");
				for (int i = 0; i < args.length; i += 2)
					build.append(args[i] + " ");
				this.set(path + "_HOLDERS", build.substring(0, build.length() - 1));
			}
		}
		try {
			return UtilsString.fix(load(path, def, List.class), target, color, args);
		} catch (Exception e) {
			e.printStackTrace();
			return UtilsString.fix(def, target, color, args);
		}
	}

	/**
	 * target = null
	 * 
	 * @param path  Path of the Object
	 * @param def   Default object
	 * @param color Whether or not translate color codes
	 * @return the value found or default if none
	 * @deprecated use
	 *             {@link YMLSection#loadMultiMessage(String, List, boolean, CommandSender, String...)}
	 */
	public @NotNull List<String> getStringList(@NotNull String path, @Nullable List<String> def, boolean color) {
		return getStringList(path, def, null, color);
	}

	/**
	 * adds path+_HOLDERS if any exists to notify usable holders
	 * 
	 * @param path   Path of the Object
	 * @param def    Default object
	 * @param target Player target for PlaceHolderAPI holders
	 * @param color  Whether or not translate color codes
	 * @param args   holders and replacer
	 * @return the value found or default if none
	 * @deprecated use
	 *             {@link YMLSection#loadMultiMessage(String, List, boolean, CommandSender, String...)}
	 */
	@SuppressWarnings("unchecked")
	public @NotNull List<String> getStringList(@NotNull String path, @Nullable List<String> def,
			@Nullable Player target, boolean color, String... args) {
		if (args.length > 0) {
			if (!this.contains(path + "_HOLDERS")) {
				StringBuilder build = new StringBuilder("");
				for (int i = 0; i < args.length; i += 2)
					build.append(args[i] + " ");
				this.set(path + "_HOLDERS", build.substring(0, build.length() - 1));
			}
		}
		try {
			return UtilsString.fix(get(path, def, List.class), target, color, args);
		} catch (Exception e) {
			e.printStackTrace();
			return UtilsString.fix(def, target, color, args);
		}
	}

	public @Nullable ItemStack loadItemStack(@NotNull String path, @Nullable ItemStack def) {
		return load(path, def, ItemStack.class);
	}

	/**
	 * Return result component
	 * 
	 * @param path            Path of the Object
	 * @param defMessage      Default message
	 * @param defHover        Default hover message
	 * @param defClickSuggest Default click suggestion
	 * @param target          Player target for PlaceHolderAPI holders
	 * @param color           Whether or not translate color codes
	 * @param holders         Additional placeholders to replace in the format
	 *                        "holder1", "value1", "holder2", "value2"...
	 * @return result component
	 * @deprecated use #loadMessage(String, String, String, String,
	 *             net.md_5.bungee.api.chat.ClickEvent.Action, boolean, Player,
	 *             String...)
	 */
	@Deprecated
	public @Nullable ComponentBuilder loadSimpleComponentBuilder(@NotNull String path, @Nullable String defMessage,
			@Nullable String defHover, @Nullable String defClickSuggest, @Nullable CommandSender target, boolean color,
			String... holders) {
		Player p = (target instanceof Player) ? null : ((Player) target);
		String message = loadString(path + ".message", defMessage, p, color, holders);

		if (message == null || message.isEmpty())
			return null;
		if (holders != null && holders.length > 0) {
			String HOLDERS = "";
			for (int i = 0; i < holders.length; i += 2)
				HOLDERS = HOLDERS + holders[i] + " ";
			loadString(path + ".USABLE_HOLDERS", HOLDERS, false);
		}
		String hover = loadString(path + ".hover", defHover == null ? "" : defHover, p, color, holders);
		String suggest = loadString(path + ".suggest", defClickSuggest == null ? "" : defClickSuggest, p, color,
				holders);

		ComponentBuilder comp = new ComponentBuilder(message);
		if (hover != null && !hover.isEmpty())
			comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
		if (suggest != null && !suggest.isEmpty())
			comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
		return comp;
	}

	/**
	 * 
	 * @param path            Path of the Object
	 * @param defMessage      Default message
	 * @param defHover        Default hover message
	 * @param defClickSuggest Default click suggestion
	 * @param p               Player target for PlaceHolderAPI holders
	 * @param color           Whether or not translate color codes
	 * @param holders         Additional placeholders to replace in the format
	 *                        "holder1", "value1", "holder2", "value2"...
	 * @return result basecomponent
	 * @deprecated use #loadMessage(String, String, String, String,
	 *             net.md_5.bungee.api.chat.ClickEvent.Action, boolean, Player,
	 *             String...)
	 */

	public @Nullable BaseComponent[] loadSimpleBaseComponent(@NotNull String path, @Nullable String defMessage,
			@Nullable String defHover, @Nullable String defClickSuggest, @Nullable Player p, boolean color,
			String... holders) {
		ComponentBuilder comp = loadSimpleComponentBuilder(path, defMessage, defHover, defClickSuggest, p, color,
				holders);
		return comp == null ? null : comp.create();
	}

	protected void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {
		for (Map.Entry<?, ?> entry : input.entrySet()) {
			String key = entry.getKey().toString();
			Object value = entry.getValue();

			if (value instanceof Map) {
				convertMapsToSections((Map<?, ?>) value, section.createSection(key));
				continue;
			}
			if (section instanceof YMLSection)
				((YMLSection) section).setNoDirty(key, value);
			else
				section.set(key, value);
		}
	}
}