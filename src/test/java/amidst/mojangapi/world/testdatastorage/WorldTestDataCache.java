package amidst.mojangapi.world.testdatastorage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.testdatastorage.json.BiomeDataJson;
import amidst.mojangapi.world.testdatastorage.json.CoordinatesCollectionJson;
import amidst.mojangapi.world.testdatastorage.json.WorldMetadataJson;

@ThreadSafe
public enum WorldTestDataCache {
	INSTANCE;

	public static WorldTestData get(TestWorldDeclaration declaration) {
		try {
			return INSTANCE.createIfNecessaryAndGet(declaration);
		} catch (IOException | MinecraftInterfaceException e) {
			throw new RuntimeException("unable to load testdata", e);
		}
	}

	public static void createAndPut(TestWorldDeclaration declaration,
			MinecraftInterface realMinecraftInterface)
			throws MinecraftInterfaceException, FileNotFoundException,
			IOException {
		INSTANCE.doCreateAndPut(declaration, realMinecraftInterface);
	}

	// TODO: add end cities
	private static WorldTestDataZipFileDeclaration createZipFileDeclaration(
			int biomeDataFragmentsAroundOrigin,
			int structureFragmentsAroundOrigin) {
		// @formatter:off
		return WorldTestDataZipFileDeclaration.builder()
			.entry(WTDEntries.METADATA,                  WorldMetadataJson.class, (d, w) -> WorldMetadataJson.from(d.getRecognisedVersion(), w))
			.entry(WTDEntries.QUARTER_RESOLUTION_BIOME_DATA, BiomeDataJson.class, (d, w) -> BiomeDataJson.from(w.getBiomeDataOracle(), biomeDataFragmentsAroundOrigin, true))
			.entry(WTDEntries.FULL_RESOLUTION_BIOME_DATA,    BiomeDataJson.class, (d, w) -> BiomeDataJson.from(w.getBiomeDataOracle(), biomeDataFragmentsAroundOrigin, false))
			.entry(WTDEntries.SPAWN,             CoordinatesCollectionJson.class, (d, w) -> CoordinatesCollectionJson.fromWorldIconProducer(w.getSpawnProducer(),          DefaultWorldIconTypes.SPAWN.getName(),           corner -> null, structureFragmentsAroundOrigin))
			.entry(WTDEntries.STRONGHOLDS,       CoordinatesCollectionJson.class, (d, w) -> CoordinatesCollectionJson.fromWorldIconProducer(w.getStrongholdProducer(),     DefaultWorldIconTypes.STRONGHOLD.getName(),      corner -> null, structureFragmentsAroundOrigin))
			.entry(WTDEntries.VILLAGES,          CoordinatesCollectionJson.class, (d, w) -> CoordinatesCollectionJson.fromWorldIconProducer(w.getVillageProducer(),        DefaultWorldIconTypes.VILLAGE.getName(),         corner -> null, structureFragmentsAroundOrigin))
			.entry(WTDEntries.WITCH_HUTS,        CoordinatesCollectionJson.class, (d, w) -> CoordinatesCollectionJson.fromWorldIconProducer(w.getTempleProducer(),         DefaultWorldIconTypes.WITCH.getName(),           corner -> null, structureFragmentsAroundOrigin))
			.entry(WTDEntries.JUNGLE_TEMPLES,    CoordinatesCollectionJson.class, (d, w) -> CoordinatesCollectionJson.fromWorldIconProducer(w.getTempleProducer(),         DefaultWorldIconTypes.JUNGLE.getName(),          corner -> null, structureFragmentsAroundOrigin))
			.entry(WTDEntries.DESERT_TEMPLES,    CoordinatesCollectionJson.class, (d, w) -> CoordinatesCollectionJson.fromWorldIconProducer(w.getTempleProducer(),         DefaultWorldIconTypes.DESERT.getName(),          corner -> null, structureFragmentsAroundOrigin))
			.entry(WTDEntries.IGLOOS,            CoordinatesCollectionJson.class, (d, w) -> CoordinatesCollectionJson.fromWorldIconProducer(w.getTempleProducer(),         DefaultWorldIconTypes.IGLOO.getName(),           corner -> null, structureFragmentsAroundOrigin))
			.entry(WTDEntries.MINESHAFTS,        CoordinatesCollectionJson.class, (d, w) -> CoordinatesCollectionJson.fromWorldIconProducer(w.getMineshaftProducer(),      DefaultWorldIconTypes.MINESHAFT.getName(),       corner -> null, structureFragmentsAroundOrigin))
			.entry(WTDEntries.OCEAN_MONUMENTS,   CoordinatesCollectionJson.class, (d, w) -> CoordinatesCollectionJson.fromWorldIconProducer(w.getOceanMonumentProducer(),  DefaultWorldIconTypes.OCEAN_MONUMENT.getName(),  corner -> null, structureFragmentsAroundOrigin))
			.entry(WTDEntries.NETHER_FORTRESSES, CoordinatesCollectionJson.class, (d, w) -> CoordinatesCollectionJson.fromWorldIconProducer(w.getNetherFortressProducer(), DefaultWorldIconTypes.NETHER_FORTRESS.getName(), corner -> null, structureFragmentsAroundOrigin))
			.get();
		// @formatter:on
	}

	/**
	 * We don't generate testing structures in the last fragment to ensure that
	 * the structure generation code does not ask for biome data outside of the
	 * stored area.
	 */
	private static final int PADDING_FRAGMENTS = 1;
	private static final int FRAGMENTS_AROUND_ORIGIN = 10;
	private final WorldTestDataBuilder builder = WorldTestDataBuilder
			.from(createZipFileDeclaration(FRAGMENTS_AROUND_ORIGIN,
					FRAGMENTS_AROUND_ORIGIN - PADDING_FRAGMENTS));
	private final WorldTestDataReader reader = builder.createReader();
	private final WorldTestDataWriter writer = builder.createWriter();
	private final ConcurrentHashMap<TestWorldDeclaration, WorldTestData> cache = new ConcurrentHashMap<TestWorldDeclaration, WorldTestData>();

	public void doCreateAndPut(TestWorldDeclaration declaration,
			MinecraftInterface realMinecraftInterface)
			throws MinecraftInterfaceException, FileNotFoundException,
			IOException {
		writer.write(declaration,
				builder.create(declaration, realMinecraftInterface));
	}

	public WorldTestData createIfNecessaryAndGet(
			TestWorldDeclaration declaration) throws IOException,
			MinecraftInterfaceException {
		WorldTestData result = cache.get(declaration);
		if (result != null) {
			return result;
		} else {
			create(declaration);
			return cache.get(declaration);
		}
	}

	private synchronized void create(TestWorldDeclaration declaration)
			throws IOException, MinecraftInterfaceException {
		cache.putIfAbsent(declaration, reader.read(declaration));
	}
}
