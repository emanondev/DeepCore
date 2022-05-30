package emanondev.core.util.items;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import org.jetbrains.annotations.NotNull;

public interface ItemConverter {
    ItemBuilder convert(@NotNull YMLSection section);
}
