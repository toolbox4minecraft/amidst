package amidst.mojangapi.minecraftinterface.local;

import amidst.clazz.translator.ClassTranslator;

public enum DefaultClassTranslator {
	INSTANCE;

	private final ClassTranslator classTranslator = createClassTranslator();

	public static ClassTranslator get() {
		return INSTANCE.classTranslator;
	}

	// @formatter:off
	private ClassTranslator createClassTranslator() {
		throw new UnsupportedOperationException("NotImplemented");
	}
	// @formatter:on
}
