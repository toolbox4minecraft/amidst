package amidst.mojangapi.minecraftinterface;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldType;

@ThreadSafe
public class LoggingMinecraftInterface implements MinecraftInterface {
	private final MinecraftInterface inner;

	public LoggingMinecraftInterface(MinecraftInterface minecraftInterface) {
		this.inner = minecraftInterface;
	}

	@Override
	public MinecraftInterface.WorldConfig createWorldConfig() throws MinecraftInterfaceException {
		MinecraftInterface.WorldConfig config = new WorldConfig();
		
		StringBuilder sb = new StringBuilder("Supported dimensions for world configuration: ");
		boolean firstDim = true;
		for(Dimension dimension : config.supportedDimensions()) {
			if(firstDim) {
				firstDim = false;
			} else {
				sb.append(", ");
			}
			
			sb.append(dimension.getDisplayName());
		}
		AmidstLogger.info(sb.toString());
		
		return new WorldConfig();
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return inner.getRecognisedVersion();
	}

	private class WorldConfig implements MinecraftInterface.WorldConfig {
		private final MinecraftInterface.WorldConfig innerConfig;
		
		// This is used so we don't log the message every time a thread creates a new WorldAccessor.
		private final AtomicBoolean shouldLogAccessor = new AtomicBoolean(true);
		
		private WorldConfig() throws MinecraftInterfaceException {
			this.innerConfig = inner.createWorldConfig();
		}

		@Override
		public Set<Dimension> supportedDimensions() {
			return innerConfig.supportedDimensions();
		}

		@Override
		public WorldAccessor createWorldAccessor(long seed, WorldType worldType, String generatorOptions)
				throws MinecraftInterfaceException {
			if(shouldLogAccessor.getAndSet(false)) {
				AmidstLogger.info("Creating world with seed '{}' and type '{}'", seed, worldType.getName());
				AmidstLogger.info("Using the following generator options: {}", generatorOptions);
			}
			
			return innerConfig.createWorldAccessor(seed, worldType, generatorOptions);
		}
	}
}
