package amidst.gui.main.menu;

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

import amidst.documentation.NotThreadSafe;
import amidst.gui.main.Actions;
import amidst.logging.Log;
import amidst.settings.biomecolorprofile.BiomeColorProfile;
import amidst.settings.biomecolorprofile.BiomeColorProfileDirectory;
import amidst.settings.biomecolorprofile.BiomeColorProfileVisitor;

@NotThreadSafe
public class BiomeColorMenuFactory {
	@NotThreadSafe
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

	private final JMenu parentMenu;
	private final Actions actions;
	private final BiomeColorProfileDirectory biomeColorProfileDirectory;

	public BiomeColorMenuFactory(JMenu parentMenu, Actions actions,
			BiomeColorProfileDirectory biomeColorProfileDirectory) {
		this.parentMenu = parentMenu;
		this.actions = actions;
		this.biomeColorProfileDirectory = biomeColorProfileDirectory;
		Log.i("Checking for additional biome color profiles.");
		initParentMenu();
	}

	private void initParentMenu() {
		parentMenu.removeAll();
		biomeColorProfileDirectory.saveDefaultProfileIfNecessary();
		BiomeColorProfileVisitorImpl visitor = new BiomeColorProfileVisitorImpl(
				parentMenu, actions);
		biomeColorProfileDirectory.visitProfiles(visitor);
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
