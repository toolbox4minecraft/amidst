package amidst.minecraft.local;

import java.util.HashMap;
import java.util.Map;

public class MinecraftClass {
	private String minecraftClassName;
	private String byteClassName;
	private Minecraft minecraft;
	private Class<?> clazz;
	private Map<String, MinecraftProperty> propertiesByMinecraftName = new HashMap<String, MinecraftProperty>();
	private Map<String, MinecraftProperty> propertiesByByteName = new HashMap<String, MinecraftProperty>();
	private Map<String, MinecraftMethod> methodsByMinecraftName = new HashMap<String, MinecraftMethod>();
	private Map<String, MinecraftMethod> methodsByByteName = new HashMap<String, MinecraftMethod>();
	private Map<String, MinecraftConstructor> constructorsByMinecraftName = new HashMap<String, MinecraftConstructor>();

	public MinecraftClass(String minecraftClassName, String byteClassName,
			Minecraft minecraft) {
		this.minecraftClassName = minecraftClassName;
		this.byteClassName = byteClassName;
		this.minecraft = minecraft;
		this.clazz = minecraft.loadClass(byteClassName);
	}

	public String getMinecraftClassName() {
		return minecraftClassName;
	}

	public String getByteClassName() {
		return byteClassName;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void addProperty(MinecraftProperty property) {
		property.load(minecraft, this);
		propertiesByMinecraftName.put(property.getName(), property);
		propertiesByByteName.put(property.getInternalName(), property);
	}

	public Object getStaticPropertyValue(String name) {
		return propertiesByMinecraftName.get(name).getStaticValue();
	}

	public Object callMethod(String name, Object... args) {
		return methodsByMinecraftName.get(name).callStatic(args);
	}

	public Object callMethod(String name, MinecraftObject obj, Object... args) {
		return methodsByMinecraftName.get(name).call(obj, args);
	}

	public void addMethod(MinecraftMethod method) {
		method.load(minecraft, this);
		methodsByMinecraftName.put(method.getName(), method);
		methodsByByteName.put(method.getInternalName(), method);
	}

	public void addConstructor(MinecraftConstructor constructor) {
		constructor.load(minecraft, this);
		constructorsByMinecraftName.put(constructor.getName(), constructor);
	}

	public String toString() {
		return byteClassName;
	}

	public MinecraftObject newInstance(String constructor, Object... param) {
		return constructorsByMinecraftName.get(constructor).getNew(param);
	}

	public MinecraftConstructor getConstructor(String name) {
		return constructorsByMinecraftName.get(name);
	}

	public Object getPropertyValue(String propertyName,
			MinecraftObject minecraftObject) {
		return propertiesByMinecraftName.get(propertyName).getValue(
				minecraftObject);
	}

	public MinecraftMethod getMethod(String name) {
		return methodsByMinecraftName.get(name);
	}
}
