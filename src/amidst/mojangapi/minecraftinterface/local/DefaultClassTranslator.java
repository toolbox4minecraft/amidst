package amidst.mojangapi.minecraftinterface.local;

import amidst.clazz.real.AccessFlags;
import amidst.clazz.translator.ClassTranslator;

public enum DefaultClassTranslator {
	INSTANCE;

	public ClassTranslator get() {
		return classTranslator;
	}

	private final ClassTranslator classTranslator = createClassTranslator();

	// @formatter:off
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

	private int[] createIntCacheWildcardBytes() {
		return new int[] { 0x11, 0x01, 0x00, 0xB3, 0x00, -1, 0xBB, 0x00, -1,
				0x59, 0xB7, 0x00, -1, 0xB3, 0x00, -1, 0xBB, 0x00, -1, 0x59,
				0xB7, 0x00, -1, 0xB3, 0x00, -1, 0xBB, 0x00, -1, 0x59, 0xB7,
				0x00, -1, 0xB3, 0x00, -1, 0xBB, 0x00, -1, 0x59, 0xB7, 0x00, -1,
				0xB3, 0x00, -1, 0xB1 };
	}
}
