package amidst.clazz;

public class MethodDeclaration {
	private String symbolicName;
	private String realName;
	private ParameterDeclarationList parameters;

	public MethodDeclaration(String symbolicName, String realName,
			ParameterDeclarationList parameters) {
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

	public ParameterDeclarationList getParameters() {
		return parameters;
	}
}
