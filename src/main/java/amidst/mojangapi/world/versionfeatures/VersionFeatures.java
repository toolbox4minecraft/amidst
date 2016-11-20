package amidst.mojangapi.world.versionfeatures;

import java.util.List;
import java.util.function.Function;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm_Base;
import amidst.mojangapi.world.icon.producer.StrongholdProducer_Base;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@Immutable
public class VersionFeatures {
	private final List<Integer> enabledLayers;
	private final List<Biome> validBiomesForStructure_Spawn;
	private final List<Biome> validBiomesAtMiddleOfChunk_Stronghold;
	private final TriFunction<Long, BiomeDataOracle, List<Biome>, StrongholdProducer_Base> strongholdProducerFactory;
	private final List<Biome> validBiomesForStructure_Village;
	private final List<Biome> validBiomesAtMiddleOfChunk_Temple;
	private final Function<Long, MineshaftAlgorithm_Base> mineshaftAlgorithmFactory;
	private final QuadFunction<Long, BiomeDataOracle, List<Biome>, List<Biome>, LocationChecker> oceanMonumentLocationCheckerFactory;
	private final List<Biome> validBiomesAtMiddleOfChunk_OceanMonument;
	private final List<Biome> validBiomesForStructure_OceanMonument;

	public VersionFeatures(
			List<Integer> enabledLayers,
			List<Biome> validBiomesForStructure_Spawn,
			List<Biome> validBiomesAtMiddleOfChunk_Stronghold,
			TriFunction<Long, BiomeDataOracle, List<Biome>, StrongholdProducer_Base> strongholdProducerFactory,
			List<Biome> validBiomesForStructure_Village,
			List<Biome> validBiomesAtMiddleOfChunk_Temple,
			Function<Long, MineshaftAlgorithm_Base> mineshaftAlgorithmFactory,
			QuadFunction<Long, BiomeDataOracle, List<Biome>, List<Biome>, LocationChecker> oceanMonumentLocationCheckerFactory,
			List<Biome> validBiomesAtMiddleOfChunk_OceanMonument,
			List<Biome> validBiomesForStructure_OceanMonument) {
		this.enabledLayers = enabledLayers;
		this.validBiomesForStructure_Spawn = validBiomesForStructure_Spawn;
		this.validBiomesAtMiddleOfChunk_Stronghold = validBiomesAtMiddleOfChunk_Stronghold;
		this.strongholdProducerFactory = strongholdProducerFactory;
		this.validBiomesForStructure_Village = validBiomesForStructure_Village;
		this.validBiomesAtMiddleOfChunk_Temple = validBiomesAtMiddleOfChunk_Temple;
		this.mineshaftAlgorithmFactory = mineshaftAlgorithmFactory;
		this.oceanMonumentLocationCheckerFactory = oceanMonumentLocationCheckerFactory;
		this.validBiomesAtMiddleOfChunk_OceanMonument = validBiomesAtMiddleOfChunk_OceanMonument;
		this.validBiomesForStructure_OceanMonument = validBiomesForStructure_OceanMonument;
	}

	public boolean hasLayer(int layerId) {
		return enabledLayers.contains(layerId);
	}

	public List<Biome> getValidBiomesForStructure_Spawn() {
		return validBiomesForStructure_Spawn;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_Stronghold() {
		return validBiomesAtMiddleOfChunk_Stronghold;
	}

	public TriFunction<Long, BiomeDataOracle, List<Biome>, StrongholdProducer_Base> getStrongholdProducerFactory() {
		return strongholdProducerFactory;
	}

	public List<Biome> getValidBiomesForStructure_Village() {
		return validBiomesForStructure_Village;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_Temple() {
		return validBiomesAtMiddleOfChunk_Temple;
	}

	public Function<Long, MineshaftAlgorithm_Base> getMineshaftAlgorithmFactory() {
		return mineshaftAlgorithmFactory;
	}

	public QuadFunction<Long, BiomeDataOracle, List<Biome>, List<Biome>, LocationChecker> getOceanMonumentLocationCheckerFactory() {
		return oceanMonumentLocationCheckerFactory;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_OceanMonument() {
		return validBiomesAtMiddleOfChunk_OceanMonument;
	}

	public List<Biome> getValidBiomesForStructure_OceanMonument() {
		return validBiomesForStructure_OceanMonument;
	}
}
