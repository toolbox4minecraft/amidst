package amidst.byteclass.finder.prepare;

import amidst.byteclass.ByteClass;
import amidst.byteclass.MethodDeclaration;
import amidst.byteclass.ParameterDeclarationList;

public class MethodBCP extends ByteClassPreparer {
	private String externalName;
	private String internalName;
	private ParameterDeclarationList parameters;

	public MethodBCP(String externalName, String internalName,
			ParameterDeclarationList parameters) {
		this.externalName = externalName;
		this.internalName = internalName;
		this.parameters = parameters;
	}

	@Override
	public void prepare(ByteClass byteClass) {
		byteClass.addMethod(new MethodDeclaration(externalName, internalName,
				parameters));
	}
}
