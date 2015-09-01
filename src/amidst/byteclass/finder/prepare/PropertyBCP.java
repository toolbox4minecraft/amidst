package amidst.byteclass.finder.prepare;

import amidst.byteclass.ByteClass;

public class PropertyBCP extends ByteClassPreparer {
	private String bytePropertyName;
	private String minecraftPropertyName;

	public PropertyBCP(String bytePropertyName, String minecraftPropertyName) {
		this.bytePropertyName = bytePropertyName;
		this.minecraftPropertyName = minecraftPropertyName;
	}

	@Override
	public void prepare(ByteClass byteClass) {
		byteClass.addProperty(bytePropertyName, minecraftPropertyName);
	}
}
