package amidst.bytedata.factory;

import java.util.Objects;

import amidst.bytedata.CCLong;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.factory.ClassCheckerBuilder.ClassCheckerFactory;

public class CCLongFactory extends ClassCheckerFactory {
	private String name;
	private long[] checkData;

	public CCLongFactory(String name) {
		this.name = name;
	}

	public CCLongFactory data(long... checkData) {
		this.checkData = checkData;
		return this;
	}

	@Override
	protected ClassChecker get() {
		Objects.requireNonNull(checkData, "long matcher needs data to check");
		return new CCLong(name, checkData);
	}
}
