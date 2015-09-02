package amidst.clazz.symbolic.declaration;

public class SymbolicMethodDeclaration {
	private String symbolicName;
	private String realName;
	private SymbolicParameterDeclarationList parameters;

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
