package amidst.clazz.symbolic.declaration;

public class SymbolicConstructorDeclaration {
	private String symbolicName;
	private SymbolicParameterDeclarationList parameters;

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
