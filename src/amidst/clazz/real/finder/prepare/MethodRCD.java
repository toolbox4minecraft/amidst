package amidst.clazz.real.finder.prepare;

import amidst.clazz.MethodDeclaration;
import amidst.clazz.ParameterDeclarationList;
import amidst.clazz.real.RealClass;

public class MethodRCD extends RealClassPreparer {
	private String symbolicName;
	private String realName;
	private ParameterDeclarationList parameters;

	public MethodRCD(String symbolicName, String realName,
			ParameterDeclarationList parameters) {
		this.symbolicName = symbolicName;
		this.realName = realName;
		this.parameters = parameters;
	}

	@Override
	public void prepare(RealClass realClass) {
		realClass.addMethod(new MethodDeclaration(symbolicName, realName,
				parameters));
	}
}
