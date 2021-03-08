package emanondev.core;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class MessageBuilder {

	ComponentBuilder base;
	private CommandSender target;
	private YMLConfig langConfig;

	public MessageBuilder(CorePlugin plugin, CommandSender languageTarget) {
		if (plugin == null || languageTarget == null)
			throw new NullPointerException();
		this.target = languageTarget;
		this.langConfig = plugin.getLanguageConfig(target);
		base = new ComponentBuilder();
	}

	public MessageBuilder setTarget(CommandSender p) {
		this.target = p;
		return this;
	}

	public MessageBuilder addText(String text) {
		base.append(text);
		return this;
	}

	public MessageBuilder addText(List<String> text) {
		return addText(String.join("\n", text));
	}

	public MessageBuilder addTranslatedText(String path, String def, String... holders) {
		return addTranslatedText(path, def, true, null, holders);
	}

	public MessageBuilder addTranslatedText(String path, List<String> def, String... holders) {
		return addTranslatedText(path, def, true, null, holders);
	}

	public MessageBuilder addTranslatedText(String path, String def, boolean color, String... holders) {
		return addTranslatedText(path, def, color, null, holders);
	}

	public MessageBuilder addTranslatedText(String path, List<String> def, boolean color, String... holders) {
		return addTranslatedText(path, def, color, null, holders);
	}

	public MessageBuilder addTranslatedText(String path, String def, Player p, String... holders) {
		return addTranslatedText(path, def, true, p, holders);
	}

	public MessageBuilder addTranslatedText(String path, List<String> def, Player p, String... holders) {
		return addTranslatedText(path, def, true, p, holders);
	}

	public MessageBuilder addTranslatedText(String path, String def, boolean color, Player p, String... holders) {
		base.append(langConfig.loadMessage(path, def, color,
				p != null ? p : (this.target instanceof Player ? (Player) target : null), holders));
		return this;
	}

	public MessageBuilder addTranslatedText(String path, List<String> def, boolean color, Player p, String... holders) {
		base.append(String.join("\n", langConfig.loadMultiMessage(path, def, color,
				p != null ? p : (this.target instanceof Player ? (Player) target : null), holders)));
		return this;
	}

	public MessageBuilder addTranslatedHover(String path, String def, String... holders) {
		return addTranslatedHover(path, def, true, null, holders);
	}

	public MessageBuilder addTranslatedHover(String path, List<String> def, String... holders) {
		return addTranslatedHover(path, def, true, null, holders);
	}

	public MessageBuilder addTranslatedHover(String path, String def, boolean color, String... holders) {
		return addTranslatedHover(path, def, color, null, holders);
	}

	public MessageBuilder addTranslatedHover(String path, List<String> def, boolean color, String... holders) {
		return addTranslatedHover(path, def, color, null, holders);
	}

	public MessageBuilder addTranslatedHover(String path, String def, Player p, String... holders) {
		return addTranslatedHover(path, def, true, p, holders);
	}

	public MessageBuilder addTranslatedHover(String path, List<String> def, Player p, String... holders) {
		return addTranslatedHover(path, def, true, p, holders);
	}

	public MessageBuilder addTranslatedHover(String path, String def, boolean color, Player p, String... holders) {
		base.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new Text(langConfig.loadMessage(path, def, color,
						p != null ? p : (this.target instanceof Player ? (Player) target : null), holders))));
		return this;
	}

	public MessageBuilder addTranslatedHover(String path, List<String> def, boolean color, Player p, String... holders) {
		base.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new Text(String.join("\n", langConfig.loadMultiMessage(path, def, color,
						p != null ? p : (this.target instanceof Player ? (Player) target : null), holders)))));
		return this;
	}

	public MessageBuilder addTranslatedCommand(String path, String def, String... holders) {
		return addTranslatedCommand(path, def, true, null, holders);
	}

	public MessageBuilder addTranslatedCommand(String path, List<String> def, String... holders) {
		return addTranslatedCommand(path, def, true, null, holders);
	}

	public MessageBuilder addTranslatedCommand(String path, String def, boolean color, String... holders) {
		return addTranslatedCommand(path, def, color, null, holders);
	}

	public MessageBuilder addTranslatedCommand(String path, List<String> def, boolean color, String... holders) {
		return addTranslatedCommand(path, def, color, null, holders);
	}

	public MessageBuilder addTranslatedCommand(String path, String def, Player p, String... holders) {
		return addTranslatedCommand(path, def, true, p, holders);
	}

	public MessageBuilder addTranslatedCommand(String path, List<String> def, Player p, String... holders) {
		return addTranslatedCommand(path, def, true, p, holders);
	}

	public MessageBuilder addTranslatedCommand(String path, String def, boolean color, Player p, String... holders) {
		base.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, langConfig.loadMessage(path, def,
				color, p != null ? p : (this.target instanceof Player ? (Player) target : null), holders)));
		return this;
	}

	public MessageBuilder addTranslatedCommand(String path, List<String> def, boolean color, Player p,
			String... holders) {
		base.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
				String.join("\n", langConfig.loadMultiMessage(path, def, color,
						p != null ? p : (this.target instanceof Player ? (Player) target : null), holders))));
		return this;
	}

	public MessageBuilder addTranslatedSuggestion(String path, String def, String... holders) {
		return addTranslatedSuggestion(path, def, true, null, holders);
	}

	public MessageBuilder addTranslatedSuggestion(String path, List<String> def, String... holders) {
		return addTranslatedSuggestion(path, def, true, null, holders);
	}

	public MessageBuilder addTranslatedSuggestion(String path, String def, boolean color, String... holders) {
		return addTranslatedSuggestion(path, def, color, null, holders);
	}

	public MessageBuilder addTranslatedSuggestion(String path, List<String> def, boolean color, String... holders) {
		return addTranslatedSuggestion(path, def, color, null, holders);
	}

	public MessageBuilder addTranslatedSuggestion(String path, String def, Player p, String... holders) {
		return addTranslatedSuggestion(path, def, true, p, holders);
	}

	public MessageBuilder addTranslatedSuggestion(String path, List<String> def, Player p, String... holders) {
		return addTranslatedSuggestion(path, def, true, p, holders);
	}

	public MessageBuilder addTranslatedSuggestion(String path, String def, boolean color, Player p, String... holders) {
		base.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, langConfig.loadMessage(path,
				def, color, p != null ? p : (this.target instanceof Player ? (Player) target : null), holders)));
		return this;
	}

	public MessageBuilder addTranslatedSuggestion(String path, List<String> def, boolean color, Player p,
			String... holders) {
		base.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
				String.join("\n", langConfig.loadMultiMessage(path, def, color,
						p != null ? p : (this.target instanceof Player ? (Player) target : null), holders))));
		return this;
	}

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, Player p, FormatRetention retention,
			String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, defClickAction, true, p, retention, holders);
	}

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, boolean color, FormatRetention retention,
			String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, defClickAction, color, null, retention,
				holders);
	}

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, FormatRetention retention, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, defClickAction, true, null, retention,
				holders);
	}

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, Player p, FormatRetention retention, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, null, true, p, retention, holders);
	}

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, boolean color, FormatRetention retention, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, null, color, null, retention, holders);
	}

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, FormatRetention retention, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, null, true, null, retention, holders);
	}
	

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, Player p,
			String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, defClickAction, true, p, null, holders);
	}

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, boolean color,
			String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, defClickAction, color, null, null,
				holders);
	}

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, defClickAction, true, null, null,
				holders);
	}

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, Player p, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, null, true, p, null, holders);
	}

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, boolean color, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, null, color, null, null, holders);
	}

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, null, true, null, null, holders);
	}

	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, boolean color, Player p, FormatRetention retention,
			String... holders) {
		ComponentBuilder comp = langConfig.loadComponentMessage(path, defText, defHover, defAction,
				defClickAction, color, p != null ? p : ((target instanceof Player) ? ((Player) target) : null),
				holders);
		if (comp != null)
			if (retention == null)
				base.append(comp.create());
			else
				base.append(comp.create(), retention);
		return this;
	}

	public MessageBuilder retent(FormatRetention retention) {
		base.retain(retention);
		return this;
	}

	public void send() {
		UtilsMessages.sendMessage(target, base.create());
	}

}
