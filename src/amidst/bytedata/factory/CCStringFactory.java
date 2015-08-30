package amidst.bytedata.factory;

import java.util.Objects;

import amidst.bytedata.CCString;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.factory.ClassCheckerBuilder.ClassCheckerFactory;

public class CCStringFactory extends ClassCheckerFactory {
	private String name;
	private String checkData;

	public CCStringFactory(String name) {
		this.name = name;
	}

	public CCStringFactory data(String checkData) {
		this.checkData = checkData;
		return this;
	}

	@Override
	protected ClassChecker get() {
		Objects.requireNonNull(checkData, "string matcher needs data to check");
		return new CCString(name, checkData);
	}
}
