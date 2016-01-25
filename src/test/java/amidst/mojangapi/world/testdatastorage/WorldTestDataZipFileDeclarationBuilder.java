package amidst.mojangapi.world.testdatastorage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.World;

@NotThreadSafe
public class WorldTestDataZipFileDeclarationBuilder {
	private final Map<String, WorldTestDataZipFileEntryDeclaration<?>> entries = new HashMap<String, WorldTestDataZipFileEntryDeclaration<?>>();
	private final WorldTestDataZipFileDeclaration product = new WorldTestDataZipFileDeclaration(
			Collections.unmodifiableMap(entries));

	public WorldTestDataZipFileDeclaration get() {
		return product;
	}

	public <T> WorldTestDataZipFileDeclarationBuilder entry(String name,
			Class<T> clazz,
			BiFunction<TestWorldDeclaration, World, T> worldExtractor) {
		entries.put(name, new WorldTestDataZipFileEntryDeclaration<T>(name,
				clazz, worldExtractor));
		return this;
	}
}
