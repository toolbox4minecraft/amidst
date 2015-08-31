package amidst.bytedata.builder;

import amidst.bytedata.CCAddProperty;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.builder.ClassCheckerBuilder.SimpleClassCheckerBuilder;

public class CCAddPropertyBuilder extends SimpleClassCheckerBuilder {
	private String property;
	private String propertyName;

	public CCAddPropertyBuilder(String property, String propertyName) {
		this.property = property;
		this.propertyName = propertyName;
	}

	@Override
	protected ClassChecker get() {
		return new CCAddProperty(property, propertyName);
	}
}
