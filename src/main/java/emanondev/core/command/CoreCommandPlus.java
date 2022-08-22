package emanondev.core.command;

import emanondev.core.*;
import emanondev.core.util.TriConsumer;
import emanondev.core.util.TriFunction;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class CoreCommandPlus extends CoreCommand {

    /**
     * Construct a new Command. Note: id is used for both config file of the command
     * and default command name.
     *
     * @param id         Command ID.
     * @param plugin     Plugin that own this Command.
     * @param permission Permission required to use this Command if exists.
     */
    public CoreCommandPlus(@NotNull String id, @NotNull CorePlugin plugin, @Nullable Permission permission) {
        this(id, plugin, permission, null, null);
    }

    /**
     * Construct a new Command. Note: id is used for both config file of the command
     * and default command name.
     *
     * @param id                 Command ID.
     * @param plugin             Plugin that own this Command.
     * @param permission         Permission required to use this Command if exists.
     * @param defaultDescription Default description of the Command, might be updated on Command
     *                           file.
     */
    public CoreCommandPlus(@NotNull String id, @NotNull CorePlugin plugin, @Nullable Permission permission,
                           @Nullable String defaultDescription) {
        this(id, plugin, permission, defaultDescription, null);
    }

    /**
     * Construct a new Command. Note: id is used for both config file of the command
     * and default command name.
     *
     * @param id                 Command ID.
     * @param plugin             Plugin that own this Command.
     * @param permission         Permission required to use this Command if exists.
     * @param defaultDescription Default description of the Command, might be updated on Command
     *                           file.
     * @param defaultAliases     Default aliases of the Command, might be changed on Command file
     *                           and applied with Server restart or Plugin restart.
     */
    public CoreCommandPlus(@NotNull String id, @NotNull CorePlugin plugin, @Nullable Permission permission,
                           @Nullable String defaultDescription, @Nullable List<String> defaultAliases) {
        super(id, plugin, permission, defaultDescription, defaultAliases);
    }

    /**
     * Add a subCommand
     * a permission is generated as default from command permission plus the id
     *
     * @param id default id of the sub command
     * @param exec consumer responsible to handle the command, arguments are sender, alias, arguments
     * @param tab function responsible to return completable results, arguments are sender, alias, arguments
     */
    public void addSubCommandHandler(@NotNull String id, @NotNull TriConsumer<CommandSender, String, String[]> exec,
                                        @Nullable TriFunction<CommandSender, String, String[], List<String>> tab) {
        addSubCommandHandler(id, exec, tab, getCommandPermission() == null ? null : PermissionBuilder.asSubPermission(getCommandPermission(), id).buildAndRegister(getPlugin()));
    }

    /**
     * Add a subCommand
     *
     * @param id default id of the sub command
     * @param exec consumer responsible to handle the command, arguments are sender, alias, arguments
     * @param tab function responsible to return completable results, arguments are sender, alias, arguments
     * @param perm the permission required to sender to execute or tab this sub command
     */
    public void addSubCommandHandler(@NotNull String id, @NotNull TriConsumer<CommandSender, String, String[]> exec,
                                        @Nullable TriFunction<CommandSender, String, String[], List<String>> tab, @Nullable Permission perm) {
        if (!UtilsString.isValidID(id)) {
            this.logProblem("invalid sub command id &e" + id + "&f, sub command not registered");
            return;
        }
        id = id.toLowerCase();
        ids.add(id);
        if (perm != null)
            subPerms.put(id, perm);
        subExecutors.put(id, exec);
        if (tab!=null)
            subCompleter.put(id, tab);
        this.getConfig().loadInteger("subCommands." + id + ".display_order", ids.size() + 1);
        ids.sort(Comparator.comparingInt(commandId -> this.getConfig().loadInteger("subCommands." + commandId + ".display_order", 1)));

        String nick = this.getConfig().loadString("subCommands." + id + ".nick", id);
        if (!UtilsString.isValidID(nick)) {
            this.logProblem("invalid sub command nick &e" + nick + "&f, sub command (&e" + id + "&f) skipped (check &e/plugins/" + getPlugin().getName() + "/Commands/" + getID() + ".yml&f on &esubCommands." + id + ".nick&f and reload the plugin)");
            return;
        }
        nick = nick.toLowerCase();
        if (subNicks.containsKey(nick))
            this.logProblem("already used sub command nick &e" + nick + "&f, sub command (&e" + id + "&f) skipped (check &e/plugins/" + getPlugin().getName() + "/Commands/" + getID() + ".yml&f on &esubCommands." + id + ".nick&f and reload the plugin)");

    }

    private final HashMap<String, TriConsumer<CommandSender, String, String[]>> subExecutors = new HashMap<>();
    private final HashMap<String, TriFunction<CommandSender, String, String[], List<String>>> subCompleter = new HashMap<>();
    private final HashMap<String, Permission> subPerms = new HashMap<>();
    private final HashMap<String, String> subNicks = new HashMap<>();
    private final List<String> ids = new ArrayList<>();

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length != 0) {
            String id = subNicks.get(args[0].toLowerCase());
            if (id != null) {
                if (subPerms.get(id) == null || sender.hasPermission(subPerms.get(id)))
                    subExecutors.get(id).consume(sender, alias, args);
                else
                    permissionLackNotify(sender, subPerms.get(id));
                return;
            }
        }
        onHelp(sender, alias, args);
    }

    //TODO may make paged help
    public void onHelp(CommandSender sender, String alias, String[] args) {
        YMLSection lang = getLanguageSection(sender).loadSection("onHelp");
        ComponentBuilder comp = new ComponentBuilder();
        comp.append(lang.getTrackMessage("prefix", sender, "%alias%", alias));
        ids.forEach(id -> {
            if (subPerms.get(id) == null || sender.hasPermission(subPerms.get(id))) {
                String nick = this.getConfig().loadString("subCommands." + id + ".nick", id);
                if (nick == null)
                    return;
                nick = nick.toLowerCase();
                if (!subNicks.get(nick.toLowerCase()).equals(id))
                    return;

                String msg = lang.getTrackMessage(id + "_message", sender, "%alias%", alias, "%sub_name%", nick);
                if (!msg.isEmpty()) {
                    comp.retain(ComponentBuilder.FormatRetention.NONE).append(msg);
                    msg = String.join("\n", lang.getTrackMultiMessage(id + "_description", sender,
                            "%alias%", alias, "%sub_name%", nick));
                    if (!msg.isEmpty()) {
                        comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(msg)));
                        msg = lang.getMessage(id + "_click", sender, "%alias%", alias, "%sub_name%", nick);
                        if (msg != null && !msg.isEmpty())
                            comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, msg));
                    }
                }

            }
        });
        comp.retain(ComponentBuilder.FormatRetention.NONE).append(lang.getTrackMessage("postfix", sender, "%alias%", alias));
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location loc) {
        if (args.length > 1) {
            String id = subNicks.get(args[0].toLowerCase());
            if (id != null)
                if (subPerms.get(id) == null || sender.hasPermission(subPerms.get(id)))
                    return subCompleter.get(id)==null?Collections.emptyList():subCompleter.get(id).apply(sender, alias, args);
                else
                    return Collections.emptyList();
            return Collections.emptyList();
        }
        return this.complete(args[0], subNicks.keySet(), (Predicate<String>) (val) -> {
            Permission perm = subPerms.get(subNicks.get(val));
            return perm == null || sender.hasPermission(perm);
        });
    }

    @Override
    public void reload() {
        subNicks.clear();
        ids.forEach(id -> {
            this.getConfig().loadInteger("subCommands." + id + ".display_order", ids.size() + 1);
            ids.sort(Comparator.comparingInt(commandId -> this.getConfig().loadInteger("subCommands." + commandId + ".display_order", 1)));
            String nick = this.getConfig().loadString("subCommands." + id + ".nick", id);
            if (!UtilsString.isValidID(nick)) {
                this.logProblem("invalid sub command nick &e" + nick + "&f, sub command (&e" + id + "&f) skipped (check &e/plugins/" + getPlugin().getName() + "/Commands/" + getID() + ".yml&f on &esubCommands." + id + ".nick&f and reload the plugin)");
                return;
            }
            nick = nick.toLowerCase();
            if (subNicks.containsKey(nick))
                this.logProblem("already used sub command nick &e" + nick + "&f, sub command (&e" + id + "&f) skipped (check &e/plugins/" + getPlugin().getName() + "/Commands/" + getID() + ".yml&f on &esubCommands." + id + ".nick&f and reload the plugin)");
        });
    }
}
