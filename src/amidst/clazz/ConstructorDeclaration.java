package amidst.clazz;

public class ConstructorDeclaration {
	private String symbolicName;
	private ParameterDeclarationList parameters;

	public ConstructorDeclaration(String symbolicName,
			ParameterDeclarationList parameters) {
		this.symbolicName = symbolicName;
		this.parameters = parameters;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public ParameterDeclarationList getParameters() {
		return parameters;
	}
}
