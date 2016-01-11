package amidst.gui.main.menu;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenu;

import amidst.AmidstSettings;
import amidst.ResourceLoader;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.Actions;
import amidst.mojangapi.world.Dimension;
import amidst.settings.Setting;
import amidst.settings.Settings;

@NotThreadSafe
public class LayersMenu {
	private final JMenu menu;
	private final Actions actions;
	private final AmidstSettings settings;
	private final Setting<Dimension> dimensionSetting;

	@CalledOnlyBy(AmidstThread.EDT)
	public LayersMenu(JMenu menu, Actions actions, AmidstSettings settings) {
		this.menu = menu;
		this.actions = actions;
		this.settings = settings;
		this.dimensionSetting = Settings.createWithListener(settings.dimension,
				this::update);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void update() {
		init();
		actions.selectDimension(dimensionSetting.get());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void init() {
		menu.removeAll();
		// @formatter:off
		Menus.checkbox(    menu, settings.showGrid,                 "Grid",                   getIcon("grid.png"),            "ctrl G");
		menu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		Menus.radio(       menu, dimensionSetting, group,           Dimension.OVERWORLD,                                      "ctrl shift 1");
		Menus.radio(       menu, dimensionSetting, group,           Dimension.END,                                            "ctrl shift 2");
		menu.addSeparator();
		if (dimensionSetting.get().equals(Dimension.OVERWORLD)) {
			Menus.checkbox(menu, settings.showSlimeChunks,          "Slime Chunks",           getIcon("slime.png"),           "ctrl 1");
			Menus.checkbox(menu, settings.showSpawn,                "Spawn Location Icon",    getIcon("spawn.png"),           "ctrl 2");
			Menus.checkbox(menu, settings.showStrongholds,          "Stronghold Icons",       getIcon("stronghold.png"),      "ctrl 3");
			Menus.checkbox(menu, settings.showPlayers,              "Player Icons",           getIcon("player.png"),          "ctrl 4");
			Menus.checkbox(menu, settings.showVillages,             "Village Icons",          getIcon("village.png"),         "ctrl 5");
			Menus.checkbox(menu, settings.showTemples,              "Temple/Witch Hut Icons", getIcon("desert.png"),          "ctrl 6");
			Menus.checkbox(menu, settings.showMineshafts,           "Mineshaft Icons",        getIcon("mineshaft.png"),       "ctrl 7");
			Menus.checkbox(menu, settings.showOceanMonuments,       "Ocean Monument Icons",   getIcon("ocean_monument.png"),  "ctrl 8");
			Menus.checkbox(menu, settings.showNetherFortresses,     "Nether Fortress Icons",  getIcon("nether_fortress.png"), "ctrl 9");
		} else if (dimensionSetting.get().equals(Dimension.END)) {
			Menus.checkbox(menu, settings.showEndCities,            "End City Icons",         getIcon("end_city.png"),        "ctrl 1");
		}
		// @formatter:on
		menu.setEnabled(true);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void disable() {
		menu.setEnabled(false);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private ImageIcon getIcon(String icon) {
		return new ImageIcon(ResourceLoader.getImage("/amidst/gui/main/icon/"
				+ icon));
	}
}
