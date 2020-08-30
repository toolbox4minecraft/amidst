package amidst.mojangapi.world.versionfeatures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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
import amidst.mojangapi.world.icon.producer.BastionRemnantProducer;
import amidst.mojangapi.world.icon.producer.CachedWorldIconProducer;
import amidst.mojangapi.world.icon.producer.ChunkStructureProducer;
import amidst.mojangapi.world.icon.producer.NetherFortressProducer_Original;
import amidst.mojangapi.world.icon.producer.NetherFortressProducer_Scattered;
import amidst.mojangapi.world.icon.producer.NoopProducer;
import amidst.mojangapi.world.icon.producer.OceanMonumentProducer_Fixed;
import amidst.mojangapi.world.icon.producer.OceanMonumentProducer_Original;
import amidst.mojangapi.world.icon.producer.PillagerOutpostProducer;
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
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.EndIsland;
import amidst.mojangapi.world.oracle.EndIslandOracle;
import amidst.mojangapi.world.oracle.HeuristicWorldSpawnOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;
import amidst.util.FastRand;

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
	private static final FeatureKey<MinecraftInterface.World> MINECRAFT_WORLD                  = FeatureKey.make();
	public static final FeatureKey<List<Biome>>      SPAWN_VALID_BIOMES                        = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     STRONGHOLD_VALID_MIDDLE_CHUNK_BIOMES      = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     VILLAGE_VALID_BIOMES                      = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     PILLAGER_OUTPOST_VALID_BIOMES             = FeatureKey.make();
	private static final FeatureKey<Boolean>         DO_COMPLEX_VILLAGE_CHECK                  = FeatureKey.make();
	private static final FeatureKey<Integer>         OUTPOST_VILLAGE_AVOID_DISTANCE            = FeatureKey.make();
	private static final FeatureKey<Boolean>         OUTPOST_USE_CHECKED_VILLAGES              = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     DESERT_TEMPLE_VALID_MIDDLE_CHUNK_BIOMES   = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     IGLOO_VALID_MIDDLE_CHUNK_BIOMES           = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     JUNGLE_TEMPLE_VALID_MIDDLE_CHUNK_BIOMES   = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     WITCH_HUT_VALID_MIDDLE_CHUNK_BIOMES       = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     OCEAN_RUINS_VALID_MIDDLE_CHUNK_BIOMES     = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     SHIPWRECK_VALID_MIDDLE_CHUNK_BIOMES       = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     OCEAN_MONUMENT_VALID_MIDDLE_CHUNK_BIOMES  = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     BURIED_TREASURE_VALID_MIDDLE_CHUNK_BIOMES = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     BASTION_REMNANT_VALID_MIDDLE_CHUNK_BIOMES = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     OCEAN_MONUMENT_VALID_BIOMES               = FeatureKey.make();
	private static final FeatureKey<List<Biome>>     WOODLAND_MANSION_VALID_BIOMES             = FeatureKey.make();
	private static final FeatureKey<LocationChecker> MINESHAFT_LOCATION_CHECKER                = FeatureKey.make();
	private static final FeatureKey<Long>            DESERT_TEMPLE_SALT                        = FeatureKey.make();
	private static final FeatureKey<Long>            IGLOO_SALT                                = FeatureKey.make();
	private static final FeatureKey<Long>            JUNGLE_TEMPLE_SALT                        = FeatureKey.make();
	private static final FeatureKey<Long>            WITCH_HUT_SALT                            = FeatureKey.make();
	private static final FeatureKey<Long>            OCEAN_RUINS_SALT                          = FeatureKey.make();
	private static final FeatureKey<Long>            SHIPWRECK_SALT                            = FeatureKey.make();
	private static final FeatureKey<Long>            BURIED_TREASURE_SALT                      = FeatureKey.make();
	private static final FeatureKey<Long>            NETHER_BUILDING_SALT                      = FeatureKey.make();
	private static final FeatureKey<Byte>            SHIPWRECK_SPACING                         = FeatureKey.make();
	private static final FeatureKey<Byte>            SHIPWRECK_SEPARATION                      = FeatureKey.make();
	private static final FeatureKey<Byte>            OCEAN_RUINS_SPACING                       = FeatureKey.make();
	private static final FeatureKey<Byte>            OCEAN_RUINS_SEPARATION                    = FeatureKey.make();
	private static final FeatureKey<Byte>            NETHER_BUILDING_SPACING                   = FeatureKey.make();
	private static final FeatureKey<Byte>            NETHER_BUILDING_SEPARATION                = FeatureKey.make();
	private static final FeatureKey<Function<FastRand, Boolean>> NETHER_FORTRESS_FUNCTION         = FeatureKey.make();
	private static final FeatureKey<Function<FastRand, Boolean>> BASTION_REMNANT_FUNCTION         = FeatureKey.make();
	private static final FeatureKey<Boolean>         BUGGY_STRUCTURE_COORDINATE_MATH           = FeatureKey.make();
	private static final FeatureKey<Boolean>         BIOME_DATA_ORACLE_QUARTER_RES_OVERRIDE    = FeatureKey.make();
	private static final FeatureKey<Boolean>         BIOME_DATA_ORACLE_ACCURATE_LOCATION_COUNT = FeatureKey.make();
	private static final FeatureKey<Integer>         BIOME_DATA_ORACLE_MIDDLE_OF_CHUNK_OFFSET  = FeatureKey.make();

	private static final VersionFeatures.Builder FEATURES_BUILDER = VersionFeatures.builder()
			.with(FeatureKey.OVERWORLD_BIOME_DATA_ORACLE,
				VersionFeature.fixed(features -> new BiomeDataOracle(
					features.get(MINECRAFT_WORLD),
					Dimension.OVERWORLD,
					features.get(FeatureKey.BIOME_LIST),
					getBiomeOracleConfig(features)
				))
			)
			.with(FeatureKey.NETHER_BIOME_DATA_ORACLE, VersionFeature.fixed(features -> {
				MinecraftInterface.World world = features.get(MINECRAFT_WORLD);
				if (world.supportedDimensions().contains(Dimension.NETHER)) {
					return Optional.of(new BiomeDataOracle(
						features.get(MINECRAFT_WORLD),
						Dimension.NETHER,
						features.get(FeatureKey.BIOME_LIST),
						getBiomeOracleConfig(features)
					));
				} else {
					return Optional.empty();
				}
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
					LayerIds.NETHER_FEATURES
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
			.with(FeatureKey.END_ISLAND_ORACLE, VersionFeature.fixed(features ->
				EndIslandOracle.from(getWorldSeed(features))
			))

			.with(FeatureKey.SLIME_CHUNK_ORACLE, VersionFeature.fixed(features ->
				new SlimeChunkOracle(getWorldSeed(features))
			))

			.with(FeatureKey.WORLD_SPAWN_ORACLE, VersionFeature.fixed(features ->
				new HeuristicWorldSpawnOracle(
					getWorldSeed(features),
					getBiomeOracle(features, Dimension.OVERWORLD),
					features.get(SPAWN_VALID_BIOMES))
				))
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
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))

			.with(FeatureKey.NETHER_FORTRESS_PRODUCER, VersionFeature.<WorldIconProducer<Void>> builder()
				.init(new NoopProducer<>())
				.since(RecognisedVersion._b1_9_pre1,
					VersionFeature.fixed(features -> new NetherFortressProducer_Original(getWorldSeed(features)))
				).since(RecognisedVersion._20w16a,
					VersionFeature.fixed(features ->
						NetherFortressProducer_Scattered.create(
							getWorldSeed(features),
							features.get(NETHER_BUILDING_SALT),
							features.get(NETHER_BUILDING_SPACING),
							features.get(NETHER_BUILDING_SEPARATION),
							features.get(BUGGY_STRUCTURE_COORDINATE_MATH),
							features.get(NETHER_FORTRESS_FUNCTION)
						)
					)
				).construct())
			.with(NETHER_FORTRESS_FUNCTION, VersionFeature.<Function<FastRand, Boolean>> builder()
				.init(
					r -> r.nextInt(6) < 2
				).since(RecognisedVersion._1_16_pre3,
					r -> r.nextInt(5) < 2
				).construct())

			.with(FeatureKey.BASTION_REMNANT_PRODUCER, VersionFeature.<WorldIconProducer<Void>> builder()
				.init(new NoopProducer<>())
				.since(RecognisedVersion._20w16a,
					VersionFeature.fixed(features ->
						BastionRemnantProducer.create(
							getWorldSeed(features),
							getBiomeOracle(features, Dimension.NETHER),
							features.get(BASTION_REMNANT_VALID_MIDDLE_CHUNK_BIOMES),
							features.get(NETHER_BUILDING_SALT),
							features.get(NETHER_BUILDING_SPACING),
							features.get(NETHER_BUILDING_SEPARATION),
							features.get(BUGGY_STRUCTURE_COORDINATE_MATH),
							features.get(BASTION_REMNANT_FUNCTION)
						)
					)
				).construct())
			.with(BASTION_REMNANT_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._20w16a,
					DefaultBiomes.hell,
					DefaultBiomes.soulSandValley,
					DefaultBiomes.crimsonForest,
					DefaultBiomes.warpedForest
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))
			.with(BASTION_REMNANT_FUNCTION, VersionFeature.<Function<FastRand, Boolean>> builder()
				.init(
					r -> r.nextInt(6) >= 2
				).since(RecognisedVersion._1_16_pre3,
					r -> r.nextInt(5) >= 2
				).construct())

			.with(NETHER_BUILDING_SALT, VersionFeature.constant(30084232L))
			.with(NETHER_BUILDING_SPACING, VersionFeature.<Byte> builder()
				.init(
					(byte) 24
				).since(RecognisedVersion._20w19a,
					(byte) 30
				).since(RecognisedVersion._1_16_pre3,
					(byte) 27
				).construct()
			)
			.with(NETHER_BUILDING_SEPARATION, VersionFeature.constant((byte) 4))

			.with(FeatureKey.END_CITY_PRODUCER, VersionFeature.<WorldIconProducer<List<EndIsland>>> builder()
				.init(new NoopProducer<>())
				.since(RecognisedVersion._15w31c,
					VersionFeature.fixed(features ->
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
							true,
							features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
						)
					)
				).construct())

			.with(MINESHAFT_LOCATION_CHECKER, VersionFeature.<LocationChecker> builder()
				.init( // Actually starts at beta 1.8
					VersionFeature.fixed(features -> new MineshaftAlgorithm_Original(getWorldSeed(features)))
				).since(RecognisedVersion._1_4_2,
					VersionFeature.fixed(features -> new MineshaftAlgorithm_ChanceBased(getWorldSeed(features), 0.01D, true))
				).since(RecognisedVersion._1_7_2,
					VersionFeature.fixed(features -> new MineshaftAlgorithm_ChanceBased(getWorldSeed(features), 0.004D, true))
				).since(RecognisedVersion._18w06a,
					VersionFeature.fixed(features -> new MineshaftAlgorithm_ChanceBased(getWorldSeed(features), 0.01D, false))
				).construct())
			.with(FeatureKey.MINESHAFT_PRODUCER, VersionFeature.fixed(features ->
					new ChunkStructureProducer<>(
						Resolution.CHUNK,
						8,
						features.get(MINESHAFT_LOCATION_CHECKER),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.MINESHAFT),
						Dimension.OVERWORLD,
						false
					)
				))

			.with(FeatureKey.STRONGHOLD_PRODUCER, VersionFeature.<CachedWorldIconProducer>builder()
				.init( // Actually starts at beta 1.8, with a single stronghold per world
					VersionFeature.fixed(features -> new StrongholdProducer_Original(
						getWorldSeed(features), getBiomeOracle(features, Dimension.OVERWORLD),
						features.get(STRONGHOLD_VALID_MIDDLE_CHUNK_BIOMES)
					))
				).since(RecognisedVersion._15w43c,
					// this should be 15w43a, which is not recognised
					VersionFeature.fixed(features -> new StrongholdProducer_Buggy128Algorithm(
						getWorldSeed(features), getBiomeOracle(features, Dimension.OVERWORLD),
						features.get(STRONGHOLD_VALID_MIDDLE_CHUNK_BIOMES)
					))
				).since(RecognisedVersion._1_9_pre2,
					// this should be 16w06a
					VersionFeature.fixed(features -> new StrongholdProducer_128Algorithm(
						getWorldSeed(features), getBiomeOracle(features, Dimension.OVERWORLD),
						features.get(STRONGHOLD_VALID_MIDDLE_CHUNK_BIOMES)
					))
				)
				.construct())
			.with(STRONGHOLD_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init(
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
				).since(RecognisedVersion._13w36a, VersionFeature.fixed(features ->
					// this includes all the biomes above, except for the swampland
					getValidBiomesForStrongholdSinceV13w36a(features.get(FeatureKey.BIOME_LIST))
				)).sinceExtend(RecognisedVersion._18w06a,
					DefaultBiomes.mushroomIslandShore
				).sinceRemove(RecognisedVersion._20w21a, // MC-199298
					DefaultBiomes.bambooJungle,
					DefaultBiomes.bambooJungleHills
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))

			.with(FeatureKey.VILLAGE_PRODUCER, VersionFeature.fixed(features ->
					// Actually starts at beta 1.8
					new VillageProducer(
						getBiomeOracle(features, Dimension.OVERWORLD),
						features.get(VILLAGE_VALID_BIOMES),
						getWorldSeed(features),
						features.get(DO_COMPLEX_VILLAGE_CHECK),
						features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
					)
				))
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
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))
			.with(DO_COMPLEX_VILLAGE_CHECK, VersionFeature.<Boolean> builder()
				.init(
					true
				).since(RecognisedVersion._16w20a,
					false
				).construct())

			.with(FeatureKey.PILLAGER_OUTPOST_PRODUCER, VersionFeature.fixed(features ->
					new PillagerOutpostProducer(
						getBiomeOracle(features, Dimension.OVERWORLD),
						features.get(PILLAGER_OUTPOST_VALID_BIOMES),
						getWorldSeed(features),
						features.get(FeatureKey.VILLAGE_PRODUCER),
						features.get(OUTPOST_VILLAGE_AVOID_DISTANCE),
						features.get(OUTPOST_USE_CHECKED_VILLAGES),
						features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
					)
				))
			.with(PILLAGER_OUTPOST_VALID_BIOMES, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._18w47b,
					DefaultBiomes.plains,
					DefaultBiomes.desert,
					DefaultBiomes.savanna,
					DefaultBiomes.taiga
				).sinceExtend(RecognisedVersion._18w49a,
					DefaultBiomes.icePlains
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))
			.with(OUTPOST_VILLAGE_AVOID_DISTANCE, VersionFeature.<Integer> builder()
				.init(
					-1
				// from 19w11a to 19w13a, outpost towers aren't generated close
				// to villages, but the structure is still reported by `/locate`.
				).since(RecognisedVersion._19w13b,
					10
				).construct())
			.with(OUTPOST_USE_CHECKED_VILLAGES, VersionFeature.<Boolean> builder()
				.init(
					true
				).since(RecognisedVersion._20w21a,
					false
				).construct())

			.with(FeatureKey.DESERT_TEMPLE_PRODUCER, scatteredFeature(
				Resolution.CHUNK,
				8,
				DESERT_TEMPLE_VALID_MIDDLE_CHUNK_BIOMES,
				DefaultWorldIconTypes.DESERT,
				Dimension.OVERWORLD,
				DESERT_TEMPLE_SALT))
			.with(DESERT_TEMPLE_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init(
					DefaultBiomes.desert
				).sinceExtend(RecognisedVersion._12w01a,
					DefaultBiomes.desertHills
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))
			.with(DESERT_TEMPLE_SALT, VersionFeature.<Long> builder()
				.init(
					14357617L
				).construct())

			.with(FeatureKey.IGLOO_PRODUCER, scatteredFeature(
				Resolution.CHUNK,
				8,
				IGLOO_VALID_MIDDLE_CHUNK_BIOMES,
				DefaultWorldIconTypes.IGLOO,
				Dimension.OVERWORLD,
				IGLOO_SALT))
			.with(IGLOO_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._15w43c,
					DefaultBiomes.icePlains,
					DefaultBiomes.coldTaiga
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))

			.with(IGLOO_SALT, VersionFeature.<Long> builder()
				.init(
					14357617L
				).since(RecognisedVersion._18w06a,
					14357618L
				).construct())

			.with(FeatureKey.JUNGLE_TEMPLE_PRODUCER, scatteredFeature(
				Resolution.CHUNK,
				8,
				JUNGLE_TEMPLE_VALID_MIDDLE_CHUNK_BIOMES,
				DefaultWorldIconTypes.JUNGLE,
				Dimension.OVERWORLD,
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
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))
			.with(JUNGLE_TEMPLE_SALT,  VersionFeature.<Long> builder()
				.init(
						14357617L
				).since(RecognisedVersion._18w06a,
						14357619L
				).construct())

			.with(FeatureKey.WITCH_HUT_PRODUCER, scatteredFeature(
				Resolution.CHUNK,
				8,
				WITCH_HUT_VALID_MIDDLE_CHUNK_BIOMES,
				DefaultWorldIconTypes.WITCH,
				Dimension.OVERWORLD,
				WITCH_HUT_SALT))
			.with(WITCH_HUT_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._1_4_2,
					DefaultBiomes.swampland
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))
			.with(WITCH_HUT_SALT, VersionFeature.<Long> builder()
				.init(
						14357617L
				).since(RecognisedVersion._18w06a,
						14357620L
				).construct())

			.with(FeatureKey.OCEAN_MONUMENT_PRODUCER, VersionFeature.<WorldIconProducer<Void>> builder()
				.init(
					VersionFeature.fixed(features -> new OceanMonumentProducer_Original(
						getWorldSeed(features), getBiomeOracle(features, Dimension.OVERWORLD),
						features.get(OCEAN_MONUMENT_VALID_MIDDLE_CHUNK_BIOMES),
						features.get(OCEAN_MONUMENT_VALID_BIOMES),
						features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
					))
				).since(RecognisedVersion._15w46a,
					VersionFeature.fixed(features -> new OceanMonumentProducer_Fixed(
						getWorldSeed(features), getBiomeOracle(features, Dimension.OVERWORLD),
						features.get(OCEAN_MONUMENT_VALID_MIDDLE_CHUNK_BIOMES),
						features.get(OCEAN_MONUMENT_VALID_BIOMES),
						features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
					))
				).construct())
			.with(OCEAN_MONUMENT_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._1_8,
					DefaultBiomes.deepOcean
				).sinceExtend(RecognisedVersion._18w08a,
					DefaultBiomes.coldDeepOcean,
					DefaultBiomes.warmDeepOcean,
					DefaultBiomes.lukewarmDeepOcean,
					DefaultBiomes.frozenDeepOcean
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))
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
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))

			.with(FeatureKey.WOODLAND_MANSION_PRODUCER, VersionFeature.fixed(features ->
					new RegionalStructureProducer<> (
							Resolution.CHUNK,
							8,
							new StructureBiomeLocationChecker(getBiomeOracle(features, Dimension.OVERWORLD), 32, features.get(WOODLAND_MANSION_VALID_BIOMES)),
							new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WOODLAND_MANSION),
							Dimension.OVERWORLD,
							false,
							getWorldSeed(features),
							10387319L,
							(byte) 80,
							(byte) 20,
							true,
							features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
					)
				))
			.with(WOODLAND_MANSION_VALID_BIOMES, VersionFeature.<Integer> listBuilder()
				.init().sinceExtend(RecognisedVersion._16w43a, // Actually 16w39a, but version strings are identical
					DefaultBiomes.roofedForest,
					DefaultBiomes.roofedForestM
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))

			.with(FeatureKey.OCEAN_RUINS_PRODUCER, scatteredFeature(
				Resolution.CHUNK,
				8,
				OCEAN_RUINS_VALID_MIDDLE_CHUNK_BIOMES,
				DefaultWorldIconTypes.OCEAN_RUINS,
				Dimension.OVERWORLD,
				OCEAN_RUINS_SALT,
				OCEAN_RUINS_SPACING,
				OCEAN_RUINS_SEPARATION))
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
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))
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
				Resolution.CHUNK,
				8,
				SHIPWRECK_VALID_MIDDLE_CHUNK_BIOMES,
				DefaultWorldIconTypes.SHIPWRECK,
				Dimension.OVERWORLD,
				SHIPWRECK_SALT,
				SHIPWRECK_SPACING,
				SHIPWRECK_SEPARATION))
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
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))
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

			.with(FeatureKey.BURIED_TREASURE_PRODUCER, VersionFeature.fixed(features ->
					new ChunkStructureProducer<>(
						Resolution.CHUNK,
						9,
						new BuriedTreasureLocationChecker(
								getWorldSeed(features),
								getBiomeOracle(features, Dimension.OVERWORLD),
								features.get(BURIED_TREASURE_VALID_MIDDLE_CHUNK_BIOMES),
								features.get(BURIED_TREASURE_SALT)
						),
						new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.BURIED_TREASURE),
						Dimension.OVERWORLD,
						false
					)
				))
			.with(BURIED_TREASURE_VALID_MIDDLE_CHUNK_BIOMES, VersionFeature.<Integer> listBuilder()
				.init().sinceExtend(RecognisedVersion._18w10d,
					DefaultBiomes.beach,
					DefaultBiomes.coldBeach
				).construct().andThenFixed(DefaultVersionFeatures::makeBiomeList))
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
				).since(RecognisedVersion._18w30b, // actually 18w30a
						false
				).construct());

	private static BiomeDataOracle.Config getBiomeOracleConfig(VersionFeatures features) {
		BiomeDataOracle.Config config = new BiomeDataOracle.Config();
		config.quarterResOverride = features.get(BIOME_DATA_ORACLE_QUARTER_RES_OVERRIDE);
		config.middleOfChunkOffset = features.get(BIOME_DATA_ORACLE_MIDDLE_OF_CHUNK_OFFSET);
		config.accurateLocationCount = features.get(BIOME_DATA_ORACLE_ACCURATE_LOCATION_COUNT);
		return config;
	}

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

	private static BiomeDataOracle getBiomeOracle(VersionFeatures features, Dimension dimension) {
		switch (dimension) {
		case OVERWORLD:
			return features.get(FeatureKey.OVERWORLD_BIOME_DATA_ORACLE);
		case NETHER:
			Optional<BiomeDataOracle> oracle = features.get(FeatureKey.NETHER_BIOME_DATA_ORACLE);
			if (oracle.isPresent()) {
				return oracle.get();
			}
		default:
			throw new RuntimeException("Can't retrieve BiomeDataOracle for dimension " + dimension);
		}
	}

	private static VersionFeature<WorldIconProducer<Void>> scatteredFeature(
			Resolution resolution,
			int offsetInWorld,
			FeatureKey<List<Biome>> validBiomes,
			DefaultWorldIconTypes iconType,
			Dimension dimension,
			FeatureKey<Long> salt,
			FeatureKey<Byte> spacing,
			FeatureKey<Byte> separation) {
		return VersionFeature.fixed(features ->
			new ScatteredFeaturesProducer(
				resolution,
				offsetInWorld,
				getBiomeOracle(features, dimension),
				validBiomes == null ? null : features.get(validBiomes),
				new ImmutableWorldIconTypeProvider(iconType),
				dimension,
				false,
				getWorldSeed(features),
				features.get(salt),
				features.get(spacing),
				features.get(separation),
				features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
			)
		);
	}

	private static VersionFeature<WorldIconProducer<Void>> scatteredFeature(
			Resolution resolution,
			int offsetInWorld,
			FeatureKey<List<Biome>> validBiomes,
			DefaultWorldIconTypes iconType,
			Dimension dimension,
			FeatureKey<Long> salt) {
		return VersionFeature.fixed(features ->
			new ScatteredFeaturesProducer(
				resolution,
				offsetInWorld,
				getBiomeOracle(features, dimension),
				validBiomes == null ? null : features.get(validBiomes),
				new ImmutableWorldIconTypeProvider(iconType),
				dimension,
				false,
				getWorldSeed(features),
				features.get(salt),
				features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
			)
		);
	}
}
