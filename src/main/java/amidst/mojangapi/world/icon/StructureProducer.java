package amidst.mojangapi.world.icon;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

@NotThreadSafe
public abstract class StructureProducer extends WorldIconProducer {
	private final Resolution resolution;
	private final int size;
	private final LocationChecker checker;
	private final WorldIconTypeProvider provider;
	private final boolean displayNetherCoordinates;

	public StructureProducer(Resolution resolution, LocationChecker checker,
			WorldIconTypeProvider provider, boolean displayNetherCoordinates) {
		this.resolution = resolution;
		this.size = resolution.getStepsPerFragment();
		this.checker = checker;
		this.provider = provider;
		this.displayNetherCoordinates = displayNetherCoordinates;
	}

	@Override
	public void produce(CoordinatesInWorld corner, WorldIconConsumer consumer) {
		for (int xRelativeToFragment = 0; xRelativeToFragment < size; xRelativeToFragment++) {
			for (int yRelativeToFragment = 0; yRelativeToFragment < size; yRelativeToFragment++) {
				generateAt(corner, consumer, xRelativeToFragment,
						yRelativeToFragment);
			}
		}
	}

	// TODO: use longs?
	private void generateAt(CoordinatesInWorld corner,
			WorldIconConsumer consumer, int xRelativeToFragment,
			int yRelativeToFragment) {
		int x = xRelativeToFragment + (int) corner.getXAs(resolution);
		int y = yRelativeToFragment + (int) corner.getYAs(resolution);
		if (checker.isValidLocation(x, y)) {
			DefaultWorldIconTypes worldIconType = provider.get(x, y);
			if (worldIconType != null) {
				CoordinatesInWorld coordinates = createCoordinates(corner,
						xRelativeToFragment, yRelativeToFragment);
				consumer.consume(new WorldIcon(coordinates, worldIconType
						.getName(), worldIconType.getImage(),
						displayNetherCoordinates));
			}
		}
	}

	private CoordinatesInWorld createCoordinates(CoordinatesInWorld corner,
			int xRelativeToFragment, int yRelativeToFragment) {
		long xInWorld = resolution.convertFromThisToWorld(xRelativeToFragment);
		long yInWorld = resolution.convertFromThisToWorld(yRelativeToFragment);
		return corner.add(xInWorld, yInWorld);
	}
}
