package amidst.clazz.real.finder.prepare;

import amidst.clazz.PropertyDeclaration;
import amidst.clazz.real.RealClass;

public class PropertyRCD extends RealClassPreparer {
	private String symbolicName;
	private String realName;

	public PropertyRCD(String symbolicName, String realName) {
		this.symbolicName = symbolicName;
		this.realName = realName;
	}

	@Override
	public void prepare(RealClass byteClass) {
		byteClass.addProperty(new PropertyDeclaration(symbolicName, realName));
	}
}
