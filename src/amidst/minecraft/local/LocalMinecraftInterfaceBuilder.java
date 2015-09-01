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
import java.util.Map.Entry;
import java.util.regex.Matcher;

import amidst.Options;
import amidst.Util;
import amidst.byteclass.ByteClass;
import amidst.byteclass.ByteClasses;
import amidst.byteclass.finder.ByteClassFinder;
import amidst.json.JarLibrary;
import amidst.json.JarProfile;
import amidst.logging.Log;
import amidst.minecraft.IMinecraftInterface;
import amidst.utilties.FileSystemUtils;
import amidst.utilties.JavaUtils;
import amidst.version.VersionInfo;

public class LocalMinecraftInterfaceBuilder {
	private class MinecraftMethodAndConstructorBuilder {
		private String minecraftName;
		private String byteName;
		private String[] byteParameterNames;

		private MinecraftMethodAndConstructorBuilder(
				String minecraftMethodString, String minecraftName) {
			this.minecraftName = minecraftName;
			String byteString = replaceMinecraftClassNamesWithByteClassNames(minecraftMethodString);
			this.byteName = byteString.substring(0, byteString.indexOf('('));
			String byteParameterString = byteString.substring(
					byteString.indexOf('(') + 1, byteString.indexOf(')'));
			if (byteParameterString.isEmpty()) {
				this.byteParameterNames = new String[0];
			} else {
				this.byteParameterNames = byteParameterString.split(",");
			}
		}

		private MinecraftMethod createMinecraftMethod(
				MinecraftClass minecraftClass) {
			return new MinecraftMethod(
					StatelessResources.INSTANCE.getPrimitivesMap(),
					minecraftName, byteName, byteParameterNames);
		}

		private MinecraftConstructor createMinecraftConstructor(
				MinecraftClass minecraftClass) {
			return new MinecraftConstructor(
					StatelessResources.INSTANCE.getPrimitivesMap(),
					minecraftClass, minecraftName, byteParameterNames);
		}

		private String replaceMinecraftClassNamesWithByteClassNames(
				String inString) {
			return doReplaceMinecraftClassNamesWithByteClassNames(
					inString.replaceAll(" ", "")).replaceAll(",INVALID", "")
					.replaceAll("INVALID,", "").replaceAll("INVALID", "");
		}

		private String doReplaceMinecraftClassNamesWithByteClassNames(
				String result) {
			Matcher matcher = StatelessResources.INSTANCE.getClassNameRegex()
					.matcher(result);
			while (matcher.find()) {
				String match = result.substring(matcher.start(), matcher.end());
				result = replaceWithByteClassName(result, match);
				matcher = StatelessResources.INSTANCE.getClassNameRegex()
						.matcher(result);
			}
			return result;
		}

		private String replaceWithByteClassName(String result, String match) {
			String minecraftClassName = match.substring(1);
			ByteClass byteClass = minecraftClassNameToByteClassMap
					.get(minecraftClassName);
			if (byteClass != null) {
				result = result.replaceAll(match, byteClass.getByteClassName());
			} else {
				result = result.replaceAll(match, "INVALID");
			}
			return result;
		}
	}

	private URLClassLoader classLoader;

	private File jarFile;
	private VersionInfo version;

	private Map<String, ByteClass> minecraftClassNameToByteClassMap = new HashMap<String, ByteClass>();
	private Map<String, MinecraftClass> minecraftClassNameToMinecraftClassMap = new HashMap<String, MinecraftClass>();
	private Map<String, MinecraftClass> byteClassNameToMinecraftClassMap = new HashMap<String, MinecraftClass>();

	public LocalMinecraftInterfaceBuilder(File jarFile) {
		try {
			this.jarFile = jarFile;
			Log.i("Reading minecraft.jar...");
			List<ByteClass> byteClasses = ByteClasses.fromJarFile(jarFile);
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
			String versionID = generateVersionID(getMainClassFields(loadMainClass()));
			version = VersionInfo.from(versionID);
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

	private void identifyClasses(List<ByteClass> byteClasses) {
		for (ByteClassFinder finder : StatelessResources.INSTANCE
				.getByteClassFinders()) {
			ByteClass byteClass = finder.find(byteClasses);
			if (byteClass != null) {
				registerClass(finder.getMinecraftClassName(), byteClass);
				Log.debug("Found: " + byteClass.getByteClassName() + " as "
						+ finder.getMinecraftClassName());
			} else {
				Log.debug("Missing: " + finder.getMinecraftClassName());
			}
		}
	}

	private void registerClass(String minecraftClassName, ByteClass byteClass) {
		if (!minecraftClassNameToByteClassMap.containsKey(minecraftClassName)) {
			minecraftClassNameToByteClassMap.put(minecraftClassName, byteClass);
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
			addConstructors(minecraftClass, byteClass.getConstructors());
		}
	}

	private void addProperties(MinecraftClass minecraftClass,
			List<String[]> properties) {
		for (String[] property : properties) {
			minecraftClass.addProperty(new MinecraftProperty(minecraftClass,
					property[1], property[0]));
		}
	}

	private void addMethods(MinecraftClass minecraftClass,
			List<String[]> methods) {
		for (String[] method : methods) {
			minecraftClass
					.addMethod(new MinecraftMethodAndConstructorBuilder(
							method[0], method[1])
							.createMinecraftMethod(minecraftClass));
		}
	}

	private void addConstructors(MinecraftClass minecraftClass,
			List<String[]> constructors) {
		for (String[] constructor : constructors) {
			minecraftClass
					.addConstructor(new MinecraftMethodAndConstructorBuilder(
							constructor[0], constructor[1])
							.createMinecraftConstructor(minecraftClass));
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

	public Class<?> loadClass(String byteClassName) {
		try {
			return classLoader.loadClass(byteClassName);
		} catch (ClassNotFoundException e) {
			Log.crash(e, "Error loading a class (" + byteClassName + ")");
			e.printStackTrace();
		}
		return null;
	}

	public MinecraftClass getMinecraftClassByByteClassName(String byteClassName) {
		return byteClassNameToMinecraftClassMap.get(byteClassName);
	}

	public IMinecraftInterface create() {
		return new LocalMinecraftInterface(
				minecraftClassNameToMinecraftClassMap, version);
	}
}
