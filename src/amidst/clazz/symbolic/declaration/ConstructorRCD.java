package amidst.clazz.symbolic.declaration;

import amidst.clazz.real.RealClass;

public class ConstructorRCD extends RealClassPreparer {
	private ConstructorDeclaration constructorDeclaration;

	public ConstructorRCD(ConstructorDeclaration constructorDeclaration) {
		this.constructorDeclaration = constructorDeclaration;
	}

	@Override
	public void prepare(RealClass realClass) {
		realClass.addConstructor(constructorDeclaration);
	}
}
