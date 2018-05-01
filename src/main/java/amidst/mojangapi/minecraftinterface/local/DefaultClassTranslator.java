package amidst.mojangapi.minecraftinterface.local;

import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.*;

import amidst.clazz.real.AccessFlags;
import amidst.clazz.translator.ClassTranslator;

public enum DefaultClassTranslator {
	INSTANCE;
	
	private final ClassTranslator classTranslator = createClassTranslator();

	public ClassTranslator get() {
		return classTranslator;
	}
	
	// @formatter:off
		private ClassTranslator createClassTranslator() {
			return ClassTranslator
				.builder()
					.ifDetect(c -> c.searchForStringContaining("default_1_1"))
					.thenDeclareOptional(CLASS_WORLD_TYPE)
						.requiredField(FIELD_WORLD_TYPE_DEFAULT,      "b")
						.requiredField(FIELD_WORLD_TYPE_FLAT,         "c")
						.requiredField(FIELD_WORLD_TYPE_LARGE_BIOMES, "d")
						.requiredField(FIELD_WORLD_TYPE_AMPLIFIED,    "e")
						.requiredField(FIELD_WORLD_TYPE_CUSTOMIZED,   "f")
				.next()
					.ifDetect(c -> 
						c.getNumberOfConstructors() == 0
						&& c.getNumberOfMethods() == 6
						&& c.getNumberOfFields() >= 3
						&& c.getNumberOfFields() <= 4
						&& c.getField(0).hasFlags(AccessFlags.STATIC)
						&& c.getField(1).hasFlags(AccessFlags.PRIVATE | AccessFlags.STATIC)
						&& c.searchForUtf8EqualTo("isDebugEnabled")
					)
					.thenDeclareOptional(CLASS_BLOCK_INIT)
						.requiredMethod(METHOD_BLOCK_INIT_INITIALIZE, "c").end()
				.construct();
		}
		// @formatter:on
}
