package emanondev.core.util;

import emanondev.core.CorePlugin;
import emanondev.core.message.DMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public interface CorePluginLinked {

    @NotNull CorePlugin getPlugin();

    default DMessage getDMessage(@NotNull CommandSender sender, @NotNull String path, String... placeholders) {
        return new DMessage(getPlugin(), sender).appendLang(path, placeholders);
    }

    default void sendDMessage(@NotNull CommandSender sender, @NotNull String path, String... placeholders) {
        getDMessage(sender, path, placeholders).send();
    }

    default <T extends CommandSender> void sendDMessage(@NotNull Collection<T> targets, @NotNull String path, String... placeholders) {
        targets.forEach((t) -> sendDMessage(t, path, placeholders));
    }

    default <T extends CommandSender> void sendDMessage(@NotNull Collection<T> targets, @NotNull String path, @NotNull Predicate<T> shouldSend, String... placeholders) {
        targets.forEach((t) -> {
            if (shouldSend.test(t)) sendDMessage(t, path, placeholders);
        });
    }

    default <T extends CommandSender> void sendDMessage(@NotNull Collection<T> targets, @NotNull String path, Function<T, String[]> getPlaceholders) {
        targets.forEach((t) -> sendDMessage(t, path, getPlaceholders.apply(t)));
    }

    default <T extends CommandSender> void sendDMessage(@NotNull Collection<T> targets, @NotNull String path, @NotNull Predicate<T> shouldSend, Function<T, String[]> getPlaceholders) {
        targets.forEach((t) -> {
            if (shouldSend.test(t)) sendDMessage(t, path, getPlaceholders.apply(t));
        });
    }

    default void sendActionBarDMessage(@NotNull CommandSender sender, @NotNull String path, String... placeholders) {
        getDMessage(sender, path, placeholders).sendActionBar();
    }

    default <T extends CommandSender> void sendActionBarDMessage(@NotNull Collection<T> targets, @NotNull String path, String... placeholders) {
        targets.forEach((t) -> sendActionBarDMessage(t, path, placeholders));
    }

    default <T extends CommandSender> void sendActionBarDMessage(@NotNull Collection<T> targets, @NotNull String path, @NotNull Predicate<T> shouldSend, String... placeholders) {
        targets.forEach((t) -> {
            if (shouldSend.test(t)) sendActionBarDMessage(t, path, placeholders);
        });
    }

    default <T extends CommandSender> void sendActionBarDMessage(@NotNull Collection<T> targets, @NotNull String path, Function<T, String[]> getPlaceholders) {
        targets.forEach((t) -> sendActionBarDMessage(t, path, getPlaceholders.apply(t)));
    }

    default <T extends CommandSender> void sendActionBarDMessage(@NotNull Collection<T> targets, @NotNull String path, @NotNull Predicate<T> shouldSend, Function<T, String[]> getPlaceholders) {
        targets.forEach((t) -> {
            if (shouldSend.test(t)) sendActionBarDMessage(t, path, getPlaceholders.apply(t));
        });
    }
}
