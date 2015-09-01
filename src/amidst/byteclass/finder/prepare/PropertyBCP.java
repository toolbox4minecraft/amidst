package amidst.byteclass.finder.prepare;

import amidst.byteclass.ByteClass;
import amidst.byteclass.PropertyDeclaration;

public class PropertyBCP extends ByteClassPreparer {
	private String externalName;
	private String internalName;

	public PropertyBCP(String externalName, String internalName) {
		this.externalName = externalName;
		this.internalName = internalName;
	}

	@Override
	public void prepare(ByteClass byteClass) {
		byteClass.addProperty(new PropertyDeclaration(externalName,
				internalName));
	}
}
