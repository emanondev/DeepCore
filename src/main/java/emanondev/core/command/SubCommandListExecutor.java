package emanondev.core.command;

import emanondev.core.MessageBuilder;
import emanondev.core.UtilsString;
import emanondev.core.YMLSection;
import emanondev.core.util.TriConsumer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class SubCommandListExecutor<T> implements TriConsumer<CommandSender, String, String[]> {

    private final CoreCommandPlus command;
    private final String id;
    private final Supplier<Collection<T>> values;
    private final Function<T, String> names;
    private final BiFunction<T, CommandSender, String> descriptions;

    /**
     * @param command      parent command
     * @param id           the id of this sub command
     * @param values       a supplier to obtain possible values
     * @param names        a function to obtain the name from a value
     * @param descriptions a function to obtain the description from a value
     */
    public SubCommandListExecutor(@NotNull CoreCommandPlus command, @NotNull String id, @NotNull Supplier<Collection<T>> values,
                                  @NotNull Function<T, String> names, @NotNull BiFunction<T, CommandSender, String> descriptions) {
        if (!UtilsString.isLowcasedValidID(id))
            throw new IllegalArgumentException();
        this.command = command;
        this.id = id;
        this.values = values;
        this.names = names;
        this.descriptions = descriptions;
    }

    @Override
    public void consume(CommandSender sender, String alias, String[] args) {
        YMLSection subConfig = command.getConfig().loadSection("subCommands." + id);

        Collection<T> values = this.values.get();
        if (values.isEmpty()) {
            subConfig.getTrackMessage("success.list_no_values", sender);
            return;
        }

        MessageBuilder mBuilder = new MessageBuilder(command.getPlugin(), sender);
        boolean color = true;
        String color1 = subConfig.loadMessage("color_1", "&3");
        String color2 = subConfig.loadMessage("color_2", "&b");
        for (T val : values) {
            mBuilder.addText(color ? color1 : color2);
            color = !color;
            mBuilder.addText(subConfig.getTrackMessage("text", sender), "%name%", names.apply(val))
                    .addHover(descriptions.apply(val, sender), "%name%", names.apply(val))
                    .addSuggestCommand(subConfig.loadMessage("suggest", (String) null, false, sender, "%label% ", alias,
                            "%name%", names.apply(val)));
        }
        mBuilder.send();
    }
}
