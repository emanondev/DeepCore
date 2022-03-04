package emanondev.core.gui;

public interface PagedGui extends Gui {
    int getPage();

    boolean setPage(int i);

    default boolean incPage() {
        return setPage(getPage() + 1);
    }

    default boolean decPage() {
        return setPage(getPage() - 1);
    }

    int getMaxPage();

}
