package amidst.byteclass.finder.prepare;

import amidst.byteclass.ByteClass;
import amidst.byteclass.ConstructorDeclaration;
import amidst.byteclass.ParameterDeclarationList;

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
