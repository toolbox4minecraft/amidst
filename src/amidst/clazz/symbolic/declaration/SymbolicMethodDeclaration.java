package amidst.clazz.symbolic.declaration;

import amidst.documentation.Immutable;

@Immutable
public class SymbolicMethodDeclaration {
	private final String symbolicName;
	private final String realName;
	private final SymbolicParameterDeclarationList parameters;

	public SymbolicMethodDeclaration(String symbolicName, String realName,
			SymbolicParameterDeclarationList parameters) {
		this.symbolicName = symbolicName;
		this.realName = realName;
		this.parameters = parameters;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public String getRealName() {
		return realName;
	}

	public SymbolicParameterDeclarationList getParameters() {
		return parameters;
	}
}
