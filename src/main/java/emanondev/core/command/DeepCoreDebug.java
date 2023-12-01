package emanondev.core.command;

import emanondev.core.CoreMain;
import emanondev.core.CorePlugin;
import emanondev.core.PermissionBuilder;
import emanondev.core.message.DMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DeepCoreDebug extends CoreCommand {
    public DeepCoreDebug() {
        super("deepcoredebug", CoreMain.get(), PermissionBuilder.ofCommand(CoreMain.get(), "deepcoredebug")
                .buildAndRegister(CoreMain.get()), "monitor and toggle debugs");
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        switch (args.length) {
            case 0 -> {
                DMessage msg = new DMessage(getPlugin(), sender).appendLang("command.debug.pre").newLine();
                boolean even = true;
                for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
                    if (!(pl instanceof CorePlugin plugin))
                        continue;
                    msg.append(even ? "<yellow>" : "<aqua>").appendHover(debugList(plugin)
                            , new DMessage(getPlugin(), sender).appendRunCommand("/" + alias + " " + plugin.getName(), plugin.getName()
                            )).append(even ? "</yellow> " : "</aqua> ");
                    even = !even;
                }
                msg.send();
            }
            case 1 -> {
                Plugin pl = Bukkit.getPluginManager().getPlugin(args[0]);
                if (!(pl instanceof CorePlugin plugin)){
                    new DMessage(getPlugin(), sender).appendLang("command.debug.invalid_plugin","%plugin%",args[0]).send();
                    return;
                }
                DMessage msg = new DMessage(getPlugin(), sender).appendLang("command.debug.pre_plugin","%plugin%",plugin.getName()).newLine();
                List<String> list = debugList(plugin);
                List<String> options = new ArrayList<>(getOptions(plugin));

                for (int i = 0 ; i < options.size();i++)
                    msg.appendHover("<gold>Click to toggle</gold>",new DMessage(getPlugin(), sender)
                            .appendRunCommand("/"+alias+" "+plugin.getName()+" "+options.get(i),list.get(i))).newLine();
                msg.send();
            }
            case 2 -> {
                Plugin pl = Bukkit.getPluginManager().getPlugin(args[0]);
                if (!(pl instanceof CorePlugin plugin)){
                    new DMessage(getPlugin(), sender).appendLang("command.debug.invalid_plugin","%plugin%",args[0]).send();
                    return;
                }
                plugin.setDebug(args[1].toLowerCase(Locale.ENGLISH),!plugin.debug(args[1].toLowerCase(Locale.ENGLISH)));

                DMessage msg = new DMessage(getPlugin(), sender).appendLang("command.debug.pre_plugin","%plugin%",plugin.getName()).newLine();
                List<String> list = debugList(plugin);
                List<String> options = new ArrayList<>(getOptions(plugin));

                for (int i = 0 ; i < options.size();i++)
                    msg.appendHover("<gold>Click to toggle</gold>",new DMessage(getPlugin(), sender)
                            .appendRunCommand("/"+alias+" "+plugin.getName()+" "+options.get(i),list.get(i))).newLine();
                msg.send();
            }
            default -> new DMessage(getPlugin(), sender).appendLang("command.debug.usage","%alias%",alias).send();
        }
    }

    private List<String> debugList(CorePlugin plugin) {
        ArrayList<String> lines = new ArrayList<>();
        for (String option : getOptions(plugin))
            lines.add("<gold>" + option + ":</gold> " + (plugin.debug(option) ? "<blue>on</blue>" : "<yellow>off</yellow>"));
        if (lines.isEmpty())
            lines.add("<red>No debug found</red>");
        return lines;
    }

    @Override
    public @Nullable List<String> onComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location loc) {
        return switch (args.length) {
            case 1 -> complete(args[0], List.of(Bukkit.getPluginManager().getPlugins()), (Plugin::getName), (p -> p instanceof CorePlugin));
            case 2 -> {
                Plugin pl = Bukkit.getPluginManager().getPlugin(args[0]);
                if (!(pl instanceof CorePlugin plugin))
                    yield Collections.emptyList();
                yield complete(args[1], getOptions(plugin));
            }
            default -> Collections.emptyList();
        };
    }

    private Set<String> getOptions(CorePlugin plugin) {
        return plugin.getConfig().getKeys("debug");
    }
}
