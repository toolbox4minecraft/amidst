package amidst.clazz.symbolic.declaration;

import amidst.documentation.Immutable;

@Immutable
public class SymbolicConstructorDeclaration {
	private final String symbolicName;
	private final SymbolicParameterDeclarationList parameters;

	public SymbolicConstructorDeclaration(String symbolicName,
			SymbolicParameterDeclarationList parameters) {
		this.symbolicName = symbolicName;
		this.parameters = parameters;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public SymbolicParameterDeclarationList getParameters() {
		return parameters;
	}
}
