package amidst.mojangapi.minecraftinterface.local;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.Map;

import amidst.clazz.Classes;
import amidst.clazz.real.RealClass.AccessFlags;
import amidst.clazz.symbolic.SymbolicClass;
import amidst.clazz.translator.ClassTranslator;
import amidst.logging.Log;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.minecraftinterface.IMinecraftInterface;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

public class LocalMinecraftInterfaceBuilder {
	public static enum StatelessResources {
		INSTANCE;

		public ClassTranslator classTranslator = createClassTranslator();

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
		private ClassTranslator createClassTranslator() {
			return ClassTranslator
				.builder()
					.ifDetect()
						.wildcardBytes(createIntCacheWildcardBytes())
						.or()
						.strings(", tcache: ")
					.thenDeclare("IntCache")
						.method("getIntCache", 		"a").real("int").end()
						.method("resetIntCache", 	"a").end()
						.method("getInformation", 	"b").end()
						.field("intCacheSize", 		"a")
						.field("freeSmallArrays", 	"b")
						.field("inUseSmallArrays", 	"c")
						.field("freeLargeArrays", 	"d")
						.field("inUseLargeArrays", 	"e")
				.next()
					.ifDetect()
						.strings("default_1_1")
					.thenDeclare("WorldType")
						.field("types", 			"a")
						.field("default", 			"b")
						.field("flat", 				"c")
						.field("largeBiomes",	 	"d")
						.field("amplified", 		"e")
						.field("customized", 		"f")
						.field("default_1_1", 		"g")
				.next()
					.ifDetect()
						.longs(1000L, 2001L, 2000L)
					.thenDeclare("GenLayer")
						.method("initializeAllBiomeGenerators", 			"a").real("long").symbolic("WorldType").end()
						.method("initializeAllBiomeGeneratorsWithParams", 	"a").real("long").symbolic("WorldType").real("String").end()
						.method("getInts", 									"a").real("int") .real("int")          .real("int")   .real("int").end()
				.next()
					.ifDetect()
						.numberOfConstructors(0)
						.numberOfMethods(6)
						.numberOfFields(3)
						.fieldFlags(AccessFlags.PRIVATE | AccessFlags.STATIC, 0, 1, 2)
						.utf8s("isDebugEnabled")
						.or()
						.numberOfConstructors(0)
						.numberOfMethods(6)
						.numberOfFields(3)
						.fieldFlags(AccessFlags.PUBLIC | AccessFlags.STATIC, 0)
						.fieldFlags(AccessFlags.PRIVATE | AccessFlags.STATIC, 1, 2)
						.utf8s("isDebugEnabled")
					.thenDeclare("BlockInit")
						.method("initialize", "c").end()
				.construct();
		}
		// @formatter:on
	}

	private static final String CLIENT_CLASS_RESOURCE = "net/minecraft/client/Minecraft.class";
	private static final String CLIENT_CLASS = "net.minecraft.client.Minecraft";
	private static final String SERVER_CLASS_RESOURCE = "net/minecraft/server/MinecraftServer.class";
	private static final String SERVER_CLASS = "net.minecraft.server.MinecraftServer";

	private Map<String, SymbolicClass> symbolicClassMap;
	private RecognisedVersion recognisedVersion;

	public LocalMinecraftInterfaceBuilder(VersionDirectory versionDirectory) {
		try {
			URLClassLoader classLoader = versionDirectory.createClassLoader();
			recognisedVersion = getRecognisedVersion(classLoader);
			symbolicClassMap = Classes.createSymbolicClassMap(
					versionDirectory.getJar(), classLoader,
					StatelessResources.INSTANCE.classTranslator);
			Log.i("Minecraft load complete.");
		} catch (RuntimeException e) {
			Log.crash(
					e.getCause(),
					"error while building local minecraft interface: "
							+ e.getMessage());
			e.printStackTrace();
		} catch (MalformedURLException e) {
			Log.crash(
					e,
					"error while building local minecraft interface: minecraft jar file has malformed url");
			e.printStackTrace();
		}
	}

	private RecognisedVersion getRecognisedVersion(URLClassLoader classLoader) {
		Log.i("Generating version ID...");
		String magicString = generateMagicString(getMainClassFields(loadMainClass(classLoader)));
		RecognisedVersion result = RecognisedVersion.from(magicString);
		Log.i("Identified Minecraft [" + result.getName()
				+ "] with magic string of " + magicString);
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

	public IMinecraftInterface create() {
		return new LocalMinecraftInterface(symbolicClassMap, recognisedVersion);
	}
}
