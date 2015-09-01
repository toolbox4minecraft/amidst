package amidst.clazz.real.finder.prepare;

import amidst.clazz.real.ByteClass;
import amidst.clazz.real.MethodDeclaration;
import amidst.clazz.real.ParameterDeclarationList;

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
