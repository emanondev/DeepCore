package emanondev.core.gui;

import org.jetbrains.annotations.NotNull;

public interface PagedButton extends GuiButton {
    /**
     * @return current page
     */
    default int getPage() {
        return getGui().getPage();
    }

    @NotNull
    PagedGui getGui();
}