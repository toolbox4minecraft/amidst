package amidst.minecraft.local;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amidst.Options;
import amidst.Util;
import amidst.clazz.real.RealClass;
import amidst.clazz.real.RealClasses;
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
	private URLClassLoader classLoader;

	private File jarFile;
	private VersionInfo version;

	private Map<String, RealClass> realClassesBySymbolicClassName = new HashMap<String, RealClass>();
	private Map<String, SymbolicClass> symbolicClassesBySymbolicClassName;

	public LocalMinecraftInterfaceBuilder(File jarFile) {
		try {
			this.jarFile = jarFile;
			Log.i("Reading minecraft.jar...");
			List<RealClass> realClasses = RealClasses.fromJarFile(jarFile);
			Log.i("Jar load complete.");
			Log.i("Searching for classes...");
			identifyClasses(realClasses);
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
			String versionID = generateVersionID(getMainClassFields(loadMainClass()));
			version = VersionInfo.from(versionID);
			Log.i("Identified Minecraft [" + version.name()
					+ "] with versionID of " + versionID);
			Log.i("Loading classes...");
			symbolicClassesBySymbolicClassName = SymbolicClasses
					.createClasses(classLoader, realClassesBySymbolicClassName);
			Log.i("Classes loaded.");
			Log.i("Minecraft load complete.");
		} catch (RuntimeException e) {
			Log.crash(
					e.getCause(),
					"error while building local minecraft interface: "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	private void identifyClasses(List<RealClass> realClasses) {
		for (RealClassFinder finder : StatelessResources.INSTANCE
				.getRealClassFinders()) {
			RealClass realClass = finder.find(realClasses);
			if (realClass != null) {
				registerClass(finder.getSymbolicClassName(), realClass);
				Log.debug("Found: " + realClass.getRealClassName() + " as "
						+ finder.getSymbolicClassName());
			} else {
				Log.debug("Missing: " + finder.getSymbolicClassName());
			}
		}
	}

	private void registerClass(String symbolicClassName, RealClass realClass) {
		if (!realClassesBySymbolicClassName.containsKey(symbolicClassName)) {
			realClassesBySymbolicClassName.put(symbolicClassName, realClass);
		}
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

	private Class<?> loadMainClass() {
		try {
			if (classLoader.findResource(StatelessResources.INSTANCE
					.getClientClassResource()) != null) {
				return classLoader.loadClass(StatelessResources.INSTANCE
						.getClientClass());
			} else if (classLoader.findResource(StatelessResources.INSTANCE
					.getServerClassResource()) != null) {
				return classLoader.loadClass(StatelessResources.INSTANCE
						.getServerClass());
			} else {
				throw new RuntimeException("cannot find minecraft jar file");
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Attempted to load non-minecraft jar, or unable to locate starting point.",
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

	public IMinecraftInterface create() {
		return new LocalMinecraftInterface(symbolicClassesBySymbolicClassName,
				version);
	}
}
