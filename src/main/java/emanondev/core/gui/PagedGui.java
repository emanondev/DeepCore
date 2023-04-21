package emanondev.core.gui;

public interface PagedGui extends Gui {
    default boolean incPage() {
        return setPage(getPage() + 1);
    }

    boolean setPage(int i);

    int getPage();

    default boolean decPage() {
        return setPage(getPage() - 1);
    }

    int getMaxPage();

}
