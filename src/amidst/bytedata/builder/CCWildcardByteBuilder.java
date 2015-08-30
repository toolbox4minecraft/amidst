package amidst.bytedata.builder;

import java.util.Objects;

import amidst.bytedata.CCWildcardByte;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.builder.ClassCheckerBuilder.SimpleClassCheckerBuilder;

public class CCWildcardByteBuilder extends SimpleClassCheckerBuilder {
	private String name;
	private int[] checkData;

	public CCWildcardByteBuilder(String name) {
		this.name = name;
	}

	public CCWildcardByteBuilder data(int[] checkData) {
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
