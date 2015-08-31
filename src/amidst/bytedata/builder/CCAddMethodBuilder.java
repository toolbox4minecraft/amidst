package amidst.bytedata.builder;

import amidst.bytedata.CCAddMethod;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.builder.ClassCheckerBuilder.SimpleClassCheckerBuilder;

public class CCAddMethodBuilder extends SimpleClassCheckerBuilder {
	private String method;
	private String methodName;

	public CCAddMethodBuilder(String method, String methodName) {
		this.method = method;
		this.methodName = methodName;
	}

	@Override
	protected ClassChecker get() {
		return new CCAddMethod(method, methodName);
	}
}
