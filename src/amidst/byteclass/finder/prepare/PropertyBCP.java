package amidst.byteclass.finder.prepare;

import amidst.byteclass.ByteClass;
import amidst.byteclass.PropertyDeclaration;

public class PropertyBCP extends ByteClassPreparer {
	private String symbolicName;
	private String realName;

	public PropertyBCP(String symbolicName, String realName) {
		this.symbolicName = symbolicName;
		this.realName = realName;
	}

	@Override
	public void prepare(ByteClass byteClass) {
		byteClass.addProperty(new PropertyDeclaration(symbolicName, realName));
	}
}
