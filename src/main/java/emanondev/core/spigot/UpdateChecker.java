package emanondev.core.spigot;

import emanondev.core.CoreMain;
import emanondev.core.CorePlugin;
import emanondev.core.PermissionBuilder;
import emanondev.core.UtilsMessages;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {

    private final CorePlugin plugin;
    private final String localPluginVersion;
    private final int ID; // The ID of your resource. Can be found in the resource URL.
    private final boolean notifyOP;
    private final Permission updatePerm;
    private String spigotPluginVersion;


    public UpdateChecker(CorePlugin plugin, int id) {
        this(plugin, id, CoreMain.get().getConfig().loadBoolean("updates.notify_admin", true));
    }

    public UpdateChecker(CorePlugin plugin, int id, boolean notifyOP) {
        this.ID = id;
        this.plugin = plugin;
        this.localPluginVersion = plugin.getDescription().getVersion();
        this.updatePerm = PermissionBuilder.ofPlugin(plugin, "update_notify").build();
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
                plugin.logIssue(CoreMain.get().getLanguageConfig(null).loadMessage("update.console_error_message", "", true, "%plugin%", plugin.getName()));
                e.printStackTrace();
                return;
            }

            // Check if the requested version is the same as the one in your plugin.yml.
            if (localPluginVersion.equals(spigotPluginVersion))
                return;

            plugin.logIssue(CoreMain.get().getLanguageConfig(null).loadMessage("update.console_message",
                    "",
                    true, "%plugin%", plugin.getName(), "%link%", "https://www.spigotmc.org/resources/"
                            + ID + "/updates"));

            if (notifyOP)
                // Register the PlayerJoinEvent
                Bukkit.getScheduler().runTask(plugin, () -> plugin.registerListener(new Listener() {
                    @EventHandler(priority = EventPriority.MONITOR)
                    public void onPlayerJoin(final PlayerJoinEvent event) {
                        if (!event.getPlayer().hasPermission(updatePerm))
                            return;
                        UtilsMessages.sendMessage(event.getPlayer(), CoreMain.get().getLanguageConfig(event.getPlayer()).loadMessage("update.admin_message",
                                "",
                                true, "%plugin%", plugin.getName(), "%link%", "https://www.spigotmc.org/resources/"
                                        + ID + "/updates"));
                    }
                }));
        });
    }
}