package amidst.mojangapi.minecraftinterface.local;

import amidst.clazz.translator.ClassTranslator;
import amidst.documentation.Immutable;

@Immutable
public enum MinecraftClassTranslator_EarlyBetas {
	INSTANCE;

	private final ClassTranslator classTranslator = createClassTranslator();

	public ClassTranslator get() {
		return classTranslator;
	}

	// @formatter:off
	
	// These hooks into the beta .jar files are very brittle, luckily the beta versions of
	// Minecraft are never going to change so it shouldn't matter.
	private ClassTranslator createClassTranslator() {
		return ClassTranslator
			.builder()
				.ifDetect()
					.ints(5169201)
				.thenDeclareRequired(SymbolicNames.CLASS_BETA_BIOME)
					.requiredFieldOfRealType(SymbolicNames.FIELD_BETA_BIOME_NAME, "String")
			.next()
				.ifDetect()
					.longs(9871L, 39811L, 543321L)
				.thenDeclareRequired(SymbolicNames.CLASS_BETA_BIOMEGENERATOR)
					.requiredConstructor(SymbolicNames.CLASS_BETA_BIOMEGENERATOR).symbolic(SymbolicNames.CLASS_BETA_WORLD).end()
					.requiredMethod(SymbolicNames.METHOD_BETA_BIOMEGENERATOR_GET_BIOME, "a").real("int").real("int").end()
					.requiredMethod(SymbolicNames.METHOD_BETA_BIOMEGENERATOR_GET_BIOMES_ARRAY,      "a").anyReferenceType().real("int").real("int").real("int").real("int").end()
					// We don't actually need GET_TEMPERATURE_ARRAY, I'm matching it only so that GET_BIOME_ARRAY will
					// match against the second method with that signature (a feature I've only implemented in requiredMethod()).
					// (The order matching is performed seems to reverse, so rather than investigate, I've just placed 
					// GET_TEMPERATURE_ARRAY after GET_BIOMES_ARRAY)
					.requiredMethod(SymbolicNames.METHOD_BETA_BIOMEGENERATOR_GET_TEMPERATURE_ARRAY, "a").anyReferenceType().real("int").real("int").real("int").real("int").end()
					
			.next()
				// We need the Dimension base class for building constructor declarations.
				.ifDetect()
					.floats(0.7529412f, 0.84705883f) 
				.thenDeclareRequired(SymbolicNames.CLASS_BETA_DIMENSION_ABSTRACT)
					
			.next()
				// In b1.6 the Dimension class became abstract, so search for The End subclass first,
				// because DimensionEnd is a concrete class we can find easily (DimensionOverworld is
				// empty so we have nothing to match it on). We will then instantiate the Overworld's
				// biome generator manually, so it won't matter which dimension the World instance thinks
				// it's in - the World instance is just a vessel for the seed.
				.ifDetect()
					.ints(8421536) // Matches DimensionEnd
					.or()
					.floats(0.7529412f, 0.84705883f) // else match the Dimension class (for versions earlier than 1.6)
				.thenDeclareRequired(SymbolicNames.CLASS_BETA_DIMENSION_CONCRETE)
					.requiredFieldOfSymbolicType(SymbolicNames.FIELD_BETA_DIMENSION_WORLD, SymbolicNames.CLASS_BETA_WORLD)
					.requiredFieldOfSymbolicType(SymbolicNames.FIELD_BETA_DIMENSION_BIOMEGENERATOR, SymbolicNames.CLASS_BETA_BIOMEGENERATOR)  // I'm currently not using this because as of 1.6 the matched class might be dimensionEnd instead of dimensionOverworld
				
			.next()
				.ifDetect()
					.ints(1013904223)
					//.or()
					//.utf8EqualTo("Saving level")
					//.utf8EqualTo("Saving chunks")
					//.or()
					//.fieldFlags(AccessFlags.PUBLIC, 0, 2, 6, 7)
					//.fieldFlags(AccessFlags.PRIVATE, 1, 3, 4, 5)
				.thenDeclareRequired(SymbolicNames.CLASS_BETA_WORLD)
					.optionalConstructor(SymbolicNames.CLASS_BETA_WORLD).real("String").symbolic(SymbolicNames.CLASS_BETA_DIMENSION_ABSTRACT).real("long").end()
					// version b1.3 introduced an ISaveHander param to the constructor, so match that form as well:
					.optionalConstructor(SymbolicNames.CLASS_BETA_WORLD).anyReferenceType().real("String").symbolic(SymbolicNames.CLASS_BETA_DIMENSION_ABSTRACT).real("long").end()
					
			.next()
				.ifDetect()
					.utf8EqualTo("RandomLevelSource")
					.or()
					.numberOfFields(23) // b1.0 lacks the handy classname string, so match it on other random stuff
					.longs(341873128712L, 132897987541L)
				.thenDeclareRequired(SymbolicNames.CLASS_BETA_CHUNKGENERATOR)
					.requiredConstructor(SymbolicNames.CLASS_BETA_CHUNKGENERATOR).anyReferenceType().real("long").end()
					.requiredMethod(SymbolicNames.METHOD_BETA_CHUNKGENERATOR_PREPARECHUNK, "a").real("int").real("int").anyReferenceType().anyReferenceType().anyReferenceType().end()
					.requiredField(SymbolicNames.FIELD_BETA_CHUNKGENERATOR_PRGN, "j")

										
			.construct();
	}
	// @formatter:on
}
