package amidst.mojangapi.mocking;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;

@ThreadSafe
public class RequestStoringMinecraftInterface implements MinecraftInterface {
	private final MinecraftInterface realMinecraftInterface;
	private final BiomeDataJsonBuilder builder;

	public RequestStoringMinecraftInterface(MinecraftInterface realMinecraftInterface, BiomeDataJsonBuilder builder) {
		this.realMinecraftInterface = realMinecraftInterface;
		this.builder = builder;
	}

	@Override
	public synchronized int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		int[] biomeData = realMinecraftInterface.getBiomeData(x, y, width, height, useQuarterResolution);
		store(x, y, width, height, useQuarterResolution, biomeData);
		return biomeData;
	}

	private void store(int x, int y, int width, int height, boolean useQuarterResolution, int[] biomeData) {
		builder.store(x, y, width, height, useQuarterResolution, biomeData);
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
