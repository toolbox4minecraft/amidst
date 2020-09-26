package amidst.mojangapi.mocking;

import java.util.Set;
import java.util.function.Function;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldOptions;

@ThreadSafe
public class RequestStoringMinecraftInterface implements MinecraftInterface {
	private final MinecraftInterface realMinecraftInterface;
	private final BiomeDataJsonBuilder builder;

	public RequestStoringMinecraftInterface(MinecraftInterface realMinecraftInterface, BiomeDataJsonBuilder builder) {
		this.realMinecraftInterface = realMinecraftInterface;
		this.builder = builder;
	}

	private void store(Dimension dimension, int x, int y, int width, int height, boolean useQuarterResolution, int[] biomeData) {
		builder.store(dimension, x, y, width, height, useQuarterResolution, biomeData);
	}

	@Override
	public synchronized MinecraftInterface.WorldAccessor createWorldAccessor(WorldOptions worldOptions) throws MinecraftInterfaceException {
		return new WorldAccessor(realMinecraftInterface.createWorldAccessor(worldOptions));
	}

	@Override
	public synchronized RecognisedVersion getRecognisedVersion() {
		return realMinecraftInterface.getRecognisedVersion();
	}

	private class WorldAccessor implements MinecraftInterface.WorldAccessor {
		private final MinecraftInterface.WorldAccessor realMinecraftWorld;

		private WorldAccessor(MinecraftInterface.WorldAccessor realMinecraftWorld) {
			this.realMinecraftWorld = realMinecraftWorld;
		}

		@Override
		public synchronized<T> T getBiomeData(Dimension dimension, int x, int y, int width, int height,
				boolean useQuarterResolution, Function<int[], T> biomeDataMapper)
				throws MinecraftInterfaceException {
			return realMinecraftWorld.getBiomeData(dimension, x, y, width, height, useQuarterResolution, biomeData -> {
				store(dimension, x, y, width, height, useQuarterResolution, biomeData);
				return biomeDataMapper.apply(biomeData);
			});
		}

		@Override
		public Set<Dimension> supportedDimensions() {
			return realMinecraftWorld.supportedDimensions();
		}
	}
}
