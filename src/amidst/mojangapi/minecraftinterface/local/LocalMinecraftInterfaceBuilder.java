package amidst.mojangapi.minecraftinterface.local;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Map;

import amidst.clazz.Classes;
import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.translator.ClassTranslator;
import amidst.documentation.Immutable;
import amidst.logging.Log;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@Immutable
public class LocalMinecraftInterfaceBuilder {
	private static final String CLIENT_CLASS_RESOURCE = "net/minecraft/client/Minecraft.class";
	private static final String CLIENT_CLASS = "net.minecraft.client.Minecraft";
	private static final String SERVER_CLASS_RESOURCE = "net/minecraft/server/MinecraftServer.class";
	private static final String SERVER_CLASS = "net.minecraft.server.MinecraftServer";

	private final ClassTranslator translator;

	public LocalMinecraftInterfaceBuilder(ClassTranslator translator) {
		this.translator = translator;
	}

	public MinecraftInterface create(VersionDirectory versionDirectory)
			throws LocalMinecraftInterfaceCreationException {
		try {
			URLClassLoader classLoader = versionDirectory.createClassLoader();
			RecognisedVersion recognisedVersion = getRecognisedVersion(classLoader);
			Map<String, SymbolicClass> symbolicClassMap = Classes
					.createSymbolicClassMap(versionDirectory.getJar(),
							classLoader, translator);
			Log.i("Minecraft load complete.");
			return new LocalMinecraftInterface(
					symbolicClassMap.get(SymbolicNames.CLASS_INT_CACHE),
					symbolicClassMap.get(SymbolicNames.CLASS_BLOCK_INIT),
					symbolicClassMap.get(SymbolicNames.CLASS_GEN_LAYER),
					symbolicClassMap.get(SymbolicNames.CLASS_WORLD_TYPE),
					recognisedVersion);
		} catch (Exception e) {
			throw new LocalMinecraftInterfaceCreationException(
					"unable to create local minecraft interface", e);
		}
	}

	private RecognisedVersion getRecognisedVersion(URLClassLoader classLoader)
			throws LocalMinecraftInterfaceCreationException {
		Log.i("Generating version ID...");
		String magicString = generateMagicString(getMainClassFields(loadMainClass(classLoader)));
		RecognisedVersion result = RecognisedVersion.from(magicString);
		Log.i("Recognised Minecraft Version " + result.getName()
				+ " with magic string of \"" + magicString + "\"");
		return result;
	}

	private Field[] getMainClassFields(Class<?> mainClass)
			throws LocalMinecraftInterfaceCreationException {
		try {
			return mainClass.getDeclaredFields();
		} catch (NoClassDefFoundError e) {
			throw new LocalMinecraftInterfaceCreationException(
					"Unable to find critical external class while loading.\n"
							+ "Please ensure you have the correct Minecraft libraries installed.",
					e);
		}
	}

	private String generateMagicString(Field[] fields) {
		String result = "";
		for (Field field : fields) {
			String typeString = field.getType().toString();
			if (typeString.startsWith("class ") && !typeString.contains(".")) {
				result += typeString.substring(6);
			}
		}
		return result;
	}

	private Class<?> loadMainClass(URLClassLoader classLoader)
			throws LocalMinecraftInterfaceCreationException {
		try {
			if (classLoader.findResource(CLIENT_CLASS_RESOURCE) != null) {
				return classLoader.loadClass(CLIENT_CLASS);
			} else if (classLoader.findResource(SERVER_CLASS_RESOURCE) != null) {
				return classLoader.loadClass(SERVER_CLASS);
			} else {
				throw new LocalMinecraftInterfaceCreationException(
						"Attempted to load non-minecraft jar, or unable to locate main class.");
			}
		} catch (ClassNotFoundException e) {
			throw new LocalMinecraftInterfaceCreationException(
					"Attempted to load non-minecraft jar, or unable to locate main class.",
					e);
		}
	}
}
