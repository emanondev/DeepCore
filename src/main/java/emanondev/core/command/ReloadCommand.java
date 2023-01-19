package emanondev.core.command;

import emanondev.core.CoreMain;
import emanondev.core.CorePlugin;
import emanondev.core.PermissionBuilder;
import emanondev.core.message.MessageComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends CoreCommand {

    /**
     * Create a command for the plugin to call reload()
     * the command is named by default /{pluginname}reload
     * required permission for the command is {pluginname}.command.reload
     *
     * @param plugin the plugin
     */
    public ReloadCommand(@NotNull CorePlugin plugin) {
        super(plugin.getName() + "reload", plugin,
                PermissionBuilder.ofCommand(plugin, "reload").setDescription("Allows to reload " + plugin.getName()).build(),
                "reload " + plugin.getName(), null);
        assert getCommandPermission() != null; //<pluginname>.command.reload
        getPlugin().registerPermission(getCommandPermission());
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        try {
            getPlugin().onReload();
            new MessageComponent(CoreMain.get(), sender).appendConfigurable(
                    "command.reload.success", "",
                    "%plugin%", getPlugin().getName()).send();
        } catch (Exception e) {
            e.printStackTrace();
            new MessageComponent(CoreMain.get(), sender).appendConfigurable(
                    "command.reload.fail", "",
                    "%plugin%", getPlugin().getName()).send();
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args, Location loc) {
        return Collections.emptyList();
    }
}
