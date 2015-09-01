package amidst.clazz.real.finder.prepare;

import amidst.clazz.MethodDeclaration;
import amidst.clazz.ParameterDeclarationList;
import amidst.clazz.real.ByteClass;

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
