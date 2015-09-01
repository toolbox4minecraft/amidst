package amidst.byteclass.finder.prepare;

import amidst.byteclass.ByteClass;
import amidst.byteclass.MethodDeclaration;
import amidst.byteclass.ParameterDeclarationList;

public class MethodBCP extends ByteClassPreparer {
	private String symbolicName;
	private String realName;
	private ParameterDeclarationList parameters;

	public MethodBCP(String symbolicName, String realName,
			ParameterDeclarationList parameters) {
		this.symbolicName = symbolicName;
		this.realName = realName;
		this.parameters = parameters;
	}

	@Override
	public void prepare(ByteClass byteClass) {
		byteClass.addMethod(new MethodDeclaration(symbolicName, realName,
				parameters));
	}
}
