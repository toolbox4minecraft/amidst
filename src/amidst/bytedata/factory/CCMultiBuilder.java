package amidst.bytedata.factory;

import java.util.List;

import amidst.bytedata.CCMulti;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.factory.ClassCheckerBuilder.SubClassCheckerBuilder;

public class CCMultiBuilder extends SubClassCheckerBuilder {
	@Override
	protected ClassChecker get() {
		List<ClassChecker> checkers = getCheckers();
		ensureCheckerPresent(checkers);
		return new CCMulti(checkers.toArray(new ClassChecker[checkers.size()]));
	}

	private void ensureCheckerPresent(List<ClassChecker> checkers) {
		if (checkers.size() == 0) {
			throw new IllegalStateException(
					"multi must have at least one checker");
		}
	}
}
