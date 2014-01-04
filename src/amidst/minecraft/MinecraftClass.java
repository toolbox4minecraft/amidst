package amidst.minecraft;

import java.lang.reflect.Constructor;
import java.util.HashMap;


public class MinecraftClass {
	private String name, className;
	private HashMap<String, MinecraftMethod> methods;
	private Class<?> clazz;
	private HashMap<String, MinecraftProperty> propertiesByName;
	private HashMap<String, MinecraftProperty> propertiesByObfName;
	private HashMap<String, MinecraftMethod> methodsByName;
	private HashMap<String, MinecraftMethod> methodsByObfName;
	private HashMap<String, MinecraftConstructor> constructorByName;
	private Constructor<?>[] constructors;
	private Minecraft minecraft;
	public MinecraftClass(String name, String className) {
		this.name = name;
		this.className = className;
		methods = new HashMap<String, MinecraftMethod>();
		propertiesByName = new HashMap<String, MinecraftProperty>();
		propertiesByObfName = new HashMap<String, MinecraftProperty>();
		methodsByName = new HashMap<String, MinecraftMethod>();
		methodsByObfName = new HashMap<String, MinecraftMethod>();
		constructorByName = new HashMap<String, MinecraftConstructor>();
	}
	public String getName() {
		return name;
	}
	public void load(Minecraft mc) {
		minecraft = mc;
		clazz = minecraft.loadClass(className);
	}
	public String getClassName() {
		return className;
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public void addProperty(MinecraftProperty property) {
		property.load(minecraft, this);
		propertiesByName.put(property.getName(), property);
		propertiesByObfName.put(property.getInternalName(), property);
	}
	public Object getValue(String name) {
		MinecraftProperty prop = propertiesByName.get(name);
		return prop.getStaticValue();
	}
	public Object callFunction(String name, Object... args) {
		return methodsByName.get(name).callStatic(args);
	}
	public Object callFunction(String name, MinecraftObject obj, Object... args) {
		return methodsByName.get(name).call(obj, args);
	}
	public void addMethod(MinecraftMethod method) {
		method.load(minecraft, this);
		methodsByName.put(method.getName(), method);
		methodsByObfName.put(method.getInternalName(), method);
	}
	public void addConstructor(MinecraftConstructor constructor) {
		constructor.load(minecraft, this);
		constructorByName.put(constructor.getName(), constructor);
	}
	public String toString() {
		return className;
	}
	public MinecraftObject newInstance(String constructor, Object... param) {
		return constructorByName.get(constructor).getNew(param);
	}
	public MinecraftConstructor getConstructor(String name) {
		return constructorByName.get(name);
	}
	public Object getValue(String propertyName, MinecraftObject minecraftObject) {
		return propertiesByName.get(propertyName).getValue(minecraftObject);
	}
}
