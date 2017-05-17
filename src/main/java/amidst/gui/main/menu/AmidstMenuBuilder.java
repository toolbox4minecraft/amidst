package amidst.gui.main.menu;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import amidst.AmidstSettings;
import amidst.FeatureToggles;
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
	private JMenuItem exportMenu;
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
		return new AmidstMenu(
				menuBar,
				exportMenu,
				worldMenu,
				savePlayerLocationsMenu,
				reloadPlayerLocationsMenu,
				layersMenu);
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
		Menus.item(result, actions::newFromSeed,           "New from seed",            KeyEvent.VK_N, MenuShortcuts.NEW_FROM_SEED);
		Menus.item(result, actions::newFromRandom,         "New from random seed",     KeyEvent.VK_R, MenuShortcuts.NEW_FROM_RANDOM_SEED);
		if (FeatureToggles.SEED_SEARCH) {
			Menus.item(result, actions::searchForRandom,   "Search for random seed",   KeyEvent.VK_F, MenuShortcuts.SEARCH_FOR_RANDOM_SEED);
		}
		Menus.item(result, actions::openSaveGame,          "Open save game ...",       KeyEvent.VK_O, MenuShortcuts.OPEN_SAVE_GAME);
		result.addSeparator();
		if (FeatureToggles.WORLD_EXPORTER) {
			exportMenu =
			Menus.item(result, actions::export,            "Export ...",               KeyEvent.VK_E, MenuShortcuts.EXPORT);
			exportMenu.setEnabled(false);
			result.addSeparator();
		}
		Menus.item(result, actions::switchProfile,         "Switch profile ...",       KeyEvent.VK_P, MenuShortcuts.SWITCH_PROFILE);
		Menus.item(result, actions::exit,                  "Exit",                     KeyEvent.VK_X, MenuShortcuts.EXIT);
		// @formatter:on
		return result;
	}

	private JMenu create_World() {
		JMenu result = new JMenu("World");
		result.setEnabled(false);
		result.setMnemonic(KeyEvent.VK_W);
		// @formatter:off
		Menus.item(result, actions::goToCoordinate,        "Go to Coordinate",         KeyEvent.VK_C, MenuShortcuts.GO_TO_COORDINATE);
		Menus.item(result, actions::goToSpawn,             "Go to World Spawn",        KeyEvent.VK_S, MenuShortcuts.GO_TO_WORLD_SPAWN);
		Menus.item(result, actions::goToStronghold,        "Go to Stronghold",         KeyEvent.VK_H, MenuShortcuts.GO_TO_STRONGHOLD);
		Menus.item(result, actions::goToPlayer,            "Go to Player",             KeyEvent.VK_P, MenuShortcuts.GO_TO_PLAYER);
		result.addSeparator();
		Menus.item(result, actions::zoomIn,                "Zoom in",                  KeyEvent.VK_I, MenuShortcuts.ZOOM_IN);
		Menus.item(result, actions::zoomOut,               "Zoom out",                 KeyEvent.VK_O, MenuShortcuts.ZOOM_OUT);
		result.addSeparator();
		savePlayerLocationsMenu =
		Menus.item(result, actions::savePlayerLocations,   "Save player locations",    KeyEvent.VK_V, MenuShortcuts.SAVE_PLAYER_LOCATIONS);
		savePlayerLocationsMenu.setEnabled(false);
		reloadPlayerLocationsMenu =
		Menus.item(result, actions::reloadPlayerLocations, "Reload player locations",  KeyEvent.VK_R, MenuShortcuts.RELOAD_PLAYER_LOCATIONS);
		reloadPlayerLocationsMenu.setEnabled(false);
		Menus.item(result, actions::howCanIMoveAPlayer,    "How can I move a player?", KeyEvent.VK_M);
		result.addSeparator();
		Menus.item(result, actions::copySeedToClipboard,   "Copy Seed to Clipboard",   KeyEvent.VK_B, MenuShortcuts.COPY_SEED_TO_CLIPBOARD);
		Menus.item(result, actions::takeScreenshot,        "Take Screenshot ...",      KeyEvent.VK_T, MenuShortcuts.TAKE_SCREENSHOT);
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
		Menus.checkbox(result, settings.smoothScrolling,      "Smooth Scrolling");
		Menus.checkbox(result, settings.fragmentFading,       "Fragment Fading");
		Menus.checkbox(result, settings.maxZoom,              "Restrict Maximum Zoom");
		Menus.checkbox(result, settings.showFPS,              "Show Framerate");
		Menus.checkbox(result, settings.showScale,            "Show Scale");
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
		new BiomeProfileMenuFactory(result, actions, biomeProfileDirectory, "Reload biome profiles", KeyEvent.VK_R, MenuShortcuts.RELOAD_BIOME_PROFILES);
		// @formatter:on
		return result;
	}

	private JMenu create_Help() {
		JMenu result = new JMenu("Help");
		result.setMnemonic(KeyEvent.VK_H);
		// @formatter:off
		Menus.item(result, actions::displayLogMessages,    "Display Log Messages",     KeyEvent.VK_M);
		Menus.item(result, actions::checkForUpdates,       "Check for Updates",        KeyEvent.VK_U);
		Menus.item(result, actions::viewLicense,           "View Licenses",            KeyEvent.VK_L);
		Menus.item(result, actions::about,                 "About",                    KeyEvent.VK_A);
		// @formatter:on
		return result;
	}
}
