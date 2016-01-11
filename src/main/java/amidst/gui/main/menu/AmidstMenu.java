package amidst.gui.main.menu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.World;

@NotThreadSafe
public class AmidstMenu {
	private final JMenuBar menuBar;
	private final JMenu worldMenu;
	private final JMenuItem savePlayerLocationsMenu;
	private final JMenuItem reloadPlayerLocationsMenu;
	private final LayersMenu layersMenu;

	@CalledOnlyBy(AmidstThread.EDT)
	public AmidstMenu(JMenuBar menuBar, JMenu worldMenu,
			JMenuItem savePlayerLocationsMenu,
			JMenuItem reloadPlayerLocationsMenu, LayersMenu layersMenu) {
		this.menuBar = menuBar;
		this.worldMenu = worldMenu;
		this.savePlayerLocationsMenu = savePlayerLocationsMenu;
		this.reloadPlayerLocationsMenu = reloadPlayerLocationsMenu;
		this.layersMenu = layersMenu;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public JMenuBar getMenuBar() {
		return menuBar;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void setWorld(World world) {
		worldMenu.setEnabled(true);
		savePlayerLocationsMenu.setEnabled(world.getMovablePlayerList()
				.canSave());
		reloadPlayerLocationsMenu.setEnabled(world.getMovablePlayerList()
				.canLoad());
		layersMenu.init(world);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void clearWorld() {
		worldMenu.setEnabled(false);
		savePlayerLocationsMenu.setEnabled(false);
		reloadPlayerLocationsMenu.setEnabled(false);
		layersMenu.disable();
	}
}
