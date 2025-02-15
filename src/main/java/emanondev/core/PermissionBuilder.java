package emanondev.core;

import emanondev.core.command.CoreCommand;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A builder class for creating and registering Bukkit {@link Permission} objects in a fluent manner.
 * <p>
 * This builder allows you to define a permission name, description, default access level,
 * and child permissions. It also provides various static factory methods to create permission
 * names based on a plugin, command, or module context.
 * </p>
 *
 * @see Permission
 */
public class PermissionBuilder {

    private final Map<String, Boolean> children = new LinkedHashMap<>();
    private String name;
    private PermissionDefault def = PermissionDefault.OP;
    private String description;

    /**
     * Constructs a new {@code PermissionBuilder} using a permission name derived from the given plugin's name and a sub-name.
     * <p>
     * The resulting permission name is in the format {@code plugin.getName() + "." + subName}.
     * </p>
     *
     * @param plugin  the plugin responsible for this permission; must not be null
     * @param subName the sub-name for the permission; must not be null
     */
    public PermissionBuilder(final @NotNull Plugin plugin, final @NotNull String subName) {
        this(plugin.getName() + "." + subName);
    }

    /**
     * Constructs a new {@code PermissionBuilder} with the specified permission name.
     *
     * @param name the permission name; must not be null. The name is validated to ensure it is not empty,
     *             does not contain spaces, and does not start or end with a period.
     * @throws IllegalArgumentException if the permission name is invalid
     */
    public PermissionBuilder(final @NotNull String name) {
        setName(name);
    }

    /**
     * Constructs a new {@code PermissionBuilder} using an existing {@link Permission}.
     * <p>
     * This copies the name, description, default access, and child permissions from the provided permission.
     * </p>
     *
     * @param perm the base permission; must not be null
     */
    public PermissionBuilder(final @NotNull Permission perm) {
        setName(perm.getName());
        setDescription(perm.getDescription());
        setAccess(perm.getDefault());
        children.putAll(perm.getChildren());
    }

    /**
     * Creates a new {@code PermissionBuilder} for a command-based permission.
     * <p>
     * The resulting permission name is in the format
     * {@code command.getPlugin().getName() + ".command." + command.getID() + "." + subName}.
     * </p>
     *
     * @param command the command associated with the permission; must not be null
     * @param subName the sub-path for the permission; must not be null
     * @return a new {@code PermissionBuilder} instance with the constructed permission name
     */
    @NotNull
    public static PermissionBuilder ofCommand(final @NotNull CoreCommand command, final @NotNull String subName) {
        return new PermissionBuilder(command.getPlugin().getName() + ".command." + command.getID() + "." + subName);
    }

    /**
     * Creates a new {@code PermissionBuilder} for a command-based permission.
     * <p>
     * The resulting permission name is in the format
     * {@code plugin.getName() + ".command." + commandName + "." + subName}.
     * </p>
     *
     * @param plugin      the plugin associated with the command; must not be null
     * @param commandName the command name; must not be null
     * @param subName     the sub-path for the permission; must not be null
     * @return a new {@code PermissionBuilder} instance with the constructed permission name
     */
    @NotNull
    public static PermissionBuilder ofCommand(final @NotNull Plugin plugin,
                                              final @NotNull String commandName,
                                              final @NotNull String subName) {
        return new PermissionBuilder(plugin.getName() + ".command." + commandName + "." + subName);
    }

    /**
     * Creates a new {@code PermissionBuilder} for a command-based permission.
     * <p>
     * The resulting permission name is in the format
     * {@code plugin.getName() + ".command." + commandName}.
     * </p>
     *
     * @param plugin      the plugin associated with the command; must not be null
     * @param commandName the command name; must not be null
     * @return a new {@code PermissionBuilder} instance with the constructed permission name
     */
    @NotNull
    public static PermissionBuilder ofCommand(final @NotNull Plugin plugin, final @NotNull String commandName) {
        return new PermissionBuilder(plugin.getName() + ".command." + commandName);
    }

    /**
     * Creates a new {@code PermissionBuilder} as a sub-permission of an existing permission.
     * <p>
     * The resulting permission name is in the format {@code perm.getName() + "." + subName}.
     * </p>
     *
     * @param perm    the base permission; must not be null
     * @param subName the sub-name to append; must not be null
     * @return a new {@code PermissionBuilder} instance with the constructed permission name
     */
    @NotNull
    public static PermissionBuilder asSubPermission(final @NotNull Permission perm, final @NotNull String subName) {
        return new PermissionBuilder(perm.getName() + "." + subName);
    }

    /**
     * Creates a new {@code PermissionBuilder} for a module-based permission.
     * <p>
     * The resulting permission name is in the format
     * {@code plugin.getName() + ".module." + moduleId + "." + subName}.
     * </p>
     *
     * @param plugin   the plugin associated with the module; must not be null
     * @param moduleId the module identifier; must not be null
     * @param subName  the sub-name for the permission; must not be null
     * @return a new {@code PermissionBuilder} instance with the constructed permission name
     */
    @NotNull
    public static PermissionBuilder ofModule(final @NotNull Plugin plugin,
                                             final @NotNull String moduleId,
                                             final @NotNull String subName) {
        return new PermissionBuilder(plugin.getName() + ".module." + moduleId + "." + subName);
    }

    /**
     * Creates a new {@code PermissionBuilder} for a module-based permission.
     * <p>
     * The resulting permission name is in the format
     * {@code module.getPlugin().getName() + ".module." + module.getID() + "." + subName}.
     * </p>
     *
     * @param module  the module associated with the permission; must not be null
     * @param subName the sub-name for the permission; must not be null
     * @return a new {@code PermissionBuilder} instance with the constructed permission name
     */
    @NotNull
    public static PermissionBuilder ofModule(final @NotNull Module module, final @NotNull String subName) {
        return new PermissionBuilder(module.getPlugin().getName() + ".module." + module.getID() + "." + subName);
    }

    /**
     * Creates a new {@code PermissionBuilder} for a plugin-based permission.
     * <p>
     * The resulting permission name is in the format {@code plugin.getName() + "." + subName}.
     * </p>
     *
     * @param plugin  the plugin associated with the permission; must not be null
     * @param subName the sub-name for the permission; must not be null
     * @return a new {@code PermissionBuilder} instance with the constructed permission name
     */
    @NotNull
    public static PermissionBuilder ofPlugin(final @NotNull Plugin plugin, final @NotNull String subName) {
        return new PermissionBuilder(plugin.getName() + "." + subName);
    }

    private void setName(final @NotNull String name) {
        if (name.isEmpty() || name.contains(" ") || name.startsWith(".") || name.endsWith("."))
            throw new IllegalArgumentException(name + " is invalid permission name");
        this.name = name.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Sets the description for the permission.
     * <p>
     * If a description is provided, it will be translated to include color codes using {@link ChatColor#translateAlternateColorCodes(char, String)}.
     * </p>
     *
     * @param description the permission description; may be null
     * @return this {@code PermissionBuilder} instance for chaining
     */
    @Contract("_ -> this")
    @NotNull
    public PermissionBuilder setDescription(final @Nullable String description) {
        this.description = description == null ? null : ChatColor.translateAlternateColorCodes('&', description);
        return this;
    }

    /**
     * Sets the default access level for the permission.
     * <p>
     * If {@code def} is null, it defaults to {@link PermissionDefault#OP}.
     * </p>
     *
     * @param def the default access level; may be null
     * @return this {@code PermissionBuilder} instance for chaining
     */
    @Contract("_ -> this")
    @NotNull
    public PermissionBuilder setAccess(final @Nullable PermissionDefault def) {
        this.def = def == null ? PermissionDefault.OP : def;
        return this;
    }

    /**
     * Adds a child permission by its name.
     * <p>
     * <strong>Note:</strong> This method is deprecated in favor of {@link #addChild(Permission, boolean)}.
     * </p>
     *
     * @param name  the child permission name; must not be null
     * @param value the value for the child permission
     * @return this {@code PermissionBuilder} instance for chaining
     * @deprecated Use {@link #addChild(Permission, boolean)} instead.
     */
    @Deprecated
    @Contract("_, _ -> this")
    @NotNull
    public PermissionBuilder addChild(final @NotNull String name, final boolean value) {
        children.put(name.toLowerCase(Locale.ENGLISH), value);
        return this;
    }

    /**
     * Adds a child permission.
     *
     * @param perm  the child {@link Permission}; must not be null
     * @param value the value for the child permission
     * @return this {@code PermissionBuilder} instance for chaining
     */
    @Contract("_, _ -> this")
    @NotNull
    public PermissionBuilder addChild(final @NotNull Permission perm, final boolean value) {
        children.put(perm.getName(), value);
        return this;
    }

    /**
     * Builds the {@link Permission} object based on the configured settings.
     *
     * @return a new {@link Permission} instance with the specified name, description, default access, and children
     */
    @Contract(" -> new")
    @NotNull
    public Permission build() {
        return new Permission(name, description, def, children);
    }

    /**
     * Builds and registers the {@link Permission} with the given {@link CorePlugin}.
     * <p>
     * By default, the registration is done in silent mode (i.e. without console notification).
     * </p>
     *
     * @param plugin the plugin that registers this permission; must not be null
     * @return the newly built and registered {@link Permission} instance
     */
    @Contract("_ -> new")
    @NotNull
    public Permission buildAndRegister(final @NotNull CorePlugin plugin) {
        return buildAndRegister(plugin, true);
    }

    /**
     * Builds and registers the {@link Permission} with the given {@link CorePlugin}.
     * <p>
     * This method allows you to specify whether the registration should be silent.
     * </p>
     *
     * @param plugin the plugin that registers this permission; must not be null
     * @param silent if {@code false}, the console will be notified of the newly generated permission; if {@code true}, it will be silent
     * @return the newly built and registered {@link Permission} instance
     */
    @Contract("_, _ -> new")
    @NotNull
    public Permission buildAndRegister(final @NotNull CorePlugin plugin, final boolean silent) {
        Permission perm = build();
        plugin.registerPermission(perm, silent);
        return perm;
    }
}
