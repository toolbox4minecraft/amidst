package amidst.bytedata.factory;

import java.util.Objects;

import amidst.bytedata.CCStringMatch;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.factory.ClassCheckerBuilder.ClassCheckerFactory;

public class CCStringMatchFactory extends ClassCheckerFactory {
	private String name;
	private String checkData;

	public CCStringMatchFactory(String name) {
		this.name = name;
	}

	public CCStringMatchFactory data(String checkData) {
		this.checkData = checkData;
		return this;
	}

	@Override
	protected ClassChecker get() {
		Objects.requireNonNull(checkData, "string matcher needs data to check");
		return new CCStringMatch(name, checkData);
	}
}
