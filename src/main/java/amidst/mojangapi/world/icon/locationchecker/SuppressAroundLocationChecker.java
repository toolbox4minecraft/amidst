package amidst.mojangapi.world.icon.locationchecker;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;

/**
 * Only use for non regional checks.
 */
@ThreadSafe
public class SuppressAroundLocationChecker<T> implements LocationChecker {

	private final WorldIconProducer<T> worldIconProducer;
	private final int distance;

	public SuppressAroundLocationChecker(WorldIconProducer<T> worldIconProducer, int distance) {
		this.worldIconProducer = worldIconProducer;
		this.distance = distance;
	}

	@Override
	public boolean isValidLocation(int x, int y) {
		if (distance < 0) {
			return true;
		}

		int xMin = x - distance;
		int xMax = x + distance;
		int yMin = y - distance;
		int yMax = y + distance;

		for (int i = xMin; i < xMax; i++) {
			for (int j = yMin; j < yMax; j++) {
				if (worldIconProducer.isValidLocation(i, j)) {
					return false;
				}
			}
		}

		return true;
	}

}
