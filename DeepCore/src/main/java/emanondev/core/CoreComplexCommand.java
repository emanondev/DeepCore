package emanondev.core;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Function;


import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

@Deprecated
public abstract class CoreComplexCommand extends CoreCommand implements ComplexCommandComponent {

	public CoreComplexCommand(@NotNull String id, @NotNull CorePlugin plugin, @Nullable Permission permission,
			@Nullable String defaultDescription, @Nullable List<String> defaultAliases) {
		super(id, plugin, permission, defaultDescription, defaultAliases);
	}

	@Override
	public final void onExecute(CommandSender sender, String alias, String[] args) {
		onExecute(sender, alias, args, new ArrayList<String>(Arrays.asList(args)));

	}

	public void onExecute(CommandSender sender, String alias, String[] args, ArrayList<String> params) {
		if (this.getPermission() != null && !sender.hasPermission(getPermission())) {
			this.permissionLackNotify(sender, this.getCommandPermission());
			return;
		}
		if (params.size() == 0) {
			help(sender, alias, args, params);
			return;
		}
		CoreComplexSubCommand sub = subCommands.get(params.get(0).toLowerCase());
		if (sub == null) {
			help(sender, alias, args, params);
			return;
		}
		params.remove(0);
		if (sub.getPermission() != null && !sender.hasPermission(sub.getPermission())) {
			this.permissionLackNotify(sender, sub.getPermission());
			return;
		}
		if (sub.isPlayerOnly() && !(sender instanceof Player)) {
			this.playerOnlyNotify(sender);
			return;
		}
		sub.onExecute(sender, alias, args, params);

	}

	@Override
	public final List<String> onComplete(CommandSender sender, String alias, String[] args, Location loc) {
		ArrayList<String> params = new ArrayList<>();
		params.addAll(Arrays.asList(args));
		return this.onComplete(sender, alias, args, loc, params);
	}

	public List<String> onComplete(CommandSender sender, String alias, String[] args, Location loc,
			ArrayList<String> params) {
		if (params.size() == 1)
			return this.complete(params.get(0), subCommands.values(), new Function<CoreComplexSubCommand, String>() {

				@Override
				public String apply(CoreComplexSubCommand t) {
					return t.getName();
				}

			}, new Predicate<CoreComplexSubCommand>() {

				@Override
				public boolean test(CoreComplexSubCommand t) {
					if (t.getPermission() != null && !sender.hasPermission(t.getPermission()))
						return false;
					if (t.isPlayerOnly() && !(sender instanceof Player))
						return false;
					return true;
				}
			});

		else {
			CoreComplexSubCommand sub = subCommands.get(args[0].toLowerCase());
			if (sub == null)
				return new ArrayList<>();
			params.remove(0);
			return sub.onComplete(sender, alias, args, loc, params);
		}
	}

	protected void addSubCommand(CoreComplexSubCommand sub) {
		Validate.isTrue(!subCommands.containsKey(sub.getName()));
		subCommands.put(sub.getName(), sub);
	}

	private final LinkedHashMap<String, CoreComplexSubCommand> subCommands = new LinkedHashMap<>();

	@Override
	public CoreComplexCommand getCommand() {
		return this;
	}

	@Override
	public String getConfigPath() {
		return "command." + this.getID();
	}

	public boolean getShowLockedSubCommands() {
		return getConfig().loadBoolean("show-locked-sub-commands", defaultShowLockedSubCommands);
	}

	private boolean defaultShowLockedSubCommands = true;

	protected void setDefaultShowLockedSubCommands(boolean value) {
		this.defaultShowLockedSubCommands = value;
	}

	private String defaultParams = null;

	protected void setDefaultParams(String params) {
		this.defaultParams = params;
	}

	/**
	 * example: <br>
	 * "/pay &#60;player&#62; &#60;amount&#62;" <br>
	 * params = "&#60;player&#62; &#60;amount&#62;"
	 * @param sender who
	 */
	public String getParams(CommandSender sender) {
		return getCommand().loadLanguageMessage(sender, getConfigPath() + ".params", defaultParams);
	}

	/**
	 * send info abouth subcommands to sender if he has permission to use them
	 * @param s
	 */
	public void help(CommandSender s, String alias, String[] args, ArrayList<String> params){
		StringBuilder usedParams = new StringBuilder(alias+" ");
		for (int i =0; i<args.length-params.size();i++)
			usedParams.append(args[i]+" ");
		String helpTopLine = getHelpTopLine(s);
		ComponentBuilder helpText;
		if (helpTopLine==null||helpTopLine.isEmpty()) {
			helpText = new ComponentBuilder(
				ChatColor.BLUE.toString()
				+usedParams+(getParams(s)==null?(ChatColor.YELLOW+getParams(s)+" "+ChatColor.BLUE):"")+"[...]\n");
		}
		else		
			helpText = new ComponentBuilder(helpTopLine+"\n"+ChatColor.BLUE.toString()
				+usedParams+(getParams(s)==null?(ChatColor.YELLOW+getParams(s)+" "+ChatColor.BLUE):"")+"[...]\n");
		boolean c = false;
		for (CoreComplexSubCommand sub:subCommands.values()){
			if (sub.getPermission()!=null && !s.hasPermission(sub.getPermission())&&!getShowLockedSubCommands()) {
				boolean allowed = sub.getCanUsePredicate(s).test(sub);
				BaseComponent[] subHelp = sub.craftHelp(s,usedParams.toString(),allowed?ChatColor.GREEN:ChatColor.RED,allowed?ChatColor.YELLOW:ChatColor.GOLD);
				if (subHelp!=null) {
					helpText.append(subHelp);
					helpText.append("\n");
				}
				c = true;
			}
		}
		if (c==true) {
			String helpBottomLine = getHelpBottomLine(s);
			if (helpBottomLine!=null && !helpBottomLine.isEmpty())
				helpText.append(helpBottomLine);
			s.spigot().sendMessage(helpText.create());
		}
		else
			this.sendLanguageMessage(s, this.getConfigPath()+".usage", "&cUsage /"+getConfigPath().substring(getConfigPath().indexOf(".")).replace(".", " ")+(getParams(s)==null?(ChatColor.GOLD+getParams(s)):""));
	}

	private String getHelpTopLine(CommandSender s) {
		return this.getCommand().loadLanguageMessage(s, getCommand().getConfigPath() + ".help-top-line",
				"&9<-> Help <->");
	}

	private String getHelpBottomLine(CommandSender s) {
		return this.getCommand().loadLanguageMessage(s, getCommand().getConfigPath() + ".help-bottom-line",
				"&9<-> Help <->");
	}

}
