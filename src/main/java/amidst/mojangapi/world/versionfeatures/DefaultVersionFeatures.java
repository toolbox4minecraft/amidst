package amidst.mojangapi.world.versionfeatures;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import amidst.fragment.layer.LayerIds;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.BiomeIdNameMap;
import amidst.mojangapi.world.icon.locationchecker.BuriedTreasureLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.EndCityLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_ChanceBased;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_Original;
import amidst.mojangapi.world.icon.locationchecker.NetherFortressAlgorithm;
import amidst.mojangapi.world.icon.locationchecker.OceanMonumentLocationChecker_Fixed;
import amidst.mojangapi.world.icon.locationchecker.OceanMonumentLocationChecker_Original;
import amidst.mojangapi.world.icon.locationchecker.PillagerOutpostLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.ScatteredFeaturesLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.VillageLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.WoodlandMansionLocationChecker;
import amidst.mojangapi.world.icon.producer.CachedWorldIconProducer;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_128Algorithm;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_Buggy128Algorithm;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_Original;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.EndIslandOracle;
import amidst.mojangapi.world.oracle.HeuristicWorldSpawnOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;

public enum DefaultVersionFeatures {
	;

	public static VersionFeatures.Builder builder(WorldOptions worldOptions, BiomeDataOracle biomeDataOracle) {
		return FEATURES_BUILDER.clone()
			.withValue(FeatureKey.WORLD_OPTIONS, worldOptions)
			.withValue(FeatureKey.BIOME_DATA_ORACLE, biomeDataOracle);
	}

	// @formatter:off
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
	private static final FeatureKey<Byte>          MAX_DISTANCE_SCATTERED_FEATURES_SHIPWRECK       = FeatureKey.make();
	private static final FeatureKey<Byte>          MIN_DISTANCE_SCATTERED_FEATURES_SHIPWRECK       = FeatureKey.make();
	private static final FeatureKey<Byte>          MAX_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS     = FeatureKey.make();
	private static final FeatureKey<Byte>          MIN_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS     = FeatureKey.make();
	private static final FeatureKey<Boolean>       BUGGY_STRUCTURE_COORDINATE_MATH                 = FeatureKey.make();

	private static final VersionFeatures.Builder FEATURES_BUILDER = VersionFeatures.builder()
			.with(FeatureKey.ENABLED_LAYERS, VersionFeature.<Integer> listBuilder()
				.init(
					LayerIds.ALPHA,
					LayerIds.BIOME_DATA,
					LayerIds.BACKGROUND,
					LayerIds.SLIME,
					LayerIds.GRID,
					LayerIds.SPAWN,
					LayerIds.STRONGHOLD,
					LayerIds.PLAYER,
					LayerIds.VILLAGE,
					LayerIds.MINESHAFT,
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
			
			.with(FeatureKey.BIOME_ID_NAME_MAP, VersionFeature.<Integer, String> biMapBuilder()
				.init( // Starts at beta 1.8
					entry(0,  "Ocean"),
					entry(1,  "Plains"),
					entry(2,  "Desert"),
					entry(3,  "Extreme Hills"),
					entry(4,  "Forest"),
					entry(5,  "Taiga"),
					entry(6,  "Swampland"),
					entry(7,  "River"),
					entry(8,  "Hell"),
					entry(9,  "Sky")
				).sinceExtend(RecognisedVersion._b1_9_pre1,
					entry(10, "Frozen Ocean"),
					entry(11, "Frozen River"),
					entry(12, "Ice Plains"),
					entry(13, "Ice Mountains"), // There was a bug causing this biome to not be generated until the next version
					entry(14, "Mushroom Island"),
					entry(15, "Mushroom Island Shore")
				).sinceExtend(RecognisedVersion._1_1, // Closest to 12w01a
					entry(16, "Beach"),
					entry(17, "Desert Hills"),
					entry(18, "Forest Hills"),
					entry(19, "Taiga Hills"),
					entry(20, "Extreme Hills Edge")
				).sinceExtend(RecognisedVersion._12w03a,
					entry(21, "Jungle"),
					entry(22, "Jungle Hills")
				).sinceExtend(RecognisedVersion._13w36a,
					entry(23, "Jungle Edge"),
					entry(24, "Deep Ocean"),
					entry(25, "Stone Beach"),
					entry(26, "Cold Beach"),
					entry(27, "Birch Forest"),
					entry(28, "Birch Forest Hills"),
					entry(29, "Roofed Forest"),
					entry(30, "Cold Taiga"),
					entry(31, "Cold Taiga Hills"),
					entry(32, "Mega Taiga"),
					entry(33, "Mega Taiga Hills"),
					entry(34, "Extreme Hills+"),
					entry(35, "Savanna"),
					entry(36, "Savanna Plateau"),
					entry(37, "Mesa"),
					entry(38, "Mesa Plateau F"),
					entry(39, "Mesa Plateau"),
					// All of the modified biomes in this version just had an M after their original biome name
					entry(129,"Plains M"),
					entry(130,"Desert M"),
					entry(131,"Extreme Hills M"),
					entry(132,"Forest M"),
					entry(133,"Taiga M"),
					entry(134,"Swampland M"),
					entry(140,"Ice Plains M"),
					entry(149,"Jungle M"),
					entry(151,"Jungle Edge M"),
					entry(155,"Birch Forest M"),
					entry(156,"Birch Forest Hills M"),
					entry(157,"Roofed Forest M"),
					entry(158,"Cold Taiga M"),
					entry(160,"Mega Taiga M"),
					entry(161,"Mega Taiga Hills M"),
					entry(162,"Extreme Hills+ M"),
					entry(163,"Savanna M"),
					entry(164,"Savanna Plateau M"),
					entry(165,"Mesa M"),
					entry(166,"Mesa Plateau F M"),
					entry(167,"Mesa Plateau M")
				).sinceExtend(RecognisedVersion._14w02a, // Need confirmation on version; this was changed sometime after 1.7.10 and before 1.8.8
					entry(161,"Redwood Taiga Hills M")
				).sinceExtend(RecognisedVersion._14w21b, // Closest to 14w17a
					entry(9,  "The End")
				).sinceExtend(RecognisedVersion._15w31c, // Closest to 15w31a, need confirmation on this version. Was after 1.8.8 and before 1.9.4
					entry(129,"Sunflower Plains"),
					entry(132,"Flower Forest"),
					entry(140,"Ice Plains Spikes"),
					entry(160,"Mega Spruce Taiga"),
					entry(165,"Mesa (Bryce)")
				).sinceExtend(RecognisedVersion._15w40b, // Closest to 15w37a
					entry(127,"The Void")
				).sinceExtend(RecognisedVersion._18w06a,
					entry(40, "The End - Floating Island"),
					entry(41, "The End - Medium Island"),
					entry(42, "The End - High Island"),
					entry(43, "The End - Barren Island")
				).sinceExtend(RecognisedVersion._18w08b, // Closest to 18w08a
					entry(44, "Warm Ocean"),
					entry(45, "Lukewarm Ocean"),
					entry(46, "Cold Ocean"),
					entry(47, "Warm Deep Ocean"),
					entry(48, "Lukewarm Deep Ocean"),
					entry(49, "Cold Deep Ocean"),
					entry(50, "Frozen Deep Ocean")
				).sinceExtend(RecognisedVersion._18w16a,
					entry(8,  "Nether"),
					entry(38, "Mesa Forest Plateu"),
					entry(130,"Mutated Desert"),
					entry(131,"Mutated Extreme Hills"),
					entry(133,"Mutated Taiga"),
					entry(134,"Mutated Swampland"),
					entry(149,"Mutated Jungle"),
					entry(151,"Mutated Jungle Edge"),
					entry(155,"Mutated Birch Forest"),
					entry(156,"Mutated Birch Forest Hills"),
					entry(157,"Mutated Roofed Forest"),
					entry(158,"Mutated Cold Taiga"),
					entry(161,"Mutated Redwood Taiga Hills"),
					entry(162,"Mutated Extreme Hills+"),
					entry(163,"Mutated Savanna"),
					entry(164,"Mutated Savanna Plateau"),
					entry(166,"Mutated Mesa Forest Plateau"),
					entry(167,"Mutated Mesa Plateau")
				).sinceExtend(RecognisedVersion._18w19b, // Closest to 18w19a
					entry(3,  "Mountains"),
					entry(6,  "Swamp"),
					entry(12, "Snowy Tundra"),
					entry(13, "Snowy Mountains"),
					entry(14, "Mushroom Fields"),
					entry(15, "Mushroom Field Shore"),
					entry(18, "Wooded Hills"),
					entry(20, "Mountain Edge"),
					entry(25, "Stone Shore"),
					entry(26, "Snowy Beach"),
					entry(29, "Dark Forest"),
					entry(30, "Snowy Taiga"),
					entry(31, "Snowy Taiga Hills"),
					entry(32, "Giant Tree Taiga"),
					entry(33, "Giant Tree Taiga Hills"),
					entry(34, "Wooded Mountains"),
					entry(37, "Badlands"),
					entry(38, "Wooded Badlands Plateau"),
					entry(39, "Badlands Plateau"),
					entry(40, "Small End Islands"),
					entry(41, "End Midlands"),
					entry(42, "End Highlands"),
					entry(43, "End Barrens"),
					entry(47, "Deep Warm Ocean"),
					entry(48, "Deep Lukewarm Ocean"),
					entry(49, "Deep Cold Ocean"),
					entry(50, "Deep Frozen Ocean"),
					entry(130,"Desert Lakes"),
					entry(131,"Gravelly Mountains"),
					entry(133,"Taiga Mountains"),
					entry(134,"Swamp Hills"),
					entry(140,"Ice Spikes"),
					entry(149,"Modified Jungle"),
					entry(151,"Modified Jungle Edge"),
					entry(155,"Tall Birch Forest"),
					entry(156,"Tall Birch Hills"),
					entry(157,"Dark Forest Hills"),
					entry(158,"Snowy Taiga Mountains"),
					entry(160,"Giant Spruce Taiga"),
					entry(161,"Giant Spruce Taiga Hills"),
					entry(162,"Gravelly Mountains+"),
					entry(163,"Shattered Savanna"),
					entry(164,"Shattered Savanna Plateau"),
					entry(165,"Eroded Badlands"),
					entry(166,"Modified Wooded Badlands Plateau"),
					entry(167,"Modified Badlands Plateau")
				).sinceExtend(RecognisedVersion._18w43c, // Closest to 18w43a
					entry(168,"Bamboo Jungle"),
					entry(169,"Bamboo Jungle Hills")
				).sinceExtend(RecognisedVersion._20w06a,
					entry(8  ,"Nether Wastes"),
					entry(170,"Soul Sand Valley"),
					entry(171,"Crimson Forest"),
					entry(172,"Warped Forest")
				).construct()
				.andThen(BiomeIdNameMap::new))

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
						features.get(VALID_BIOMES_FOR_STRUCTURE_SPAWN))
				)))
			.with(VALID_BIOMES_FOR_STRUCTURE_SPAWN, VersionFeature.<Biome> listBuilder()
				.init(
					Biome.forest,
					Biome.plains,
					Biome.taiga,
					Biome.taigaHills,
					Biome.forestHills,
					Biome.jungle,
					Biome.jungleHills
				).construct())

			.with(FeatureKey.NETHER_FORTRESS_LOCATION_CHECKER, VersionFeature.bind(features ->
				VersionFeature.constant(new NetherFortressAlgorithm(getWorldSeed(features)))
			))

			.with(FeatureKey.END_ISLAND_LOCATION_CHECKER, VersionFeature.bind(features ->
				VersionFeature.constant(new EndCityLocationChecker(getWorldSeed(features)))
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
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_STRONGHOLD, VersionFeature.<Biome> listBuilder()
				.init().since(RecognisedVersion._b1_8_1,
					Biome.desert,
					Biome.forest,
					Biome.extremeHills,
					Biome.swampland
				).sinceExtend(RecognisedVersion._b1_9_pre6,
					Biome.taiga,
					Biome.icePlains,
					Biome.iceMountains
				).sinceExtend(RecognisedVersion._1_1,
					Biome.desertHills,
					Biome.forestHills,
					Biome.extremeHillsEdge
				).sinceExtend(RecognisedVersion._12w03a,
					Biome.jungle,
					Biome.jungleHills
				).since(RecognisedVersion._13w36a,
					// this includes all the biomes above, except for the swampland
					getValidBiomesForStrongholdSinceV13w36a()
				).sinceExtend(RecognisedVersion._18w06a,
					Biome.mushroomIslandShore
				).construct())


			.with(FeatureKey.VILLAGE_LOCATION_CHECKER, VersionFeature.bind(features ->
				VersionFeature.constant(
					new VillageLocationChecker(
						getWorldSeed(features),
						features.get(FeatureKey.BIOME_DATA_ORACLE),
						features.get(VALID_BIOMES_FOR_STRUCTURE_VILLAGE),
						features.get(DO_COMPLEX_VILLAGE_CHECK)
					)
				)))
			.with(VALID_BIOMES_FOR_STRUCTURE_VILLAGE, VersionFeature.<Biome> listBuilder()
				.init(
					Biome.plains,
					Biome.desert,
					Biome.savanna
				).sinceExtend(RecognisedVersion._16w20a,
					Biome.taiga
				).sinceExtend(RecognisedVersion._18w49a,
					Biome.icePlains
				).construct())
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
			.with(VALID_BIOMES_FOR_STRUCTURE_PILLAGER_OUTPOST, VersionFeature.<Biome> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._18w47b,
					Biome.plains,
					Biome.desert,
					Biome.savanna,
					Biome.taiga
				).sinceExtend(RecognisedVersion._18w49a,
					Biome.icePlains
				).construct())
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
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_DESERT_TEMPLE, VersionFeature.<Biome> listBuilder()
				.init(
					Biome.desert,
					Biome.desertHills
				).construct())
			.with(SEED_FOR_STRUCTURE_DESERT_TEMPLE, VersionFeature.<Long> builder()
				.init(
					14357617L
				).construct())

			.with(FeatureKey.IGLOO_LOCATION_CHECKER, scatteredFeature(
				VALID_BIOMES_AT_MIDDLE_OF_CHUNK_IGLOO,
				SEED_FOR_STRUCTURE_IGLOO))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_IGLOO, VersionFeature.<Biome> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._15w43c,
					Biome.icePlains,
					Biome.coldTaiga
				).construct())

			.with(SEED_FOR_STRUCTURE_IGLOO, VersionFeature.<Long> builder()
				.init(
					14357617L
				).since(RecognisedVersion._18w06a,
					14357618L
				).construct())

			.with(FeatureKey.JUNGLE_TEMPLE_LOCATION_CHECKER, scatteredFeature(
				VALID_BIOMES_AT_MIDDLE_OF_CHUNK_JUNGLE_TEMPLE,
				SEED_FOR_STRUCTURE_JUNGLE_TEMPLE))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_JUNGLE_TEMPLE, VersionFeature.<Biome> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._12w22a,
					Biome.jungle
				).sinceExtend(RecognisedVersion._1_4_2,
					Biome.jungleHills // TODO: jungle temples spawn only since 1.4.2 in jungle hills?
				).sinceExtend(RecognisedVersion._19w06a,
					Biome.bambooJungle,
					Biome.bambooJungleHills
				).construct())
			.with(SEED_FOR_STRUCTURE_JUNGLE_TEMPLE,  VersionFeature.<Long> builder()
				.init(
						14357617L
				).since(RecognisedVersion._18w06a,
						14357619L
				).construct())

			.with(FeatureKey.WITCH_HUT_LOCATION_CHECKER, scatteredFeature(
				VALID_BIOMES_AT_MIDDLE_OF_CHUNK_WITCH_HUT,
				SEED_FOR_STRUCTURE_WITCH_HUT))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_WITCH_HUT, VersionFeature.<Biome> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._1_4_2,
					Biome.swampland
				).construct())
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
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_MONUMENT, VersionFeature.<Biome> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._1_8,
					Biome.deepOcean,
					Biome.coldDeepOcean,
					Biome.warmDeepOcean,
					Biome.lukewarmDeepOcean,
					Biome.frozenDeepOcean
				).construct())
			.with(VALID_BIOMES_FOR_STRUCTURE_OCEAN_MONUMENT, VersionFeature.<Biome> listBuilder()
				.init().sinceExtend(RecognisedVersion._1_8,
					Biome.ocean,
					Biome.deepOcean,
					Biome.frozenOcean,
					Biome.river,
					Biome.frozenRiver,
					Biome.coldOcean,
					Biome.coldDeepOcean,
					Biome.warmOcean,
					Biome.warmDeepOcean,
					Biome.lukewarmOcean,
					Biome.lukewarmDeepOcean,
					Biome.frozenDeepOcean
				).construct())

			.with(FeatureKey.WOODLAND_MANSION_LOCATION_CHECKER, VersionFeature.bind(features ->
				VersionFeature.constant(
					new WoodlandMansionLocationChecker(
						getWorldSeed(features),
						features.get(FeatureKey.BIOME_DATA_ORACLE),
						features.get(VALID_BIOMES_FOR_STRUCTURE_WOODLAND_MANSION)
					)
				)))
			.with(VALID_BIOMES_FOR_STRUCTURE_WOODLAND_MANSION, VersionFeature.<Biome> listBuilder()
				.init(
					Biome.roofedForest,
					Biome.roofedForestM
				).construct())

			.with(FeatureKey.OCEAN_RUINS_LOCATION_CHECKER, scatteredFeature(
				MAX_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS,
				MIN_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS,
				VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_RUINS,
				SEED_FOR_STRUCTURE_OCEAN_RUINS))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_RUINS, VersionFeature.<Biome> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._18w09a,
					Biome.ocean,
					Biome.deepOcean,
					Biome.coldOcean,
					Biome.coldDeepOcean,
					Biome.warmOcean,
					Biome.warmDeepOcean,
					Biome.lukewarmOcean,
					Biome.lukewarmDeepOcean,
					Biome.frozenOcean,
					Biome.frozenDeepOcean
				).construct())
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
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_SHIPWRECK, VersionFeature.<Biome> listBuilder()
				.init()
				.sinceExtend(RecognisedVersion._18w11a,
					Biome.beach,
					Biome.coldBeach,
					Biome.ocean,
					Biome.deepOcean,
					Biome.coldOcean,
					Biome.coldDeepOcean,
					Biome.warmOcean,
					Biome.warmDeepOcean,
					Biome.lukewarmOcean,
					Biome.lukewarmDeepOcean,
					Biome.frozenOcean,
					Biome.frozenDeepOcean
				).construct())
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
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_BURIED_TREASURE, VersionFeature.<Biome> listBuilder()
				.init().sinceExtend(RecognisedVersion._18w10d,
					Biome.beach,
					Biome.coldBeach
				).construct())
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
	
	private static <K, V> Entry<K, V> entry(K key, V value) {
		return new SimpleEntry<K, V>(key, value);
	}

	private static List<Biome> getValidBiomesForStrongholdSinceV13w36a() {
		List<Biome> result = new ArrayList<>();
		for (Biome biome : Biome.allBiomes()) {
			if (biome.getType().getBiomeDepth() > 0) {
				result.add(biome);
			}
		}
		return result;
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
					features.get(validBiomes), features.get(structSeed),
					features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
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
					features.get(validBiomes), features.get(structSeed),
					features.get(BUGGY_STRUCTURE_COORDINATE_MATH)
				)
			)
		);
	}

}
