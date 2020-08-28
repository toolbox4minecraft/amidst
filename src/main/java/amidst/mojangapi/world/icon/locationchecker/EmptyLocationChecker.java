package amidst.mojangapi.world.icon.locationchecker;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class EmptyLocationChecker implements LocationChecker {
	public EmptyLocationChecker() {
	}

	@Override
	public boolean isValidLocation(int x, int y) {
		return false;
	}

	@Override
	public boolean hasValidLocations() {
		return false;
	}
}
