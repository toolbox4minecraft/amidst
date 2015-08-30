package amidst.bytedata.factory;

import amidst.bytedata.CCJustAnother;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.factory.ClassCheckerBuilder.ClassCheckerFactory;

public class CCJustAnotherFactory extends ClassCheckerFactory {
	private String name;

	public CCJustAnotherFactory(String name) {
		this.name = name;
	}

	@Override
	protected ClassChecker get() {
		return new CCJustAnother(name);
	}
}
