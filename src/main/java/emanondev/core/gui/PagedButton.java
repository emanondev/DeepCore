package emanondev.core.gui;

import org.jetbrains.annotations.NotNull;

public interface PagedButton extends GuiButton {
    @NotNull PagedGui getGui();

    /**
     * @return current page
     */
    default int getPage() {
        return getGui().getPage();
    }
}