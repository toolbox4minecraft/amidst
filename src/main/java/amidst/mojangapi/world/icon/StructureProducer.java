package amidst.mojangapi.world.icon;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public abstract class StructureProducer extends WorldIconProducer {
	protected final BiomeDataOracle biomeDataOracle;
	protected final Resolution resolution;
	protected final int size;

	protected final boolean displayNetherCoordinates;

	private CoordinatesInWorld corner;
	private WorldIconConsumer consumer;
	private int xRelativeToFragmentAsChunkResolution;
	private int yRelativeToFragmentAsChunkResolution;
	protected int chunkX;
	protected int chunkY;

	public StructureProducer(BiomeDataOracle biomeDataOracle) {
		this.biomeDataOracle = biomeDataOracle;
		this.resolution = Resolution.CHUNK;
		this.size = resolution.getStepsPerFragment();
		this.displayNetherCoordinates = displayNetherCoordinates();
	}

	@Override
	public void produce(CoordinatesInWorld corner, WorldIconConsumer consumer) {
		this.corner = corner;
		this.consumer = consumer;
		for (xRelativeToFragmentAsChunkResolution = 0; xRelativeToFragmentAsChunkResolution < size; xRelativeToFragmentAsChunkResolution++) {
			for (yRelativeToFragmentAsChunkResolution = 0; yRelativeToFragmentAsChunkResolution < size; yRelativeToFragmentAsChunkResolution++) {
				generateAt();
			}
		}
	}

	// TODO: use longs?
	private void generateAt() {
		chunkX = xRelativeToFragmentAsChunkResolution
				+ (int) corner.getXAs(resolution);
		chunkY = yRelativeToFragmentAsChunkResolution
				+ (int) corner.getYAs(resolution);
		if (isValidLocation()) {
			DefaultWorldIconTypes worldIconType = getWorldIconType();
			if (worldIconType != null) {
				consumer.consume(new WorldIcon(createCoordinates(),
						worldIconType.getName(), worldIconType.getImage(),
						displayNetherCoordinates));
			}
		}
	}

	private CoordinatesInWorld createCoordinates() {
		long xInWorld = resolution
				.convertFromThisToWorld(xRelativeToFragmentAsChunkResolution);
		long yInWorld = resolution
				.convertFromThisToWorld(yRelativeToFragmentAsChunkResolution);
		return corner.add(xInWorld, yInWorld);
	}

	protected abstract boolean isValidLocation();

	protected abstract DefaultWorldIconTypes getWorldIconType();

	protected abstract boolean displayNetherCoordinates();
}
