package amidst.clazz.symbolic.declaration;

public class SymbolicPropertyDeclaration {
	private String symbolicName;
	private String realName;

	public SymbolicPropertyDeclaration(String symbolicName, String realName) {
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
