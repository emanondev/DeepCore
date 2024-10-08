package emanondev.core.gui;

import emanondev.core.CorePlugin;
import emanondev.core.UtilsString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TypeGui implements Gui {

    private final Gui previousHolder;
    private final Player player;
    private final Inventory inv;
    private final InventoryType type;
    private final CorePlugin plugin;
    private final GuiButton[] buttons;
    private boolean updateOnOpen = true;

    /**
     * Create a chest-type gui
     *
     * @param title          the raw title
     * @param p              target player
     * @param previousHolder previously used gui
     * @param plugin         the plugin responsible for this gui
     */
    public TypeGui(@Nullable String title, @NotNull InventoryType type, @Nullable Player p,
                   @Nullable Gui previousHolder, @NotNull CorePlugin plugin) {
        this(title, type, p, previousHolder, plugin, false);
    }

    /**
     * Create a gui
     *
     * @param title          the raw title
     * @param type           inventory type
     * @param p              target player
     * @param previousHolder previously used gui
     * @param plugin         the plugin responsible for this gui
     * @param isTimerUpdated update the inventory each seconds as long as it has at
     *                       least a player
     */
    public TypeGui(@Nullable String title, @NotNull InventoryType type, @Nullable Player p,
                   @Nullable Gui previousHolder, @NotNull CorePlugin plugin, boolean isTimerUpdated) {
        this.previousHolder = previousHolder;
        this.player = p;
        this.type = type;
        this.inv = Bukkit.createInventory(this, type, UtilsString.fix(title, player, true));
        this.plugin = plugin;
        this.buttons = new GuiButton[inv.getSize()];
    }

    public InventoryType getInventoryType() {
        return type;
    }

    public @NotNull CorePlugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

    @Override
    public Gui getPreviousGui() {
        return previousHolder;
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (event.getRawSlot() >= 0 && event.getRawSlot() < getInventory().getSize())
            if (buttons[event.getRawSlot()] != null && buttons[event.getRawSlot()].onClick(event))
                updateInventory();
    }

    /**
     * @return true if the inventory is updated when a player open it
     */
    public boolean isUpdateOnOpen() {
        return this.updateOnOpen;
    }

    /**
     * Sets whenever the inventory should be updated when a player open it
     *
     * @param value
     */
    public void setUpdateOnOpen(boolean value) {
        this.updateOnOpen = value;
    }

    @Override
    public GuiButton getButton(int slot) {
        return buttons[slot];
    }

    @Override
    public void setButton(int slot, GuiButton button) {
        buttons[slot] = button;
    }

    @Override
    public void addButton(@NotNull GuiButton button) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearButtons() {
        for (int i = 0; i < buttons.length; i++)
            buttons[i] = null;
    }

    @Override
    public final Player getTargetPlayer() {
        return player;
    }

    @Override
    public void updateInventory() {
        for (int i = 0; i < inv.getSize(); i++)
            if (buttons[i] != null)
                inv.setItem(i, buttons[i].getItem());
    }

}
