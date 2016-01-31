package amidst.mojangapi.world.testworld;

import java.util.function.Function;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.testworld.file.TestWorldDirectoryDeclaration;
import amidst.mojangapi.world.testworld.io.TestWorldEntrySerializer;
import amidst.mojangapi.world.testworld.storage.json.BiomeDataJson;
import amidst.mojangapi.world.testworld.storage.json.CoordinatesCollectionJson;
import amidst.mojangapi.world.testworld.storage.json.WorldMetadataJson;

@Immutable
public enum DefaultTestWorldDirectoryDeclaration {
	INSTANCE;

	public static TestWorldDirectoryDeclaration get() {
		return INSTANCE.declaration;
	}

	private static final int MIN_NUMBER_OF_COORDINATES = 5;
	private static final int FRAGMENTS_AROUND_ORIGIN = 10;

	private final TestWorldDirectoryDeclaration declaration = createDeclaration();

	// TODO: add end cities
	private TestWorldDirectoryDeclaration createDeclaration() {
		// @formatter:off
		return TestWorldDirectoryDeclaration.builder()
			.entry(TestWorldEntryNames.METADATA,                  WorldMetadataJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readMetaData)
				.extractor(WorldMetadataJson::from)
				.skipEqualityCheck()
			.entry(TestWorldEntryNames.QUARTER_RESOLUTION_BIOME_DATA, BiomeDataJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readBiomeData)
				.extractor(extractBiomeData())
				.skipEqualityCheck()
			.entry(TestWorldEntryNames.FULL_RESOLUTION_BIOME_DATA,    BiomeDataJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readBiomeData)
				.extractor(extractBiomeData())
				.skipEqualityCheck()
			.entry(TestWorldEntryNames.SPAWN,             CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(CoordinatesCollectionJson::extractWorldSpawn)
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.STRONGHOLDS,       CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(CoordinatesCollectionJson::extractStrongholds)
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.VILLAGES,          CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(worldIconExtractor(World::getVillageProducer,        DefaultWorldIconTypes.VILLAGE))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.WITCH_HUTS,        CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(worldIconExtractor(World::getTempleProducer,         DefaultWorldIconTypes.WITCH))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.JUNGLE_TEMPLES,    CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(worldIconExtractor(World::getTempleProducer,         DefaultWorldIconTypes.JUNGLE))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.DESERT_TEMPLES,    CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(worldIconExtractor(World::getTempleProducer,         DefaultWorldIconTypes.DESERT))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.IGLOOS,            CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(worldIconExtractor(World::getTempleProducer,         DefaultWorldIconTypes.IGLOO))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.MINESHAFTS,        CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(worldIconExtractor(World::getMineshaftProducer,      DefaultWorldIconTypes.MINESHAFT))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.OCEAN_MONUMENTS,   CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(worldIconExtractor(World::getOceanMonumentProducer,  DefaultWorldIconTypes.OCEAN_MONUMENT))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.NETHER_FORTRESSES, CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(worldIconExtractor(World::getNetherFortressProducer, DefaultWorldIconTypes.NETHER_FORTRESS))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.create();
		// @formatter:on
	}

	private Function<World, BiomeDataJson> extractBiomeData() {
		return world -> {
			throw new UnsupportedOperationException(
					"the biome data are extracted with a special mechanism: the class RequestStoringMinecraftInterface");
		};
	}

	private Function<World, CoordinatesCollectionJson> worldIconExtractor(
			Function<World, WorldIconProducer<Void>> producer,
			DefaultWorldIconTypes worldIconType) {
		return world -> CoordinatesCollectionJson.fromWorldIconProducer(
				producer.apply(world), worldIconType.getName(), corner -> null,
				FRAGMENTS_AROUND_ORIGIN, MIN_NUMBER_OF_COORDINATES);
	}
}