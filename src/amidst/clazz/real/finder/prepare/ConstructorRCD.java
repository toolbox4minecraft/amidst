package amidst.clazz.real.finder.prepare;

import amidst.clazz.ConstructorDeclaration;
import amidst.clazz.ParameterDeclarationList;
import amidst.clazz.real.RealClass;

public class ConstructorRCD extends RealClassPreparer {
	private String symbolicName;
	private ParameterDeclarationList parameters;

	public ConstructorRCD(String symbolicName,
			ParameterDeclarationList parameters) {
		this.symbolicName = symbolicName;
		this.parameters = parameters;
	}

	@Override
	public void prepare(RealClass byteClass) {
		byteClass.addConstructor(new ConstructorDeclaration(symbolicName,
				parameters));
	}
}
