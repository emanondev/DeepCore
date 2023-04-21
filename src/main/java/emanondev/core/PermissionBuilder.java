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

public class PermissionBuilder {

    private final Map<String, Boolean> children = new LinkedHashMap<>();
    private String name;
    private PermissionDefault def = PermissionDefault.OP;
    private String description;

    /**
     * Build a permission with name {@code plugin.getName()+"."+subName} .
     *
     * @param plugin  plugin responsible for this permission
     * @param subName permission subName
     */
    public PermissionBuilder(@NotNull Plugin plugin, @NotNull String subName) {
        this(plugin.getName() + "." + subName);
    }

    /**
     * Build a permission.
     *
     * @param name permission name
     */
    public PermissionBuilder(@NotNull String name) {
        setName(name);
    }

    /**
     * @param perm base permission
     */
    public PermissionBuilder(@NotNull Permission perm) {
        setName(perm.getName());
        setDescription(perm.getDescription());
        setAccess(perm.getDefault());
        children.putAll(perm.getChildren());
    }

    /**
     * Build a permission with name  {@code command.getPlugin().getName()+".command."+command.getID()+"."+subName} .
     *
     * @param command command
     * @param subName sub-path
     * @return a permission builder
     */
    @NotNull
    public static PermissionBuilder ofCommand(@NotNull CoreCommand command, @NotNull String subName) {
        return new PermissionBuilder(command.getPlugin().getName() + ".command." + command.getID() + "." + subName);
    }

    /**
     * Build a permission with name  {@code plugin.getName()+".command."+commandName+"."+subName} .
     *
     * @param plugin      plugin
     * @param commandName command name
     * @param subName     sub-path
     * @return a permission builder
     */
    @NotNull
    public static PermissionBuilder ofCommand(@NotNull Plugin plugin, @NotNull String commandName, @NotNull String subName) {
        return new PermissionBuilder(plugin.getName() + ".command." + commandName + "." + subName);
    }

    /**
     * Build a permission with name  {@code plugin.getName()+".command."+commandName} .
     *
     * @param plugin      plugin
     * @param commandName command name
     * @return a permission builder
     */
    @NotNull
    public static PermissionBuilder ofCommand(@NotNull Plugin plugin, @NotNull String commandName) {
        return new PermissionBuilder(plugin.getName() + ".command." + commandName);
    }

    @NotNull
    public static PermissionBuilder asSubPermission(@NotNull Permission perm, @NotNull String subName) {
        return new PermissionBuilder(perm.getName() + "." + subName);
    }

    @NotNull
    public static PermissionBuilder ofModule(@NotNull Plugin plugin, @NotNull String moduleId, @NotNull String subName) {
        return new PermissionBuilder(plugin.getName() + ".module." + moduleId + "." + subName);
    }

    @NotNull
    public static PermissionBuilder ofModule(@NotNull Module module, @NotNull String subName) {
        return new PermissionBuilder(module.getPlugin().getName() + ".module." + module.getID() + "." + subName);
    }

    @NotNull
    public static PermissionBuilder ofPlugin(@NotNull Plugin plugin, @NotNull String subName) {
        return new PermissionBuilder(plugin.getName() + "." + subName);
    }

    private void setName(@NotNull String name) {
        if (name.isEmpty() || name.contains(" ") || name.startsWith(".") || name.endsWith("."))
            throw new IllegalArgumentException(name + " is invalid permission name");
        this.name = name.toLowerCase(Locale.ENGLISH);
    }

    /**
     * @param description permission description
     * @return this for chaining.
     */
    @Contract("_ -> this")
    @NotNull
    public PermissionBuilder setDescription(String description) {
        this.description = description == null ? null : ChatColor.translateAlternateColorCodes('&', description);
        return this;
    }

    /**
     * Set default access to this permission.<br>
     * If not specified for OP only
     *
     * @param def type
     * @return this for chaining.
     */
    @Contract("_ -> this")
    @NotNull
    public PermissionBuilder setAccess(@Nullable PermissionDefault def) {
        if (def == null)
            def = PermissionDefault.OP;
        this.def = def;
        return this;
    }

    /**
     * Adds a child Permission.
     *
     * @param name  child permission name
     * @param value value of child permission
     * @return this for chaining.
     * @see #addChild(Permission, boolean)
     */
    @Deprecated
    @Contract("_, _ -> this")
    @NotNull
    public PermissionBuilder addChild(String name, boolean value) {
        children.put(name.toLowerCase(Locale.ENGLISH), value);
        return this;
    }

    /**
     * Adds a child Permission.
     *
     * @param perm  child permission
     * @param value value of child permission
     * @return this for chaining.
     */
    @Contract("_, _ -> this")
    @NotNull
    public PermissionBuilder addChild(Permission perm, boolean value) {
        children.put(perm.getName(), value);
        return this;
    }

    /**
     * Build Permission.
     *
     * @return permission obtained from this
     */
    @Contract(" -> new")
    @NotNull
    public Permission build() {
        return new Permission(name.toLowerCase(Locale.ENGLISH), description, def, children);
    }

    /**
     * Build and register Permission.
     *
     * @param plugin which plugin register this permission
     * @return permission obtained from this
     */
    @Contract("_ -> new")
    @NotNull
    public Permission buildAndRegister(@NotNull CorePlugin plugin) {
        return buildAndRegister(plugin, true);
    }

    /**
     * Build and register Permission.
     *
     * @param plugin which plugin register this permission
     * @param silent if false notify console of newly generated permission
     * @return permission obtained from this
     */
    @Contract("_, _ -> new")
    @NotNull
    public Permission buildAndRegister(@NotNull CorePlugin plugin, boolean silent) {
        Permission perm = new Permission(name.toLowerCase(Locale.ENGLISH), description, def, children);
        plugin.registerPermission(perm, silent);
        return perm;
    }

}
