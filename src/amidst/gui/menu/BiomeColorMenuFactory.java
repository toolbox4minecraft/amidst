package amidst.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import amidst.gui.MapWindow;
import amidst.logging.Log;
import amidst.preferences.BiomeColorProfile;

public class BiomeColorMenuFactory {
	private static interface BiomeColorProfileVisitor {
		void enterFolder(String name);

		void visitProfile(BiomeColorProfile profile);

		void leaveFolder();
	}

	private static class BiomeColorProfileVisitorImpl implements
			BiomeColorProfileVisitor {
		private List<JCheckBoxMenuItem> allCheckBoxes = new ArrayList<JCheckBoxMenuItem>();
		private List<JMenu> menuStack = new ArrayList<JMenu>();
		private ActionListener firstListener;
		private boolean isFirstContainer = true;
		private MapWindow mapWindow;

		private BiomeColorProfileVisitorImpl(JMenu parentMenu,
				MapWindow mapWindow) {
			this.mapWindow = mapWindow;
			menuStack.add(parentMenu);
		}

		@Override
		public void enterFolder(String name) {
			if (isFirstContainer) {
				isFirstContainer = false;
			} else {
				JMenu newMenu = new JMenu(name);
				getLastMenu().add(newMenu);
				menuStack.add(newMenu);
			}
		}

		@Override
		public void visitProfile(BiomeColorProfile profile) {
			JCheckBoxMenuItem checkBox = createCheckBox(profile);
			allCheckBoxes.add(checkBox);
			getLastMenu().add(checkBox);
		}

		@Override
		public void leaveFolder() {
			removeLastMenu();
		}

		private JMenu getLastMenu() {
			return menuStack.get(menuStack.size() - 1);
		}

		private void removeLastMenu() {
			menuStack.remove(menuStack.size() - 1);
		}

		private JCheckBoxMenuItem createCheckBox(BiomeColorProfile profile) {
			JCheckBoxMenuItem result = new JCheckBoxMenuItem(profile.name);
			tryCreateKeyboardShortcut(profile.shortcut, result);
			result.addActionListener(createListener(profile, result));
			return result;
		}

		private void tryCreateKeyboardShortcut(String shortcut,
				JCheckBoxMenuItem checkBox) {
			if (shortcut != null) {
				KeyStroke accelerator = KeyStroke.getKeyStroke(shortcut);
				if (accelerator != null) {
					checkBox.setAccelerator(accelerator);
				} else {
					Log.i("Unable to create keyboard shortcut from: "
							+ shortcut);
				}
			}
		}

		private ActionListener createListener(final BiomeColorProfile profile,
				final JCheckBoxMenuItem selectedCheckBox) {
			ActionListener result = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (JCheckBoxMenuItem checkBox : allCheckBoxes) {
						checkBox.setSelected(false);
					}
					selectedCheckBox.setSelected(true);
					profile.activate();
					mapWindow.repaintBiomeLayer();
				}
			};
			if (firstListener == null) {
				firstListener = result;
			}
			return result;
		}

		public void selectFirstProfile() {
			if (firstListener != null) {
				firstListener.actionPerformed(null);
			}
		}
	}

	private JMenu parentMenu = new JMenu("Biome profile");
	private MapWindow mapWindow;

	public BiomeColorMenuFactory(MapWindow mapWindow) {
		this.mapWindow = mapWindow;
		Log.i("Checking for additional biome color profiles.");
		initParentMenu();
	}

	public JMenu getMenu() {
		return parentMenu;
	}

	private void initParentMenu() {
		parentMenu.removeAll();
		BiomeColorProfileVisitorImpl visitor = new BiomeColorProfileVisitorImpl(
				parentMenu, mapWindow);
		createMenuItems(new File("./biome"), visitor);
		parentMenu.add(createReloadMenuItem());
		visitor.selectFirstProfile();
	}

	private void createMenuItems(File folder, BiomeColorProfileVisitor visitor) {
		boolean entered = false;
		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				BiomeColorProfile profile = BiomeColorProfile
						.createFromFile(file);
				if (profile != null) {
					if (!entered) {
						entered = true;
						visitor.enterFolder(folder.getName());
					}
					visitor.visitProfile(profile);
				}
			} else {
				createMenuItems(file, visitor);
			}
		}
		if (entered) {
			visitor.leaveFolder();
		}
	}

	private JMenuItem createReloadMenuItem() {
		final JMenuItem result = new JMenuItem("Reload Menu");
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg) {
				Log.i("Reloading additional biome color profiles.");
				initParentMenu();
			}
		});
		return result;
	}
}
