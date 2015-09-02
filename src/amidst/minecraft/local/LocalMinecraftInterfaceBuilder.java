package amidst.minecraft.local;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import amidst.Options;
import amidst.Util;
import amidst.clazz.real.RealClass.AccessFlags;
import amidst.clazz.real.finder.RealClassFinder;
import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.symbolic.SymbolicClasses;
import amidst.json.JarLibrary;
import amidst.json.JarProfile;
import amidst.logging.Log;
import amidst.minecraft.IMinecraftInterface;
import amidst.utilties.FileSystemUtils;
import amidst.utilties.JavaUtils;
import amidst.version.VersionInfo;

public class LocalMinecraftInterfaceBuilder {
	private static enum StatelessResources {
		INSTANCE;

		private List<RealClassFinder> realClassFinders = createRealClassFinders();

		private int[] createIntCacheWildcardBytes() {
			return new int[] { 0x11, 0x01, 0x00, 0xB3, 0x00, -1, 0xBB, 0x00,
					-1, 0x59, 0xB7, 0x00, -1, 0xB3, 0x00, -1, 0xBB, 0x00, -1,
					0x59, 0xB7, 0x00, -1, 0xB3, 0x00, -1, 0xBB, 0x00, -1, 0x59,
					0xB7, 0x00, -1, 0xB3, 0x00, -1, 0xBB, 0x00, -1, 0x59, 0xB7,
					0x00, -1, 0xB3, 0x00, -1, 0xB1 };
		}

		// @formatter:off
		// This deactivates the automatic formatter of Eclipse.
		// However, you need to activate this in:
		// Java -> Code Style -> Formatter -> Edit -> Off/On Tags
		// see: http://stackoverflow.com/questions/1820908/how-to-turn-off-the-eclipse-code-formatter-for-certain-sections-of-java-code
		private List<RealClassFinder> createRealClassFinders() {
			return RealClassFinder.builder()
				.name("IntCache")
					.detect()
						.wildcardBytes(createIntCacheWildcardBytes())
						.or()
						.strings(", tcache: ")
					.prepare()
						.addMethod("getIntCache", 			"a").real("int").end()
						.addMethod("resetIntCache", 		"a").end()
						.addMethod("getInformation", 		"b").end()
						.addProperty("intCacheSize", 		"a")
						.addProperty("freeSmallArrays", 	"b")
						.addProperty("inUseSmallArrays", 	"c")
						.addProperty("freeLargeArrays", 	"d")
						.addProperty("inUseLargeArrays", 	"e")
				.next()
				.name("WorldType")
					.detect()
						.strings("default_1_1")
					.prepare()
						.addProperty("types", 			"a")
						.addProperty("default", 		"b")
						.addProperty("flat", 			"c")
						.addProperty("largeBiomes", 	"d")
						.addProperty("amplified", 		"e")
						.addProperty("customized", 		"f")
						.addProperty("default_1_1", 	"g")
				.next()
				.name("GenLayer")
					.detect()
						.longs(1000L, 2001L, 2000L)
					.prepare()
						.addMethod("initializeAllBiomeGenerators", 				"a").real("long").symbolic("WorldType").end()
						.addMethod("initializeAllBiomeGeneratorsWithParams", 	"a").real("long").symbolic("WorldType").real("String").end()
						.addMethod("getInts", 									"a").real("int") .real("int")          .real("int")   .real("int").end()
				.next()
				.name("BlockInit")
					.detect()
						.numberOfFields(3)
						.fieldFlags(AccessFlags.PRIVATE | AccessFlags.STATIC, 0, 1, 2)
						.numberOfConstructors(0)
						.numberOfMethodsAndConstructors(6)
						.utf8s("isDebugEnabled")
					.prepare()
						.addMethod("initialize", "c").end()
				.construct();
		}
		// @formatter:on
	}

	private static final String CLIENT_CLASS_RESOURCE = "net/minecraft/client/Minecraft.class";
	private static final String CLIENT_CLASS = "net.minecraft.client.Minecraft";
	private static final String SERVER_CLASS_RESOURCE = "net/minecraft/server/MinecraftServer.class";
	private static final String SERVER_CLASS = "net.minecraft.server.MinecraftServer";

	private Map<String, SymbolicClass> symbolicClassesBySymbolicClassName;
	private VersionInfo version;

	public LocalMinecraftInterfaceBuilder(File jarFile) {
		try {
			URLClassLoader classLoader = getClassLoader(jarFile);
			version = getVersion(classLoader);
			symbolicClassesBySymbolicClassName = SymbolicClasses.loadClasses(
					jarFile, classLoader,
					StatelessResources.INSTANCE.realClassFinders);
			Log.i("Minecraft load complete.");
		} catch (RuntimeException e) {
			Log.crash(
					e.getCause(),
					"error while building local minecraft interface: "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	private URLClassLoader getClassLoader(File jarFile) {
		File librariesJson = getLibrariesJsonFile(jarFile);
		URLClassLoader classLoader;
		if (librariesJson.exists()) {
			Log.i("Loading libraries.");
			classLoader = createClassLoader(getJarFileUrl(jarFile),
					getAllLibraryUrls(librariesJson));
		} else {
			Log.i("Unable to find Minecraft library JSON at: " + librariesJson
					+ ". Skipping.");
			classLoader = createClassLoader(getJarFileUrl(jarFile));
		}
		return classLoader;
	}

	private VersionInfo getVersion(URLClassLoader classLoader) {
		Log.i("Generating version ID...");
		String versionID = generateVersionID(getMainClassFields(loadMainClass(classLoader)));
		VersionInfo result = VersionInfo.from(versionID);
		Log.i("Identified Minecraft [" + result.name() + "] with versionID of "
				+ versionID);
		return result;
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

	private Class<?> loadMainClass(URLClassLoader classLoader) {
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

	private File getLibrariesJsonFile(File jarFile) {
		if (Options.instance.minecraftJson != null) {
			return new File(Options.instance.minecraftJson);
		} else {
			return new File(jarFile.getPath().replace(".jar", ".json"));
		}
	}

	private URL getJarFileUrl(File jarFile) {
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

	public IMinecraftInterface create() {
		return new LocalMinecraftInterface(symbolicClassesBySymbolicClassName,
				version);
	}
}
