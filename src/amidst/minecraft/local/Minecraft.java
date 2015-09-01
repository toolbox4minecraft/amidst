package amidst.minecraft.local;

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
import amidst.byteclass.ByteClass;
import amidst.byteclass.ByteClass.AccessFlags;
import amidst.byteclass.ByteClass.ByteClassFactory;
import amidst.byteclass.finder.ByteClassFinder;
import amidst.json.JarLibrary;
import amidst.json.JarProfile;
import amidst.logging.Log;
import amidst.minecraft.IMinecraftInterface;
import amidst.utilties.FileSystemUtils;
import amidst.utilties.JavaUtils;
import amidst.version.VersionInfo;

public class Minecraft {
	private static final int[] INT_CACHE_WILDCARD_BYTES = new int[] { 0x11,
			0x01, 0x00, 0xB3, 0x00, -1, 0xBB, 0x00, -1, 0x59, 0xB7, 0x00, -1,
			0xB3, 0x00, -1, 0xBB, 0x00, -1, 0x59, 0xB7, 0x00, -1, 0xB3, 0x00,
			-1, 0xBB, 0x00, -1, 0x59, 0xB7, 0x00, -1, 0xB3, 0x00, -1, 0xBB,
			0x00, -1, 0x59, 0xB7, 0x00, -1, 0xB3, 0x00, -1, 0xB1 };

	private static final String CLIENT_CLASS_RESOURCE = "net/minecraft/client/Minecraft.class";
	private static final String CLIENT_CLASS = "net.minecraft.client.Minecraft";
	private static final String SERVER_CLASS_RESOURCE = "net/minecraft/server/MinecraftServer.class";
	private static final String SERVER_CLASS = "net.minecraft.server.MinecraftServer";

	private ByteClassFactory byteClassFactory = ByteClass.factory();
	private Pattern classNameRegex = Pattern.compile("@[A-Za-z]+");

	private URLClassLoader classLoader;

	private File jarFile;
	private String versionID;
	private VersionInfo version;

	private Map<String, ByteClass> minecraftClassNameToByteClassMap = new HashMap<String, ByteClass>();
	private Map<String, MinecraftClass> minecraftClassNameToMinecraftClassMap = new HashMap<String, MinecraftClass>();
	private Map<String, MinecraftClass> byteClassNameToMinecraftClassMap = new HashMap<String, MinecraftClass>();

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
						getAllLibraryUrls(librariesJson));
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
			Log.crash(e.getCause(), "error while loading minecraft jar file: "
					+ e.getMessage());
			e.printStackTrace();
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
		String byteClassName = FileSystemUtils.getFileNameWithoutExtension(
				entry.getName(), "class");
		if (!entry.isDirectory() && byteClassName != null) {
			BufferedInputStream is = new BufferedInputStream(
					jar.getInputStream(entry));
			// TODO: Double check that this filter won't mess anything up.
			if (is.available() < 8000) {
				byte[] classData = new byte[is.available()];
				is.read(classData);
				is.close();
				return byteClassFactory.create(byteClassName, classData);
			}
		}
		return null;
	}

	private void identifyClasses(List<ByteClass> byteClasses) {
		for (ByteClassFinder finder : createByteClassFinders()) {
			ByteClass byteClass = findClass(finder, byteClasses);
			if (byteClass != null) {
				Log.debug("Found: " + byteClass.getByteClassName() + " as "
						+ finder.getMinecraftClassName());
			} else {
				Log.debug("Missing: " + finder.getMinecraftClassName());
			}
		}
	}

	private ByteClass findClass(ByteClassFinder finder,
			List<ByteClass> byteClasses) {
		for (ByteClass byteClass : byteClasses) {
			if (finder.find(this, byteClass)) {
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
	private List<ByteClassFinder> createByteClassFinders() {
		return ByteClassFinder.builder()
			.name("IntCache")
				.detect()
					.wildcardBytes(INT_CACHE_WILDCARD_BYTES)
					.or()
					.strings(", tcache: ")
				.prepare()
					.addMethod("a(int)", "getIntCache")
					.addMethod("a()", "resetIntCache")
					.addMethod("b()", "getInformation")
					.addProperty("a", "intCacheSize")
					.addProperty("b","freeSmallArrays")
					.addProperty("c","inUseSmallArrays")
					.addProperty("d","freeLargeArrays")
					.addProperty("e","inUseLargeArrays")
			.next()
			.name("WorldType")
				.detect()
					.strings("default_1_1")
				.prepare()
					.addProperty("a", "types")
					.addProperty("b", "default")
					.addProperty("c", "flat")
					.addProperty("d", "largeBiomes")
					.addProperty("e", "amplified")
					.addProperty("g", "default_1_1")
					.addProperty("f", "customized")
			.next()
			.name("GenLayer")
				.detect()
					.longs(1000L, 2001L, 2000L)
				.prepare()
					.addMethod("a(long, @WorldType)", "initializeAllBiomeGenerators")
					.addMethod("a(long, @WorldType, String)", "initializeAllBiomeGeneratorsWithParams")
					.addMethod("a(int, int, int, int)", "getInts")
			.next()
			.name("BlockInit")
				.detect()
					.numberOfFields(3)
					.fieldFlags(AccessFlags.PRIVATE | AccessFlags.STATIC, 0, 1, 2)
					.numberOfConstructors(0)
					.numberOfMethodsAndConstructors(6)
					.utf8s("isDebugEnabled")
				.prepare()
					.addMethod("c()", "initialize")
			.construct();
	}
	// @formatter:on

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
		for (Entry<String, ByteClass> entry : minecraftClassNameToByteClassMap
				.entrySet()) {
			String minecraftClassName = entry.getKey();
			ByteClass byteClass = entry.getValue();
			String byteClassName = byteClass.getByteClassName();
			MinecraftClass minecraftClass = new MinecraftClass(
					minecraftClassName, byteClassName, this);
			minecraftClassNameToMinecraftClassMap.put(minecraftClassName,
					minecraftClass);
			byteClassNameToMinecraftClassMap.put(byteClassName, minecraftClass);
		}
	}

	private void addPropertiesMethodsConstructors() {
		for (Entry<String, ByteClass> entry : minecraftClassNameToByteClassMap
				.entrySet()) {
			ByteClass byteClass = entry.getValue();
			MinecraftClass minecraftClass = minecraftClassNameToMinecraftClassMap
					.get(entry.getKey());
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

	private void addMethods(MinecraftClass minecraftClass, List<String[]> list) {
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
		Matcher matcher = classNameRegex.matcher(result);
		while (matcher.find()) {
			String match = result.substring(matcher.start(), matcher.end());
			result = replaceWithByteClassName(result, match);
			matcher = classNameRegex.matcher(result);
		}
		return result;
	}

	private String replaceWithByteClassName(String result, String match) {
		ByteClass byteClass = getByteClass(match.substring(1));
		if (byteClass != null) {
			result = result.replaceAll(match, byteClass.getByteClassName());
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

	private List<URL> getAllLibraryUrls(File jsonFile) {
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

	public Class<?> loadClass(String byteClassName) {
		try {
			return classLoader.loadClass(byteClassName);
		} catch (ClassNotFoundException e) {
			Log.crash(e, "Error loading a class (" + byteClassName + ")");
			e.printStackTrace();
		}
		return null;
	}

	public void registerClass(String minecraftClassName, ByteClass byteClass) {
		if (!minecraftClassNameToByteClassMap.containsKey(minecraftClassName)) {
			minecraftClassNameToByteClassMap.put(minecraftClassName, byteClass);
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

	public ByteClass getByteClass(String minecraftClassName) {
		return minecraftClassNameToByteClassMap.get(minecraftClassName);
	}

	public MinecraftClass getMinecraftClassByMinecraftClassName(
			String minecraftClassName) {
		return minecraftClassNameToMinecraftClassMap.get(minecraftClassName);
	}

	public MinecraftClass getMinecraftClassByByteClassName(String byteClassName) {
		return byteClassNameToMinecraftClassMap.get(byteClassName);
	}

	public IMinecraftInterface createInterface() {
		return new LocalMinecraftInterface(this);
	}
}
