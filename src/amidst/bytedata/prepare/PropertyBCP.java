package amidst.bytedata.prepare;

import amidst.bytedata.ByteClass;

public class PropertyBCP extends ByteClassPreparer {
	private String property;
	private String propertyName;

	public PropertyBCP(String property, String propertyName) {
		this.property = property;
		this.propertyName = propertyName;
	}

	@Override
	public void prepare(ByteClass byteClass) {
		byteClass.addProperty(property, propertyName);
	}
}
