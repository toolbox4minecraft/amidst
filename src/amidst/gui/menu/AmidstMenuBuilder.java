package amidst.gui.menu;

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

import amidst.Options;
import amidst.preferences.BiomeColorProfile;
import amidst.preferences.SelectPrefModel.SelectButtonModel;
import amidst.resources.ResourceLoader;

public class AmidstMenuBuilder {
	private final Options options;
	private final MenuActions actions;
	private final JMenuBar menuBar;
	private JMenu worldMenu;
	private JMenuItem savePlayerLocationsMenu;
	private JMenuItem reloadPlayerLocationsMenu;

	public AmidstMenuBuilder(Options options, MenuActions actions) {
		this.options = options;
		this.actions = actions;
		this.menuBar = createMenuBar();
	}

	public AmidstMenu construct() {
		return new AmidstMenu(menuBar, worldMenu, savePlayerLocationsMenu,
				reloadPlayerLocationsMenu);
	}

	private JMenuBar createMenuBar() {
		JMenuBar result = new JMenuBar();
		result.add(create_File());
		worldMenu = result.add(create_World());
		result.add(create_Options());
		result.add(create_Help());
		return result;
	}

	private JMenu create_File() {
		JMenu result = new JMenu("File");
		result.setMnemonic(KeyEvent.VK_F);
		result.add(create_File_New());
		savePlayerLocationsMenu = result.add(create_File_SavePlayerLocations());
		reloadPlayerLocationsMenu = result
				.add(create_File_ReloadPlayerLocations());
		result.add(create_File_SwitchProfile());
		result.add(create_File_Exit());
		return result;
	}

	private JMenuItem create_File_New() {
		JMenu result = new JMenu("New");
		result.setMnemonic(KeyEvent.VK_N);
		result.add(create_File_New_Seed());
		result.add(create_File_New_File());
		result.add(create_File_New_Random());
		return result;
	}

	private JMenuItem create_File_New_Seed() {
		JMenuItem result = new JMenuItem("From seed");
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actions.newFromSeed();
			}
		});
		return result;
	}

	private JMenuItem create_File_New_File() {
		JMenuItem result = new JMenuItem("From file or folder");
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actions.newFromFileOrFolder();
			}
		});
		return result;
	}

	private JMenuItem create_File_New_Random() {
		JMenuItem result = new JMenuItem("From random seed");
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actions.newFromRandom();
			}
		});
		return result;
	}

	private JMenuItem create_File_SavePlayerLocations() {
		JMenuItem result = new JMenuItem("Save player locations");
		result.setEnabled(false);
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actions.savePlayerLocations();
			}
		});
		return result;
	}

	private JMenuItem create_File_ReloadPlayerLocations() {
		JMenuItem result = new JMenuItem("Reload player locations");
		result.setEnabled(false);
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actions.reloadPlayerLocations();
			}
		});
		return result;
	}

	private JMenuItem create_File_SwitchProfile() {
		JMenuItem result = new JMenuItem("Switch profile");
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actions.switchProfile();
			}
		});
		return result;
	}

	private JMenuItem create_File_Exit() {
		JMenuItem result = new JMenuItem("Exit");
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actions.exit();
			}
		});
		return result;
	}

	private JMenu create_World() {
		JMenu result = new JMenu("World");
		result.setEnabled(false);
		result.setMnemonic(KeyEvent.VK_M);
		result.add(create_World_Find());
		result.add(create_World_GoTo());
		result.add(create_World_Layers());
		result.add(create_World_CopySeed());
		result.add(create_World_Capture());
		return result;
	}

	private JMenuItem create_World_Find() {
		JMenu result = new JMenu("Find");
		result.add(create_World_Find_Stronghold());
		return result;
	}

	private JMenuItem create_World_Find_Stronghold() {
		JMenuItem result = new JMenuItem("Stronghold");
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actions.findStronghold();
			}
		});
		return result;
	}

	private JMenuItem create_World_GoTo() {
		JMenu result = new JMenu("Go to");
		result.add(create_World_GoTo_Coordinate());
		result.add(create_World_GoTo_Player());
		return result;
	}

	private JMenuItem create_World_GoTo_Coordinate() {
		JMenuItem result = new JMenuItem("Coordinate");
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actions.gotoCoordinate();
			}
		});
		return result;
	}

	private JMenuItem create_World_GoTo_Player() {
		JMenuItem result = new JMenuItem("Player");
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actions.gotoPlayer();
			}
		});
		return result;
	}

	private JMenuItem create_World_Layers() {
		JMenu result = new JMenu("Layers");
		// @formatter:off
		result.add(createJCheckBoxItem("Grid",						"grid.png",				KeyEvent.VK_1, options.showGrid));
		result.add(createJCheckBoxItem("Slime chunks",				"slime.png",			KeyEvent.VK_2, options.showSlimeChunks));
		result.add(createJCheckBoxItem("Village Icons",				"village.png",			KeyEvent.VK_3, options.showVillages));
		result.add(createJCheckBoxItem("Ocean Monument Icons",		"ocean_monument.png",	KeyEvent.VK_4, options.showOceanMonuments));
		result.add(createJCheckBoxItem("Temple/Witch Hut Icons",	"desert.png",			KeyEvent.VK_5, options.showTemples));
		result.add(createJCheckBoxItem("Stronghold Icons",			"stronghold.png",		KeyEvent.VK_6, options.showStrongholds));
		result.add(createJCheckBoxItem("Player Icons",				"player.png",			KeyEvent.VK_7, options.showPlayers));
		result.add(createJCheckBoxItem("Nether Fortress Icons",		"nether_fortress.png",	KeyEvent.VK_8, options.showNetherFortresses));
		result.add(createJCheckBoxItem("Spawn Location Icon",		"spawn.png",			KeyEvent.VK_9, options.showSpawn));
		// @formatter:on
		return result;
	}

	private JMenuItem create_World_CopySeed() {
		JMenuItem result = new JMenuItem("Copy Seed to Clipboard");
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

	private JMenuItem create_World_Capture() {
		JMenuItem result = new JMenuItem("Capture");
		result.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				InputEvent.CTRL_DOWN_MASK));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.capture();
			}
		});
		return result;
	}

	private JMenu create_Options() {
		JMenu result = new JMenu("Options");
		result.setMnemonic(KeyEvent.VK_M);
		result.add(create_Options_Map());
		if (BiomeColorProfile.isEnabled()) {
			result.add(create_Options_BiomeColor());
		}
		result.add(create_Options_WorldType());
		return result;
	}

	private JMenu create_Options_Map() {
		JMenu result = new JMenu("Map");
		// @formatter:off
		result.add(createJCheckBoxItem("Smooth Scrolling",					null, KeyEvent.VK_I,	options.smoothScrolling));
		result.add(createJCheckBoxItem("Restrict Maximum Zoom",				null, KeyEvent.VK_Z,	options.maxZoom));
		result.add(createJCheckBoxItem("Show Framerate",					null, KeyEvent.VK_L,	options.showFPS));
		result.add(createJCheckBoxItem("Show Scale",						null, KeyEvent.VK_K,	options.showScale));
		result.add(createJCheckBoxItem("Fragment Fading",					null, -1,				options.fragmentFading));
		result.add(createJCheckBoxItem("Show Debug Info",					null, -1,				options.showDebug));
		// @formatter:on
		return result;
	}

	private JMenu create_Options_BiomeColor() {
		return new BiomeColorMenuFactory(actions).getMenu();
	}

	private JMenu create_Options_WorldType() {
		JMenu result = new JMenu("World type");
		SelectButtonModel[] buttonModels = options.worldType.getButtonModels();
		for (SelectButtonModel buttonModel : buttonModels) {
			result.add(createJCheckBoxItem(buttonModel.getName(), null, -1,
					buttonModel));
		}
		return result;
	}

	private JMenu create_Help() {
		JMenu result = new JMenu("Help");
		result.add(create_Help_CheckForUpdates());
		result.add(create_Help_ViewLicenses());
		result.add(create_Help_About());
		return result;
	}

	private JMenuItem create_Help_CheckForUpdates() {
		JMenuItem result = new JMenuItem("Check for updates");
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.checkForUpdates();
			}
		});
		return result;
	}

	private JMenuItem create_Help_ViewLicenses() {
		JMenuItem result = new JMenuItem("View licenses");
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
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.about();
			}
		});
		return result;
	}

	private JCheckBoxMenuItem createJCheckBoxItem(String text, String image,
			int key, JToggleButton.ToggleButtonModel preference) {
		JCheckBoxMenuItem result = new JCheckBoxMenuItem(text);
		result.setIcon(getIcon(image));
		if (key != -1) {
			result.setAccelerator(KeyStroke.getKeyStroke(key,
					InputEvent.CTRL_DOWN_MASK));
		}
		result.setModel(preference);
		return result;
	}

	private ImageIcon getIcon(String image) {
		if (image == null) {
			return null;
		} else {
			BufferedImage icon = ResourceLoader.getImage(image);
			if (icon != null) {
				return null;
			} else {
				return new ImageIcon(icon);
			}
		}
	}
}
