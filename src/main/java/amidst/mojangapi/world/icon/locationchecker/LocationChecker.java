package amidst.mojangapi.world.icon.locationchecker;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.coordinates.Coordinates;

@ThreadSafe
public interface LocationChecker {
	boolean isValidLocation(int x, int y);

	default boolean isValidLocation(Coordinates location) {
		return isValidLocation((int) location.getX(), (int) location.getY());
	}
}
