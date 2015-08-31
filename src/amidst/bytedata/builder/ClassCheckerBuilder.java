package amidst.bytedata.builder;

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

		public List<ClassChecker> construct() {
			throw new IllegalStateException(
					"only the main builder can be constructed");
		}

		public ClassCheckerBuilder end() {
			parent.addChecker(get());
			return parent;
		}

		protected abstract ClassChecker get();
	}

	public static abstract class SimpleClassCheckerBuilder {
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

	public CCJustAnotherBuilder matchJustAnother(String name) {
		return setBuilder(new CCJustAnotherBuilder(name));
	}

	public CCLongBuilder matchLong(String name) {
		return setBuilder(new CCLongBuilder(name));
	}

	public CCStringBuilder matchString(String name) {
		return setBuilder(new CCStringBuilder(name));
	}

	public CCWildcardByteBuilder matchWildcardBytes(String name) {
		return setBuilder(new CCWildcardByteBuilder(name));
	}

	public CCAddMethodBuilder addMethod(String method, String methodName) {
		return setBuilder(new CCAddMethodBuilder(method, methodName));
	}

	public CCAddPropertyBuilder addProperty(String property, String propertyName) {
		return setBuilder(new CCAddPropertyBuilder(property, propertyName));
	}

	public CCMatchAllBuilder matchAll() {
		return setParent(new CCMatchAllBuilder());
	}

	public CCClassNameBuilder className(String className) {
		return setParent(new CCClassNameBuilder(className));
	}

	private <T extends SimpleClassCheckerBuilder> T setBuilder(T builder) {
		builder.setBuilder(nextBuilder);
		return builder;
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

	public List<ClassChecker> construct() {
		return checkers;
	}

	public ClassCheckerBuilder end() {
		throw new IllegalStateException("only sub builders can be ended");
	}
}
