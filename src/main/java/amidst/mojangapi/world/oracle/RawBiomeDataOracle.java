package amidst.mojangapi.world.oracle;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.biome.BiomeData;
import amidst.mojangapi.world.coordinates.Region;

@ThreadSafe
public class RawBiomeDataOracle extends BiomeDataOracle {
	private final MinecraftInterface minecraftInterface;
	
	public RawBiomeDataOracle(MinecraftInterface minecraftInterface) {
		this.minecraftInterface = minecraftInterface;
		
	}

	@Override
	protected BiomeData doGetBiomeData(Region.Box region, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		
		int[] data = minecraftInterface.getBiomeData(region, useQuarterResolution);
		return new BiomeData(data, region.getWidth(), region.getHeight()).view();	
	}
}
