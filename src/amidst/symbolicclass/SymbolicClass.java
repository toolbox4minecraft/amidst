package amidst.symbolicclass;

import java.util.Map;

public class SymbolicClass {
	private String symbolicClassName;
	private String realClassName;
	private Class<?> clazz;
	private Map<String, SymbolicConstructor> constructorsByMinecraftName;
	private Map<String, SymbolicMethod> methodsByMinecraftName;
	private Map<String, SymbolicProperty> propertiesByMinecraftName;

	public SymbolicClass(String symbolicClassName, String realClassName,
			Class<?> clazz,
			Map<String, SymbolicConstructor> constructorsByMinecraftName,
			Map<String, SymbolicMethod> methodsByMinecraftName,
			Map<String, SymbolicProperty> propertiesByMinecraftName) {
		this.symbolicClassName = symbolicClassName;
		this.realClassName = realClassName;
		this.clazz = clazz;
		this.constructorsByMinecraftName = constructorsByMinecraftName;
		this.methodsByMinecraftName = methodsByMinecraftName;
		this.propertiesByMinecraftName = propertiesByMinecraftName;
	}

	public String getSymbolicName() {
		return symbolicClassName;
	}

	public String getRealName() {
		return realClassName;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public boolean hasConstructor(String minecraftName) {
		return constructorsByMinecraftName.get(minecraftName) != null;
	}

	public boolean hasMethod(String minecraftName) {
		return methodsByMinecraftName.get(minecraftName) != null;
	}

	public boolean hasProperty(String minecraftName) {
		return propertiesByMinecraftName.get(minecraftName) != null;
	}

	public SymbolicObject callConstructor(String constructor, Object... param) {
		return constructorsByMinecraftName.get(constructor).call(param);
	}

	public Object callMethod(String name, SymbolicObject obj, Object... args) {
		return methodsByMinecraftName.get(name).call(obj, args);
	}

	public Object callStaticMethod(String name, Object... args) {
		return methodsByMinecraftName.get(name).callStatic(args);
	}

	public Object getPropertyValue(String propertyName,
			SymbolicObject minecraftObject) {
		return propertiesByMinecraftName.get(propertyName).getValue(
				minecraftObject);
	}

	public Object getStaticPropertyValue(String name) {
		return propertiesByMinecraftName.get(name).getStaticValue();
	}

	@Override
	public String toString() {
		return realClassName;
	}
}
