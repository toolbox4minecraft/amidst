package amidst.symbolicclass;

public class SymbolicObject {
	private SymbolicClass symbolicClass;
	private Object object;

	public SymbolicObject(SymbolicClass symbolicClass, Object object) {
		this.symbolicClass = symbolicClass;
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	public Object callMethod(String symbolicName, Object... parameters) {
		return symbolicClass.callMethod(symbolicName, this, parameters);
	}

	public Object getPropertyValue(String symbolicName) {
		return symbolicClass.getPropertyValue(symbolicName, this);
	}
}
