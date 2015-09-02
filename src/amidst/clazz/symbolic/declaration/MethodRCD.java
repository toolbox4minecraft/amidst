package amidst.clazz.symbolic.declaration;

import amidst.clazz.real.RealClass;

public class MethodRCD extends RealClassPreparer {
	private MethodDeclaration methodDeclaration;

	public MethodRCD(MethodDeclaration methodDeclaration) {
		this.methodDeclaration = methodDeclaration;
	}

	@Override
	public void prepare(RealClass realClass) {
		realClass.addMethod(methodDeclaration);
	}
}
