package amidst.mojangapi.world.versionfeatures;

import java.util.List;
import java.util.function.Function;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.icon.locationchecker.MineshaftAlgorithm;

@Immutable
public class VersionFeatures {
	private final boolean isSaveEnabled;
	private final List<Biome> validBiomesForStructure_Spawn;
	private final List<Biome> validBiomesAtMiddleOfChunk_Stronghold;
	private final int numberOfStrongholds;
	private final List<Biome> validBiomesForStructure_Village;
	private final List<Biome> validBiomesAtMiddleOfChunk_Temple;
	private final Function<Long, MineshaftAlgorithm> mineshaftAlgorithmFactory;
	private final List<Biome> validBiomesAtMiddleOfChunk_OceanMonument;
	private final List<Biome> validBiomesForStructure_OceanMonument;

	public VersionFeatures(boolean isSaveEnabled,
			List<Biome> validBiomesForStructure_Spawn,
			List<Biome> validBiomesAtMiddleOfChunk_Stronghold,
			int numberOfStrongholds,
			List<Biome> validBiomesForStructure_Village,
			List<Biome> validBiomesAtMiddleOfChunk_Temple,
			Function<Long, MineshaftAlgorithm> mineshaftAlgorithmFactory,
			List<Biome> validBiomesAtMiddleOfChunk_OceanMonument,
			List<Biome> validBiomesForStructure_OceanMonument) {
		this.isSaveEnabled = isSaveEnabled;
		this.validBiomesForStructure_Spawn = validBiomesForStructure_Spawn;
		this.validBiomesAtMiddleOfChunk_Stronghold = validBiomesAtMiddleOfChunk_Stronghold;
		this.numberOfStrongholds = numberOfStrongholds;
		this.validBiomesForStructure_Village = validBiomesForStructure_Village;
		this.validBiomesAtMiddleOfChunk_Temple = validBiomesAtMiddleOfChunk_Temple;
		this.mineshaftAlgorithmFactory = mineshaftAlgorithmFactory;
		this.validBiomesAtMiddleOfChunk_OceanMonument = validBiomesAtMiddleOfChunk_OceanMonument;
		this.validBiomesForStructure_OceanMonument = validBiomesForStructure_OceanMonument;
	}

	/**
	 * TODO: @skiphs why does it depend on the loaded minecraft version whether
	 * we can save player locations or not? we do not use the minecraft jar file
	 * to save player locations and it does not depend on the jar file which
	 * worlds can be loaded.
	 */
	public boolean isSaveEnabled() {
		return isSaveEnabled;
	}

	public List<Biome> getValidBiomesForStructure_Spawn() {
		return validBiomesForStructure_Spawn;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_Stronghold() {
		return validBiomesAtMiddleOfChunk_Stronghold;
	}

	public int getNumberOfStrongholds() {
		return numberOfStrongholds;
	}

	public List<Biome> getValidBiomesForStructure_Village() {
		return validBiomesForStructure_Village;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_Temple() {
		return validBiomesAtMiddleOfChunk_Temple;
	}

	public Function<Long, MineshaftAlgorithm> getMineshaftAlgorithmFactory() {
		return mineshaftAlgorithmFactory;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_OceanMonument() {
		return validBiomesAtMiddleOfChunk_OceanMonument;
	}

	public List<Biome> getValidBiomesForStructure_OceanMonument() {
		return validBiomesForStructure_OceanMonument;
	}
}
