package amidst.mojangapi.world.testdatastorage;

import java.util.Map;
import java.util.function.BiFunction;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.World;

@Immutable
public class WorldTestDataZipFileEntryDeclaration<T> {
	private final String name;
	private final Class<T> clazz;
	private final BiFunction<TestWorldDeclaration, World, T> worldExtractor;

	public WorldTestDataZipFileEntryDeclaration(String name, Class<T> clazz,
			BiFunction<TestWorldDeclaration, World, T> worldExtractor) {
		this.name = name;
		this.clazz = clazz;
		this.worldExtractor = worldExtractor;
	}

	public String getName() {
		return name;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public BiFunction<TestWorldDeclaration, World, T> getWorldExtractor() {
		return worldExtractor;
	}

	@SuppressWarnings("unchecked")
	public T extractFrom(Map<String, Object> data) {
		Object object = data.get(name);
		if (object != null) {
			if (clazz.isAssignableFrom(object.getClass())) {
				return (T) object;
			} else {
				throw new ClassCastException(clazz + " is not assignable from "
						+ object.getClass());
			}
		} else {
			throw new IllegalArgumentException(
					"data does not contain the key: " + name);
		}
	}
}
