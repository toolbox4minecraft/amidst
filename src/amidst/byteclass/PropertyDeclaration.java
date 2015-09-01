package amidst.byteclass;

public class PropertyDeclaration {
	private String externalName;
	private String internalName;

	public PropertyDeclaration(String externalName, String internalName) {
		this.externalName = externalName;
		this.internalName = internalName;
	}

	public String getExternalName() {
		return externalName;
	}

	public String getInternalName() {
		return internalName;
	}
}
