package MoF;

import amidst.Log;
import amidst.Options;
import amidst.foreign.VersionInfo;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public enum ReflectionInfo {
	instance;
	
	public Class<?> biomeFinder;
	public String biomeName;
	public String chunkName;
	public VersionInfo version;
	
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
			for (Field field : mc.getDeclaredFields()) {
				String typeString = field.getType().toString();
				if (typeString.startsWith("class ") && !typeString.contains("."))
					typeDump += typeString.substring(6);
			}
			Log.debug(typeDump.replace("[", "-"));
			String worldName;
			
			boolean is25 = false;
			boolean atLeast131 = false;
			if (typeDump.equals("[Bbeabdsbawemabdtbfzbdwngngbevawfbgfawvawvaxrawbbfrausbjhaycawwaraavybkdavwbjvbkila")) {
				worldName = "aab";
				atLeast131 = true;
			} else if (typeDump.equals("[Baywayoaaszleaypbavaysmdazratabbaatqatqaulaswbanarnbdzauwatraohastbevasrbenbezbdmbdjkh")) {
				worldName = "yc";
				atLeast131 = true;
			} else if (typeDump.equals("[Bayoaygaasrleayhbakaykmdazfassbapatjatjaueasobacarfbdoaupatkanzaslbekasjbecbenbdbbcykh")) {
				worldName = "xv";
				atLeast131 = true;
			} else if (typeDump.equals("[Baxgawyaarjkpawzayyaxclnaxxarkazcasbasbaswargaytaqabcbathascamuardbcxarbbcpbdabbobbljy")) {
				worldName = "xe";
				atLeast131 = true;
			} else if (typeDump.equals("[Batkatcaaofjbatdavbatgjwaubaogavfaovaovapnaocauwamxaxvapyaowajqanzayqanxayjaytaxkaxhik")) {
				worldName = "up";
				atLeast131 = true;
			} else if (typeDump.equals("[Batjatbaaoejaatcavaatfjvauaaofaveaouaouapmaobauvamwaxuapxaovajpanyaypanwayiaysaxjaxgij")) {
				worldName = "uo";
				atLeast131 = true;
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
				atLeast131 = true;
			} else {
				//Get world
				Type worldType = mc.getDeclaredField("f").getType();
				worldName = worldType.toString().split(" ")[1];
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
			boolean atLeast13pre = false;
			try {
				m = mc.getMethod("a");
			} catch (NoSuchMethodException e3) {
				atLeast13pre = true;
			}
			if (atLeast13pre) {
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
			
			if (atLeast13pre) {
				if (!atLeast131)
					Log.debug("1.3-pre or newer found.");
				else
					Log.debug("1.3.1 or newer found.");
			}
			
			Type t = m.getReturnType();
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
			
			Field f = mc.getDeclaredField(atLeast131 ? "d" : "b");
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
			f = mc.getDeclaredField(atLeast131 ? "d" : "a");
			t = f.getType();
			biomeName = t.toString().split(" ")[1];
			Log.debug("Biome class:", biomeName);
			
			version = VersionInfo.unknown;
			for (VersionInfo v : VersionInfo.values()) {
				if (biomeName.equals(v.biomeName)) {
					if (v == VersionInfo.V12w25a || v == VersionInfo.V12w23b) {
						if ((v == VersionInfo.V12w25a) == is25)
							version = v;
					} else if (v.isAtLeast(VersionInfo.V1_3_1) == atLeast131
							&& v.isAtLeast(VersionInfo.V1_3pre) == atLeast13pre) {
						version = v;
					}
				}
			}
			
			Log.debug("Version:", version);
			
			if (version == VersionInfo.unknown) {
				JOptionPane.showMessageDialog(null, "Unsupported version of minecraft detected!", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			
			if (!version.saveEnabled())
				Log.debug("Saves disabled.");
//			pre5 - y, ug
//			pre4 - y, uh
//			pre3 - x, to
//			pre2 - x, sv
//			pre1 - x, sq
//			pre0 - w, rj
		} catch (java.lang.NoClassDefFoundError e2) {
			JOptionPane.showMessageDialog(null, "AMIDST ran in JAR mode without -noverify!\nUse: java -noverify -jar AMIDST.jar");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
