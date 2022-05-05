package emanondev.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import emanondev.core.CorePlugin;
import emanondev.core.UtilsString;

public abstract class AnvilGui implements TextGui {

    private final Gui previousHolder;
    private final Player player;
    private final Inventory inv;
    private final CorePlugin plugin;
    private boolean updateOnOpen = true;
    private boolean timerUpdated = false;

    /**
     * Create a anvil-type gui
     *
     * @param title          the raw title
     * @param p              target player
     * @param previousHolder previously used gui
     * @param plugin         the plugin responsible for this gui
     */
    public AnvilGui(@Nullable String title, @Nullable Player p, @Nullable Gui previousHolder, @NotNull CorePlugin plugin) {
        this(title, p, previousHolder, plugin, false);
    }

    /**
     * Create a anvil-type gui
     *
     * @param title          the raw title
     * @param p              target player
     * @param previousHolder previously used gui
     * @param plugin         the plugin responsible for this gui
     * @param isTimerUpdated update the inventory each seconds as long as it has at least a player
     */
    public AnvilGui(@Nullable String title, @Nullable Player p, @Nullable Gui previousHolder, @NotNull CorePlugin plugin, boolean isTimerUpdated) {
        this.previousHolder = previousHolder;
        this.player = p;
        this.inv = Bukkit.createInventory(this, InventoryType.ANVIL, UtilsString.fix(title, player, true));
        this.plugin = plugin;
        if (isTimerUpdated)
            GuiHandler.registerTimerUpdatedGui(this);
    }

    public @NotNull CorePlugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

    @Override
    public final Player getTargetPlayer() {
        return player;
    }


    @Override
    public Gui getPreviousGui() {
        return previousHolder;
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
    }

    /**
     * Sets whenever the inventory should be updated when a player open it
     *
     * @param value whenever the inventory should be updated when a player open it or not
     */
    @Override
    public void setUpdateOnOpen(boolean value) {
        this.updateOnOpen = value;
    }

    /**
     * @return true if the inventory is updated when a player open it
     */
    @Override
    public boolean isUpdateOnOpen() {
        return this.updateOnOpen;
    }

    @Override
    public boolean isTimerUpdated() {
        return timerUpdated;
    }

    @Override
    public void setTimerUpdated(boolean value) {
        timerUpdated = value;
    }

}
