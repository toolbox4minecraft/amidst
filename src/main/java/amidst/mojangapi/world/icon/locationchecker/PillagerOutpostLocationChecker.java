package amidst.mojangapi.world.icon.locationchecker;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class PillagerOutpostLocationChecker implements LocationChecker {

	public PillagerOutpostLocationChecker(
			long seed, BiomeDataOracle biomeDataOracle, List<Biome> validBiomesForStructure, boolean doComplexVillageCheck) {

	}

	@Override
	public boolean isValidLocation(int x, int y) {
		// TODO: implement algorithm
		return false;
	}

}
