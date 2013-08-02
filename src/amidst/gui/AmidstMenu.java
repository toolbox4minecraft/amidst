package amidst.gui;

import MoF.*;
import amidst.Options;
import amidst.Util;
import amidst.map.MapObjectPlayer;
import amidst.resources.ResourceLoader;

import javax.swing.*;
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
import java.util.List;
import java.util.Random;

/** Structured menubar-creation to alleviate the huge mess that it would be elsewise
 */
public class AmidstMenu extends JMenuBar {
	final JMenu fileMenu;
	//final JMenu scriptMenu;
	public final JMenu mapMenu; //TODO: protected
	final JMenu helpMenu;
	
	private final FinderWindow window;
	
	public AmidstMenu(FinderWindow win) {
		this.window = win;
		
		fileMenu = add(new FileMenu());
//		scriptMenu = add(new JMenu("Script") {{
//			setEnabled(false);
//			add(new JMenuItem("New"));
//			add(new JMenuItem("Open"));
//			add(new JMenuItem("Save"));
//			add(new JMenuItem("Run"));
//		}});
		mapMenu = add(new MapMenu());
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
				setEnabled(ReflectionInfo.instance.version.saveEnabled());
				setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (window.curProject.saveLoaded)
							for (MapObjectPlayer player : window.curProject.save.getPlayers())
								if (player.needSave) {
									window.curProject.save.movePlayer(player.getName(), player.x, player.y);
									player.needSave = false;
								}
					}
				});
			}});
			
			add(new JMenuItem("Exit") {{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int ret = JOptionPane.showConfirmDialog(window, "Are you sure you want to exit?");
						if (ret == 0)
							System.exit(0);
					}
				});
			}});
		}
		
		private class SeedMenuItem extends JMenuItem {
			private SeedMenuItem() {
				super("From seed");
				setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						//Create the JOptionPane.
						String s = JOptionPane.showInputDialog(null, "Enter seed", "New Project", JOptionPane.QUESTION_MESSAGE);
						if (s != null) {
							SaveLoader.Type worldType = choose("New Project", "Enter world type\n", SaveLoader.Type.values());
							if (s.equals(""))
								s = "" + (new Random()).nextLong();
							//If a string was returned, say so.
							if (worldType != null)
								window.setProject(new Project(s, worldType));
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
							SaveLoader.Type worldType = choose("New Project", "Enter world type\n", SaveLoader.Type.values());
							
							//If a string was returned, say so.
							if (worldType != null)
								window.setProject(new Project(seed, worldType));
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
						
						if (fc.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
							File f = fc.getSelectedFile();
							
							SaveLoader s = null;
							if (f.isDirectory())
								s = new SaveLoader(new File(f.getAbsoluteFile() + "/level.dat"));
							else
								s = new SaveLoader(f);
							window.setProject(new Project(s));
						}
					}
				});
			}
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
			add(new MiscMenu());
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
							goToChosenPoint(window.curProject.manager.strongholds, "Stronghold");
						}
					});
				}});
			}
		}
		private class MiscMenu extends JMenu {
			private MiscMenu() {
				super("Miscellaneous");

				add(new DisplayingCheckbox("Map Flicking",
						null,
						KeyEvent.VK_I,
						Options.instance.mapFlicking));
				add(new CopySeedMenuItem());
				
				add(new DisplayingCheckbox("Restrict Maximum Zoom",
						null,
						KeyEvent.VK_Z,
						Options.instance.maxZoom));
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
								} catch (NumberFormatException ignored) {
									ignored.printStackTrace();
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
				
				add(new DisplayingCheckbox("Slime chunks",
					ResourceLoader.getImage("slime.png"),
					KeyEvent.VK_1,
					Options.instance.showSlimeChunks));
				add(new DisplayingCheckbox("Grid",
					null,
					KeyEvent.VK_2,
					Options.instance.showGrid));
				add(new DisplayingCheckbox("Nether fortresses",
					ResourceLoader.getImage("nether_fortress.png"),
					KeyEvent.VK_3,
					Options.instance.showNetherFortresses));
				add(new DisplayingCheckbox("Icons",
					null,
					KeyEvent.VK_4,
					Options.instance.showIcons));
			}
			

		}
		public class DisplayingCheckbox extends JCheckBoxMenuItem {
			private DisplayingCheckbox(String text, BufferedImage icon, int key, JToggleButton.ToggleButtonModel model) {
				super(text, (icon != null) ? new ImageIcon(icon) : null);
				setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
				setModel(model);
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
