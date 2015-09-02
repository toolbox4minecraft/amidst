package amidst.clazz.symbolic;

public class SymbolicObject {
	private SymbolicClass type;
	private Object object;

	public SymbolicObject(SymbolicClass type, Object object) {
		this.type = type;
		this.object = object;
	}

	public SymbolicClass getType() {
		return type;
	}

	public Object getObject() {
		return object;
	}

	public boolean hasMethod(String symbolicName) {
		return type.hasMethod(symbolicName);
	}

	public boolean hasProperty(String symbolicName) {
		return type.hasProperty(symbolicName);
	}

	public Object callMethod(String symbolicName, Object... parameters) {
		return type.callMethod(symbolicName, this, parameters);
	}

	public Object getPropertyValue(String symbolicName) {
		return type.getPropertyValue(symbolicName, this);
	}
}
