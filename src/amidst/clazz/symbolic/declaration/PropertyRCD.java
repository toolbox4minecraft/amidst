package amidst.clazz.symbolic.declaration;

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
