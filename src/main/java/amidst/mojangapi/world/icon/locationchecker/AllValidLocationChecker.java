package amidst.mojangapi.world.icon.locationchecker;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class AllValidLocationChecker implements LocationChecker {
	private final LocationChecker[] checkers;

	public AllValidLocationChecker(LocationChecker... checkers) {
		this.checkers = checkers;
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
}
