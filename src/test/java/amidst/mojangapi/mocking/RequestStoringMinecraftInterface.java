package amidst.mojangapi.mocking;

import java.util.Set;
import java.util.function.Function;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldType;

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
	public MinecraftInterface.WorldConfig createWorldConfig() throws MinecraftInterfaceException {
		return new WorldConfig(realMinecraftInterface.createWorldConfig());
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return realMinecraftInterface.getRecognisedVersion();
	}
	
	private class WorldConfig implements MinecraftInterface.WorldConfig {
		private final MinecraftInterface.WorldConfig realWorldConfig;

		private WorldConfig(MinecraftInterface.WorldConfig realWorldConfig) {
			this.realWorldConfig = realWorldConfig;
		}
		
		@Override
		public Set<Dimension> supportedDimensions() {
			return realWorldConfig.supportedDimensions();
		}
		
		@Override
		public MinecraftInterface.WorldAccessor createWorldAccessor(long seed, WorldType worldType, String generatorOptions)
				throws MinecraftInterfaceException {
			return new WorldAccessor(realWorldConfig.createWorldAccessor(seed, worldType, generatorOptions));
		}
	}

	private class WorldAccessor implements MinecraftInterface.WorldAccessor {
		private final MinecraftInterface.WorldAccessor realWorldAccessor;

		private WorldAccessor(MinecraftInterface.WorldAccessor realWorldAccessor) {
			this.realWorldAccessor = realWorldAccessor;
		}

		@Override
		public<T> T getBiomeData(Dimension dimension, int x, int y, int width, int height,
				boolean useQuarterResolution, Function<int[], T> biomeDataMapper) throws MinecraftInterfaceException {
			return realWorldAccessor.getBiomeData(dimension, x, y, width, height, useQuarterResolution, biomeData -> {
				store(dimension, x, y, width, height, useQuarterResolution, biomeData);
				return biomeDataMapper.apply(biomeData);
			});
		}
	}
}
