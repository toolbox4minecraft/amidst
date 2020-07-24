package amidst.gui.main;

import javax.swing.InputMap;
import javax.swing.UIManager;
import javax.swing.text.DefaultEditorKit;

import amidst.gui.main.menu.MenuShortcuts;

public enum OsXWorkarounds {
	;

	public static void applyWorkarounds() {
		menuProperties();
		textActions();
	}

	private static void menuProperties() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.macos.useScreenMenuBar", "true");
	}

	// Text actions aren't properly set on the default L&F; fix this.
	private static void textActions() {
		// Text actions aren't properly set on the default L&F; fix this
		textActionsFor("TextArea.focusInputMap");
		textActionsFor("TextField.focusInputMap");
		textActionsFor("TextPane.focusInputMap");
	}

	private static void textActionsFor(String inputMapName) {
		InputMap im = (InputMap) UIManager.get(inputMapName);
		im.put(MenuShortcuts.getKeyStroke("menu C"), DefaultEditorKit.copyAction);
		im.put(MenuShortcuts.getKeyStroke("menu V"), DefaultEditorKit.pasteAction);
		im.put(MenuShortcuts.getKeyStroke("menu X"), DefaultEditorKit.cutAction);
		im.put(MenuShortcuts.getKeyStroke("menu A"), DefaultEditorKit.selectAllAction);
	}
}
