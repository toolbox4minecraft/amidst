package amidst.mojangapi.world.testdatastorage;

import java.util.HashMap;
import java.util.Map;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.mocking.FakeWorldBuilder;
import amidst.mojangapi.world.World;

@ThreadSafe
public class WorldTestDataBuilder {
	public static WorldTestDataBuilder from(
			WorldTestDataZipFileDeclaration zipFileDeclaration) {
		return new WorldTestDataBuilder(zipFileDeclaration,
				FakeWorldBuilder.create(zipFileDeclaration));
	}

	private final WorldTestDataZipFileDeclaration zipFileDeclaration;
	private final FakeWorldBuilder worldBuilder;

	public WorldTestDataBuilder(
			WorldTestDataZipFileDeclaration zipFileDeclaration,
			FakeWorldBuilder worldBuilder) {
		this.zipFileDeclaration = zipFileDeclaration;
		this.worldBuilder = worldBuilder;
	}

	public WorldTestDataReader createReader() {
		return new WorldTestDataReader(this, zipFileDeclaration);
	}

	public WorldTestDataWriter createWriter() {
		return new WorldTestDataWriter();
	}

	public WorldTestData create(TestWorldDeclaration declaration,
			MinecraftInterface realMinecraftInterface)
			throws MinecraftInterfaceException {
		return create(declaration, worldBuilder.createRealWorld(declaration,
				realMinecraftInterface));
	}

	public WorldTestData create(TestWorldDeclaration declaration,
			World realWorld) throws MinecraftInterfaceException {
		Map<String, Object> data = new HashMap<String, Object>();
		for (WorldTestDataZipFileEntryDeclaration<?> entry : zipFileDeclaration
				.getDeclarations()) {
			extractAndPut(data, entry, declaration, realWorld);
		}
		return create(declaration, data);
	}

	private void extractAndPut(Map<String, Object> data,
			WorldTestDataZipFileEntryDeclaration<?> entry,
			TestWorldDeclaration declaration, World realWorld) {
		data.put(
				entry.getName(),
				extract(entry.getName(), entry.getClazz(), declaration,
						realWorld));
	}

	private <T> T extract(String name, Class<T> clazz,
			TestWorldDeclaration declaration, World realWorld) {
		return zipFileDeclaration.get(name, clazz).getWorldExtractor()
				.apply(declaration, realWorld);
	}

	public WorldTestData create(TestWorldDeclaration declaration,
			Map<String, Object> data) throws MinecraftInterfaceException {
		World world = worldBuilder.createFakeWorld(data);
		return new WorldTestData(zipFileDeclaration, declaration, world, data);
	}
}
