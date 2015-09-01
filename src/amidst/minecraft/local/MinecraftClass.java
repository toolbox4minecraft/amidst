package amidst.minecraft.local;

import java.util.Map;

public class MinecraftClass {
	private String minecraftClassName;
	private String byteClassName;
	private Class<?> clazz;
	private Map<String, MinecraftConstructor> constructorsByMinecraftName;
	private Map<String, MinecraftMethod> methodsByMinecraftName;
	private Map<String, MinecraftProperty> propertiesByMinecraftName;

	public MinecraftClass(String minecraftClassName, String byteClassName,
			Class<?> clazz,
			Map<String, MinecraftConstructor> constructorsByMinecraftName,
			Map<String, MinecraftMethod> methodsByMinecraftName,
			Map<String, MinecraftProperty> propertiesByMinecraftName) {
		this.minecraftClassName = minecraftClassName;
		this.byteClassName = byteClassName;
		this.clazz = clazz;
		this.constructorsByMinecraftName = constructorsByMinecraftName;
		this.methodsByMinecraftName = methodsByMinecraftName;
		this.propertiesByMinecraftName = propertiesByMinecraftName;
	}

	public String getMinecraftName() {
		return minecraftClassName;
	}

	public String getByteName() {
		return byteClassName;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public boolean hasConstructor(String minecraftName) {
		return constructorsByMinecraftName.containsKey(minecraftName);
	}

	public boolean hasMethod(String minecraftName) {
		return methodsByMinecraftName.containsKey(minecraftName);
	}

	public boolean hasProperty(String minecraftName) {
		return propertiesByMinecraftName.containsKey(minecraftName);
	}

	public MinecraftObject callConstructor(String constructor, Object... param) {
		return constructorsByMinecraftName.get(constructor).call(param);
	}

	public Object callMethod(String name, MinecraftObject obj, Object... args) {
		return methodsByMinecraftName.get(name).call(obj, args);
	}

	public Object callStaticMethod(String name, Object... args) {
		return methodsByMinecraftName.get(name).callStatic(args);
	}

	public Object getPropertyValue(String propertyName,
			MinecraftObject minecraftObject) {
		return propertiesByMinecraftName.get(propertyName).getValue(
				minecraftObject);
	}

	public Object getStaticPropertyValue(String name) {
		return propertiesByMinecraftName.get(name).getStaticValue();
	}

	@Override
	public String toString() {
		return byteClassName;
	}
}
