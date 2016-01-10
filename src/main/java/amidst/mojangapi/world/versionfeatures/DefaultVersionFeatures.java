package amidst.mojangapi.world.versionfeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.icon.locationchecker.ChanceBasedMineshaftAlgorithm;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm;
import amidst.mojangapi.world.icon.locationchecker.OriginalMineshaftAlgorithm;

@Immutable
public enum DefaultVersionFeatures {
	INSTANCE;
	
	public static VersionFeatures create(RecognisedVersion version) {
		// @formatter:off
		return new VersionFeatures(
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

	private final VersionFeature<Boolean> isSaveEnabled;
	private final VersionFeature<List<Biome>> validBiomesForStructure_Spawn;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_Stronghold;
	private final VersionFeature<Integer> numberOfStrongholds;
	private final VersionFeature<List<Biome>> validBiomesForStructure_Village;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_Temple;
	private final VersionFeature<Function<Long, MineshaftAlgorithm>> mineshaftAlgorithmFactory;
	private final VersionFeature<List<Biome>> validBiomesAtMiddleOfChunk_OceanMonument;
	private final VersionFeature<List<Biome>> validBiomesForStructure_OceanMonument;

	private DefaultVersionFeatures() {
		// @formatter:off
		this.isSaveEnabled = VersionFeature.<Boolean> builder()
				.init(
						true
				).exact(RecognisedVersion.V12w21a,
						false
				).exact(RecognisedVersion.V12w21b,
						false
				).exact(RecognisedVersion.V12w22a,
						false
				).exact(RecognisedVersion.UNKNOWN,
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
						Biome.desert,
						Biome.desertHills
				).sinceExtend(RecognisedVersion.V12w22a,
						Biome.jungle
				).sinceExtend(RecognisedVersion.V1_4_2,
						Biome.jungleHills,
						Biome.swampland
				).sinceExtend(RecognisedVersion.V15w43c,
						Biome.icePlains,
						Biome.coldTaiga
				).construct();
		this.mineshaftAlgorithmFactory = VersionFeature.<Function<Long, MineshaftAlgorithm>> builder()
				.init(
						OriginalMineshaftAlgorithm::new
				).since(RecognisedVersion.V1_4_2,
						seed -> new ChanceBasedMineshaftAlgorithm(seed, 0.01D)
				).since(RecognisedVersion.V1_7_2,
						seed -> new ChanceBasedMineshaftAlgorithm(seed, 0.004D)
				).construct();
		this.validBiomesAtMiddleOfChunk_OceanMonument = VersionFeature.<Biome> listBuilder()
				.init(
						// Not sure if the extended biomes count
						Biome.deepOcean
//						Biome.deepOceanM
				).construct();
		this.validBiomesForStructure_OceanMonument = VersionFeature.<Biome> listBuilder()
				.init(
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
