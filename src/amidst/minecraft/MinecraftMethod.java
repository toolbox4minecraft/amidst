package amidst.minecraft;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import amidst.logging.Log;

public class MinecraftMethod {
	private MinecraftClass parent;
	private Minecraft minecraft;
	private Class<?>[] paramClasses;
	private String[] paramNames;
	private boolean hasParameters;
	private Method method;
	private String name, internalName;
	private MinecraftClass returnType;
	private boolean isMinecraftClass = false;
	private static HashMap<String, Class<?>> primitives;
	static {
		primitives = new HashMap<String, Class<?>>();
		primitives.put("byte", byte.class);
		primitives.put("int", int.class);
		primitives.put("float", float.class);
		primitives.put("short", short.class);
		primitives.put("long", long.class);
		primitives.put("double", double.class);
		primitives.put("boolean", boolean.class);
		primitives.put("char", char.class);
		primitives.put("String", String.class);
	}
	public MinecraftMethod(MinecraftClass parent, String name, String methodName) {
		this.parent = parent;
		hasParameters = false;
		this.name = name;
		internalName = methodName;
		paramClasses = new Class<?>[] {};
	}
	public MinecraftMethod(MinecraftClass parent, String name, String methodName, String... args) {
		this.parent = parent;
		paramNames = args;
		paramClasses = new Class<?>[paramNames.length];
		hasParameters = true;
		this.name = name;
		internalName = methodName;
	}
	public void load(Minecraft mc, MinecraftClass mcClass) {
		minecraft = mc;
		Class<?> clazz = mcClass.getClazz();
		int i = 0;
		try {
			if (hasParameters) {
				for (; i < paramNames.length; i++) {
					paramClasses[i] = primitives.get(paramNames[i]);
					if (paramClasses[i] == null)
						paramClasses[i] = mc.getClassLoader().loadClass(paramNames[i]); // TODO: Does this cause duplicate loads?
				}
			}
			
			method = clazz.getDeclaredMethod(internalName, paramClasses);
			method.setAccessible(true);
			String methodType = method.getReturnType().getName();
			if (methodType.contains(".")) {
				String[] typeSplit = methodType.split("\\.");
				methodType = typeSplit[typeSplit.length-1];
			}
			returnType = minecraft.getClassByType(methodType);
			if (returnType == null)
				isMinecraftClass = false;
		} catch (ClassNotFoundException e) {
			Log.crash(e, "Unabled to find class for parameter. (" + paramNames[i] + ") on (" + mcClass.getName() + " / " + mcClass.getClassName() + ")");
			e.printStackTrace();
		} catch (SecurityException e) {
			Log.crash(e, "SecurityException on (" + mcClass.getName() + " / " + mcClass.getClassName() + ") method (" + name + " / " + internalName +")");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.crash(e, "Unable to find class method (" + mcClass.getName() + " / " + mcClass.getClassName() + ") (" + name + " / " + internalName +")");
			e.printStackTrace();
		}
	}
	public Object callStatic(Object... param) {
		Object value = call((Object)null, param);
		if (isMinecraftClass) {
			return new MinecraftObject(returnType, value);
		}
		return value;
	}
	public Object call(MinecraftObject obj, Object... param) {
		Object value = call(obj.get(), param);
		if (isMinecraftClass)
			return new MinecraftObject(returnType, value);
		return value;
	}
	private Object call(Object obj, Object... param) {
		try {
			return method.invoke(obj, param);
		} catch (IllegalArgumentException e) { // TODO : Add error text
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Object getParentName() {
		return parent.getName();
	}
	public String getInternalName() {
		return internalName;
	}
	public String getName() {
		return name;
	}
}
