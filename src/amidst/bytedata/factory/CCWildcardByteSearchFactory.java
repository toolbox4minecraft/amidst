package amidst.bytedata.factory;

import java.util.Objects;

import amidst.bytedata.CCWildcardByteSearch;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.factory.ClassCheckerBuilder.ClassCheckerFactory;

public class CCWildcardByteSearchFactory extends ClassCheckerFactory {
	private String name;
	private int[] checkData;

	public CCWildcardByteSearchFactory(String name) {
		this.name = name;
	}

	public CCWildcardByteSearchFactory data(int[] checkData) {
		this.checkData = checkData;
		return this;
	}

	@Override
	protected ClassChecker get() {
		Objects.requireNonNull(checkData,
				"wildcard bytes matcher needs data to check");
		return new CCWildcardByteSearch(name, checkData);
	}
}
