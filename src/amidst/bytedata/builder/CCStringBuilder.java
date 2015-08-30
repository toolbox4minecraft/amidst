package amidst.bytedata.builder;

import java.util.Objects;

import amidst.bytedata.CCString;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.builder.ClassCheckerBuilder.SimpleClassCheckerBuilder;

public class CCStringBuilder extends SimpleClassCheckerBuilder {
	private String name;
	private String checkData;

	public CCStringBuilder(String name) {
		this.name = name;
	}

	public CCStringBuilder data(String checkData) {
		this.checkData = checkData;
		return this;
	}

	@Override
	protected ClassChecker get() {
		Objects.requireNonNull(checkData, "string matcher needs data to check");
		return new CCString(name, checkData);
	}
}
