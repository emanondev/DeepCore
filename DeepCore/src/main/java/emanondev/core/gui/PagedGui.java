package emanondev.core.gui;

public interface PagedGui extends Gui {
	public int getPage();
	
	public boolean setPage(int i);
	
	public default boolean incPage() {
		return setPage(getPage()+1);
	}
	
	public default boolean decPage() {
		return setPage(getPage()-1);
	}
	
	public int getMaxPage();

}
