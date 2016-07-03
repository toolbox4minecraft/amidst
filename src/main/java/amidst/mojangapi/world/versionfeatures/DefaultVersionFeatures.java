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
		// @formatter:off
		return new VersionFeatures(
				INSTANCE.enabledLayers.getValue(version),
				INSTANCE.isSaveEnabled.getValue(version),
				INSTANCE.validBiomesForStructure_Spawn.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_Stronghold.getValue(version),
				INSTANCE.strongholdProducerFactory.getValue(version),
				INSTANCE.validBiomesForStructure_Village.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_Temple.getValue(version),
				INSTANCE.mineshaftAlgorithmFactory.getValue(version),
				INSTANCE.oceanMonumentLocationCheckerFactory.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_OceanMonument.getValue(version),
				INSTANCE.validBiomesForStructure_OceanMonument.getValue(version)
		);
		// @formatter:on
	}

	private final VersionFeature<List<Integer>> enabledLayers;
	private final VersionFeature<Boolean> isSaveEnabled;
	private final VersionFeature<List<Biome>> validBiomesForStructure_Spawn;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_Stronghold;
	private final VersionFeature<TriFunction<Long, BiomeDataOracle, List<Biome>, StrongholdProducer_Base>> strongholdProducerFactory;
	private final VersionFeature<List<Biome>> validBiomesForStructure_Village;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_Temple;
	private final VersionFeature<Function<Long, MineshaftAlgorithm_Base>> mineshaftAlgorithmFactory;
	private final VersionFeature<QuadFunction<Long, BiomeDataOracle, List<Biome>, List<Biome>, LocationChecker>> oceanMonumentLocationCheckerFactory;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_OceanMonument;
	private final VersionFeature<List<Biome>> validBiomesForStructure_OceanMonument;

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
				).construct();
		this.isSaveEnabled = VersionFeature.<Boolean> builder()
				.init(
						true
				).exact(RecognisedVersion._12w21a,
						false
				).exact(RecognisedVersion._12w21b,
						false
				).exact(RecognisedVersion._12w22a,
						false
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
				).construct();
		this.validBiomesAtMiddleOfChunk_Temple = VersionFeature.<Biome> listBuilder()
				.init(
						// this is for the enable all layers function
						Biome.desert,
						Biome.desertHills,
						Biome.jungle,
						Biome.jungleHills,
						Biome.swampland,
						Biome.icePlains,
						Biome.coldTaiga
				).since(RecognisedVersion._12w21a,
						Biome.desert,
						Biome.desertHills
				).sinceExtend(RecognisedVersion._12w22a,
						Biome.jungle
				).sinceExtend(RecognisedVersion._1_4_2,
						Biome.jungleHills, // TODO: jungle temples spawn only since 1.4.2 in jungle hills?
						Biome.swampland
				).sinceExtend(RecognisedVersion._15w43c,
						Biome.icePlains,
						Biome.coldTaiga
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
						// Not sure if the extended biomes count
						Biome.deepOcean
//						Biome.deepOceanM
				).construct();
		this.validBiomesForStructure_OceanMonument = VersionFeature.<Biome> listBuilder()
				.init(
						// this is also for the enable all layers function, else it would be since 1.8
						// Not sure if the extended biomes count
						Biome.ocean,
						Biome.deepOcean,
						Biome.frozenOcean,
						Biome.river,
						Biome.frozenRiver,
						Biome.oceanM,
						Biome.deepOceanM,
						Biome.frozenOceanM,
						Biome.riverM,
						Biome.frozenRiverM
				).construct();
		// @formatter:on
	}

	private static List<Biome> getValidBiomesForStrongholdSinceV13w36a() {
		List<Biome> result = new ArrayList<Biome>();
		for (Biome biome : Biome.allBiomes()) {
			if (biome.getType().getBiomeDepth() > 0) {
				result.add(biome);
			}
		}
		return result;
	}
}
