package amidst.mojangapi.world.icon.locationchecker;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class BiomeLocationChecker implements LocationChecker {
	private final BiomeDataOracle biomeDataOracle;
	private final List<Integer> validBiomeIds;

	public BiomeLocationChecker(BiomeDataOracle biomeDataOracle, List<Integer> validBiomeIds) {
		this.biomeDataOracle = biomeDataOracle;
		this.validBiomeIds = validBiomeIds;
	}

	@Override
	public boolean isValidLocation(int x, int y) {
		return biomeDataOracle.isValidBiomeAtMiddleOfChunk(x, y, validBiomeIds);
	}

	@Override
	public boolean hasValidLocations() {
		return !validBiomeIds.isEmpty();
	}
}
