package amidst.mojangapi.world.testworld;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.testworld.io.TestWorldDirectoryReader;
import amidst.mojangapi.world.testworld.io.TestWorldDirectoryWriter;

@ThreadSafe
public enum TestWorldCache {
	INSTANCE;

	public static TestWorld get(TestWorldDeclaration declaration) {
		try {
			return INSTANCE.createIfNecessaryAndGet(declaration);
		} catch (IOException | MinecraftInterfaceException e) {
			throw new RuntimeException("unable to load testdata", e);
		}
	}

	public static void createAndPut(TestWorldDeclaration declaration, MinecraftInterface realMinecraftInterface)
			throws MinecraftInterfaceException,
			FileNotFoundException,
			IOException {
		INSTANCE.doCreateAndPut(declaration, realMinecraftInterface);
	}

	private final TestWorldBuilder builder = TestWorldBuilder.from(DefaultTestWorldDirectoryDeclaration.get());
	private final TestWorldDirectoryReader reader = builder.createReader();
	private final TestWorldDirectoryWriter writer = builder.createWriter();
	private final ConcurrentHashMap<TestWorldDeclaration, TestWorld> cache = new ConcurrentHashMap<>();

	public void doCreateAndPut(TestWorldDeclaration declaration, MinecraftInterface realMinecraftInterface)
			throws MinecraftInterfaceException,
			FileNotFoundException,
			IOException {
		writer.write(declaration, builder.createDirectory(declaration, realMinecraftInterface));
	}

	public TestWorld createIfNecessaryAndGet(TestWorldDeclaration declaration)
			throws IOException,
			MinecraftInterfaceException {
		TestWorld result = cache.get(declaration);
		if (result != null) {
			return result;
		} else {
			create(declaration);
			return cache.get(declaration);
		}
	}

	private synchronized void create(TestWorldDeclaration declaration) throws IOException, MinecraftInterfaceException {
		cache.putIfAbsent(declaration, builder.createTestWorld(reader.read(declaration)));
	}
}
