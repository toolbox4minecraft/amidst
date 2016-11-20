package amidst.gui.main.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.KeyStroke;

import amidst.documentation.NotThreadSafe;
import amidst.gui.main.Actions;
import amidst.logging.Log;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileDirectory;
import amidst.settings.biomeprofile.BiomeProfileVisitor;

@NotThreadSafe
public class BiomeProfileMenuFactory {
	@NotThreadSafe
	private static class BiomeProfileVisitorImpl implements BiomeProfileVisitor {
		private final List<JCheckBoxMenuItem> allCheckBoxes = new ArrayList<>();
		private final List<JMenu> menuStack = new ArrayList<>();
		private ActionListener firstListener;
		private boolean isFirstContainer = true;

		private final Actions actions;

		private BiomeProfileVisitorImpl(JMenu parentMenu, Actions actions) {
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
		public void visitProfile(BiomeProfile profile) {
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

		private JCheckBoxMenuItem createCheckBox(BiomeProfile profile) {
			JCheckBoxMenuItem result = new JCheckBoxMenuItem(profile.getName());
			tryCreateKeyboardShortcut(profile.getShortcut(), result);
			result.addActionListener(createListener(profile, result));
			return result;
		}

		private void tryCreateKeyboardShortcut(String shortcut, JCheckBoxMenuItem checkBox) {
			if (shortcut != null) {
				KeyStroke accelerator = KeyStroke.getKeyStroke(shortcut);
				if (accelerator != null) {
					checkBox.setAccelerator(accelerator);
				} else {
					Log.i("Unable to create keyboard shortcut from: " + shortcut);
				}
			}
		}

		private ActionListener createListener(final BiomeProfile profile, final JCheckBoxMenuItem selectedCheckBox) {
			ActionListener result = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (JCheckBoxMenuItem checkBox : allCheckBoxes) {
						checkBox.setSelected(false);
					}
					selectedCheckBox.setSelected(true);
					actions.selectBiomeProfile(profile);
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
	private final BiomeProfileDirectory biomeProfileDirectory;
	private final String reloadText;
	private final int reloadMnemonic;
	private final String reloadAccelerator;

	public BiomeProfileMenuFactory(
			JMenu parentMenu,
			Actions actions,
			BiomeProfileDirectory biomeProfileDirectory,
			String reloadText,
			int reloadMnemonic,
			String reloadAccelerator) {
		this.parentMenu = parentMenu;
		this.actions = actions;
		this.biomeProfileDirectory = biomeProfileDirectory;
		this.reloadText = reloadText;
		this.reloadMnemonic = reloadMnemonic;
		this.reloadAccelerator = reloadAccelerator;
		Log.i("Checking for additional biome profiles.");
		initParentMenu();
	}

	private void initParentMenu() {
		parentMenu.removeAll();
		biomeProfileDirectory.saveDefaultProfileIfNecessary();
		BiomeProfileVisitorImpl visitor = new BiomeProfileVisitorImpl(parentMenu, actions);
		biomeProfileDirectory.visitProfiles(visitor);
		parentMenu.addSeparator();
		Menus.item(parentMenu, this::doReload, reloadText, reloadMnemonic, reloadAccelerator);
		visitor.selectFirstProfile();
	}

	private void doReload() {
		Log.i("Reloading additional biome profiles.");
		initParentMenu();
	}
}
