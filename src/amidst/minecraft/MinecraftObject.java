package amidst.minecraft;

public class MinecraftObject {
	private MinecraftClass type;
	private Object value;
	public MinecraftObject(MinecraftClass type, Object value) {
		this.type = type;
		this.value = value;
	}
	public MinecraftObject(Minecraft mc, Object value) {
		this.type = mc.getClassByType(value.getClass().getCanonicalName());
		this.value = value;
	}
	public Object get() {
		return value;
	}
	
	public Object callFunction(String funcName, Object... args) {
		return type.callFunction(funcName, this, args);
	}
	
	public Object getValue(String propertyName) {
		return type.getValue(propertyName, this);
	}
}
