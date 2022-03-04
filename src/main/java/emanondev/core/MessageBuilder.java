package emanondev.core;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class MessageBuilder {

	private final ComponentBuilder base;
	private final CommandSender target;
	private final YMLSection langSection;
	private final Player papiDefTarget;

	public MessageBuilder(@NotNull CorePlugin plugin, @Nullable CommandSender languageTarget) {
		this(plugin.getLanguageConfig(languageTarget), languageTarget);
	}

	public MessageBuilder(@NotNull CoreCommand command, @Nullable CommandSender languageTarget) {
		this(command.getLanguageSection(languageTarget), languageTarget);
	}

	public MessageBuilder(@NotNull Module module, @Nullable CommandSender languageTarget) {
		this(module.getLanguageSection(languageTarget), languageTarget);
	}

	public MessageBuilder(YMLSection section, @Nullable CommandSender languageTarget) {
		if (section == null)// || languageTarget == null)
			throw new NullPointerException();
		this.target = languageTarget;
		this.langSection = section;
		base = new ComponentBuilder();
		papiDefTarget = (target != null && (target instanceof Player)) ? (Player) target : null;
	}

	/**
	 * Adds Text to the message
	 * 
	 * @param texts       text to add (each string is a line)
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addText(List<String> texts, String... holders) {
		return addText(texts, null, true, holders);
	}

	/**
	 * Adds Text to the message
	 * 
	 * @param texts      text to add (each string is a line)
	 * @param papiTarget target for papi placeholder, if null attempt to use language
	 *                   target from constructor is made
	 * @param color      should replace colors? by default true
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addText(List<String> texts, Player papiTarget, boolean color, String... holders) {
		if (texts == null || texts.isEmpty())
			return this;
		return addText(String.join("\n", texts), papiTarget, color, holders);
	}

	/**
	 * Adds Text to the message
	 * 
	 * @param text       text to add
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 * @see #addText(String, Player, boolean, String...)
	 */
	public MessageBuilder addText(String text, String... holders) {
		return addText(text, null, true, holders);
	}

	/**
	 * Adds Text to the message
	 * 
	 * @param text       text to add
	 * @param papiTarget target for papi placeholder, if null attempt to use language
	 *                   target from constructor is made
	 * @param color      should replace colors? by default true
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addText(String text, Player papiTarget, boolean color, String... holders) {
		if (text != null)
			base.append(UtilsString.fix(text, papiTarget == null ? papiDefTarget : papiTarget, color, holders));
		return this;
	}

	/**
	 * Adds hover message
	 * 
	 * @param texts      text to add (each string is a line)
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addHover(List<String> texts, String... holders) {
		return addHover(texts, null, true, holders);
	}

	/**
	 * Adds hover message
	 * 
	 * @param texts      text to add (each string is a line)
	 * @param papiTarget target for papi placeholder, if null attempt to use language
	 *                   target from constructor is made
	 * @param color      should replace colors? by default true
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addHover(List<String> texts, Player papiTarget, boolean color, String... holders) {
		if (texts == null || texts.isEmpty())
			return this;
		return addHover(String.join("\n", texts), papiTarget, color, holders);
	}

	/**
	 * Adds hover message
	 * 
	 * @param text       text to add
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addHover(String text, String... holders) {
		return addHover(text, null, true, holders);
	}

	/**
	 * Adds hover message
	 * 
	 * @param text       text to add
	 * @param papiTarget target for papi placeholder, if null attempt to use language
	 *                   target from constructor is made
	 * @param color      should replace colors? by default true
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addHover(String text, Player papiTarget, boolean color, String... holders) {
		if (text != null)
			base.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new Text(UtilsString.fix(text, papiTarget == null ? papiDefTarget : papiTarget, color, holders))));
		return this;
	}

	/**
	 * Adds command suggestion on click
	 * 
	 * @param command    text to add
	 * @param holders placeholders with format
	 *                {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addSuggestCommand(String command, String... holders) {
		return addCommand(command, ClickEvent.Action.SUGGEST_COMMAND, holders);
	}

	/**
	 * Adds command execution on click
	 * 
	 * @param command    text to add
	 * @param holders placeholders with format
	 *                {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addRunCommand(String command, String... holders) {
		return addCommand(command, ClickEvent.Action.RUN_COMMAND, holders);
	}

	/**
	 * Adds command action on click
	 * 
	 * @param command    text to add
	 * @param holders placeholders with format
	 *                {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addCommand(String command, @NotNull ClickEvent.Action action, String... holders) {
		if (command != null && action != null)
			base.event(new ClickEvent(action, UtilsString.fix(command, null, false, holders)));
		return this;
	}

	/**
	 * Adds text message
	 * 
	 * @param path       where to get value
	 * @param def        default text to add
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addTextTranslation(String path, List<String> def, String... holders) {
		return addTextTranslation(path, def, null, true, holders);
	}

	/**
	 * Adds text message
	 * 
	 * @param path       where to get value
	 * @param def        default text to add
	 * @param papiTarget target for papi placeholder, if null attempt to use language
	 *                   target from constructor is made
	 * @param color      should replace colors? by default true
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addTextTranslation(String path, List<String> def, Player papiTarget, boolean color,
			String... holders) {
		return addText(langSection.loadMultiMessage(path, def, false, null, holders), papiTarget, color);
	}

	/**
	 * Adds text message
	 * 
	 * @param path       where to get value
	 * @param def        default text to add
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addTextTranslation(String path, String def, String... holders) {
		return addTextTranslation(path, def, null, true, holders);
	}

	/**
	 * Adds text message
	 * 
	 * @param path       where to get value
	 * @param def        default text to add
	 * @param papiTarget target for papi placeholder, if null attempt to use language
	 *                   target from constructor is made
	 * @param color      should replace colors? by default true
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addTextTranslation(String path, String def, Player papiTarget, boolean color,
			String... holders) {
		return addText(langSection.loadMessage(path, def, false, null, holders), papiTarget, color);
	}

	/**
	 * Adds hover message
	 * 
	 * @param path       where to get value
	 * @param def        default text to add
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addHoverTranslation(String path, List<String> def, String... holders) {
		return addHoverTranslation(path, def, null, true, holders);
	}

	/**
	 * Adds hover message
	 * 
	 * @param path       where to get value
	 * @param def        default text to add
	 * @param papiTarget target for papi placeholder, if null attempt to use language
	 *                   target from constructor is made
	 * @param color      should replace colors? by default true
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addHoverTranslation(String path, List<String> def, Player papiTarget, boolean color,
			String... holders) {
		return addHover(langSection.loadMultiMessage(path, def, false, null, holders), papiTarget, color);
	}

	/**
	 * Adds hover message
	 * 
	 * @param path       where to get value
	 * @param def        default text to add
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addHoverTranslation(String path, String def, String... holders) {
		return addHoverTranslation(path, def, null, true, holders);
	}

	/**
	 * Adds hover message
	 * 
	 * @param path       where to get value
	 * @param def        default text to add
	 * @param papiTarget target for papi placeholder, if null attempt to use language
	 *                   target from constructor is made
	 * @param color      should replace colors? by default true
	 * @param holders    additional placeholders with format
	 *                   {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addHoverTranslation(String path, String def, Player papiTarget, boolean color,
			String... holders) {
		return addHover(langSection.loadMessage(path, def, false, null, holders), papiTarget, color);
	}

	/**
	 * Adds command execution on click
	 * 
	 * @param path    where to get value
	 * @param def     default command
	 * @param holders additional placeholders with format
	 *                {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addRunCommandConfigurable(String path, String def, String... holders) {
		return addRunCommand(langSection.loadMessage(path, def, false, null, holders));
	}

	/**
	 * Adds command suggestion on click
	 * 
	 * @param path    where to get value
	 * @param def     default command
	 * @param holders additional placeholders with format
	 *                {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addSuggestCommandConfigurable(String path, String def, String... holders) {
		return addSuggestCommand(langSection.loadMessage(path, def, false, null, holders));
	}

	/**
	 * Adds full component
	 * 
	 * @param path           where to get value
	 * @param defText        default message
	 * @param defHover       default hover message
	 * @param defAction      default command text
	 * @param defClickAction default click action type
	 * @param holders        additional placeholders with format
	 *                       {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addFullComponentTranslation(String path, String defText, List<String> defHover,
			String defAction, ClickEvent.Action defClickAction, String... holders) {
		return addFullComponentTranslation(path, List.of(defText), defHover, defAction, defClickAction, null,
				true, holders);
	}

	/**
	 * Adds full component
	 * 
	 * @param path           where to get value
	 * @param defText        default message
	 * @param defHover       default hover message
	 * @param defAction      default command text
	 * @param defClickAction default click action type
	 * @param color          should replace colors? by default true
	 * @param papiTarget     target for papi placeholder, if null attempt to use
	 *                       language target from constructor is made
	 * @param holders        additional placeholders with format
	 *                       {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addFullComponentTranslation(String path, String defText, List<String> defHover,
			String defAction, ClickEvent.Action defClickAction, Player papiTarget, boolean color, String... holders) {
		return addFullComponentTranslation(path, List.of(defText), defHover, defAction, defClickAction,
				papiTarget, color, holders);
	}

	/**
	 * Adds full component
	 * 
	 * @param path           where to get value
	 * @param defText        default message
	 * @param defHover       default hover message
	 * @param defAction      default command text
	 * @param defClickAction default click action type
	 * @param holders        additional placeholders with format
	 *                       {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addFullComponentTranslation(String path, String defText, String defHover, String defAction,
			ClickEvent.Action defClickAction, String... holders) {
		return addFullComponentTranslation(path, List.of(defText), List.of(defHover), defAction,
				defClickAction, null, true, holders);
	}

	/**
	 * Adds full component
	 * 
	 * @param path           where to get value
	 * @param defText        default message
	 * @param defHover       default hover message
	 * @param defAction      default command text
	 * @param defClickAction default click action type
	 * @param color          should replace colors? by default true
	 * @param papiTarget     target for papi placeholder, if null attempt to use
	 *                       language target from constructor is made
	 * @param holders        additional placeholders with format
	 *                       {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addFullComponentTranslation(String path, String defText, String defHover, String defAction,
			ClickEvent.Action defClickAction, Player papiTarget, boolean color, String... holders) {
		return addFullComponentTranslation(path, List.of(defText), List.of(defHover), defAction,
				defClickAction, papiTarget, color, holders);
	}

	/**
	 * Adds full component
	 * 
	 * @param path           where to get value
	 * @param defText        default message
	 * @param defHover       default hover message
	 * @param defAction      default command text
	 * @param defClickAction default click action type
	 * @param holders        additional placeholders with format
	 *                       {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addFullComponentTranslation(String path, List<String> defText, String defHover,
			String defAction, ClickEvent.Action defClickAction, String... holders) {
		return addFullComponentTranslation(path, defText, List.of(defHover), defAction, defClickAction, null,
				true, holders);
	}

	/**
	 * Adds full component
	 * 
	 * @param path           where to get value
	 * @param defText        default message
	 * @param defHover       default hover message
	 * @param defAction      default command text
	 * @param defClickAction default click action type
	 * @param color          should replace colors? by default true
	 * @param papiTarget     target for papi placeholder, if null attempt to use
	 *                       language target from constructor is made
	 * @param holders        additional placeholders with format
	 *                       {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addFullComponentTranslation(String path, List<String> defText, String defHover,
			String defAction, ClickEvent.Action defClickAction, Player papiTarget, boolean color, String... holders) {
		return addFullComponentTranslation(path, defText, List.of(defHover), defAction, defClickAction,
				papiTarget, color, holders);
	}

	/**
	 * Adds full component
	 * 
	 * @param path           where to get value
	 * @param defText        default message
	 * @param defHover       default hover message
	 * @param defAction      default command text
	 * @param defClickAction default click action type
	 * @param holders        additional placeholders with format
	 *                       {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addFullComponentTranslation(String path, List<String> defText, List<String> defHover,
			String defAction, ClickEvent.Action defClickAction, String... holders) {
		return addFullComponentTranslation(path, defText, defHover, defAction, defClickAction, null, true, holders);
	}

	/**
	 * Adds full component
	 * 
	 * @param path           where to get value
	 * @param defText        default message
	 * @param defHover       default hover message
	 * @param defAction      default command text
	 * @param defClickAction default click action type
	 * @param color          should replace colors? by default true
	 * @param papiTarget     target for papi placeholder, if null attempt to use
	 *                       language target from constructor is made
	 * @param holders        additional placeholders with format
	 *                       {holder1,replacer1,holder2,replacer2,...}
	 * @return this for chaining
	 */
	public MessageBuilder addFullComponentTranslation(String path, List<String> defText, List<String> defHover,
			String defAction, ClickEvent.Action defClickAction, Player papiTarget, boolean color, String... holders) {
		ComponentBuilder comp = langSection.loadComponentMessage(path, defText, defHover, defAction, defClickAction,
				color, papiTarget == null ? papiDefTarget : papiTarget, holders);
		if (comp != null)
			base.append(comp.create());
		return this;
	}

	public MessageBuilder retent(FormatRetention retention) {
		base.retain(retention);
		return this;
	}

	public void send() {
		if (target != null)
			UtilsMessages.sendMessage(target, base.create());
	}

	public void send(CommandSender to) {
		if (to != null)
			UtilsMessages.sendMessage(to, base.create());
	}

	public void send(Collection<? extends CommandSender> to) {
		if (to != null)
			for (CommandSender t : to)
				send(t);
	}

	/**
	 * Invalid (target shouldn't change)
	 * 
	 * @param p target
	 * @return
	 */
	@Deprecated
	public MessageBuilder setTarget(@Nullable CommandSender p) {
		new IllegalStateException("Unwanted call").printStackTrace();
		return this;
	}

	@Deprecated
	public MessageBuilder addTranslatedText(String path, String def, String... holders) {
		return addTranslatedText(path, def, true, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedText(String path, List<String> def, String... holders) {
		return addTranslatedText(path, def, true, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedText(String path, String def, boolean color, String... holders) {
		return addTranslatedText(path, def, color, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedText(String path, List<String> def, boolean color, String... holders) {
		return addTranslatedText(path, def, color, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedText(String path, String def, Player p, String... holders) {
		return addTranslatedText(path, def, true, p, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedText(String path, List<String> def, Player p, String... holders) {
		return addTranslatedText(path, def, true, p, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedText(String path, String def, boolean color, Player p, String... holders) {
		base.append(langSection.loadMessage(path, def, color,
				p != null ? p : (this.target instanceof Player ? (Player) target : null), holders));
		return this;
	}

	@Deprecated
	public MessageBuilder addTranslatedText(String path, List<String> def, boolean color, Player p, String... holders) {
		base.append(String.join("\n", langSection.loadMultiMessage(path, def, color,
				p != null ? p : (this.target instanceof Player ? (Player) target : null), holders)));
		return this;
	}

	@Deprecated
	public MessageBuilder addTranslatedHover(String path, String def, String... holders) {
		return addTranslatedHover(path, def, true, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedHover(String path, List<String> def, String... holders) {
		return addTranslatedHover(path, def, true, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedHover(String path, String def, boolean color, String... holders) {
		return addTranslatedHover(path, def, color, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedHover(String path, List<String> def, boolean color, String... holders) {
		return addTranslatedHover(path, def, color, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedHover(String path, String def, Player p, String... holders) {
		return addTranslatedHover(path, def, true, p, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedHover(String path, List<String> def, Player p, String... holders) {
		return addTranslatedHover(path, def, true, p, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedHover(String path, String def, boolean color, Player p, String... holders) {
		base.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(langSection.loadMessage(path, def, color,
				p != null ? p : (this.target instanceof Player ? (Player) target : null), holders))));
		return this;
	}

	@Deprecated
	public MessageBuilder addTranslatedHover(String path, List<String> def, boolean color, Player p,
			String... holders) {
		base.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new Text(String.join("\n", langSection.loadMultiMessage(path, def, color,
						p != null ? p : (this.target instanceof Player ? (Player) target : null), holders)))));
		return this;
	}

	@Deprecated
	public MessageBuilder addTranslatedCommand(String path, String def, String... holders) {
		return addTranslatedCommand(path, def, true, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedCommand(String path, List<String> def, String... holders) {
		return addTranslatedCommand(path, def, true, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedCommand(String path, String def, boolean color, String... holders) {
		return addTranslatedCommand(path, def, color, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedCommand(String path, List<String> def, boolean color, String... holders) {
		return addTranslatedCommand(path, def, color, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedCommand(String path, String def, Player p, String... holders) {
		return addTranslatedCommand(path, def, true, p, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedCommand(String path, List<String> def, Player p, String... holders) {
		return addTranslatedCommand(path, def, true, p, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedCommand(String path, String def, boolean color, Player p, String... holders) {
		base.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, langSection.loadMessage(path, def, color,
				p != null ? p : (this.target instanceof Player ? (Player) target : null), holders)));
		return this;
	}

	@Deprecated
	public MessageBuilder addTranslatedCommand(String path, List<String> def, boolean color, Player p,
			String... holders) {
		base.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.join("\n", langSection.loadMultiMessage(path,
				def, color, p != null ? p : (this.target instanceof Player ? (Player) target : null), holders))));
		return this;
	}

	@Deprecated
	public MessageBuilder addTranslatedSuggestion(String path, String def, String... holders) {
		return addTranslatedSuggestion(path, def, true, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedSuggestion(String path, List<String> def, String... holders) {
		return addTranslatedSuggestion(path, def, true, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedSuggestion(String path, String def, boolean color, String... holders) {
		return addTranslatedSuggestion(path, def, color, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedSuggestion(String path, List<String> def, boolean color, String... holders) {
		return addTranslatedSuggestion(path, def, color, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedSuggestion(String path, String def, Player p, String... holders) {
		return addTranslatedSuggestion(path, def, true, p, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedSuggestion(String path, List<String> def, Player p, String... holders) {
		return addTranslatedSuggestion(path, def, true, p, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedSuggestion(String path, String def, boolean color, Player p, String... holders) {
		base.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, langSection.loadMessage(path, def, color,
				p != null ? p : (this.target instanceof Player ? (Player) target : null), holders)));
		return this;
	}

	@Deprecated
	public MessageBuilder addTranslatedSuggestion(String path, List<String> def, boolean color, Player p,
			String... holders) {
		base.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
				String.join("\n", langSection.loadMultiMessage(path, def, color,
						p != null ? p : (this.target instanceof Player ? (Player) target : null), holders))));
		return this;
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, Player p, FormatRetention retention,
			String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, defClickAction, true, p, retention, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, boolean color, FormatRetention retention,
			String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, defClickAction, color, null, retention,
				holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, FormatRetention retention, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, defClickAction, true, null, retention,
				holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, Player p, FormatRetention retention, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, null, true, p, retention, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, boolean color, FormatRetention retention, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, null, color, null, retention, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, FormatRetention retention, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, null, true, null, retention, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, Player p, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, defClickAction, true, p, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, boolean color, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, defClickAction, color, null, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, defClickAction, true, null, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, Player p, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, null, true, p, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, boolean color, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, null, color, null, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, String... holders) {
		return addTranslatedComponent(path, defText, defAction, defHover, null, true, null, null, holders);
	}

	@Deprecated
	public MessageBuilder addTranslatedComponent(String path, List<String> defText, String defAction,
			List<String> defHover, ClickEvent.Action defClickAction, boolean color, Player p, FormatRetention retention,
			String... holders) {
		ComponentBuilder comp = langSection.loadComponentMessage(path, defText, defHover, defAction, defClickAction,
				color, p != null ? p : ((target instanceof Player) ? ((Player) target) : null), holders);
		if (comp != null)
			if (retention == null)
				base.append(comp.create());
			else
				base.append(comp.create(), retention);
		return this;
	}

}
