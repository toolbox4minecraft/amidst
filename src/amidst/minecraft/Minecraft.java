package amidst.minecraft;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import amidst.Options;
import amidst.Util;
import amidst.bytedata.ByteClass;
import amidst.bytedata.ByteClass.ByteClassFactory;
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

	private Map<String, ByteClass> byteClassMap = new HashMap<String, ByteClass>(
			MAX_CLASSES);
	private Map<String, MinecraftClass> nameMap = new HashMap<String, MinecraftClass>();
	private Map<String, MinecraftClass> classMap = new HashMap<String, MinecraftClass>();
	private Vector<String> byteClassNames = new Vector<String>();

	public String versionId;
	public VersionInfo version = VersionInfo.unknown;

	private ByteClassFactory byteClassFactory = ByteClass.factory();
	private ByteClass[] byteClasses;

	public Minecraft(File jarFile) throws MalformedURLException {
		this.jarFile = jarFile;
		this.urlToJar = jarFile.toURI().toURL();
		readByteClassesFromJarFile();
		searchClasses();
		generateVersionID();
		loadClasses();
		Log.i("Minecraft load complete.");
	}

	private void readByteClassesFromJarFile() {
		Log.i("Reading minecraft.jar...");
		if (!jarFile.exists()) {
			Log.crash("Attempted to load jar file at: " + jarFile
					+ " but it does not exist.");
		}
		try {
			ZipFile jar = new ZipFile(jarFile);
			byteClasses = readJarFile(jar);
			jar.close();
			Log.i("Jar load complete.");
		} catch (Exception e) {
			e.printStackTrace();
			Log.crash(e, "Error extracting jar data.");
		}
	}

	private ByteClass[] readJarFile(ZipFile jar) throws IOException {
		Enumeration<? extends ZipEntry> enu = jar.entries();
		List<ByteClass> byteClassList = new ArrayList<ByteClass>();
		while (enu.hasMoreElements()) {
			ByteClass entry = readJarFileEntry(jar, enu.nextElement());
			if (entry != null) {
				byteClassList.add(entry);
			}
		}
		return byteClassList.toArray(new ByteClass[byteClassList.size()]);
	}

	private ByteClass readJarFileEntry(ZipFile jar, ZipEntry entry)
			throws IOException {
		String className = getClassName(entry);
		if (className != null) {
			BufferedInputStream is = new BufferedInputStream(
					jar.getInputStream(entry));
			// TODO: Double check that this filter won't mess anything up.
			if (is.available() < 8000) {
				byte[] classData = new byte[is.available()];
				is.read(classData);
				is.close();
				return byteClassFactory.create(className, classData);
			}
		}
		return null;
	}

	private String getClassName(ZipEntry entry) {
		String[] nameSplit = entry.getName().split("\\.");
		if (!entry.isDirectory() && nameSplit.length == 2
				&& nameSplit[0].indexOf('/') == -1
				&& nameSplit[1].equals("class")) {
			return nameSplit[0];
		} else {
			return null;
		}
	}

	private void searchClasses() {
		Log.i("Searching for classes...");
		ClassChecker[] classCheckers = createClassCheckers();
		int checksRemaining = classCheckers.length;
		boolean[] found = new boolean[byteClasses.length];
		while (checksRemaining != 0) {
			for (int q = 0; q < classCheckers.length; q++) {
				for (int i = 0; i < byteClasses.length; i++) {
					if (!found[q]) {
						classCheckers[q].check(this, byteClasses[i]);
						if (classCheckers[q].isComplete()) {
							Log.debug("Found: "
									+ byteClasses[i]
									+ " as "
									+ classCheckers[q].getName()
									+ " | "
									+ classCheckers[q].getClass()
											.getSimpleName());
							found[q] = true;
							checksRemaining--;
						}
						// TODO: What is this line, and why is it commented
						// byteClassMap.put(classChecks[q].getName(),
						// classFiles[i].getName().split("\\.")[0]);
					}
				}
				if (!found[q]) {
					classCheckers[q].decreasePasses();
					if (classCheckers[q].getPasses() == 0) {
						found[q] = true;
						checksRemaining--;
					}
				}

			}
		}
		Log.i("Class search complete.");
	}

	// @formatter:off
	// This deactivates the automatic formatter of Eclipse.
	// However, you need to activate this in:
	// Java -> Code Style -> Formatter -> Edit -> Off/On Tags
	// see: http://stackoverflow.com/questions/1820908/how-to-turn-off-the-eclipse-code-formatter-for-certain-sections-of-java-code
	private ClassChecker[] createClassCheckers() {
		return ClassChecker.builder()
			.matchWildcardBytes("IntCache").data(DeobfuscationData.intCache).end()
			.matchString("WorldType").data("default_1_1").end()
			.matchLong("GenLayer").data(1000L, 2001L, 2000L).end()
			.matchString("IntCache").data(", tcache: ").end()
			.matchJustAnother("BlockInit").end()
			.require("WorldType")
				.matchProperties("WorldType")
					.property("a", "types")
					.property("b", "default")
					.property("c", "flat")
					.property("d", "largeBiomes")
					.property("e", "amplified")
					.property("g", "default_1_1")
					.property("f", "customized")
				.end()
			.end()
			.require("BlockInit")
				.matchMethods("BlockInit")
					.method("c()", "initialize")
				.end()
			.end()
			.require("GenLayer")
				.matchMethods("GenLayer")
					.method("a(long, @WorldType)", "initializeAllBiomeGenerators")
					.method("a(long, @WorldType, String)", "initializeAllBiomeGeneratorsWithParams")
					.method("a(int, int, int, int)", "getInts")
				.end()
			.end()
			.require("IntCache")
				.multi()
					.matchMethods("IntCache")
						.method("a(int)", "getIntCache")
						.method("a()", "resetIntCache")
						.method("b()", "getInformation")
					.end()
					.matchProperties("IntCache")
						.property("a", "intCacheSize")
						.property("b","freeSmallArrays")
						.property("c","inUseSmallArrays")
						.property("d","freeLargeArrays")
						.property("e","inUseLargeArrays")
					.end()
				.end()
			.end()
		.construct();
	}
	// @formatter:on

	private void generateVersionID() {
		Log.i("Generating version ID...");
		use();
		try {
			use();
			if (classLoader
					.findResource("net/minecraft/client/Minecraft.class") != null)
				mainClass = classLoader
						.loadClass("net.minecraft.client.Minecraft");
			else if (classLoader
					.findResource("net/minecraft/server/MinecraftServer.class") != null)
				mainClass = classLoader
						.loadClass("net.minecraft.server.MinecraftServer");
			else
				throw new RuntimeException();
		} catch (Exception e) {
			e.printStackTrace(); // TODO: Make this exception far less broad.
			Log.crash(e,
					"Attempted to load non-minecraft jar, or unable to locate starting point.");
		}
		String typeDump = "";
		Field fields[] = null;
		try {
			fields = mainClass.getDeclaredFields();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			Log.crash(
					e,
					"Unable to find critical external class while loading.\nPlease ensure you have the correct Minecraft libraries installed.");
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
		Log.i("Identified Minecraft [" + version.name()
				+ "] with versionID of " + versionId);
	}

	private void loadClasses() {
		Log.i("Loading classes...");

		for (String name : byteClassNames) {
			ByteClass byteClass = byteClassMap.get(name);
			MinecraftClass minecraftClass = new MinecraftClass(name,
					byteClass.getClassName());
			minecraftClass.load(this);
			nameMap.put(minecraftClass.getName(), minecraftClass);
			classMap.put(minecraftClass.getClassName(), minecraftClass);
		}

		for (MinecraftClass minecraftClass : nameMap.values()) {
			ByteClass byteClass = byteClassMap.get(minecraftClass.getName());
			for (String[] property : byteClass.getProperties())
				minecraftClass.addProperty(new MinecraftProperty(
						minecraftClass, property[1], property[0]));
			for (String[] method : byteClass.getMethods()) {
				String methodString = obfuscateStringClasses(method[0]);
				methodString = methodString.replaceAll(",INVALID", "")
						.replaceAll("INVALID,", "").replaceAll("INVALID", "");
				String methodDeobfName = method[1];
				String methodObfName = methodString.substring(0,
						methodString.indexOf('('));
				String parameterString = methodString.substring(
						methodString.indexOf('(') + 1,
						methodString.indexOf(')'));

				if (parameterString.equals("")) {
					minecraftClass.addMethod(new MinecraftMethod(
							minecraftClass, methodDeobfName, methodObfName));
				} else {
					String[] parameterClasses = parameterString.split(",");
					minecraftClass.addMethod(new MinecraftMethod(
							minecraftClass, methodDeobfName, methodObfName,
							parameterClasses));
				}
			}
			for (String[] constructor : byteClass.getConstructors()) {
				String methodString = obfuscateStringClasses(constructor[0])
						.replaceAll(",INVALID", "").replaceAll("INVALID,", "")
						.replaceAll("INVALID", "");
				String methodDeobfName = constructor[1];
				String methodObfName = methodString.substring(0,
						methodString.indexOf('('));
				String parameterString = methodString.substring(
						methodString.indexOf('(') + 1,
						methodString.indexOf(')'));

				if (parameterString.equals("")) {
					minecraftClass.addMethod(new MinecraftMethod(
							minecraftClass, methodDeobfName, methodObfName));
				} else {
					String[] parameterClasses = parameterString.split(",");
					minecraftClass.addMethod(new MinecraftMethod(
							minecraftClass, methodDeobfName, methodObfName,
							parameterClasses));
				}
			}
		}
		Log.i("Classes loaded.");
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
				tempOutput = tempOutput.replaceAll(match,
						byteClass.getClassName());
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
			Log.w("Invalid jar profile loaded. Library loading will be skipped. (Path: "
					+ jsonFile + ")");
			return libraries;
		}

		for (JarLibrary library : profile.getLibraries()) {
			File libraryFile = library.getFile();
			if (libraryFile != null) {
				try {
					libraries.add(libraryFile.toURI().toURL());
					Log.i("Found library: " + libraryFile);
				} catch (MalformedURLException e) {
					Log.w("Unable to convert library file to URL with path: "
							+ libraryFile);
					e.printStackTrace();
				}
			} else {
				Log.i("Skipping library: " + library.getName());
			}
		}

		return libraries;
	}

	/*-
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
		File librariesJson = Options.instance.minecraftJson == null ? new File(
				jarFile.getPath().replace(".jar", ".json")) : new File(
				Options.instance.minecraftJson);
		if (librariesJson.exists()) {
			Stack<URL> libraries = getLibraries(librariesJson);
			URL[] libraryArray = new URL[libraries.size() + 1];
			libraries.toArray(libraryArray);
			libraryArray[libraries.size()] = urlToJar;
			classLoader = new URLClassLoader(libraryArray);
		} else {
			Log.i("Unable to find Minecraft library JSON at: " + librariesJson
					+ ". Skipping.");
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
		if (byteClassMap.get(publicName) == null) {
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
