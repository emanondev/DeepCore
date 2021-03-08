package emanondev.core;

import java.util.*;
import java.util.regex.Pattern;

import javax.annotation.*;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

public class UtilsString {
	
	UtilsString(){
		throw new UnsupportedOperationException();
	}

	/**
	 * Update the item with the description, covering both title and lore
	 * 
	 * @param item
	 *            item to update
	 * @param desc
	 *            raw text
	 * @param p
	 *            player or null for placeHolderApi use
	 * @param color
	 *            translate colors
	 * @param holders
	 *            additional Holders, must be even number with the format "to
	 *            replace","replacer","to replace 2","replacer 2"....
	 */
	public static void updateDescription(@Nullable ItemStack item, @Nullable List<String> desc, @Nullable Player p, boolean color,
			String... holders) {
		if (item == null)
			return;

		// prepare title and lore
		String title;
		ArrayList<String> lore;
		if (desc == null || desc.isEmpty()) {
			title = " ";
			lore = null;
		} else if (desc.size() == 1) {
			if (desc.get(0) != null)
				if (!desc.get(0).startsWith(ChatColor.RESET + ""))
					title = ChatColor.RESET + desc.get(0);
				else
					title = desc.get(0);
			else
				title = null;
			lore = null;
		} else {
			if (!desc.get(0).startsWith(ChatColor.RESET + ""))
				title = ChatColor.RESET + desc.get(0);
			else
				title = desc.get(0);
			lore = new ArrayList<String>();
			for (int i = 1; i < desc.size(); i++)
				if (desc.get(i) != null)
					if (!desc.get(i).startsWith(ChatColor.RESET + ""))
						lore.add(ChatColor.RESET + desc.get(i));
					else
						lore.add(desc.get(i));
				else
					lore.add("");
		}

		// apply holders and colors for title and lore
		title = fix(title, p, color, holders);
		fix(lore, p, color, holders);

		// apply title and lore to item
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title);
		meta.setLore(lore);
		item.setItemMeta(meta);

	}
	
	public static void updateDescription(@Nullable ItemStack item, @Nullable List<String> desc,
			String... holders) {
		updateDescription(item,desc,null,false,holders);
	}

	/**
	 * 
	 * @param list
	 *            raw text
	 * @param player
	 *            player or null for placeHolderApi use
	 * @param color
	 *            translate colors
	 * @param holders
	 *            additional place Holders, must be even number with the format
	 *            "to replace#1","replacer#1","to replace#2","replacer#2"....
	 * @return a new list with fixed text, or null if list was null
	 */
	public static @Nullable ArrayList<String> fix(@Nullable List<String> list,@Nullable Player player, boolean color, String... holders) {
		if (list == null)
			return null;
		ArrayList<String> newList = new ArrayList<>();
		for (String line : list)
			newList.add(fix(line, player, color, holders));
		return newList;
	}

	/**
	 * Set the description on an item clone, covering both title and lore original
	 * item is unmodified
	 * 
	 * @param item
	 *            item to clone and update
	 * @param description
	 *            raw text
	 * @param player
	 *            player or null for placeHolderApi use
	 * @param color
	 *            translate colors
	 * @param holders
	 *            additional place Holders, must be even number with the format
	 *            "to replace","replacer","to replace 2","replacer 2"....
	 * @return new item with display name and lore used for desc
	 */
	public static ItemStack setDescription(@Nullable ItemStack item,@Nullable List<String> description,@Nullable Player player, boolean color,
			String... holders) {
		if (item == null || item.getType() == Material.AIR)
			return null;

		ItemStack itemCopy = new ItemStack(item);
		updateDescription(itemCopy, description, player, color, holders);
		return itemCopy;
	}

	public static String fix(@Nullable String text,@Nullable Player player, boolean color, String... holders) {
		if (text == null)
			return null;

		// holders

		Validate.isTrue(holders == null || holders.length % 2 == 0,"holder withouth replacer");
		if (holders != null && holders.length > 0)
			for (int i = 0; i < holders.length; i += 2)
				text = text.replace(holders[i], holders[i + 1]);

		// papi
		if (player != null && Hooks.isPAPIEnabled())
			text = PlaceholderAPI.setPlaceholders(player, text);
		
		// colore
		if (color)
			text = ChatColor.translateAlternateColorCodes('&', text);

		return text;
	}

	/**
	 * 
	 * @param text Text to revert
	 * @return a string with original colors and formats but with &amp; instead of §
	 */
	public static @Nullable String revertColors(@Nullable String text) {
		if (text == null)
			return null;
		return text.replace("§", "&");
	}
	/**
	 * 
	 * @param text Text to clear
	 * @return a string with no colors and no formats
	 */
	public static @Nullable String clearColors(@Nullable String text) {
		if (text==null)
			return null;
		return ChatColor.stripColor(text);
	}
	

	public static String getTimeStringMilliseconds(CommandSender sender,long cooldown) {
		return getTimeStringSeconds(sender,cooldown/1000);
	}
	public static String getTimeStringSeconds(CommandSender sender,long cooldown) {
		String and = " "+CoreMain.get().getLanguageConfig(sender).loadString("conjunction.and", "and")+" ";
		StringBuilder result = new StringBuilder("");
		if (cooldown>=Time.WEEK.seconds) {//week
			int val = (int) (cooldown/Time.WEEK.seconds);
			if (val>1)
				result.append(val+" "+Time.WEEK.getMultipleName(sender));
			else
				result.append(val+" "+Time.WEEK.getSingleName(sender));
			val =  (int) (cooldown%Time.WEEK.seconds/Time.DAY.seconds);
			if (val>1)
				result.append(and+ val+" "+Time.DAY.getMultipleName(sender));
			else
				if (val==1)
					result.append(and+ val+" "+Time.DAY.getSingleName(sender));
			return result.toString();
		}
		if (cooldown>=Time.DAY.seconds) {//day
			int val = (int) (cooldown/Time.DAY.seconds);
			if (val>1)
				result.append(val+" "+Time.DAY.getMultipleName(sender));
			else
				result.append(val+" "+Time.DAY.getSingleName(sender));
			val =  (int) (cooldown%Time.DAY.seconds/Time.HOUR.seconds);
			if (val>1)
				result.append(and+ val+" "+Time.HOUR.getMultipleName(sender));
			else
				if (val==1)
					result.append(and+ val+" "+Time.HOUR.getSingleName(sender));
			return result.toString();
		}
		if (cooldown>=Time.HOUR.seconds) {//hour
			int val = (int) (cooldown/Time.HOUR.seconds);
			if (val>1)
				result.append(val+" "+Time.HOUR.getMultipleName(sender));
			else
				result.append(val+" "+Time.HOUR.getSingleName(sender));
			val =  (int) (cooldown%Time.HOUR.seconds/Time.MINUTE.seconds);
			if (val>1)
				result.append(and+ val+" "+Time.MINUTE.getMultipleName(sender));
			else
				if (val==1)
					result.append(and+ val+" "+Time.MINUTE.getSingleName(sender));
			return result.toString();
		}
		if (cooldown>=Time.MINUTE.seconds) {//minute
			int val = (int) (cooldown/Time.MINUTE.seconds);
			if (val>1)
				result.append(val+" "+Time.MINUTE.getMultipleName(sender));
			else
				result.append(val+" "+Time.MINUTE.getSingleName(sender));
			val =  (int) (cooldown%Time.MINUTE.seconds/Time.SECOND.seconds);
			if (val>1)
				result.append(and+ val+" "+Time.SECOND.getMultipleName(sender));
			else
				if (val==1)
					result.append(and+ val+" "+Time.SECOND.getSingleName(sender));
			return result.toString();
		}
		int val = (int) (cooldown/Time.SECOND.seconds);
		if (val>1)
			result.append(val+" "+Time.SECOND.getMultipleName(sender));
		else
			result.append(val+" "+Time.SECOND.getSingleName(sender));
		return result.toString();
	}

	private static Pattern LOWCASED_KEY_ID = Pattern.compile("[0-9a-z_]+");
	/**
	 * 
	 * @param id Id to test
	 * @return id is lowcased, alphanumeric + '_' , non empty
	 */
	public static boolean isLowcasedValidID(String id) {
		return LOWCASED_KEY_ID.matcher(id).matches();
	}

	private static Pattern KEY_ID = Pattern.compile("[0-9a-zA-Z_]+");
	/**
	 * 
	 * @param id Id to test
	 * @return id alphanumeric + '_' , non empty
	 */
	public static boolean isValidID(String id) {
		return KEY_ID.matcher(id).matches();
	}

}
