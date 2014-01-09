package amidst.minecraft;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import amidst.Util;
import amidst.bytedata.ByteClass;
import amidst.bytedata.ByteClass.AccessFlags;
import amidst.bytedata.CCLongMatch;
import amidst.bytedata.CCMethodPreset;
import amidst.bytedata.CCMulti;
import amidst.bytedata.CCPropertyPreset;
import amidst.bytedata.CCRequire;
import amidst.bytedata.CCStringMatch;
import amidst.bytedata.CCWildcardByteSearch;
import amidst.bytedata.ClassChecker;
import amidst.json.JarLibrary;
import amidst.json.JarProfile;
import amidst.logging.Log;
import amidst.version.VersionInfo;

public class Minecraft {
	private static final int MAX_CLASSES = 128;
	private Class<?> mainClass;
	private URLClassLoader classLoader;
	private String versionID; 
	private URL urlToJar;
	private File jarFile;
	
	private static ClassChecker[] classChecks = new ClassChecker[] {
			new CCWildcardByteSearch("IntCache", DeobfuscationData.intCache),
			new CCStringMatch("WorldType", "default_1_1"),
			new CCLongMatch("GenLayer", 1000L, 2001L, 2000L),
			new CCStringMatch("IntCache", ", tcache: "),
			(new ClassChecker() {
				@Override
				public void check(Minecraft m, ByteClass bClass) {
					if ((bClass.fields.length == 1) && 
						(bClass.fields[0].accessFlags == (AccessFlags.PRIVATE | AccessFlags.STATIC)) &&
						(bClass.methodCount == 4)) {
						m.registerClass("BlockInit", bClass);
						isComplete = true;
					}
				}
			}),
			new CCRequire(
				new CCPropertyPreset(
					"WorldType",
					"a", "types",
					"b", "default",
					"c", "flat",
					"d", "largeBiomes",
					"e", "amplified",
					"g", "name"
				)
			, "WorldType"),
			new CCRequire(
				new CCMethodPreset(
					"GenLayer",
					"a(long, @WorldType)", "initializeAllBiomeGenerators",
					"a(int, int, int, int)", "getInts"
				)
			, "GenLayer"),
			new CCRequire(new CCMulti(
				new CCMethodPreset(
					"IntCache",
					"a(int)", "getIntCache",
					"a()", "resetIntCache",
					"b()", "getInformation"
				),
				new CCPropertyPreset(
					"IntCache",
					"a", "intCacheSize",
					"b","freeSmallArrays",
					"c","inUseSmallArrays",
					"d","freeLargeArrays",
					"e","inUseLargeArrays"
				)
			), "IntCache")
	};
	private HashMap<String, ByteClass> byteClassMap;
	private HashMap<String, MinecraftClass> nameMap;
	private HashMap<String, MinecraftClass> classMap;
	private Vector<String> byteClassNames;
	
	public String versionId;
	public VersionInfo version = VersionInfo.unknown;
	
	public Minecraft(File jarFile)  throws MalformedURLException {
		this.jarFile = jarFile;
		byteClassNames = new Vector<String>();
		byteClassMap = new HashMap<String, ByteClass>(MAX_CLASSES);
		urlToJar = jarFile.toURI().toURL();
		
		Log.i("Reading minecraft.jar...");
		if (!jarFile.exists())
			Log.crash("Attempted to load jar file at: " + jarFile + " but it does not exist.");
		Stack<ByteClass> byteClassStack = new Stack<ByteClass>();
		try {
			ZipFile jar = new ZipFile(jarFile);
			Enumeration<? extends ZipEntry> enu = jar.entries();
			
			while (enu.hasMoreElements()) {
				ZipEntry entry = enu.nextElement();
				String currentEntry = entry.getName();
				String[] nameSplit = currentEntry.split("\\.");
				if (!entry.isDirectory() && (nameSplit.length == 2) && (nameSplit[0].indexOf('/') == -1) && nameSplit[1].equals("class")) {
					BufferedInputStream is = new BufferedInputStream(jar.getInputStream(entry));
					if (is.available() < 8000) { // TODO: Double check that this filter won't mess anything up.
						byte[] classData = new byte[is.available()];
						is.read(classData);
						is.close();
						byteClassStack.push(new ByteClass(nameSplit[0], classData));
					}
				}
			}
			jar.close();
			Log.i("Jar load complete.");
		} catch (Exception e) {
			e.printStackTrace();
			Log.crash(e, "Error extracting jar data.");
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
							Log.debug("Found: " + byteClasses[i] + " as " + classChecks[q].getName() + " | " + classChecks[q].getClass().getSimpleName());
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
		
		Log.i("Generating version ID...");
		use();
		try {
			use();
			if (classLoader.findResource("net/minecraft/client/Minecraft.class") != null)
				mainClass = classLoader.loadClass("net.minecraft.client.Minecraft");
			else if (classLoader.findResource("net/minecraft/server/MinecraftServer.class") != null)
				mainClass = classLoader.loadClass("net.minecraft.server.MinecraftServer");
			else
				throw new RuntimeException();
		} catch (Exception e) {
			e.printStackTrace(); // TODO: Make this exception far less broad.
			Log.crash(e, "Attempted to load non-minecraft jar, or unable to locate starting point.");
		}
		String typeDump = "";
		Field fields[] = null;
		try {
			fields = mainClass.getDeclaredFields();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			Log.crash(e, "Unable to find critical external class while loading.\nPlease ensure you have the correct Minecraft libraries installed.");
		}
		
		for (int i = 0; i < fields.length; i++) {
			String typeString = fields[i].getType().toString();
			if (typeString.startsWith("class ") && !typeString.contains("."))
				typeDump += typeString.substring(6);
		}
		versionId = typeDump;
		for (VersionInfo v : VersionInfo.values()) {
			if (versionId.equals(v.versionId)) {
				version = v;
				break;
			}
		}

		Log.i("Identified Minecraft [" + version.name() + "] with versionID of " + versionId);
		Log.i("Loading classes...");
		nameMap = new HashMap<String, MinecraftClass>();
		classMap = new HashMap<String, MinecraftClass>();

		for (String name : byteClassNames) {
			ByteClass byteClass = byteClassMap.get(name);
			MinecraftClass minecraftClass = new MinecraftClass(name, byteClass.getClassName());
			minecraftClass.load(this);
			nameMap.put(minecraftClass.getName(), minecraftClass);
			classMap.put(minecraftClass.getClassName(), minecraftClass);
		}
		
		for (MinecraftClass minecraftClass : nameMap.values()) {
			ByteClass byteClass = byteClassMap.get(minecraftClass.getName());
			for (String[] property : byteClass.getProperties())
				minecraftClass.addProperty(new MinecraftProperty(minecraftClass, property[1], property[0]));
			for (String[] method : byteClass.getMethods()) {
				String methodString = obfuscateStringClasses(method[0]);
				methodString = methodString.replaceAll(",INVALID", "").replaceAll("INVALID,","").replaceAll("INVALID", "");
				String methodDeobfName = method[1];
				String methodObfName = methodString.substring(0, methodString.indexOf('('));
				String parameterString = methodString.substring(methodString.indexOf('(') + 1, methodString.indexOf(')'));
				
				if (parameterString.equals("")) {
					minecraftClass.addMethod(new MinecraftMethod(minecraftClass, methodDeobfName, methodObfName));
				} else {
					String[] parameterClasses = parameterString.split(",");
					minecraftClass.addMethod(new MinecraftMethod(minecraftClass, methodDeobfName, methodObfName, parameterClasses));
				}
			}
			for (String[] constructor : byteClass.getConstructors()) {
				String methodString = obfuscateStringClasses(constructor[0]).replaceAll(",INVALID", "").replaceAll("INVALID,","").replaceAll("INVALID", "");
				String methodDeobfName = constructor[1];
				String methodObfName = methodString.substring(0, methodString.indexOf('('));
				String parameterString = methodString.substring(methodString.indexOf('(') + 1, methodString.indexOf(')'));
				
				if (parameterString.equals("")) {
					minecraftClass.addMethod(new MinecraftMethod(minecraftClass, methodDeobfName, methodObfName));
				} else {
					String[] parameterClasses = parameterString.split(",");
					minecraftClass.addMethod(new MinecraftMethod(minecraftClass, methodDeobfName, methodObfName, parameterClasses));
				}
			}
		}
		Log.i("Classes loaded.");
		Log.i("Minecraft load complete.");
	}
	private String obfuscateStringClasses(String inString) {
		inString = inString.replaceAll(" ", "");
		Pattern cPattern = Pattern.compile("@[A-Za-z]+");
		Matcher cMatcher = cPattern.matcher(inString);
		String tempOutput = inString;
		while (cMatcher.find()) {
			String match = inString.substring(cMatcher.start(), cMatcher.end());
			ByteClass byteClass = getByteClass(match.substring(1));
			if (byteClass != null) {
				tempOutput = tempOutput.replaceAll(match, byteClass.getClassName());
			} else {
				tempOutput = tempOutput.replaceAll(match, "INVALID");
			}
			cMatcher = cPattern.matcher(tempOutput);
		}
		return tempOutput;
	}
	
	
	public URL getPath() {
		return urlToJar;
	}
	
	private Stack<URL> getLibraries(File jsonFile) {
		Log.i("Loading libraries.");
		Stack<URL> libraries = new Stack<URL>();
		JarProfile profile = null;
		try {
			profile = Util.readObject(jsonFile, JarProfile.class);
		} catch (IOException e) {
			Log.w("Invalid jar profile loaded. Library loading will be skipped. (Path: " + jsonFile + ")");
			return libraries;
		}
		
		for (int i = 0; i < profile.libraries.size(); i++) {
			JarLibrary library = profile.libraries.get(i);
			if (library.isActive() && library.getFile().exists()) {
				try {
					libraries.add(library.getFile().toURI().toURL());
					Log.i("Found library: " + library.getFile());
				} catch (MalformedURLException e) {
					Log.w("Unable to convert library file to URL with path: " + library.getFile());
					e.printStackTrace();
				}
			} else {
				Log.i("Skipping library: " + library.name);
			}
		}
		
		return libraries;
	}
	
	/* 
	 * This was the old search-and-add-all libraries method. This may still be useful
	 * if the user doesn't have a json file, or mojang changes the format.
	 * 
	private Stack<URL> getLibraries(File path, Stack<URL> urls) {
		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				getLibraries(files[i], urls);
			} else {
				try {
					Log.i("Found library: " + files[i]);
					urls.push(files[i].toURI().toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		return urls;
	}
	*/
	
	public void use() {
		File librariesJson = new File(jarFile.getPath().replace(".jar", ".json"));
		if (librariesJson.exists()) {
			Stack<URL> libraries = getLibraries(librariesJson);
			URL[] libraryArray = new URL[libraries.size() + 1];
			libraries.toArray(libraryArray);
			libraryArray[libraries.size()] = urlToJar;
			classLoader = new URLClassLoader(libraryArray);
		} else {
			Log.i("Unable to find Minecraft library JSON at: " + librariesJson + ". Skipping.");
			classLoader = new URLClassLoader(new URL[] { urlToJar });
		}
		Thread.currentThread().setContextClassLoader(classLoader);
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
			Log.crash(e, "Error loading a class (" + name + ")");
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
	}
	public ByteClass getByteClass(String name) {
		return byteClassMap.get(name);
	}
	
	public IMinecraftInterface createInterface() {
		return new LocalMinecraftInterface(this);
	}
	
}
