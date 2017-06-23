package amidst.mojangapi.mocking;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.Region;

@ThreadSafe
public class RequestStoringMinecraftInterface implements MinecraftInterface {
	private final MinecraftInterface realMinecraftInterface;
	private final BiomeDataJsonBuilder builder;

	public RequestStoringMinecraftInterface(MinecraftInterface realMinecraftInterface, BiomeDataJsonBuilder builder) {
		this.realMinecraftInterface = realMinecraftInterface;
		this.builder = builder;
	}

	@Override
	public synchronized int[] getBiomeData(Region.Box region, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		int[] biomeData = realMinecraftInterface.getBiomeData(region, useQuarterResolution);
		store(region, useQuarterResolution, biomeData);
		return biomeData;
	}

	private void store(Region.Box region, boolean useQuarterResolution, int[] biomeData) {
		builder.store(region, useQuarterResolution, biomeData);
	}

	@Override
	public synchronized void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		realMinecraftInterface.createWorld(seed, worldType, generatorOptions);
	}

	@Override
	public synchronized RecognisedVersion getRecognisedVersion() {
		return realMinecraftInterface.getRecognisedVersion();
	}
}
