package emanondev.core.util.items;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;

public interface ItemConverter {
    ItemBuilder convert(@NotNull YMLSection section);
}
