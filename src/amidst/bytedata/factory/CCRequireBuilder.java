package amidst.bytedata.factory;

import java.util.List;

import amidst.bytedata.CCRequire;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.factory.ClassCheckerBuilder.SubClassCheckerBuilder;

public class CCRequireBuilder extends SubClassCheckerBuilder {
	private String[] requiredNames;

	public CCRequireBuilder(String[] requiredNames) {
		this.requiredNames = requiredNames;
	}

	@Override
	protected ClassChecker get() {
		List<ClassChecker> checkers = getCheckers();
		ensureExactlyOneChecker(checkers);
		return new CCRequire(checkers.get(0), requiredNames);
	}

	private void ensureExactlyOneChecker(List<ClassChecker> checkers) {
		if (checkers.size() != 1) {
			throw new IllegalStateException("require can only have one checker");
		}
	}
}
