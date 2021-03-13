package emanondev.core;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.economy.Economy;

public class VaultEconomyHandler {

	private final static Economy economy = Bukkit.getServer().getServicesManager()
			.getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();

	private static final DecimalFormat format = createFormat();

	private static DecimalFormat createFormat() {
		DecimalFormat format = new DecimalFormat("#.0");
		int fractions = economy.fractionalDigits();
		if (fractions == -1)
			fractions = 0;
		format.setMinimumFractionDigits(fractions);
		format.setMaximumFractionDigits(fractions);
		format.setRoundingMode(RoundingMode.DOWN);
		return format;
	}

	/**
	 * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param player to check
	 * @param amount to check for
	 * @return True if <b>player</b> has <b>amount</b>, False else wise
	 */
	public boolean hasMoney(OfflinePlayer player, double amount) {
		return getMoney(player) >= amount;
	}

	/**
	 * Gets balance of a player
	 * 
	 * @param player of the player
	 * @return Amount currently held in players account
	 */
	public double getMoney(OfflinePlayer player) {
		return economy.getBalance(player);
	}

	/**
	 * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param player to deposit to
	 * @param amount Amount to deposit
	 * @return True if operation was successful
	 */
	public boolean addMoney(OfflinePlayer player, double amount) {
		return economy.depositPlayer(player, amount).transactionSuccess();
	}

	/**
	 * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param player to withdraw from
	 * @param amount Amount to withdraw
	 * @return True if operation was successful
	 */
	public boolean removeMoney(OfflinePlayer player, double amount) {
		return economy.withdrawPlayer(player, amount).transactionSuccess();
	}

	/**
	 * Transfert an amount from a player to another - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param from   to withdraw from
	 * @param to     to deposit to
	 * @param amount Amount to transfert
	 * @return True if operation was successful
	 */
	public boolean payMoney(OfflinePlayer from, OfflinePlayer to, double amount) {
		if (from == null || to == null)
			throw new NullPointerException();
		if (!removeMoney(from, amount))
			return false;
		if (addMoney(to, amount))
			return true;

		if (addMoney(from, amount))
			return false;
		CoreMain.get().logProblem("Transaction failed multiple times &e" + from.getName() + "&f (&e"
				+ from.getUniqueId() + "&f) lost &e" + amount + "&f $");
		CoreMain.get().logOnFile("economy_errors.txt", "Transaction failed multiple times &e" + from.getName()
				+ "&f (&e" + from.getUniqueId() + "&f) lost &e" + amount + "&f $");
		return false;
	}

	/**
	 * Some economy plugins round off after a certain number of digits. This
	 * function returns the number of digits the plugin keeps or -1 if no rounding
	 * occurs.
	 * 
	 * @return number of digits after the decimal point kept
	 */
	public int fractionalDigits() {
		return economy.fractionalDigits();
	}

	/**
	 * Format a value with economy fractional digits amount
	 * 
	 * @param value Value to format
	 * @return string value of formatted amount
	 */
	public String format(double value) {
		return format.format(value);
	}

	public String getCurrencyNamePlural() {
		return economy.currencyNamePlural();
	}

	public String getCurrencyNameSingular() {
		return economy.currencyNameSingular();
	}

}
