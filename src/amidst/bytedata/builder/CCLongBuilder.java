package amidst.bytedata.builder;

import java.util.Objects;

import amidst.bytedata.CCLong;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.builder.ClassCheckerBuilder.SimpleClassCheckerBuilder;

public class CCLongBuilder extends SimpleClassCheckerBuilder {
	private String name;
	private long[] checkData;

	public CCLongBuilder(String name) {
		this.name = name;
	}

	public CCLongBuilder data(long... checkData) {
		this.checkData = checkData;
		return this;
	}

	@Override
	protected ClassChecker get() {
		Objects.requireNonNull(checkData, "long matcher needs data to check");
		return new CCLong(name, checkData);
	}
}
