package amidst.byteclass;

public class PropertyDeclaration {
	private String symbolicName;
	private String realName;

	public PropertyDeclaration(String symbolicName, String realName) {
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
