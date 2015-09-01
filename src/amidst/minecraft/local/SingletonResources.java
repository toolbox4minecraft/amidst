package amidst.minecraft.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import amidst.byteclass.ByteClass;
import amidst.byteclass.ByteClass.AccessFlags;
import amidst.byteclass.ByteClass.ByteClassFactory;
import amidst.byteclass.finder.ByteClassFinder;

public enum SingletonResources {
	INSTANCE;

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
	private Map<String, Class<?>> primitivesMap = createPrimitivesMap();
	private List<ByteClassFinder> byteClassFinders = createByteClassFinders();

	private Map<String, Class<?>> createPrimitivesMap() {
		Map<String, Class<?>> result = new HashMap<String, Class<?>>();
		result.put("byte", byte.class);
		result.put("int", int.class);
		result.put("float", float.class);
		result.put("short", short.class);
		result.put("long", long.class);
		result.put("double", double.class);
		result.put("boolean", boolean.class);
		result.put("char", char.class);
		result.put("String", String.class);
		return result;
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

	public int[] getIntCacheWildcardBytes() {
		return INT_CACHE_WILDCARD_BYTES;
	}

	public String getClientClassResource() {
		return CLIENT_CLASS_RESOURCE;
	}

	public String getClientClass() {
		return CLIENT_CLASS;
	}

	public String getServerClassResource() {
		return SERVER_CLASS_RESOURCE;
	}

	public String getServerClass() {
		return SERVER_CLASS;
	}

	public ByteClassFactory getByteClassFactory() {
		return byteClassFactory;
	}

	public Pattern getClassNameRegex() {
		return classNameRegex;
	}

	public Map<String, Class<?>> getPrimitivesMap() {
		return primitivesMap;
	}

	public List<ByteClassFinder> getByteClassFinders() {
		return byteClassFinders;
	}
}
