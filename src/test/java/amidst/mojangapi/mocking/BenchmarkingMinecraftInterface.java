package amidst.mojangapi.mocking;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.mocking.json.BiomeRequestRecordJson;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldType;

@ThreadSafe
public class BenchmarkingMinecraftInterface implements MinecraftInterface {
	private final MinecraftInterface inner;
	private final List<BiomeRequestRecordJson> records;

	public BenchmarkingMinecraftInterface(MinecraftInterface inner, List<BiomeRequestRecordJson> records) {
		this.inner = inner;
		this.records = Collections.synchronizedList(records);
	}

	@Override
	public MinecraftInterface.World createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		return new World(inner.createWorld(seed, worldType, generatorOptions));
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return inner.getRecognisedVersion();
	}

	private class World implements MinecraftInterface.World {
		private final MinecraftInterface.World innerWorld;

		private World(MinecraftInterface.World innerWorld) {
			this.innerWorld = innerWorld;
		}

		@Override
		public<T> T getBiomeData(Dimension dimension, int x, int y, int width, int height,
				boolean useQuarterResolution, Function<int[], T> biomeDataMapper) throws MinecraftInterfaceException {
			long start = System.nanoTime();
			return innerWorld.getBiomeData(dimension, x, y, width, height, useQuarterResolution, biomeData -> {
				long end = System.nanoTime();
				String thread = Thread.currentThread().getName();
				records.add(new BiomeRequestRecordJson(x, y, width, height, useQuarterResolution, start, end-start, thread));

				return biomeDataMapper.apply(biomeData);
			});
		}

		@Override
		public Set<Dimension> supportedDimensions() {
			return innerWorld.supportedDimensions();
		}
	}
}
