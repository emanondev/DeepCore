package emanondev.core.message;


import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * this class provide native game translations based on client translations, converted by MiniMessage format
 */
public class Translations {

    private Translations() {}

    public static @NotNull String get(@NotNull Block block) {
        return get(block.getType());
    }

    public static @NotNull String get(@NotNull ItemStack stack) {
        return get(stack.getType());
    }

    public static @NotNull String get(@NotNull Material material) {
        return getTranslation((material.isBlock() ? "block." : "item.") + format(material));
    }

    /*

     public static String get(@NotNull Translatable tr) {
     return getTranslation(tr.getTranslationKey());
     }
     */

    /**
     * Note: only vanilla effects are translated
     *
     * @return Translatable string with MiniMessage format
     */
    public static @NotNull String get(@NotNull PotionEffectType effectType) {
        return getTranslation("effect." + format(effectType));
    }

    public static @NotNull String get(@NotNull Biome biome) {
        return getTranslation("biome." + format(biome));
    }

    public static @NotNull String get(@NotNull DyeColor color) {
        return getTranslation("color.minecraft." + color.name().toLowerCase(Locale.ENGLISH));
    }

    public static @NotNull String get(@NotNull GameRule rule) {
        return getTranslation("gamerule." + rule.getName());
    }

    public static @NotNull String get(Attribute attribute) {
        return getTranslation("attribute.name." + attribute.getKey().getKey());
    }

    /**
     * Note: only vanilla enchantments are translated
     *
     * @return Translatable string with MiniMessage format
     */
    public static @NotNull String get(@NotNull Enchantment enchantment) {
        return getTranslation("enchantment." + format(enchantment));
    }

    public static @NotNull String get(@NotNull EntityType type) {
        return getTranslation("enchantment." + format(type));
    }

    public static @NotNull String get(@NotNull MusicInstrument type) {
        return getTranslation("instrument." + format(type));
    }

    public static @NotNull String get(@NotNull BookMeta.Generation type) {
        return getTranslation("book.generation." + type.ordinal());
    }

    public static @NotNull String get(@NotNull GameMode type) {
        return getTranslation("gameMode." + type.name().toLowerCase(Locale.ENGLISH));
    }

    public static @NotNull String get(@NotNull Statistic type) {
        return getTranslation("stat." + format(type));
    }

    public static @NotNull String get(@NotNull Pattern type) {
        return getTranslation("block.minecraft.banner." + type.getPattern().name().toLowerCase(Locale.ENGLISH) + "." + type.getColor().name().toLowerCase(Locale.ENGLISH));
    }

    public static @NotNull String get(@NotNull Difficulty type) {
        return getTranslation("options.difficulty." + type.name().toLowerCase(Locale.ENGLISH));
    }

    public static @NotNull String get(@NotNull SoundCategory type) {
        return getTranslation("soundCategory." + type.name().toLowerCase(Locale.ENGLISH));
    }

    public static @NotNull String get(@NotNull Sound type) {
        return getTranslation("subtitles." + type.getKey().getKey());
    }

    public static @NotNull String getItemDurability(int value, int max) {
        return "<tr:item.durability:'" + value + "':'" + max + "'>";
    }

    public static @NotNull String getTranslation(@NotNull String path) {
        return "<tr:" + path + ">";
    }

    private static @NotNull String format(@NotNull Keyed keyed) {
        return keyed.getKey().getNamespace() + "." + keyed.getKey().getKey();
    }
}
