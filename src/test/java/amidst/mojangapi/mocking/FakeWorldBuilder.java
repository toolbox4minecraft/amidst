package amidst.mojangapi.mocking;

import java.util.Map;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.testworld.TestWorldDeclaration;
import amidst.mojangapi.world.testworld.TestWorldEntryNames;
import amidst.mojangapi.world.testworld.file.TestWorldDirectory;
import amidst.mojangapi.world.testworld.file.TestWorldDirectoryDeclaration;
import amidst.mojangapi.world.testworld.storage.json.BiomeDataJson;
import amidst.mojangapi.world.testworld.storage.json.WorldMetadataJson;

@ThreadSafe
public class FakeWorldBuilder {
	public static FakeWorldBuilder create(TestWorldDirectoryDeclaration directoryDeclaration) {
		return new FakeWorldBuilder(WorldBuilder.createSilentPlayerless(), directoryDeclaration);
	}

	private final WorldBuilder builder;
	private final TestWorldDirectoryDeclaration directoryDeclaration;

	public FakeWorldBuilder(WorldBuilder builder, TestWorldDirectoryDeclaration directoryDeclaration) {
		this.builder = builder;
		this.directoryDeclaration = directoryDeclaration;
	}

	public World createRealWorld(TestWorldDeclaration worldDeclaration, MinecraftInterface realMinecraftInterface)
			throws MinecraftInterfaceException {
		return builder.from(
				realMinecraftInterface,
				worldDeclaration.getWorldOptions());
	}

	public World createFakeWorld(TestWorldDirectory worldDeclaration) throws MinecraftInterfaceException {
		Map<String, Object> data = worldDeclaration.getData();
		// @formatter:off
		WorldMetadataJson worldMetadata = get(TestWorldEntryNames.METADATA,                  WorldMetadataJson.class, data);
		BiomeDataJson quarterBiomeData =  get(TestWorldEntryNames.QUARTER_RESOLUTION_BIOME_DATA, BiomeDataJson.class, data);
		BiomeDataJson fullBiomeData =     get(TestWorldEntryNames.FULL_RESOLUTION_BIOME_DATA,    BiomeDataJson.class, data);
		// @formatter:on
		return createFakeWorld(worldMetadata, quarterBiomeData, fullBiomeData);
	}

	private <T> T get(String name, Class<T> clazz, Map<String, Object> data) {
		return directoryDeclaration.getEntryDeclaration(name, clazz).extractFromDataMap(data);
	}

	private World createFakeWorld(
			WorldMetadataJson worldMetadata,
			BiomeDataJson quarterBiomeData,
			BiomeDataJson fullBiomeData) throws MinecraftInterfaceException {
		return builder.from(
				createFakeMinecraftInterface(worldMetadata, quarterBiomeData, fullBiomeData),
				worldMetadata.intoWorldOptions());
	}

	private static FakeMinecraftInterface createFakeMinecraftInterface(
			WorldMetadataJson worldMetadataJson,
			BiomeDataJson quarterBiomeData,
			BiomeDataJson fullBiomeData) {
		return new FakeMinecraftInterface(worldMetadataJson, quarterBiomeData, fullBiomeData);
	}
}
