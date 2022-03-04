package emanondev.core.spigot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;

import emanondev.core.CorePlugin;
import emanondev.core.PermissionBuilder;
import net.md_5.bungee.api.ChatColor;

public class UpdateChecker {

    private final CorePlugin plugin;
    private final String localPluginVersion;
    private String spigotPluginVersion;
    private final int ID; // The ID of your resource. Can be found in the resource URL.
    private final boolean notifyOP;
    private final Permission updatePerm;

    public UpdateChecker(final CorePlugin plugin, int id, boolean notifyOP) {
        this.ID = id;
        this.plugin = plugin;
        this.localPluginVersion = plugin.getDescription().getVersion();
        this.updatePerm = new PermissionBuilder(plugin, "update_notify").build();
        this.notifyOP = notifyOP;
        this.plugin.registerPermission(updatePerm);
        checkForUpdate();
    }

    private void checkForUpdate() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final HttpsURLConnection connection = (HttpsURLConnection) new URL(
                        "https://api.spigotmc.org/legacy/update.php?resource=" + ID).openConnection();
                connection.setRequestMethod("GET");
                spigotPluginVersion = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                        .readLine();
            } catch (final IOException e) {
                Bukkit.getServer().getConsoleSender().sendMessage(
                        plugin.getConfig().loadMessage("update.error_message", "&cUpdate checker failed!", true));
                e.printStackTrace();
                return;
            }

            // Check if the requested version is the same as the one in your plugin.yml.
            if (localPluginVersion.equals(spigotPluginVersion))
                return;

            plugin.logIssue(plugin.getConfig().loadMessage("update.message",
                    "A new update is available at:&b https://www.spigotmc.org/resources/" + ID + "/updates",
                    true));

            if (notifyOP)
                // Register the PlayerJoinEvent
                Bukkit.getScheduler().runTask(plugin, () -> plugin.registerListener(new Listener() {
                    @EventHandler(priority = EventPriority.MONITOR)
                    public void onPlayerJoin(final PlayerJoinEvent event) {
                        if (!event.getPlayer().hasPermission(updatePerm))
                            return;
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.getConfig().loadMessage("update.message",
                                        "A new update is available at:&b https://www.spigotmc.org/resources/"
                                                + ID + "/updates",
                                        true)));
                    }
                }));
        });
    }
}