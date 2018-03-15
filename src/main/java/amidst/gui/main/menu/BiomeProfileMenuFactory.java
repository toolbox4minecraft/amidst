package amidst.gui.main.menu;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import amidst.AmidstSettings;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.Actions;
import amidst.logging.AmidstLogger;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileDirectory;
import amidst.settings.biomeprofile.BiomeProfileVisitor;

@NotThreadSafe
public class BiomeProfileMenuFactory {
	@NotThreadSafe
	private static class BiomeProfileVisitorImpl implements BiomeProfileVisitor {
		private final List<AbstractButton> allCheckBoxes = new ArrayList<>();
		private final List<JMenu> menuStack = new ArrayList<>();
		private Runnable defaultBiomeProfileSelector;
		private boolean isFirstContainer = true;

		private final Actions actions;
		private final AmidstSettings settings;
		

		private BiomeProfileVisitorImpl(JMenu parentMenu, Actions actions, AmidstSettings settings) {
			this.actions = actions;
			this.settings = settings;
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
			AbstractButton checkBox = createRadioButton(profile);
			checkBox.setSelected(profile.getName().equals(settings.lastBiomeProfile.get()));
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

		private JRadioButtonMenuItem createRadioButton(BiomeProfile profile) {
			JRadioButtonMenuItem result = new JRadioButtonMenuItem(profile.getName());
			tryCreateKeyboardShortcut(profile.getShortcut(), result);
			result.addActionListener(createListener(profile, result));
			return result;
		}
		
		private void tryCreateKeyboardShortcut(String shortcut, JMenuItem checkBox) {
			if (shortcut != null) {
				KeyStroke accelerator = KeyStroke.getKeyStroke(shortcut);
				if (accelerator != null) {
					checkBox.setAccelerator(accelerator);
				} else {
					AmidstLogger.info("Unable to create keyboard shortcut from: " + shortcut);
				}
			}
		}

		private ActionListener createListener(final BiomeProfile profile, final AbstractButton selectedCheckBox) {
			ActionListener result = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (AbstractButton checkBox : allCheckBoxes) {
						checkBox.setSelected(false);
					}
					selectedCheckBox.setSelected(true);
					actions.selectBiomeProfile(profile, settings);
				}
			};
			if (defaultBiomeProfileSelector == null && profile.getName().equals("default")) {
				defaultBiomeProfileSelector = () -> result.actionPerformed(null);
			}
			return result;
		}

		public void selectDefaultBiomeProfile() {
			if (defaultBiomeProfileSelector != null) {
				defaultBiomeProfileSelector.run();
			}
		}
	}

	private final JMenu parentMenu;
	private final Actions actions;
	private final AmidstSettings settings;
	private final BiomeProfileDirectory biomeProfileDirectory;
	private final String reloadText;
	private final int reloadMnemonic;
	private final MenuShortcut reloadMenuShortcut;
	private final String editText;
	private final int editMnemonic;
	private final MenuShortcut editMenuShortcut;

	public BiomeProfileMenuFactory(
			JMenu parentMenu,
			Actions actions,
			AmidstSettings settings,			
			BiomeProfileDirectory biomeProfileDirectory,
			String reloadText,
			int reloadMnemonic,
			MenuShortcut reloadMenuShortcut,
			String editText,
			int editMnemonic,
			MenuShortcut editMenuShortcut) {
		this.parentMenu = parentMenu;
		this.actions = actions;
		this.settings = settings;
		this.biomeProfileDirectory = biomeProfileDirectory;
		this.reloadText = reloadText;
		this.reloadMnemonic = reloadMnemonic;
		this.reloadMenuShortcut = reloadMenuShortcut;
		this.editText = editText;
		this.editMnemonic = editMnemonic;
		this.editMenuShortcut = editMenuShortcut;
		AmidstLogger.info("Checking for additional biome profiles.");
		initParentMenu();
	}

	private void initParentMenu() {
		parentMenu.removeAll();
		biomeProfileDirectory.saveDefaultProfilesIfNecessary();
		BiomeProfileVisitorImpl visitor = new BiomeProfileVisitorImpl(parentMenu, actions, settings);
		biomeProfileDirectory.visitProfiles(visitor);
		parentMenu.addSeparator();
		Menus.item(parentMenu, this::doEdit,     editText,   editMnemonic,   editMenuShortcut);
		Menus.item(parentMenu, this::doReload, reloadText, reloadMnemonic, reloadMenuShortcut);
		visitor.selectDefaultBiomeProfile();
	}

	private void doReload() {
		AmidstLogger.info("Reloading additional biome profiles.");
		initParentMenu();
	}

	private void doEdit() {
		AmidstLogger.info("Opening biomes directory.");
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(biomeProfileDirectory.getRoot());
		} catch (IOException ex) {
			AmidstLogger.info("Failed to open biomes folder: " + ex.getMessage());
		}		
	}
	
}
