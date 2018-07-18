package amidst.mojangapi.world.versionfeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import amidst.documentation.Immutable;
import amidst.fragment.layer.LayerIds;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_Base;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_ChanceBased;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_Original;
import amidst.mojangapi.world.icon.locationchecker.OceanMonumentLocationChecker_Fixed;
import amidst.mojangapi.world.icon.locationchecker.OceanMonumentLocationChecker_Original;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_128Algorithm;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_Base;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_Buggy128Algorithm;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_Original;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@Immutable
public enum DefaultVersionFeatures {
	INSTANCE;

	public static VersionFeatures create(RecognisedVersion version) {
		return new VersionFeatures(
				INSTANCE.enabledLayers.getValue(version),
				INSTANCE.validBiomesForStructure_Spawn.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_Stronghold.getValue(version),
				INSTANCE.strongholdProducerFactory.getValue(version),
				INSTANCE.validBiomesForStructure_Village.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_DesertTemple.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_Igloo.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_JungleTemple.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_WitchHut.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_OceanRuins.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_Shipwreck.getValue(version),
				INSTANCE.mineshaftAlgorithmFactory.getValue(version),
				INSTANCE.oceanMonumentLocationCheckerFactory.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_OceanMonument.getValue(version),
				INSTANCE.validBiomesForStructure_OceanMonument.getValue(version),
				INSTANCE.validBiomesForStructure_WoodlandMansion.getValue(version),
				INSTANCE.seedForStructure_DesertTemple.getValue(version),
				INSTANCE.seedForStructure_Igloo.getValue(version),
				INSTANCE.seedForStructure_JungleTemple.getValue(version),
				INSTANCE.seedForStructure_WitchHut.getValue(version),
				INSTANCE.seedForStructure_OceanRuins.getValue(version),
				INSTANCE.seedForStructure_Shipwreck.getValue(version),
				INSTANCE.buggyStructureCoordinateMath.getValue(version));
	}

	private final VersionFeature<List<Integer>> enabledLayers;
	private final VersionFeature<List<Biome>> validBiomesForStructure_Spawn;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_Stronghold;
	private final VersionFeature<TriFunction<Long, BiomeDataOracle, List<Biome>, StrongholdProducer_Base>> strongholdProducerFactory;
	private final VersionFeature<List<Biome>> validBiomesForStructure_Village;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_DesertTemple;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_Igloo;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_JungleTemple;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_WitchHut;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_OceanRuins;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_Shipwreck;
	private final VersionFeature<Function<Long, MineshaftAlgorithm_Base>> mineshaftAlgorithmFactory;
	private final VersionFeature<QuadFunction<Long, BiomeDataOracle, List<Biome>, List<Biome>, LocationChecker>> oceanMonumentLocationCheckerFactory;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_OceanMonument;
	private final VersionFeature<List<Biome>> validBiomesForStructure_OceanMonument;
	private final VersionFeature<List<Biome>> validBiomesForStructure_WoodlandMansion;
	private final VersionFeature<Long> seedForStructure_DesertTemple;
	private final VersionFeature<Long> seedForStructure_Igloo;
	private final VersionFeature<Long> seedForStructure_JungleTemple;
	private final VersionFeature<Long> seedForStructure_WitchHut;
	private final VersionFeature<Long> seedForStructure_OceanRuins;
	private final VersionFeature<Long> seedForStructure_Shipwreck;
	private final VersionFeature<Boolean> buggyStructureCoordinateMath;

	private DefaultVersionFeatures() {
		// @formatter:off
		this.enabledLayers = VersionFeature.<Integer> listBuilder()
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
				).construct();
		this.validBiomesForStructure_Spawn = VersionFeature.<Biome> listBuilder()
				.init(
						Biome.forest,
						Biome.plains,
						Biome.taiga,
						Biome.taigaHills,
						Biome.forestHills,
						Biome.jungle,
						Biome.jungleHills
				).construct();
		this.validBiomesAtMiddleOfChunk_Stronghold = VersionFeature.<Biome> listBuilder()
				.init(
						// this is for the enable all layers function
						getValidBiomesForStrongholdSinceV13w36a()
				).since(RecognisedVersion._b1_8_1,
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
				).construct();
		this.strongholdProducerFactory = VersionFeature.<TriFunction<Long, BiomeDataOracle, List<Biome>, StrongholdProducer_Base>> builder()
				.init(
						(seed, biomeOracle, validBiomes) -> new StrongholdProducer_Original(seed, biomeOracle, validBiomes)
				).since(RecognisedVersion._15w43c,
						// this should be 15w43a, which is no recognised
						(seed, biomeOracle, validBiomes) -> new StrongholdProducer_Buggy128Algorithm(seed, biomeOracle, validBiomes)
				).since(RecognisedVersion._1_9_pre2,
						// this should be 16w06a
						(seed, biomeOracle, validBiomes) -> new StrongholdProducer_128Algorithm(seed, biomeOracle, validBiomes)
				).construct();
		this.validBiomesForStructure_Village = VersionFeature.<Biome> listBuilder()
				.init(
						Biome.plains,
						Biome.desert,
						Biome.savanna
				).sinceExtend(RecognisedVersion._16w20a,
						Biome.taiga
				)
				.construct();
		this.validBiomesAtMiddleOfChunk_DesertTemple = VersionFeature.<Biome> listBuilder()
				.init(
						Biome.desert,
						Biome.desertHills
				).construct();
		this.validBiomesAtMiddleOfChunk_Igloo = VersionFeature.<Biome> listBuilder()
				.init(
						// this is for the enable all layers function
						Biome.icePlains,
						Biome.coldTaiga
				).since(RecognisedVersion._12w21a
				).sinceExtend(RecognisedVersion._15w43c,
						Biome.icePlains,
						Biome.coldTaiga
				).construct();
		this.validBiomesAtMiddleOfChunk_JungleTemple = VersionFeature.<Biome> listBuilder()
				.init(
						// this is for the enable all layers function
						Biome.jungle,
						Biome.jungleHills
				).since(RecognisedVersion._12w21a
				).sinceExtend(RecognisedVersion._12w22a,
						Biome.jungle
				).sinceExtend(RecognisedVersion._1_4_2,
						Biome.jungleHills // TODO: jungle temples spawn only since 1.4.2 in jungle hills?
				).construct();
		this.validBiomesAtMiddleOfChunk_WitchHut = VersionFeature.<Biome> listBuilder()
				.init(
						// this is for the enable all layers function
						Biome.swampland
				).since(RecognisedVersion._12w21a
				).sinceExtend(RecognisedVersion._1_4_2,
						Biome.swampland
				).construct();
		this.validBiomesAtMiddleOfChunk_OceanRuins = VersionFeature.<Biome> listBuilder()
				.init(
						// this is for the enable all layers function
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
				).since(RecognisedVersion._12w21a
				).sinceExtend(RecognisedVersion._18w09a,
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
				).construct();
		this.validBiomesAtMiddleOfChunk_Shipwreck = VersionFeature.<Biome> listBuilder()
				.init(
						// this is for the enable all layers function
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
				).since(RecognisedVersion._12w21a
				).sinceExtend(RecognisedVersion._18w11a,
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
				).construct();
		this.mineshaftAlgorithmFactory = VersionFeature.<Function<Long, MineshaftAlgorithm_Base>> builder()
				.init(
						seed -> new MineshaftAlgorithm_Original(seed)
				).since(RecognisedVersion._1_4_2,
						seed -> new MineshaftAlgorithm_ChanceBased(seed, 0.01D)
				).since(RecognisedVersion._1_7_2,
						seed -> new MineshaftAlgorithm_ChanceBased(seed, 0.004D)
				).construct();
		this.oceanMonumentLocationCheckerFactory = VersionFeature.<QuadFunction<Long, BiomeDataOracle, List<Biome>, List<Biome>, LocationChecker>> builder()
				.init(
						(seed, biomeOracle, validCenterBiomes, validBiomes) -> new OceanMonumentLocationChecker_Original(seed, biomeOracle, validCenterBiomes, validBiomes)
				).since(RecognisedVersion._15w46a,
						(seed, biomeOracle, validCenterBiomes, validBiomes) -> new OceanMonumentLocationChecker_Fixed(seed, biomeOracle, validCenterBiomes, validBiomes)
				).construct();
		this.validBiomesAtMiddleOfChunk_OceanMonument = VersionFeature.<Biome> listBuilder()
				.init(
						// this is also for the enable all layers function, else it would be since 1.8
						Biome.deepOcean
				).sinceExtend(RecognisedVersion._12w08a,
						Biome.coldDeepOcean,
						Biome.warmDeepOcean,
						Biome.lukewarmDeepOcean,
						Biome.frozenDeepOcean
				).construct();
		this.validBiomesForStructure_OceanMonument = VersionFeature.<Biome> listBuilder()
				.init(
						// this is also for the enable all layers function, else it would be since 1.8
						Biome.ocean,
						Biome.deepOcean,
						Biome.frozenOcean,
						Biome.river,
						Biome.frozenRiver
				).sinceExtend(RecognisedVersion._12w08a,
						Biome.coldOcean,
						Biome.coldDeepOcean,
						Biome.warmOcean,
						Biome.warmDeepOcean,
						Biome.lukewarmOcean,
						Biome.lukewarmDeepOcean,
						Biome.frozenDeepOcean
				).construct();
		this.validBiomesForStructure_WoodlandMansion = VersionFeature.<Biome> listBuilder()
				.init(
						Biome.roofedForest,
						Biome.roofedForestM
				).construct();
		this.seedForStructure_DesertTemple = VersionFeature.<Long> builder()
				.init(
						14357617L
				).construct();
		this.seedForStructure_Igloo = VersionFeature.<Long> builder()
				.init(
						14357617L
				).since(RecognisedVersion._18w06a,
						14357618L
				).construct();
		this.seedForStructure_JungleTemple = VersionFeature.<Long> builder()
				.init(
						14357617L
				).since(RecognisedVersion._18w06a,
						14357619L
				).construct();
		this.seedForStructure_WitchHut = VersionFeature.<Long> builder()
				.init(
						14357617L
				).since(RecognisedVersion._18w06a,
						14357620L
				).construct();
		this.seedForStructure_OceanRuins = VersionFeature.<Long> builder()
				.init(
						14357621L
				).construct();
		this.seedForStructure_Shipwreck = VersionFeature.<Long> builder()
				.init(
						165745295L
				).construct();
				
		this.buggyStructureCoordinateMath = VersionFeature.<Boolean> builder()
				.init(
						false
				).since(RecognisedVersion._18w06a,
						true  // Bug MC-131462.
				).since(RecognisedVersion._1_13_pre4,
						false
				).since(RecognisedVersion._1_13_pre7,
						true
				).construct();
		// @formatter:on
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
}
