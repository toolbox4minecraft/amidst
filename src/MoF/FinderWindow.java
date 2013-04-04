package MoF;


import amidst.Amidst;
import amidst.Options;
import amidst.resources.ResourceLoader;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

public class FinderWindow extends JFrame {
	/**
	 * 
	 */
	public static Class<?> biomeFinder;
	private static final long serialVersionUID = 196896954675968191L;
	private Container pane;
	private Project curProject;
	private JMenu mapMenu;
	public JCheckBoxMenuItem layerGridMenu, layerSlimeMenu, layerNetherMenu, layerIconMenu;
	public static Preferences pref;
	public static boolean dataCollect;
	private JFileChooser fc;
	public FinderWindow() throws IOException {
		//Initialize window
		super("Amidst v" + Amidst.version());
		pref = Preferences.userRoot().node(this.getClass().getName());
		
		boolean first = pref.getBoolean("firstRun", true);
		if (first) {
			Google.track("RunFirstTime");
		} else {
			Google.track("Run");
		}
		pref.putBoolean("firstRun", false);
		setSize(800,800);
		//setLookAndFeel();
		pane = getContentPane();
		//UI Manager:
		pane.setLayout(new BorderLayout());
		loadMinecraft();
		(new UpdateManager(this, true)).start();
		setJMenuBar(createMainMenu());
		setVisible(true);
		Image icon = ResourceLoader.getImage("icon.png");
		setIconImage(icon);
		//OnClose
		/*
		boolean dcFirst = pref.getBoolean("datacheckfirst", false);
		if (!dcFirst) {
	        int result = JOptionPane.YES_OPTION;
	        result = JOptionPane.showConfirmDialog(null, "AMIDST would like to collect data about the maps you search, anonymously.\n You will only be prompted for this once:\n Would you like to allow data to be collected?", "Important alert!", JOptionPane.YES_NO_OPTION);
	        pref.putBoolean("datacollect", (result==0));
		}
		dataCollect = pref.getBoolean("datacollect", false);
	    */addWindowListener(new WindowAdapter()
	    {
	    	public void windowClosing(WindowEvent e)
	    	{
	    		dispose();
	    		System.exit(0);
	    	}
	    });
	    this.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_EQUALS) {
					if (curProject!=null)
						curProject.map.scaleBy(2);
				} else if (arg0.getKeyCode() == KeyEvent.VK_MINUS) {
					if (curProject!=null)
						curProject.map.scaleBy(0.5);
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				
			}
			
		});
	    
	}
	
	private JMenuBar createMainMenu() {
		JMenuBar menu = new JMenuBar();
			
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
			JMenu newMenu = new JMenu("New"); 
			newMenu.setMnemonic(KeyEvent.VK_N);
				JMenuItem fromSeedMenu = new JMenuItem("From Seed"); fromSeedMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
				JMenuItem fromFileMenu = new JMenuItem("From File"); 
				JMenuItem fromServerMenu = new JMenuItem("From Server");
			JMenuItem saveMenu = new JMenuItem("Save Player Locations"); saveMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
			JMenuItem exitMenu = new JMenuItem("Exit");
		JMenu scriptMenu = new JMenu("Script");
			JMenuItem newScriptMenu = new JMenuItem("New");
			JMenuItem openScriptMenu = new JMenuItem("Open");
			JMenuItem saveScriptMenu = new JMenuItem("Save");
			JMenuItem runScriptMenu = new JMenuItem("Run");
		mapMenu = new JMenu("Map");
		mapMenu.setMnemonic(KeyEvent.VK_M);
			JMenu findMenu = new JMenu("Find");
				JMenuItem findBiomeMenu = new JMenuItem("Biome"); 
				JMenuItem findVillageMenu = new JMenuItem("Village");
				JMenuItem findStrongholdMenu = new JMenuItem("Stronghold");findStrongholdMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
			JMenu gotoMenu = new JMenu("Go To");
				JMenuItem gotoCoordMenu = new JMenuItem("Coordinate"); gotoCoordMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
				JMenuItem gotoPlayerMenu = new JMenuItem("Player");
				JMenuItem gotoSpawnMenu = new JMenuItem("Spawn");
				JMenuItem gotoChunkMenu = new JMenuItem("Chunk");
			JMenu layersMenu = new JMenu("Layers");
				layerSlimeMenu = new JCheckBoxMenuItem("Slimes"); layerSlimeMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK));
				layerGridMenu = new JCheckBoxMenuItem("Grid"); layerGridMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK));
				layerNetherMenu = new JCheckBoxMenuItem("Netherholds"); layerNetherMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_DOWN_MASK));
					layerNetherMenu.setModel(Options.instance.showNetherFortresses);
				layerIconMenu = new JCheckBoxMenuItem("Icons"); layerIconMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.CTRL_DOWN_MASK));
					layerIconMenu.setModel(Options.instance.showIcons);
			JMenuItem captureMenu = new JMenuItem("Capture"); captureMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
		JMenu helpMenu = new JMenu("Help");
			JMenuItem checkMenu = new JMenuItem("Check for updates");
			JMenuItem aboutMenu = new JMenuItem("About");

		menu.add(fileMenu);
			fileMenu.add(newMenu);
				newMenu.add(fromSeedMenu);
				newMenu.add(fromFileMenu);
				//newMenu.add(fromServerMenu);
			fileMenu.add(saveMenu);
			fileMenu.add(exitMenu);
		//menu.add(scriptMenu);
			scriptMenu.add(newScriptMenu);
			scriptMenu.add(openScriptMenu);
			scriptMenu.add(saveScriptMenu);
			scriptMenu.add(runScriptMenu);
		menu.add(mapMenu);
			mapMenu.add(findMenu);
				//findMenu.add(findBiomeMenu);
				//findMenu.add(findVillageMenu);
				findMenu.add(findStrongholdMenu);
			mapMenu.add(gotoMenu);
				gotoMenu.add(gotoCoordMenu);
				gotoMenu.add(gotoPlayerMenu);
				//gotoMenu.add(gotoSpawnMenu);
				//gotoMenu.add(gotoChunkMenu);
			mapMenu.add(layersMenu);
				layersMenu.add(layerSlimeMenu);
				layersMenu.add(layerGridMenu);
				layersMenu.add(layerNetherMenu);
				layersMenu.add(layerIconMenu);
			mapMenu.add(captureMenu);
		menu.add(helpMenu);
			helpMenu.add(checkMenu);
			helpMenu.add(aboutMenu);
		

		final FinderWindow window = this;
		scriptMenu.setEnabled(false);
		findBiomeMenu.setEnabled(false);
		mapMenu.setEnabled(false);
		findVillageMenu.setEnabled(false);
		fromServerMenu.setEnabled(false);
		gotoChunkMenu.setEnabled(false);
		gotoSpawnMenu.setEnabled(false);
		layerIconMenu.setSelected(true);
		if (!Options.instance.saveEnabled)
			saveMenu.setEnabled(false);
		captureMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fc = new JFileChooser();
				fc.addChoosableFileFilter(new FileFilter() {
					public boolean accept(File f) {
						
					    if (f.isDirectory()) {
					        return true;
					    }
					    String[] st = f.getName().split("/.");
						return st[st.length - 1].equalsIgnoreCase("png");
					}
					@Override
					public String getDescription() {
						return "Portable Network Graphic (*.PNG)";
					}
				});
				fc.setAcceptAllFileFilterUsed(false);
				int returnVal = fc.showSaveDialog(window);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String s = fc.getSelectedFile().toString();
					if (!s.toLowerCase().endsWith(".png")) {
						s += ".png";
					}
					curProject.map.saveToFile(new File(s));
					
				}
			}
		});
		
		layerGridMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curProject.setGridLayer(layerGridMenu.isSelected());
			}
		});
		layerSlimeMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curProject.setSlimeLayer(layerSlimeMenu.isSelected());
			}
		});
		checkMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				(new UpdateManager(window)).start();
			}
		});
		aboutMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(window, "Advanced Minecraft Interfacing and Data/Structure Tracking (AMIDST)\nBy Skidoodle (amidst.project@gmail.com)");
			}
		});
		
		saveMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (curProject.saveLoaded) {
					for (Player player : curProject.save.getPlayers()) {
						if (player.needSave) {
							curProject.save.movePlayer(player.getName(), player.x, player.y);
							player.needSave = false;
						}
					}
				}
			}
		});
		gotoPlayerMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (curProject.saveLoaded) {
					List<Player> players = curProject.save.getPlayers();
					Object[] options = new Object[players.size()];
					for (int i = 0; i < options.length; i++) {
						options[i] = "Player \"" + players.get(i).getName() + "\" at (" + players.get(i).x + ", " + players.get(i).y + ")";
					}
					String s = (String)JOptionPane.showInputDialog(
					                    window,
					                    "Select Player:",
					                    "Go to...",
					                    JOptionPane.PLAIN_MESSAGE,
					                    null,
					                    options,
					                    options[0]);
					for (int i = 0; i < options.length; i++) {
						if (s.equals(options[i])) {
							curProject.moveMapTo(players.get(i).x >> 2, players.get(i).y >> 2);
						}
					}
				}
			}
		});
		findStrongholdMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Point[] sholds = curProject.manager.strongholds;
				Object[] options = {
						"Stronghold at (" + sholds[0].x + ", " + sholds[0].y + ")", 
						"Stronghold at (" + sholds[1].x + ", " + sholds[1].y + ")", 
						"Stronghold at (" + sholds[2].x + ", " + sholds[2].y + ")"};
				String s = (String)JOptionPane.showInputDialog(
				                    window,
				                    "Select Stronghold:",
				                    "Go to...",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    options,
				                    options[0]);
				if (s!=null) {
					for (int i = 0; i < 3; i++) {
						if (s.equals(options[i])) {
							curProject.moveMapTo(sholds[i].x >> 2, sholds[i].y >> 2);
						}
					}
				}
			}
		});
		
		fromFileMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fc = new JFileChooser();
				fc.addChoosableFileFilter(SaveLoader.getFilter());
				fc.setAcceptAllFileFilterUsed(false);
				fc.setCurrentDirectory(SaveLoader.getPath("saves"));
				int returnVal = fc.showOpenDialog(window);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					SaveLoader s = new SaveLoader(f);
					setProject(new Project(s));
				}
			}
		});
		fromSeedMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
		        //Create the JOptionPane.
				String s = JOptionPane.showInputDialog(null, "Enter seed...", "New Project", JOptionPane.QUESTION_MESSAGE);
				if (s!=null) {
					Object[] possibilities = {"default","flat", "largeBiomes"};
					String worldType = (String)JOptionPane.showInputDialog(
					                    null,
					                    "Enter world type...\n",
					                    "New Project",
					                    JOptionPane.PLAIN_MESSAGE,
					                    null,
					                    possibilities,
					                    "default");
	
					//If a string was returned, say so.
					if (worldType != null) {
						setProject(new Project(s, window, worldType));
					}
				}
			}
		});
		gotoCoordMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String s = JOptionPane.showInputDialog(null, "Enter coordinates: (Ex. 123,456)", "Go To", JOptionPane.QUESTION_MESSAGE);
				if (s!=null) {
					String[] c = s.split(",");
					long x = 0, y = 0;
					boolean w = true;
					try {
						x = Long.parseLong(c[0]) >> 2;
						y = Long.parseLong(c[1]) >> 2;
					} catch (Exception e) {
						w = false;
					}
					if (w)
						curProject.moveMapTo((int)x, (int)y);
				}
			}
		});
		exitMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int ret = JOptionPane.showConfirmDialog(window, "Are you sure you want to exit?");
				if (ret==0) {
					System.exit(0);
				}
			}
		});
		
		return menu;
	}
	
	public void setProject(Project ep) {
		// FIXME Release resources.
		if (curProject != null) {
			curProject.dispose();
			pane.remove(curProject);
			System.gc();
		}
		mapMenu.setEnabled(true);
		curProject = ep;
		pane.add(curProject, BorderLayout.CENTER);
		
		this.validate();
		
	}
	private void loadMinecraft() {
		fc = new JFileChooser();
		Class<?> mc = null;
		File s = null;
		
		//Temporary fix -- Should be removed in a few patches.
		if (pref.getBoolean("osxMistake", true)) {
			pref.putBoolean("osxMistake", false);
			pref.remove("jar");
		}
		
		try {
			s = SaveLoader.getPath("bin/minecraft.jar");
			String jar = pref.get("jar", null);
			ClasspathHacker.addFile((jar != null) ? new File(jar) : s);
			mc = ClassLoader.getSystemClassLoader().loadClass("net.minecraft.client.Minecraft");
		} catch (Exception e) {
			e.printStackTrace();
			fc.addChoosableFileFilter(new FileFilter() {
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().toLowerCase().endsWith(".jar");
				}

				@Override
				public String getDescription() {
					return "Java Executable (*.JAR)";
				}
			});
			
			fc.setAcceptAllFileFilterUsed(false);
			JOptionPane.showMessageDialog(this, "Unable to find minecraft.jar, please locate it.");
			int returnVal = fc.showOpenDialog(this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				s = fc.getSelectedFile();
				try {
					ClasspathHacker.addFile(s);
					mc = ClassLoader.getSystemClassLoader().loadClass("net.minecraft.client.Minecraft");
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(this, "Error loading minecraft.jar");
					System.exit(0);
				}
			} else {
				System.exit(0);
			}
		}
		try {
			pref.put("jar", s.getCanonicalPath());
			
			String typeDump = "";
			Field fields[] = mc.getDeclaredFields();
			for (Field field : fields) {
				String typeString = field.getType().toString();
				if (typeString.startsWith("class ") && !typeString.contains("."))
					typeDump += typeString.substring(6);
			}
			System.out.println(typeDump.replace("[", "-"));
			String worldName;
			Field f;
			java.lang.reflect.Type t;
			
			boolean is25 = false;
			boolean is131 = false;
			if (typeDump.equals("[Bbeabdsbawemabdtbfzbdwngngbevawfbgfawvawvaxrawbbfrausbjhaycawwaraavybkdavwbjvbkila")) {
				worldName = "aab";
				is131 = true;
			} else if (typeDump.equals("[Baywayoaaszleaypbavaysmdazratabbaatqatqaulaswbanarnbdzauwatraohastbevasrbenbezbdmbdjkh")) {
				worldName = "yc";
				is131 = true;
			} else if (typeDump.equals("[Bayoaygaasrleayhbakaykmdazfassbapatjatjaueasobacarfbdoaupatkanzaslbekasjbecbenbdbbcykh")) {
				worldName = "xv";
				is131 = true;
			} else if (typeDump.equals("[Baxgawyaarjkpawzayyaxclnaxxarkazcasbasbaswargaytaqabcbathascamuardbcxarbbcpbdabbobbljy")) {
				worldName = "xe";
				is131 = true;
			} else if (typeDump.equals("[Batkatcaaofjbatdavbatgjwaubaogavfaovaovapnaocauwamxaxvapyaowajqanzayqanxayjaytaxkaxhik")) {
				worldName = "up";
				is131 = true;
			} else if (typeDump.equals("[Batjatbaaoejaatcavaatfjvauaaofaveaouaouapmaobauvamwaxuapxaovajpanyaypanwayiaysaxjaxgij")) {
				worldName = "uo";
				is131 = true;
			} else if (typeDump.equals("[Barjarbaikarcataarfjfasaateamuamuanmasvavuanxamvaizawpawiawsavjavght")) {
				worldName = "ty";
			} else if (typeDump.equals("[Bafswjoganiammmnqaptajvejhfahrrmrmabmmkphdvkrarquyaqzuelaxgasiivattuucypn")) {
				worldName = "adn";
			} else if (typeDump.equals("[Bafgvznwamualymdqapfajhefhbahercrcabamaoxdsknarbuoaqktuktwvartir[J[Jateukcy")) {
				worldName = "adb";
				is25 = true;
			} else if (typeDump.equals("[Baflwdoaamzamdmhqapkajmehhdahjrgrgabfmepbdukpargusaqptykwxaaryit[J[Jatjuocy")){
				worldName = "adg";
			} else if (typeDump.equals("[Bbdtbdlbavymabdmbfsbdpngngbeoavzbfyawpawpaxlavvbfkaumbjaaxwawqaquavsbjwavqbjobkbla")){
				worldName = "zv";
				is131 = true;
			} else {
				//Get world
				f = mc.getDeclaredField("f");
				t = f.getType();
				worldName = t.toString().split(" ")[1];
				if (worldName.equals("lj")) {
					//Version 12w19a
					worldName = "abo";
				} else if (worldName.equals("lr")) {
					//Version 12w21a
					worldName = "ach";
				} else if (worldName.equals("md")) {
					//Version 12w21b
					worldName = "act";
				} else if (worldName.equals("mf")) {
					//Version 12w22a
					worldName = "acz";
					String otherName = mc.getDeclaredField("a").getType().toString().split(" ")[1];
					if (otherName.equals("aff")) {
						worldName = "adb";
					}
				} else if (worldName.equals("mb")) {
					//Version 12w24a
					worldName = "acv";
				}
			}
			System.out.println("World class : " + worldName);
			
			//Find biomeManager:
			mc = ClassLoader.getSystemClassLoader().loadClass(worldName);
			Method m = null;
			boolean v13 = false;
			try {
				m = mc.getMethod("a");
			} catch (NoSuchMethodException e3) {
				v13 = true;
				System.out.println("1.3-pre or newer found.");
			}
			if (v13) {
				try {
					m = mc.getMethod("q");
				} catch (NoSuchMethodException e4) {
					try { 
						m = mc.getMethod("r");
					} catch (NoSuchMethodException e5) {
						try {
							m = mc.getMethod("s");
						} catch (NoSuchMethodException e6) {
							m = mc.getMethod("t");
						}
					}
				
				}
			}
			t = m.getReturnType();
			if (t.toString().equals("boolean")) {
				//12w07a or newer.
				m = mc.getDeclaredMethod("i");
				t = m.getReturnType();
				System.out.println("Version 12w07a or newer found!");
			}
			if (t.toString().equals("void")) {
				System.out.println("Version 1.4.2 or newer found.");
				
			}
			String chunkName = t.toString().split(" ")[1];
			mc = ClassLoader.getSystemClassLoader().loadClass(chunkName);
			biomeFinder = mc;
			System.out.println("Biome Finder : " + chunkName);
			
			f = mc.getDeclaredField(is131?"d":"b");
			t = f.getType();
			String biomeName = t.toString().split(" ")[1];
			if (biomeName.equals("[F")) {
				f = mc.getDeclaredField("c");
				t = f.getType();
				biomeName = t.toString().split(" ")[1];
			}
			if (biomeName.equals("[F")) {
				f = mc.getDeclaredField("d");
				t = f.getType();
				biomeName = t.toString().split(" ")[1];
			}
			ReflectionInfo.chunkName = biomeName;
			f = mc.getDeclaredField(is131?"d":"a");
			t = f.getType();
			ReflectionInfo.biomeName = t.toString().split(" ")[1];
			String intCache;
			System.out.println(biomeName);
			if (biomeName.equals("ait")&&is131) {
				ReflectionInfo.version = "1.5.1";
				ReflectionInfo.versionID = 60;
				intCache = "air";
			} else if (biomeName.equals("ain")&&is131) {
				ReflectionInfo.version = "1.5.0";
				ReflectionInfo.versionID = 58;
				intCache = "ail";
			} else if (biomeName.equals("agw")&&is131) {
				ReflectionInfo.version = "1.4.6";
				ReflectionInfo.versionID = 54;
				intCache = "agu";
			} else if (biomeName.equals("agp")&&is131) {
				ReflectionInfo.version = "1.4.5";
				ReflectionInfo.versionID = 53;
				intCache = "agn";
			} else if (biomeName.equals("afu")&&is131) {
				ReflectionInfo.version = "1.4.2";
				ReflectionInfo.versionID = 50;
				intCache = "afs";
			} else if (biomeName.equals("adc")&&is131) {
				ReflectionInfo.version = "1.3.2";
				ReflectionInfo.versionID = 43;
				intCache = "ada";
			} else if (biomeName.equals("adb")&&is131) {
				ReflectionInfo.version = "1.3.1";
				ReflectionInfo.versionID = 42;
				intCache = "acz";
			} else if (biomeName.equals("acl")&&v13) {
				ReflectionInfo.version = "1.3pre";
				ReflectionInfo.versionID = 40;
				intCache = "acj";
			} else if (biomeName.equals("acs")) {
				ReflectionInfo.version = "12w27a";
				ReflectionInfo.versionID = 28;
				intCache = "av";
			} else  if (biomeName.equals("acl")) {
				ReflectionInfo.version = "12w26a";
				ReflectionInfo.versionID = 27;
				intCache = "av";
			} else if (biomeName.equals("aca")) {
				ReflectionInfo.version = "12w24a";
				ReflectionInfo.versionID = 25;
				intCache = "av";
			} else if (biomeName.equals("acg")) {
				if (is25) {
					ReflectionInfo.version = "12w25a";
					ReflectionInfo.versionID = 26;
					intCache = "av";
				} else {
					ReflectionInfo.version = "12w23b";
					ReflectionInfo.versionID = 24;
					intCache = "ay";
				}
			} else if (biomeName.equals("ace")) {
				ReflectionInfo.version = "12w22a";
				ReflectionInfo.versionID = 23;
				intCache = "ay";
				Options.instance.saveEnabled = false;
			} else if (biomeName.equals("aby")) {
				ReflectionInfo.version = "12w21b";
				ReflectionInfo.versionID = 22;
				intCache = "ax";
				Options.instance.saveEnabled = false;
			} else if (biomeName.equals("abm")) {
				ReflectionInfo.version = "12w21a";
				ReflectionInfo.versionID = 21;
				intCache = "ar";
				Options.instance.saveEnabled = false;
			} else if (biomeName.equals("aau")) {
				ReflectionInfo.version = "12w19a";
				ReflectionInfo.versionID = 19;
				intCache = "ao";
			} else if (biomeName.equals("wp")) {
				ReflectionInfo.version = "1.2.4";
				ReflectionInfo.versionID = 17;
				intCache = "ad";
			} else if (biomeName.equals("wl")) {
				ReflectionInfo.version = "1.2.2";
				ReflectionInfo.versionID = 16;
				intCache = "ac";
			} else if (biomeName.equals("wj")) {
				ReflectionInfo.version = "12w08a";
				ReflectionInfo.versionID = 15;
				intCache = "ac";
			} else if (biomeName.equals("wd")) {
				ReflectionInfo.version = "12w07b";
				intCache = "ab";
				ReflectionInfo.versionID = 14;
				
				//Skipping 12w07a
			} else if (biomeName.equals("wb")) {
				ReflectionInfo.version = "12w06a";
				intCache = "ab";
				ReflectionInfo.versionID = 12;
			} else if (biomeName.equals("vy")) {
				ReflectionInfo.version = "12w05a";
				intCache = "ab";
				ReflectionInfo.versionID = 11;
			} else if (biomeName.equals("vu")) {
				ReflectionInfo.version = "12w04a";
				intCache = "ab";
				ReflectionInfo.versionID = 10;
			} else if (biomeName.equals("vj")) {
				intCache = "ab";
				ReflectionInfo.version = "12w03a";
				ReflectionInfo.versionID = 9;
			} else if (biomeName.equals("vc")) {
				intCache = "ab";
				ReflectionInfo.version = "1.1";
				ReflectionInfo.versionID = 8;
			} else if (biomeName.equals("jx")) {
				intCache = "bm";
				ReflectionInfo.version = "1.0";
				ReflectionInfo.versionID = 7;
			} else if (biomeName.equals("uk")) {
				intCache = "z";
				ReflectionInfo.version = "1.9-pre6";
				ReflectionInfo.versionID = 6;
			} else if (biomeName.equals("ug")) {
				intCache = "y";
				ReflectionInfo.version = "1.9-pre5";
				ReflectionInfo.versionID = 5;
			} else if (biomeName.equals("uh")) {
				intCache = "y";
				ReflectionInfo.version = "1.9-pre4";
				MapGenStronghold.reset0 = true;
				ReflectionInfo.versionID = 4;
			} else if (biomeName.equals("to")) {
				intCache = "x";
				ReflectionInfo.version = "1.9-pre3";
				ReflectionInfo.versionID = 3;
			} else if (biomeName.equals("sv")) {
				intCache = "x";
				ReflectionInfo.version = "1.9-pre2";
				ReflectionInfo.versionID = 2;
			} else if (biomeName.equals("sq")) {
				intCache = "x";
				ReflectionInfo.version = "1.9-pre1";
				ReflectionInfo.versionID = 1;
			} else if (biomeName.equals("rj")) {
				intCache = "w";
				ReflectionInfo.version = "1.8.1";
				ReflectionInfo.versionID = 0;
			} else {
				ReflectionInfo.version = "unknown";
				ReflectionInfo.versionID = -1;
				intCache = "ab";
				String st = JOptionPane.showInputDialog(
					null,
					"Unsupported version of minecraft detected!\nEnter code to continue:\n(Name of the IntCache class)",
					"Error",
					JOptionPane.ERROR_MESSAGE);
				if (st==null) {
					System.exit(0);
				} else {
					intCache = st;
				}
			}
			System.out.println("Version " + ReflectionInfo.version + " detected. " + (Options.instance.saveEnabled ? "" : "Saves disabled."));
//			pre5 - y, ug
//			pre4 - y, uh
//			pre3 - x, to
//			pre2 - x, sv
//			pre1 - x, sq
//			pre0 - w, rj
			ReflectionInfo.intCacheName = intCache;
		} catch (java.lang.NoClassDefFoundError e2) {
			JOptionPane.showMessageDialog(this, "AMIDST ran in JAR mode without -noverify!\nUse: java -noverify -jar AMIDST.jar");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
