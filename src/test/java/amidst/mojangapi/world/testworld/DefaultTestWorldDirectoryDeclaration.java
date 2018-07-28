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
import amidst.mojangapi.world.testworld.storage.json.EndIslandsJson;
import amidst.mojangapi.world.testworld.storage.json.SlimeChunksJson;
import amidst.mojangapi.world.testworld.storage.json.WorldMetadataJson;

@Immutable
public enum DefaultTestWorldDirectoryDeclaration {
	INSTANCE;

	public static TestWorldDirectoryDeclaration get() {
		return INSTANCE.declaration;
	}

	/**
	 * This should always be 0. Only change it when trying to find a good test
	 * world.
	 */
	private static final int MINIMAL_NUMBER_OF_COORDINATES = 0;

	private static final int OVERWORLD_FRAGMENTS_AROUND_ORIGIN = 10;
	private static final int END_FRAGMENTS_AROUND_ORIGIN = 4;

	private final TestWorldDirectoryDeclaration declaration = createDeclaration();

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
				.extractor(biomeDataExtractor())
				.skipEqualityCheck()
			.entry(TestWorldEntryNames.FULL_RESOLUTION_BIOME_DATA,    BiomeDataJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readBiomeData)
				.extractor(biomeDataExtractor())
				.skipEqualityCheck()
			.entry(TestWorldEntryNames.END_ISLANDS,      EndIslandsJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readEndIslands)
				.extractor(endIslandExtractor())
				.equalityChecker(EndIslandsJson::equals)
			.entry(TestWorldEntryNames.SLIME_CHUNKS,      SlimeChunksJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readSlimeChunks)
				.extractor(SlimeChunksJson::from)
				.equalityChecker(SlimeChunksJson::equals)
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
			.entry(TestWorldEntryNames.OCEAN_RUINS,            CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(worldIconExtractor(World::getTempleProducer,         DefaultWorldIconTypes.OCEAN_RUINS))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.SHIPWRECKS,            CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(worldIconExtractor(World::getTempleProducer,         DefaultWorldIconTypes.SHIPWRECK))
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
			.entry(TestWorldEntryNames.WOODLAND_MANSIONS,   CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(worldIconExtractor(World::getWoodlandMansionProducer,  DefaultWorldIconTypes.WOODLAND_MANSION))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.NETHER_FORTRESSES, CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(worldIconExtractor(World::getNetherFortressProducer, DefaultWorldIconTypes.NETHER_FORTRESS))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.LIKELY_END_CITY,   CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(endCityExtractor(DefaultWorldIconTypes.END_CITY))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.entry(TestWorldEntryNames.POSSIBLE_END_CITY, CoordinatesCollectionJson.class)
				.serializer(TestWorldEntrySerializer::writeJson)
				.deserializer(TestWorldEntrySerializer::readCoordinatesCollection)
				.extractor(endCityExtractor(DefaultWorldIconTypes.POSSIBLE_END_CITY))
				.equalityChecker(CoordinatesCollectionJson::equals)
			.create();
		// @formatter:on
	}

	private Function<World, BiomeDataJson> biomeDataExtractor() {
		return world -> {
			throw new UnsupportedOperationException(
					"the biome data are extracted with a special mechanism: the class RequestStoringMinecraftInterface");
		};
	}

	private Function<World, EndIslandsJson> endIslandExtractor() {
		return world -> EndIslandsJson.extract(world.getEndIslandOracle(), END_FRAGMENTS_AROUND_ORIGIN);
	}

	private Function<World, CoordinatesCollectionJson> worldIconExtractor(
			Function<World, WorldIconProducer<Void>> producer,
			DefaultWorldIconTypes worldIconType) {
		return world -> CoordinatesCollectionJson.extractWorldIcons(
				producer.apply(world),
				worldIconType.getLabel(),
				corner -> null,
				OVERWORLD_FRAGMENTS_AROUND_ORIGIN,
				MINIMAL_NUMBER_OF_COORDINATES);
	}

	private Function<World, CoordinatesCollectionJson> endCityExtractor(DefaultWorldIconTypes worldIconType) {
		return world -> CoordinatesCollectionJson.extractWorldIcons(
				world.getEndCityProducer(),
				worldIconType.getLabel(),
				corner -> world.getEndIslandOracle().getAt(corner),
				END_FRAGMENTS_AROUND_ORIGIN,
				MINIMAL_NUMBER_OF_COORDINATES);
	}
}
