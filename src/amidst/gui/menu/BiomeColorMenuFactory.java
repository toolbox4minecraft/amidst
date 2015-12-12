package amidst.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import amidst.logging.Log;
import amidst.preferences.biomecolorprofile.BiomeColorProfile;
import amidst.preferences.biomecolorprofile.BiomeColorProfileLoader;
import amidst.preferences.biomecolorprofile.BiomeColorProfileVisitor;

public class BiomeColorMenuFactory {
	private static class BiomeColorProfileVisitorImpl implements
			BiomeColorProfileVisitor {
		private final List<JCheckBoxMenuItem> allCheckBoxes = new ArrayList<JCheckBoxMenuItem>();
		private final List<JMenu> menuStack = new ArrayList<JMenu>();
		private ActionListener firstListener;
		private boolean isFirstContainer = true;

		private final Actions actions;

		private BiomeColorProfileVisitorImpl(JMenu parentMenu, Actions actions) {
			this.actions = actions;
			menuStack.add(parentMenu);
		}

		@Override
		public void enterDirectory(String name) {
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
		public void leaveDirectory() {
			removeLastMenu();
		}

		private JMenu getLastMenu() {
			return menuStack.get(menuStack.size() - 1);
		}

		private void removeLastMenu() {
			menuStack.remove(menuStack.size() - 1);
		}

		private JCheckBoxMenuItem createCheckBox(BiomeColorProfile profile) {
			JCheckBoxMenuItem result = new JCheckBoxMenuItem(profile.getName());
			tryCreateKeyboardShortcut(profile.getShortcut(), result);
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
					actions.selectBiomeColorProfile(profile);
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

	private final JMenu parentMenu = new JMenu("Biome color profile");
	private final Actions actions;
	private final BiomeColorProfileLoader biomeColorProfileLoader = new BiomeColorProfileLoader();

	public BiomeColorMenuFactory(Actions actions) {
		this.actions = actions;
		Log.i("Checking for additional biome color profiles.");
		initParentMenu();
	}

	public JMenu getMenu() {
		return parentMenu;
	}

	private void initParentMenu() {
		parentMenu.removeAll();
		BiomeColorProfile.saveDefaultProfileIfNecessary();
		BiomeColorProfileVisitorImpl visitor = new BiomeColorProfileVisitorImpl(
				parentMenu, actions);
		biomeColorProfileLoader.visitProfiles(visitor);
		parentMenu.add(createReloadMenuItem());
		visitor.selectFirstProfile();
	}

	private JMenuItem createReloadMenuItem() {
		final JMenuItem result = new JMenuItem("Reload biome color profiles");
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
