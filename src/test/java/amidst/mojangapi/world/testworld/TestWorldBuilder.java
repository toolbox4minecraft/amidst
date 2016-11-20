package amidst.mojangapi.world.testworld;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.mocking.BiomeDataJsonBuilder;
import amidst.mojangapi.mocking.FakeWorldBuilder;
import amidst.mojangapi.mocking.RequestStoringMinecraftInterface;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.testworld.file.TestWorldDirectory;
import amidst.mojangapi.world.testworld.file.TestWorldDirectoryDeclaration;
import amidst.mojangapi.world.testworld.io.TestWorldDirectoryReader;
import amidst.mojangapi.world.testworld.io.TestWorldDirectoryWriter;

@ThreadSafe
public class TestWorldBuilder {
	public static TestWorldBuilder from(TestWorldDirectoryDeclaration directoryDeclaration) {
		return new TestWorldBuilder(directoryDeclaration, FakeWorldBuilder.create(directoryDeclaration));
	}

	private final TestWorldDirectoryDeclaration directoryDeclaration;
	private final FakeWorldBuilder worldBuilder;

	public TestWorldBuilder(TestWorldDirectoryDeclaration directoryDeclaration, FakeWorldBuilder worldBuilder) {
		this.directoryDeclaration = directoryDeclaration;
		this.worldBuilder = worldBuilder;
	}

	public TestWorldDirectoryReader createReader() {
		return new TestWorldDirectoryReader(directoryDeclaration);
	}

	public TestWorldDirectoryWriter createWriter() {
		return new TestWorldDirectoryWriter(directoryDeclaration);
	}

	public TestWorldDirectory createDirectory(
			TestWorldDeclaration worldDeclaration,
			MinecraftInterface realMinecraftInterface) throws MinecraftInterfaceException {
		BiomeDataJsonBuilder builder = new BiomeDataJsonBuilder();
		RequestStoringMinecraftInterface minecraftInterface = new RequestStoringMinecraftInterface(
				realMinecraftInterface,
				builder);
		World realWorld = worldBuilder.createRealWorld(worldDeclaration, minecraftInterface);
		return directoryDeclaration
				.create(worldDeclaration, realWorld, builder.createQuarterBiomeData(), builder.createFullBiomeData());
	}

	public TestWorld createTestWorld(TestWorldDirectory directory) throws MinecraftInterfaceException {
		World world = worldBuilder.createFakeWorld(directory);
		return new TestWorld(world, directory);
	}
}
