package amidst.mojangapi.minecraftinterface;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.WorldType;

@ThreadSafe
public class LoggingMinecraftInterface implements MinecraftInterface {
	private final MinecraftInterface inner;
	
	public LoggingMinecraftInterface(MinecraftInterface minecraftInterface) {
		this.inner = minecraftInterface;
	}

	@Override
	public int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		return inner.getBiomeData(x, y, width, height, useQuarterResolution);
	}

	@Override
	public void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		AmidstLogger.info("Creating world with seed '{}' and type '{}'", seed, worldType.getName());
		AmidstLogger.info("Using the following generator options: {}", generatorOptions);
		inner.createWorld(seed, worldType, generatorOptions);
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return inner.getRecognisedVersion();
	}

}
