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
import java.util.Map.Entry;
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
import amidst.utilties.FileSystemUtils;
import amidst.utilties.Utils;
import amidst.version.VersionInfo;

public class Minecraft {
	private static final String CLIENT_CLASS_RESOURCE = "net/minecraft/client/Minecraft.class";
	private static final String CLIENT_CLASS = "net.minecraft.client.Minecraft";
	private static final String SERVER_CLASS_RESOURCE = "net/minecraft/server/MinecraftServer.class";
	private static final String SERVER_CLASS = "net.minecraft.server.MinecraftServer";

	private static final int MAX_CLASSES = 128;

	private ByteClassFactory byteClassFactory = ByteClass.factory();
	private Pattern classNamePattern = Pattern.compile("@[A-Za-z]+");

	private File jarFile;
	private URLClassLoader classLoader;

	private Map<String, ByteClass> nameToByteClassMap = new HashMap<String, ByteClass>(
			MAX_CLASSES);
	private Map<String, MinecraftClass> nameToMinecraftClassMap = new HashMap<String, MinecraftClass>();
	private Map<String, MinecraftClass> typeToMinecraftClassMap = new HashMap<String, MinecraftClass>();

	private String versionID;
	public VersionInfo version = VersionInfo.unknown;

	public Minecraft(File jarFile) {
		Log.i("Reading minecraft.jar...");
		try {
			this.jarFile = jarFile;
			identifyClasses(readByteClassesFromJarFile());
			classLoader = createAndUseClassLoader();
			generateVersion(getMainClassFields(loadMainClass()));
			loadClasses();
		} catch (RuntimeException e) {
			e.printStackTrace();
			Log.crash(e.getCause(), "error while loading minecraft jar file: "
					+ e.getMessage());
		}
		Log.i("Minecraft load complete.");
	}

	private ByteClass[] readByteClassesFromJarFile() {
		if (!jarFile.exists()) {
			throw new RuntimeException("Attempted to load jar file at: "
					+ jarFile + " but it does not exist.");
		}
		try {
			ZipFile jar = new ZipFile(jarFile);
			ByteClass[] byteClasses = readJarFile(jar);
			jar.close();
			Log.i("Jar load complete.");
			return byteClasses;
		} catch (IOException e) {
			throw new RuntimeException("Error extracting jar data.", e);
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
		String className = FileSystemUtils.getFileNameWithoutExtension(entry,
				"class");
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

	private void identifyClasses(ByteClass[] byteClasses) {
		Log.i("Searching for classes...");
		ClassChecker[] classCheckers = createClassCheckers();
		for (int q = 0; q < classCheckers.length; q++) {
			ClassChecker classChecker = classCheckers[q];
			ByteClass byteClass = findClass(classChecker, byteClasses);
			if (byteClass != null) {
				Log.debug("Found: " + byteClass + " as "
						+ classChecker.getName() + " | "
						+ classChecker.getClass().getSimpleName());
			} else {
				Log.debug("Missing: " + classChecker.getName() + " | "
						+ classChecker.getClass().getSimpleName());
			}
		}
		Log.i("Class search complete.");
	}

	private ByteClass findClass(ClassChecker classChecker,
			ByteClass[] byteClasses) {
		for (ByteClass byteClass : byteClasses) {
			classChecker.check(this, byteClass);
			if (classChecker.isComplete()) {
				return byteClass;
			}
		}
		return null;
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

	private void generateVersion(Field[] mainClassFields) {
		Log.i("Generating version ID...");
		versionID = generateVersionID(mainClassFields);
		version = findMatchingVersion();
		Log.i("Identified Minecraft [" + version.name()
				+ "] with versionID of " + versionID);
	}

	private Field[] getMainClassFields(Class<?> mainClass) {
		try {
			return mainClass.getDeclaredFields();
		} catch (NoClassDefFoundError e) {
			throw new RuntimeException(
					"Unable to find critical external class while loading.\nPlease ensure you have the correct Minecraft libraries installed.",
					e);
		}
	}

	private String generateVersionID(Field[] fields) {
		String result = "";
		for (Field field : fields) {
			String typeString = field.getType().toString();
			if (typeString.startsWith("class ") && !typeString.contains(".")) {
				result += typeString.substring(6);
			}
		}
		return result;
	}

	private VersionInfo findMatchingVersion() {
		for (VersionInfo versionInfo : VersionInfo.values()) {
			if (versionID.equals(versionInfo.versionID)) {
				return versionInfo;
			}
		}
		return null;
	}

	private Class<?> loadMainClass() {
		try {
			if (classLoader.findResource(CLIENT_CLASS_RESOURCE) != null) {
				return classLoader.loadClass(CLIENT_CLASS);
			} else if (classLoader.findResource(SERVER_CLASS_RESOURCE) != null) {
				return classLoader.loadClass(SERVER_CLASS);
			} else {
				throw new RuntimeException("cannot find minecraft jar file");
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Attempted to load non-minecraft jar, or unable to locate starting point.",
					e);
		}
	}

	private void loadClasses() {
		Log.i("Loading classes...");

		populateMinecraftClassMaps();
		addPropertiesMethodsConstructors();

		Log.i("Classes loaded.");
	}

	private void populateMinecraftClassMaps() {
		for (Entry<String, ByteClass> entry : nameToByteClassMap.entrySet()) {
			ByteClass byteClass = entry.getValue();
			String name = entry.getKey();
			String className = byteClass.getClassName();
			MinecraftClass minecraftClass = new MinecraftClass(name, className,
					this);
			nameToMinecraftClassMap.put(name, minecraftClass);
			typeToMinecraftClassMap.put(className, minecraftClass);
		}
	}

	private void addPropertiesMethodsConstructors() {
		for (Entry<String, ByteClass> entry : nameToByteClassMap.entrySet()) {
			ByteClass byteClass = entry.getValue();
			MinecraftClass minecraftClass = nameToMinecraftClassMap.get(entry
					.getKey());
			addProperties(minecraftClass, byteClass.getProperties());
			addMethods(minecraftClass, byteClass.getMethods());
			addMethods(minecraftClass, byteClass.getConstructors());
		}
	}

	private void addProperties(MinecraftClass minecraftClass,
			Vector<String[]> properties) {
		for (String[] property : properties) {
			minecraftClass.addProperty(new MinecraftProperty(minecraftClass,
					property[1], property[0]));
		}
	}

	private void addMethods(MinecraftClass minecraftClass,
			Vector<String[]> methods) {
		for (String[] method : methods) {
			String methodString = obfuscateStringClasses(method[0]);
			String methodDeobfName = method[1];
			String methodObfName = methodString.substring(0,
					methodString.indexOf('('));
			String parameterString = methodString.substring(
					methodString.indexOf('(') + 1, methodString.indexOf(')'));
			if (parameterString.isEmpty()) {
				minecraftClass.addMethod(new MinecraftMethod(minecraftClass,
						methodDeobfName, methodObfName));
			} else {
				String[] parameterClasses = parameterString.split(",");
				minecraftClass.addMethod(new MinecraftMethod(minecraftClass,
						methodDeobfName, methodObfName, parameterClasses));
			}
		}
	}

	private String obfuscateStringClasses(String inString) {
		return doObfuscateStringClasses(inString.replaceAll(" ", ""))
				.replaceAll(",INVALID", "").replaceAll("INVALID,", "")
				.replaceAll("INVALID", "");
	}

	private String doObfuscateStringClasses(String result) {
		Matcher matcher = classNamePattern.matcher(result);
		while (matcher.find()) {
			String match = result.substring(matcher.start(), matcher.end());
			result = replaceWithByteClassName(result, match);
			matcher = classNamePattern.matcher(result);
		}
		return result;
	}

	private String replaceWithByteClassName(String result, String match) {
		ByteClass byteClass = getByteClass(match.substring(1));
		if (byteClass != null) {
			result = result.replaceAll(match, byteClass.getClassName());
		} else {
			result = result.replaceAll(match, "INVALID");
		}
		return result;
	}

	private List<URL> getLibraries(File jsonFile) {
		Log.i("Loading libraries.");
		List<URL> libraries = new ArrayList<URL>();
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

	public URLClassLoader createAndUseClassLoader() {
		try {
			File librariesJson = getLibrariesJsonFile();
			URL[] urls;
			if (librariesJson.exists()) {
				List<URL> libraries = getLibraries(librariesJson);
				libraries.add(jarFile.toURI().toURL());
				urls = Utils.toArray(libraries, URL.class);
			} else {
				Log.i("Unable to find Minecraft library JSON at: "
						+ librariesJson + ". Skipping.");
				urls = new URL[] { jarFile.toURI().toURL() };
			}
			URLClassLoader classLoader = new URLClassLoader(urls);
			Thread.currentThread().setContextClassLoader(classLoader);
			return classLoader;
		} catch (MalformedURLException e) {
			throw new RuntimeException("minecraft jar file has malformed url",
					e);
		}
	}

	private File getLibrariesJsonFile() {
		if (Options.instance.minecraftJson != null) {
			return new File(Options.instance.minecraftJson);
		} else {
			return new File(jarFile.getPath().replace(".jar", ".json"));
		}
	}

	public String getVersionID() {
		return versionID;
	}

	public MinecraftClass getClassByName(String name) {
		return nameToMinecraftClassMap.get(name);
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
		return typeToMinecraftClassMap.get(name);

	}

	public void registerClass(String name, ByteClass bClass) {
		if (nameToByteClassMap.get(name) == null) {
			nameToByteClassMap.put(name, bClass);
		}
	}

	public ByteClass getByteClass(String name) {
		return nameToByteClassMap.get(name);
	}

	public IMinecraftInterface createInterface() {
		return new LocalMinecraftInterface(this);
	}
}
