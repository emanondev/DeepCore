package emanondev.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends CoreCommand {

    public ReloadCommand(CorePlugin plugin) {
        super(plugin.getName() + "reload", plugin,
                PermissionBuilder.ofCommand(plugin,"reload").setDescription("Allows to reload " + plugin.getName()).build(),
                "reload " + plugin.getName(), null);
        getPlugin().registerPermission(getCommandPermission());
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        try {
            getPlugin().onReload();
            UtilsMessages.sendMessage(sender, CoreMain.get().getLanguageConfig(sender)
                    .loadMessage("command.reload.success", "&9[&f%plugin%&9]&a reloaded", true,
                            "%plugin%",getPlugin().getName()));
        } catch (Exception e) {
            e.printStackTrace();
            UtilsMessages.sendMessage(sender, CoreMain.get().getLanguageConfig(sender)
                    .loadMessage("command.reload.fail", "&4[&c%plugin%&4]&c reload failed", true,
                            "%plugin%",getPlugin().getName()));
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args, Location loc) {
        return Collections.emptyList();
    }
}
