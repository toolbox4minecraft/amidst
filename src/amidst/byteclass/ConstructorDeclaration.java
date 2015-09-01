package amidst.byteclass;

public class ConstructorDeclaration {
	private String externalName;
	private ParameterDeclarationList parameters;

	public ConstructorDeclaration(String externalName,
			ParameterDeclarationList parameters) {
		this.externalName = externalName;
		this.parameters = parameters;
	}

	public String getExternalName() {
		return externalName;
	}

	public ParameterDeclarationList getParameters() {
		return parameters;
	}
}
