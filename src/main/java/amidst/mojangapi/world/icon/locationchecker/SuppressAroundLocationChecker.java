package amidst.mojangapi.world.icon.locationchecker;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class SuppressAroundLocationChecker implements LocationChecker {

	private final LocationChecker locationChecker;
	private final int distance;

	public SuppressAroundLocationChecker(LocationChecker locationChecker, int distance) {
		this.locationChecker = locationChecker;
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
				if (locationChecker.isValidLocation(i, j)) {
					return false;
				}
			}
		}

		return true;
	}

}
