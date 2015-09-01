package amidst.symbolicclass;

public class SymbolicObject {
	private SymbolicClass minecraftClass;
	private Object object;

	public SymbolicObject(SymbolicClass minecraftClass, Object object) {
		this.minecraftClass = minecraftClass;
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	public Object callMethod(String functionName, Object... args) {
		return minecraftClass.callMethod(functionName, this, args);
	}

	public Object getPropertyValue(String propertyName) {
		return minecraftClass.getPropertyValue(propertyName, this);
	}
}
