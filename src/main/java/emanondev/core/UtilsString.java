package emanondev.core;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class UtilsString {

    private UtilsString() {
        throw new AssertionError();
    }

    /**
     * Update the item with the description, covering both title and lore
     *
     * @param item       item to update
     * @param desc       raw text
     * @param papiTarget player or null for placeHolderApi use
     * @param color      translate colors
     * @param holders    additional Holders, must be even number with the format "to
     *                   replace","replacer","to replace 2","replacer 2"....
     */
    public static void updateDescription(@Nullable ItemStack item, @Nullable List<String> desc,
                                         @Nullable Player papiTarget, boolean color, String... holders) {
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
            lore = new ArrayList<>();
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
        title = fix(title, papiTarget, color, holders);
        fix(lore, papiTarget, color, holders);

        // apply title and lore to item
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(lore);
        item.setItemMeta(meta);

    }

    public static void updateDescription(@Nullable ItemStack item, @Nullable List<String> desc, String... holders) {
        updateDescription(item, desc, null, false, holders);
    }

    /**
     * @param list       raw text
     * @param papiTarget player or null for placeHolderApi use
     * @param color      translate colors
     * @param holders    additional placeholders, must be even number with the format
     *                   "to replace#1","replacer#1","to replace#2","replacer#2"....
     * @return a new list with fixed text, or null if list was null
     */
    @Contract("!null, _, _, _ -> !null")
    public static @Nullable ArrayList<String> fix(@Nullable List<String> list, @Nullable Player papiTarget,
                                                  boolean color, String... holders) {
        if (list == null)
            return null;
        ArrayList<String> newList = new ArrayList<>();
        for (String line : list)
            newList.add(fix(line, papiTarget, color, holders));
        return newList;
    }

    /**
     * Set the description on an item clone, covering both title and lore original
     * item is unmodified
     *
     * @param item        item to clone and update
     * @param description raw text
     * @param player      player or null for placeHolderApi use
     * @param color       translate colors
     * @param holders     additional placeholders, must be even number with the
     *                    format "to replace","replacer","to replace 2","replacer
     *                    2"....
     * @return new item with display name and lore used for desc
     */
    @Contract("null, _, _, _, _ -> null")
    public static ItemStack setDescription(@Nullable ItemStack item, @Nullable List<String> description,
                                           @Nullable Player player, boolean color, String... holders) {
        if (item == null || item.getType() == Material.AIR)
            return null;

        ItemStack itemCopy = new ItemStack(item);
        updateDescription(itemCopy, description, player, color, holders);
        return itemCopy;
    }

    @Contract("!null, _, _, _ -> !null; null, _, _, _ -> null")
    public static String fix(@Nullable String text, @Nullable Player papiTarget, boolean color, String... holders) {
        if (text == null)
            return null;

        // holders
        Validate.isTrue(holders == null || holders.length % 2 == 0, "holder without replacer");
        if (holders != null && holders.length > 0)
            for (int i = 0; i < holders.length; i += 2)
                text = text.replace(holders[i], holders[i + 1]);

        // papi
        if (papiTarget != null && Hooks.isPAPIEnabled())
            text = PlaceholderAPI.setPlaceholders(papiTarget, text);

        // color
        if (color)
            text = ChatColor.translateAlternateColorCodes('&', text);

        return text;
    }

    /**
     * @param text Text to revert
     * @return a string with original colors and formats but with &amp; instead of §
     */
    @Contract("!null -> !null; null -> null")
    public static @Nullable String revertColors(@Nullable String text) {
        if (text == null)
            return null;
        return text.replace("§", "&");
    }

    /**
     * @param text Text to clear
     * @return a string with no colors and no formats
     */
    @Contract("!null -> !null; null -> null")
    public static @Nullable String clearColors(@Nullable String text) {
        if (text == null)
            return null;
        return ChatColor.stripColor(text);
    }

    public static String getTimeStringMilliseconds(CommandSender sender, long cooldown) {
        return getTimeStringSeconds(sender, cooldown / 1000);
    }

    public static String getTimeStringSeconds(CommandSender sender, long cooldown) {
        String and = " " + CoreMain.get().getLanguageConfig(sender).loadString("conjunction.and", "and") + " ";
        StringBuilder result = new StringBuilder();
        if (cooldown >= Time.WEEK.seconds) {// week
            int val = (int) (cooldown / Time.WEEK.seconds);
            if (val > 1)
                result.append(val).append(" ").append(Time.WEEK.getMultipleName(sender));
            else
                result.append(val).append(" ").append(Time.WEEK.getSingleName(sender));
            val = (int) (cooldown % Time.WEEK.seconds / Time.DAY.seconds);
            if (val > 1)
                result.append(and).append(val).append(" ").append(Time.DAY.getMultipleName(sender));
            else if (val == 1)
                result.append(and).append(val).append(" ").append(Time.DAY.getSingleName(sender));
            return result.toString();
        }
        if (cooldown >= Time.DAY.seconds) {// day
            int val = (int) (cooldown / Time.DAY.seconds);
            if (val > 1)
                result.append(val).append(" ").append(Time.DAY.getMultipleName(sender));
            else
                result.append(val).append(" ").append(Time.DAY.getSingleName(sender));
            val = (int) (cooldown % Time.DAY.seconds / Time.HOUR.seconds);
            if (val > 1)
                result.append(and).append(val).append(" ").append(Time.HOUR.getMultipleName(sender));
            else if (val == 1)
                result.append(and).append(val).append(" ").append(Time.HOUR.getSingleName(sender));
            return result.toString();
        }
        if (cooldown >= Time.HOUR.seconds) {// hour
            int val = (int) (cooldown / Time.HOUR.seconds);
            if (val > 1)
                result.append(val).append(" ").append(Time.HOUR.getMultipleName(sender));
            else
                result.append(val).append(" ").append(Time.HOUR.getSingleName(sender));
            val = (int) (cooldown % Time.HOUR.seconds / Time.MINUTE.seconds);
            if (val > 1)
                result.append(and).append(val).append(" ").append(Time.MINUTE.getMultipleName(sender));
            else if (val == 1)
                result.append(and).append(val).append(" ").append(Time.MINUTE.getSingleName(sender));
            return result.toString();
        }
        if (cooldown >= Time.MINUTE.seconds) {// minute
            int val = (int) (cooldown / Time.MINUTE.seconds);
            if (val > 1)
                result.append(val).append(" ").append(Time.MINUTE.getMultipleName(sender));
            else
                result.append(val).append(" ").append(Time.MINUTE.getSingleName(sender));
            val = (int) (cooldown % Time.MINUTE.seconds / Time.SECOND.seconds);
            if (val > 1)
                result.append(and).append(val).append(" ").append(Time.SECOND.getMultipleName(sender));
            else if (val == 1)
                result.append(and).append(val).append(" ").append(Time.SECOND.getSingleName(sender));
            return result.toString();
        }
        int val = (int) (cooldown / Time.SECOND.seconds);
        if (val > 1)
            result.append(val).append(" ").append(Time.SECOND.getMultipleName(sender));
        else
            result.append(val).append(" ").append(Time.SECOND.getSingleName(sender));
        return result.toString();
    }

    private static final Pattern LOWCASED_KEY_ID = Pattern.compile("[0-9a-z_]+");

    /**
     * @param id ID to test
     * @return id is low-cased, alphanumeric + '_' , not empty
     */
    public static boolean isLowcasedValidID(String id) {
        return LOWCASED_KEY_ID.matcher(id).matches();
    }

    private static final Pattern KEY_ID = Pattern.compile("[0-9a-zA-Z_]+");

    /**
     * @param id ID to test
     * @return id alphanumeric + '_' , not empty
     */
    public static boolean isValidID(String id) {
        return KEY_ID.matcher(id).matches();
    }

    private static final Map<Character, Integer> roman = new HashMap<>() {
        {
            put('I', 1);
            put('V', 5);
            put('X', 10);
            put('L', 50);
            put('C', 100);
            put('D', 500);
            put('M', 1000);
        }
    };

    private static final int[] romanValues = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] romanLiterals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV",
            "I"};

    public static int romanToInt(String s) {
        int sum = 0;
        int n = s.length();

        for (int i = 0; i < n; i++) {
            if (i != n - 1 && roman.get(s.charAt(i)) < roman.get(s.charAt(i + 1))) {
                sum += roman.get(s.charAt(i + 1)) - roman.get(s.charAt(i));
                i++;
            } else {
                sum += roman.get(s.charAt(i));
            }
        }
        return sum;
    }

    public static String intToRoman(int num) {
        StringBuilder roman = new StringBuilder();
        for (int i = 0; i < romanValues.length; i++) {
            while (num >= romanValues[i]) {
                num -= romanValues[i];
                roman.append(romanLiterals[i]);
            }
        }
        return roman.toString();
    }

    private static final DecimalFormat optional1Digit = new DecimalFormat("0.#");
    private static final DecimalFormat optional2Digit = new DecimalFormat("0.##");
    private static final DecimalFormat optional10Digit = new DecimalFormat("0.##########");
    private static final DecimalFormat forced1Digit = new DecimalFormat("0.0");
    private static final DecimalFormat forced2Digit = new DecimalFormat("0.00");
    private static final DecimalFormat forcedIntDigit = new DecimalFormat("0");

    public static @NotNull String formatForced2Digit(@NotNull Number num) {
        return forced2Digit.format(num);
    }

    public static @NotNull String formatForcedInt(@NotNull Number num) {
        return forcedIntDigit.format(num);
    }

    public static @NotNull String formatForced1Digit(@NotNull Number num) {
        return forced1Digit.format(num);
    }

    public static @NotNull String formatOptional1Digit(@NotNull Number num) {
        return optional1Digit.format(num);
    }

    public static @NotNull String formatOptional10Digit(@NotNull Number num) {
        return optional10Digit.format(num);
    }

    public static @NotNull String formatOptional2Digit(@NotNull Number num) {
        return optional2Digit.format(num);
    }

    public static @NotNull List<String> textLineSplitter(@Nullable String text) {
        return textLineSplitter(text, 50);
    }

    public static @NotNull List<String> textLineSplitter(@Nullable String text, int maxLength) {
        return textLineSplitter(text, maxLength, ChatColor.WHITE);
    }

    public static @NotNull List<String> textLineSplitter(@Nullable String text, int maxLength, @Nullable ChatColor base) {
        ArrayList<String> val = new ArrayList<>();
        if (text == null || text.isEmpty())
            return val;
        StringBuilder format = new StringBuilder(base == null ? "" : base.toString());
        String[] words = text.split(" ");

        int wordCounter = 0;
        StringBuilder line = new StringBuilder();
        for (int index = 0; index < words.length; index++) {
            if (wordCounter > 0 && ChatColor.stripColor(line.toString()).length() + 1
                    + ChatColor.stripColor(words[index]).length() > maxLength) {
                val.add(format + line.toString());
                int start = 0;
                while (start < line.toString().length()) {
                    int j = line.toString().indexOf("§", start);
                    if (j == -1)
                        break;
                    try {
                        format.append(line.substring(j, j + 2));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    start = j + 2;
                }
                line = new StringBuilder();
                wordCounter = 0;
            }
            if (wordCounter == 0)
                line.append(words[index]);
            else
                line.append(" ").append(words[index]);
            wordCounter++;
        }
        val.add(format.toString() + line);
        return val;
    }

}
