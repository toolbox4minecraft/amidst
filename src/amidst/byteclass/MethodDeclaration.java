package amidst.byteclass;

public class MethodDeclaration {
	private String externalName;
	private String internalName;
	private ParameterDeclarationList parameters;

	public MethodDeclaration(String externalName, String internalName,
			ParameterDeclarationList parameters) {
		this.externalName = externalName;
		this.internalName = internalName;
		this.parameters = parameters;
	}

	public String getExternalName() {
		return externalName;
	}

	public String getInternalName() {
		return internalName;
	}

	public ParameterDeclarationList getParameters() {
		return parameters;
	}
}
