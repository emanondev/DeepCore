package emanondev.core;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;

public abstract class CoreComplexSubCommand implements ComplexCommandComponent {
	private final String name;
	private ComplexCommandComponent parent;
	private String defaultDescription = null;
	private String defaultParams = null;

	public CoreComplexSubCommand(ComplexCommandComponent parent, String name, Permission permission) {
		Validate.notNull(name);
		Validate.isTrue(!name.isEmpty() && !name.contains(" "));
		this.name = name.toLowerCase();
		this.permission = permission;
	}

	protected void setDefaultDescription(String description) {
		this.defaultDescription = description;
	}
	protected void setDefaultParams(String params) {
		this.defaultParams = params;
	}

	public CoreComplexCommand getCommand() {
		return parent.getCommand();

	}

	public String getName() {
		return name;
	}

	private final Permission permission;

	public Permission getPermission() {
		return permission;
	}

	public List<String> onComplete(CommandSender sender, String alias, String[] args, Location loc,
			ArrayList<String> params) {
		if (params.size() == 1)
			return this.getCommand().complete(params.get(0), subCommands.values(),
					new Function<CoreComplexSubCommand, String>() {

						@Override
						public String apply(CoreComplexSubCommand t) {
							return t.getName();
						}

					},getCanUsePredicate(sender) );

		else {
			CoreComplexSubCommand sub = subCommands.get(args[0].toLowerCase());
			if (sub == null)
				return new ArrayList<>();
			params.remove(0);
			return sub.onComplete(sender, alias, args, loc, params);
		}
	}

	private final LinkedHashMap<String, CoreComplexSubCommand> subCommands = new LinkedHashMap<>();

	private boolean playerOnly = false;

	public boolean isPlayerOnly() {
		return playerOnly;
	}

	protected void setPlayerOnly(boolean value) {
		this.playerOnly = value;
	}

	public void onExecute(CommandSender sender, String alias, String[] args, ArrayList<String> params) {
		if (this.getPermission() != null && !sender.hasPermission(getPermission())) {
			this.getCommand().permissionLackNotify(sender, this.getPermission());
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
			this.getCommand().permissionLackNotify(sender, sub.getPermission());
			return;
		}
		if (sub.isPlayerOnly() && !(sender instanceof Player)) {
			this.getCommand().playerOnlyNotify(sender);
			return;
		}
		sub.onExecute(sender, alias, args, params);

	}
	
	
	protected BaseComponent[] craftHelp(CommandSender sender,String commandSuggest,
			ChatColor mainColor,ChatColor paramColor) {
		ComponentBuilder base;
		String params = getParams(sender);
		if (params == null || params.isEmpty())
			base = new ComponentBuilder(mainColor+"[...] " +name);
		else 
			base = new ComponentBuilder(mainColor+"[...] " +name
			+paramColor+" "+params);
		
		base.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/"+commandSuggest + " "+name));
		String description = getDescription(sender);
		if (description!=null && !description.isEmpty())
			base.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new Text(getDescription(sender))));
		return base.create();
	}
	

	public boolean getShowLockedSubCommands() {
		return getCommand().getShowLockedSubCommands();
				
				//loadLanguageMessage(sender, getConfigPath()+".description", defaultDescription);
	}
	/**
	 * description
	 */
	public String getDescription(CommandSender sender) {
		return getCommand().loadMessage(sender, getConfigPath()+".description", defaultDescription);
	}
	/**
	 * example:
	 * <br> "/pay &#60;player&#62; &#60;amount&#62;"
	 * <br> params = "&#60;player&#62; &#60;amount&#62;"
	 */
	public String getParams(CommandSender sender) {
		return getCommand().loadMessage(sender, getConfigPath()+".params", defaultParams);
	}
	
	public String getConfigPath() {
		return parent.getConfigPath()+"."+name;
	}


	/**
	 * send info abouth subcommands to sender if he has permission to use them
	 * @param target Target of the help
	 */
	public void help(CommandSender target, String alias, String[] args, ArrayList<String> params){
		this.getCommand().sendMessageFeedback(target, this.getConfigPath()+".usage", "&cUsage /"+getConfigPath().substring(getConfigPath().indexOf(".")).replace(".", " ")+(getParams(target)==null?(ChatColor.GOLD+getParams(target)):""));
		StringBuilder usedParams = new StringBuilder(alias+" ");
		for (int i =0; i<args.length-params.size();i++)
			usedParams.append(args[i]+" ");
		String helpTopLine = getHelpTopLine(target);
		ComponentBuilder helpText;
		if (helpTopLine==null||helpTopLine.isEmpty()) {
			helpText = new ComponentBuilder(
				ChatColor.BLUE.toString()
				+usedParams+(getParams(target)==null?(ChatColor.YELLOW+getParams(target)+" "+ChatColor.BLUE):"")+"[...]\n");
		}
		else		
			helpText = new ComponentBuilder(helpTopLine+"\n"+ChatColor.BLUE.toString()
				+usedParams+(getParams(target)==null?(ChatColor.YELLOW+getParams(target)+" "+ChatColor.BLUE):"")+"[...]\n");
		boolean c = false;
		for (CoreComplexSubCommand sub:subCommands.values()){
			if (sub.getPermission()!=null && !target.hasPermission(sub.getPermission())&&!getShowLockedSubCommands()) {
				boolean allowed = sub.getCanUsePredicate(target).test(sub);
				BaseComponent[] subHelp = sub.craftHelp(target,usedParams.toString(),allowed?ChatColor.GREEN:ChatColor.RED,allowed?ChatColor.YELLOW:ChatColor.GOLD);
				if (subHelp!=null) {
					helpText.append(subHelp);
					helpText.append("\n");
				}
				c = true;
			}
		}
		if (c==true) {
			String helpBottomLine = getHelpBottomLine(target);
			if (helpBottomLine!=null && !helpBottomLine.isEmpty())
				helpText.append(helpBottomLine);
			target.spigot().sendMessage(helpText.create());
		}
		else
			this.getCommand().sendMessageFeedback(target, this.getConfigPath()+".usage", "&cUsage /"+getConfigPath().substring(getConfigPath().indexOf(".")).replace(".", " ")+(getParams(target)==null?(ChatColor.GOLD+getParams(target)):""));
	}/*
	public void help(CommandSender s, String alias, String[] args, ArrayList<String> params){
		StringBuilder usedParams = new StringBuilder(alias+" ");
		for (int i =0; i<args.length-params.size();i++)
			usedParams.append(args[i]+" ");
		String helpTopLine = getHelpTopLine(s);
		ComponentBuilder helpText;
		if (helpTopLine==null||helpTopLine.isEmpty()) {
			helpText = new ComponentBuilder(
					ChatColor.DARK_AQUA.toString()+ChatColor.BOLD
					+usedParams+" [...]\n");
		}
		else		
				helpText = new ComponentBuilder(helpTopLine+"\n"+ChatColor.DARK_AQUA.toString()+ChatColor.BOLD
				+usedParams+" [...]\n");
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
			s.sendMessage();
	}*/

	private String getHelpTopLine(CommandSender s) {
		return this.getCommand().loadMessage(s, getCommand().getConfigPath()+".help-top-line", "&9<-> Help <->");
	}
	private String getHelpBottomLine(CommandSender s) {
		return this.getCommand().loadMessage(s, getCommand().getConfigPath()+".help-bottom-line", "&9<-> Help <->");
	}
	
	public Predicate<CoreComplexSubCommand> getCanUsePredicate(CommandSender sender) {
		return new Predicate<CoreComplexSubCommand>() {

			@Override
			public boolean test(CoreComplexSubCommand t) {
				if (t.getPermission() != null && !sender.hasPermission(t.getPermission()))
					return false;
				if (t.isPlayerOnly() && !(sender instanceof Player))
					return false;
				return true;
			}
		};
	}
}
