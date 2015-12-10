package amidst.clazz.symbolic.declaration;

import amidst.documentation.Immutable;

@Immutable
public class SymbolicFieldDeclaration {
	private final String symbolicName;
	private final String realName;

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
