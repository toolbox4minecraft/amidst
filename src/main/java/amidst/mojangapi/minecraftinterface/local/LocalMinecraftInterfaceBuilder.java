package amidst.mojangapi.minecraftinterface.local;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Map;

import amidst.clazz.Classes;
import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.translator.ClassTranslator;
import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
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

	@NotNull
	public MinecraftInterface create(VersionDirectory versionDirectory)
			throws LocalMinecraftInterfaceCreationException {
		try {
			URLClassLoader classLoader = versionDirectory.createClassLoader();
			RecognisedVersion recognisedVersion = RecognisedVersion
					.from(getMainClassFields(classLoader));
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

	@NotNull
	private Field[] getMainClassFields(URLClassLoader classLoader)
			throws SecurityException, ClassNotFoundException {
		if (classLoader.findResource(CLIENT_CLASS_RESOURCE) != null) {
			return classLoader.loadClass(CLIENT_CLASS).getDeclaredFields();
		} else if (classLoader.findResource(SERVER_CLASS_RESOURCE) != null) {
			return classLoader.loadClass(SERVER_CLASS).getDeclaredFields();
		} else {
			throw new ClassNotFoundException(
					"unable to find the main class in the given jar file");
		}
	}
}
