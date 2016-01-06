package amidst.mojangapi.world.icon;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

@NotThreadSafe
public abstract class StructureProducer extends WorldIconProducer {
	protected final Resolution resolution;
	protected final int size;
	protected final boolean displayNetherCoordinates;

	private CoordinatesInWorld corner;
	private WorldIconConsumer consumer;
	private int xRelativeToFragmentAsChunkResolution;
	private int yRelativeToFragmentAsChunkResolution;
	private int chunkX;
	private int chunkY;

	public StructureProducer() {
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
		if (isValidLocation(chunkX, chunkY)) {
			DefaultWorldIconTypes worldIconType = getWorldIconType(chunkX,
					chunkY);
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

	protected abstract boolean isValidLocation(int x, int y);

	protected abstract DefaultWorldIconTypes getWorldIconType(int x, int y);

	protected abstract boolean displayNetherCoordinates();
}
