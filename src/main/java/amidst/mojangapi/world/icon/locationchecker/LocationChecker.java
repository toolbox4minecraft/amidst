package amidst.mojangapi.world.icon.locationchecker;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public interface LocationChecker {
	boolean isValidLocation(int x, int y);
}
