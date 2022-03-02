package emanondev.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

public class PermissionBuilder {

	private String name;
	private PermissionDefault def = PermissionDefault.OP;
	private String description;
	private final Map<String, Boolean> soons = new LinkedHashMap<>();

	/**
	 * Build a permission with name plugin.getName()+"."+subName.
	 * @param plugin plugin responsible for this permission
	 * @param subName permission subName
	 */
	public PermissionBuilder(Plugin plugin, String subName) {
		this(plugin.getName()+"."+subName);
	}
	
	/**
	 * Build a permission with name plugin.getName()+".command."+command.getID()+"."+subName.
	 * @param command
	 * @param subname
	 * @return
	 */
	public static PermissionBuilder ofCommand(CoreCommand command,String subname) {
		return new PermissionBuilder(command.getPlugin().getName()+".command."+command.getID()+"."+subname);
	}
	
	public static PermissionBuilder ofCommand(Plugin plugin,String commandName,String subname) {
		return new PermissionBuilder(plugin.getName()+".command."+commandName+"."+subname);
	}

	public static PermissionBuilder ofCommand(Plugin plugin,String commandName) {
		return new PermissionBuilder(plugin.getName()+".command."+commandName);
	}
	public static PermissionBuilder asSubPermission(Permission perm,String subName) {
		return new PermissionBuilder(perm.getName()+"."+subName);
	}

	public static PermissionBuilder ofModule(Plugin plugin,String moduleId,String subname) {
		return new PermissionBuilder(plugin.getName()+".module."+moduleId+"."+subname);
	}
	public static PermissionBuilder ofModule(Module module,String subname) {
		return new PermissionBuilder(module.getPlugin().getName()+".module."+module.getID()+"."+subname);
	}
	public static PermissionBuilder ofPlugin(Plugin plugin,String subname) {
		return new PermissionBuilder(plugin.getName()+"."+subname);
	}

	/**
	 * Build a permission.
	 * @param name permission name
	 */
	public PermissionBuilder(String name) {
		setName(name);
	}
	

	public PermissionBuilder(Permission perm) {
		setName(perm.getName());
		setDescription(perm.getDescription());
		setAccess(perm.getDefault());
		soons.putAll(perm.getChildren());
	}

	private void setName(String name) {
		if (name == null)
			throw new NullPointerException();
		if (name.isEmpty() || name.contains(" ") || name.startsWith(".") || name.endsWith("."))
			throw new IllegalArgumentException(name+" is invalid permission name");
		this.name = name.toLowerCase();
	}

	/**
	 * Set default access to this permission.<br>
	 * By default for OP only
	 * @param def type
	 * @return this for chaining.
	 */
	public PermissionBuilder setAccess(PermissionDefault def) {
		if (def==null)
			def = PermissionDefault.OP;
		this.def = def;
		return this;
	}

	/**
	 * 
	 * @param description permission description
	 * @return this for chaining.
	 */
	public PermissionBuilder setDescription(String description) {
		if (description != null)
			this.description = ChatColor.translateAlternateColorCodes('&', description);
		else
			this.description = null;
		return this;
	}

	/**
	 * Adds a child Permission.
	 * @param name child permission name
	 * @param value value of child permission
	 * @return this for chaining.
	 * @see #addChild(Permission, boolean)
	 */
	@Deprecated
	public PermissionBuilder addChild(String name, boolean value) {
		soons.put(name.toLowerCase(), value);
		return this;
	}
	/**
	 * Adds a child Permission.
	 * @param perm child permission
	 * @param value value of child permission
	 * @return this for chaining.
	 */
	public PermissionBuilder addChild(Permission perm, boolean value) {
		soons.put(perm.getName(), value);
		return this;
	}

	/**
	 * Build Permission.
	 * @return permission obtained from this
	 */
	public Permission build() {
		return new Permission(name.toLowerCase(), description, def, soons);
	}
	
	/**
	 * Build and register Permission.
	 * @param plugin which plugin register this permission
	 * @return permission obtained from this
	 */
	public Permission buildAndRegister(CorePlugin plugin) {
		return buildAndRegister(plugin,true);
	}
	/**
	 * Build and register Permission.
	 * @param plugin which plugin register this permission
	 * @param silent if false notify console of newly generated permission
	 * @return permission obtained from this
	 */
	public Permission buildAndRegister(CorePlugin plugin,boolean silent) {
		Permission perm = new Permission(name.toLowerCase(), description, def, soons);
		plugin.registerPermission(perm,silent);
		return perm;
	}

}
