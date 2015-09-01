package amidst.clazz.real.finder.prepare;

import amidst.clazz.ConstructorDeclaration;
import amidst.clazz.ParameterDeclarationList;
import amidst.clazz.real.ByteClass;

public class ConstructorBCP extends ByteClassPreparer {
	private String symbolicName;
	private ParameterDeclarationList parameters;

	public ConstructorBCP(String symbolicName,
			ParameterDeclarationList parameters) {
		this.symbolicName = symbolicName;
		this.parameters = parameters;
	}

	@Override
	public void prepare(ByteClass byteClass) {
		byteClass.addConstructor(new ConstructorDeclaration(symbolicName,
				parameters));
	}
}
