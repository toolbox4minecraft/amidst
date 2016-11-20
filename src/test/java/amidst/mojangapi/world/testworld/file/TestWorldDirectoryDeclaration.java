package amidst.mojangapi.world.testworld.file;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.testworld.TestWorldDeclaration;
import amidst.mojangapi.world.testworld.TestWorldEntryNames;
import amidst.mojangapi.world.testworld.file.builder.TestWorldDirectoryDeclarationBuilder;
import amidst.mojangapi.world.testworld.storage.json.BiomeDataJson;

@Immutable
public class TestWorldDirectoryDeclaration {
	public static TestWorldDirectoryDeclarationBuilder builder() {
		return new TestWorldDirectoryDeclarationBuilder();
	}

	private final Map<String, TestWorldEntryDeclaration<?>> entries;

	public TestWorldDirectoryDeclaration(Map<String, TestWorldEntryDeclaration<?>> entries) {
		this.entries = entries;
	}

	public Iterable<TestWorldEntryDeclaration<?>> getEntryDeclarations() {
		return entries.values();
	}

	public boolean containsEntry(String name) {
		return entries.containsKey(name);
	}

	public <T> TestWorldEntryDeclaration<T> getEntryDeclaration(String name, Class<T> clazz) {
		TestWorldEntryDeclaration<?> entry = entries.get(name);
		if (entry != null) {
			return entry.getAsEntryWithClazz(clazz);
		} else {
			throw new IllegalArgumentException("unknown entry: " + name);
		}
	}

	public TestWorldDirectory create(
			TestWorldDeclaration worldDeclaration,
			World world,
			BiomeDataJson quarterBiomeData,
			BiomeDataJson fullBiomeData) {
		Map<String, Object> data = new HashMap<>();
		for (Entry<String, TestWorldEntryDeclaration<?>> entry : entries.entrySet()) {
			String name = entry.getKey();
			if (name.equals(TestWorldEntryNames.QUARTER_RESOLUTION_BIOME_DATA)) {
				data.put(name, quarterBiomeData);
			} else if (name.equals(TestWorldEntryNames.FULL_RESOLUTION_BIOME_DATA)) {
				data.put(name, fullBiomeData);
			} else if (worldDeclaration.isSupported(name)) {
				TestWorldEntryDeclaration<?> entryDeclaration = entry.getValue();
				data.put(name, entryDeclaration.extractFromWorld(world));
			}
		}
		return new TestWorldDirectory(this, data);
	}

	public boolean isDataMapAndWorldEntryEqual(String name, Map<String, Object> data, World world) {
		return entries.get(name).isDataMapAndWorldEntryEqual(data, world);
	}
}
