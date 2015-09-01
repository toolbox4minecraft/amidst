package amidst.minecraft.local;

import java.lang.reflect.Field;

import amidst.logging.Log;

public class MinecraftProperty {
	private MinecraftClass parent;
	private String minecraftName;
	private String byteName;
	private Field field;
	private MinecraftClass type;

	public MinecraftProperty(MinecraftClass parent, String minecraftName,
			String byteName) {
		this.parent = parent;
		this.minecraftName = minecraftName;
		this.byteName = byteName;
	}

	public String getMinecraftName() {
		return minecraftName;
	}

	public String getByteName() {
		return byteName;
	}

	public Object getValue(MinecraftObject minecraftObject) {
		return getValueFromObject(minecraftObject.getObject());
	}

	public Object getStaticValue() {
		return getValueFromObject(null);
	}

	private Object getValueFromObject(Object object) {
		Object value = get(object);
		if (isTypeMinecraftClass()) {
			return new MinecraftObject(type, value);
		} else {
			return value;
		}
	}

	private Object get(Object obj) {
		try {
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			Log.crash(e,
					"Error [IllegalArgumentException] getting property value ("
							+ toString() + ")");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.crash(e,
					"Error [IllegalAccessException] getting property value ("
							+ toString() + ")");
			e.printStackTrace();
		}
		return null;
	}

	private boolean isTypeMinecraftClass() {
		return type != null;
	}

	public void setValue(Object object, Object value) {
		try {
			field.set(object, value);
		} catch (IllegalArgumentException e) {
			Log.crash(e,
					"Error [IllegalArgumentException] setting property value ("
							+ toString() + ")");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.crash(e,
					"Error [IllegalAccessException] setting property value ("
							+ toString() + ")");
			e.printStackTrace();
		}
	}

	public void initialize(Minecraft minecraft, MinecraftClass minecraftClass) {
		Class<?> clazz = minecraftClass.getClazz();
		try {
			field = getField(clazz);
			type = getType(minecraft);
		} catch (SecurityException e) {
			Log.crash(
					e,
					"SecurityException on ("
							+ minecraftClass.getMinecraftName() + " / "
							+ minecraftClass.getByteName() + ") property ("
							+ minecraftName + " / " + byteName + ")");
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			Log.crash(
					e,
					"Unable to find class property ("
							+ minecraftClass.getMinecraftName() + " / "
							+ minecraftClass.getByteName() + ") ("
							+ minecraftName + " / " + byteName + ")");
			e.printStackTrace();
		}
	}

	private Field getField(Class<?> clazz) throws NoSuchFieldException {
		Field result = clazz.getDeclaredField(byteName);
		result.setAccessible(true);
		return result;
	}

	private MinecraftClass getType(Minecraft minecraft) {
		String result = field.getType().getName();
		if (result.contains(".")) {
			String[] typeSplit = result.split("\\.");
			result = typeSplit[typeSplit.length - 1];
		}
		return minecraft.getMinecraftClassByByteClassName(result);
	}

	@Override
	public String toString() {
		return "[Method " + minecraftName + " (" + byteName + ") of class "
				+ parent.getMinecraftName() + "]";
	}
}
