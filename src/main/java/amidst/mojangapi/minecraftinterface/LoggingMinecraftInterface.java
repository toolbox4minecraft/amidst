package amidst.mojangapi.minecraftinterface;

import java.util.concurrent.atomic.AtomicBoolean;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldType;

@ThreadSafe
public class LoggingMinecraftInterface implements MinecraftInterface {
	private final MinecraftInterface inner;
	
	// This is used so we don't log the message every time a thread creates a new WorldAccessor.
	private final AtomicBoolean shouldLogAccessor = new AtomicBoolean(true);

	public LoggingMinecraftInterface(MinecraftInterface minecraftInterface) {
		this.inner = minecraftInterface;
	}
	
	@Override
	public WorldAccessor createWorldAccessor(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		WorldAccessor innerAccessor;
		
		if(shouldLogAccessor.getAndSet(false)) {
			AmidstLogger.info("Creating world with seed '{}' and type '{}'", seed, worldType.getName());
			AmidstLogger.info("Using the following generator options: {}", generatorOptions);
			
			innerAccessor = inner.createWorldAccessor(seed, worldType, generatorOptions);
			
			StringBuilder sb = new StringBuilder("Supported dimensions for world: ");
			boolean firstDim = true;
			for(Dimension dimension : innerAccessor.supportedDimensions()) {
				if(firstDim) {
					firstDim = false;
				} else {
					sb.append(", ");
				}
				
				sb.append(dimension.getDisplayName());
			}
			AmidstLogger.info(sb.toString());
		} else {
			innerAccessor = inner.createWorldAccessor(seed, worldType, generatorOptions);
		}
		
		return innerAccessor;
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return inner.getRecognisedVersion();
	}
}
