package amidst.gui.menu;

import MoF.*;
import amidst.Options;
import amidst.Util;
import amidst.gui.LicenseWindow;
import amidst.logging.Log;
import amidst.map.MapObjectPlayer;
import amidst.map.layers.StrongholdLayer;
import amidst.minecraft.MinecraftUtil;
import amidst.preferences.BiomeColorProfile;
import amidst.preferences.SelectPrefModel.SelectButtonModel;
import amidst.resources.ResourceLoader;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Structured menubar-creation to alleviate the huge mess that it would be elsewise
 */

// TODO: This class is a mess-- it should be split into pieces.
public class AmidstMenu extends JMenuBar {
	final JMenu fileMenu;
	//final JMenu scriptMenu;
	public final JMenu mapMenu; //TODO: protected
	final JMenu optionsMenu;
	final JMenu helpMenu;
	
	private final FinderWindow window;
	
	public AmidstMenu(FinderWindow window) {
		this.window = window;
		
		fileMenu = add(new FileMenu());
		mapMenu = add(new MapMenu());
		optionsMenu = add(new OptionsMenu());
		helpMenu = add(new HelpMenu());
	}
	
	private class FileMenu extends JMenu {
		private FileMenu() {
			super("File");
			setMnemonic(KeyEvent.VK_F);
			
			add(new JMenu("New") {{
				setMnemonic(KeyEvent.VK_N);
				add(new SeedMenuItem());
				add(new FileMenuItem());
				add(new RandomSeedMenuItem());
				//add(new JMenuItem("From Server"));
			}});
			
			add(new JMenuItem("Save player locations") {{
				setEnabled(MinecraftUtil.getVersion().saveEnabled());
				setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (window.curProject.saveLoaded) {
							for (MapObjectPlayer player : window.curProject.save.getPlayers()) {
								if (player.needSave) {
									window.curProject.save.movePlayer(player.getName(), player.globalX, player.globalY);
									player.needSave = false;
								}
							}
						}
					}
				});
			}});
			
			add(new JMenuItem("Exit") {{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					System.exit(0);
					}
				});
			}});
		}
		private String showSeedPrompt(String title) {
			final String blankText = "A random seed will be generated if left blank.";
			final String leadingSpaceText = "Warning: There is a space at the start!";
			final String trailingSpaceText = "Warning: There is a space at the end!";
			
			final JTextField inputText = new JTextField();

			inputText.addAncestorListener( new AncestorListener() {
				@Override
				public void ancestorAdded(AncestorEvent arg0) {
					inputText.requestFocus();
				}
				@Override
				public void ancestorMoved(AncestorEvent arg0) {
					inputText.requestFocus();
				}
				@Override
				public void ancestorRemoved(AncestorEvent arg0) {
					inputText.requestFocus();
				}
			});
			
			final JLabel inputInformation = new JLabel(blankText);
			inputInformation.setForeground(Color.red);
			inputInformation.setFont(new Font("arial", Font.BOLD, 10));
			inputText.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					update();
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					update();
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					update();
				}
				public void update() {
					String text = inputText.getText();
					if (text.equals("")) {
						inputInformation.setText(blankText);
						inputInformation.setForeground(Color.red);
					} else if (text.startsWith(" ")) {
						inputInformation.setText(leadingSpaceText);
						inputInformation.setForeground(Color.red);
					} else if (text.endsWith(" ")) {
						inputInformation.setText(trailingSpaceText);
						inputInformation.setForeground(Color.red);
					} else {
						try {
							Long.parseLong(text);
							inputInformation.setText("Seed is valid.");
							inputInformation.setForeground(Color.gray);
						} catch (NumberFormatException e) {
							inputInformation.setText("This seed's value is " + text.hashCode() + ".");
							inputInformation.setForeground(Color.black);
						}
					}
				}
			});
			
			final JComponent[] inputs = new JComponent[] {
					new JLabel("Enter your seed: "),
					inputInformation,
					inputText
			};
			int result = JOptionPane.showConfirmDialog(window, inputs, title, JOptionPane.OK_CANCEL_OPTION);
			return (result == 0)?inputText.getText():null;
		}
		
		private class SeedMenuItem extends JMenuItem {
			private SeedMenuItem() {
				super("From seed");
				setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						String seed = showSeedPrompt("New Project");
						if (seed != null) {
							String worldTypePreference = Options.instance.worldType.get();
							SaveLoader.Type worldType = null;
							if (worldTypePreference.equals("Prompt each time")) {
								worldType = choose("New Project", "Enter world type\n", SaveLoader.Type.values());
							} else {
								worldType = SaveLoader.Type.fromMixedCase(worldTypePreference);
							}
							
							if (seed.equals(""))
								seed = "" + (new Random()).nextLong();
							if (worldType != null) {
								window.clearProject();
								window.setProject(new Project(seed, worldType.getValue()));
							}
						}
					}
				});
			}
		}
		
		private class RandomSeedMenuItem extends JMenuItem {
			private RandomSeedMenuItem() {
				super("From random seed");
				setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						//Create the JOptionPane.
							Random random = new Random();
							long seed = random.nextLong();
							String worldTypePreference = Options.instance.worldType.get();
							SaveLoader.Type worldType = null;
							if (worldTypePreference.equals("Prompt each time")) {
								worldType = choose("New Project", "Enter world type\n", SaveLoader.Type.values());
							} else {
								worldType = SaveLoader.Type.fromMixedCase(worldTypePreference);
							}
							
							//If a string was returned, say so.
							if (worldType != null) {
								window.clearProject();
								window.setProject(new Project(seed, worldType.getValue()));
							}
						}
					
				});
			}
		}
		
		private class FileMenuItem extends JMenuItem {
			private FileMenuItem() {
				super("From file or folder");
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						JFileChooser fc = new JFileChooser();
						fc.addChoosableFileFilter(SaveLoader.getFilter());
						fc.setAcceptAllFileFilterUsed(false);
						fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						fc.setCurrentDirectory(new File(Util.minecraftDirectory, "saves"));
						fc.setFileHidingEnabled(false);
						if (fc.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
							File f = fc.getSelectedFile();
							
							SaveLoader s = null;
							if (f.isDirectory())
								s = new SaveLoader(new File(f.getAbsoluteFile() + "/level.dat"));
							else
								s = new SaveLoader(f);
							window.clearProject();
							window.setProject(new Project(s));
						}
					}
				});
			}
		}
	}

	public class DisplayingCheckbox extends JCheckBoxMenuItem {
		private DisplayingCheckbox(String text, BufferedImage icon, int key, JToggleButton.ToggleButtonModel model) {
			super(text, (icon != null) ? new ImageIcon(icon) : null);
			if (key != -1)
				setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
			setModel(model);
		}
	}
	private class MapMenu extends JMenu {
		private MapMenu() {
			super("Map");
			setEnabled(false);
			setMnemonic(KeyEvent.VK_M);
			add(new FindMenu());
			add(new GoToMenu());
			add(new LayersMenu());
			add(new CopySeedMenuItem());
			add(new CaptureMenuItem());
		
		}
		
		private class FindMenu extends JMenu {
			private FindMenu() {
				super("Find");
				//add(new JMenuItem("Biome"));
				//add(new JMenuItem("Village"));
				add(new JMenuItem("Stronghold") {{
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
					addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							goToChosenPoint(StrongholdLayer.instance.getStrongholds(), "Stronghold");
						}
					});
				}});
			}
		}
		
		private class GoToMenu extends JMenu {
			private GoToMenu() {
				super("Go to");
				add(new JMenuItem("Coordinate") {{
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							String s = JOptionPane.showInputDialog(null, "Enter coordinates: (Ex. 123,456)", "Go To", JOptionPane.QUESTION_MESSAGE);
							if (s != null) {
								String[] c = s.replaceAll(" ", "").split(",");
								try {
									long x = Long.parseLong(c[0]);
									long y = Long.parseLong(c[1]);
									window.curProject.moveMapTo(x, y);
								} catch (NumberFormatException e1) {
									Log.w("Invalid location entered, ignoring.");
									e1.printStackTrace();
								}
							}
						}
					});
				}});
				
				add(new JMenuItem("Player") {{
					addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							if (window.curProject.saveLoaded) {
								List<MapObjectPlayer> playerList = window.curProject.save.getPlayers();
								MapObjectPlayer[] players = playerList.toArray(new MapObjectPlayer[playerList.size()]);
								goToChosenPoint(players, "Player");
								MapObjectPlayer p = choose("Go to", "Select player:", players);
								if (p != null)
									window.curProject.moveMapTo(p.globalX, p.globalY);
							}
						}
					});
				}});
				//add(new JMenuItem("Spawn"));
				//add(new JMenuItem("Chunk"));
			}
		}
		
		private class LayersMenu extends JMenu {
			private LayersMenu() {
				super("Layers");

				add(new DisplayingCheckbox("Grid",
					ResourceLoader.getImage("grid.png"),
					KeyEvent.VK_1,
					Options.instance.showGrid));
				
				add(new DisplayingCheckbox("Slime chunks",
					ResourceLoader.getImage("slime.png"),
					KeyEvent.VK_2,
					Options.instance.showSlimeChunks));
				
				add(new DisplayingCheckbox("Village Icons",
					ResourceLoader.getImage("village.png"),
					KeyEvent.VK_3,
					Options.instance.showVillages));
				
				add(new DisplayingCheckbox("Temple/Witch Hut Icons",
					ResourceLoader.getImage("temple.png"),
					KeyEvent.VK_4,
					Options.instance.showTemples));
				
				add(new DisplayingCheckbox("Stronghold Icons",
					ResourceLoader.getImage("stronghold.png"),
					KeyEvent.VK_5,
					Options.instance.showStrongholds));
				
				add(new DisplayingCheckbox("Player Icons",
					ResourceLoader.getImage("player.png"),
					KeyEvent.VK_6,
					Options.instance.showPlayers));
				
				add(new DisplayingCheckbox("Nether Fortress Icons",
					ResourceLoader.getImage("nether_fortress.png"),
					KeyEvent.VK_7,
					Options.instance.showNetherFortresses));
				
				add(new DisplayingCheckbox("Spawn Location Icon",
						ResourceLoader.getImage("spawn.png"),
						KeyEvent.VK_8,
						Options.instance.showSpawn));
				
			}
			

		}
		private class CaptureMenuItem extends JMenuItem {
			private CaptureMenuItem() {
				super("Capture");
				
				setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
				
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser fc = new JFileChooser();
						fc.addChoosableFileFilter(new PNGFileFilter());
						fc.setAcceptAllFileFilterUsed(false);
						int returnVal = fc.showSaveDialog(window);
						
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							String s = fc.getSelectedFile().toString();
							if (!s.toLowerCase().endsWith(".png"))
								s += ".png";
							window.curProject.map.saveToFile(new File(s));
						}
					}
				});
			}
		}
		private class CopySeedMenuItem extends JMenuItem {
			private CopySeedMenuItem() {
				super("Copy Seed to Clipboard");
				
				setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
				
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						StringSelection stringSelection = new StringSelection(Options.instance.seed + "");
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(stringSelection, new ClipboardOwner() {
							@Override
							public void lostOwnership(Clipboard arg0, Transferable arg1) {
								// TODO Auto-generated method stub
								
							}
						});
					}
				});
			}
		}
	}
	
	private class OptionsMenu extends JMenu {
		private OptionsMenu() {
			super("Options");
			add(new MapOptionsMenu());
			if (BiomeColorProfile.isEnabled)
				add(new BiomeColorMenu());
			add(new WorldTypeMenu());
			setMnemonic(KeyEvent.VK_M);
		}
		private class BiomeColorMenu extends JMenu {
			private ArrayList<JCheckBoxMenuItem> profileCheckboxes = new ArrayList<JCheckBoxMenuItem>();
			private JMenuItem reloadMenuItem;
			private class BiomeProfileActionListener implements ActionListener {
				private BiomeColorProfile profile;
				private ArrayList<JCheckBoxMenuItem> profileCheckboxes;
				private JCheckBoxMenuItem checkBox;
				public BiomeProfileActionListener(BiomeColorProfile profile, JCheckBoxMenuItem checkBox, ArrayList<JCheckBoxMenuItem> profileCheckboxes) {
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
			private BiomeColorMenu() {
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
							JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(profile.name);
							menuItem.addActionListener(new BiomeProfileActionListener(profile, menuItem, profileCheckboxes));
							if (profile.shortcut != null) {
								KeyStroke accelerator = KeyStroke.getKeyStroke(profile.shortcut);
								if (accelerator != null)
									menuItem.setAccelerator(accelerator);
								else
									Log.i("Unable to create keyboard shortcut from: " + profile.shortcut);
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
		private class MapOptionsMenu extends JMenu {
			private MapOptionsMenu() {
				super("Map");

				add(new DisplayingCheckbox("Map Flicking (Smooth Scrolling)",
						null,
						KeyEvent.VK_I,
						Options.instance.mapFlicking));
				
				add(new DisplayingCheckbox("Restrict Maximum Zoom",
						null,
						KeyEvent.VK_Z,
						Options.instance.maxZoom));
				
				add(new DisplayingCheckbox("Show Framerate",
						null,
						KeyEvent.VK_L,
						Options.instance.showFPS));
				
				add(new DisplayingCheckbox("Use Fragment Fading",
						null,
						-1,
						Options.instance.mapFading));
				
				add(new DisplayingCheckbox("Show Debug Info",
						null,
						-1,
						Options.instance.showDebug));
			}
			
		}
		private class WorldTypeMenu extends JMenu {
			private WorldTypeMenu() {
				super("World type");

				SelectButtonModel[] buttonModels = Options.instance.worldType.getButtonModels();
				
				for (int i = 0; i < buttonModels.length; i++) {
					add(new DisplayingCheckbox(buttonModels[i].getName(),
							null,
							-1,
							buttonModels[i]));
				}
			}
			
		}
	}
	
	private class HelpMenu extends JMenu {
		private HelpMenu() {
			super("Help");
			
			add(new JMenuItem("Check for updates") {{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						new UpdateManager(window).start();
					}
				});
			}});

			add(new JMenuItem("View licenses") {{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						new LicenseWindow();
					}
				});
			}});
			
			add(new JMenuItem("About") {{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JOptionPane.showMessageDialog(window,
							"Advanced Minecraft Interfacing and Data/Structure Tracking (AMIDST)\n" +
							"By Skidoodle (amidst.project@gmail.com)");
					}
				});
			}});
			
		}
	}
	
	/** Allows the user to choose one of several things.
	 * 
	 * Convenience wrapper around JOptionPane.showInputDialog
	 */
	private <T> T choose(String title, String message, T[] choices) {
		return (T) JOptionPane.showInputDialog(
			window,
			message,
			title,
			JOptionPane.PLAIN_MESSAGE,
			null,
			choices,
			choices[0]);
	}
	
	/** Lets the user decide one of the given points and go to it
	 * @param points Given points to choose from
	 * @param name name displayed in the choice
	 */
	private <T extends Point> void goToChosenPoint(T[] points, String name) {

		T p = choose("Go to", "Select " + name + ":", points);
		if (p != null)
			window.curProject.moveMapTo(p.x, p.y);
	}
}
