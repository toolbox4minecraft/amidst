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
import amidst.utilties.JavaUtils;
import amidst.version.VersionInfo;

public class Minecraft {
	private static final String CLIENT_CLASS_RESOURCE = "net/minecraft/client/Minecraft.class";
	private static final String CLIENT_CLASS = "net.minecraft.client.Minecraft";
	private static final String SERVER_CLASS_RESOURCE = "net/minecraft/server/MinecraftServer.class";
	private static final String SERVER_CLASS = "net.minecraft.server.MinecraftServer";

	private static final int MAX_CLASSES = 128;

	private ByteClassFactory byteClassFactory = ByteClass.factory();
	private Pattern classNamePattern = Pattern.compile("@[A-Za-z]+");

	private URLClassLoader classLoader;

	private File jarFile;
	private String versionID;
	private VersionInfo version;

	private Map<String, ByteClass> nameToByteClassMap = new HashMap<String, ByteClass>(
			MAX_CLASSES);
	private Map<String, MinecraftClass> nameToMinecraftClassMap = new HashMap<String, MinecraftClass>();
	private Map<String, MinecraftClass> typeToMinecraftClassMap = new HashMap<String, MinecraftClass>();

	public Minecraft(File jarFile) {
		try {
			this.jarFile = jarFile;
			Log.i("Reading minecraft.jar...");
			List<ByteClass> byteClasses = readByteClassesFromJarFile();
			Log.i("Jar load complete.");
			Log.i("Searching for classes...");
			identifyClasses(byteClasses);
			Log.i("Class search complete.");
			File librariesJson = getLibrariesJsonFile();
			if (librariesJson.exists()) {
				Log.i("Loading libraries.");
				classLoader = createClassLoader(getJarFileUrl(),
						getLibraries(librariesJson));
			} else {
				Log.i("Unable to find Minecraft library JSON at: "
						+ librariesJson + ". Skipping.");
				classLoader = createClassLoader(getJarFileUrl());
			}
			Log.i("Generating version ID...");
			versionID = generateVersionID(getMainClassFields(loadMainClass()));
			version = findMatchingVersion();
			Log.i("Identified Minecraft [" + version.name()
					+ "] with versionID of " + versionID);
			Log.i("Loading classes...");
			populateMinecraftClassMaps();
			addPropertiesMethodsConstructors();
			Log.i("Classes loaded.");
			Log.i("Minecraft load complete.");
		} catch (RuntimeException e) {
			e.printStackTrace();
			Log.crash(e.getCause(), "error while loading minecraft jar file: "
					+ e.getMessage());
		}
	}

	private List<ByteClass> readByteClassesFromJarFile() {
		if (!jarFile.exists()) {
			throw new RuntimeException("Attempted to load jar file at: "
					+ jarFile + " but it does not exist.");
		}
		try {
			ZipFile jar = new ZipFile(jarFile);
			List<ByteClass> byteClasses = readJarFile(jar);
			jar.close();
			return byteClasses;
		} catch (IOException e) {
			throw new RuntimeException("Error extracting jar data.", e);
		}
	}

	private List<ByteClass> readJarFile(ZipFile jar) throws IOException {
		Enumeration<? extends ZipEntry> enu = jar.entries();
		List<ByteClass> byteClassList = new ArrayList<ByteClass>();
		while (enu.hasMoreElements()) {
			ByteClass entry = readJarFileEntry(jar, enu.nextElement());
			if (entry != null) {
				byteClassList.add(entry);
			}
		}
		return byteClassList;
	}

	private ByteClass readJarFileEntry(ZipFile jar, ZipEntry entry)
			throws IOException {
		String className = FileSystemUtils.getFileNameWithoutExtension(
				entry.getName(), "class");
		if (!entry.isDirectory() && className != null) {
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

	private void identifyClasses(List<ByteClass> byteClasses) {
		for (ClassChecker classChecker : createClassCheckers()) {
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
	}

	private ByteClass findClass(ClassChecker classChecker,
			List<ByteClass> byteClasses) {
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
	private List<ClassChecker> createClassCheckers() {
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
		return VersionInfo.unknown;
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
			List<String[]> list) {
		for (String[] property : list) {
			minecraftClass.addProperty(new MinecraftProperty(minecraftClass,
					property[1], property[0]));
		}
	}

	private void addMethods(MinecraftClass minecraftClass,
			List<String[]> list) {
		for (String[] method : list) {
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

	private File getLibrariesJsonFile() {
		if (Options.instance.minecraftJson != null) {
			return new File(Options.instance.minecraftJson);
		} else {
			return new File(jarFile.getPath().replace(".jar", ".json"));
		}
	}

	private URL getJarFileUrl() {
		try {
			return jarFile.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("minecraft jar file has malformed url",
					e);
		}
	}
	
	private URLClassLoader createClassLoader(URL jarFileUrl, List<URL> libraries) {
		libraries.add(jarFileUrl);
		return new URLClassLoader(JavaUtils.toArray(libraries, URL.class));
	}
	
	private URLClassLoader createClassLoader(URL jarFileUrl) {
		return new URLClassLoader(new URL[] { jarFileUrl });
	}
	
	private List<URL> getLibraries(File jsonFile) {
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
			File libraryFile = getLibraryFile(library);
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

	private File getLibraryFile(JarLibrary library) {
		if (library.isActive()) {
			File result = getLibraryFile(library.getName());
			if (result != null && result.exists()) {
				return result;
			}
		}
		return null;
	}

	private File getLibraryFile(String libraryName) {
		String searchPath = getLibrarySearchPath(libraryName);
		File searchPathFile = new File(searchPath);
		if (!searchPathFile.exists()) {
			Log.w("Failed attempt to load library at: " + searchPathFile);
			return null;
		}
		File result = FileSystemUtils.getFirstFileWithExtension(
				searchPathFile.listFiles(), "jar");
		if (result == null) {
			Log.w("Attempted to search for file at path: " + searchPath
					+ " but found nothing. Skipping.");
		}
		return result;
	}

	private String getLibrarySearchPath(String libraryName) {
		String result = Util.minecraftLibraries.getAbsolutePath() + "/";
		String[] split = libraryName.split(":");
		split[0] = split[0].replace('.', '/');
		for (int i = 0; i < split.length; i++) {
			result += split[i] + "/";
		}
		return result;
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

	public void registerClass(String name, ByteClass bClass) {
		if (!nameToByteClassMap.containsKey(name)) {
			nameToByteClassMap.put(name, bClass);
		}
	}

	public URLClassLoader getClassLoader() {
		return classLoader;
	}

	public File getJarFile() {
		return jarFile;
	}

	public VersionInfo getVersion() {
		return version;
	}

	public String getVersionID() {
		return versionID;
	}

	public ByteClass getByteClass(String name) {
		return nameToByteClassMap.get(name);
	}

	public MinecraftClass getClassByName(String name) {
		return nameToMinecraftClassMap.get(name);
	}

	public MinecraftClass getClassByType(String name) {
		return typeToMinecraftClassMap.get(name);

	}

	public IMinecraftInterface createInterface() {
		return new LocalMinecraftInterface(this);
	}
}
