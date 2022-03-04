package emanondev.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemBuilder {
	private final ItemStack result;
	private ItemMeta resultMeta;

	/**
	 * Sets unbreakable.<br>
	 * Sets all {@link ItemFlag}.
	 * 
	 * @return this for chaining.
	 */
	public ItemBuilder setGuiProperty() {
		setUnbreakable(true);
		hideAllFlags();
		if (!resultMeta.hasDisplayName())
			resultMeta.setDisplayName(" ");
		return this;
	}

	public ItemBuilder(@NotNull Material result) {
		this(new ItemStack(result));
	}

	public ItemBuilder(@NotNull ItemStack result) {
		this.result = new ItemStack(result);
		this.resultMeta = this.result.getItemMeta();
	}

	/**
	 * 
	 * @return created ItemStack.
	 */
	public ItemStack build() {
		this.result.setItemMeta(this.resultMeta);
		return this.result;
	}

	/**
	 * 
	 * @param value Should this be unbreakable?
	 * @return this for chaining.
	 */
	public ItemBuilder setUnbreakable(boolean value) {
		this.resultMeta.setUnbreakable(value);
		return this;
	}

	/**
	 * 
	 * @param flag Adds the flag to this
	 * @return this for chaining.
	 */
	public ItemBuilder addFlag(ItemFlag flag) {
		this.resultMeta.addItemFlags(flag);
		return this;
	}

	/**
	 * 
	 * @param displayName Set displayName of this
	 * @return this for chaining.
	 */
	public ItemBuilder setDisplayName(String displayName) {

		this.resultMeta.setDisplayName(displayName);
		return this;
	}

	/**
	 * Adds line to lore.
	 * 
	 * @param line Line to add.
	 * @return this for chaining.
	 */
	public ItemBuilder addLore(String line) {
		if (line == null)
			return this;
		List<String> lore = this.resultMeta.hasLore()? this.resultMeta.getLore():new ArrayList<>();
		lore.add(line);
		this.resultMeta.setLore(lore);
		return this;
	}

	/**
	 * Sets lore.
	 * 
	 * @param lore Lore to set.
	 * @return this for chaining.
	 */
	public ItemBuilder setLore(@Nullable List<String> lore) {
		this.resultMeta.setLore(lore);
		return this;
	}

	/**
	 * Add enchantment.
	 * 
	 * @param enchantment Enchantment to set.
	 * @param level       Level to set.
	 * @return this for chaining.
	 */
	public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
		if (level == 0)
			this.resultMeta.removeEnchant(enchantment);
		else
			this.resultMeta.addEnchant(enchantment, level, true);
		return this;
	}

	/**
	 * For both Potion and Leather Armor
	 * 
	 * @param color Applied color.
	 * @return this for chaining.
	 * @throws IllegalStateException if this meta is not LeatherArmorMeta nor
	 *                               PotionMeta
	 */
	public ItemBuilder setColor(Color color) {
		if (resultMeta instanceof LeatherArmorMeta) {
			((LeatherArmorMeta) this.resultMeta).setColor(color);
			return this;
		} else if (resultMeta instanceof PotionMeta) {
			((PotionMeta) this.resultMeta).setColor(color);
			return this;
		} else
			new IllegalStateException("meta is not LeatherArmorMeta nor PotionMeta").printStackTrace();
		return this;
	}

	/**
	 * Set author name
	 * 
	 * @param name Name of the Author.
	 * @return this for chaining.
	 * @throws IllegalStateException if this meta is not BookMeta
	 */
	public ItemBuilder setAuthor(String name) {
		if (this.resultMeta instanceof BookMeta)
			((BookMeta) this.resultMeta).setAuthor(name);
		else
			new IllegalStateException("meta is not BookMeta").printStackTrace();
		return this;
	}

	/**
	 * Set book title.
	 * 
	 * @param title Title of the book.
	 * @return this for chaining.
	 * @throws IllegalStateException if this meta is not BookMeta
	 */
	public ItemBuilder setTitle(String title) {
		if (this.resultMeta instanceof BookMeta)
			((BookMeta) this.resultMeta).setTitle(title);
		else
			new IllegalStateException("meta is not BookMeta").printStackTrace();
		return this;
	}

	/**
	 * Set book title.
	 * 
	 * @param page Index of the page to set.
	 * @param text Text of the page.
	 * @return this for chaining.
	 * @throws IllegalStateException if this meta is not BookMeta
	 */
	public ItemBuilder setPage(int page, String text) {
		if (this.resultMeta instanceof BookMeta)
			((BookMeta) this.resultMeta).setPage(page, text);
		else
			new IllegalStateException("meta is not BookMeta").printStackTrace();
		return this;
	}

	/**
	 * Add Potion effect.
	 * 
	 * @param effect Effect to add to Potion.
	 * @return this for chaining.
	 * @throws IllegalStateException if this meta is not PotionMeta
	 */
	public ItemBuilder addPotionEffect(PotionEffect effect) {
		if (this.resultMeta instanceof PotionMeta)
			((PotionMeta) this.resultMeta).addCustomEffect(effect, true);
		else
			new IllegalStateException("meta is not PotionMeta").printStackTrace();
		return this;
	}

	@SuppressWarnings("deprecation")
	public ItemBuilder setSkullOwner(String name) {
		if (this.resultMeta instanceof SkullMeta)
			((SkullMeta) this.resultMeta).setOwner(name);
		else
			new IllegalStateException("meta is not SkullMeta").printStackTrace();
		return this;
	}

	public ItemBuilder setSkullOwner(OfflinePlayer player) {
		if (this.resultMeta instanceof SkullMeta)
			((SkullMeta) this.resultMeta).setOwningPlayer(player);
		else
			new IllegalStateException("meta is not SkullMeta").printStackTrace();
		return this;
	}

	public ItemBuilder setDamage(int dmg) {
		if (this.resultMeta instanceof Damageable)
			((Damageable) this.resultMeta).setDamage(dmg);
		else
			new IllegalStateException("meta is not Damageable").printStackTrace();
		return this;
	}

	public ItemBuilder setAmount(int amount) {
		if (amount > 0)
			this.result.setAmount(amount);
		else
			new IllegalStateException("invalid amount (" + amount + ")").printStackTrace();
		return this;
	}

	public ItemBuilder clone() {
		ItemBuilder copy = new ItemBuilder(this.result);
		copy.resultMeta = this.resultMeta.clone();
		return copy;
	}

	/**
	 * Sets all {@link ItemFlag} on this
	 * 
	 * @return this for chaining.
	 */
	public ItemBuilder hideAllFlags() {
		this.resultMeta.addItemFlags(ItemFlag.values());
		return this;
	}

	public ItemBuilder addPattern(DyeColor color, PatternType patternType) {
		if (this.resultMeta instanceof BannerMeta)
			((BannerMeta) this.resultMeta).addPattern(new Pattern(color, patternType));
		else
			new IllegalStateException("meta is not BannerMeta").printStackTrace();
		return this;
	}

	/**
	 * Update both title and lore with description
	 * 
	 * @param description Description text
	 * @param holders     Additional placeholders to replace in the format
	 *                    "holder1", "value1", "holder2", "value2"...
	 * @return this for chaining.
	 */
	public ItemBuilder setDescription(List<String> description, String... holders) {
		setDescription(description, true, null, holders);
		return this;
	}

	/**
	 * Update both title and lore with description
	 * 
	 * @param description Text to set
	 * @param color       Whether translate color codes or not
	 * @param holders     Additional placeholders to replace in the format
	 *                    "holder1", "value1", "holder2", "value2"...
	 * @return this for chaining.
	 */
	public ItemBuilder setDescription(List<String> description, boolean color, String... holders) {
		setDescription(description, color, null, holders);
		return this;
	}

	/**
	 * update both title and lore
	 * 
	 * @param description Text to set
	 * @param color       Whether translate color codes or not
	 * @param player      Player target for PlaceHolderAPI holders
	 * @param holders     Additional placeholders to replace in the format
	 *                    "holder1", "value1", "holder2", "value2"...
	 * @return this for chaining.
	 */
	public ItemBuilder setDescription(List<String> description, boolean color, Player player, String... holders) {
		List<String> list = UtilsString.fix(description, player, color, holders);
		if (list == null || list.isEmpty()) {
			this.resultMeta.setDisplayName(null);
			this.resultMeta.setLore(null);
		} else if (list.size() == 1) {
			this.resultMeta.setDisplayName(list.get(0));
			this.resultMeta.setLore(null);
		} else {
			this.resultMeta.setDisplayName(list.remove(0));
			this.resultMeta.setLore(list);
		}
		return this;
	}
}
