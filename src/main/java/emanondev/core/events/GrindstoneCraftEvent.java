package emanondev.core.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class GrindstoneCraftEvent extends CraftEvent {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    GrindstoneCraftEvent(InventoryClickEvent clickEvent) {
        super(clickEvent);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
