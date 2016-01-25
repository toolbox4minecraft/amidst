package amidst.mojangapi.world.testdatastorage;

import java.util.Map;

import amidst.documentation.Immutable;

@Immutable
public class WorldTestDataZipFileDeclaration {
	public static WorldTestDataZipFileDeclarationBuilder builder() {
		return new WorldTestDataZipFileDeclarationBuilder();
	}

	private final Map<String, WorldTestDataZipFileEntryDeclaration<?>> entries;

	public WorldTestDataZipFileDeclaration(
			Map<String, WorldTestDataZipFileEntryDeclaration<?>> entries) {
		this.entries = entries;
	}

	public Iterable<WorldTestDataZipFileEntryDeclaration<?>> getDeclarations() {
		return entries.values();
	}

	@SuppressWarnings("unchecked")
	public <T> WorldTestDataZipFileEntryDeclaration<T> get(String name,
			Class<T> clazz) {
		WorldTestDataZipFileEntryDeclaration<?> entry = entries.get(name);
		if (entry != null) {
			if (clazz.isAssignableFrom(entry.getClazz())) {
				return (WorldTestDataZipFileEntryDeclaration<T>) entry;
			} else {
				throw new ClassCastException(clazz + " is not assignable from "
						+ entry.getClazz());
			}
		} else {
			throw new IllegalArgumentException("unknown entry: " + name);
		}
	}

	public boolean containsEntry(String name) {
		return entries.containsKey(name);
	}

	public Class<?> getClazz(String name) {
		WorldTestDataZipFileEntryDeclaration<?> entry = entries.get(name);
		if (entry != null) {
			return entry.getClazz();
		} else {
			throw new IllegalArgumentException("unknown entry: " + name);
		}
	}
}
