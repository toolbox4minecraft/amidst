package amidst.bytedata.builder;

import java.util.List;

import amidst.bytedata.CCMatchAll;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.builder.ClassCheckerBuilder.SubClassCheckerBuilder;

public class CCMatchAllBuilder extends SubClassCheckerBuilder {
	@Override
	protected ClassChecker get() {
		List<ClassChecker> checkers = getCheckers();
		ensureCheckerPresent(checkers);
		return new CCMatchAll(checkers);
	}

	private void ensureCheckerPresent(List<ClassChecker> checkers) {
		if (checkers.size() == 0) {
			throw new IllegalStateException(
					"match all must have at least one checker");
		}
	}
}
