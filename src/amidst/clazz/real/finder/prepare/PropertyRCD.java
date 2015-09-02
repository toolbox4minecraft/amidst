package amidst.clazz.real.finder.prepare;

import amidst.clazz.PropertyDeclaration;
import amidst.clazz.real.RealClass;

public class PropertyRCD extends RealClassPreparer {
	private PropertyDeclaration propertyDeclaration;

	public PropertyRCD(PropertyDeclaration propertyDeclaration) {
		this.propertyDeclaration = propertyDeclaration;
	}

	@Override
	public void prepare(RealClass realClass) {
		realClass.addProperty(propertyDeclaration);
	}
}
