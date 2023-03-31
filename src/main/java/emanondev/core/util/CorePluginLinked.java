package emanondev.core.util;

import emanondev.core.CorePlugin;
import emanondev.core.message.DMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface CorePluginLinked {

    @NotNull CorePlugin getPlugin();

    default DMessage getDMessage(@NotNull CommandSender sender, @NotNull String path, String... placeholders){
        return new DMessage(getPlugin(),sender).appendLang(path,placeholders);
    }

    default void sendDMessage(@NotNull CommandSender sender, @NotNull String path, String... placeholders){
        getDMessage(sender,path,placeholders).send();
    }
}
