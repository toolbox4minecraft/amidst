package amidst.bytedata.factory;

import java.util.Objects;

import amidst.bytedata.CCWildcardByte;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.factory.ClassCheckerBuilder.ClassCheckerFactory;

public class CCWildcardByteFactory extends ClassCheckerFactory {
	private String name;
	private int[] checkData;

	public CCWildcardByteFactory(String name) {
		this.name = name;
	}

	public CCWildcardByteFactory data(int[] checkData) {
		this.checkData = checkData;
		return this;
	}

	@Override
	protected ClassChecker get() {
		Objects.requireNonNull(checkData,
				"wildcard bytes matcher needs data to check");
		return new CCWildcardByte(name, checkData);
	}
}
