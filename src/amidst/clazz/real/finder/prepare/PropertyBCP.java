package amidst.clazz.real.finder.prepare;

import amidst.clazz.real.ByteClass;
import amidst.clazz.real.PropertyDeclaration;

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
