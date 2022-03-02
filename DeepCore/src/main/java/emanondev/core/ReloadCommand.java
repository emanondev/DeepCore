package emanondev.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends CoreCommand {

	public ReloadCommand(CorePlugin plugin) {
		super(plugin.getName() + "reload", plugin,
				new PermissionBuilder(plugin.getName() + ".reload").setDescription("Allows to reload " + plugin.getName()).build(),
				 "reload " + plugin.getName(), null);
		getPlugin().registerPermission(getCommandPermission());
	}
	

	@Override
	public void onExecute(@NotNull CommandSender sender,@NotNull String alias,@NotNull String[] args) {
		try{
			getPlugin().onReload();
			UtilsMessages.sendMessage(sender, getConfig().loadMessage("message.reload-success", "&2[&a"+getPlugin().getName()+"&2]&a reloaded",  true));
			
		} catch (Exception e) {
			UtilsMessages.sendMessage(sender, getConfig().loadMessage("message.reload-fail", "&4[&c"+getPlugin().getName()+"&4]&c reload failed", true));
		}
	}

	@Override
	public List<String> onComplete(CommandSender sender, String alias, String[] args, Location loc) {
		return new ArrayList<String>();
	}


}
