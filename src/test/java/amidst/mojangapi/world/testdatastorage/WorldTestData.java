package amidst.mojangapi.world.testdatastorage;

import java.util.Map;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.World;

@ThreadSafe
public class WorldTestData {
	private final WorldTestDataZipFileDeclaration zipFileDeclaration;
	private final TestWorldDeclaration declaration;
	private final World world;
	private final Map<String, Object> data;

	public WorldTestData(WorldTestDataZipFileDeclaration zipFileDeclaration,
			TestWorldDeclaration declaration, World world,
			Map<String, Object> data) throws MinecraftInterfaceException {
		this.zipFileDeclaration = zipFileDeclaration;
		this.declaration = declaration;
		this.world = world;
		this.data = data;
	}

	public TestWorldDeclaration getDeclaration() {
		return declaration;
	}

	public World getWorld() {
		return world;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public <T> T getTestData(String name, Class<T> clazz) {
		return zipFileDeclaration.get(name, clazz).extractFrom(data);
	}

	public <T> T extractFromWorld(String name, Class<T> clazz) {
		return zipFileDeclaration.get(name, clazz).getWorldExtractor()
				.apply(declaration, world);
	}
}
