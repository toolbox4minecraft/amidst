package amidst.gui.main.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

import amidst.ResourceLoader;
import amidst.AmidstSettings;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.Actions;
import amidst.settings.MultipleStringsSetting.SelectButtonModel;
import amidst.settings.biomecolorprofile.BiomeColorProfileDirectory;

@NotThreadSafe
public class AmidstMenuBuilder {
	private final AmidstSettings settings;
	private final Actions actions;
	private final BiomeColorProfileDirectory biomeColorProfileDirectory;
	private final DimensionToggleButtonModels dimensionToggleButtonModels;
	private final JMenuBar menuBar;
	private JMenu worldMenu;
	private JMenuItem savePlayerLocationsMenu;
	private JMenuItem reloadPlayerLocationsMenu;

	public AmidstMenuBuilder(AmidstSettings settings, Actions actions,
			BiomeColorProfileDirectory biomeColorProfileDirectory) {
		this.settings = settings;
		this.actions = actions;
		this.biomeColorProfileDirectory = biomeColorProfileDirectory;
		this.dimensionToggleButtonModels = new DimensionToggleButtonModels(
				actions);
		this.menuBar = createMenuBar();
	}

	public AmidstMenu construct() {
		return new AmidstMenu(menuBar, worldMenu, savePlayerLocationsMenu,
				reloadPlayerLocationsMenu, dimensionToggleButtonModels);
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
		result.add(create_File_NewFromSeed());
		result.add(create_File_NewFromRandom());
		result.add(create_File_OpenWorldFile());
		result.addSeparator();
		result.add(create_File_SwitchProfile());
		result.add(create_File_Exit());
		return result;
	}

	private JMenuItem create_File_NewFromSeed() {
		JMenuItem result = new JMenuItem("New from seed");
		result.setMnemonic(KeyEvent.VK_S);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.newFromSeed();
			}
		});
		return result;
	}

	private JMenuItem create_File_NewFromRandom() {
		JMenuItem result = new JMenuItem("New from random seed");
		result.setMnemonic(KeyEvent.VK_R);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.newFromRandom();
			}
		});
		return result;
	}

	private JMenuItem create_File_OpenWorldFile() {
		JMenuItem result = new JMenuItem("Open world file ...");
		result.setMnemonic(KeyEvent.VK_F);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.openWorldFile();
			}
		});
		return result;
	}

	private JMenuItem create_File_SwitchProfile() {
		JMenuItem result = new JMenuItem("Switch profile ...");
		result.setMnemonic(KeyEvent.VK_P);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.switchProfile();
			}
		});
		return result;
	}

	private JMenuItem create_File_Exit() {
		JMenuItem result = new JMenuItem("Exit");
		result.setMnemonic(KeyEvent.VK_X);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.exit();
			}
		});
		return result;
	}

	private JMenu create_World() {
		JMenu result = new JMenu("World");
		result.setEnabled(false);
		result.setMnemonic(KeyEvent.VK_W);
		result.add(create_World_GoToCoordinate());
		result.add(create_World_GoToSpawn());
		result.add(create_World_GoToStronghold());
		result.add(create_World_GoToPlayer());
		result.addSeparator();
		savePlayerLocationsMenu = result
				.add(create_World_SavePlayerLocations());
		reloadPlayerLocationsMenu = result
				.add(create_Players_ReloadPlayerLocations());
		result.add(create_World_HowCanIMoveAPlayer());
		result.addSeparator();
		result.add(create_World_CopySeed());
		result.add(create_World_SaveCaptureImage());
		return result;
	}

	private JMenuItem create_World_GoToCoordinate() {
		JMenuItem result = new JMenuItem("Go to Coordinate");
		result.setMnemonic(KeyEvent.VK_C);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.goToCoordinate();
			}
		});
		return result;
	}

	private JMenuItem create_World_GoToSpawn() {
		JMenuItem result = new JMenuItem("Go to Spawn");
		result.setMnemonic(KeyEvent.VK_S);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.goToSpawn();
			}
		});
		return result;
	}

	private JMenuItem create_World_GoToStronghold() {
		JMenuItem result = new JMenuItem("Go to Stronghold");
		result.setMnemonic(KeyEvent.VK_H);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.goToStronghold();
			}
		});
		return result;
	}

	private JMenuItem create_World_GoToPlayer() {
		JMenuItem result = new JMenuItem("Go to Player");
		result.setMnemonic(KeyEvent.VK_P);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.goToPlayer();
			}
		});
		return result;
	}

	private JMenuItem create_World_SavePlayerLocations() {
		JMenuItem result = new JMenuItem("Save player locations");
		result.setEnabled(false);
		result.setMnemonic(KeyEvent.VK_V);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.savePlayerLocations();
			}
		});
		return result;
	}

	private JMenuItem create_Players_ReloadPlayerLocations() {
		JMenuItem result = new JMenuItem("Reload player locations");
		result.setEnabled(false);
		result.setMnemonic(KeyEvent.VK_R);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.reloadPlayerLocations();
			}
		});
		return result;
	}

	private JMenuItem create_World_HowCanIMoveAPlayer() {
		JMenuItem result = new JMenuItem("How can I move a player?");
		result.setMnemonic(KeyEvent.VK_M);
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.howCanIMoveAPlayer();
			}
		});
		return result;
	}

	private JMenuItem create_World_CopySeed() {
		JMenuItem result = new JMenuItem("Copy Seed to Clipboard");
		result.setMnemonic(KeyEvent.VK_B);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.copySeedToClipboard();
			}
		});
		return result;
	}

	private JMenuItem create_World_SaveCaptureImage() {
		JMenuItem result = new JMenuItem("Save capture image ...");
		result.setMnemonic(KeyEvent.VK_I);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.saveCaptureImage();
			}
		});
		return result;
	}

	private JMenuItem create_Layers() {
		JMenu result = new JMenu("Layers");
		result.setMnemonic(KeyEvent.VK_L);
		// @formatter:off
		result.add(createJCheckBoxItem("Overworld",                null,                  KeyEvent.VK_1, InputEvent.SHIFT_DOWN_MASK, dimensionToggleButtonModels  .getOverworld()));
		result.add(createJCheckBoxItem("The End",                  null,                  KeyEvent.VK_2, InputEvent.SHIFT_DOWN_MASK, dimensionToggleButtonModels  .getTheEnd()));
		result.addSeparator();
		result.add(createJCheckBoxItem("Slime chunks",             "slime.png",           KeyEvent.VK_3, InputEvent.SHIFT_DOWN_MASK, settings.showSlimeChunks     .getButtonModel()));
		result.add(createJCheckBoxItem("Grid",                     "grid.png",            KeyEvent.VK_4, InputEvent.SHIFT_DOWN_MASK, settings.showGrid            .getButtonModel()));
		result.addSeparator();
		result.add(createJCheckBoxItem("Spawn Location Icon",      "spawn.png",           KeyEvent.VK_1, 0,                          settings.showSpawn           .getButtonModel()));
		result.add(createJCheckBoxItem("Stronghold Icons",         "stronghold.png",      KeyEvent.VK_2, 0,                          settings.showStrongholds     .getButtonModel()));
		result.add(createJCheckBoxItem("Player Icons",             "player.png",          KeyEvent.VK_3, 0,                          settings.showPlayers         .getButtonModel()));
		result.add(createJCheckBoxItem("Village Icons",            "village.png",         KeyEvent.VK_4, 0,                          settings.showVillages        .getButtonModel()));
		result.add(createJCheckBoxItem("Temple/Witch Hut Icons",   "desert.png",          KeyEvent.VK_5, 0,                          settings.showTemples         .getButtonModel()));
		result.add(createJCheckBoxItem("Mineshaft Icons",          "mineshaft.png",       KeyEvent.VK_6, 0,                          settings.showMineshafts      .getButtonModel()));
		result.add(createJCheckBoxItem("Nether Fortress Icons",    "nether_fortress.png", KeyEvent.VK_7, 0,                          settings.showNetherFortresses.getButtonModel()));
		result.add(createJCheckBoxItem("Ocean Monument Icons",     "ocean_monument.png",  KeyEvent.VK_8, 0,                          settings.showOceanMonuments  .getButtonModel()));
		result.add(createJCheckBoxItem("End City Icons",           "end_city.png",        KeyEvent.VK_9, 0,                          settings.showEndCities       .getButtonModel()));
		// @formatter:on
		return result;
	}

	private JMenu create_Settings() {
		JMenu result = new JMenu("Settings");
		result.setMnemonic(KeyEvent.VK_S);
		result.add(create_Settings_DefaultWorldType());
		if (biomeColorProfileDirectory.isValid()) {
			result.add(create_Settings_BiomeColor());
		}
		result.addSeparator();
		// @formatter:off
		result.add(createJCheckBoxItem("Smooth Scrolling",         null,                  KeyEvent.VK_I, 0,                          settings.smoothScrolling     .getButtonModel()));
		result.add(createJCheckBoxItem("Fragment Fading",          null,                  -1,            0,                          settings.fragmentFading      .getButtonModel()));
		result.add(createJCheckBoxItem("Restrict Maximum Zoom",    null,                  KeyEvent.VK_Z, 0,                          settings.maxZoom             .getButtonModel()));
		result.add(createJCheckBoxItem("Show Framerate",           null,                  KeyEvent.VK_L, 0,                          settings.showFPS             .getButtonModel()));
		result.add(createJCheckBoxItem("Show Scale",               null,                  KeyEvent.VK_K, 0,                          settings.showScale           .getButtonModel()));
		result.add(createJCheckBoxItem("Show Debug Info",          null,                  -1,            0,                          settings.showDebug           .getButtonModel()));
		// @formatter:on
		return result;
	}

	private JMenu create_Settings_DefaultWorldType() {
		JMenu result = new JMenu("Default world type");
		for (SelectButtonModel buttonModel : settings.worldType
				.getButtonModels()) {
			result.add(createJCheckBoxItem(buttonModel.getName(), null, -1, 0,
					buttonModel));
		}
		return result;
	}

	private JMenu create_Settings_BiomeColor() {
		return new BiomeColorMenuFactory(actions, biomeColorProfileDirectory)
				.getMenu();
	}

	private JMenu create_Help() {
		JMenu result = new JMenu("Help");
		result.setMnemonic(KeyEvent.VK_H);
		result.add(create_Help_CheckForUpdates());
		result.add(create_Help_ViewLicenses());
		result.add(create_Help_About());
		return result;
	}

	private JMenuItem create_Help_CheckForUpdates() {
		JMenuItem result = new JMenuItem("Check for Updates");
		result.setMnemonic(KeyEvent.VK_U);
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.checkForUpdates();
			}
		});
		return result;
	}

	private JMenuItem create_Help_ViewLicenses() {
		JMenuItem result = new JMenuItem("View Licenses");
		result.setMnemonic(KeyEvent.VK_L);
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.viewLicense();
			}
		});
		return result;
	}

	private JMenuItem create_Help_About() {
		JMenuItem result = new JMenuItem("About");
		result.setMnemonic(KeyEvent.VK_A);
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.about();
			}
		});
		return result;
	}

	private JCheckBoxMenuItem createJCheckBoxItem(String text, String image,
			int key, int modifier, JToggleButton.ToggleButtonModel buttonModel) {
		JCheckBoxMenuItem result = new JCheckBoxMenuItem(text);
		result.setIcon(getIcon(image));
		if (key != -1) {
			result.setAccelerator(KeyStroke.getKeyStroke(key,
					InputEvent.CTRL_DOWN_MASK | modifier));
		}
		result.setModel(buttonModel);
		return result;
	}

	private ImageIcon getIcon(String image) {
		if (image != null) {
			BufferedImage icon = ResourceLoader
					.getImage("/amidst/gui/main/icon/" + image);
			if (icon != null) {
				return new ImageIcon(icon);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
