package amidst.clazz.symbolic.declaration;

public class SymbolicFieldDeclaration {
	private String symbolicName;
	private String realName;

	public SymbolicFieldDeclaration(String symbolicName, String realName) {
		this.symbolicName = symbolicName;
		this.realName = realName;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public String getRealName() {
		return realName;
	}
}
