package amidst.minecraft;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import amidst.logging.Log;


public class MinecraftConstructor {
	private MinecraftClass parent;
	private Minecraft minecraft;
	private Class<?>[] paramClasses;
	private String[] paramNames;
	private boolean hasParameters;
	private Constructor<?> constructor;
	private String name;
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
	public MinecraftConstructor(MinecraftClass parent, String name) {
		this.parent = parent;
		hasParameters = false;
		this.name = name;
		paramClasses = new Class<?>[] {};
	}
	public MinecraftConstructor(MinecraftClass parent, String name, String... args) {
		this.parent = parent;
		paramNames = args;
		paramClasses = new Class<?>[paramNames.length];
		hasParameters = true;
		this.name = name;
	}
	public void load(Minecraft mc, MinecraftClass mcClass) {
		minecraft = mc;
		Class<?> clazz = mcClass.getClazz();
		int i = 0;
		try {
			if (hasParameters) {
				for (; i < paramNames.length; i++) {
					paramClasses[i] = primitives.get(paramNames[i]);
					if (paramClasses[i] == null) {
						if (paramNames[i].charAt(0) == '@') {
							
						} else {
							paramClasses[i] = Class.forName(paramNames[i], true, minecraft.getClassLoader());
						}
					}
						
				}
			}

			constructor = clazz.getConstructor(paramClasses);
			constructor.setAccessible(true);
		} catch (ClassNotFoundException e) {
			Log.crash(e, "Unabled to find class for constructor. (" + paramNames[i] + ") on (" + mcClass.getName() + " / " + mcClass.getClassName() + ")");
			e.printStackTrace();
		} catch (SecurityException e) {
			Log.crash(e, "SecurityException on (" + mcClass.getName() + " / " + mcClass.getClassName() + ") contructor (" + name + ")");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.crash(e, "Unable to find class constructor (" + mcClass.getName() + " / " + mcClass.getClassName() + ") (" + name + ")");
			e.printStackTrace();
		}
	}
	public MinecraftObject getNew(Object... param) {
		return new MinecraftObject(parent, call(param));
	}
	private Object call(Object... param) {
		try {
			return constructor.newInstance(param);
		} catch (IllegalArgumentException e) { // TODO : Add error text
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			
			e.printStackTrace();
			
		}
		return null;
	}
	public Object getParentName() {
		return parent.getName();
	}
	public String getName() {
		return name;
	}
	public Class<?>[] getParameters() {
		return paramClasses;
	}
	@Override
	public String toString() {
		return "[Constructor " + name +" of class " + parent.getName() + "]";
	}
}
