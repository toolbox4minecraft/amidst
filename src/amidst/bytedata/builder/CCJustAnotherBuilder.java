package amidst.bytedata.builder;

import amidst.bytedata.CCJustAnother;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.builder.ClassCheckerBuilder.SimpleClassCheckerBuilder;

public class CCJustAnotherBuilder extends SimpleClassCheckerBuilder {
	private String name;

	public CCJustAnotherBuilder(String name) {
		this.name = name;
	}

	@Override
	protected ClassChecker get() {
		return new CCJustAnother(name);
	}
}
