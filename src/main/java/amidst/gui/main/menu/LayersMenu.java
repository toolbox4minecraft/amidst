package amidst.gui.main.menu;

import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import amidst.AmidstSettings;
import amidst.ResourceLoader;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.Actions;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.World;
import amidst.settings.Setting;
import amidst.settings.Settings;

@NotThreadSafe
public class LayersMenu {
	private final JMenu menu;
	private final Actions actions;
	private final AmidstSettings settings;
	private final Setting<Dimension> dimensionSetting;
	private final Setting<Boolean> unlockAll;
	private final List<JMenuItem> overworldMenuItems = new LinkedList<JMenuItem>();
	private final List<JMenuItem> endMenuItems = new LinkedList<JMenuItem>();
	private volatile World world;

	@CalledOnlyBy(AmidstThread.EDT)
	public LayersMenu(JMenu menu, Actions actions, AmidstSettings settings) {
		this.menu = menu;
		this.actions = actions;
		this.settings = settings;
		this.dimensionSetting = Settings.createWithListener(settings.dimension,
				this::selectDimension);
		this.unlockAll = Settings.createDummyWithListener(false, this::reinit);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void selectDimension() {
		reinit();
		actions.selectDimension(dimensionSetting.get());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void reinit() {
		init(world);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void init(World world) {
		if (world == null) {
			disable();
			return;
		}
		this.world = world;
		menu.removeAll();
		overworldMenuItems.clear();
		endMenuItems.clear();
		// @formatter:off
		Menus.checkbox(menu, settings.showGrid,           "Grid",                   getIcon("grid.png"),            "ctrl G");
		menu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		Menus.radio(   menu, dimensionSetting, group,     Dimension.OVERWORLD,                                      "ctrl shift 1");
		overworldLayer(settings.showSlimeChunks,          "Slime Chunks",           getIcon("slime.png"),           "ctrl 1", false);
		overworldLayer(settings.showSpawn,                "Spawn Location Icon",    getIcon("spawn.png"),           "ctrl 2", true);
		overworldLayer(settings.showStrongholds,          "Stronghold Icons",       getIcon("stronghold.png"),      "ctrl 3", true);
		overworldLayer(settings.showPlayers,              "Player Icons",           getIcon("player.png"),          "ctrl 4", true);
		overworldLayer(settings.showVillages,             "Village Icons",          getIcon("village.png"),         "ctrl 5", true);
		overworldLayer(settings.showTemples,              "Temple/Witch Hut Icons", getIcon("desert.png"),          "ctrl 6", false);
		overworldLayer(settings.showMineshafts,           "Mineshaft Icons",        getIcon("mineshaft.png"),       "ctrl 7", false);
		overworldLayer(settings.showOceanMonuments,       "Ocean Monument Icons",   getIcon("ocean_monument.png"),  "ctrl 8", true);
		overworldLayer(settings.showNetherFortresses,     "Nether Fortress Icons",  getIcon("nether_fortress.png"), "ctrl 9", true);
		menu.addSeparator();
		Menus.radio(   menu, dimensionSetting, group,     Dimension.END,                                            "ctrl shift 2");
		endLayer(      settings.showEndCities,            "End City Icons",         getIcon("end_city.png"),        "ctrl 0", true);
		menu.addSeparator();
		Menus.checkbox(menu, unlockAll,                   "Unlock All");
		// @formatter:on
		menu.setEnabled(true);
		if (dimensionSetting.get().equals(Dimension.OVERWORLD)) {
			endMenuItems.forEach(menuItem -> menuItem.setEnabled(false));
		} else if (dimensionSetting.get().equals(Dimension.END)) {
			overworldMenuItems.forEach(menuItem -> menuItem.setEnabled(false));
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void overworldLayer(Setting<Boolean> setting, String text,
			ImageIcon icon, String accelerator, boolean enabled) {
		JCheckBoxMenuItem result = Menus.checkbox(menu, setting, text, icon,
				accelerator);
		if (!enabled) {
			result.setEnabled(false);
		}
		overworldMenuItems.add(result);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void endLayer(Setting<Boolean> setting, String text, ImageIcon icon,
			String accelerator, boolean enabled) {
		JCheckBoxMenuItem result = Menus.checkbox(menu, setting, text, icon,
				accelerator);
		if (!enabled) {
			result.setEnabled(false);
		}
		endMenuItems.add(result);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void disable() {
		this.world = null;
		menu.setEnabled(false);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private ImageIcon getIcon(String icon) {
		return new ImageIcon(ResourceLoader.getImage("/amidst/gui/main/icon/"
				+ icon));
	}
}
