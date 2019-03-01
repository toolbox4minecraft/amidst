package amidst.mojangapi.world.icon.locationchecker;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class StructureBiomeLocationChecker implements LocationChecker {
	private final BiomeDataOracle biomeDataOracle;
	private final int size;
	private final List<Biome> validBiomes;

	public StructureBiomeLocationChecker(BiomeDataOracle biomeDataOracle, int size, List<Biome> validBiomes) {
		this.biomeDataOracle = biomeDataOracle;
		this.size = size;
		this.validBiomes = validBiomes;
	}

	@Override
	public boolean isValidLocation(int x, int y) {
		return biomeDataOracle.isValidBiomeForStructureAtMiddleOfChunk(x, y, size, validBiomes);
	}

	@Override
	public boolean hasValidLocations() {
		return !validBiomes.isEmpty();
	}
}
