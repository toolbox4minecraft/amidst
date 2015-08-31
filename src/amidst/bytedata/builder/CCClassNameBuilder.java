package amidst.bytedata.builder;

import java.util.List;

import amidst.bytedata.CCClassName;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.builder.ClassCheckerBuilder.SubClassCheckerBuilder;

public class CCClassNameBuilder extends SubClassCheckerBuilder {
	private String className;

	public CCClassNameBuilder(String className) {
		this.className = className;
	}

	@Override
	protected ClassChecker get() {
		List<ClassChecker> checkers = getCheckers();
		ensureExactlyOneChecker(checkers);
		return new CCClassName(className, checkers.get(0));
	}

	private void ensureExactlyOneChecker(List<ClassChecker> checkers) {
		if (checkers.size() != 1) {
			throw new IllegalStateException(
					"class name can only have one checker");
		}
	}
}
