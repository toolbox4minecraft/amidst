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

	private Class<?> mainClass;
	private URLClassLoader classLoader;
	private String versionID;
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

	public Minecraft(File jarFile) {
		this.jarFile = jarFile;
		readByteClassesFromJarFile();
		identifyClasses();
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

	private void identifyClasses() {
		Log.i("Searching for classes...");
		ClassChecker[] classCheckers = createClassCheckers();
		for (int q = 0; q < classCheckers.length; q++) {
			ClassChecker classChecker = classCheckers[q];
			ByteClass byteClass = findClass(classChecker);
			if (byteClass != null) {
				Log.debug("Found: " + byteClass + " as "
						+ classChecker.getName() + " | "
						+ classChecker.getClass().getSimpleName());
			} else {
				Log.w("Missing: " + classChecker.getName() + " | "
						+ classChecker.getClass().getSimpleName());
			}
		}
		Log.i("Class search complete.");
	}

	private ByteClass findClass(ClassChecker classChecker) {
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

	private void generateVersionID() {
		Log.i("Generating version ID...");
		try {
			createAndUseClassLoader();
			if (classLoader.findResource(CLIENT_CLASS_RESOURCE) != null) {
				mainClass = classLoader.loadClass(CLIENT_CLASS);
			} else if (classLoader.findResource(SERVER_CLASS_RESOURCE) != null) {
				mainClass = classLoader.loadClass(SERVER_CLASS);
			} else {
				throw new RuntimeException("cannot find minecraft jar file");
			}
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

	public void createAndUseClassLoader() throws MalformedURLException {
		File librariesJson = getLibrariesJsonFile();
		URL[] urls;
		if (librariesJson.exists()) {
			List<URL> libraries = getLibraries(librariesJson);
			libraries.add(jarFile.toURI().toURL());
			urls = Utils.toArray(libraries, URL.class);
		} else {
			Log.i("Unable to find Minecraft library JSON at: " + librariesJson
					+ ". Skipping.");
			urls = new URL[] { jarFile.toURI().toURL() };
		}
		classLoader = new URLClassLoader(urls);
		Thread.currentThread().setContextClassLoader(classLoader);
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
