package emanondev.core.gui;

public interface PagedButton extends GuiButton {
	public PagedGui getGui();
	
	/**
	 * @return current page
	 */
	public default int getPage() {
		return getGui().getPage();
	}
}