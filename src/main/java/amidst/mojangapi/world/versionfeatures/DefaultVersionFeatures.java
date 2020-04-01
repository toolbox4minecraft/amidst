package amidst.mojangapi.world.versionfeatures;

import java.util.ArrayList;
import java.util.List;
import amidst.fragment.layer.LayerIds;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.biome.Biome;
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

import static amidst.mojangapi.world.biome.BiomeType.*;

public enum DefaultVersionFeatures {
	;

	public static VersionFeatures.Builder builder(WorldOptions worldOptions, BiomeDataOracle biomeDataOracle) {
		return FEATURES_BUILDER.clone()
						.withValue(FeatureKey.WORLD_OPTIONS, worldOptions)
						.withValue(FeatureKey.BIOME_DATA_ORACLE, biomeDataOracle);
	}
	
	public static VersionFeatures.Builder builder() {
		return FEATURES_BUILDER.clone();
	}

	// @formatter:off
	public static final FeatureKey<List<Integer>>  VALID_BIOMES_FOR_STRUCTURE_SPAWN                = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_AT_MIDDLE_OF_CHUNK_STRONGHOLD      = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_FOR_STRUCTURE_VILLAGE              = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_FOR_STRUCTURE_PILLAGER_OUTPOST     = FeatureKey.make();
	private static final FeatureKey<Boolean>       DO_COMPLEX_VILLAGE_CHECK                        = FeatureKey.make();
	private static final FeatureKey<Integer>       OUTPOST_VILLAGE_AVOID_DISTANCE                  = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_AT_MIDDLE_OF_CHUNK_DESERT_TEMPLE   = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_AT_MIDDLE_OF_CHUNK_IGLOO           = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_AT_MIDDLE_OF_CHUNK_JUNGLE_TEMPLE   = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_AT_MIDDLE_OF_CHUNK_WITCH_HUT       = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_RUINS     = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_AT_MIDDLE_OF_CHUNK_SHIPWRECK       = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_MONUMENT  = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_AT_MIDDLE_OF_CHUNK_BURIED_TREASURE = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_FOR_STRUCTURE_OCEAN_MONUMENT       = FeatureKey.make();
	private static final FeatureKey<List<Integer>> VALID_BIOMES_FOR_STRUCTURE_WOODLAND_MANSION     = FeatureKey.make();
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
			
			.with(FeatureKey.BIOME_LIST, VersionFeature.constant(
				VersionFeature.biomeListBuilder()
					.init( // Starts at beta 1.8
						new Biome(0, "Ocean", OCEAN),
						new Biome(1, "Plains", PLAINS),
						new Biome(2, "Desert", PLAINS_FLAT),
						new Biome(3, "Extreme Hills", MOUNTAINS),
						new Biome(4, "Forest", PLAINS),
						new Biome(5, "Taiga", PLAINS_TAIGA),
						new Biome(6, "Swampland", SWAMPLAND),
						new Biome(7, "River", RIVER),
						new Biome(8, "Hell", PLAINS),
						new Biome(9, "Sky", PLAINS)
					).sinceExtend(RecognisedVersion._b1_9_pre1,
						new Biome(10, "Frozen Ocean", OCEAN),
						new Biome(11, "Frozen River", RIVER),
						new Biome(12, "Ice Plains", PLAINS_FLAT),
						new Biome(13, "Ice Mountains", HILLS), // There was a bug causing this biome to not be generated until the next version
						new Biome(14, "Mushroom Island", ISLAND),
						new Biome(15, "Mushroom Island Shore", BEACH)
					).sinceExtend(RecognisedVersion._1_1, // Closest to 12w01a
						new Biome(16, "Beach", BEACH),
						new Biome(17, "Desert Hills", HILLS),
						new Biome(18, "Forest Hills", HILLS),
						new Biome(19, "Taiga Hills", HILLS),
						new Biome(20, "Extreme Hills Edge", MOUNTAINS.weaken())
					).sinceExtend(RecognisedVersion._12w03a,
						new Biome(21, "Jungle", PLAINS),
						new Biome(22, "Jungle Hills", HILLS)
					).sinceExtend(RecognisedVersion._13w36a,
						new Biome(23, "Jungle Edge", PLAINS),
						new Biome(24, "Deep Ocean", DEEP_OCEAN),
						new Biome(25, "Stone Beach", BEACH_CLIFFS),
						new Biome(26, "Cold Beach", BEACH),
						new Biome(27, "Birch Forest", PLAINS),
						new Biome(28, "Birch Forest Hills", HILLS),
						new Biome(29, "Roofed Forest", PLAINS),
						new Biome(30, "Cold Taiga", PLAINS_TAIGA),
						new Biome(31, "Cold Taiga Hills", HILLS),
						new Biome(32, "Mega Taiga", PLAINS_TAIGA),
						new Biome(33, "Mega Taiga Hills", HILLS),
						new Biome(34, "Extreme Hills+", MOUNTAINS),
						new Biome(35, "Savanna", PLAINS_FLAT),
						new Biome(36, "Savanna Plateau", PLATEAU),
						new Biome(37, "Mesa", PLAINS),
						new Biome(38, "Mesa Plateau F", PLATEAU),
						new Biome(39, "Mesa Plateau", PLATEAU),
						// All of the modified biomes in this version just had an M after their original biome name
						new Biome("Plains M", 1, PLAINS),
						new Biome("Desert M", 2, PLAINS_FLAT),
						new Biome("Extreme Hills M", 3, MOUNTAINS),
						new Biome("Forest M", 4, PLAINS),
						new Biome("Taiga M", 5, PLAINS_TAIGA),
						new Biome("Swampland M", 6, SWAMPLAND),
						new Biome("Ice Plains M", 12, PLAINS_FLAT),
						new Biome("Jungle M", 21, PLAINS),
						new Biome("Jungle Edge M", 23, PLAINS),
						new Biome("Birch Forest M", 27, PLAINS),
						new Biome("Birch Forest Hills M", 28, HILLS),
						new Biome("Roofed Forest M", 29, PLAINS),
						new Biome("Cold Taiga M", 30, PLAINS_TAIGA),
						new Biome("Mega Taiga M", 32, PLAINS_TAIGA),
						new Biome(161, "Mega Taiga Hills M", PLAINS_TAIGA.strengthen(), true),
						new Biome("Extreme Hills+ M", 34, MOUNTAINS),
						new Biome("Savanna M", 35, PLAINS_FLAT),
						new Biome("Savanna Plateau M", 36, PLATEAU),
						new Biome("Mesa M", 37, PLAINS),
						new Biome("Mesa Plateau F M", 38, PLATEAU),
						new Biome("Mesa Plateau M", 39, PLATEAU)
					).sinceExtend(RecognisedVersion._14w02a, // Need confirmation on version; this was changed sometime after 1.7.10 and before 1.8.8
						new Biome(161,"Redwood Taiga Hills M", PLAINS_TAIGA.strengthen(), true)
					).sinceExtend(RecognisedVersion._14w21b, // Closest to 14w17a
						new Biome(9,  "The End", PLAINS)
					).sinceExtend(RecognisedVersion._15w31c, // Closest to 15w31a, need confirmation on this version. Was after 1.8.8 and before 1.9.4
						new Biome("Sunflower Plains", 1, PLAINS),
						new Biome("Flower Forest", 4, PLAINS),
						new Biome("Ice Plains Spikes", 12, PLAINS_FLAT),
						new Biome("Mega Spruce Taiga", 32, PLAINS_TAIGA),
						new Biome("Mesa (Bryce)", 37, PLAINS)
					).sinceExtend(RecognisedVersion._15w40b, // Closest to 15w37a
						new Biome(127,"The Void", PLAINS)
					).sinceExtend(RecognisedVersion._18w06a,
						new Biome(40, "The End - Floating Island", PLAINS),
						new Biome(41, "The End - Medium Island", PLAINS),
						new Biome(42, "The End - High Island", PLAINS),
						new Biome(43, "The End - Barren Island", PLAINS)
					).sinceExtend(RecognisedVersion._18w08b, // Closest to 18w08a
						new Biome(44, "Warm Ocean", OCEAN),
						new Biome(45, "Lukewarm Ocean", OCEAN),
						new Biome(46, "Cold Ocean", OCEAN),
						new Biome(47, "Warm Deep Ocean", OCEAN),
						new Biome(48, "Lukewarm Deep Ocean", OCEAN),
						new Biome(49, "Cold Deep Ocean", OCEAN),
						new Biome(50, "Frozen Deep Ocean", OCEAN)
					).sinceExtend(RecognisedVersion._18w16a,
						new Biome(8, "Nether", PLAINS),
						new Biome(38, "Mesa Forest Plateu", PLATEAU),
						new Biome("Mutated Desert", 2, PLAINS_FLAT),
						new Biome("Mutated Extreme Hills", 3, MOUNTAINS),
						new Biome("Mutated Taiga", 5, PLAINS_TAIGA),
						new Biome("Mutated Swampland", 6, SWAMPLAND),
						new Biome("Mutated Jungle", 21, PLAINS),
						new Biome("Mutated Jungle Edge", 23, PLAINS),
						new Biome("Mutated Birch Forest", 27, PLAINS),
						new Biome("Mutated Birch Forest Hills", 28, HILLS),
						new Biome("Mutated Roofed Forest", 29, PLAINS),
						new Biome("Mutated Cold Taiga", 30, PLAINS_TAIGA),
						new Biome("Mutated Extreme Hills+", 34, MOUNTAINS),
						new Biome("Mutated Savanna", 35, PLAINS_FLAT),
						new Biome("Mutated Savanna Plateau", 36, PLATEAU),
						new Biome("Mutated Mesa Forest Plateau", 38, PLATEAU),
						new Biome("Mutated Mesa Plateau", 39, PLATEAU)
					).sinceExtend(RecognisedVersion._18w19b, // Closest to 18w19a
						new Biome(3, "Mountains", MOUNTAINS),
						new Biome(6, "Swamp", SWAMPLAND),
						new Biome(12, "Snowy Tundra", PLAINS_FLAT),
						new Biome(13, "Snowy Mountains", HILLS),
						new Biome(14, "Mushroom Fields", ISLAND),
						new Biome(15, "Mushroom Field Shore", BEACH),
						new Biome(18, "Wooded Hills", HILLS),
						new Biome(20, "Mountain Edge", MOUNTAINS.weaken()),
						new Biome(25, "Stone Shore", BEACH_CLIFFS),
						new Biome(26, "Snowy Beach", BEACH),
						new Biome(29, "Dark Forest", PLAINS),
						new Biome(30, "Snowy Taiga", PLAINS_TAIGA),
						new Biome(31, "Snowy Taiga Hills", HILLS),
						new Biome(32, "Giant Tree Taiga", PLAINS_TAIGA),
						new Biome(33, "Giant Tree Taiga Hills", HILLS),
						new Biome(34, "Wooded Mountains", MOUNTAINS),
						new Biome(37, "Badlands", PLAINS),
						new Biome(38, "Wooded Badlands Plateau", PLATEAU),
						new Biome(39, "Badlands Plateau", PLATEAU),
						new Biome(40, "Small End Islands", PLAINS),
						new Biome(41, "End Midlands", PLAINS),
						new Biome(42, "End Highlands", PLAINS),
						new Biome(43, "End Barrens", PLAINS),
						new Biome(47, "Deep Warm Ocean", OCEAN),
						new Biome(48, "Deep Lukewarm Ocean", OCEAN),
						new Biome(49, "Deep Cold Ocean", OCEAN),
						new Biome(50, "Deep Frozen Ocean", OCEAN),
						new Biome("Desert Lakes", 2, PLAINS_FLAT),
						new Biome("Gravelly Mountains", 3, MOUNTAINS),
						new Biome("Taiga Mountains", 5, PLAINS_TAIGA),
						new Biome("Swamp Hills", 6, SWAMPLAND),
						new Biome("Ice Spikes", 12, PLAINS_FLAT),
						new Biome("Modified Jungle", 21, PLAINS),
						new Biome("Modified Jungle Edge", 23, PLAINS),
						new Biome("Tall Birch Forest", 27, PLAINS),
						new Biome("Tall Birch Hills", 28, HILLS),
						new Biome("Dark Forest Hills", 29, PLAINS),
						new Biome("Snowy Taiga Mountains", 30, PLAINS_TAIGA),
						new Biome("Giant Spruce Taiga", 32, PLAINS_TAIGA),
						new Biome(161, "Giant Spruce Taiga Hills", PLAINS_TAIGA, true), // Don't strengthen this in newer versions (might be wrong here)
						new Biome("Gravelly Mountains+", 34, MOUNTAINS),
						new Biome("Shattered Savanna", 35, PLAINS_FLAT),
						new Biome("Shattered Savanna Plateau", 36, PLATEAU),
						new Biome("Eroded Badlands", 37, PLAINS),
						new Biome("Modified Wooded Badlands Plateau", 38, PLATEAU),
						new Biome("Modified Badlands Plateau", 39, PLATEAU)
					).sinceExtend(RecognisedVersion._18w43c, // Closest to 18w43a
						new Biome(168, "Bamboo Jungle", PLAINS),
						new Biome(169, "Bamboo Jungle Hills", HILLS)
					).sinceExtend(RecognisedVersion._20w06a,
						new Biome(8, "Nether Wastes", PLAINS),
						new Biome(170, "Soul Sand Valley", PLAINS),
						new Biome(171, "Crimson Forest", PLAINS),
						new Biome(172, "Warped Forest", PLAINS)
					).construct()))

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
			.with(VALID_BIOMES_FOR_STRUCTURE_SPAWN, VersionFeature.<Integer> listBuilder()
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
				List<Integer> validBiomes = features.get(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_STRONGHOLD);
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
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_STRONGHOLD, VersionFeature.bind(features ->
				VersionFeature.<Integer> listBuilder()
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
						getValidBiomesForStrongholdSinceV13w36a(features.get(FeatureKey.BIOME_LIST))
					).sinceExtend(RecognisedVersion._18w06a,
						Biome.mushroomIslandShore
					).construct()))


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
			.with(VALID_BIOMES_FOR_STRUCTURE_PILLAGER_OUTPOST, VersionFeature.<Integer> listBuilder()
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
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_DESERT_TEMPLE, VersionFeature.<Integer> listBuilder()
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
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_IGLOO, VersionFeature.<Integer> listBuilder()
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
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_JUNGLE_TEMPLE, VersionFeature.<Integer> listBuilder()
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
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_WITCH_HUT, VersionFeature.<Integer> listBuilder()
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
				List<Integer> validCenterBiomes = features.get(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_MONUMENT);
				List<Integer> validBiomes = features.get(VALID_BIOMES_FOR_STRUCTURE_OCEAN_MONUMENT);
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
					Biome.deepOcean,
					Biome.coldDeepOcean,
					Biome.warmDeepOcean,
					Biome.lukewarmDeepOcean,
					Biome.frozenDeepOcean
				).construct())
			.with(VALID_BIOMES_FOR_STRUCTURE_OCEAN_MONUMENT, VersionFeature.<Integer> listBuilder()
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
			.with(VALID_BIOMES_FOR_STRUCTURE_WOODLAND_MANSION, VersionFeature.<Integer> listBuilder()
				.init(
					Biome.roofedForest,
					Biome.roofedForestM
				).construct())

			.with(FeatureKey.OCEAN_RUINS_LOCATION_CHECKER, scatteredFeature(
				MAX_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS,
				MIN_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS,
				VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_RUINS,
				SEED_FOR_STRUCTURE_OCEAN_RUINS))
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_RUINS, VersionFeature.<Integer> listBuilder()
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
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_SHIPWRECK, VersionFeature.<Integer> listBuilder()
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
			.with(VALID_BIOMES_AT_MIDDLE_OF_CHUNK_BURIED_TREASURE, VersionFeature.<Integer> listBuilder()
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

	private static List<Integer> getValidBiomesForStrongholdSinceV13w36a(BiomeList biomeList) {
		List<Integer> result = new ArrayList<>();
		for (Biome biome : biomeList.iterable()) {
			if (biome.getType().getBiomeDepth() > 0) {
				result.add(biome.getId());
			}
		}
		return result;
	}

	private static long getWorldSeed(VersionFeatures features) {
		return features.get(FeatureKey.WORLD_OPTIONS).getWorldSeed().getLong();
	}

	private static VersionFeature<LocationChecker> scatteredFeature(
			FeatureKey<Byte> maxDistance, FeatureKey<Byte> minDistance,
			FeatureKey<List<Integer>> validBiomes, FeatureKey<Long> structSeed) {
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
			FeatureKey<List<Integer>> validBiomes, FeatureKey<Long> structSeed) {
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
