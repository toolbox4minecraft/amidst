package amidst.mojangapi.world.icon.locationchecker;

import amidst.documentation.ThreadSafe;

/**
 * This should ONLY be used for things where we have to check against
 * it every time. For example, we have to do this with biome checks
 * and buried treasures.
 */
@ThreadSafe
public interface LocationChecker {
	boolean isValidLocation(int x, int y);

	default boolean hasValidLocations() {
		return true;
	}
}
