package amidst.mojangapi.world.testworld;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.testworld.file.TestWorldDirectory;

@ThreadSafe
public class TestWorld {
	private final World world;
	private final TestWorldDirectory directory;

	public TestWorld(World world, TestWorldDirectory directory) {
		this.world = world;
		this.directory = directory;
	}

	public World getWorld() {
		return world;
	}

	public TestWorldDirectory getDirectory() {
		return directory;
	}

	public <T> T getEntryValue(String name, Class<T> clazz) {
		return directory.getEntryValue(name, clazz);
	}

	public <T> T extractFromWorld(String name, Class<T> clazz) {
		return directory.extractFromWorld(name, clazz, world);
	}

	public boolean isDirectoryAndWorldEntryEqual(String name) {
		return directory.isDirectoryAndWorldEntryEqual(name, world);
	}
}
