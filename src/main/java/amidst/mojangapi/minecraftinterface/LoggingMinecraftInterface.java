package amidst.mojangapi.minecraftinterface;

import java.util.concurrent.atomic.AtomicBoolean;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.SeedHistoryLogger;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.versionfeatures.DefaultVersionFeatures;
import amidst.mojangapi.world.versionfeatures.VersionFeatures;

@ThreadSafe
public class LoggingMinecraftInterface implements MinecraftInterface {
	private final MinecraftInterface inner;
	
	// This is used so we don't log the message every time a thread creates a new WorldAccessor.
	private final AtomicBoolean shouldLogAccessor = new AtomicBoolean(true);

	public LoggingMinecraftInterface(MinecraftInterface minecraftInterface) {
		this.inner = minecraftInterface;
	}

	public void logNextAccessor() {
		shouldLogAccessor.set(true);
	}

	@Override
	public WorldAccessor createWorldAccessor(WorldOptions worldOptions) throws MinecraftInterfaceException {
		WorldAccessor innerAccessor;
		
		if(shouldLogAccessor.getAndSet(false)) {
			AmidstLogger.info("Creating world with seed '{}' and type '{}'", worldOptions.getWorldSeed().getLong(), worldOptions.getWorldType().getName());
			AmidstLogger.info("Using the following generator options: {}", worldOptions.getGeneratorOptions());
			
			innerAccessor = inner.createWorldAccessor(worldOptions);
			
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
			innerAccessor = inner.createWorldAccessor(worldOptions);
		}
		
		return innerAccessor;
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return inner.getRecognisedVersion();
	}

	@Override
	public VersionFeatures initInterfaceAndGetFeatures(WorldOptions worldOptions, MinecraftInterface minecraftInterface, SeedHistoryLogger seedHistoryLogger)
			throws MinecraftInterfaceException {
		RecognisedVersion recognisedVersion = minecraftInterface.getRecognisedVersion();
		if(minecraftInterface instanceof LoggingMinecraftInterface) {
			((LoggingMinecraftInterface) minecraftInterface).logNextAccessor();
		}
		MinecraftInterface.WorldAccessor worldAccessor = new ThreadedWorldAccessor(v -> minecraftInterface.createWorldAccessor(worldOptions));
		seedHistoryLogger.log(recognisedVersion, worldOptions.getWorldSeed());
		return DefaultVersionFeatures.builder(worldOptions, worldAccessor).create(recognisedVersion);
	}
}
