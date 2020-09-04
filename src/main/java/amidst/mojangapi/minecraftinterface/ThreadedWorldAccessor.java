package amidst.mojangapi.minecraftinterface;

import java.util.function.Function;
import java.util.function.Supplier;

import amidst.mojangapi.world.Dimension;

public class ThreadedWorldAccessor implements MinecraftInterface.WorldAccessor {
	private final ThreadLocal<MinecraftInterface.WorldAccessor> rawThreadedAccessor;
	
	public ThreadedWorldAccessor(Supplier<MinecraftInterface.WorldAccessor> innerAccessorFactory) {
		this.rawThreadedAccessor = ThreadLocal.withInitial(innerAccessorFactory);
	}

	@Override
	public<T> T getBiomeData(Dimension dimension, int x, int y, int width, int height, boolean useQuarterResolution,
			Function<int[], T> biomeDataMapper) throws MinecraftInterfaceException {
		return rawThreadedAccessor.get().getBiomeData(dimension, x, y, width, height, useQuarterResolution, biomeDataMapper);
	}
}
