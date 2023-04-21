package emanondev.core.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class StonecutterCraftEvent extends CraftEvent {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    StonecutterCraftEvent(InventoryClickEvent clickEvent) {
        super(clickEvent);
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

}