package amidst.mojangapi.world.versionfeatures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import amidst.fragment.layer.LayerIds;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.BiomeList;
import amidst.mojangapi.world.biome.UnknownBiomeIdException;
import amidst.mojangapi.world.icon.locationchecker.BuriedTreasureLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.EndCityLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_ChanceBased;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_Original;
import amidst.mojangapi.world.icon.locationchecker.NetherFortressAlgorithm_Original;
import amidst.mojangapi.world.icon.locationchecker.OceanMonumentLocationChecker_Fixed;
import amidst.mojangapi.world.icon.locationchecker.OceanMonumentLocationChecker_Original;
import amidst.mojangapi.world.icon.locationchecker.PillagerOutpostLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.ScatteredFeaturesLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.VillageLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.WoodlandMansionLocationChecker;
import amidst.mojangapi.world.icon.producer.CachedWorldIconProducer;
import amidst.mojangapi.world.icon.producer.EndGatewayProducer;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_128Algorithm;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_Buggy128Algorithm;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_Original;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.HeuristicWorldSpawnOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;
import amidst.mojangapi.world.oracle.end.EndIslandList;
import amidst.mojangapi.world.oracle.end.EndIslandOracle;

public enum DefaultVersionFeatures {
	;

	public static VersionFeatures.Builder builder(WorldOptions worldOptions, MinecraftInterface.World minecraftWorld) {
		if (worldOptions == null || minecraftWorld == null) {
			return FEATURES_BUILDER.clone();
		} else {
			return FEATURES_BUILDER.clone()
							.withValue(FeatureKey.WORLD_OPTIONS, worldOptions)
							.withValue(MINECRAFT_WORLD, minecraftWorld);
		}
	}

	// @formatter:off
	private static final FeatureKey<MinecraftInterface.World> MINECRAFT_WORLD                      = FeatureKey.make();
	public static final FeatureKey<List<Biome>>    VALID_BIOMES_FOR_STRUCTURE_SPAWN                = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_STRONGHOLD      = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_FOR_STRUCTURE_VILLAGE              = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_FOR_STRUCTURE_PILLAGER_OUTPOST     = FeatureKey.make();
	private static final FeatureKey<Boolean>       DO_COMPLEX_VILLAGE_CHECK                        = FeatureKey.make();
	private static final FeatureKey<Integer>       OUTPOST_VILLAGE_AVOID_DISTANCE                  = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_DESERT_TEMPLE   = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_IGLOO           = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_JUNGLE_TEMPLE   = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_WITCH_HUT       = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_RUINS     = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_SHIPWRECK       = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_MONUMENT  = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_BURIED_TREASURE = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_FOR_STRUCTURE_OCEAN_MONUMENT       = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VALID_BIOMES_FOR_STRUCTURE_WOODLAND_MANSION     = FeatureKey.make();
	private static final FeatureKey<Long>          SEED_FOR_STRUCTURE_DESERT_TEMPLE                = FeatureKey.make();
	private static final FeatureKey<Long>          SEED_FOR_STRUCTURE_IGLOO                        = FeatureKey.make();
	private static final FeatureKey<Long>          SEED_FOR_STRUCTURE_JUNGLE_TEMPLE                = FeatureKey.make();
	private static final FeatureKey<Long>          SEED_FOR_STRUCTURE_WITCH_HUT                    = FeatureKey.make();
	private static final FeatureKey<Long>          SEED_FOR_STRUCTURE_OCEAN_RUINS                  = FeatureKey.make();
	private static final FeatureKey<Long>          SEED_FOR_STRUCTURE_SHIPWRECK                    = FeatureKey.make();
	private static final FeatureKey<Long>          SEED_FOR_STRUCTURE_BURIED_TREASURE              = FeatureKey.make();
	private static final FeatureKey<Long>          SEED_FOR_STRUCTURE_NETHER_FORTRESS              = FeatureKey.make();
	private static final FeatureKey<Byte>          MAX_DISTANCE_SCATTERED_FEATURES_SHIPWRECK       = FeatureKey.make();
	private static final FeatureKey<Byte>          MIN_DISTANCE_SCATTERED_FEATURES_SHIPWRECK       = FeatureKey.make();
	private static final FeatureKey<Byte>          MAX_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS     = FeatureKey.make();
	private static final FeatureKey<Byte>          MIN_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS     = FeatureKey.make();
	private static final FeatureKey<Byte>          MAX_DISTANCE_SCATTERED_FEATURES_NETHER_FORTRESS = FeatureKey.make();
	private static final FeatureKey<Byte>          MIN_DISTANCE_SCATTERED_FEATURES_NETHER_FORTRESS = FeatureKey.make();
	private static final FeatureKey<Boolean>       BUGGY_STRUCTURE_COORDINATE_MATH                 = FeatureKey.make();
	private static final FeatureKey<Boolean>       BIOME_DATA_ORACLE_QUARTER_RES_OVERRIDE          = FeatureKey.make();
	private static final FeatureKey<Boolean>       BIOME_DATA_ORACLE_ACCURATE_LOCATION_COUNT       = FeatureKey.make();
	private static final FeatureKey<Integer>       BIOME_DATA_ORACLE_MIDDLE_OF_CHUNK_OFFSET        = FeatureKey.make();

	private static final VersionFeatures.Builder FEATURES_BUILDER = VersionFeatures.builder()
			.with(FeatureKey.BIOME_DATA_ORACLE, VersionFeature.bind(features -> {
				BiomeDataOracle.Config config = new BiomeDataOracle.Config();
				config.quarterResOverride = features.get(BIOME_DATA_ORACLE_QUARTER_RES_OVERRIDE);
				config.middleOfChunkOffset = features.get(BIOME_DATA_ORACLE_MIDDLE_OF_CHUNK_OFFSET);
				config.accurateLocationCount = features.get(BIOME_DATA_ORACLE_ACCURATE_LOCATION_COUNT);
				return VersionFeature.constant(new BiomeDataOracle(
					features.get(MINECRAFT_WORLD),
					features.get(FeatureKey.BIOME_LIST),
					config
				));
			}))
			.with(BIOME_DATA_ORACLE_QUARTER_RES_OVERRIDE, VersionFeature.<Boolean> builder()
				.init(
					false
				).since(RecognisedVersion._20w21a,
					true
				).construct()
			)
			.with(BIOME_DATA_ORACLE_ACCURATE_LOCATION_COUNT, VersionFeature.<Boolean> builder()
				.init(
					false
				).since(RecognisedVersion._18w06a,
					true
				).construct()
			)
			.with(BIOME_DATA_ORACLE_MIDDLE_OF_CHUNK_OFFSET, VersionFeature.<Integer> builder()
				.init(
					8
				).since(RecognisedVersion._18w06a,
					9
				).construct()
			)

			.with(FeatureKey.ENABLED_LAYERS, VersionFeature.<Integer> listBuilder()
				.init(
					LayerIds.ALPHA,
					LayerIds.BIOME_DATA,
					LayerIds.BACKGROUND,
					LayerIds.SLIME,
					LayerIds.GRID,
					LayerIds.SPAWN,
					LayerIds.PLAYER
				).sinceExtend(RecognisedVersion._b1_8_1, // Actually b1.8, but the version strings are identical
					LayerIds.STRONGHOLD,
					LayerIds.VILLAGE,
					LayerIds.MINESHAFT
				).sinceExtend(RecognisedVersion._b1_9_pre1,
					LayerIds.NETHER_FORTRESS
				).sinceExtend(RecognisedVersion._12w21a,
					LayerIds.TEMPLE
				).sinceExtend(RecognisedVersion._1_8,
					LayerIds.OCEAN_MONUMENT
				).sinceExtend(RecognisedVersion._15w31c,
					LayerIds.END_ISLANDS,
					LayerIds.END_CITY
				).sinceExtend(RecognisedVersion._16w43a,
					LayerIds.WOODLAND_MANSION
				).sinceExtend(RecognisedVersion._18w09a,
					LayerIds.OCEAN_FEATURES,
					LayerIds.END_GATEWAY // were introduced in 16w39a, but we can only find them past here
				).construct())

			.with(FeatureKey.BIOME_LIST, DefaultBiomes.DEFAULT_BIOMES)
			.with(FeatureKey.END_ISLAND_ORACLE, (recognisedVersion, features) ->
				EndIslandOracle.from(getWorldSeed(features), recognisedVersion)
			)

			.with(FeatureKey.SLIME_CHUNK_ORACLE, VersionFeature.bind(features ->
				VersionFeature.constant(new SlimeChunkOracle(getWorldSeed(features)))
			))

			.with(FeatureKey.WORLD_SPAWN_ORACLE, VersionFeature.bind(features ->
				VersionFeature.constant(
					new HeuristicWorldSpawnOracle(
						getWorldSeed(features),
						features.get(FeatureKey.BIOME_DATA_ORACLE),
						features.get(VALID_BIOMES_FOR_STRUCTURE_SPAWN))
				)))
			.with(VALID_BIOMES_FOR_STRUCTURE_SPAWN, VersionFeature.<Integer> listBuilder()
				.init(
					DefaultBiomes.forest,
					DefaultBiomes.plains,
					DefaultBiomes.taiga
				).sinceExtend(RecognisedVersion._12w01a,
					DefaultBiomes.taigaHills,
					DefaultBiomes.forestHills
				).sinceExtend(RecognisedVersion._12w03a,
					DefaultBiomes.jungle,
					DefaultBiomes.jungleHills
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))

			.with(FeatureKey.NETHER_FORTRESS_LOCATION_CHECKER, (version, features) -> {
				if(RecognisedVersion.isOlder(version, RecognisedVersion._20w16a)) {
					return new NetherFortressAlgorithm_Original(getWorldSeed(features));
				} else {
					// TODO: add Nether biome checks when we implement Nether biomes
					// Bastions replace Fortresses in Warped/Crimson Forest
					return scatteredFeature(
						MAX_DISTANCE_SCATTERED_FEATURES_NETHER_FORTRESS,
						MIN_DISTANCE_SCATTERED_FEATURES_NETHER_FORTRESS,
						null,
						SEED_FOR_STRUCTURE_NETHER_FORTRESS
					).getValue(version, features);
				}
			})
			.with(SEED_FOR_STRUCTURE_NETHER_FORTRESS, VersionFeature.constant(30084232L))
			.with(MAX_DISTANCE_SCATTERED_FEATURES_NETHER_FORTRESS, VersionFeature.<Byte> builder()
				.init(
					(byte) 24
				).since(RecognisedVersion._20w19a,
					(byte) 30
				).since(RecognisedVersion._1_16_pre3,
					(byte) 27
				).construct()
			)
			.with(MIN_DISTANCE_SCATTERED_FEATURES_NETHER_FORTRESS, VersionFeature.constant((byte) 4))

			.with(FeatureKey.END_CITY_LOCATION_CHECKER, VersionFeature.bind(features ->
				VersionFeature.constant(new EndCityLocationChecker(getWorldSeed(features)))
			))
			
			.with(FeatureKey.END_GATEWAY_PRODUCER, VersionFeature.bind(features ->
				VersionFeature.<WorldIconProducer<EndIslandList>> builder()
					.init(
						new EndGatewayProducer(getWorldSeed(features), 0, 3, features.get(FeatureKey.END_ISLAND_ORACLE))
					).since(RecognisedVersion._20w06a, // TODO: confirm this version is correct; this changed after 1.15.2 and before 1.16
						new EndGatewayProducer(getWorldSeed(features), 13, 4, features.get(FeatureKey.END_ISLAND_ORACLE))
					)
					.construct()
			))

			.with(FeatureKey.MINESHAFT_LOCATION_CHECKER, VersionFeature.bind(features ->
				VersionFeature.<LocationChecker> builder()
					.init(
						new MineshaftAlgorithm_Original(getWorldSeed(features))
					).since(RecognisedVersion._1_4_2,
						new MineshaftAlgorithm_ChanceBased(getWorldSeed(features), 0.01D, true)
					).since(RecognisedVersion._1_7_2,
						new MineshaftAlgorithm_ChanceBased(getWorldSeed(features), 0.004D, true)
					).since(RecognisedVersion._18w06a,
						new MineshaftAlgorithm_ChanceBased(getWorldSeed(features), 0.01D, false)
					).construct()
			))

			.with(FeatureKey.STRONGHOLD_PRODUCER, VersionFeature.bind(features -> {
				long worldSeed = getWorldSeed(features);
				BiomeDataOracle biomeOracle = features.get(FeatureKey.BIOME_DATA_ORACLE);
				List<Biome> validBiomes = features.get(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_STRONGHOLD);
				return VersionFeature.<CachedWorldIconProducer>builder()
					.init(
						new StrongholdProducer_Original(worldSeed, biomeOracle, validBiomes)
					).since(RecognisedVersion._15w43c,
						// this should be 15w43a, which is not recognised
						new StrongholdProducer_Buggy128Algorithm(worldSeed, biomeOracle, validBiomes)
					).since(RecognisedVersion._1_9_pre2,
						// this should be 16w06a
						new StrongholdProducer_128Algorithm(worldSeed, biomeOracle, validBiomes)
					).construct();
			}))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_STRONGHOLD, VersionFeature.<Integer> listBuilder()
				.init().since(RecognisedVersion._b1_8_1,
					DefaultBiomes.desert,
					DefaultBiomes.forest,
					DefaultBiomes.extremeHills,
					DefaultBiomes.swampland
				).sinceExtend(RecognisedVersion._b1_9_pre6,
					DefaultBiomes.taiga,
					DefaultBiomes.icePlains,
					DefaultBiomes.iceMountains
				).sinceExtend(RecognisedVersion._1_1,
					DefaultBiomes.desertHills,
					DefaultBiomes.forestHills,
					DefaultBiomes.extremeHillsEdge
				).sinceExtend(RecognisedVersion._12w03a,
					DefaultBiomes.jungle,
					DefaultBiomes.jungleHills
				).since(RecognisedVersion._13w36a,
					// this includes all the biomes above, except for the swampland
					features -> getValidBiomesForStrongholdSinceV13w36a(features.get(FeatureKey.BIOME_LIST))
				).sinceExtend(RecognisedVersion._18w06a,
					DefaultBiomes.mushroomIslandShore
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))


			.with(FeatureKey.VILLAGE_LOCATION_CHECKER, VersionFeature.bind(features ->
				VersionFeature.constant(
					new VillageLocationChecker(
						getWorldSeed(features),
						features.get(FeatureKey.BIOME_DATA_ORACLE),
						features.get(VALID_BIOMES_FOR_STRUCTURE_VILLAGE),
						features.get(DO_COMPLEX_VILLAGE_CHECK)
					)
				)))
			.with(VALID_BIOMES_FOR_STRUCTURE_VILLAGE, VersionFeature.<Integer> listBuilder()
				.init(
					DefaultBiomes.plains,
					DefaultBiomes.desert
				).sinceExtend(RecognisedVersion._13w36a,
					DefaultBiomes.savanna
				).sinceExtend(RecognisedVersion._16w20a,
					DefaultBiomes.taiga
				).sinceExtend(RecognisedVersion._18w49a,
					DefaultBiomes.icePlains
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(DO_COMPLEX_VILLAGE_CHECK, VersionFeature.<Boolean> builder()
				.init(
					true
				).since(RecognisedVersion._16w20a,
					false
				).construct())

			.with(FeatureKey.PILLAGER_OUTPOST_LOCATION_CHECKER, VersionFeature.bind(features ->
				VersionFeature.constant(
					new PillagerOutpostLocationChecker(
						getWorldSeed(features),
						features.get(FeatureKey.BIOME_DATA_ORACLE),
						features.get(FeatureKey.VILLAGE_LOCATION_CHECKER),
						features.get(OUTPOST_VILLAGE_AVOID_DISTANCE),
						features.get(VALID_BIOMES_FOR_STRUCTURE_PILLAGER_OUTPOST)
					)
				)))
			.with(VALID_BIOMES_FOR_STRUCTURE_PILLAGER_OUTPOST, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._18w47b,
					DefaultBiomes.plains,
					DefaultBiomes.desert,
					DefaultBiomes.savanna,
					DefaultBiomes.taiga
				).sinceExtend(RecognisedVersion._18w49a,
					DefaultBiomes.icePlains
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(OUTPOST_VILLAGE_AVOID_DISTANCE, VersionFeature.<Integer> builder()
				.init(
					-1
				// from 19w11a to 19w13a, outpost towers aren't generated close
				// to villages, but the structure is still reported by `/locate`.
				).since(RecognisedVersion._19w13b,
					10
				).construct())

			.with(FeatureKey.DESERT_TEMPLE_LOCATION_CHECKER, scatteredFeature(
				VALID_BIOMES_AT_MIDDLE_OF_CHUNK_DESERT_TEMPLE,
				SEED_FOR_STRUCTURE_DESERT_TEMPLE))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_DESERT_TEMPLE, VersionFeature.<Integer> listBuilder()
				.init(
					DefaultBiomes.desert
				).sinceExtend(RecognisedVersion._12w01a,
					DefaultBiomes.desertHills
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(SEED_FOR_STRUCTURE_DESERT_TEMPLE, VersionFeature.<Long> builder()
				.init(
					14357617L
				).construct())

			.with(FeatureKey.IGLOO_LOCATION_CHECKER, scatteredFeature(
				VALID_BIOMES_AT_MIDDLE_OF_CHUNK_IGLOO,
				SEED_FOR_STRUCTURE_IGLOO))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_IGLOO, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._15w43c,
					DefaultBiomes.icePlains,
					DefaultBiomes.coldTaiga
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))

			.with(SEED_FOR_STRUCTURE_IGLOO, VersionFeature.<Long> builder()
				.init(
					14357617L
				).since(RecognisedVersion._18w06a,
					14357618L
				).construct())

			.with(FeatureKey.JUNGLE_TEMPLE_LOCATION_CHECKER, scatteredFeature(
				VALID_BIOMES_AT_MIDDLE_OF_CHUNK_JUNGLE_TEMPLE,
				SEED_FOR_STRUCTURE_JUNGLE_TEMPLE))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_JUNGLE_TEMPLE, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._12w22a,
					DefaultBiomes.jungle
				).sinceExtend(RecognisedVersion._1_4_2,
					DefaultBiomes.jungleHills // TODO: jungle temples spawn only since 1.4.2 in jungle hills?
				).sinceExtend(RecognisedVersion._19w06a,
					DefaultBiomes.bambooJungle,
					DefaultBiomes.bambooJungleHills
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(SEED_FOR_STRUCTURE_JUNGLE_TEMPLE,  VersionFeature.<Long> builder()
				.init(
						14357617L
				).since(RecognisedVersion._18w06a,
						14357619L
				).construct())

			.with(FeatureKey.WITCH_HUT_LOCATION_CHECKER, scatteredFeature(
				VALID_BIOMES_AT_MIDDLE_OF_CHUNK_WITCH_HUT,
				SEED_FOR_STRUCTURE_WITCH_HUT))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_WITCH_HUT, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._1_4_2,
					DefaultBiomes.swampland
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(SEED_FOR_STRUCTURE_WITCH_HUT, VersionFeature.<Long> builder()
				.init(
						14357617L
				).since(RecognisedVersion._18w06a,
						14357620L
				).construct())

			.with(FeatureKey.OCEAN_MONUMENT_LOCATION_CHECKER, VersionFeature.bind(features -> {
				long worldSeed = getWorldSeed(features);
				BiomeDataOracle biomeOracle = features.get(FeatureKey.BIOME_DATA_ORACLE);
				List<Biome> validCenterBiomes = features.get(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_MONUMENT);
				List<Biome> validBiomes = features.get(VALID_BIOMES_FOR_STRUCTURE_OCEAN_MONUMENT);
				return VersionFeature.<LocationChecker> builder()
					.init(
						new OceanMonumentLocationChecker_Original(worldSeed, biomeOracle, validCenterBiomes, validBiomes)
					).since(RecognisedVersion._15w46a,
						new OceanMonumentLocationChecker_Fixed(worldSeed, biomeOracle, validCenterBiomes, validBiomes)
					).construct();
			}))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_MONUMENT, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._1_8,
					DefaultBiomes.deepOcean
				).sinceExtend(RecognisedVersion._18w08a,
					DefaultBiomes.coldDeepOcean,
					DefaultBiomes.warmDeepOcean,
					DefaultBiomes.lukewarmDeepOcean,
					DefaultBiomes.frozenDeepOcean
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(VALID_BIOMES_FOR_STRUCTURE_OCEAN_MONUMENT, VersionFeature.<Integer> listBuilder()
				.init().sinceExtend(RecognisedVersion._1_8,
					DefaultBiomes.ocean,
					DefaultBiomes.deepOcean,
					DefaultBiomes.frozenOcean,
					DefaultBiomes.river,
					DefaultBiomes.frozenRiver
				).sinceExtend(RecognisedVersion._18w08a,
					DefaultBiomes.coldOcean,
					DefaultBiomes.coldDeepOcean,
					DefaultBiomes.warmOcean,
					DefaultBiomes.warmDeepOcean,
					DefaultBiomes.lukewarmOcean,
					DefaultBiomes.lukewarmDeepOcean,
					DefaultBiomes.frozenDeepOcean
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))

			.with(FeatureKey.WOODLAND_MANSION_LOCATION_CHECKER, VersionFeature.bind(features ->
				VersionFeature.constant(
					new WoodlandMansionLocationChecker(
						getWorldSeed(features),
						features.get(FeatureKey.BIOME_DATA_ORACLE),
						features.get(VALID_BIOMES_FOR_STRUCTURE_WOODLAND_MANSION)
					)
				)))
			.with(VALID_BIOMES_FOR_STRUCTURE_WOODLAND_MANSION, VersionFeature.<Integer> listBuilder()
				.init().sinceExtend(RecognisedVersion._16w43a, // Actually 16w39a, but version strings are identical
					DefaultBiomes.roofedForest,
					DefaultBiomes.roofedForestM
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))

			.with(FeatureKey.OCEAN_RUINS_LOCATION_CHECKER, scatteredFeature(
				MAX_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS,
				MIN_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS,
				VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_RUINS,
				SEED_FOR_STRUCTURE_OCEAN_RUINS))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_RUINS, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._18w09a,
					DefaultBiomes.ocean,
					DefaultBiomes.deepOcean,
					DefaultBiomes.coldOcean,
					DefaultBiomes.coldDeepOcean,
					DefaultBiomes.warmOcean,
					DefaultBiomes.warmDeepOcean,
					DefaultBiomes.lukewarmOcean,
					DefaultBiomes.lukewarmDeepOcean,
					DefaultBiomes.frozenOcean,
					DefaultBiomes.frozenDeepOcean
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(MIN_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS, VersionFeature.<Byte> builder()
				.init(
					(byte) 8
				).construct())
			.with(MAX_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS, VersionFeature.<Byte> builder()
				.init(
					(byte) 16
				).since(RecognisedVersion._20w06a,
					(byte) 20
				).construct())
			.with(SEED_FOR_STRUCTURE_OCEAN_RUINS, VersionFeature.<Long> builder()
				.init(
						14357621L
				).construct())

			.with(FeatureKey.SHIPWRECK_LOCATION_CHECKER, scatteredFeature(
				MAX_DISTANCE_SCATTERED_FEATURES_SHIPWRECK,
				MIN_DISTANCE_SCATTERED_FEATURES_SHIPWRECK,
				VALID_BIOMES_AT_MIDDLE_OF_CHUNK_SHIPWRECK,
				SEED_FOR_STRUCTURE_SHIPWRECK))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_SHIPWRECK, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._18w11a,
					DefaultBiomes.beach,
					DefaultBiomes.coldBeach,
					DefaultBiomes.ocean,
					DefaultBiomes.deepOcean,
					DefaultBiomes.coldOcean,
					DefaultBiomes.coldDeepOcean,
					DefaultBiomes.warmOcean,
					DefaultBiomes.warmDeepOcean,
					DefaultBiomes.lukewarmOcean,
					DefaultBiomes.lukewarmDeepOcean,
					DefaultBiomes.frozenOcean,
					DefaultBiomes.frozenDeepOcean
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(MAX_DISTANCE_SCATTERED_FEATURES_SHIPWRECK, VersionFeature.<Byte> builder()
				.init(
					(byte) 15
				).since(RecognisedVersion._1_13_pre7,
					(byte) 16
				).since(RecognisedVersion._20w06a,
					(byte) 24
				).construct())
			.with(MIN_DISTANCE_SCATTERED_FEATURES_SHIPWRECK, VersionFeature.<Byte> builder()
				.init(
					(byte) 8
				).since(RecognisedVersion._20w06a,
					(byte) 4
				).construct())
			.with(SEED_FOR_STRUCTURE_SHIPWRECK, VersionFeature.<Long> builder()
				.init(
						165745295L
				).construct())

			.with(FeatureKey.BURIED_TREASURE_LOCATION_CHECKER, VersionFeature.bind(features ->
				VersionFeature.constant(
					new BuriedTreasureLocationChecker(
						getWorldSeed(features),
						features.get(FeatureKey.BIOME_DATA_ORACLE),
						features.get(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_BURIED_TREASURE),
						features.get(SEED_FOR_STRUCTURE_BURIED_TREASURE)
					)
				)))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_BURIED_TREASURE, VersionFeature.<Integer> listBuilder()
				.init().sinceExtend(RecognisedVersion._18w10d,
					DefaultBiomes.beach,
					DefaultBiomes.coldBeach
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(SEED_FOR_STRUCTURE_BURIED_TREASURE, VersionFeature.<Long> builder()
				.init(
						10387320L
				).construct())

			.with(BUGGY_STRUCTURE_COORDINATE_MATH, VersionFeature.<Boolean> builder()
				.init(
						false
				).since(RecognisedVersion._18w06a,
						true  // Bug MC-131462.
				).since(RecognisedVersion._1_13_pre4,
						false
				).since(RecognisedVersion._1_13_pre7,
						true  // Bug MC-131462, again.
				).since(RecognisedVersion._18w30b,
						false
				).construct());

	private static List<Integer> getValidBiomesForStrongholdSinceV13w36a(BiomeList biomeList) {
		List<Integer> result = new ArrayList<>();
		for (Biome biome : biomeList.iterable()) {
			if (biome.getType().getBiomeDepth() > 0) {
				result.add(biome.getId());
			}
		}
		return result;
	}

	private static List<Biome> makeBiomeList(VersionFeatures features, List<Integer> biomeIds) {
		BiomeList biomeList = features.get(FeatureKey.BIOME_LIST);
		List<Biome> biomes = biomeIds.stream()
			.flatMap(id -> {
				try {
					return Stream.of(biomeList.getById(id));
				} catch (UnknownBiomeIdException e) {
					AmidstLogger.warn("Unknown biome id found in version features: " + id + ", skipping");
					return Stream.empty();
				}
			})
			.collect(Collectors.toList());
		return Collections.unmodifiableList(biomes);
	}

	private static long getWorldSeed(VersionFeatures features) {
		return features.get(FeatureKey.WORLD_OPTIONS).getWorldSeed().getLong();
	}

	private static VersionFeature<LocationChecker> scatteredFeature(
			FeatureKey<Byte> maxDistance, FeatureKey<Byte> minDistance,
			FeatureKey<List<Biome>> validBiomes, FeatureKey<Long> structSeed) {
		return VersionFeature.bind(features ->
			VersionFeature.constant(
				new ScatteredFeaturesLocationChecker(
					getWorldSeed(features),
					features.get(FeatureKey.BIOME_DATA_ORACLE),
					features.get(maxDistance), features.get(minDistance),
					validBiomes == null ? null : features.get(validBiomes),
					features.get(structSeed), features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
				)
			)
		);
	}

	private static VersionFeature<LocationChecker> scatteredFeature(
			FeatureKey<List<Biome>> validBiomes, FeatureKey<Long> structSeed) {
		return VersionFeature.bind(features ->
			VersionFeature.constant(
				new ScatteredFeaturesLocationChecker(
					getWorldSeed(features),
					features.get(FeatureKey.BIOME_DATA_ORACLE),
					validBiomes == null ? null : features.get(validBiomes),
					features.get(structSeed), features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
				)
			)
		);
	}
}
