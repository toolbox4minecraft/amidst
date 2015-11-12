package amidst.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import amidst.logging.Log;
import amidst.preferences.BiomeColorProfile;

public class BiomeColorMenu extends JMenu {
	private ArrayList<JCheckBoxMenuItem> profileCheckboxes = new ArrayList<JCheckBoxMenuItem>();
	private JMenuItem reloadMenuItem;

	private static class BiomeProfileActionListener implements ActionListener {
		private BiomeColorProfile profile;
		private ArrayList<JCheckBoxMenuItem> profileCheckboxes;
		private JCheckBoxMenuItem checkBox;

		public BiomeProfileActionListener(BiomeColorProfile profile,
				JCheckBoxMenuItem checkBox,
				ArrayList<JCheckBoxMenuItem> profileCheckboxes) {
			this.profile = profile;
			this.checkBox = checkBox;
			this.profileCheckboxes = profileCheckboxes;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < profileCheckboxes.size(); i++)
				profileCheckboxes.get(i).setSelected(false);
			checkBox.setSelected(true);
			profile.activate();
		}
	}

	public BiomeColorMenu() {
		super("Biome profile");
		reloadMenuItem = new JMenuItem("Reload Menu");
		final BiomeColorMenu biomeColorMenu = this;
		reloadMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg) {
				profileCheckboxes.clear();
				Log.i("Reloading additional biome color profiles.");
				File colorProfileFolder = new File("./biome");
				biomeColorMenu.removeAll();
				scanAndLoad(colorProfileFolder, biomeColorMenu);
				biomeColorMenu.add(reloadMenuItem);
			}
		});
		reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl B"));
		Log.i("Checking for additional biome color profiles.");
		File colorProfileFolder = new File("./biome");
		scanAndLoad(colorProfileFolder, this);
		profileCheckboxes.get(0).setSelected(true);
		add(reloadMenuItem);
	}

	private boolean scanAndLoad(File folder, JMenu menu) {
		File[] files = folder.listFiles();
		BiomeColorProfile profile;
		boolean foundProfiles = false;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				if ((profile = BiomeColorProfile.createFromFile(files[i])) != null) {
					JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(
							profile.name);
					menuItem.addActionListener(new BiomeProfileActionListener(
							profile, menuItem, profileCheckboxes));
					if (profile.shortcut != null) {
						KeyStroke accelerator = KeyStroke
								.getKeyStroke(profile.shortcut);
						if (accelerator != null)
							menuItem.setAccelerator(accelerator);
						else
							Log.i("Unable to create keyboard shortcut from: "
									+ profile.shortcut);
					}
					menu.add(menuItem);
					profileCheckboxes.add(menuItem);
					foundProfiles = true;
				}
			} else {
				JMenu subMenu = new JMenu(files[i].getName());
				if (scanAndLoad(files[i], subMenu)) {
					menu.add(subMenu);
				}
			}
		}
		return foundProfiles;
	}
}
