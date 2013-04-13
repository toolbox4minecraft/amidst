package MoF;

import amidst.Log;
import amidst.Options;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public enum ReflectionInfo {
	instance;
	
	public Class<?> biomeFinder;
	public String biomeName;
	public String intCacheName;
	public String chunkName;
	public String version;
	public int versionID;
	
	private ReflectionInfo() {
		JFileChooser fc = new JFileChooser();
		Class<?> mc = null;
		File s = null;
		
		try {
			ClasspathHacker.addFile(Options.instance.jar.get());
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
			JOptionPane.showMessageDialog(null, "Unable to find minecraft.jar, please locate it.");
			int returnVal = fc.showOpenDialog(null);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				s = fc.getSelectedFile();
				try {
					ClasspathHacker.addFile(s);
					mc = ClassLoader.getSystemClassLoader().loadClass("net.minecraft.client.Minecraft");
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Error loading minecraft.jar");
					System.exit(0);
				}
			} else {
				System.exit(0);
			}
		}
		
		try {
			if (s != null)
				Options.instance.jar.set(s);
			
			String typeDump = "";
			Field fields[] = mc.getDeclaredFields();
			for (Field field : fields) {
				String typeString = field.getType().toString();
				if (typeString.startsWith("class ") && !typeString.contains("."))
					typeDump += typeString.substring(6);
			}
			Log.debug(typeDump.replace("[", "-"));
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
			Log.debug("World class:", worldName);
			
			//Find biomeManager:
			mc = ClassLoader.getSystemClassLoader().loadClass(worldName);
			Method m = null;
			boolean v13 = false;
			try {
				m = mc.getMethod("a");
			} catch (NoSuchMethodException e3) {
				v13 = true;
				Log.debug("1.3-pre or newer found.");
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
				Log.debug("Version 12w07a or newer found!");
			}
			if (t.toString().equals("void")) {
				Log.debug("Version 1.4.2 or newer found.");
			}
			chunkName = t.toString().split(" ")[1];
			mc = ClassLoader.getSystemClassLoader().loadClass(chunkName);
			biomeFinder = mc;
			Log.debug("Biome Finder:", chunkName);
			
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
			chunkName = biomeName;
			f = mc.getDeclaredField(is131?"d":"a");
			t = f.getType();
			biomeName = t.toString().split(" ")[1];
			String intCache;
			Log.debug("Biome class:", biomeName);
			if (biomeName.equals("ait")&&is131) {
				version = "1.5.1";
				versionID = 60;
				intCache = "air";
			} else if (biomeName.equals("ain")&&is131) {
				version = "1.5.0";
				versionID = 58;
				intCache = "ail";
			} else if (biomeName.equals("agw")&&is131) {
				version = "1.4.6";
				versionID = 54;
				intCache = "agu";
			} else if (biomeName.equals("agp")&&is131) {
				version = "1.4.5";
				versionID = 53;
				intCache = "agn";
			} else if (biomeName.equals("afu")&&is131) {
				version = "1.4.2";
				versionID = 50;
				intCache = "afs";
			} else if (biomeName.equals("adc")&&is131) {
				version = "1.3.2";
				versionID = 43;
				intCache = "ada";
			} else if (biomeName.equals("adb")&&is131) {
				version = "1.3.1";
				versionID = 42;
				intCache = "acz";
			} else if (biomeName.equals("acl")&&v13) {
				version = "1.3pre";
				versionID = 40;
				intCache = "acj";
			} else if (biomeName.equals("acs")) {
				version = "12w27a";
				versionID = 28;
				intCache = "av";
			} else  if (biomeName.equals("acl")) {
				version = "12w26a";
				versionID = 27;
				intCache = "av";
			} else if (biomeName.equals("aca")) {
				version = "12w24a";
				versionID = 25;
				intCache = "av";
			} else if (biomeName.equals("acg")) {
				if (is25) {
					version = "12w25a";
					versionID = 26;
					intCache = "av";
				} else {
					version = "12w23b";
					versionID = 24;
					intCache = "ay";
				}
			} else if (biomeName.equals("ace")) {
				version = "12w22a";
				versionID = 23;
				intCache = "ay";
				Options.instance.saveEnabled = false;
			} else if (biomeName.equals("aby")) {
				version = "12w21b";
				versionID = 22;
				intCache = "ax";
				Options.instance.saveEnabled = false;
			} else if (biomeName.equals("abm")) {
				version = "12w21a";
				versionID = 21;
				intCache = "ar";
				Options.instance.saveEnabled = false;
			} else if (biomeName.equals("aau")) {
				version = "12w19a";
				versionID = 19;
				intCache = "ao";
			} else if (biomeName.equals("wp")) {
				version = "1.2.4";
				versionID = 17;
				intCache = "ad";
			} else if (biomeName.equals("wl")) {
				version = "1.2.2";
				versionID = 16;
				intCache = "ac";
			} else if (biomeName.equals("wj")) {
				version = "12w08a";
				versionID = 15;
				intCache = "ac";
			} else if (biomeName.equals("wd")) {
				version = "12w07b";
				intCache = "ab";
				versionID = 14;
				
				//Skipping 12w07a
			} else if (biomeName.equals("wb")) {
				version = "12w06a";
				intCache = "ab";
				versionID = 12;
			} else if (biomeName.equals("vy")) {
				version = "12w05a";
				intCache = "ab";
				versionID = 11;
			} else if (biomeName.equals("vu")) {
				version = "12w04a";
				intCache = "ab";
				versionID = 10;
			} else if (biomeName.equals("vj")) {
				intCache = "ab";
				version = "12w03a";
				versionID = 9;
			} else if (biomeName.equals("vc")) {
				intCache = "ab";
				version = "1.1";
				versionID = 8;
			} else if (biomeName.equals("jx")) {
				intCache = "bm";
				version = "1.0";
				versionID = 7;
			} else if (biomeName.equals("uk")) {
				intCache = "z";
				version = "1.9-pre6";
				versionID = 6;
			} else if (biomeName.equals("ug")) {
				intCache = "y";
				version = "1.9-pre5";
				versionID = 5;
			} else if (biomeName.equals("uh")) {
				intCache = "y";
				version = "1.9-pre4";
				MapGenStronghold.reset0 = true;
				versionID = 4;
			} else if (biomeName.equals("to")) {
				intCache = "x";
				version = "1.9-pre3";
				versionID = 3;
			} else if (biomeName.equals("sv")) {
				intCache = "x";
				version = "1.9-pre2";
				versionID = 2;
			} else if (biomeName.equals("sq")) {
				intCache = "x";
				version = "1.9-pre1";
				versionID = 1;
			} else if (biomeName.equals("rj")) {
				intCache = "w";
				version = "1.8.1";
				versionID = 0;
			} else {
				version = "unknown";
				versionID = -1;
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
			Log.debug("Version:", version);
			if (!Options.instance.saveEnabled) Log.debug("Saves disabled.");
//			pre5 - y, ug
//			pre4 - y, uh
//			pre3 - x, to
//			pre2 - x, sv
//			pre1 - x, sq
//			pre0 - w, rj
			intCacheName = intCache;
		} catch (java.lang.NoClassDefFoundError e2) {
			JOptionPane.showMessageDialog(null, "AMIDST ran in JAR mode without -noverify!\nUse: java -noverify -jar AMIDST.jar");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
