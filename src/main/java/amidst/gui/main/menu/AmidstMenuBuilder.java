package amidst.gui.main.menu;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import amidst.AmidstSettings;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.Actions;
import amidst.mojangapi.world.WorldType;
import amidst.settings.biomeprofile.BiomeProfileDirectory;

@NotThreadSafe
public class AmidstMenuBuilder {
	private final AmidstSettings settings;
	private final Actions actions;
	private final BiomeProfileDirectory biomeProfileDirectory;
	private final JMenuBar menuBar;
	private JMenu worldMenu;
	private JMenuItem savePlayerLocationsMenu;
	private JMenuItem reloadPlayerLocationsMenu;
	private LayersMenu layersMenu;

	public AmidstMenuBuilder(AmidstSettings settings, Actions actions, BiomeProfileDirectory biomeProfileDirectory) {
		this.settings = settings;
		this.actions = actions;
		this.biomeProfileDirectory = biomeProfileDirectory;
		this.menuBar = createMenuBar();
	}

	public AmidstMenu construct() {
		return new AmidstMenu(menuBar, worldMenu, savePlayerLocationsMenu, reloadPlayerLocationsMenu, layersMenu);
	}

	private JMenuBar createMenuBar() {
		JMenuBar result = new JMenuBar();
		result.add(create_File());
		worldMenu = result.add(create_World());
		result.add(create_Layers());
		result.add(create_Settings());
		result.add(create_Help());
		return result;
	}

	private JMenu create_File() {
		JMenu result = new JMenu("File");
		result.setMnemonic(KeyEvent.VK_F);
		// @formatter:off
		Menus.item(result, actions::newFromSeed,           "New from seed",            KeyEvent.VK_N, "menu N");
		Menus.item(result, actions::newFromRandom,         "New from random seed",     KeyEvent.VK_R, "menu R");
		if (new File("search.json").exists()) {
			Menus.item(result, actions::searchForRandom,     "Search for random seed",   KeyEvent.VK_F, "menu F");
		}
		Menus.item(result, actions::openSaveGame,          "Open save game ...",       KeyEvent.VK_O, "menu O");
		result.addSeparator();
		Menus.item(result, actions::switchProfile,         "Switch profile ...",       KeyEvent.VK_P, "menu W");
		Menus.item(result, actions::exit,                  "Exit",                     KeyEvent.VK_X, "menu Q");
		// @formatter:on
		return result;
	}

	private JMenu create_World() {
		JMenu result = new JMenu("World");
		result.setEnabled(false);
		result.setMnemonic(KeyEvent.VK_W);
		// @formatter:off
		Menus.item(result, actions::goToCoordinate,        "Go to Coordinate",         KeyEvent.VK_C, "menu shift C");
		Menus.item(result, actions::goToSpawn,             "Go to World Spawn",        KeyEvent.VK_S, "menu shift S");
		Menus.item(result, actions::goToStronghold,        "Go to Stronghold",         KeyEvent.VK_H, "menu shift H");
		Menus.item(result, actions::goToPlayer,            "Go to Player",             KeyEvent.VK_P, "menu shift P");
		result.addSeparator();
		savePlayerLocationsMenu =
		Menus.item(result, actions::savePlayerLocations,   "Save player locations",    KeyEvent.VK_V, "menu S");
		savePlayerLocationsMenu.setEnabled(false);
		reloadPlayerLocationsMenu =
		Menus.item(result, actions::reloadPlayerLocations, "Reload player locations",  KeyEvent.VK_R, "F5");
		reloadPlayerLocationsMenu.setEnabled(false);
		Menus.item(result, actions::howCanIMoveAPlayer,    "How can I move a player?", KeyEvent.VK_M);
		result.addSeparator();
		Menus.item(result, actions::copySeedToClipboard,   "Copy Seed to Clipboard",   KeyEvent.VK_B, "menu C");
		Menus.item(result, actions::saveCaptureImage,      "Save capture image ...",   KeyEvent.VK_I, "menu T");
		// @formatter:on
		return result;
	}

	private JMenuItem create_Layers() {
		JMenu result = new JMenu("Layers");
		result.setMnemonic(KeyEvent.VK_L);
		layersMenu = new LayersMenu(result, settings);
		return result;
	}

	private JMenu create_Settings() {
		JMenu result = new JMenu("Settings");
		result.setMnemonic(KeyEvent.VK_S);
		result.add(create_Settings_DefaultWorldType());
		if (biomeProfileDirectory.isValid()) {
			result.add(create_Settings_BiomeProfile());
		}
		result.addSeparator();
		// @formatter:off
		Menus.checkbox(result, settings.smoothScrolling,      "Smooth Scrolling",      "menu I");
		Menus.checkbox(result, settings.fragmentFading,       "Fragment Fading");
		Menus.checkbox(result, settings.maxZoom,              "Restrict Maximum Zoom", "menu Z");
		Menus.checkbox(result, settings.showFPS,              "Show Framerate",        "menu L");
		Menus.checkbox(result, settings.showScale,            "Show Scale",            "menu K");
		Menus.checkbox(result, settings.showDebug,            "Show Debug Information");
		// @formatter:on
		return result;
	}

	private JMenu create_Settings_DefaultWorldType() {
		JMenu result = new JMenu("Default world type");
		// @formatter:off
		Menus.radios(result, settings.worldType, WorldType.getWorldTypeSettingAvailableValues());
		// @formatter:on
		return result;
	}

	private JMenu create_Settings_BiomeProfile() {
		JMenu result = new JMenu("Biome profile");
		// @formatter:off
		new BiomeProfileMenuFactory(result, actions, biomeProfileDirectory, "Reload biome profiles", KeyEvent.VK_R, "menu B");
		// @formatter:on
		return result;
	}

	private JMenu create_Help() {
		JMenu result = new JMenu("Help");
		result.setMnemonic(KeyEvent.VK_H);
		// @formatter:off
		Menus.item(result, actions::checkForUpdates,       "Check for Updates",        KeyEvent.VK_U);
		Menus.item(result, actions::viewLicense,           "View Licenses",            KeyEvent.VK_L);
		Menus.item(result, actions::about,                 "About",                    KeyEvent.VK_A);
		// @formatter:on
		return result;
	}
}
