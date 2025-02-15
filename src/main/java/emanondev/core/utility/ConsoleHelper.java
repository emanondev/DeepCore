package emanondev.core.utility;

import net.md_5.bungee.api.ChatColor;


public interface ConsoleHelper {


    /**
     * logs on console message with plugin name and a green ✓ prefix
     *
     * @param log Message to print
     */
    default void logDone(String log) {
        logDone(ChatColor.GREEN, log);
    }

    /**
     * logs on console message with plugin name and a colored ✓ prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logDone(ChatColor color, String log) {
        log(color + "✓ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console Message to print with plugin prefix
     *
     * @param log Message to print
     */
    void log(String log);

    /**
     * logs on console message with plugin name and a colored ✓ prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logDone(org.bukkit.ChatColor color, String log) {
        log(color + "✓ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and red ✗ prefix
     *
     * @param log Message to print
     */
    default void logProblem(String log) {
        logProblem(ChatColor.RED, log);
    }

    /**
     * logs on console message with plugin name and colored ✗ prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logProblem(ChatColor color, String log) {
        log(color + "✗ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and colored ✗ prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logProblem(org.bukkit.ChatColor color, String log) {
        log(color + "✗ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and yellow signal prefix
     *
     * @param log Message to print
     */
    default void logIssue(String log) {
        logIssue(ChatColor.YELLOW, log);
    }

    /**
     * logs on console message with plugin name and colored signal prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logIssue(ChatColor color, String log) {
        log(color + "⚠ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and colored signal prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logIssue(org.bukkit.ChatColor color, String log) {
        log(color + "⚠  " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and colored ✧ prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logTetraStar(ChatColor color, String log) {
        log(color + "✧ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and colored ✧ prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logTetraStar(org.bukkit.ChatColor color, String log) {
        log(color + "✧ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and colored ✧ prefix
     *
     * @param log Message to print
     */
    default void logInfo(String log) {
        log(ChatColor.BLUE + "✧ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and colored ✧ prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logInfo(ChatColor color, String log) {
        log(color + "✧ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and colored ✧ prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logInfo(org.bukkit.ChatColor color, String log) {
        log(color + "✧ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and colored ☆ prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logPentaStar(ChatColor color, String log) {
        log(color + "☆ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and colored ☆ prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logPentaStar(org.bukkit.ChatColor color, String log) {
        log(color + "☆ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and colored ☆ prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logStar(ChatColor color, String log) {
        log(color + "☆ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and colored ☆ prefix
     *
     * @param log   Message to print
     * @param color Symbol color
     */
    default void logStar(org.bukkit.ChatColor color, String log) {
        log(color + "☆ " + ChatColor.WHITE + log);
    }

    /**
     * logs on console message with plugin name and colored ☆ prefix
     *
     * @param log Message to print
     */
    default void logPentaStar(String log) {
        log(ChatColor.YELLOW + "☆ " + ChatColor.WHITE + log);
    }

}
