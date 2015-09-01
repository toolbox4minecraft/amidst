package amidst.minecraft.local;

import java.lang.reflect.Field;

import amidst.logging.Log;

public class MinecraftProperty {
	private String name, internalName;
	private MinecraftClass parent;
	private Field property;
	private boolean isMinecraftClass = true;
	private MinecraftClass type;
	public MinecraftProperty(MinecraftClass minecraftClass, String minecraftPropertyName, String bytePropertyName) {
		this.parent = minecraftClass;
		this.name = minecraftPropertyName;
		this.internalName = bytePropertyName;
	}
	
	
	
	@Override
	public String toString() {
		return "[Method " + name +" (" + internalName +") of class " + parent.getMinecraftClassName() + "]";
	}



	public String getName() {
		return name;
	}

	public String getInternalName() {
		return internalName;
	}
	public void setValue(Object obj, Object val) {
		try {
			property.set(obj, val);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public void load(Minecraft mc, MinecraftClass mcClass) {
		Class<?> clazz = mcClass.getClazz();
		try {
			property = clazz.getDeclaredField(internalName);
			String propType = property.getType().getName();
			if (propType.contains(".")) {
				String[] typeSplit = propType.split("\\.");
				propType = typeSplit[typeSplit.length-1];
			}
			type = mc.getMinecraftClassByByteClassName(propType);
			if (type == null)
				isMinecraftClass = false;
			property.setAccessible(true);
		} catch (SecurityException e) {
			Log.crash(e, "SecurityException on (" + mcClass.getMinecraftClassName() + " / " + mcClass.getByteClassName() + ") property (" + name + " / " + internalName +")");
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			Log.crash(e, "Unable to find class property (" + mcClass.getMinecraftClassName() + " / " + mcClass.getByteClassName() + ") (" + name + " / " + internalName +")");
			e.printStackTrace();
		}
	}
	
	public Object getValue(MinecraftObject mcObject) {
		Object object = mcObject.get();
		Object value = getValue(object);
		if (isMinecraftClass)
			return new MinecraftObject(type, value);
		return value;
	}
	public Object getStaticValue() {
		Object value = getValue((Object)null);
		if (isMinecraftClass) {
			return new MinecraftObject(type, value);
		}
		return value;
	}
	private Object getValue(Object obj) {
		try {
			return property.get(obj);
		} catch (IllegalArgumentException e) { // TODO : Add error text.
			e.printStackTrace();
			Log.crash(e, "Error [IllegalArgumentException] loading property (" + toString() + ")");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			Log.crash(e, "Error [IllegalAccessException] loading property (" + toString() + ")");
		}
		return null;
	}

	public String getParentName() {
		return parent.getMinecraftClassName();
	}
}
