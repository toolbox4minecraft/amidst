package amidst.byteclass.finder.prepare;

import amidst.byteclass.ByteClass;

public class MethodBCP extends ByteClassPreparer {
	private String method;
	private String methodName;

	public MethodBCP(String method, String methodName) {
		this.method = method;
		this.methodName = methodName;
	}

	@Override
	public void prepare(ByteClass byteClass) {
		byteClass.addMethod(method, methodName);
	}
}
