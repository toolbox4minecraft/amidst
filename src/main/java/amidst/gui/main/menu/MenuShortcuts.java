package amidst.gui.main.menu;

import java.awt.Toolkit;

import javax.swing.KeyStroke;

import amidst.documentation.Immutable;

/**
 * This enum declares all menu shortcuts. This makes it easier to ensure that
 * each menu shortcut is only used for a single action.
 */
@Immutable
public enum MenuShortcuts implements MenuShortcut {
	NEW_FROM_SEED("menu N"),
	NEW_FROM_RANDOM_SEED("menu R"),
	SEARCH_FOR_RANDOM_SEED("menu F"),
	OPEN_SAVE_GAME("menu O"),
	EXPORT_BIOMES("menu X"),
	SWITCH_PROFILE("menu W"),
	EXIT("menu Q"),

	GO_TO_COORDINATE("menu shift C"),
	GO_TO_WORLD_SPAWN("menu shift S"),
	GO_TO_STRONGHOLD("menu shift H"),
	GO_TO_PLAYER("menu shift P"),
	ZOOM_IN("menu K"),
	ZOOM_OUT("menu J"),
	SAVE_PLAYER_LOCATIONS("menu S"),
	RELOAD_PLAYER_LOCATIONS("F5"),
	COPY_SEED_TO_CLIPBOARD("menu C"),
	TAKE_SCREENSHOT("menu T"),

	DISPLAY_DIMENSION_OVERWORLD("menu shift 1"),
	DISPLAY_DIMENSION_END("menu shift 2"),

	SHOW_SLIME_CHUNKS("menu 1"),
	SHOW_WORLD_SPAWN("menu 2"),
	SHOW_STRONGHOLDS("menu 3"),
	SHOW_VILLAGES("menu 4"),
	SHOW_TEMPLES("menu 5"),
	SHOW_MINESHAFTS("menu 6"),
	SHOW_OCEAN_MONUMENTS("menu 7"),
	SHOW_WOODLAND_MANSIONS("menu 8"),
	SHOW_OCEAN_FEATURES("menu 9"),
	SHOW_NETHER_FEATURES("menu 0"),

	// It's okay to duplicate the Overworld layers shortcuts here, because
	// the End layers will never be active at the same time.
	SHOW_END_CITIES("menu 1"),

	SHOW_GRID("menu G"),
	SHOW_PLAYERS("menu P"),
	ENABLE_ALL_LAYERS("menu E"),

	RELOAD_BIOME_PROFILES("menu B"),;

	public static KeyStroke getKeyStroke(String accelerator) {
		return getPlatformSpecificKeyStroke(accelerator.replace("menu", ""), accelerator.contains("menu"));
	}

	private static KeyStroke getPlatformSpecificKeyStroke(String accelerator, boolean addMenuMask) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke(accelerator);
		int keycode = keyStroke.getKeyCode();
		int keymask = keyStroke.getModifiers();
		if (addMenuMask) {
			keymask |= Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		}
		return KeyStroke.getKeyStroke(keycode, keymask);
	}

	private final KeyStroke keystroke;

	private MenuShortcuts(String shortcut) {
		this.keystroke = getKeyStroke(shortcut);
	}

	@Override
	public KeyStroke getKeyStroke() {
		return keystroke;
	}
}
