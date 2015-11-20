package amidst.minecraft.world.finder;

import java.util.List;

import amidst.map.MapMarkers;
import amidst.minecraft.Biome;

public class NetherFortressFinder extends StructureFinder {
	@Override
	protected boolean isValidLocation() {
		int i = chunkX >> 4;
		int j = chunkY >> 4;
		random.setSeed(i ^ j << 4 ^ world.getSeed());
		random.nextInt();
		if (random.nextInt(3) != 0) {
			return false;
		}
		if (chunkX != (i << 4) + 4 + random.nextInt(8)) {
			return false;
		}
		return chunkY == (j << 4) + 4 + random.nextInt(8);
	}

	@Override
	protected MapMarkers getMapMarker() {
		return MapMarkers.NETHER_FORTRESS;
	}

	@Override
	protected List<Biome> getValidBiomesForStructure() {
		return null; // not used
	}

	@Override
	protected List<Biome> getValidBiomesAtMiddleOfChunk() {
		return null; // not used
	}

	@Override
	protected int updateValue(int value) {
		return -1; // not used
	}

	@Override
	protected long getMagicNumberForSeed1() {
		return -1; // not used
	}

	@Override
	protected long getMagicNumberForSeed2() {
		return -1; // not used
	}

	@Override
	protected long getMagicNumberForSeed3() {
		return -1; // not used
	}

	@Override
	protected byte getMaxDistanceBetweenScatteredFeatures() {
		return -1; // not used
	}

	@Override
	protected byte getMinDistanceBetweenScatteredFeatures() {
		return -1; // not used
	}

	@Override
	protected int getStructureSize() {
		return -1; // not used
	}
}
