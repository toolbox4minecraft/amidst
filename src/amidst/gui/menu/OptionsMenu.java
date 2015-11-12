package amidst.gui.menu;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;

import amidst.Options;
import amidst.preferences.BiomeColorProfile;
import amidst.preferences.SelectPrefModel.SelectButtonModel;

public class OptionsMenu extends JMenu {
	public OptionsMenu() {
		super("Options");
		add(new MapOptionsMenu());
		if (BiomeColorProfile.isEnabled)
			add(new BiomeColorMenu());
		add(new WorldTypeMenu());
		setMnemonic(KeyEvent.VK_M);
	}

	private static class MapOptionsMenu extends JMenu {
		private MapOptionsMenu() {
			super("Map");

			add(new DisplayingCheckbox("Map Flicking (Smooth Scrolling)", null,
					KeyEvent.VK_I, Options.instance.mapFlicking));

			add(new DisplayingCheckbox("Restrict Maximum Zoom", null,
					KeyEvent.VK_Z, Options.instance.maxZoom));

			add(new DisplayingCheckbox("Show Framerate", null, KeyEvent.VK_L,
					Options.instance.showFPS));

			add(new DisplayingCheckbox("Show Scale", null, KeyEvent.VK_K,
					Options.instance.showScale));

			add(new DisplayingCheckbox("Use Fragment Fading", null, -1,
					Options.instance.mapFading));

			add(new DisplayingCheckbox("Show Debug Info", null, -1,
					Options.instance.showDebug));
		}

	}

	private static class WorldTypeMenu extends JMenu {
		private WorldTypeMenu() {
			super("World type");

			SelectButtonModel[] buttonModels = Options.instance.worldType
					.getButtonModels();

			for (int i = 0; i < buttonModels.length; i++) {
				add(new DisplayingCheckbox(buttonModels[i].getName(), null, -1,
						buttonModels[i]));
			}
		}

	}
}
