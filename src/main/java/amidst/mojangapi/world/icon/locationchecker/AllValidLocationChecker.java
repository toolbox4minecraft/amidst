package amidst.mojangapi.world.icon.locationchecker;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class AllValidLocationChecker implements LocationChecker {
	private final LocationChecker[] checkers;
	private final boolean hasValidLocation;

	public AllValidLocationChecker(LocationChecker... checkers) {
		this.checkers = checkers;
		hasValidLocation = getHasValidLocation(checkers);
	}

	@Override
	public boolean isValidLocation(int x, int y) {
		for (LocationChecker checker : checkers) {
			if (!checker.isValidLocation(x, y)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean hasValidLocations() {
		return hasValidLocation;
	}

	private static boolean getHasValidLocation(LocationChecker[] checkers) {
		for(LocationChecker checker: checkers) {
			if(!checker.hasValidLocations())
				return false;
		}
		return true;
	}
}
