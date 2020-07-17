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
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.BiomeList;
import amidst.mojangapi.world.biome.UnknownBiomeIdException;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.BuriedTreasureLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_ChanceBased;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_Original;
import amidst.mojangapi.world.icon.locationchecker.StructureBiomeLocationChecker;
import amidst.mojangapi.world.icon.producer.CachedWorldIconProducer;
import amidst.mojangapi.world.icon.producer.NetherFortressProducer_Original;
import amidst.mojangapi.world.icon.producer.OceanMonumentProducer_Fixed;
import amidst.mojangapi.world.icon.producer.RegionalStructureProducer;
import amidst.mojangapi.world.icon.producer.ScatteredFeaturesProducer;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_128Algorithm;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_Buggy128Algorithm;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_Original;
import amidst.mojangapi.world.icon.producer.VillageProducer;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.EndCityWorldIconTypeProvider;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.mojangapi.world.icon.type.WorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.EndIsland;
import amidst.mojangapi.world.oracle.EndIslandOracle;
import amidst.mojangapi.world.oracle.HeuristicWorldSpawnOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;

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
	private static final FeatureKey<MinecraftInterface.World> MINECRAFT_WORLD                = FeatureKey.make();
	public static final FeatureKey<List<Biome>>    SPAWN_VALID_BIOMES                        = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   STRONGHOLD_VALID_MIDDLE_CHUNK_BIOMES      = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   VILLAGE_VALID_BIOMES                      = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   PILLAGER_OUTPOST_VALID_BIOMES             = FeatureKey.make();
	private static final FeatureKey<Boolean>       DO_COMPLEX_VILLAGE_CHECK                  = FeatureKey.make();
	private static final FeatureKey<Integer>       OUTPOST_VILLAGE_AVOID_DISTANCE            = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   DESERT_TEMPLE_VALID_MIDDLE_CHUNK_BIOMES   = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   IGLOO_VALID_MIDDLE_CHUNK_BIOMES           = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   JUNGLE_TEMPLE_VALID_MIDDLE_CHUNK_BIOMES   = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   WITCH_HUT_VALID_MIDDLE_CHUNK_BIOMES       = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   OCEAN_RUINS_VALID_MIDDLE_CHUNK_BIOMES     = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   SHIPWRECK_VALID_MIDDLE_CHUNK_BIOMES       = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   OCEAN_MONUMENT_VALID_MIDDLE_CHUNK_BIOMES  = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   BURIED_TREASURE_VALID_MIDDLE_CHUNK_BIOMES = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   OCEAN_MONUMENT_VALID_BIOMES               = FeatureKey.make();
	private static final FeatureKey<List<Biome>>   WOODLAND_MANSION_VALID_BIOMES             = FeatureKey.make();
	private static final FeatureKey<Long>          DESERT_TEMPLE_SALT                        = FeatureKey.make();
	private static final FeatureKey<Long>          IGLOO_SALT                                = FeatureKey.make();
	private static final FeatureKey<Long>          JUNGLE_TEMPLE_SALT                        = FeatureKey.make();
	private static final FeatureKey<Long>          WITCH_HUT_SALT                            = FeatureKey.make();
	private static final FeatureKey<Long>          OCEAN_RUINS_SALT                          = FeatureKey.make();
	private static final FeatureKey<Long>          SHIPWRECK_SALT                            = FeatureKey.make();
	private static final FeatureKey<Long>          BURIED_TREASURE_SALT                      = FeatureKey.make();
	private static final FeatureKey<Long>          NETHER_FORTRESS_SALT                      = FeatureKey.make();
	private static final FeatureKey<Byte>          SHIPWRECK_SPACING                         = FeatureKey.make();
	private static final FeatureKey<Byte>          SHIPWRECK_SEPARATION                      = FeatureKey.make();
	private static final FeatureKey<Byte>          OCEAN_RUINS_SPACING                       = FeatureKey.make();
	private static final FeatureKey<Byte>          OCEAN_RUINS_SEPARATION                    = FeatureKey.make();
	private static final FeatureKey<Byte>          NETHER_FORTRESS_SPACING                   = FeatureKey.make();
	private static final FeatureKey<Byte>          NETHER_FORTRESS_SEPARATION                = FeatureKey.make();
	private static final FeatureKey<Boolean>       BUGGY_STRUCTURE_COORDINATE_MATH           = FeatureKey.make();

	private static final VersionFeatures.Builder FEATURES_BUILDER = VersionFeatures.builder()
			.with(FeatureKey.BIOME_DATA_ORACLE, (recognisedVersion, features) -> new BiomeDataOracle(
				features.get(MINECRAFT_WORLD),
				recognisedVersion,
				features.get(FeatureKey.BIOME_LIST)
			))
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
					LayerIds.OCEAN_FEATURES
				).construct())

			.with(FeatureKey.BIOME_LIST, DefaultBiomes.DEFAULT_BIOMES)
			.with(FeatureKey.END_ISLAND_ORACLE, VersionFeature.bind(features ->
				VersionFeature.constant(EndIslandOracle.from(getWorldSeed(features)))
			))

			.with(FeatureKey.SLIME_CHUNK_ORACLE, VersionFeature.bind(features ->
				VersionFeature.constant(new SlimeChunkOracle(getWorldSeed(features)))
			))

			.with(FeatureKey.WORLD_SPAWN_ORACLE, VersionFeature.bind(features ->
				VersionFeature.constant(
					new HeuristicWorldSpawnOracle(
						getWorldSeed(features),
						features.get(FeatureKey.BIOME_DATA_ORACLE),
						features.get(SPAWN_VALID_BIOMES))
				)))
			.with(SPAWN_VALID_BIOMES, VersionFeature.<Integer> listBuilder()
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

			.with(FeatureKey.NETHER_FORTRESS_PRODUCER, (version, features) -> {
				if(RecognisedVersion.isOlder(version, RecognisedVersion._20w16a)) {
					return new NetherFortressProducer_Original(getWorldSeed(features));
				} else {
					// TODO: add Nether biome checks when we implement Nether biomes
					// Bastions replace Fortresses in Warped/Crimson Forest
					// EDIT: THIS IS NOT TRUE, it instead does a random check beforehand
					// that we'll have to implement. Also the biome restrictions are
					// different, but I can't remember exactly what biomes.
					return scatteredFeature(
						NETHER_FORTRESS_SPACING,
						NETHER_FORTRESS_SEPARATION,
						null,
						NETHER_FORTRESS_SALT
					).getValue(version, features);
				}
			})
			.with(NETHER_FORTRESS_SALT, VersionFeature.constant(30084232L))
			.with(NETHER_FORTRESS_SPACING, VersionFeature.<Byte> builder()
				.init(
					(byte) 24
				).since(RecognisedVersion._20w19a,
					(byte) 30
				).since(RecognisedVersion._1_16_pre3,
					(byte) 27
				).construct()
			)
			.with(NETHER_FORTRESS_SEPARATION, VersionFeature.constant((byte) 4))

			.with(FeatureKey.END_CITY_PRODUCER, VersionFeature.bind(features ->
				VersionFeature.constant(
					new RegionalStructureProducer<List<EndIsland>>(
							Resolution.CHUNK,
							8,
							null,
							new EndCityWorldIconTypeProvider(),
							Dimension.END,
							false,
							getWorldSeed(features),
							10387313L,
							(byte) 20,
							(byte) 11,
							true
						)
				)
			))

			.with(FeatureKey.MINESHAFT_PRODUCER, VersionFeature.bind(features ->
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
				List<Biome> validBiomes = features.get(STRONGHOLD_VALID_MIDDLE_CHUNK_BIOMES);
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
			.with(STRONGHOLD_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
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


			.with(FeatureKey.VILLAGE_PRODUCER, VersionFeature.bind(features ->
				VersionFeature.constant(
					new VillageProducer(
						getWorldSeed(features),
						features.get(FeatureKey.BIOME_DATA_ORACLE),
						features.get(VILLAGE_VALID_BIOMES),
						features.get(DO_COMPLEX_VILLAGE_CHECK)
					)
				)))
			.with(VILLAGE_VALID_BIOMES, VersionFeature.<Integer> listBuilder()
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

			.with(FeatureKey.PILLAGER_OUTPOST_PRODUCER, VersionFeature.bind(features ->
				VersionFeature.constant(
					new PillagerOutpostProducer(
						getWorldSeed(features),
						features.get(FeatureKey.BIOME_DATA_ORACLE),
						features.get(FeatureKey.VILLAGE_PRODUCER),
						features.get(OUTPOST_VILLAGE_AVOID_DISTANCE),
						features.get(PILLAGER_OUTPOST_VALID_BIOMES)
					)
				)))
			.with(PILLAGER_OUTPOST_VALID_BIOMES, VersionFeature.<Integer> listBuilder()
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

			.with(FeatureKey.DESERT_TEMPLE_PRODUCER, scatteredFeature(
				DESERT_TEMPLE_VALID_MIDDLE_CHUNK_BIOMES,
				DESERT_TEMPLE_SALT))
			.with(DESERT_TEMPLE_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init(
					DefaultBiomes.desert
				).sinceExtend(RecognisedVersion._12w01a,
					DefaultBiomes.desertHills
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(DESERT_TEMPLE_SALT, VersionFeature.<Long> builder()
				.init(
					14357617L
				).construct())

			.with(FeatureKey.IGLOO_PRODUCER, scatteredFeature(
				IGLOO_VALID_MIDDLE_CHUNK_BIOMES,
				IGLOO_SALT))
			.with(IGLOO_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._15w43c,
					DefaultBiomes.icePlains,
					DefaultBiomes.coldTaiga
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))

			.with(IGLOO_SALT, VersionFeature.<Long> builder()
				.init(
					14357617L
				).since(RecognisedVersion._18w06a,
					14357618L
				).construct())

			.with(FeatureKey.JUNGLE_TEMPLE_PRODUCER, scatteredFeature(
				JUNGLE_TEMPLE_VALID_MIDDLE_CHUNK_BIOMES,
				JUNGLE_TEMPLE_SALT))
			.with(JUNGLE_TEMPLE_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._12w22a,
					DefaultBiomes.jungle
				).sinceExtend(RecognisedVersion._1_4_2,
					DefaultBiomes.jungleHills // TODO: jungle temples spawn only since 1.4.2 in jungle hills?
				).sinceExtend(RecognisedVersion._19w06a,
					DefaultBiomes.bambooJungle,
					DefaultBiomes.bambooJungleHills
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(JUNGLE_TEMPLE_SALT,  VersionFeature.<Long> builder()
				.init(
						14357617L
				).since(RecognisedVersion._18w06a,
						14357619L
				).construct())

			.with(FeatureKey.WITCH_HUT_PRODUCER, scatteredFeature(
				WITCH_HUT_VALID_MIDDLE_CHUNK_BIOMES,
				WITCH_HUT_SALT))
			.with(WITCH_HUT_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._1_4_2,
					DefaultBiomes.swampland
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(WITCH_HUT_SALT, VersionFeature.<Long> builder()
				.init(
						14357617L
				).since(RecognisedVersion._18w06a,
						14357620L
				).construct())

			.with(FeatureKey.OCEAN_MONUMENT_PRODUCER, VersionFeature.bind(features -> {
				long worldSeed = getWorldSeed(features);
				BiomeDataOracle biomeOracle = features.get(FeatureKey.BIOME_DATA_ORACLE);
				List<Biome> validCenterBiomes = features.get(OCEAN_MONUMENT_VALID_MIDDLE_CHUNK_BIOMES);
				List<Biome> validBiomes = features.get(OCEAN_MONUMENT_VALID_BIOMES);
				return VersionFeature.<LocationChecker> builder()
					.init(
						new OceanMonumentLocationChecker_Original(worldSeed, biomeOracle, validCenterBiomes, validBiomes)
					).since(RecognisedVersion._15w46a,
						new OceanMonumentProducer_Fixed(worldSeed, biomeOracle, validCenterBiomes, validBiomes)
					).construct();
			}))
			.with(OCEAN_MONUMENT_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._1_8,
					DefaultBiomes.deepOcean
				).sinceExtend(RecognisedVersion._18w08a,
					DefaultBiomes.coldDeepOcean,
					DefaultBiomes.warmDeepOcean,
					DefaultBiomes.lukewarmDeepOcean,
					DefaultBiomes.frozenDeepOcean
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(OCEAN_MONUMENT_VALID_BIOMES, VersionFeature.<Integer> listBuilder()
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

			.with(FeatureKey.WOODLAND_MANSION_PRODUCER, VersionFeature.bind(features ->
				VersionFeature.constant(
					new RegionalStructureProducer<>(
						Resolution.CHUNK,
						8,
						new StructureBiomeLocationChecker(features.get(FeatureKey.BIOME_DATA_ORACLE), 32, features.get(WOODLAND_MANSION_VALID_BIOMES)),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WOODLAND_MANSION),
						Dimension.OVERWORLD,
						false,
						getWorldSeed(features),
						10387319L,
						(byte) 80,
						(byte) 20,
						true
					)
				)))
			.with(WOODLAND_MANSION_VALID_BIOMES, VersionFeature.<Integer> listBuilder()
				.init().sinceExtend(RecognisedVersion._16w43a, // Actually 16w39a, but version strings are identical
					DefaultBiomes.roofedForest,
					DefaultBiomes.roofedForestM
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))

			.with(FeatureKey.OCEAN_RUINS_PRODUCER, scatteredFeature(
				OCEAN_RUINS_SPACING,
				OCEAN_RUINS_SEPARATION,
				OCEAN_RUINS_VALID_MIDDLE_CHUNK_BIOMES,
				OCEAN_RUINS_SALT))
			.with(OCEAN_RUINS_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
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
			.with(OCEAN_RUINS_SEPARATION, VersionFeature.<Byte> builder()
				.init(
					(byte) 8
				).construct())
			.with(OCEAN_RUINS_SPACING, VersionFeature.<Byte> builder()
				.init(
					(byte) 16
				).since(RecognisedVersion._20w06a,
					(byte) 20
				).construct())
			.with(OCEAN_RUINS_SALT, VersionFeature.<Long> builder()
				.init(
						14357621L
				).construct())

			.with(FeatureKey.SHIPWRECK_PRODUCER, scatteredFeature(
				SHIPWRECK_SPACING,
				SHIPWRECK_SEPARATION,
				SHIPWRECK_VALID_MIDDLE_CHUNK_BIOMES,
				SHIPWRECK_SALT))
			.with(SHIPWRECK_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
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
			.with(SHIPWRECK_SPACING, VersionFeature.<Byte> builder()
				.init(
					(byte) 15
				).since(RecognisedVersion._1_13_pre7,
					(byte) 16
				).since(RecognisedVersion._20w06a,
					(byte) 24
				).construct())
			.with(SHIPWRECK_SEPARATION, VersionFeature.<Byte> builder()
				.init(
					(byte) 8
				).since(RecognisedVersion._20w06a,
					(byte) 4
				).construct())
			.with(SHIPWRECK_SALT, VersionFeature.<Long> builder()
				.init(
						165745295L
				).construct())

			.with(FeatureKey.BURIED_TREASURE_PRODUCER, VersionFeature.bind(features ->
				VersionFeature.constant(
					new BuriedTreasureLocationChecker(
						getWorldSeed(features),
						features.get(FeatureKey.BIOME_DATA_ORACLE),
						features.get(BURIED_TREASURE_VALID_MIDDLE_CHUNK_BIOMES),
						features.get(SEED_FOR_STRUCTURE_BURIED_TREASURE)
					)
				)))
			.with(BURIED_TREASURE_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init().sinceExtend(RecognisedVersion._18w10d,
					DefaultBiomes.beach,
					DefaultBiomes.coldBeach
				).construct().andThenBind(DefaultVersionFeatures::makeBiomeList))
			.with(BURIED_TREASURE_SALT, VersionFeature.<Long> builder()
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

	private static VersionFeature<WorldIconProducer<Void>> scatteredFeature(
			Resolution resolution,
			int offsetInWorld,
			FeatureKey<List<Biome>> validBiomes,
			WorldIconTypeProvider<Void> provider,
			Dimension dimension,
			boolean displayDimension,
			FeatureKey<Long> salt,
			FeatureKey<Byte> spacing,
			FeatureKey<Byte> separation) {
		return VersionFeature.bind(features ->
			VersionFeature.constant(
				new ScatteredFeaturesProducer(
					resolution,
					offsetInWorld,
					features.get(FeatureKey.BIOME_DATA_ORACLE),
					validBiomes == null ? null : features.get(validBiomes),
					provider,
					dimension,
					displayDimension,
					getWorldSeed(features),
					features.get(salt),
					features.get(spacing),
					features.get(separation),
					features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
				)
			)
		);
	}

	private static VersionFeature<WorldIconProducer<Void>> scatteredFeature(
			Resolution resolution,
			int offsetInWorld,
			FeatureKey<List<Biome>> validBiomes,
			WorldIconTypeProvider<Void> provider,
			Dimension dimension,
			boolean displayDimension,
			FeatureKey<Long> salt) {
		return VersionFeature.bind(features ->
			VersionFeature.constant(
				new ScatteredFeaturesProducer(
					resolution,
					offsetInWorld,
					features.get(FeatureKey.BIOME_DATA_ORACLE),
					validBiomes == null ? null : features.get(validBiomes),
					provider,
					dimension,
					displayDimension,
					getWorldSeed(features),
					features.get(salt),
					features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
				)
			)
		);
	}
}
