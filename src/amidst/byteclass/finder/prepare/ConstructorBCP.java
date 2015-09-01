package amidst.byteclass.finder.prepare;

import amidst.byteclass.ByteClass;
import amidst.byteclass.ConstructorDeclaration;
import amidst.byteclass.ParameterDeclarationList;

public class ConstructorBCP extends ByteClassPreparer {
	private String externalName;
	private ParameterDeclarationList parameters;

	public ConstructorBCP(String externalName,
			ParameterDeclarationList parameters) {
		this.externalName = externalName;
		this.parameters = parameters;
	}

	@Override
	public void prepare(ByteClass byteClass) {
		byteClass.addConstructor(new ConstructorDeclaration(externalName,
				parameters));
	}
}
