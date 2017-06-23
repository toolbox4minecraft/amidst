package amidst.mojangapi.world.filter;

import java.util.Set;

import amidst.documentation.NotThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.biome.BiomeData;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;

@NotThreadSafe
public class WorldFilter_Biome extends WorldFilter {
	private final Set<Short> validBiomeIndexes;

	public WorldFilter_Biome(int worldFilterSize, Set<Short> validBiomeIndexes) {
		super(worldFilterSize);
		this.validBiomeIndexes = validBiomeIndexes;
	}

	@Override
	public boolean isValid(World world) {
		try {
			BiomeData data = world.getBiomeDataOracle().getBiomeData(Region.box(Coordinates.origin(), this.quarterFilterSize), true);
			return data.checkAll((x, y, b) -> validBiomeIndexes.contains(b));
			
		} catch (MinecraftInterfaceException e) {
			AmidstLogger.error(e);
			return false;
		}
	}
}
