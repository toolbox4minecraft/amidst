package amidst.mojangapi.world.filter;

import java.util.Set;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.World;

@NotThreadSafe
public class WorldFilter_Biome extends WorldFilter {
	private final Set<Short> validBiomeIndexes;
	private short[][] region;

	public WorldFilter_Biome(long worldFilterSize, Set<Short> validBiomeIndexes) {
		super(worldFilterSize);
		this.validBiomeIndexes = validBiomeIndexes;
		this.region = new short[(int) this.quarterFilterSize * 2][(int) this.quarterFilterSize * 2];
	}

	@Override
	public boolean isValid(World world) {
		world.getBiomeDataOracle().populateArray(corner, region, true);
		for (short[] row : region) {
			for (short entry : row) {
				if (validBiomeIndexes.contains(entry)) {
					return true;
				}
			}
		}
		return false;
	}
}
