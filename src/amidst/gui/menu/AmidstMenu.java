package amidst.gui.menu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class AmidstMenu {
	private final JMenuBar menuBar;
	private final JMenu worldMenu;
	private final JMenuItem savePlayerLocationsMenu;
	private final JMenuItem reloadPlayerLocationsMenu;

	public AmidstMenu(JMenuBar menuBar, JMenu worldMenu,
			JMenuItem savePlayerLocationsMenu,
			JMenuItem reloadPlayerLocationsMenu) {
		this.menuBar = menuBar;
		this.worldMenu = worldMenu;
		this.savePlayerLocationsMenu = savePlayerLocationsMenu;
		this.reloadPlayerLocationsMenu = reloadPlayerLocationsMenu;
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	public void setWorldMenuEnabled(boolean isEnabled) {
		worldMenu.setEnabled(isEnabled);
	}

	public void setSavePlayerLocationsMenuEnabled(boolean isEnabled) {
		savePlayerLocationsMenu.setEnabled(isEnabled);
	}

	public void setReloadPlayerLocationsMenuEnabled(boolean isEnabled) {
		reloadPlayerLocationsMenu.setEnabled(isEnabled);
	}
}
