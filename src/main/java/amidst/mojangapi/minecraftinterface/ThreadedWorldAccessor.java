package amidst.mojangapi.minecraftinterface;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import amidst.mojangapi.minecraftinterface.MinecraftInterface.WorldAccessor;
import amidst.mojangapi.world.Dimension;
import amidst.util.FaillibleFunction;

public class ThreadedWorldAccessor implements WorldAccessor {
	private final ThreadLocal<WorldAccessor> rawThreadedAccessor;
	private final Set<Dimension> supportedDimensions;
	
	public ThreadedWorldAccessor(FaillibleFunction<Void, WorldAccessor, MinecraftInterfaceException> innerAccessorFactory)
			throws MinecraftInterfaceException {
		AtomicReference<MinecraftInterface.WorldAccessor> initialWorld =
								new AtomicReference<>(innerAccessorFactory.apply(null));
		
		this.supportedDimensions = initialWorld.get().supportedDimensions();
		this.rawThreadedAccessor = ThreadLocal.withInitial(() -> {
			WorldAccessor world = initialWorld.getAndSet(null);
			if (world == null) {
				try {
					return innerAccessorFactory.apply(null);
				} catch (MinecraftInterfaceException e) {
					throw new RuntimeException(e);
				}
			} else {
				return world;
			}
		});
	}
	
	@Override
	public <T> T getBiomeData(Dimension dimension, int x, int y, int width, int height, boolean useQuarterResolution,
			Function<int[], T> biomeDataMapper) throws MinecraftInterfaceException {
		return rawThreadedAccessor.get().getBiomeData(dimension, x, y, width, height, useQuarterResolution,
				biomeDataMapper);
	}
	
	@Override
	public Set<Dimension> supportedDimensions() {
		return supportedDimensions;
	}
}
