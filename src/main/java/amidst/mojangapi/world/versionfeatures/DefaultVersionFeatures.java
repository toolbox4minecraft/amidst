package amidst.mojangapi.world.versionfeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import amidst.documentation.Immutable;
import amidst.fragment.layer.LayerIds;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_ChanceBased;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_Base;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_Original;

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
				INSTANCE.numberOfStrongholds.getValue(version),
				INSTANCE.validBiomesForStructure_Village.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_Temple.getValue(version),
				INSTANCE.mineshaftAlgorithmFactory.getValue(version),
				INSTANCE.validBiomesAtMiddleOfChunk_OceanMonument.getValue(version),
				INSTANCE.validBiomesForStructure_OceanMonument.getValue(version)
		);
		// @formatter:on
	}

	private final VersionFeature<List<Integer>> enabledLayers;
	private final VersionFeature<Boolean> isSaveEnabled;
	private final VersionFeature<List<Biome>> validBiomesForStructure_Spawn;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_Stronghold;
	private final VersionFeature<Integer> numberOfStrongholds;
	private final VersionFeature<List<Biome>> validBiomesForStructure_Village;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_Temple;
	private final VersionFeature<Function<Long, MineshaftAlgorithm_Base>> mineshaftAlgorithmFactory;
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
				).sinceExtend(RecognisedVersion.V12w21a,
						LayerIds.TEMPLE
				).sinceExtend(RecognisedVersion.V1_8,
						LayerIds.OCEAN_MONUMENT
				).sinceExtend(RecognisedVersion.V15w31c,
						LayerIds.END_ISLANDS,
						LayerIds.END_CITY
				).construct();
		this.isSaveEnabled = VersionFeature.<Boolean> builder()
				.init(
						true
				).exact(RecognisedVersion.V12w21a,
						false
				).exact(RecognisedVersion.V12w21b,
						false
				).exact(RecognisedVersion.V12w22a,
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
				).since(RecognisedVersion.Vb1_8_1,
						Biome.desert,
						Biome.forest,
						Biome.extremeHills,
						Biome.swampland
				).sinceExtend(RecognisedVersion.V1_9pre6,
						Biome.taiga,
						Biome.icePlains,
						Biome.iceMountains
				).sinceExtend(RecognisedVersion.V1_1,
						Biome.desertHills,
						Biome.forestHills,
						Biome.extremeHillsEdge
				).sinceExtend(RecognisedVersion.V12w03a,
						Biome.jungle,
						Biome.jungleHills
				).since(RecognisedVersion.V13w36a,
						// this includes all the biomes above, except for the swampland
						getValidBiomesForStrongholdSinceV13w36a()
				).construct();
		this.numberOfStrongholds = VersionFeature.<Integer> builder()
				.init(
						3
				).since(RecognisedVersion.V15w43c,
						128
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
				).since(RecognisedVersion.V12w21a,
						Biome.desert,
						Biome.desertHills
				).sinceExtend(RecognisedVersion.V12w22a,
						Biome.jungle
				).sinceExtend(RecognisedVersion.V1_4_2,
						Biome.jungleHills, // TODO: jungle temples spawn only since 1.4.2 in jungle hills?
						Biome.swampland
				).sinceExtend(RecognisedVersion.V15w43c,
						Biome.icePlains,
						Biome.coldTaiga
				).construct();
		this.mineshaftAlgorithmFactory = VersionFeature.<Function<Long, MineshaftAlgorithm_Base>> builder()
				.init(
						seed -> new MineshaftAlgorithm_Original(seed)
				).since(RecognisedVersion.V1_4_2,
						seed -> new MineshaftAlgorithm_ChanceBased(seed, 0.01D)
				).since(RecognisedVersion.V1_7_2,
						seed -> new MineshaftAlgorithm_ChanceBased(seed, 0.004D)
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
			if (biome.getType().getValue1() > 0) {
				result.add(biome);
			}
		}
		return result;
	}
}
