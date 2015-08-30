package amidst.bytedata.factory;

import java.util.Objects;

import amidst.bytedata.CCLongMatch;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.factory.ClassCheckerBuilder.ClassCheckerFactory;

public class CCLongMatchFactory extends ClassCheckerFactory {
	private String name;
	private long[] checkData;

	public CCLongMatchFactory(String name) {
		this.name = name;
	}

	public CCLongMatchFactory data(long... checkData) {
		this.checkData = checkData;
		return this;
	}

	@Override
	protected ClassChecker get() {
		Objects.requireNonNull(checkData, "long matcher needs data to check");
		return new CCLongMatch(name, checkData);
	}
}
