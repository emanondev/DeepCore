package emanondev.core.message;

import emanondev.core.CorePlugin;
import emanondev.core.UtilsString;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class MessageComponent {

    private final TextComponent.Builder base;
    private final CorePlugin plugin;
    private final CommandSender target;
    private Player papiTarget;

    public MessageComponent(@NotNull CorePlugin plugin, @Nullable CommandSender target) {
        this(plugin, target, target instanceof Player p ? p : null);
    }

    public MessageComponent(@NotNull CorePlugin plugin, @Nullable CommandSender target, @Nullable Player papiTarget) {
        this.base = Component.text();
        this.plugin = plugin;
        this.target = target;
        this.papiTarget = papiTarget;
    }

    public MessageComponent(@NotNull CorePlugin plugin) {
        this(plugin, null, null);
    }

    @Contract("_ -> this")
    @NotNull
    public MessageComponent append(@NotNull MessageComponent component) {
        base.append(component.base);
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public MessageComponent append(@Nullable String text, String... holders) {
        if (text != null)
            base.append(format(text));
        return this;
    }

    @NotNull
    private Component format(@NotNull String text, String... holders) {
        text = UtilsString.fix(text, papiTarget, false, holders);
        text = text.replace('§', '&');
        for (ChatColor color : ChatColor.values())
            text = text.replace("&" + color.toString().charAt(1),
                    (color.getColor()==null?"":"<reset>")+
                    "<" + color.getName()
                    .toLowerCase(Locale.ENGLISH) + ">");
        try {
            int from = 0;
            while (text.indexOf("&#", from) >= 0) {
                from = text.indexOf("&#", from) + 1;
                text = text.replace(text.substring(from - 1, from + 7),
                        "<reset><#" + text.substring(from, from + 7) + ">");
            }
        } catch (Throwable ignored) {
        }
        return MiniMessage.miniMessage().deserialize(text);
    }

    @Contract("_, _ -> this")
    @NotNull
    public MessageComponent append(@Nullable List<String> text, String... holders) {
        if (text != null)
            append(String.join("\n", text), holders);
        return this;
    }

    @Contract("_, _, _ -> this")
    @NotNull
    public MessageComponent appendConfigurable(@NotNull String path, @Nullable List<String> text, String... holders) {
        if (text != null)
            append(plugin.getLanguageConfig(this.target).loadMessage(path,text,holders), holders);
        return this;
    }

    @Contract("_, _, _ -> this")
    @NotNull
    public MessageComponent appendConfigurable(@NotNull String path, @Nullable String text, String... holders) {
        if (text != null)
            append(plugin.getLanguageConfig(this.target).loadMessage(path,text,holders), holders);
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public MessageComponent hoverText(@Nullable String text, String... holders) {
        if (text != null)
            base.hoverEvent(HoverEvent.showText(format(text)));
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public MessageComponent hoverText(@Nullable List<String> text, String... holders) {
        if (text != null)
            hoverText(String.join("\n", text), holders);
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public MessageComponent clickRunCommand(@Nullable String text, String... holders) {
        if (text != null)
            base.clickEvent(ClickEvent.runCommand(UtilsString.fix(text, papiTarget, false, holders)));
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public MessageComponent clickOpenUrlCommand(@Nullable String text, String... holders) {
        if (text != null)
            base.clickEvent(ClickEvent.openUrl(UtilsString.fix(text, papiTarget, false, holders)));
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public MessageComponent clickSuggestCommand(@Nullable String text, String... holders) {
        if (text != null)
            base.clickEvent(ClickEvent.suggestCommand(UtilsString.fix(text, papiTarget, false, holders)));
        return this;
    }

    @Contract(" -> this")
    @NotNull
    public MessageComponent newLine() {
        base.append(Component.newline());
        return this;
    }

    @Contract(" -> this")
    @NotNull
    public MessageComponent resetStyle() {
        base.resetStyle();
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public MessageComponent setPapiTarget(Player papiTarget) {
        this.papiTarget = papiTarget;
        return this;
    }

    @Contract(pure = true)
    public void send() {
        send(target);
    }

    @Contract(pure = true)
    public void send(CommandSender target) {
        if (target != null)
            if (target instanceof Player player)
                plugin.adventure().player(player).sendMessage(base);
            else //???
                plugin.adventure().sender(target).sendMessage(base);
    }

    @Contract(pure = true)
    public void send(Collection<? extends CommandSender> targets) {
        targets.forEach(this::send);
    }

    @Contract(pure = true)
    public void sendActionBar() {
        sendActionBar(target);
    }

    @Contract(pure = true)
    public void sendActionBar(CommandSender target) {
        if (target != null)
            if (target instanceof Player player)
                plugin.adventure().player(player).sendActionBar(base);
            else //???
                plugin.adventure().sender(target).sendActionBar(base);
    }

    @Contract(pure = true)
    public void sendActionBar(Collection<? extends CommandSender> targets) {
        targets.forEach(this::sendActionBar);
    }

}
