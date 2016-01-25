package amidst.mojangapi.mocking;

import java.util.Map;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.SeedHistoryLogger;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.player.PlayerInformation;
import amidst.mojangapi.world.testdatastorage.TestWorldDeclaration;
import amidst.mojangapi.world.testdatastorage.WTDEntries;
import amidst.mojangapi.world.testdatastorage.WorldTestDataZipFileDeclaration;
import amidst.mojangapi.world.testdatastorage.json.BiomeDataJson;
import amidst.mojangapi.world.testdatastorage.json.WorldMetadataJson;

@ThreadSafe
public class FakeWorldBuilder {
	public static FakeWorldBuilder create(
			WorldTestDataZipFileDeclaration zipFileDeclaration) {
		return new FakeWorldBuilder(createWorldBuilder(), zipFileDeclaration);
	}

	private static WorldBuilder createWorldBuilder() {
		return new WorldBuilder(new ImmutablePlayerInformationCache(
				PlayerInformation.theSingleplayerPlayer()),
				SeedHistoryLogger.from(null));
	}

	private final WorldBuilder builder;
	private final WorldTestDataZipFileDeclaration zipFileDeclaration;

	public FakeWorldBuilder(WorldBuilder builder,
			WorldTestDataZipFileDeclaration zipFileDeclaration) {
		this.builder = builder;
		this.zipFileDeclaration = zipFileDeclaration;
	}

	public World createRealWorld(TestWorldDeclaration declaration,
			MinecraftInterface realMinecraftInterface)
			throws MinecraftInterfaceException {
		// @formatter:off
		return builder.fromSeed(
				realMinecraftInterface,
				declaration.getWorldSeed(),
				declaration.getWorldType());
		// @formatter:on
	}

	public World createFakeWorld(Map<String, Object> data)
			throws MinecraftInterfaceException {
		// @formatter:off
		WorldMetadataJson worldMetadata = get(WTDEntries.METADATA,                  WorldMetadataJson.class, data);
		BiomeDataJson quarterBiomeData =  get(WTDEntries.QUARTER_RESOLUTION_BIOME_DATA, BiomeDataJson.class, data);
		BiomeDataJson fullBiomeData =     get(WTDEntries.FULL_RESOLUTION_BIOME_DATA,    BiomeDataJson.class, data);
		// @formatter:on
		return createFakeWorld(worldMetadata, quarterBiomeData, fullBiomeData);
	}

	private <T> T get(String name, Class<T> clazz, Map<String, Object> data) {
		return zipFileDeclaration.get(name, clazz).extractFrom(data);
	}

	public World createFakeWorld(WorldMetadataJson worldMetadata,
			BiomeDataJson quarterBiomeData, BiomeDataJson fullBiomeData)
			throws MinecraftInterfaceException {
		// @formatter:off
		return builder.fromSeed(
				createFakeMinecraftInterface(worldMetadata, quarterBiomeData, fullBiomeData),
				WorldSeed.fromUserInput(worldMetadata.getSeed() + ""),
				worldMetadata.getWorldType());
		// @formatter:on
	}

	private static FakeMinecraftInterface createFakeMinecraftInterface(
			WorldMetadataJson worldMetadataJson,
			BiomeDataJson quarterBiomeData, BiomeDataJson fullBiomeData) {
		return new FakeMinecraftInterface(worldMetadataJson, quarterBiomeData,
				fullBiomeData);
	}
}
