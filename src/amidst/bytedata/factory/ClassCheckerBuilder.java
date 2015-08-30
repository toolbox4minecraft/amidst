package amidst.bytedata.factory;

import java.util.ArrayList;
import java.util.List;

import amidst.bytedata.ClassChecker;

public class ClassCheckerBuilder {
	public static abstract class SubClassCheckerBuilder extends
			ClassCheckerBuilder {
		private ClassCheckerBuilder parent;

		void setParent(ClassCheckerBuilder parent) {
			this.parent = parent;
		}

		public ClassChecker[] construct() {
			throw new IllegalStateException(
					"only the main builder can be constructed");
		}

		public ClassCheckerBuilder end() {
			parent.addChecker(get());
			return parent;
		}

		protected abstract ClassChecker get();
	}

	public static abstract class ClassCheckerFactory {
		private ClassCheckerBuilder builder;

		void setBuilder(ClassCheckerBuilder builder) {
			this.builder = builder;
		}

		public ClassCheckerBuilder end() {
			builder.addChecker(get());
			return builder;
		}

		protected abstract ClassChecker get();
	}

	private List<ClassChecker> checkers = new ArrayList<ClassChecker>();
	private ClassCheckerBuilder nextBuilder = this;

	public ClassCheckerBuilder() {
	}

	public CCJustAnotherFactory matchJustAnother(String name) {
		return setBuilder(new CCJustAnotherFactory(name));
	}

	public CCLongMatchFactory matchLong(String name) {
		return setBuilder(new CCLongMatchFactory(name));
	}

	public CCMethodPresetFactory matchMethodPreset(String name) {
		return setBuilder(new CCMethodPresetFactory(name));
	}

	public CCPropertyPresetFactory matchPropertyPreset(String name) {
		return setBuilder(new CCPropertyPresetFactory(name));
	}

	public CCStringMatchFactory matchString(String name) {
		return setBuilder(new CCStringMatchFactory(name));
	}

	public CCWildcardByteSearchFactory matchWildcardBytes(String name) {
		return setBuilder(new CCWildcardByteSearchFactory(name));
	}

	public CCMultiBuilder multi() {
		return setParent(new CCMultiBuilder());
	}

	public CCRequireBuilder require(String... requiredNames) {
		return setParent(new CCRequireBuilder(requiredNames));
	}

	private <T extends ClassCheckerFactory> T setBuilder(T factory) {
		factory.setBuilder(nextBuilder);
		return factory;
	}

	private <T extends SubClassCheckerBuilder> T setParent(T builder) {
		builder.setParent(this);
		return builder;
	}

	protected void addChecker(ClassChecker checker) {
		checkers.add(checker);
	}

	protected List<ClassChecker> getCheckers() {
		return checkers;
	}

	protected void setNextBuilder(ClassCheckerBuilder nextBuilder) {
		this.nextBuilder = nextBuilder;
	}

	public ClassChecker[] construct() {
		return checkers.toArray(new ClassChecker[checkers.size()]);
	}

	public ClassCheckerBuilder end() {
		throw new IllegalStateException("only sub builders can be ended");
	}
}
