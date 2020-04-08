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

	private void store(int x, int y, int width, int height, boolean useQuarterResolution, int[] biomeData) {
		builder.store(x, y, width, height, useQuarterResolution, biomeData);
	}

	@Override
	public synchronized MinecraftInterface.World createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		return new World(realMinecraftInterface.createWorld(seed, worldType, generatorOptions));
	}

	@Override
	public synchronized RecognisedVersion getRecognisedVersion() {
		return realMinecraftInterface.getRecognisedVersion();
	}

	private class World implements MinecraftInterface.World {
		private final MinecraftInterface.World realMinecraftWorld;

		private World(MinecraftInterface.World realMinecraftWorld) {
			this.realMinecraftWorld = realMinecraftWorld;
		}

		@Override
		public synchronized int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
				throws MinecraftInterfaceException {
			int[] biomeData = realMinecraftWorld.getBiomeData(x, y, width, height, useQuarterResolution);
			store(x, y, width, height, useQuarterResolution, biomeData);
			return biomeData;
		}
	}
}
