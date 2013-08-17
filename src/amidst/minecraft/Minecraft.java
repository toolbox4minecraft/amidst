package amidst.minecraft;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import amidst.Amidst;
import amidst.Log;
import amidst.bytedata.ByteClass;
import amidst.bytedata.CCByteSearch;
import amidst.bytedata.CCConstructorPreset;
import amidst.bytedata.CCFloatMatch;
import amidst.bytedata.CCLongMatch;
import amidst.bytedata.CCMethodPreset;
import amidst.bytedata.CCMulti;
import amidst.bytedata.CCPropertyPreset;
import amidst.bytedata.CCRequire;
import amidst.bytedata.CCStringMatch;
import amidst.bytedata.ClassChecker;
import amidst.foreign.VersionInfo;

public class Minecraft {
	private static final int MAX_CLASSES = 128;
	private Class<?> mainClass;
	private URLClassLoader classLoader;
	private String versionID; 
	private URL urlToJar;
	private static Minecraft activeMinecraft; 
	public HashMap<String, MinecraftObject> globalMap;
	
	private static ClassChecker[] classChecks = new ClassChecker[] {
			new CCStringMatch("BiomeGenBase", "MushroomIsland"),
			new CCStringMatch("WorldType", "default_1_1"),
			new CCStringMatch("Server", "To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\""),
			new CCRequire(
					new CCStringMatch("WorldSolo", "iceandsnow")
			, "WorldCore"),
			new CCStringMatch("MapGenStronghold", "Placed stronghold in INVALID biome at"),
			new CCStringMatch("AnvilSaveConverter", "Scanning folders..."),
			new CCFloatMatch("WorldProvider", 0.84705883f),
			new CCStringMatch("Chunk", "recheckGaps"),
			new CCLongMatch("GenLayer", 1000L, 2001L, 2000L),
			new CCRequire(
					new CCPropertyPreset(
							"WorldType",
							"b", "DEFAULT",
							"c", "FLAT",
							"d", "LARGE_BIOMES"
					)
			, "WorldType"),
			new CCRequire(
					new CCMethodPreset(
							"GenLayer",
							"a(long, @WorldType)", "initializeAllBiomeGenerators",
							"a(int, int, int, int)", "getInts"
					)
			, "GenLayer"),
			new CCRequire(
					new CCConstructorPreset(
							"WorldSettings",
							0, "init"
					)
			,"WorldSettings"),
			new CCRequire(new CCMulti(
					new CCMethodPreset(
							"IntCache",
							"a(int)", "getIntCache",
							"a()", "removeIntCache"
					),
					new CCPropertyPreset(
							"IntCache",
							"a", "intCacheSize",
							"b","freeSmallArrays",
							"c","inUseSmallArrays",
							"d","freeLargeArrays",
							"e","inUseLargeArrays"
					)
			), "IntCache"),
			new CCRequire(
					new CCMethodPreset(
							"WorldProvider",
							"a(int)", "getProviderForDimension"
					)
			, "WorldProvider"),
	};
	private HashMap<String, ByteClass> byteClassMap;
	private HashMap<String, MinecraftClass> nameMap;
	private HashMap<String, MinecraftClass> classMap;
	private Vector<String> byteClassNames;
	
	public VersionInfo version = VersionInfo.unknown;
	
	public Minecraft() throws MalformedURLException {
		this(Amidst.installInformation.getJarFile().toURI().toURL());
	}
	
	public Minecraft(URL url) {
		byteClassNames = new Vector<String>();
		byteClassMap = new HashMap<String, ByteClass>(MAX_CLASSES);
		urlToJar = url;
		
		Log.i("Reading minecraft.jar...");
		
		int bufferSize = 2048;
		byte dataBuffer[] = new byte[bufferSize];

		Stack<ByteClass> byteClassStack = new Stack<ByteClass>();
		try {
			ZipFile jar = new ZipFile(urlToJar.getFile());
			Enumeration<? extends ZipEntry> enu = jar.entries();
			
			while (enu.hasMoreElements()) {
				ZipEntry entry = enu.nextElement();
				
				String currentEntry = entry.getName();
				String[] nameSplit = currentEntry.split("\\.");
				if (!entry.isDirectory() && (nameSplit.length == 2) && (nameSplit[0].indexOf('/') == -1) && nameSplit[1].equals("class")) {
			        BufferedInputStream is = new BufferedInputStream(jar.getInputStream(entry));
			        byte[] classData = new byte[is.available()];
			        is.read(classData);
			        is.close();
					byteClassStack.push(new ByteClass(nameSplit[0], classData));
				}
			}

			Log.i("Jar load complete.");
		} catch (Exception e) {
			e.printStackTrace();
			Log.kill("Error extracting jar data.");
		}
		
		Log.i("Searching for classes...");
		int checksRemaining = classChecks.length;
		Object[] byteClasses = byteClassStack.toArray();
		boolean[] found = new boolean[byteClasses.length];
		while (checksRemaining != 0) {
			for (int q = 0; q < classChecks.length; q++) {
				for (int i = 0; i < byteClasses.length; i++) {

					if (!found[q]) {
						classChecks[q].check(this, (ByteClass)byteClasses[i]);
						if (classChecks[q].isComplete) {
							Log.i("Found: " + byteClasses[i] + " as " + classChecks[q].getName());
							found[q] = true;
							checksRemaining--;
						}
						// TODO: What is this line, and why is it commented
						//byteClassMap.put(classChecks[q].getName(), classFiles[i].getName().split("\\.")[0]);
					}
				}
				if (!found[q]) {
					classChecks[q].passes--;
					if (classChecks[q].passes == 0) {
						found[q] = true;
						checksRemaining--;
					}
				}
				

			}
		}
		Log.i("Class search complete.");
		System.exit(0);

/*
		String output = "";
		for (String name : byteClassNames) {
			ByteClass bClass = byteClassMap.get(name);
			output += "c:" + bClass.getClassName() + "=" + name + "\n";
			Vector<String[]> methods = bClass.getMethods();
			Vector<String[]> properties = bClass.getProperties();
			Vector<String[]> constructors = bClass.getConstructors();
			for (String[] method : methods) {
				output += "m:" + method[0] + "=" + method[1] + "\n";
			}
			for (String[] property : properties) {
				output += "p:" + property[0] + "=" + property[1] + "\n";
			}
			for (String[] constructor : constructors) {
				output += "i:" + constructor[0] + "=" + constructor[1] + "\n";
			}
		}
		Pattern cPattern = Pattern.compile("@[A-Za-z]+");
		Matcher cMatcher = cPattern.matcher(output);
		String tempOutput = output;
		while (cMatcher.find()) {
			String match = output.substring(cMatcher.start(), cMatcher.end());
			tempOutput = tempOutput.replaceAll(match, getByteClass(match.substring(1)).getClassName());
		}
		output = tempOutput;*/
	}
	/*
	private void createFromMCInfo(MCInfo info) {
		nameMap = new HashMap<String, MinecraftClass>();
		classMap = new HashMap<String, MinecraftClass>();
		try {
			classLoader = new URLClassLoader(new URL[]{urlToJar});
			use();
			mainClass = classLoader.loadClass("net.minecraft.client.Minecraft");
			
			Vector<MinecraftClass> classes = info.getMinecraftClasses();
			Vector<MinecraftProperty> properties = info.getMinecraftProperties();
			Vector<MinecraftMethod> methods = info.getMinecraftMethods();
			Vector<MinecraftConstructor> constructors = info.getMinecraftConstructors();
			for (MinecraftClass clazz : classes) { //Load all the classes into the name dictionary.
				clazz.load(this);
				nameMap.put(clazz.getName(), clazz);
				classMap.put(clazz.getClassName(), clazz);
			}
			for (MinecraftClass clazz : classes) { //Load all the properties and methods.
				for (MinecraftProperty property : properties) {
					if (clazz.getName().equals(property.getParentName()))
						clazz.addProperty(property);
				}
				for (MinecraftMethod method : methods) {
					if (clazz.getName().equals(method.getParentName()))
						clazz.addMethod(method);
				}
				for (MinecraftConstructor constructor : constructors) {
					if (clazz.getName().equals(constructor.getParentName()))
						clazz.addConstructor(constructor);
				}
			}
			activeMinecraft = this;
			//classMap = info.getMinecraftClasses(this);
			//Log.i(classMap.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.i(nameMap.toString());
		
	}*/
	
	
	public URL getPath() {
		return urlToJar;
	}
	
	public void use() {
		Thread.currentThread().setContextClassLoader(classLoader);
		activeMinecraft = this;
	}
	
	public String getVersionID() {
		return versionID;
	}
	public MinecraftClass getClassByName(String name) {
		return nameMap.get(name);
	}
	public URLClassLoader getClassLoader() {
		return classLoader;
	}
	public Class<?> loadClass(String name) {
		try {
			return classLoader.loadClass(name);
		} catch (ClassNotFoundException e) {
			Log.e("Error loading a class (" + name + ")");
			e.printStackTrace();
		}
		return null;
	}
	public MinecraftClass getClassByType(String name) {
		return classMap.get(name);
		
		
		
	}
	public void registerClass(String publicName, ByteClass bClass) {
		if (byteClassMap.get(publicName)==null) {
			byteClassMap.put(publicName, bClass);
			byteClassNames.add(publicName);
		}
		//Log.i(publicName);
	}
	public ByteClass getByteClass(String name) {
		return byteClassMap.get(name);
	}
	public static Minecraft getActiveMinecraft() {
		return activeMinecraft;
	}

	public void setGlobal(String name, MinecraftObject object) {
		globalMap.put(name, object);
	}
	public MinecraftObject getGlobal(String name) {
		return globalMap.get(name);
	}
	
}
