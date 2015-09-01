package amidst.minecraft.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amidst.byteclass.ByteClass.AccessFlags;
import amidst.byteclass.finder.ByteClassFinder;

public enum StatelessResources {
	INSTANCE;

	private static final String CLIENT_CLASS_RESOURCE = "net/minecraft/client/Minecraft.class";
	private static final String CLIENT_CLASS = "net.minecraft.client.Minecraft";
	private static final String SERVER_CLASS_RESOURCE = "net/minecraft/server/MinecraftServer.class";
	private static final String SERVER_CLASS = "net.minecraft.server.MinecraftServer";

	private Map<String, Class<?>> primitivesMap = createPrimitivesMap();
	private List<ByteClassFinder> byteClassFinders = createByteClassFinders();

	private int[] createIntCacheWildcardBytes() {
		return new int[] { 0x11, 0x01, 0x00, 0xB3, 0x00, -1, 0xBB, 0x00, -1,
				0x59, 0xB7, 0x00, -1, 0xB3, 0x00, -1, 0xBB, 0x00, -1, 0x59,
				0xB7, 0x00, -1, 0xB3, 0x00, -1, 0xBB, 0x00, -1, 0x59, 0xB7,
				0x00, -1, 0xB3, 0x00, -1, 0xBB, 0x00, -1, 0x59, 0xB7, 0x00, -1,
				0xB3, 0x00, -1, 0xB1 };
	}

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
					.wildcardBytes(createIntCacheWildcardBytes())
					.or()
					.strings(", tcache: ")
				.prepare()
					.addMethod("getIntCache", 			"a").i("int").end()
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
					.addMethod("initializeAllBiomeGenerators", 				"a")	.i("long")	.e("WorldType")	.end()
					.addMethod("initializeAllBiomeGeneratorsWithParams", 	"a")	.i("long")	.e("WorldType")	.i("String").end()
					.addMethod("getInts", 									"a")	.i("int")	.i("int")		.i("int")	.i("int").end()
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

	public Map<String, Class<?>> getPrimitivesMap() {
		return primitivesMap;
	}

	public List<ByteClassFinder> getByteClassFinders() {
		return byteClassFinders;
	}
}
