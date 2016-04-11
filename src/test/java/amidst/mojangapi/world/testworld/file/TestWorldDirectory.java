package amidst.mojangapi.world.testworld.file;

import java.util.Map;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.World;

@ThreadSafe
public class TestWorldDirectory {
	private final TestWorldDirectoryDeclaration declaration;
	private final Map<String, Object> data;

	public TestWorldDirectory(TestWorldDirectoryDeclaration declaration, Map<String, Object> data) {
		this.declaration = declaration;
		this.data = data;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public <T> T getEntryValue(String name, Class<T> clazz) {
		return declaration.getEntryDeclaration(name, clazz).extractFromDataMap(data);
	}

	public <T> T extractFromWorld(String name, Class<T> clazz, World world) {
		return declaration.getEntryDeclaration(name, clazz).extractFromWorld(world);
	}

	public boolean isDirectoryAndWorldEntryEqual(String name, World world) {
		return declaration.isDataMapAndWorldEntryEqual(name, data, world);
	}
}
