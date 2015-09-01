package amidst.minecraft.local;

import java.util.HashMap;
import java.util.Map;

public class MinecraftClass {
	private String minecraftClassName;
	private String byteClassName;
	private LocalMinecraftInterfaceBuilder minecraft;
	private Class<?> clazz;
	private Map<String, MinecraftConstructor> constructorsByMinecraftName = new HashMap<String, MinecraftConstructor>();
	private Map<String, MinecraftMethod> methodsByMinecraftName = new HashMap<String, MinecraftMethod>();
	private Map<String, MinecraftMethod> methodsByByteName = new HashMap<String, MinecraftMethod>();
	private Map<String, MinecraftProperty> propertiesByMinecraftName = new HashMap<String, MinecraftProperty>();
	private Map<String, MinecraftProperty> propertiesByByteName = new HashMap<String, MinecraftProperty>();

	public MinecraftClass(String minecraftClassName, String byteClassName,
			LocalMinecraftInterfaceBuilder minecraft) {
		this.minecraftClassName = minecraftClassName;
		this.byteClassName = byteClassName;
		this.minecraft = minecraft;
		this.clazz = minecraft.loadClass(byteClassName);
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

	public MinecraftConstructor getConstructor(String minecraftName) {
		return constructorsByMinecraftName.get(minecraftName);
	}

	public MinecraftMethod getMethod(String minecraftName) {
		return methodsByMinecraftName.get(minecraftName);
	}

	public MinecraftProperty getProperty(String minecraftName) {
		return propertiesByMinecraftName.get(minecraftName);
	}

	public void addConstructor(MinecraftConstructor constructor) {
		constructor.initialize(minecraft, this);
		constructorsByMinecraftName.put(constructor.getMinecraftName(),
				constructor);
	}

	public void addMethod(MinecraftMethod method) {
		method.initialize(minecraft, this);
		methodsByMinecraftName.put(method.getMinecraftName(), method);
		methodsByByteName.put(method.getByteName(), method);
	}

	public void addProperty(MinecraftProperty property) {
		property.initialize(minecraft, this);
		propertiesByMinecraftName.put(property.getMinecraftName(), property);
		propertiesByByteName.put(property.getByteName(), property);
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
