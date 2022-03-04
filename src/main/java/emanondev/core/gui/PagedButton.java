package emanondev.core.gui;

public interface PagedButton extends GuiButton {
    PagedGui getGui();

    /**
     * @return current page
     */
    default int getPage() {
        return getGui().getPage();
    }
}